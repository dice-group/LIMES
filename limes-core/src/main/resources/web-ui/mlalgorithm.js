// Define a new component for prefixes
Vue.component('mlalgorithm-component', {
  template: '#mlalgorithmComponent',
  props: ['data','measures','source','target'],
  data() {
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
    };
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
      this.source.properties = this.source.properties.filter(p => p !== pr);
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
      if(!this.source.properties.some(i => i === pr)){
        this.source.properties.push(pr);
      }
      setTimeout(() => this.sourcePropName = null, 1);
    },
    selectTProperty(pr){
      if(!this.target.properties.some(i => i === pr)){
        this.target.properties.push(pr);
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
  },
});
