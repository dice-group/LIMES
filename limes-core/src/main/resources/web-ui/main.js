window.SPARQL_ENDPOINT = "/sparql/";
window.SPARQL_ENDPOINT = "http://localhost:8080/sparql/";

// apply vue-material stuff
Vue.use(VueMaterial);
Vue.config.devtools = true;

const makeDatasource = (data, tag) => `<${tag.toUpperCase()}>
<ID>${data.id}</ID>
<ENDPOINT>${data.endpoint}</ENDPOINT>
<VAR>${data.var}</VAR>
<PAGESIZE>${data.pagesize}</PAGESIZE>
<RESTRICTION>${data.restriction}</RESTRICTION>
${data.type && data.type.length ? `<TYPE>${data.type}</TYPE>` : ''}
${data.properties.map(p => `<PROPERTY>${p}</PROPERTY>`).join('\n')}
${data.optionalProperties.map(p => `<OPTIONAL_PROPERTY>${p}</OPTIONAL_PROPERTY>`).join('\n')}
</${tag.toUpperCase()}>
`;

const makeAccReview = (data, tag) => `<${tag.toUpperCase()}>
<THRESHOLD>${data.threshold}</THRESHOLD>
<FILE>${data.file}</FILE>
<RELATION>${data.relation}</RELATION>
</${tag.toUpperCase()}>
`;

const measures = ['Cosine', 'ExactMatch', 'Jaccard', 'Overlap', 'Jaro', 'JaroWinkler', 
'Levenshtein', 'MongeElkan', 'RatcliffObershelp', 'Soundex', 'Koeln', 'DoubleMetaphone',
'Trigram', 'Qgrams'];
let measureOptionsArray = [];
measures.forEach(i => measureOptionsArray.push({text: i.toLowerCase(), value: i.toLowerCase()}));

const operators = ['MAX', 'AND'];
let operatorOptionsArray = [];
operators.forEach(i => operatorOptionsArray.push({text: i.toLowerCase(), value: i.toLowerCase()}));

