<template>
</template>

<script>
import Properties from './Properties.vue'
export default {
	mixins: [Properties],
	components: {
		Properties
	},
  data () {
    return {

    }
  },
	methods: {
	    processingPropertyWithPrepFunc(i){

	      if(i.getChildren().length && i.type.indexOf('preprocessing')!== -1)
	      
	      /*&& i.getChildren()
	      .filter(b => b.type === "propertyPath" 
	      || b.type === "sourceproperty" 
	      || b.type === "targetproperty" 
	      || b.type === "optionalsourceproperty" 
	      || b.type === "optionaltargetproperty"	      
	      || b.type === "emptyBlock").length === 0)*/
	      
	      {
	        if(!this.$store.state.notConnectedToStart){
	          i.setDisabled(false);
	        }
	        i.getChildren().forEach(child => {
	          if(!this.$store.state.notConnectedToStart){
	            child.setDisabled(false);
	          }
	          if(i.type.indexOf('preprocessing')!== -1){

	            //changeOutputOfPreprocessingFunction
	            let preprWithParam = i.toString().split(" ");
	            let isOptional = preprWithParam.find(val => val.toLowerCase() === "optional");
	            let srcOrTgt = preprWithParam.find(val => val.toLowerCase() === "source" || val.toLowerCase() === "target");
	            let stringForCheck = srcOrTgt[0].toUpperCase() + srcOrTgt.slice(1);
	            if(isOptional){
	              i.setOutput(true, isOptional+stringForCheck + "Property");
	            } else {
	              i.setOutput(true, stringForCheck + "Property");
	            }

	            // check preprocessing for availability to use optional property
	            if(srcOrTgt.toLowerCase() === 'source'){
	              this.$store.state.mainSource = true;
	            }
	            if(srcOrTgt.toLowerCase() === 'target'){
	              this.$store.state.mainTarget = true;
	            }

	            if(i.type.indexOf('renamepreprocessing')!== -1){
	              if(child.type === "preprocessingfunction"){
	                this.addChainOfPreprocessings(i, i.getField("RENAME").getDisplayText_());
	              } else {
	                let defaultRenameText = child.getFieldValue("propTitle").split(":")[1].toUpperCase();
	                if(i.getField("RENAME").getDisplayText_() === "X" || i.getField("RENAME").getDisplayText_().trim() === ''){
	                  i.getField("RENAME").setText(defaultRenameText); 
	                }

	                let renameText = i.getField("RENAME").getDisplayText_();                 
	                if(renameText !== defaultRenameText){
	                  i.getField("RENAME").setText(renameText); 
	                }
	                let isCleared = this.clearPropertyPath(child);
	                //without PP
	                if(isCleared){
						let strForXml = child.getFieldValue("propTitle")+ " RENAME " + i.getField("RENAME").getDisplayText_();
						this.addProperies(child,strForXml);
					}
					//with PP
					else if(child.getChildren().length && child.getChildren().filter(b => b.type === "propertyPath").length){

						if(!isCleared){

							let strForXml = child.getFieldValue("propTitle");
							let propPath = child.getChildren()[0].getField("path").getDisplayText_() + child.getChildren()[0].getFieldValue("propTitle");
							this.addProperies(child,strForXml + propPath + " RENAME " + i.getField("RENAME").getDisplayText_());
						}
					}
	              }
	            } else {
	              if(child.type === "preprocessingfunction"){
	                this.addChainOfPreprocessings(i, null);
	              } else {
	              	let isCleared = this.clearPropertyPath(child);
					//without PP
					if(isCleared){	
						let strForXml = child.getFieldValue("propTitle");
						let strOfPreprocessings = " AS "+ i.getFieldValue('function');
						strForXml += strOfPreprocessings;
						this.addProperies(child,strForXml);
					}
	                //with PP
					else if(child.getChildren().length && child.getChildren().filter(b => b.type === "propertyPath").length){

						if(!isCleared){

							let strForXml = child.getFieldValue("propTitle");
							let propPath = child.getChildren()[0].getField("path").getDisplayText_() + child.getChildren()[0].getFieldValue("propTitle");
							let strOfPreprocessings = " AS "+ i.getFieldValue('function');
							this.addProperies(child,strForXml + propPath + strOfPreprocessings);
						}
					}
	              }
	            }
	          }
	        });
	      } else { // not children blocks
			  //without property path
	      	if(i.getChildren().length === 0 ||  (i.getChildren().length && i.getChildren().filter(b => b.type === "propertyPath").length === 0)){
		        let strForXml = i.getFieldValue("propTitle");
		        this.addProperies(i,strForXml);
		    }else{
				// if(i.getField("enable_propertypath").getValue().toLowerCase() === 'false'){
				// 	console.log("REMOVE");
				// 	if(i.getChildren() && i.getChildren().length && i.getChildren()[0].type === "propertyPath"){
				// 		i.getChildren()[0].dispose();
				// 	}
				// }
				

				//with property path
				if(i.getChildren().length && i.getChildren().filter(b => b.type === "propertyPath").length){
					if(!this.clearPropertyPath(i)){
						let strForXml = i.getFieldValue("propTitle");
						let propPath = i.getChildren()[0].getField("path").getDisplayText_() + i.getChildren()[0].getFieldValue("propTitle");
						this.addProperies(i,strForXml + propPath);
					}
				}
			}
		    
	      }
	    },
	    clearPropertyPath(i){
	    	// i - block
	    	let isCleared = false;
			if(i.getField("enable_propertypath").getValue().toLowerCase() === 'false'){
				console.log("REMOVE");
				if(i.getChildren() && i.getChildren().length && i.getChildren()[0].type === "propertyPath"){
					i.getChildren()[0].dispose();
					isCleared = true;
				}
			}
			return isCleared;
	    },
	    addChainOfPreprocessings(i,renameText){
	      let arrForXml = i.toString().split(" ").map(pf => pf.toLowerCase()).filter(prepFunc => prepFunc !== "optional" && prepFunc !== "source" && prepFunc !== "target" && prepFunc !== "property" && prepFunc !== "rename" && prepFunc !== "as" && prepFunc !== "pp" && prepFunc !== "???" && prepFunc !== "");
	      if(renameText !== null && arrForXml[arrForXml.length-1].toLowerCase() === renameText.toLowerCase()){
	        arrForXml.pop();
	      }
	      /*console.log(arrForXml);
	      let strForXml = "";
	      if(arrForXml.filter(elem => elem === "/").length){
			let indexSlash = arrForXml.indexOf("/");
			strForXml = arrForXml[indexSlash-1]+"/"+arrForXml[indexSlash+1]+" AS ";
		  } else {
			  strForXml = arrForXml[arrForXml.length-1]+" AS ";
			  arrForXml.pop();
		  }  */
		  
		  if(arrForXml.filter(elem => elem === "/").length){
			let indexSlash = arrForXml.indexOf("/");
			arrForXml.splice(indexSlash);
		  }	
			
	      let strForXml = arrForXml[arrForXml.length-1]+" AS ";
	      arrForXml.pop();
	      let strOfPreprocessings = "";
	      let nodes = 0;
	      arrForXml.forEach(prepFunc => {
	        strOfPreprocessings += prepFunc + "->";
	        nodes++;
	      });
	      strOfPreprocessings = strOfPreprocessings.slice(0,-2);
	      if(renameText !== null){
			nodes++;  
	        strForXml += strOfPreprocessings + " RENAME " + renameText;//i.getField("RENAME").getDisplayText_();//rename;
	      } else {
	        strForXml += strOfPreprocessings;
	      }
	      let arr = i.toString().split(" ").map(pf => pf.toLowerCase());
	      let mainType = arr.filter(type => type === "source" || type === "target");
	      let optional = arr.filter(type => type === "optional");
	      let type = optional.length ? mainType[0] + " " + optional[0] : mainType[0];
	      //let child = {type: type};
	      let child = i;
	      for(let node=0;node<nodes;node++){
			  child = child.getChildren()[0];
		  }

		  //with property path
		  if(child && child.getChildren().length && child.getChildren().filter(b => b.type === "propertyPath").length){
		  let propPath = child.getChildren()[0].getField("path").getDisplayText_() + child.getChildren()[0].getFieldValue("propTitle");
			strForXml = strForXml.split(" ")[0]+propPath+" "+strForXml.split(" ").splice(1).join(" ");
			this.addProperies(child,strForXml);
		  } else {
			this.addProperies(child,strForXml);
		  }
	    }
	}
}
</script>
