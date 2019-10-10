<template>
  <div>
    <Toolbar 
    v-bind:configurationFile="configurationFile"
    v-bind:exampleConfig="exampleConfig" 
    v-bind:exampleFilmAndMovieConfig="exampleFilmAndMovieConfig"
    v-bind:importConfigurationFile="importConfigurationFile"
    ></Toolbar>
    <md-layout md-gutter>
      <md-layout md-flex-offset="10" md-column>
        <PrefixesUI v-bind:deleteChip="deletePrefix" v-bind:addPrefix="addPrefix"
                v-bind:prefixes="prefixes" v-bind:filteredOptions="filteredOptions"
                v-bind:context="context">
        </PrefixesUI>
        <div class="row">        
          <DatasourceUI 
          class="twoColumns" 
          title="Data source" 
          v-bind:source="this.$store.state.source"
          v-bind:advancedOptionsShow="advancedOptionsShow"
          v-bind:endpointandclasses="this.$store.state.target"  
          v-bind:prefixes="context"    
          v-bind:deletePrefix="deletePrefix" 
          v-bind:addPrefix="addPrefix"
          v-bind:exampleConfigEnable="exampleConfigEnable"
          ></DatasourceUI>
          <DatasourceUI 
          class="twoColumns" 
          title="Data target" 
          v-bind:source="this.$store.state.target"
          v-bind:advancedOptionsShow="advancedOptionsShow"
          v-bind:endpointandclasses="this.$store.state.source" 
          v-bind:prefixes="context"
          v-bind:deletePrefix="deletePrefix" 
          v-bind:addPrefix="addPrefix"
          v-bind:exampleConfigEnable="exampleConfigEnable"
          ></DatasourceUI>
        </div>

        <md-whiteframe md-tag="section" style="flex: 1; padding-top: 15px;">
            <md-tabs @change="manualMetricClicked">
              <ManualMetric 
              v-bind:metrics="metrics" 
              v-bind:deleteOldPrefixes="deleteOldPrefixes"
              ></ManualMetric>
              <MachineLearning
              v-bind:mlalgorithm="mlalgorithm" 
              ></MachineLearning>
            </md-tabs> 
        </md-whiteframe>  

        <!-- acceptance UI -->
        <div class="row">
          <AccreviewComponent class="twoColumns" v-bind:data="acceptance" v-bind:review="review" v-bind:acceptance="acceptance" v-bind:prefixes="context" v-on:send-prefix="addPrefix($event)" v-on:del-exprefix="deletePrefix($event)" v-on:change-threshold="acceptance.threshold = $event" title="Acceptance Condition"></AccreviewComponent>
          <AccreviewComponent class="twoColumns" v-bind:data="review" v-bind:review="review" v-bind:acceptance="acceptance" v-bind:prefixes="context" v-on:send-prefix="addPrefix($event)" v-on:del-exprefix="deletePrefix($event)" v-on:change-revthreshold="review.threshold = $event" title="Review Condition"></AccreviewComponent>
        </div>

        <!-- execution UI -->
        <ExecutionComponent v-bind:execution="execution" v-bind:advancedOptionsShow="advancedOptionsShow"
        ></ExecutionComponent>

        <!-- output UI -->
        <OutputComponent v-bind:output="output"></OutputComponent>
        <!-- enable advanced options UI -->
        <AdvancedoptionsComponent 
        v-bind:advancedOptionsShow="advancedOptionsShow"
        v-on:toggle-advanced-options="advancedOptionsShow = $event"
        ></AdvancedoptionsComponent>

        <!-- done button -->
        <DoneButtonsComponent
        v-bind:prefixes="prefixes"
        v-bind:metrics="metrics"
        v-bind:review="review"
        v-bind:acceptance="acceptance"
        v-bind:mlalgorithm="mlalgorithm"
        v-bind:execution="execution"
        v-bind:output="output"
        ></DoneButtonsComponent>
      </md-layout>
        <md-layout md-flex="10"></md-layout>
    </md-layout>
        

  </div>
</template>

