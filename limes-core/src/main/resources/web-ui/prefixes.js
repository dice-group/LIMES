// Define a new component for prefixes
Vue.component('prefixes-list', {
  template: '#prefixComponent',
  props: ['prefixes'],
  data() {
    return {
      label: '',
      namespace: '',
    };
  },
  methods: {
    deleteChip(prefix) {
      this.prefixes = this.prefixes.filter(p => p.label !== prefix.label && p.namespace !== prefix.namespace);
    },
    add() {
      // push new prefix
      this.prefixes.push({
        label: this.label,
        namespace: this.namespace,
      });
      // cleanup
      this.namespace = '';
      this.label = '';
    },
  },
});
