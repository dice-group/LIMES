// Define a new component for data sources
Vue.component('datasource-component', {
  template: '#datasourceComponent',
  props: ['title', 'source'],
  data() {
    return {
      focused: false,
      optionsShown: false,
      focusedClass: false,
      classesShown: false,
      afterFilteredOptions: this.source.endpoints,
      afterFilteredClasses: this.source.classes,
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
    onClassFocus() {
      this.focusedClass = true;
      this.classesShown = true;
      console.log(this.source.classes);
    },
    onClassBlur() {
      this.focusedClass = false;
      this.classesShown = false;
    },
    selectOption(option){
      this.source.endpoint = option;
      this.source.classes.splice(0);
      this.source.propertiesForChoice.splice(0);
      this.source.class = '';
      fetchClasses(this.source, option);
    },
    selectClass(option){
      this.source.class = option;
      this.source.propertiesForChoice.splice(0);
      fetchProperties(this.source, this.source.endpoint, option);
    }
  },
  watch: {
      'source.endpoint': function() {
         this.afterFilteredOptions = this.source.endpoints.filter(i => {
          return i.toLowerCase().includes(this.source.endpoint.toLowerCase())
        })
      },
      'source.class': function() {
         this.afterFilteredClasses = this.source.classes.filter(i => {
          return i.toLowerCase().includes(this.source.class.toLowerCase())
        })
      }
  }
});

function fetchClasses(source, endpoint) {
    fetch(`/sparql/${encodeURIComponent(endpoint)}?query=${encodeURIComponent('select distinct ?class where {?x a ?class} LIMIT 100')}`, {
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
    })
    .then(function(response) {
      return response.json();
     })
    .then((content) => {
      console.log(content.results.bindings);
      let classes = [];
      content.results.bindings.forEach((i, index) => Vue.set(source.classes, index, i.class.value));
      //i => classes.push(i.class.value));
      //source.classes.push(...classes);
    })
    //.catch( alert );
}

function fetchProperties(source, endpoint, curClass) {
    let query = encodeURIComponent('select distinct ?p where {<'+curClass+'> ?p ?o}');
    fetch(`/sparql/${encodeURIComponent(endpoint)}?query=${query}`, {
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
    })
    .then(function(response) {
      return response.json();
     })
    .then((content) => {
      console.log(content.results.bindings);
      
      let classes = [];
      content.results.bindings.forEach(i => {
        i.p.value.split('#').length != 1 ? 
        classes.push(i.p.value.split('#')[1]): 
        classes.push(i.p.value.split('/')[i.p.value.split('/').length-1])
      });
      source.propertiesForChoice.push(...classes);
    })
    //.catch( alert );
}

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
