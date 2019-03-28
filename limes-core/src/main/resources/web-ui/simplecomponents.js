// Define a new component for data sources
Vue.component('datasource-component', {
  template: '#datasourceComponent',
  props: ['title', 'source', 'advancedOptionsShow'],
  data() {
    return {
      focused: false,
      optionsShown: false,
      focusedClass: false,
      classesShown: false,
      classes: [],
      endpoints: [],
      classVar: '',
      //propertiesForChoice: ["a","b","c"],
      afterFilteredOptions: [],
      afterFilteredClasses: [],
      
    };
  },
  beforeMount() {
        fetch('./lod-data.json')
            .then(function(response) {
              return response.json();
             })
            .then((content) => {
              let obj = {};
              for (let prop in content) {
                if(content[prop].sparql.length){
                  for(let i=0; i< content[prop].sparql.length; i++){
                    if(content[prop].sparql[i].status == "OK"){
                      obj[content[prop].sparql[i].access_url] = true;
                    }
                  }
                }
              }
              this.endpoints.push(...Object.keys(obj));
              this.afterFilteredOptions = this.endpoints;
            })
            //.catch( alert );
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
      //console.log(this.classes);
    },
    onClassBlur() {
      this.focusedClass = false;
      this.classesShown = false;
    },
    selectOption(option){
      this.source.endpoint = option;
      this.classes.splice(0);
      this.source.propertiesForChoice.splice(0);
      this.classVar = '';
      fetchClasses(this, option);
    },
    selectClass(option){
      this.classVar = option;
      this.source.propertiesForChoice.splice(0);
      fetchProperties(this.source, this.source.endpoint, option);
    }
  },
  watch: {
      'source.endpoint': function() {
         this.afterFilteredOptions = this.endpoints.filter(i => {
          return i.toLowerCase().includes(this.source.endpoint.toLowerCase())
        })
      },
      'classVar': function() {
         this.afterFilteredClasses = this.classes.filter(i => {
          return i.toLowerCase().includes(this.classVar.toLowerCase())
        })
      }
  }
});

function fetchClasses(source, endpoint) {
    fetch(`${window.SPARQL_ENDPOINT}${encodeURIComponent(endpoint)}?query=${encodeURIComponent('select distinct ?class where {?x a ?class}')}`, {
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
    })
    .then(function(response) {
      return response.json();
     })
    .then((content) => {
      //console.log(content.results.bindings);
      let classes = [];
      content.results.bindings.forEach(
        i => classes.push(i.class.value));
      source.classes.push(...classes);
      source.afterFilteredClasses = source.classes;
    })
    //.catch( alert );
}

function fetchProperties(source, endpoint, curClass) {
    let query = encodeURIComponent('select distinct ?p where { ?s a <'+curClass+'>. ?s ?p ?o}');
    fetch(`${window.SPARQL_ENDPOINT}${encodeURIComponent(endpoint)}?query=${query}`, {
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
    })
    .then(function(response) {
      return response.json();
     })
    .then((content) => {
      console.log("hello");
      //console.log(content.results.bindings);
      
      let classes = [];
      content.results.bindings.forEach(i => {
        i.p.value.split('#').length != 1 ? 
        classes.push(i.p.value.split('#')[1]): 
        classes.push(i.p.value.split('/')[i.p.value.split('/').length-1])
      });
      source.propertiesForChoice.push(...classes);

      let arr = classes.map(i => [i, i[0]]);

      if(source.id === "sourceId"){
        sourceProperty.args0[0].options.length = 0;
        arr.forEach(i => sourceProperty.args0[0].options.push(i));
      }

      if(source.id === "targetId"){
        targetProperty.args0[0].options.length = 0;
        arr.forEach(i => targetProperty.args0[0].options.push(i));
      }

    })
    //.catch( alert );
}



// Define a new component for canvas
Vue.component('datacanvas-component', {
  template: '#datacanvasComponent',
  props: ['title', 'source'],
  data() {
    return {
      focused: false,
      optionsShown: false,
      focusedClass: false,
      classesShown: false,
      classes: [],
      endpoints: [],
      classVar: '',
      propertiesForChoice: this.source.propertiesForChoice,
      afterFilteredOptions: [],
      afterFilteredClasses: [],
    };
  },
  beforeMount() {
    window.onload = function() {
          var demoWorkspace = Blockly.inject('blocklyDiv',
            {media: './blockly-1.20190215.0/media/',
             toolbox: document.getElementById('toolbox')});
          Blockly.Blocks['sourceproperty'] = {
            init: function() {
              this.jsonInit(sourceProperty);              
            }
          };
          Blockly.Blocks['targetproperty'] = {
            init: function() {
              this.jsonInit(targetProperty);
            }
          };
          Blockly.Blocks['renamepreprocessingfunction'] = {
            init: function() {
              this.jsonInit(RenamePreprocessingFunction);
            }
          };
          Blockly.Blocks['lowercasepreprocessingfunction'] = {
            init: function() {
              this.jsonInit(LowercasePreprocessingFunction);
            }
          };
          Blockly.Blocks['measure'] = {
            init: function() {
              this.jsonInit(Measure);
            }
          };
          Blockly.Blocks['operator'] = {
            init: function() {
              this.jsonInit(Operator);
            }
          };
    };
  },
  methods: {

  },
});

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



// Define a new component for advancedOptions
Vue.component('advancedoptions-component', {
  template: '#advancedOptions',
  props: ['advancedOptionsShow'],
  methods: {
    switchCheck(){
      console.log(this.$parent);
      //this.$parent.advancedOptionsShow = !this.advancedOptionsShow;
      this.$emit('toggle-advanced-options', !this.advancedOptionsShow)
    }
  },
});