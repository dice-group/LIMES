// Define a new component for prefixes
Vue.component('mlalgorithm-component', {
  template: '#mlalgorithmComponent',
  props: ['data'],
  data() {
    return {
      name: '',
      value: '',
      items: [],
      measures: [],
      isNumber: false,
      isCheck: false,
      isMultCheck: false,
      min: 0,
      max: Number.MAX_VALUE,
      step: 1,
      correctValue: true,
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
          this.measures = MLParams[param].default.split(",");
        }
      }
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
