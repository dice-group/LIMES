// Define a new component for prefixes
Vue.component('mlalgorithm-component', {
  template: '#mlalgorithmComponent',
  props: ['data'],
  data() {
    return {
      name: '',
      value: '',
      items: [],
      isNumber: false,
      min: 0,
      max: Number.MAX_VALUE,
      numStep: 1,
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
      console.log(this.name);
      this.data.parameters.push({
        name: this.name,
        value: this.value,
      });
      // cleanup
      this.name = '';
      this.value = '';
    },
    selectName(name){
      this.items = Object.keys(MLParameters[name.split(" ")[0]]);
    },
    selectParameter(param){
      let MLParams = MLParameters[this.data.name.split(" ")[0]];
      if(MLParams[param].default &&  typeof MLParams[param].default === 'number'){
        this.isNumber = true;
        this.value = MLParams[param].default;
        // if(MLParams[param].min !== 'undefined'){
        //   this.min = MLParams[param].min;
        //   this.max = MLParams[param].max;
        //   this.numStep = 0.1;
        // } 
      } else {
        this.isNumber = false;
        //not a number
      }
    },
  },
});
