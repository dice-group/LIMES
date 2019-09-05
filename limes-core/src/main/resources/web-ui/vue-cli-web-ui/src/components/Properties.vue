<template>
</template>

<script>
export default {
	components: {
		
	},
  data () {
    return {
    	currentBlock: null,
    	emptyRemoved: false,
    	blockForCheckNextLevel: null,
    }
  },
  methods: {

  		changePropertyBlockWithEmptyTail(block){
        	if(block.getChildren().length === 0 && this.emptyRemoved === false){
        	  let parentColour = block.getColour();
	          var newBlock = this.$store.state.Workspace.newBlock('emptyBlock');
	          newBlock.setColour(parentColour);
	          newBlock.setMovable(false);
			  newBlock.initSvg();
          	  newBlock.render();

          	  var parentConnection = block.getInput('propName').connection;
			  var childConnection = newBlock.outputConnection;
			  parentConnection.connect(childConnection);
			}
  		},
		addProperies(i,strForXml){			
			//for some time if added redundant emptyBlock
			// if(i.type === 'emptyBlock'){
			// 	i.dispose();
			// }
			this.currentBlock = i;	

	      if(!this.$store.state.notConnectedToStart){
	        if(i.type.indexOf("source") !== -1){
	          this.changePropertyBlockWithEmptyTail(i);
	          if(i.type.indexOf("optional") !== -1){
	            this.processProperties("os", this.$store.state.source, this.$store.state.source.optionalProperties, strForXml);
	          } else {
	            this.processProperties("s", this.$store.state.source, this.$store.state.source.properties, strForXml);
	          }   
	        } else {
		        if(i.type.indexOf("target") !== -1){
		          this.changePropertyBlockWithEmptyTail(i);
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
	      if(strForXml && strForXml.length !== 0){
	      	
	      	let curBlock = this.currentBlock;
	      	let lastBlock = this.currentBlock;
	      	
	      	let num =0;
	      	do{		
				//check next level
				if(curBlock.getField("enable_propertypath").getValue().toLowerCase() === 'true' 
					&& curBlock.getChildren()[0].type !== "propertyPath"){
						this.blockForCheckNextLevel = curBlock;
						this.checkNextLevelProperties(srcOrTgt, strForXml);

				}

				if(curBlock.getChildren().length && curBlock.getChildren()[0].type !== "emptyBlock"){
					curBlock = curBlock.getChildren()[0];
				}
				
				lastBlock = lastBlock.getChildren().length && lastBlock.getChildren()[0];
				
			} while (lastBlock.getField("enable_propertypath") !== null);
	
	      	
	      }	

	      
	      if(strForXml && strForXml.length !== 0){//&& !allProps.some(i => i === strForXml)){
		      if(kindOfProperty === "s"){
		      	if(!this.$store.state.source.properties.some(i => i === strForXml)){
		      		this.$store.commit('addSrcProps', strForXml);
		      	}
		      }
		      if(kindOfProperty === "t"){
		      	if(!this.$store.state.target.properties.some(i => i === strForXml)){
		      		this.$store.commit('addTgtProps', strForXml);
		      	}
		      }
		      if(kindOfProperty === "os"){
		      	if(!this.$store.state.source.optionalProperties.some(i => i === strForXml)){
		      		this.$store.commit('addOSrcProps', strForXml);
		      	}
		      }
		      if(kindOfProperty === "ot"){
		      	if(!this.$store.state.target.optionalProperties.some(i => i === strForXml)){
		      		this.$store.commit('addOTgtProps', strForXml);
		      	}
		      }
		  }
	    },

	    checkNextLevelProperties(srcOrTgt, property){
	      if(property && property !== null && property.length !== 0){
			if(property.indexOf("/") !== -1){
				let endpoint = srcOrTgt.endpoint;
				let arr = property.split("/");
				let urls = arr.map(p => {
					let label = p.split(":")[0];
					let propertyName = p.split(":")[1];
					return this.$store.state.context[label]+propertyName;
				});
				this.fetchForNextLevelProperties(urls, endpoint, this.$store.state.checkboxEndpointAsFile);
			} else { 
				let label = property.split(":")[0];
				let endpoint = srcOrTgt.endpoint;

				let propertyName = property.split(":")[1].split(" ")[0];
				let urls = [];
				urls.push(this.$store.state.context[label]+propertyName);
				this.fetchForNextLevelProperties(urls ,endpoint, this.$store.state.checkboxEndpointAsFile);
			}
	      }
	    },

	    fetchForNextLevelProperties(p,endpoint,endpointFromFile){
		  let keyWord = 'ASK';	
		  let str = '{';
		  let lastIndex = '';
		  p.forEach((url,index) => {
			  if(index === 0){
				  str += '?s'+' <'+url+'>' + ' ?v'+index+'. ';
			  } else {
				  str += '?v'+(index-1)+' <'+url+'>' + ' ?v'+index+'. ';
			  }
			  lastIndex = index;
		  });
	      let query = encodeURIComponent(keyWord+str+'FILTER isIRI(?v'+lastIndex+')}');
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
	        if(content.boolean){
	          this.getForNextLevelProperties(str, lastIndex, endpoint,endpointFromFile);
	        } 
	        else {
				this.blockForCheckNextLevel.getField("enable_propertypath").setValue(false);
				alert("There are no more internal properties!")
			}
	        console.log(content.boolean);

	      })
	    },

	    getForNextLevelProperties(p, lastIndex, endpoint,endpointFromFile){
		      //let block = this.block;
		      let query = encodeURIComponent('SELECT distinct ?p WHERE ' + p +' FILTER isIRI(?v'+lastIndex+')'+' ?v'+lastIndex+' ?p ?p2}');
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

		        let classes = [];

		        content.results.bindings.forEach(i => {
		          let pair = this.getPrefix(i.p.value).pair;
		          classes.push(pair);
		        });
		        let arr = classes.map(i => [i, i]);

		        var dropdown = new Blockly.FieldDropdown(arr);
		        var checkbox = new Blockly.FieldCheckbox(false);
		      	let parentColour = this.blockForCheckNextLevel.getColour();//this.currentBlock.getColour();
		        var newBlock = this.$store.state.Workspace.newBlock('propertyPath');
		        newBlock.getInput("propertyPath").removeField('propTitle');
		        newBlock.getInput("propertyPath").removeField('enable_propertypath');
		        newBlock.getInput("propertyPath").appendField(dropdown, 'propTitle');
		        newBlock.getInput("propertyPath").appendField(checkbox, 'enable_propertypath');
		        newBlock.setMovable(false);
		        newBlock.setColour(parentColour);
				newBlock.initSvg();
		      	newBlock.render();

		      	var parentConnection = this.blockForCheckNextLevel.getInput('propName')? this.blockForCheckNextLevel.getInput('propName').connection :  this.blockForCheckNextLevel.getInput('propertyPath').connection;
				var childConnection = newBlock.outputConnection;
				parentConnection.connect(childConnection);

		      })
		},
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
	        for(let key in this.$store.state.context){
	          if (this.$store.state.context[key] === prefixNamespace){
	            prefix = key;
	          }
	        }

	        if(prefix.length === 0){
	          if(!this.$store.state.context["pref0"]){
	            prefix = "pref"+ 0;
	          } else {
	            let lastKey = Object.keys(this.$store.state.context).pop();
	            prefix = "pref" +  (parseInt(lastKey.split("pref")[1]) + 1);
	          }
	          

	          this.$store.state.context[prefix] = prefixNamespace;
	        }

	        return {pair: prefix+":"+property, namespace: prefixNamespace};

	    },
	}
}
</script>
