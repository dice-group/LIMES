// Define a new component for prefixes
Vue.component('prefixes-list', {
  template: '#prefixComponent',
  props: ['prefixes', 'deleteChip', 'addPrefix'],
  data() {
    return {
      label: '',
      namespace: '',
    };
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
  },
});
