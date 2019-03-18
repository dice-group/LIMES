// Define a new component for prefixes
Vue.component('prefixes-list', {
  template: '#prefixComponent',
  props: ['prefixes', 'deleteChip', 'addPrefix', 'filteredOptions', 'context'],
  data() {
    return {
      label: '',
      namespace: '',
      focused: false,
      
      optionsShown: false,
      afterFilteredOptions: this.filteredOptions,
    };
  },
  computed: {

  },
  methods: {
    add() {
      this.addPrefix({
        label: this.label,
        namespace: this.namespace,
      });
      // cleanup
      this.namespace = '';
      this.label = '';
    },
    onFocus() {
      this.focused = true;
      console.log("focused");
      this.optionsShown = true;
        

    },
    onBlur() {
      this.focused = false;
      this.optionsShown = false;
    },
    selectOption(option){
      this.label = option;
      console.log(this.context[option]);
      this.namespace=this.context[option];
    }
  },
  watch: {
      label: function() {
        console.log("in watch "+ this.label);
         this.afterFilteredOptions = this.filteredOptions.filter(i => {
          return i.toLowerCase().includes(this.label.toLowerCase())
        })
        console.log(this.afterFilteredOptions);
      }
  }
});

