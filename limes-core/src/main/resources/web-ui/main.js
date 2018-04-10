// apply vue-material stuff
Vue.use(VueMaterial);

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
    // config
    prefixes: [],
    source: {
      id: 'sourceId',
      endpoint: 'http://source.endpoint.com/sparql',
      var: '?src',
      pagesize: 1000,
      restriction: '?src rdf:type some:Type',
      type: 'sparql',
      properties: ['dc:title AS lowercase RENAME name'],
      optionalProperties: ['rdfs:label'],
    },
    target: {
      id: 'targetId',
      endpoint: 'http://target.endpoint.com/sparql',
      var: '?target',
      pagesize: 1000,
      restriction: '?target rdf:type other:Type',
      type: 'sparql',
      properties: ['foaf:name AS lowercase RENAME name'],
      optionalProperties: ['rdf:type'],
    },
    metrics: ['trigrams(y.dc:title, x.linkedct:condition_name)'],
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
    output: 'TAB',
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
  },
  methods: {
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

      const output = `<OUTPUT>${this.output}</OUTPUT>
`;

      const config =
        configHeader + prefixes + src + target + metrics + acceptance + review + ml + execution + output + configFooter;
      return config;
    },
    showConfig() {
      this.configText = this.generateConfig();
      this.$refs.configDialog.open();
      // syntax highlight for xml
      setTimeout(() => hljs.highlightBlock(document.getElementsByTagName('code')[0]));
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
      const configBlob = new Blob([config], {type: 'text/plain'});
      const fd = new FormData();
      fd.append('fileupload', configBlob, 'config.xml');
      fetch(window.LIMES_SERVER_URL + '/execute', {
        method: 'post',
        body: fd,
      })
        .then(r => r.text())
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
      fetch(window.LIMES_SERVER_URL + '/get_status/?job_id=' + this.jobId)
        .then(r => r.text())
        .then(status => {
          this.jobError = false;
          this.jobStatus = status;
          if (status === '-1') {
            this.jobRunning = false;
            this.jobStatusText =
              'Status Unknown - a configuration file for the given job_id has not been found on the server';
          }
          if (status === '0') {
            this.jobRunning = true;
            this.jobStatusText =
              'Status Scheduled - the configuration file is present and the job is waiting for execution';
            setTimeout(() => this.getStatus(), 5000);
          }
          if (status === '1') {
            this.jobRunning = true;
            this.jobStatusText = 'Status Running - the job is currently running';
            setTimeout(() => this.getStatus(), 5000);
          }
          if (status === '2') {
            this.jobRunning = false;
            this.jobStatusText = 'Status Finished - the job is finished and its output files are ready for delivery';
            this.jobShowResult = true;
          }
        })
        .catch(e => {
          this.jobError = `Error getting job status: ${e.toString()}`;
          this.jobRunning = false;
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
      this.output = 'TAB';
    },
  },
});
