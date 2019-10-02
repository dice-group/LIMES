<template>
  <md-whiteframe md-tag="section" style="flex: 1; padding-top: 15px; padding-left: 15px; padding-right: 15px;">
    <div class="md-title">{{ title }}</div>
    <md-layout md-column>
      <Endpoint      
      v-bind:source="source"
      v-bind:exampleConfigEnable="exampleConfigEnable"
      v-bind:advancedOptionsShow="advancedOptionsShow"
      v-bind:classVar="classVarFromExample"
      v-on:toggle-classVar="classVarFromExample = $event"
      v-bind:endpointandclasses="endpointandclasses"
      v-on:repeat-source-classes="afterFilteredClasses = $event"
      v-on:repeat-classes="classes = $event"
      v-bind:fetchProperties="fetchProperties"
      v-bind:fetchClasses="fetchClasses"
      ></Endpoint>
      <Restriction
      v-bind:source="source"
      v-bind:advancedOptionsShow="advancedOptionsShow"
      v-bind:messageAboutClasses="messageAboutClasses"
      v-bind:messageAboutProps="messageAboutProps"
      v-bind:fetchProperties="fetchProperties"
      v-bind:getPrefix="getPrefix"
      v-bind:classes="classes"
      v-bind:prefixes="prefixes"
      v-bind:afterFilteredClasses="afterFilteredClasses"
      v-bind:classVarFromExample="classVarFromExample"
      v-on:toggle-afterFilteredClasses="afterFilteredClasses = $event"
      v-on:toggle-prefix-from-rest="addPrefix($event)"
      v-on:toggle-del-exprefix="deletePrefix($event)"
      ></Restriction>
    </md-layout>
  </md-whiteframe>
</template>

<script>
import Endpoint from './Endpoint.vue'
import Restriction from './Restriction.vue'
export default {
  components: {
    Endpoint, Restriction
  },
  props: {
    title: {
      type: String,
      required: true
    },
    source: {
      type: Object,
    },
    advancedOptionsShow: {
      type: Boolean,
    },
    endpointandclasses: {
      type: Object,
    },
    prefixes: {
      type: Object,
    },
    deletePrefix: {
      type: Function,
    },
    addPrefix: {
      type: Function,
    },
    exampleConfigEnable: {
      type: Boolean,
    },
  },
  data(){
    return {
      classVarFromExample: "",
      afterFilteredClasses: [],
      messageAboutClasses: "",
      messageAboutProps: "",
      classes: [],
    }
  },
  mounted(){
	this.$store.watch(this.$store.getters.checkboxEndpointS, n => {
      if(!n){
		this.classes.splice(0);
		this.afterFilteredClasses.splice(0);
      }
    });  
  this.$store.watch(this.$store.getters.checkboxEndpointT, n => {
      if(!n){
    this.classes.splice(0);
    this.afterFilteredClasses.splice(0);
      }
    }); 
  },
  methods:{
    getPrefix(urlValue){
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
        for(let key in this.prefixes){
          if (this.prefixes[key] === prefixNamespace){
            prefix = key;
          }
        }

        if(prefix.length === 0){
          if(!this.prefixes["pref0"]){
            prefix = "pref"+ 0;
          } else {
            let lastKey = Object.keys(this.prefixes).pop();
            prefix = "pref" +  (parseInt(lastKey.split("pref")[1]) + 1);
          }
          

          this.prefixes[prefix] = prefixNamespace;
        }

        return {pair: prefix+":"+property, namespace: prefixNamespace};

    },
    fetchClasses(endpoint, endpointFromFile) {
      this.classes.splice(0);
      this.messageAboutClasses = "Loading ...";
      this.messageAboutProps  = "";
      let url = `${window.SPARQL_ENDPOINT}${encodeURIComponent(endpoint)}?query=${encodeURIComponent('select distinct ?class where {?x a ?class}')}`;
      if(endpointFromFile){
		  url = `${window.LIMES_SERVER_URL}/uploads/${endpoint}/sparql?query=${encodeURIComponent('select distinct ?class where {?x a ?class}')}`;
	  }
      fetch(url, {
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
      })
      .then((response) => {
        this.messageAboutClasses = "Status of the request: "+response.statusText;
        return response.json();
       })
      .then((content) => {
        this.messageAboutClasses = "";
        let classes = [];
        content.results.bindings.forEach(
          i => classes.push(i.class.value));
        this.classes.push(...classes);

        if(this.source.id === "sourceId"){
          this.$store.commit('changeSourceClasses', classes);
        } else {
          this.$store.commit('changeTargetClasses', classes);
        }
        this.afterFilteredClasses = this.source.classes;
      })
    },
    fetchProperties(endpoint, curClass, endpointFromFile) {
      this.messageAboutProps = "Properties haven't received yet. Loading ...";
      let query = encodeURIComponent('select distinct ?p where { ?s a <'+curClass+'>. ?s ?p ?o}');
      let url = `${window.SPARQL_ENDPOINT}${encodeURIComponent(endpoint)}?query=${query}`;
      if(endpointFromFile){
		  url = `${window.LIMES_SERVER_URL}/uploads/${endpoint}/sparql?query=${query}`;
	  }
      fetch(url, {
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
      })
      .then(function(response) {
        return response.json();
       })
      .then((content) => {
        this.messageAboutProps = "Properties were received.";
        
        let classes = [];

        content.results.bindings.forEach(i => {
          let pair = this.getPrefix(i.p.value).pair;
          classes.push(pair);
        });
        
        //this.source.propertiesForChoice.push(...classes);

        let arr = classes.map(i => [i, i]);


        if(this.source.id === "sourceId"){
          sourceProperty.args0[0].options.length = 0;
          optionalSourceProperty.args0[0].options.length = 0;
          arr.forEach(i => {
            sourceProperty.args0[0].options.push(i);
            optionalSourceProperty.args0[0].options.push(i);
          });
        }

        if(this.source.id === "targetId"){
          targetProperty.args0[0].options.length = 0;
          optionalTargetProperty.args0[0].options.length = 0;
          arr.forEach(i => {
            targetProperty.args0[0].options.push(i);
            optionalTargetProperty.args0[0].options.push(i);
          });
        }
        //this.$emit('toggle-allProps', classes);
        if(this.source.id === "sourceId"){
          this.$store.commit('changeSourceAllProps', classes);
        } else {
          this.$store.commit('changeTargetAllProps', classes);
        }

      })
    },
  },

}
</script>
