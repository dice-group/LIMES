class Properties {
	constructor(context, block) {
		this.context = context;
		this.block = block;
	}

    addProperies(i,strForXml){
      if(!this.context.notConnectedToStart){
        if(i.type.indexOf("source") !== -1){
          if(i.type.indexOf("optional") !== -1){
            this.processProperties(this.context.source, this.context.source.optionalProperties, strForXml);
          } else {
            this.processProperties(this.context.source, this.context.source.properties, strForXml);
          }   
        } else {
          if(i.type.indexOf("optional") !== -1){
            this.processProperties(this.context.source, this.context.target.optionalProperties, strForXml);
          } else {
            this.processProperties(this.context.target, this.context.target.properties, strForXml);
          }
        }
      }
    }

    processProperties(srcOrTgt, allProps, strForXml){
      this.checkNextLevelProperties(srcOrTgt, strForXml);
      allProps.splice(0);
      allProps.push(strForXml);
    } 

    checkNextLevelProperties(srcOrTgt, property){
      if(property && property !== null && property.length !== 0){
        let label = property.split(":")[0];
        let endpoint = srcOrTgt.endpoint;
        //if the property is same as before, we don't need to do that
        let notSamePropertyIsChosen = srcOrTgt.properties.filter(p => p === property).length === 0;
        if(notSamePropertyIsChosen){
          let propertyName = property.split(":")[1].split(" ")[0];
          this.fetchForNextLevelProperties(this.context.context[label]+propertyName,endpoint);
        }
      }
    }

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
        console.log(content.boolean);

      })
    }

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
          let pair = getPrefix(this.context, i.p.value).pair;
          classes.push(pair);
        });
        let arr = classes.map(i => [i, i]);
        console.log(arr);

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
    }
}