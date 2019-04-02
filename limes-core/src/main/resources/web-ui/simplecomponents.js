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
      prefixes: [],
      customPrefixes: {},
      messageAboutClasses: "",
      messageAboutProps: "",
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

        fetch('http://prefix.cc/context')
              .then(function(response) {
                return response.json();
               })
              .then((content) => {
                this.prefixes = content["@context"];
              })
              //.catch( alert );
  },
  methods: {
    onFocus() {
      this.focused = true;
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
      fetchProperties(this, this.source.endpoint, option);
      changeRestrictions(this, option);
    },
    enterEndpointClicked(){
      this.onBlur();
      this.classes.splice(0);
      this.source.propertiesForChoice.splice(0);
      this.classVar = '';
      fetchClasses(this, this.source.endpoint);
    },
    enterClassClicked(){
      this.source.propertiesForChoice.splice(0);
      fetchProperties(this, this.source.endpoint, this.classVar);
      changeRestrictions(this, this.classVar);
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
    source.messageAboutClasses = "Loading ...";
    fetch(`${window.SPARQL_ENDPOINT}${encodeURIComponent(endpoint)}?query=${encodeURIComponent('select distinct ?class where {?x a ?class}')}`, {
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
    })
    .then(function(response) {
      console.log(response);
      source.messageAboutClasses = "Status of the request: "+response.statusText;
      return response.json();
     })
    .then((content) => {
      source.messageAboutClasses = "";
      let classes = [];
      content.results.bindings.forEach(
        i => classes.push(i.class.value));
      source.classes.push(...classes);
      source.afterFilteredClasses = source.classes;
    })
    //.catch( alert );
}

function fetchProperties(context, endpoint, curClass) {
    context.messageAboutProps = "Properties haven't received yet. Loading ...";
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
      context.messageAboutProps = "Properties were received.";
      
      let classes = [];

      content.results.bindings.forEach(i => {
        let pair = getPrefix(context, i.p.value);
        classes.push(pair);
      });
      
      context.source.propertiesForChoice.push(...classes);

      let arr = classes.map(i => [i, i]);

      if(context.source.id === "sourceId"){
        sourceProperty.args0[0].options.length = 0;
        arr.forEach(i => sourceProperty.args0[0].options.push(i));
      }

      if(context.source.id === "targetId"){
        targetProperty.args0[0].options.length = 0;
        arr.forEach(i => targetProperty.args0[0].options.push(i));
      }

    })
    //.catch( alert );
}

function changeRestrictions(context, option){

  let pair = getPrefix(context, option);
  let curRest = context.source.restriction;
  let rest;
  let restArr = curRest.split(" ");
  restArr[2] = pair;
  rest = restArr.join(" ");
  if(context.source.id === "sourceId"){
    context.$emit('toggle-restr-src', rest);
  }

  if(context.source.id === "targetId"){
    context.$emit('toggle-restr-target', rest);
  }  

}

function getPrefix(context, urlValue){

    let property;
    let prefixNamespace;
    if(urlValue.split('#').length != 1) {
      let url = urlValue.split('#');
      property = url[1];
      prefixNamespace = url[0]+"#";
    } else {
      let url = urlValue.split('/');
      property = url[urlValue.split('/').length-1];
      url.pop();
      prefixNamespace = url.join('/')+"/";         
    }

    let prefix = '';
    for(let key in context.prefixes){
      if (context.prefixes[key] === prefixNamespace){
        prefix = key;
      }
    }

    if(prefix.length === 0){
      if(!context.prefixes["pref0"]){
        prefix = "pref"+ 0;
      } else {
        let lastKey = Object.keys(context.prefixes).pop();
        prefix = "pref" +  (parseInt(lastKey.split("pref")[1]) + 1);
      }
      

      context.prefixes[prefix] = prefixNamespace;
    }
    

    return prefix+":"+property;

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
      this.$emit('toggle-advanced-options', !this.advancedOptionsShow)
    }
  },
});