<template>
    <md-layout md-gutter="16">
      <!-- config text dialog -->
      <md-dialog ref="configDialog">
        <md-dialog-title>Config XML</md-dialog-title>

        <md-dialog-content><pre><code class="xml hljs" v-html="configText"></code></pre></md-dialog-content>

        <md-dialog-actions>
          <md-button class="md-primary" @click="saveXML()">Save XML</md-button>
          <md-button class="md-primary" @click="closeConfig()">Close</md-button>
        </md-dialog-actions>
      </md-dialog>

      <!-- job status dialog -->
      <md-dialog ref="jobDialog">
        <md-dialog-title>Job status</md-dialog-title>

        <md-dialog-content>
          <div class="md-flex">
            <md-spinner md-indeterminate v-if="jobRunning"></md-spinner>
          </div>
          <div class="md-flex" v-if="!jobError">
            {{ jobStatusText }}
          </div>
          <div class="md-flex" style="color: red;" v-if="jobError">
            {{ jobError }}
          </div>
          <div class="md-flex" v-if="jobStatus > 0 || jobStatus === -2">
            <a v-bind:href=" limesServerUrl + '/logs/' + jobId" target="_blank">Show log</a>
          </div>
          <div class="md-flex" v-if="jobStatus === 2">
            Get results:
              <ul>
                <li v-for="r in results"><a v-bind:href=" limesServerUrl + '/result/' + jobId + '/' + r " target="_blank">{{r}}</a></li>
              </ul>
          </div>
          <div>Save the execution key for checking the state later: <span class="executionKey">{{jobId}}</span></div>
        </md-dialog-content>

        <md-dialog-actions>
          <md-button class="md-primary" @click="closeStatus()">Close</md-button>
        </md-dialog-actions>
      </md-dialog>

      <!-- previous run dialog -->
      <md-dialog ref="previousRunDialog">
        <md-dialog-title>Check the state of the previous run</md-dialog-title>

        <md-dialog-content>
          <md-layout>
            <md-layout>
              <span class="keyInputLabel">Enter execution key:</span> 
            </md-layout>
            <md-layout md-flex="50">
              <md-input-container>
                <md-input v-model="executedKey" @keyup.enter.native="checkExecutedKey()" placeholder="Enter execution key"></md-input>
              </md-input-container>
              <p class="message">{{notFoundKeyMessage}}</p>
              <p class="message">{{findStatusMessage}}</p>
            </md-layout>
            <md-layout md-flex="20">
              <md-button class="md-primary" @click="checkExecutedKey()">Check</md-button>
            </md-layout>
          </md-layout>  
        </md-dialog-content>

        <md-dialog-actions>
          <md-button v-bind:disabled="isDisabledLog" class="md-primary" 
            v-bind:href=" limesServerUrl + '/logs/' + executedKey" target="_blank"
          >Show log</md-button>
          <md-button v-bind:disabled="isDisabled" class="md-primary" @click="saveAccepted()">Save accepted links</md-button>
          <md-button v-bind:disabled="isDisabled" class="md-primary" @click="saveReviewed()">Save reviewed links</md-button>
          <md-button class="md-primary" @click="closePreviousRun()">Close</md-button>
        </md-dialog-actions>
      </md-dialog>


      <!-- visible part -->
      <md-layout>
        <md-button class="md-raised md-flex" v-on:click="showConfig()">Display config</md-button>
      </md-layout>
      <md-layout>
        <md-button class="md-raised md-primary md-flex" v-on:click="execute()">Execute</md-button>
      </md-layout>
      <md-layout>
        <md-button class="md-raised md-flex" v-on:click="showDialogForPreviousRun()">Check the state of the previous run</md-button>
      </md-layout>
    </md-layout>
</template>

