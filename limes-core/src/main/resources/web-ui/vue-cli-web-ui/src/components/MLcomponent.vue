<template>
    <md-whiteframe md-tag="section" style="flex: 1; padding-top: 15px; padding-left: 15px; padding-right: 15px;">
      <md-layout>
        <md-layout md-flex="75" style="align-items: center;">
          <div class="md-title" style="height: 26px;">
            Machine Learning
          </div>
        </md-layout>
      </md-layout>

      <md-input-container v-if="data.enabled">
        <label>Name</label>
        <!-- <md-input v-model="data.name" placeholder="Name"></md-input> -->
          <md-select v-model="data.name" @selected="selectName(data.name)">
            <md-option value="WOMBAT Simple">WOMBAT Simple</md-option>
            <md-option value="WOMBAT Complete">WOMBAT Complete</md-option>
            <md-option value="EAGLE">EAGLE</md-option>
          </md-select> 

      </md-input-container>
      <md-input-container v-if="data.enabled">
        <label>Type</label>
        <!-- <md-input v-model="data.type" placeholder="Type"></md-input> -->
          <md-select v-model="data.type" >
            <md-option value="supervised batch">supervised batch</md-option>
            <md-option value="supervised active">supervised active</md-option>
            <md-option value="unsupervised">unsupervised</md-option>
          </md-select> 

      </md-input-container>
      <md-input-container v-if="data.enabled && (data.type==='supervised batch' || data.type==='supervised active')">
        <label>Training</label>
        <!-- <md-input v-model="data.training" placeholder="Training"></md-input> -->
        <md-file v-model="data.training"></md-file>
      </md-input-container>


        <md-layout>
          <div style="padding-top: 20px;" v-if="!this.$store.state.source.properties.length">No properties defined..</div>
          <md-chip v-for="pr in this.$store.state.source.properties" md-deletable v-on:delete="deleteChip(pr)" style="margin-left: 3px">
            {{ pr }}
          </md-chip>
        </md-layout>


          <md-layout md-flex="33">
           <md-input-container>
            <label>Source property</label>
            <md-select v-model="sourcePropName" @selected="selectSProperty(sourcePropName)">
              <md-option
                v-for="option in this.$store.state.source.allProperties" :value="option">
                  {{ option }}
              </md-option>
            </md-select>  
          </md-input-container>
          </md-layout>

        <md-layout>
          <div style="padding-top: 20px;" v-if="!this.$store.state.target.properties.length">No properties defined..</div>
          <md-chip v-for="pr in this.$store.state.target.properties" md-deletable v-on:delete="deleteChip(pr)" style="margin-left: 3px">
            {{ pr }}
          </md-chip>
        </md-layout>

          <md-layout md-flex="33">
           <md-input-container>
            <label>Target property</label>
            <md-select v-model="targetPropName" @selected="selectTProperty(targetPropName)">
              <md-option
                v-for="option in this.$store.state.target.allProperties" :value="option">
                  {{ option }}
              </md-option>
            </md-select>  
          </md-input-container>
          </md-layout>
   



      <md-layout v-if="data.enabled">
        <div style="padding-top: 20px;" v-if="!data.parameters.length">No parameters defined..</div>
        <md-chip v-for="param in data.parameters" md-deletable v-on:delete="deleteParam(param)" style="margin-left: 3px">
          {{ param.name }}: {{ param.value }}
        </md-chip>
      </md-layout>
      <md-layout md-gutter="8" v-if="data.enabled">
        <md-layout md-flex="33">
          <md-input-container>
            <label>Parameter name</label>
            <!-- <md-input v-model="name" placeholder="Name"></md-input> -->
            <md-select v-model="name" @selected="selectParameter(name)">
              <md-option
                v-for="option in items" :value="option">
                  {{ option }}
              </md-option>
            </md-select>  

          </md-input-container>
        </md-layout>
        <md-layout md-flex>
          <md-input-container v-if="isNumber">
            <label>Value</label>
            <!-- <md-input v-model="value" placeholder="Value"></md-input> -->
            <md-input v-model="value" type="number" v-bind:min="min" v-bind:max="max" v-bind:step="step" @blur="isBlur"></md-input>
          </md-input-container>

          <md-input-container v-if="isCheck">
            <md-checkbox v-model="value"></md-checkbox>
          </md-input-container>

          <md-input-container v-if="isMultCheck">
          <label>Simple multiselect</label>
          <md-select multiple v-model="value">
            <md-option
              v-for="val in measuresList" :value="val">
                {{ val }}
            </md-option>
          </md-select>
        </md-input-container>

        </md-layout>
        <md-layout md-flex="25">
          <md-button v-on:click="add()">Add</md-button>
        </md-layout>
      </md-layout>

    </md-whiteframe>
