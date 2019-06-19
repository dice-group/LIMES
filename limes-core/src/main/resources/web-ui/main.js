window.SPARQL_ENDPOINT = "/sparql/";
window.PREPROCESSING_LIST = window.LIMES_SERVER_URL+"/list/preprocessings";
window.RESULT_FILES = window.LIMES_SERVER_URL+"/results/";
window.RESULT_FILE = window.LIMES_SERVER_URL+"/result/";
window.JOB_LOGS = window.LIMES_SERVER_URL+"/logs/";
window.JOB_STATUS = window.LIMES_SERVER_URL+"/status/";

// window.SPARQL_ENDPOINT = "/sparql/";
// window.SPARQL_ENDPOINT = "http://localhost:8080/sparql/";
// window.PREPROCESSING_LIST = "http://localhost:8080/list/preprocessings";
// window.RESULT_FILES = "http://localhost:8080/results/";
// window.RESULT_FILE = "http://localhost:8080/result/";
// window.JOB_LOGS = "http://localhost:8080/logs/";
// window.JOB_STATUS = "http://localhost:8080/status/";

// apply vue-material stuff
Vue.use(VueMaterial);
Vue.config.devtools = true;

const makeDatasource = (data, tag) => `<${tag.toUpperCase()}>
  <ID>${data.id}</ID>
  <ENDPOINT>${data.endpoint}</ENDPOINT>
  <VAR>${data.var}</VAR>
  <PAGESIZE>${data.pagesize}</PAGESIZE>
  <RESTRICTION>${data.restriction}</RESTRICTION>
${data.properties.length ? data.properties.map(p => `  <PROPERTY>${p}</PROPERTY>`).join('\n') : ''}
${data.optionalProperties.length ? data.optionalProperties.map(p => `  <OPTIONAL_PROPERTY>${p}</OPTIONAL_PROPERTY>`).join('\n') : ''}
${data.type && data.type.length ? `  <TYPE>${data.type}</TYPE>` : ''}
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
'Trigram', 'Qgrams','Euclidean','Manhattan','Geo_Hausdorff','Geo_Max','Geo_Min','Geo_Mean','Geo_Avg',
'Geo_Frechet','Geo_Sum_Of_Min','Geo_Naive_Surjection','Geo_Fair_Surjection','Geo_Link',
'Top_Contains','Top_Covers','Top_Covered_By','Top_Crosses','Top_Disjoint','Top_Equals','Top_Intersects',
'Top_Overlaps','Top_Touches','Top_Within','Tmp_Concurrent','Tmp_Predecessor','Tmp_Successor','Tmp_After',
'Tmp_Before','Tmp_During','Tmp_During_Reverse','Tmp_Equals','Tmp_Finishes','Tmp_Is_Finished_By',
'Tmp_Overlaps','Tmp_Is_Overlapped_By','Tmp_Starts','Tmp_Is_Started_By','Tmp_Meets','Tmp_Is_xBy',
'Set_Jaccard'];
let measureOptionsArray = measures.map(i => [i, i]);

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
    prefixes: [{label: 'owl', namespace: 'http://www.w3.org/2002/07/owl#'}],
    allPrefixes: [{label: 'owl', namespace: 'http://www.w3.org/2002/07/owl#'}],
    exPrefixes: [],
    filteredOptions: [],
    context: [],
    source: {
      id: 'sourceId',
      endpoint: '',
      classes: [],
      var: '?s',
      pagesize: 1000,
      restriction: '?s rdf:type some:Type',
      type: 'sparql',
      properties: ['dc:title AS lowercase RENAME name'],
      optionalProperties: [],//['rdfs:label'],
      propertiesForChoice: ["a","b","c"],
      allProperties: [],
    },
    target: {
      id: 'targetId',
      endpoint: '',
      classes: [],
      var: '?t',
      pagesize: 1000,
      restriction: '?t rdf:type some:Type',
      type: 'sparql',
      properties: ['foaf:name AS lowercase RENAME name'],
      optionalProperties: [],//['rdf:type'],
      propertiesForChoice: ["a","b","c"],
      allProperties: [],
    },
    Workspace: null,
    fileWorkspaceForInput: '',
    importWorkspaceString: '',
    configurationFile: '',
    metrics: [''],
    //selectedMeasureOption: '',
    measureOptions: measureOptionsArray,
    //selectedOperatorOption: '',
    //operatorOptions: operatorOptionsArray,
    acceptance: {
      id: 'acceptance',
      threshold: 0.98,
      file: 'accepted.nt',
      relation: 'owl:sameAs',
    },
    review: {
      id: 'review',
      threshold: 0.01,
      file: 'reviewme.nt',
      relation: 'owl:sameAs',
    },
    mlalgorithm: {
      enabled: false,
      name: 'WOMBAT Simple',
      type: 'unsupervised',
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
    typesOfBlocks: {
      'start': Start,
      'sourceproperty': sourceProperty,
      'targetproperty': targetProperty,
      'optionalsourceproperty': optionalSourceProperty,
      'optionaltargetproperty': optionalTargetProperty,
      'measure': Measure,
      'operator': Operator,
      'preprocessingfunction':PreprocessingFunction,
      'nolangpreprocessingfunction': NolangPreprocessingFunction,
      'renamepreprocessingfunction': RenamePreprocessingFunction,
      'lowercasepreprocessingfunction': LowercasePreprocessingFunction,
      'numberpreprocessingfunction': NumberPreprocessingFunction,
      'cleaniripreprocessingfunction': CleaniriPreprocessingFunction,
      'celsiuspreprocessingfunction': CelsiusPreprocessingFunction,
      'fahrenheitpreprocessingfunction': FahrenheitPreprocessingFunction,
      'removebracespreprocessingfunction': RemovebracesPreprocessingFunction,
      'regularalphabetpreprocessingfunction': RegularAlphabetPreprocessingFunction,
      'uriasstringpreprocessingfunction': UriasstringPreprocessingFunction,
      'uppercasepreprocessingfunction': UppercasePreprocessingFunction,
    },
    mainSource: false,
    mainTarget: false,
    notConnectedToStart: false,
    executedKey: '',
    isDisabledLog: true,
    isDisabled: true,
    availableFiles: [],
    notFoundKeyMessage: '',
    findStatusMessage: '',
    exampleConfigEnable: false,
  },
  watch: {
      'source.properties': function() {
        this.addOldAndNewPrefix(this.source.properties);
      },
      'target.properties': function() {
        this.addOldAndNewPrefix(this.target.properties);
      },
      'source.optionalProperties': function() {
        this.addOldAndNewPrefix(this.source.optionalProperties);
      },
      'target.optionalProperties': function() {
        this.addOldAndNewPrefix(this.target.optionalProperties);
      },
      'source.allProperties': function() {
        this.deleteOldPrefixes();
        this.source.properties.splice(0);
      },
      'target.allProperties': function() {
        this.deleteOldPrefixes();
        this.target.properties.splice(0);
      },
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

    let blocklyInit = () => {
      //this.convertMetricToBlocklyXML();
          // var this.Workspace = Blockly.inject('blocklyDiv',
          //   {toolbox: document.getElementById('toolbox')});

          var blocklyArea = document.getElementById('blocklyArea');
          var blocklyDiv = document.getElementById('blocklyDiv');
          // var this.Workspace = this.this.Workspace;
          this.Workspace = Blockly.inject(blocklyDiv,
              {toolbox: document.getElementById('toolbox'),
              //scrollbars: true,
               zoom:
                 {controls: true,
                  wheel: true,
                  startScale: 1.0,
                  maxScale: 3,
                  minScale: 0.3,
                  scaleSpeed: 1.2},
                trashcan: true,
              });
          console.log(this.Workspace);
          //new Blockly.ScrollbarPair(this.Workspace);
          var onresize = (e) => {
            // Compute the absolute coordinates and dimensions of blocklyArea.
            var element = blocklyArea;
            var x = 0;
            var y = 0;
            do {
              x += element.offsetLeft;
              y += element.offsetTop;
              element = element.offsetParent;
            } while (element);
            // Position blocklyDiv over blocklyArea.
            //blocklyDiv.style.left = x + 'px';
            //blocklyDiv.style.top = y + 'px';
            blocklyDiv.style.width = blocklyArea.offsetWidth + 'px';
            blocklyDiv.style.height = blocklyArea.offsetHeight + 'px';
            Blockly.svgResize(this.Workspace);
          };
          window.addEventListener('resize', onresize, false);
          onresize();
          Blockly.svgResize(this.Workspace);



          var newBlock = this.Workspace.newBlock('start');
          newBlock.setDeletable(false);
          newBlock.initSvg();
          newBlock.render();
          //addRootBlock(Blockly.Blocks['start']);

          let onFirstComment = (event) => {
            // console.log("change");

            // this.source.properties.splice(0);
            // this.target.properties.splice(0);
            // this.source.optionalProperties.splice(0);
            // this.target.optionalProperties.splice(0);

            let allBlocks = this.Workspace.getTopBlocks();

            allBlocks.forEach( block => {
              this.notConnectedToStart = false;
              if(block.type === 'start'){
                block.getChildren().forEach(i => { 
                  this.processingPropertyWithPrepFunc(i);
                  i.setDisabled(false);
                  switch(i.type) {

                    case "measure": {

                      let shownM = showThreshold(i);
                      let properties = this.getFromMeasure(i);

                      let threshold = i.getField("threshold").text_;
                      let measureFunc = i.getField("measureList").getDisplayText_();
                      this.metrics.splice(0);
                      if(shownM){
                        this.metrics.push(measureFunc.toLowerCase()+"("+ this.source.var.substr(1)+"."+properties.src+","+ this.target.var.substr(1)+"." +properties.tgt+")|"+threshold);
                      } else {
                        this.metrics.push(measureFunc.toLowerCase()+"("+this.source.var.substr(1)+"."+properties.src+","+this.target.var.substr(1)+"."+properties.tgt+")"); 
                      }
            
                      break;
                    }

                    case "operator": {
                      let firstM = i.getChildren()[0];
                      let secondM = i.getChildren()[1];

                      this.mainSource = false;
                      this.mainTarget = false;

                      let props1 = "", props2 = "";
                      if(firstM && firstM.type === "measure"){
                        props1 = this.getFromMeasure(firstM);
                      }
                      if(secondM && secondM.type === "measure"){
                        props2 = this.getFromMeasure(secondM);
                      }

                      if(firstM){
                        firstM.setDisabled(false);



                        if(secondM && secondM.getChildren().length && (secondM.getChildren()[0].type ==='sourceproperty' || secondM.getChildren()[0].type ==='targetproperty')){
                          let secondMChildren = secondM.getChildren();
                          if(secondMChildren.length){
                            secondMChildren.forEach(m => {
                              if(m.type === 'sourceproperty'){
                                this.mainSource = true;
                              }
                              if(m.type === 'targetproperty'){
                                this.mainTarget = true;
                              }
                            });
                          }

                        } else {

                          if(firstM.getChildren().length){
                            firstM.getChildren().forEach(m => {
                              if(m.type === 'sourceproperty'){
                                this.mainSource = true;
                              }
                              if(m.type === 'targetproperty'){
                                this.mainTarget = true;
                              }
                            });

                          }
                        }
                      }

                      if(secondM){
                        secondM.setDisabled(false);

                        //console.log(this.mainSource,this.mainTarget);


                          if(this.mainSource){
                            secondM.inputList[1].setCheck([
                              "SourceProperty",
                              "PreprocessingFunction",
                              "OptionalSourceProperty"        
                            ]);
                          } else {
                            secondM.inputList[1].setCheck([
                              "SourceProperty",
                              "PreprocessingFunction"   
                            ]);
                          }
            
                          if(this.mainTarget){
                            secondM.inputList[2].setCheck([
                              "TargetProperty",
                              "PreprocessingFunction",
                              "OptionalTargetProperty"        
                            ]);
                          } else {
                            secondM.inputList[2].setCheck([
                              "TargetProperty",
                              "PreprocessingFunction"   
                            ]);
                          }

                          /////

                          if(this.mainSource){
                            firstM.inputList[1].setCheck([
                              "SourceProperty",
                              "PreprocessingFunction",
                              "OptionalSourceProperty"        
                            ]);
                          } else {
                            firstM.inputList[1].setCheck([
                              "SourceProperty",
                              "PreprocessingFunction"   
                            ]);
                          }
            
                          if(this.mainTarget){
                            firstM.inputList[2].setCheck([
                              "TargetProperty",
                              "PreprocessingFunction",
                              "OptionalTargetProperty"        
                            ]);
                          } else {
                            firstM.inputList[2].setCheck([
                              "TargetProperty",
                              "PreprocessingFunction"   
                            ]);
                          }
 

                      }
                      let shownFirstM = showThreshold(firstM);
                      let shownSecondM = showThreshold(secondM);

                      let operator = i.getField("operators").text_;
                      let measureFunc1 = firstM ? firstM.getField("measureList").getDisplayText_() : "undefined";
                      let measureFunc2 = secondM ? secondM.getField("measureList").getDisplayText_() : "undefined";
                      let threshold1 = firstM ? firstM.getField("threshold").text_ : "undefined";
                      let threshold2 = secondM ? secondM.getField("threshold").text_ : "undefined";
                      let varSrc = this.source.var.substr(1)+".";
                      let varTgt = this.target.var.substr(1)+".";
                      this.metrics.splice(0);
                      if(operator[0] !== 'N'){       
                        let str1 = operator + '(' + measureFunc1.toLowerCase()+"("+varSrc+props1.src+","+varTgt+props1.tgt+")"; 
                        let str2 = ',' + measureFunc2.toLowerCase()+"("+varSrc+props2.src+","+varTgt+props2.tgt+")";
                        if(shownFirstM && shownSecondM){
                          this.metrics.push(str1 + "|" + threshold1 + str2 + "|"+threshold2 + ')');
                        } else {

                          if(!shownFirstM && !shownSecondM){
                            this.metrics.push(str1 + str2 + ')');
                          }

                          if(shownFirstM && !shownSecondM){
                            this.metrics.push(str1 + "|" + threshold1 + str2 + ')');
                          } else if(!shownFirstM && shownSecondM) {
                            this.metrics.push(str1 + str2 + "|"+threshold2 + ')');
                          }
                        }



                      } else {
                     
                        let str1 = 'NOT(' + operator.substr(1) + '(' + measureFunc1.toLowerCase()+"("+varSrc+props1.src+","+varTgt+props1.tgt+")"; 
                        let str2 = ',' + measureFunc2.toLowerCase()+"("+varSrc+props2.src+","+varTgt+props2.tgt+")";  
                        if(shownFirstM && shownSecondM){
                          this.metrics.push(str1 + "|" + threshold1 + str2 + "|"+threshold2 + ')))');
                        } else {

                          if(!shownFirstM && !shownSecondM){
                            this.metrics.push(str1 + str2 + ')))');
                          }

                          if(shownFirstM && !shownSecondM){
                            this.metrics.push(str1 + "|" + threshold1 + str2 + ')))');
                          } else if(!shownFirstM && shownSecondM) {
                            this.metrics.push(str1 + str2 + "|"+threshold2 + ')))');
                          }
                        }

                      }




                      break;
                    }

                    default: {
                    }
                  }

                  // if(this.exPrefixes.length){
                  //   this.exPrefixes.forEach(pref => {
                  //     this.prefixes.forEach(pr => {
                  //       if(pref.label === pr.label){
                  //         this.deletePrefix(pr);
                  //       }
                  //     })
                  //   }) 
                  // }

                  // this.exPrefixes.splice(0);
                  this.deleteOldPrefixes();

                  // this.addOldAndNewPrefix(this.source.properties);
                  // this.addOldAndNewPrefix(this.target.properties);

                  // this.addOldAndNewPrefix(this.source.optionalProperties);
                  // this.addOldAndNewPrefix(this.target.optionalProperties);
                
                })
              } else {
                
                if(block.type.indexOf('preprocessing')!== -1){
                  this.notConnectedToStart = true;
                  this.processingPropertyWithPrepFunc(block);
                }
                block.setDisabled(true);
              }
            });



          }
          this.Workspace.addChangeListener(onFirstComment);

          
    };
    setTimeout(blocklyInit, 500);
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
    fetch(window.PREPROCESSING_LIST)
            .then(function(response) {
              return response.json();
             })
            .then((content) => {
              PreprocessingFunction.args0[0].options.length = 0;
              content.availablePreprocessings.forEach(prepr => {
                if(prepr.name !== "rename"){
                  PreprocessingFunction.args0[0].options.push([prepr.name, prepr.name]);
                }
              });
            })
            //.catch( alert );        
          let typesOfBlocks = this.typesOfBlocks;
          for(let key in typesOfBlocks){
            Blockly.Blocks[key] = {
              init: function() {
                this.jsonInit(typesOfBlocks[key]);              
              }
            };
          }

    Measure.args0[0].options.length = 0;
    this.measureOptions.forEach(i => {
      Measure.args0[0].options.push(i);
    });  

  },
  methods: {
    deletePrefix(prefix) {
      let amountAppearPrefixes = this.allPrefixes.filter(p => p.label === prefix.label).length;
      if(amountAppearPrefixes > 1){
        this.allPrefixes = this.allPrefixes.filter(p => p.label !== prefix.label && p.namespace !== prefix.namespace);
      } else {
        this.prefixes = this.prefixes.filter(p => p.label !== prefix.label && p.namespace !== prefix.namespace);
        this.allPrefixes = this.allPrefixes.filter(p => p.label !== prefix.label && p.namespace !== prefix.namespace);
      }
    },
    addPrefix(prefix) {
      //console.log(prefix);
      // push new prefix
      if(!this.prefixes.some(i => i.label === prefix.label)){
        this.prefixes.push(prefix);
      }
      this.allPrefixes.push(prefix);
    },
    deleteOldPrefixes(){
      if(this.exPrefixes.length){
        this.exPrefixes.forEach(pref => {
          this.prefixes.forEach(pr => {
            if(pref.label === pr.label){
              this.deletePrefix(pr);
            }
          })
        }) 
      }

      this.exPrefixes.splice(0);
    },
    addOldAndNewPrefix(props){
      props.forEach(pr => 
      {
          if(pr){
            let label = pr.split(":")[0];
            let pref = {label: label, namespace: this.context[label]};
            //if(!this.prefixes.some(i => i.label === label)){
              this.exPrefixes.push(pref);
            //}

            this.addPrefix({label: label, namespace: this.context[label]});
          }
      });
    },
    addProperies(i,strForXml){
      console.log(i);
      let propertiesClass = new Properties(this, i);
      propertiesClass.addProperies(i, strForXml); 
      console.log(i);
    },
    processingPropertyWithPrepFunc(i){
      let preprocessings = new Preprocessings(this,i);
      preprocessings.processingPropertyWithPrepFunc(i);
    },
    getFromMeasure(i){
      let src;
      let tgt;
      i.getChildren().forEach(
        pr => {
          pr.setDisabled(false);
          this.processingPropertyWithPrepFunc(pr);              
      }); 

      let valuesOfBlock = i.toString().split(' ');
      let values = [];
      let srcOrTgt;
      for(let val=0; val<valuesOfBlock.length; val++){
        if(valuesOfBlock[val] === "As"){
          srcOrTgt = valuesOfBlock[val-3].toLowerCase();
          values.push(valuesOfBlock[val+1]);
        }
      }

      if(values.length === 0){
        values = valuesOfBlock.filter(val => val.indexOf(":") !== -1);
      } 
      if(values.length === 1){
        if(srcOrTgt === 'source'){
          values.push(valuesOfBlock.filter(val => val.indexOf(":") !== -1)[1]);
        }
        if(srcOrTgt === 'target'){
          values.unshift(valuesOfBlock.filter(val => val.indexOf(":") !== -1)[0]);
        }
      }
      src = values[0];
      tgt = values[1];

      return {src:src, tgt:tgt};
    },
    manualMetricClicked(index){
      if(index === 0){
        this.mlalgorithm.enabled = false;
      } else {
        this.mlalgorithm.enabled = true;
      }
      this.source.properties.splice(0);
      this.target.properties.splice(0);
      this.source.optionalProperties.splice(0);
      this.target.optionalProperties.splice(0);
      this.deleteOldPrefixes();
    },
    exportWorkspace(){
      var xml = Blockly.Xml.workspaceToDom(this.Workspace);
      var xml_text = Blockly.Xml.domToPrettyText(xml);//domToText(xml);
      this.forceFileDownload(xml_text, "workspace.xml");
    },
    importWorkspace(){

      var fileToLoad = document.getElementById("fileToLoad").files[0];

      var fileReader = new FileReader();
      fileReader.onload = (fileLoadedEvent) => {
          var textFromFileLoaded = fileLoadedEvent.target.result;
          //this.importWorkspaceString = textFromFileLoaded;
          this.xmlToWorkspace(textFromFileLoaded);
      };

      fileReader.readAsText(fileToLoad, "UTF-8");

    },
    importConfigurationFile(){
      let confugurationFileParser = new configurationFileParser(this);
      confugurationFileParser.importConfigurationFile(); 
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

      const metrics = !this.mlalgorithm.enabled ? this.metrics
        .map(
          m => `<METRIC>
  ${m}
