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

      <!-- spinner dialog -->
      <md-dialog ref="spinnerDialog">
        <md-dialog-title></md-dialog-title>

        <md-dialog-content>
          <div class="md-flex">
            <md-spinner md-indeterminate v-if="spinnerRunning"></md-spinner>
          </div>
        </md-dialog-content>
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
            <md-button class="md-primary" @click="exploreResults()">Explore the results</md-button>
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

      <!-- explore the results dialog -->
      <md-dialog ref="exploreResultsDialog">
        <md-dialog-title>Explore the results</md-dialog-title>
        <div style="margin-left: 26px;font-size: 18px;color:#3f51b5;">Iteration: {{exploreResultsArray.iteration}}</div>

        <md-dialog-content ref="focusOnTop">  
        	<md-list className="resultsList">
	          	<div v-for="(i, index) in iteratorResults">					
				    <md-list-item style="overflow:auto; word-wrap:break-word;">
				      	<div class="activeLearningItem">{{(pageNum*10-10)+index+1+"."}}</div>
				      	<div class="col activeLearningItem" style="overflow:auto; word-wrap:break-word;">{{i.source}}</div>
				      	<div class="col activeLearningItem" style="overflow:auto; word-wrap:break-word;">{{i.target}}</div>
						<md-button class="md-primary" @click="openActiveLearningTable((pageNum*10-10)+index)">Show table</md-button>
					    <md-radio v-model="radioButton[(pageNum*10-10)+index]" v-bind:md-value="true" class="md-primary" @change="changeRadioButton((pageNum*10-10)+index)">+</md-radio>
					    <md-radio v-model="radioButton[(pageNum*10-10)+index]" v-bind:md-value="false" class="md-primary" @change="changeRadioButton((pageNum*10-10)+index)">-</md-radio>	
				    </md-list-item> 
				    <md-table v-if="activeLearningTableForNum !== null && activeLearningTableForNum === (pageNum*10-10)+index">
					  <md-table-header>
					    <md-table-row>
					      <md-table-head>{{iteratorResults[activeLearningTableForNum].source}}</md-table-head>
					      <md-table-head>{{iteratorResults[activeLearningTableForNum].target}}</md-table-head>
					    </md-table-row>
					  </md-table-header>

					  <md-table-body>
					    <md-table-row v-for="(row, index) in iteratorResults[activeLearningTableForNum].sourceContext" :key="index">
					      <md-table-cell>
					      		<span>{{row.predicate}}</span><br>
						      	<span>{{row.object}}</span>
					      </md-table-cell>
					      <md-table-cell v-for="(col, index1) in 1" :key="index1">
					      		<span>{{iteratorResults[activeLearningTableForNum].targetContext[index].predicate}}</span><br>
						      	<span>{{iteratorResults[activeLearningTableForNum].targetContext[index].object}}</span>
					      </md-table-cell>
					    </md-table-row>
					  </md-table-body>
					</md-table>
		        </div> 
	        </md-list>
	        <paginate
		      :pageCount="Math.ceil(exploreResultsArray.examples.length/10)"
		      :containerClass="'pagination'"
		      :clickHandler="clickCallback"
		      :container-class="'resultsList'"
		      :page-range="3"
    		  :margin-pages="2">
			</paginate>  
        </md-dialog-content>

        <md-dialog-actions>
          <md-button class="md-raised md-primary" @click="skipIteration('explore')">Skip iteration</md-button>	
          <md-button class="md-raised md-primary" @click="continueExecute(false,'explore')">Continue execution</md-button>
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
          <md-button class="md-raised md-primary" @click="skipIteration('')">Skip iteration</md-button>	
          <md-button class="md-raised md-primary" @click="continueExecute(false,'')">Continue execution</md-button>
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
    	spinnerRunning: false,
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
	    exploreResultsArray: {"requestId" : "6a835f2e725bb8",
							 "iteration" : 1,
							 "examples" : [
							   {
							     "source" : "http://some.domain.tld/someSourceUri",
							     "target" : "http://some.domain.tld/someTargetUri",
							     "sourceContext" : [
							     	  {
							     	    "predicate" : "http://some.domain.tld/somePredicateUri",
							     	    "object" : "http://some.domain.tld/somObjectUri"
							     	  },
							          {
							     	    "predicate" : "http://some.domain.tld/somePredicateUri1",
							     	    "object" : "http://some.domain.tld/somObjectUri1"
							     	  },
							          {
							     	    "predicate" : "http://some.domain.tld/somePredicateUri2",
							     	    "object" : "http://some.domain.tld/somObjectUri2"
							     	  },
							     ],
							     "targetContext" : [
							     	  {
							     	    "predicate" : "http://some.domain.tld/somePredicateUri",
							     	    "object" : "http://some.domain.tld/somObjectUri0"
							     	  },
							          {
							     	    "predicate" : "http://some.domain.tld/somePredicateUri1",
							     	    "object" : "http://some.domain.tld/somObjectUri10"
							     	  },
							          {
							     	    "predicate" : "http://some.domain.tld/somePredicateUri2",
							     	    "object" : "http://some.domain.tld/somObjectUri20"
							     	  },
							     ]
							   },
							 ]
							},
		iteratorResults: null,
		pageNum: 1,
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
	      this.spinnerRunning = true;
	      this.$refs.spinnerDialog.open();
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
	        	this.spinnerRunning = false;
	      		this.$refs.spinnerDialog.close();
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
	    skipIteration(exploreResultsString){
	    	if(exploreResultsString.length){
	    		this.continueExecute(true, exploreResultsString);
	    	} else{
				this.continueExecute(true);
	    	}	
	    },
	    continueExecute(isSkip, exploreResultsString){
	    	let backendUrl = window.LIMES_SERVER_URL + '/activeLearning/'+this.activeLearningArray.requestId;
	    	if(exploreResultsString.length){
	    		backendUrl = window.LIMES_SERVER_URL + '/exploreResults/'+this.exploreResultsArray.requestId;//another url
	    	}
	    	let exampleScores = this.calculateScores(isSkip);

	    	// send them: http://localhost:8080/activeLearning/6a835f2e725bb8
	    	fetch(backendUrl, {
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
	    exploreResults(){
	      this.$refs.exploreResultsDialog.open();
	      this.iteratorResults = this.exploreResultsArray.examples.slice(0,10);
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
		clickCallback (pageNum){
	      console.log(pageNum)
	      this.pageNum = pageNum;
	      let size = 10;
	      let from = pageNum*size-size;
	      let to = pageNum*size;
	      console.log(from, to);
	      this.iteratorResults = this.exploreResultsArray.examples.slice(from, to);
	    },
	}
}
</script>

<style lang="css">
.pagination {
  display: inline-block;
  padding-left: 0;
  margin: 20px 0;
  border-radius: 4px;
}
.pagination > li {
  display: inline;
}
.pagination > li > a,
.pagination > li > span {
  position: relative;
  float: left;
  padding: 6px 12px;
  margin-left: -1px;
  line-height: 1.42857143;
  color: #337ab7;
  text-decoration: none;
  background-color: #fff;
  border: 1px solid #ddd;
}
.pagination > li:first-child > a,
.pagination > li:first-child > span {
  margin-left: 0;
  border-top-left-radius: 4px;
  border-bottom-left-radius: 4px;
}
.pagination > li:last-child > a,
.pagination > li:last-child > span {
  border-top-right-radius: 4px;
  border-bottom-right-radius: 4px;
}
.pagination > li > a:hover,
.pagination > li > span:hover,
.pagination > li > a:focus,
.pagination > li > span:focus {
  z-index: 3;
  color: #23527c;
  background-color: #eee;
  border-color: #ddd;
}
.pagination > .active > a,
.pagination > .active > span,
.pagination > .active > a:hover,
.pagination > .active > span:hover,
.pagination > .active > a:focus,
.pagination > .active > span:focus {
  z-index: 2;
  color: #fff;
  cursor: default;
  background-color: #337ab7;
  border-color: #337ab7;
}
.pagination > .disabled > span,
.pagination > .disabled > span:hover,
.pagination > .disabled > span:focus,
.pagination > .disabled > a,
.pagination > .disabled > a:hover,
.pagination > .disabled > a:focus {
  color: #777;
  cursor: not-allowed;
  background-color: #fff;
  border-color: #ddd;
}
.pagination-lg > li > a,
.pagination-lg > li > span {
  padding: 10px 16px;
  font-size: 18px;
  line-height: 1.3333333;
}
.pagination-lg > li:first-child > a,
.pagination-lg > li:first-child > span {
  border-top-left-radius: 6px;
  border-bottom-left-radius: 6px;
}
.pagination-lg > li:last-child > a,
.pagination-lg > li:last-child > span {
  border-top-right-radius: 6px;
  border-bottom-right-radius: 6px;
}
.pagination-sm > li > a,
.pagination-sm > li > span {
  padding: 5px 10px;
  font-size: 12px;
  line-height: 1.5;
}
.pagination-sm > li:first-child > a,
.pagination-sm > li:first-child > span {
  border-top-left-radius: 3px;
  border-bottom-left-radius: 3px;
}
.pagination-sm > li:last-child > a,
.pagination-sm > li:last-child > span {
  border-top-right-radius: 3px;
  border-bottom-right-radius: 3px;
}
</style> 
