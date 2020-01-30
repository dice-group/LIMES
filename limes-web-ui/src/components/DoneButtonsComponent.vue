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

      <!-- supervised active ml dialog -->
      <md-dialog ref="supervisedActiveMLDialog" v-bind:md-esc-to-close="false" v-bind:md-click-outside-to-close="false">
        <md-dialog-title>Supervised active ML dialog</md-dialog-title>
        <div style="margin-left: 26px;font-size: 18px;color:#3f51b5;">Iteration: {{activeLearningArray.iteration}}</div>

        <md-dialog-content ref="focusOnTop">  
        	<md-list>
	          	<div v-for="(i, index) in activeLearningArray.examples">					
				    <md-list-item style="overflow:auto; word-wrap:break-word;">
				      	<div class="activeLearningItem">{{index+1+"."}}</div>
				      	<div class="col activeLearningItem" style="overflow:auto; word-wrap:break-word;">{{i.source}}</div>
				      	<div class="col activeLearningItem" style="overflow:auto; word-wrap:break-word;">{{i.target}}</div>
						<md-button class="md-primary" @click="openActiveLearningTable(index)">Show table</md-button>
					    <md-radio v-model="radioButton[index]" v-bind:md-value="true" class="md-primary" @change="changeRadioButton(index)">+</md-radio>
					    <md-radio v-model="radioButton[index]" v-bind:md-value="false" class="md-primary" @change="changeRadioButton(index)">-</md-radio>	
				    </md-list-item> 
				    <md-table v-if="activeLearningTableForNum !== null && activeLearningTableForNum === index">
					  <md-table-header>
					    <md-table-row>
					      <md-table-head>{{activeLearningArray.examples[activeLearningTableForNum].source}}</md-table-head>
					      <md-table-head>{{activeLearningArray.examples[activeLearningTableForNum].target}}</md-table-head>
					    </md-table-row>
					  </md-table-header>

					  <md-table-body>
					    <md-table-row v-for="(row, index) in activeLearningArray.examples[activeLearningTableForNum].sourceContext" :key="index">
					      <md-table-cell>
					      		<span>{{row.predicate}}</span><br>
						      	<span>{{row.object}}</span>
					      </md-table-cell>
					      <md-table-cell v-for="(col, index1) in 1" :key="index1">
					      		<span>{{activeLearningArray.examples[activeLearningTableForNum].targetContext[index].predicate}}</span><br>
						      	<span>{{activeLearningArray.examples[activeLearningTableForNum].targetContext[index].object}}</span>
					      </md-table-cell>
					    </md-table-row>
					  </md-table-body>
					</md-table>
		        </div> 
	        </md-list>  
        </md-dialog-content>

        <md-dialog-actions>
          <md-button class="md-raised md-primary" @click="skipIteration()">Skip iteration</md-button>	
          <md-button class="md-raised md-primary" @click="continueExecute(false)">Continue execution</md-button>
        </md-dialog-actions>
      </md-dialog>


      <!-- visible part -->
      <md-layout>
        <md-button class="md-raised md-flex" v-on:click="showConfig()">Display config</md-button>
      </md-layout>
      <md-layout>
        <md-button class="md-raised md-primary md-flex" v-on:click="mlalgorithm.type === 'supervised active' ? openActiveWindow() : execute()">Execute</md-button>
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
	    activeLearningArray: {},
	    radioButton: [],
	    activeLearningTableForNum: null,
    }
  },
  // created() {
  //   this.activeLearningArray.examples.forEach(i => {
  //   	this.radioButton.push(false);
  //   });
  // },
	methods: {

		makeDatasource(data, tag){ return `<${tag.toUpperCase()}>
  <ID>${data.id}</ID>
  <ENDPOINT>${data.endpoint}</ENDPOINT>
  <VAR>${data.var}</VAR>
  <PAGESIZE>${data.pagesize}</PAGESIZE>
  <RESTRICTION>${data.restriction}</RESTRICTION>
${data.properties.length ? data.properties.map(p => `  <PROPERTY>${p}</PROPERTY>`).join('\n') : ''}
${data.function && data.function.length ? `  <FUNCTION>${data.function}</FUNCTION>` : ''}
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
	    // open this window if mlalgorithm.type === 'supervised active'
	    openActiveWindow(){
	      this.jobError = false;
	      const config = this.generateConfig();
	      const configBlob = new Blob([config], {type: 'text/xml'});
	      const fd = new FormData();
	      fd.append('config_file', configBlob, 'config.xml');
	      fetch(window.LIMES_SERVER_URL + '/submit', {
	        method: 'post',
	        body: fd,
	      })
	        .then(r => r.json())
	        .then(r => {
	        	if(r.examples.length === 0){
	        		alert('There are errors in the configuration!');
	        	}
	        	this.radioButton = [];
	        	r.examples.forEach(i => {
					this.radioButton.push(false);
				});
	        	this.activeLearningArray = r;
	        	this.$refs.supervisedActiveMLDialog.open();
	        })
	        .catch(e => {
	          alert(e.toString());	
	          this.jobError = `Error while starting the job: ${e.toString()}`;
	        });
	  
	    },
	    openActiveLearningTable(index){
	    	this.activeLearningTableForNum = index;
	    },
	    changeRadioButton(index){
			this.radioButton[index] = !this.radioButton[index];
	    },
	    calculateScores(isSkip){
	    	let exampleScores = this.radioButton;
	    	exampleScores = exampleScores.map(i => {
	    		if(isSkip){
	    			return 0;
	    		} else {
		    		let score = -1;
		    		if(i){
		    			score = 1;
		    		}
		    		return score;
		    	}
	    	});
	    	return exampleScores;
	    },
	    skipIteration(){	
	    	this.continueExecute(true);
	    },
	    continueExecute(isSkip){
	    	
	    	let exampleScores = this.calculateScores(isSkip);

	    	// send them: http://localhost:8080/activeLearning/6a835f2e725bb8
	    	fetch(window.LIMES_SERVER_URL + '/activeLearning/'+this.activeLearningArray.requestId, {
		        method: 'post',
		        body: JSON.stringify({"exampleScores" : exampleScores}),
		      })
		        .then(r => r.json())
		        .then(r => {
		          this.jobId = r.requestId;
		          if(r.examples !== undefined && r.examples.length !== 0){
		          	this.radioButton = [];
		          	r.examples.forEach(i => {
				    	this.radioButton.push(false);
				    });
		          	this.activeLearningArray = r;
		          	this.$nextTick(() => {
			            this.$refs.focusOnTop.$el.scrollTop = 0
			        })
		          } else {
		          	this.$refs.supervisedActiveMLDialog.close();
		          	this.jobRunning = true;
			        this.jobError = false;
			        this.c = 'Status Loading - waiting for status from server..';
			        this.$refs.jobDialog.open();
			        history.pushState({jobId: r.requestId}, '', `?jobId=${r.requestId}`);
			        setTimeout(() => this.getStatus(), 1000);
		          }
		        })
		        .catch(e => {
		          alert(e.toString());
		          this.jobError = `Error while starting the job: ${e.toString()}`;
		          this.jobRunning = false;
		        });
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