// init the app
let app = new Vue({
  el: '#app',
  template: '#mainApp',
  data: {
    // config display
    configText: '',
    // execution progress
    jobId: '',
    jobRunning: false,
    jobShowResult: false,
    jobStatus: '-1',
    jobStatusText: 'loading..',
    jobError: false,
    results: [],
    // config
    prefixes: [],
    filteredOptions: [],
    context: [],
    source: {
      id: 'sourceId',
      endpoint: '',
      var: '?src',
      pagesize: 1000,
      restriction: '?src rdf:type some:Type',
      type: 'sparql',
      properties: ['dc:title AS lowercase RENAME name'],
      optionalProperties: [],//['rdfs:label'],
      propertiesForChoice: ["a","b","c"],
    },
    target: {
      id: 'targetId',
      endpoint: '',
      var: '?target',
      pagesize: 1000,
      restriction: '?target rdf:type some:Type',
      type: 'sparql',
      properties: ['foaf:name AS lowercase RENAME name'],
      optionalProperties: [],//['rdf:type'],
      propertiesForChoice: ["a","b","c"],
    },
    metrics: ['trigrams(y.dc:title, x.linkedct:condition_name)'],
    selectedMeasureOption: '',
    measureOptions: measureOptionsArray,
    selectedOperatorOption: '',
    operatorOptions: operatorOptionsArray,
    acceptance: {
      threshold: 0.98,
      file: 'accepted.nt',
      relation: 'owl:sameAs',
    },
    review: {
      threshold: 0.95,
      file: 'reviewme.nt',
      relation: 'owl:sameAs',
    },
    mlalgorithm: {
      enabled: false,
      name: 'simple ml',
      type: 'supervised batch',
      training: 'trainingData.nt',
      parameters: [
        {
          name: 'max execution time in minutes',
          value: 60,
        },
      ],
    },
    execution: {
      rewriter: 'DEFAULT',
      planner: 'DEFAULT',
      engine: 'DEFAULT',
    },
    output: {
      type: 'TAB',
    },
    advancedOptionsShow: false,
  },
  mounted() {
    const jobIdmatches = /\?jobId=(.+)/.exec(window.location.search);
    if (jobIdmatches) {
      const jobId = jobIdmatches[1];
      this.jobId = jobId;
      this.jobRunning = true;
      setTimeout(() => this.$refs.jobDialog.open(), 10);
      setTimeout(() => this.getStatus(), 1000);
    }

    let source = this.source;
    let target = this.target;
    let metrics = this. metrics;
    window.onload = function() {
          var Workspace = Blockly.inject('blocklyDiv',
            {media: './blockly-1.20190215.0/media/',
             toolbox: document.getElementById('toolbox')});
          Blockly.Blocks['sourceproperty'] = {
            init: function() {
              this.jsonInit(sourceProperty);              
            }
          };
          Blockly.Blocks['targetproperty'] = {
            init: function() {
              this.jsonInit(targetProperty);
            }
          };
          Blockly.Blocks['renamepreprocessingfunction'] = {
            init: function() {
              this.jsonInit(RenamePreprocessingFunction);
            }
          };
          Blockly.Blocks['lowercasepreprocessingfunction'] = {
            init: function() {
              this.jsonInit(LowercasePreprocessingFunction);
            }
          };
          Blockly.Blocks['measure'] = {
            init: function() {
              this.jsonInit(Measure);
            }
          };
          Blockly.Blocks['operator'] = {
            init: function() {
              this.jsonInit(Operator);
            }
          };

          function onFirstComment(event) {
            //console.log(Workspace.getAllBlocks()[0].getField("propTitle").getDisplayText_());
            //console.log(Workspace.getTopBlocks());
            source.properties.splice(0);
            target.properties.splice(0);

            let allBlocks = Workspace.getTopBlocks();
            allBlocks.forEach( i => {
              switch(i.type) {
                case "lowercasepreprocessingfunction": {
                  i.getChildren().forEach(
                    pr => pr.type === "sourceproperty" ? 
                    source.properties.push(pr.getField("propTitle").getDisplayText_() + " AS lowercase") 
                    : target.properties.push(pr.getField("propTitle").getDisplayText_() + " AS lowercase"));
                
                  break;
                }

                case "renamepreprocessingfunction": {
                  i.getChildren().forEach(
                    pr => {
                        if(pr.type === "sourceproperty"){
                         source.properties.push(pr.getField("propTitle").getDisplayText_() + " RENAME " + i.getField("RENAME").getDisplayText_()); 
                        } else {
                          //target
                          if(!pr.getChildren().length) {
                            //console.log("tar");
                            target.properties.push(pr.getField("propTitle").getDisplayText_() + " RENAME " + i.getField("RENAME").getDisplayText_()); 
                          } else {
                            pr.getChildren().forEach( 
                              childPr => {
                                if(childPr.type === "sourceproperty") {
                                source.properties.push(childPr.getField("propTitle").getDisplayText_() + " AS lowercase RENAME " + i.getField("RENAME").getDisplayText_());
                                } else {
                                  target.properties.push(childPr.getField("propTitle").getDisplayText_() + " AS lowercase RENAME " + i.getField("RENAME").getDisplayText_());
                                  }
                              });
                          }
                        }
                    });

                  
                  break;                  
                }

                case "measure": {
                  let src;
                  let tgt;
                  i.getChildren().forEach(
                  pr => {
                    if(pr.type === "sourceproperty"){
                      src = pr.getField("propTitle").getDisplayText_();
                      source.properties.push(src);
                    } else {
                      tgt = pr.getField("propTitle").getDisplayText_();
                      target.properties.push(tgt);
                    }

                   
                  });
                  let measureFunc = i.getField("measureList").getDisplayText_();
                  metrics.splice(0);
                  metrics.push(measureFunc+"("+src+","+tgt+")");
             
                  break;
                }

                default: {
                  if(!i.getParent() && i.type === "sourceproperty"){
                    source.properties.push(i.getField("propTitle").getDisplayText_());
                  } else {
                    if(!i.getParent()){
                      // target
                      target.properties.push(i.getField("propTitle").getDisplayText_());
                    }
                  }
                  break;
                }
              }

            });

            
          }
          Workspace.addChangeListener(onFirstComment);

          
    };
  },
  beforeMount() {
    let context;
    let filteredOptions;
    fetch('http://prefix.cc/context')
            .then(function(response) {
              return response.json();
             })
            .then((content) => {
              context = content["@context"];
              filteredOptions = Object.keys(context);
              
              this.context = context;
              this.filteredOptions.push(...filteredOptions);
            })
            //.catch( alert );


  },
  methods: {
    deletePrefix(prefix) {
      this.prefixes = this.prefixes.filter(p => p.label !== prefix.label && p.namespace !== prefix.namespace);
    },
    addPrefix(prefix) {
      // push new prefix
      if(!this.prefixes.some(i => i.label === prefix.label)){
        this.prefixes.push(prefix);
      }

    },

    generateConfig() {
      const configHeader = `<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE LIMES SYSTEM "limes.dtd">
<LIMES>
`;
      const configFooter = `</LIMES>`;

      const prefixes = this.prefixes
        .map(
          p => `<PREFIX>
  <NAMESPACE>${p.namespace}</NAMESPACE>
  <LABEL>${p.label}</LABEL>
</PREFIX>
`
        )
        .join('');

      const src = makeDatasource(this.source, 'SOURCE');
      const target = makeDatasource(this.target, 'TARGET');

      const metrics = this.metrics
        .map(
          m => `<METRIC>
  ${m}
</METRIC>
`
        )
        .join('');

      const acceptance = makeAccReview(this.acceptance, 'ACCEPTANCE');
      const review = makeAccReview(this.review, 'REVIEW');

      const ml = this.mlalgorithm.enabled
        ? `<MLALGORITHM>
  <NAME>${this.mlalgorithm.name}</NAME>
  <TYPE>${this.mlalgorithm.type}</TYPE>
  <TRAINING>${this.mlalgorithm.training}</TRAINING>
  ${this.mlalgorithm.parameters
    .map(
      p => `<PARAMETER>
  <NAME>${p.name}</NAME>
  <VALUE>${p.value}</VALUE>
</PARAMETER>`
    )
    .join('\n  ')}
</MLALGORITHM>
`
        : '';

      const execution = `<EXECUTION>
  <REWRITER>${this.execution.rewriter}</REWRITER>
  <PLANNER>${this.execution.planner}</PLANNER>
  <ENGINE>${this.execution.engine}</ENGINE>
</EXECUTION>
`;

      const output = `<OUTPUT>${this.output.type}</OUTPUT>
`;

      const config =
        configHeader + prefixes + src + target + metrics + acceptance + review + ml + execution + output + configFooter;
      return config;
    },
    showConfig() {
      // generate new config
      const cfg = this.generateConfig();
      // highlight the code
      const h = hljs.highlight('xml', cfg);
      // set text to highlighted html
      this.configText = h.value;
      // show dialog
      this.$refs.configDialog.open();
    },
    closeConfig() {
      this.$refs.configDialog.close();
    },
    closeStatus() {
      history.pushState({}, '', '?');
      this.$refs.jobDialog.close();
    },
    execute() {
      this.jobError = false;
      const config = this.generateConfig();
      const configBlob = new Blob([config], {type: 'text/xml'});
      const fd = new FormData();
      fd.append('config_file', configBlob, 'config.xml');
      fetch(window.LIMES_SERVER_URL + '/submit', {
        method: 'post',
        body: fd,
      })
        .then(r => r.json().then(x => x.requestId))
        .then(r => {
          this.jobId = r;
          this.jobRunning = true;
          this.jobError = false;
          this.jobStatusText = 'Status Loading - waiting for status from server..';
          this.$refs.jobDialog.open();
          history.pushState({jobId: r}, '', `?jobId=${r}`);
          setTimeout(() => this.getStatus(), 1000);
        })
        .catch(e => {
          this.jobError = `Error while starting the job: ${e.toString()}`;
          this.jobRunning = false;
        });
    },
    getStatus() {
      this.jobError = false;
      fetch(window.LIMES_SERVER_URL + '/status/' + this.jobId)
        .then(r => r.json().then(x => x.status))
        .then(status => {
          this.jobError = false;
          this.jobStatus = status.code;
          if (status.code === -1) {
            this.jobRunning = false;
            this.jobStatusText = status.description;
          }
          if (status.code === 0) {
            this.jobRunning = true;
            this.jobStatusText = status.description;
            setTimeout(() => this.getStatus(), 5000);
          }
          if (status.code === 1) {
            this.jobRunning = true;
            this.jobStatusText = status.description;
            setTimeout(() => this.getStatus(), 5000);
          }
          if (status.code === 2) {
            this.jobRunning = false;
            this.jobStatusText = status.description;
            this.getResults();
          }
        })
        .catch(e => {
          this.jobError = `Error getting job status: ${e.toString()}`;
          this.jobRunning = false;
        });
    },
    getResults() {
      this.jobError = false;
      fetch(window.LIMES_SERVER_URL + '/results/' + this.jobId)
        .then(r => r.json())
        .then(results => {
          this.results = results.availableFiles;
          this.jobShowResult = true;
        })
        .catch(e => {
          this.jobError = `Error getting job results: ${e.toString()}`;
        });
    },
    exampleConfig() {
      this.prefixes = [
        {
          namespace: 'http://geovocab.org/geometry#',
          label: 'geom',
        },
        {
          namespace: 'http://www.opengis.net/ont/geosparql#',
          label: 'geos',
        },
        {
          namespace: 'http://linkedgeodata.org/ontology/',
          label: 'lgdo',
        },
        {
          namespace: 'http://www.w3.org/2000/01/rdf-schema#',
          label: 'rdfs',
        },
      ];
      this.source = {
        id: 'linkedgeodata',
        endpoint: 'http://linkedgeodata.org/sparql',
        var: '?x',
        pagesize: 2000,
        restriction: '?x a lgdo:RelayBox',
        type: '',
        properties: ['geom:geometry/geos:asWKT RENAME polygon'],
        optionalProperties: ['rdfs:label'],
      };
      this.target = {
        id: 'linkedgeodata',
        endpoint: 'http://linkedgeodata.org/sparql',
        var: '?y',
        pagesize: 2000,
        restriction: '?y a lgdo:RelayBox',
        type: '',
        properties: ['geom:geometry/geos:asWKT RENAME polygon'],
        optionalProperties: ['rdfs:label'],
      };
      this.metrics = ['geo_hausdorff(x.polygon, y.polygon)'];
      this.acceptance = {
        threshold: 0.9,
        file: 'lgd_relaybox_verynear.nt',
        relation: 'lgdo:near',
      };
      this.review = {
        threshold: 0.5,
        file: 'lgd_relaybox_near.nt',
        relation: 'lgdo:near',
      };
      this.execution = {
        rewriter: 'DEFAULT',
        planner: 'DEFAULT',
        engine: 'DEFAULT',
      };
      this.output = {type: 'TAB'};
    },
  },
});