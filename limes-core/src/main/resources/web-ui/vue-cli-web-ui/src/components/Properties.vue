<template>
</template>

<script>
export default {
	components: {
		
	},
  data () {
    return {

    }
  },
  methods: {
		addProperies(i,strForXml){
	      if(!this.$store.state.notConnectedToStart){
	        if(i.type.indexOf("source") !== -1){
	          if(i.type.indexOf("optional") !== -1){
	            this.processProperties("os", this.$store.state.source, this.$store.state.source.optionalProperties, strForXml);
	          } else {
	            this.processProperties("s", this.$store.state.source, this.$store.state.source.properties, strForXml);
	          }   
	        } else {
		        if(i.type.indexOf("target") !== -1){
		          if(i.type.indexOf("optional") !== -1){
		            this.processProperties("ot", this.$store.state.target, this.$store.state.target.optionalProperties, strForXml);
		          } else {
		            this.processProperties("t", this.$store.state.target, this.$store.state.target.properties, strForXml);
		          }
		      	}
	        }
	      }
	    },

	    processProperties(kindOfProperty, srcOrTgt, allProps, strForXml){
	      //this.checkNextLevelProperties(srcOrTgt, strForXml); // don't do it so far

	      //allProps.splice(0);
	      //allProps.push(strForXml);
	      
	      if(strForXml && strForXml.length !== 0 && !allProps.some(i => i === strForXml)){
		      if(kindOfProperty === "s"){
		      	this.$store.commit('addSrcProps', strForXml);
		      }
		      if(kindOfProperty === "t"){
		      	this.$store.commit('addTgtProps', strForXml);
		      }
		      if(kindOfProperty === "os"){
		      	this.$store.commit('addOSrcProps', strForXml);
		      }
		      if(kindOfProperty === "ot"){
		      	this.$store.commit('addOTgtProps', strForXml);
		      }
		  }
	    },

	    checkNextLevelProperties(srcOrTgt, property){
	      if(property && property !== null && property.length !== 0){
	        let label = property.split(":")[0];
	        let endpoint = srcOrTgt.endpoint;
	        //if the property is same as before, we don't need to do that
	        let notSamePropertyIsChosen = srcOrTgt.properties.filter(p => p === property).length === 0;
	        if(notSamePropertyIsChosen){
	          let propertyName = property.split(":")[1].split(" ")[0];
	          this.fetchForNextLevelProperties(this.$store.state.context[label]+propertyName,endpoint);
	        }
	      }
	    },

	    fetchForNextLevelProperties(p,endpoint){
	      let query = encodeURIComponent('ASK{?s <'+p+'> ?o. FILTER isIRI(?o)}');
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
	        if(content.boolean){
	          this.getForNextLevelProperties(p,endpoint);
	        }
	        //console.log(content.boolean);

	      })
	    },

	    getForNextLevelProperties(p,endpoint){
		      //let block = this.block;
		      let query = encodeURIComponent('SELECT distinct ?p WHERE { ?s <'+p+'> ?o. FILTER isIRI(?o) ?o ?p ?o2}');
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

		        let classes = [];

		        content.results.bindings.forEach(i => {
		          let pair = this.getPrefix(this.$store.state, i.p.value).pair;
		          classes.push(pair);
		        });
		        let arr = classes.map(i => [i, i]);
		        //console.log(arr);

		        //console.log(block);
		        // add new dropdown list in property, doesn't work without 
		        // ,
				    // {
				    //   "type": "input_value",
				    //   "name": "propName"
				    // }

		  //       var dropdown = new Blockly.FieldDropdown(arr);
				// this.block.getInput("propName").appendField(dropdown, 'propTitle');
		      

		      })
		},
		getPrefix(context, urlValue){

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

		    return {pair: prefix+":"+property, namespace: prefixNamespace};

		},
	}
}
</script>