<script>
import Toolbar from './Toolbar.vue'
import PrefixesUI from './PrefixesUI.vue'
import DatasourceUI from './DatasourceUI.vue'
import ManualMetric from './ManualMetric.vue'
import MachineLearning from './MachineLearning.vue'
import AccreviewComponent from './AccreviewComponent.vue'
import ExecutionComponent from './ExecutionComponent.vue'
import OutputComponent from './OutputComponent.vue'
import AdvancedoptionsComponent from './AdvancedoptionsComponent.vue'
import DoneButtonsComponent from './DoneButtonsComponent.vue'
import ToolbarMethods from './ToolbarMethods.vue'
export default {
  mixins: [ToolbarMethods],
  components: {
    Toolbar,
    PrefixesUI, 
    DatasourceUI, 
    ManualMetric, 
    MachineLearning, 
    AccreviewComponent, 
    ExecutionComponent,
    OutputComponent,
    AdvancedoptionsComponent,
    DoneButtonsComponent,
  },
  data () {
    return {
      exPrefixes: [],
      prefixes: [{label: 'owl', namespace: 'http://www.w3.org/2002/07/owl#'}],
      allPrefixes: [{label: 'owl', namespace: 'http://www.w3.org/2002/07/owl#'}],
      filteredOptions: [],
      context: {},
      metrics: [''],
      mlalgorithm: {
        enabled: false,
        name: 'WOMBAT Simple',
        type: 'unsupervised',
        training: 'trainingData.nt',
        parameters: [
          {
            name: 'max refinement tree size',
            value: 2000,
          },
        ],
      },
      acceptance: {
        id: 'acceptance',
        threshold: 0.98,
        file: 'accepted.nt',
        relation: 'owl:sameAs',
      },
      review: {
        id: 'review',
        threshold: 0.9,
        file: 'reviewme.nt',
        relation: 'owl:sameAs',
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
      exampleConfigEnable: false,
      configurationFile: '',
    }
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
              this.$store.commit('changeContext', context);
              this.filteredOptions.push(...filteredOptions);
            })

              
    // this.$store.commit('changeSource', this.source);
    // this.$store.commit('changeTarget', this.target);

    // this.$store.commit('changeContext', this.context);

    // console.log(this.$store.state.source);

  },
  mounted(){

    this.$store.watch(this.$store.getters.getSrcProps, n => {
      if(n && n.length !== 0){
        this.deleteOldPrefixes();
        this.addOldAndNewPrefix(this.$store.state.source.properties);
      }
    });

    this.$store.watch(this.$store.getters.getTgtProps, n => {
      if(n && n.length !== 0){
        this.addOldAndNewPrefix(this.$store.state.target.properties);
      }
    });

    this.$store.watch(this.$store.getters.getOpSrcProps, n => {
      if(n && n.length !== 0){
        this.addOldAndNewPrefix(this.$store.state.source.optionalProperties);
      }
    });

    this.$store.watch(this.$store.getters.getOpTgtProps, n => {
      if(n && n.length !== 0){
        this.addOldAndNewPrefix(this.$store.state.target.optionalProperties);
      }
    });

    this.$store.watch(this.$store.getters.getSrcAllProps, n => {
      if(n && n.length !== 0){
        //this.deleteOldPrefixes();
        this.$store.commit('changeSourceProps', []);
      }
    });

    this.$store.watch(this.$store.getters.getTgtAllProps, n => {
      if(n && n.length !== 0){
        //this.deleteOldPrefixes();
        this.$store.commit('changeTargetProps', []);
      }
    });

  },
  methods:{
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
      if(!this.prefixes.some(i => i.label === prefix.label)){
        this.prefixes.push(prefix);
      }
      this.allPrefixes.push(prefix);
    },
    deleteOldPrefixes(){
      if(this.exPrefixes.length){// && !isArraysEqual){
        //console.log(this.exPrefixes,this.prefixes);
        this.exPrefixes.forEach(pref => {
          this.prefixes.forEach(pr => {
            if(pref.label === pr.label){
              this.deletePrefix(pr);
            }
          })
        }) 
      }
      //console.log("before clear");
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
    manualMetricClicked(index){
      if(index === 0){
        this.mlalgorithm.enabled = false;
      } else {
        this.mlalgorithm.enabled = true;
      }
      // this.source.properties.splice(0);
      // this.target.properties.splice(0);
      // this.source.optionalProperties.splice(0);
      // this.target.optionalProperties.splice(0);
      this.deleteOldPrefixes();
    },
  }
}
</script>