</METRIC>
`
        )
        .join('') : '';

      const acceptance = makeAccReview(this.acceptance, 'ACCEPTANCE');
      const review = makeAccReview(this.review, 'REVIEW');
      const ml = this.mlalgorithm.enabled
        ? `<MLALGORITHM>
  <NAME>${this.mlalgorithm.name}</NAME>
  <TYPE>${this.mlalgorithm.type}</TYPE>
  ${(this.mlalgorithm.type==='supervised batch' || this.mlalgorithm.type==='supervised active') ? `<TRAINING>${this.mlalgorithm.training}</TRAINING>` : ''}
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
        configHeader + prefixes + src + target + metrics + ml + acceptance + review  + execution + output + configFooter;
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
    showDialogForPreviousRun(){
      this.isDisabledLog = true;
      this.isDisabled = true;
      this.notFoundKeyMessage = '';
      this.executedKey = '';
      this.findStatusMessage = '';
      this.$refs.previousRunDialog.open();
    },
    closePreviousRun(){
      this.$refs.previousRunDialog.close();
    },
    checkExecutedKey(){
      this.isDisabledLog = true;
      this.isDisabled = true;
      this.notFoundKeyMessage = '';
      this.getStatusOfCertainJob();
    },
    getStatusOfCertainJob(){
      fetch(window.JOB_STATUS+this.executedKey)
      .then(r => r.json().then(x => x.status))
      .then(status => {
        if (status.code === -1) {
          this.findStatusMessage = status.description;
        }
        if (status.code === 0) {
          this.findStatusMessage = status.description;
          setTimeout(() => this.getStatusOfCertainJob(), 5000);
        }
        if (status.code === 1) {
          this.findStatusMessage = status.description;
          setTimeout(() => this.getStatusOfCertainJob(), 5000);
        }
        if (status.code === 2) {
          this.findStatusMessage = status.description;
          this.getResultsOfCertainJob();
        }
      })
    },
    getResultsOfCertainJob(){
      fetch(window.RESULT_FILES+this.executedKey)
              .then(function(response) {
                return response.json();
               })
              .then((content) => {
                this.isDisabledLog = false;
                if(content.success && content.availableFiles.length){
                  this.availableFiles.splice(0);
                  this.availableFiles = content.availableFiles;
                  this.isDisabled = false;
                } 
              })
    },
    saveAccepted(){
      if(this.availableFiles.length){
        fetch(window.RESULT_FILE+this.executedKey+"/"+this.availableFiles[1])
        .then(function(response) {
          return response.text();
         })
        .then((content) => {
          this.forceFileDownload(content,this.availableFiles[1]);
        })
      }
    },
    saveReviewed(){
      if(this.availableFiles.length){
        fetch(window.RESULT_FILE+this.executedKey+"/"+this.availableFiles[0])
        .then(function(response) {
          return response.text();
         })
        .then((content) => {
          this.forceFileDownload(content,this.availableFiles[0]);
        })
      }
    },
    forceFileDownload(str, filename){
      const url = window.URL.createObjectURL(new Blob([str]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', filename) //or any other extension
      document.body.appendChild(link)
      link.click()
    },
    saveXML(){
      this.forceFileDownload(this.generateConfig(),'file.xml');
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
          this.c = 'Status Loading - waiting for status from server..';
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
    xmlToWorkspace(str){
      var xml = Blockly.Xml.textToDom(str);
      if(str.indexOf('type="start"')!== -1){
        this.Workspace.getBlocksByType("start")[0].dispose(true);
      }
      Blockly.Xml.domToWorkspace(xml, this.Workspace);
    },
    exampleConfig() {
      this.exampleConfigEnable = true;
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
        id: 'sourceId',
        endpoint: 'http://linkedgeodata.org/sparql',
        var: '?s',
        pagesize: 2000,
        restriction: '?s a lgdo:RelayBox',
        type: '',
        properties: ['geom:geometry/geos:asWKT RENAME polygon'],
        optionalProperties: ['rdfs:label'],
        classes: ['http://linkedgeodata.org/ontology/RelayBox'],
        propertiesForChoice: [],
      };
      this.target = {
        id: 'targetId',
        endpoint: 'http://linkedgeodata.org/sparql',
        var: '?t',
        pagesize: 2000,
        restriction: '?t a lgdo:RelayBox',
        type: '',
        properties: ['geom:geometry/geos:asWKT RENAME polygon'],
        optionalProperties: ['rdfs:label'],
        classes: ['http://linkedgeodata.org/ontology/RelayBox'],
        propertiesForChoice: [],
      };
      this.importWorkspaceString = `
      <xml xmlns="http://www.w3.org/1999/xhtml">
        <block type="start" id="]~iOOuxgG1il)Qn#!5@R" deletable="false" x="0" y="0">
          <value name="NAME">
            <block type="measure" id="KV+e%A!n/j5k2T@D@*GH">
              <field name="measureList">Geo_Hausdorff</field>
              <field name="enable_threshold">FALSE</field>
              <field name="threshold">0.5</field>
              <value name="sourceProperty">
                <block type="renamepreprocessingfunction" id="$J8bghZmrI;ETkue6mwG">
                  <field name="RENAME">polygon</field>
                  <value name="RENAME">
                    <block type="sourceproperty" id="sp">
                      <field name="propTitle">geom:geometry/geos:asWKT</field>
                    </block>
                  </value>
                </block>
              </value>
              <value name="targetProperty">
                <block type="renamepreprocessingfunction" id="rpf">
                  <field name="RENAME">polygon</field>
                  <value name="RENAME">
                    <block type="targetproperty" id="tg">
                      <field name="propTitle">geom:geometry/geos:asWKT</field>
                    </block>
                  </value>
                </block>
              </value>
            </block>
          </value>
        </block>
      </xml>
      `;
      this.xmlToWorkspace(this.importWorkspaceString);
      this.metrics = ['geo_hausdorff(s.polygon, t.polygon)'];
      this.acceptance = {
        id: 'acceptance',
        threshold: 0.9,
        file: 'lgd_relaybox_verynear.nt',
        relation: 'lgdo:near',
      };
      this.review = {
        id: 'review',
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

function showThreshold(i){
  let isShown = false;
  if(i){
    if(i.getField("enable_threshold").getValue().toLowerCase() === 'true'){
      i.getField("threshold").setVisible(true);
      i.getField("threshold").forceRerender();
      isShown = true;
    } else {
      i.getField("threshold").setVisible(false);
      i.getField("threshold").forceRerender();
      isShown = false;
    }
  }
  return isShown;
}