<script>
import SharedMethods from './SharedMethods.vue'
export default {
	mixins: [SharedMethods],
	components: {
		
	},
	props: {
	    prefixes: {
	      type: Array,
	    },
		metrics: {
	      type: Array,
	    }, 
	    review: {
	      type: Object,
	    },
	    acceptance: {
	      type: Object,
	    }, 	
	    mlalgorithm: {
	      type: Object,
	    }, 		   
	    execution: {
	      type: Object,
	    },
	    output: {
	      type: Object,
	    },	    	             
	},
  data () {
    return {
    	configText: '',
    	// execution progress
	    jobId: '',
	    jobRunning: false,
	    jobShowResult: false,
	    jobStatus: '-1',
	    jobStatusText: 'loading..',
	    jobError: false,
	    results: [],
	    limesServerUrl: '',
	    // previous run
	    executedKey: '',
	    isDisabledLog: true,
	    isDisabled: true,
	    availableFiles: [],
	    notFoundKeyMessage: '',
	    findStatusMessage: '',
	    exampleConfigEnable: false,
    }
  },
	methods: {

		makeDatasource(data, tag){ return `<${tag.toUpperCase()}>
  <ID>${data.id}</ID>
  <ENDPOINT>${data.endpoint}</ENDPOINT>
  <VAR>${data.var}</VAR>
  <PAGESIZE>${data.pagesize}</PAGESIZE>
  <RESTRICTION>${data.restriction}</RESTRICTION>
${data.properties.length ? data.properties.map(p => `  <PROPERTY>${p}</PROPERTY>`).join('\n') : ''}
${data.optionalProperties.length ? data.optionalProperties.map(p => `  <OPTIONAL_PROPERTY>${p}</OPTIONAL_PROPERTY>`).join('\n') : ''}
${data.type && data.type.length ? `  <TYPE>${data.type}</TYPE>` : ''}
</${tag.toUpperCase()}>
`},

		makeAccReview(data, tag){ return `<${tag.toUpperCase()}>
  <THRESHOLD>${data.threshold}</THRESHOLD>
  <FILE>${data.file}</FILE>
  <RELATION>${data.relation}</RELATION>
</${tag.toUpperCase()}>
`},

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

			const src = this.makeDatasource(this.$store.state.source, 'SOURCE');
			const target = this.makeDatasource(this.$store.state.target, 'TARGET');

			      // add threshold from review threshold if user didn't choose threshold for measures using operators
		      let changedMetrics = [];
		      //let operators = ['and','or','xor','nand'];
		      let hasOperator = this.$store.state.operators.filter( i => this.metrics[0].toLowerCase().indexOf(i) !== -1);
		      if(hasOperator){ 
		        let isLeftThreshold = this.metrics[0].indexOf("),");
		        let isRightThreshold = this.metrics[0].indexOf("))"); 
		        let placeForThreshold = this.metrics[0].split(")");  
		        if (isLeftThreshold !== -1 && isRightThreshold !== -1){
		          // user didn't choose threshold
		          let strWithThreshold = placeForThreshold[0] + ")|" + this.review.threshold + placeForThreshold[1] + ")|" + this.review.threshold+")";
		          changedMetrics.push(strWithThreshold);
		        } else if(isLeftThreshold !== -1 && isRightThreshold === -1){
		          // user changed right threshold  
		          let strWithThreshold = placeForThreshold[0] + ")|" + this.review.threshold + placeForThreshold[1] + ")" + placeForThreshold[2] +")";
		          changedMetrics.push(strWithThreshold);
		        } else if(isLeftThreshold === -1 && isRightThreshold !== -1){
		          // user changed left threshold  
		          let strWithThreshold = placeForThreshold[0] + ")" + placeForThreshold[1] + ")|" + this.review.threshold+")";
		          changedMetrics.push(strWithThreshold);
		        } else {
		          changedMetrics = this.metrics;
		        }
		      } else {
		        // don't change
		        changedMetrics = this.metrics;
		      }
		      
		    const metrics = !this.mlalgorithm.enabled ? changedMetrics//this.metrics
        .map(m => `<METRIC>
  ${m}
</METRIC>
`
        )
        .join('') : '';

		const acceptance = this.makeAccReview(this.acceptance, 'ACCEPTANCE');
      const review = this.makeAccReview(this.review, 'REVIEW');
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
	    saveXML(){
	      this.forceFileDownload(this.generateConfig(),'file.xml');
	    },
	    // execute button
	    execute() {
	      this.limesServerUrl = window.LIMES_SERVER_URL;	
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
	    closeStatus() {
	      history.pushState({}, '', '?');
	      this.$refs.jobDialog.close();
	    },

	    //button check the state of the previous one 
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
	}
}
</script>
