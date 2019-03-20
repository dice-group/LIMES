// Define a new component for data sources
Vue.component('datasource-component', {
  template: '#datasourceComponent',
  props: ['title', 'source'],
  data() {
    return {
      focused: false,
      optionsShown: false,
      afterFilteredOptions: this.source.endpoints,
    };
  },
  methods: {
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
      this.source.endpoint = option;
      // /sparql?default-graph-uri=&query=select+distinct+%3Fclass+where+{[]+a+%3Fclass}+LIMIT+100&format=text%2Fhtml&timeout=0&debug=on
      // fetchClasses();
    }
  },
  watch: {
      'source.endpoint': function() {
         this.afterFilteredOptions = this.source.endpoints.filter(i => {
          return i.toLowerCase().includes(this.source.endpoint.toLowerCase())
        })
      }
  }
});

// function fetchClasses(endpoint) {
//     fetch(endpoint + '/sparql?default-graph-uri=http://dbpedia.org&query=select+distinct+?class+where+{?x+a+?class}+LIMIT+100&format=application/sparql-results+json&CXML_redir_for_subjs=121&CXML_redir_for_hrefs=&timeout=30000&debug=on&run=+Run+Query+')
//           .then(function(response) {
//             return response.json();
//            })
//           .then((content) => {
//             console.log(content);
//           })
//             //.catch( alert );
// }

// Define a new component for metric
Vue.component('metrics-component', {
  template: '#metricsComponent',
  props: ['metrics', 'selectedMeasureOption', 'measureOptions', 'selectedOperatorOption', 'operatorOptions'],
});


// Define a new component for metric
Vue.component('accreview-component', {
  template: '#accreviewComponent',
  props: ['data', 'title'],
});

// Define a new component for execution
Vue.component('execution-component', {
  template: '#executionComponent',
  props: ['execution'],
});

// Define a new component for output
Vue.component('output-component', {
  template: '#outputComponent',
  props: ['output'],
});