</template>

<script>
export default {
	components: {
		
	},
  props: {
      data: {
        type: Object,
      },
  },
  beforeMount(){
    let params = Object.keys(MLParameters.WOMBAT).map(function(i) {
      return {
        name: i,
        value: MLParameters.WOMBAT[i].default || MLParameters.WOMBAT[i],
      } 
    });
    this.data.parameters.splice(0);
    this.data.parameters = params;
  },
  data () {
    return {
      name: '',
      value: '',
      items: [],
      measuresList: [],
      isNumber: false,
      isCheck: false,
      isMultCheck: false,
      min: 0,
      max: Number.MAX_VALUE,
      step: 1,
      correctValue: true,
      sourcePropName: null,
      targetPropName: null,
    }
  },
	methods: {
    enable() {
      this.data.enabled = true;
    },
    disable() {
      this.data.enabled = false;
    },
    deleteParam(param) {
      this.data.parameters = this.data.parameters.filter(p => p.name !== param.name && p.value !== param.value);
    },
    deleteChip(pr) {
      this.$store.state.source.properties = this.$store.state.source.properties.filter(p => p !== pr);
    },
    add() {
      // push new prefix
      if(this.correctValue){
        if(this.value === '' && this.isCheck){
          this.value = false;
        }
        if(this.isMultCheck){
          let str = this.value.join(",");
          this.value = str;
        }

        let currentParams = this.data.parameters.filter(p => p.name === this.name);
        if(currentParams.length){ //delete first similar parameter
          this.deleteParam(currentParams[0]);
        }

        this.data.parameters.push({
          name: this.name,
          value: this.value,
        });
        // cleanup
        this.name = '';
        this.value = '';
        this.cleanup();
      }
    },
    selectName(name){
      this.items = Object.keys(MLParameters[name.split(" ")[0]]);
    },
    selectParameter(param){
      this.cleanup();
      let MLParams = MLParameters[this.data.name.split(" ")[0]];
      if(MLParams[param].default &&  typeof MLParams[param].default === 'number'){
        this.isNumber = true;
        if(typeof MLParams[param].min !== 'undefined'){
          this.min = MLParams[param].min;
          this.max = MLParams[param].max;
          this.step = 0.1;
        } 
        this.value = MLParams[param].default;

      } else {
        //not a number
        if(typeof MLParams[param].default === 'undefined'){
          this.isCheck = true;
        } else {
          this.isMultCheck = true;
          this.measuresList = this.measures;//MLParams[param].default.split(",");
        }
      }
    },
    selectSProperty(pr){
      if(!this.$store.state.source.properties.some(i => i === pr)){
        this.$store.state.source.properties.push(pr);
      }
      setTimeout(() => this.sourcePropName = null, 1);
    },
    selectTProperty(pr){
      if(!this.$store.state.target.properties.some(i => i === pr)){
        this.$store.state.target.properties.push(pr);
      }
      //setTimeout(() => this.targetPropName = null, 1);
      
      this.$nextTick(function () {
        this.targetPropName = null;
      })
    },
    isBlur(){
        let curValue = this.value;
        if(curValue < this.min || curValue > this.max){
          //this.value = '';
          this.correctValue = false;
        } else {
          this.correctValue = true;
        }
    },
    cleanup(){
      this.isCheck = false;
      this.isNumber = false;
      this.isMultCheck = false;
      this.min = 0;
      this.max = Number.MAX_VALUE;
      this.step = 1;
    },
	}
}
</script>
