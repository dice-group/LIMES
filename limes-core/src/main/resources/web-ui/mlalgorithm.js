// Define a new component for prefixes
Vue.component('mlalgorithm-component', {
  template: '#mlalgorithmComponent',
  props: ['data'],
  data() {
    return {
      name: '',
      value: '',
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
      this.data.parameters.push({
        name: this.name,
        value: this.value,
      });
      // cleanup
      this.name = '';
      this.value = '';
    },
  },
});
