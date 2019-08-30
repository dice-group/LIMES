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
							let propPath = this.getPropPath(child);
							this.addProperies(child,strForXml + propPath + " RENAME " + i.getField("RENAME").getDisplayText_());
						}
					}
	              }
	            } else {	
	              if(child.type === "preprocessingfunction"){
	                this.addChainOfPreprocessings(i, null);
	              } else {

	              	let strOfPreprocessings =  " AS "+ i.getFieldValue('function');
	              	if(this.addFieldsForPreprFunction(i)){
	              		strOfPreprocessings = " AS "+ i.getFieldValue('function')+"("+i.getField("textA").text_+","+i.getField("textB").text_+")";
	              	}

	              	let isCleared = this.clearPropertyPath(child);
					//without PP

					//if(isCleared){	
						let strForXml = child.getFieldValue("propTitle");
						//let strOfPreprocessings = " AS "+ i.getFieldValue('function');
						strForXml += strOfPreprocessings;
						this.addProperies(child,strForXml);
					//}
	                //with PP
					//else 
					if(!isCleared && child.getChildren().length && child.getChildren().filter(b => b.type === "propertyPath").length){

						if(!isCleared){

							//let 
							strForXml = child.getFieldValue("propTitle");
							let propPath = this.getPropPath(child);
							//let strOfPreprocessings = " AS "+ i.getFieldValue('function');
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
				
				//with property path
				if(i.getChildren().length && i.getChildren().filter(b => b.type === "propertyPath").length){
					if(!this.clearPropertyPath(i)){
						let strForXml = i.getFieldValue("propTitle");
						let propPath = this.getPropPath(i);
						this.addProperies(i,strForXml + propPath);
					}
				}
			}
		    
	      }
	    },
	    addFieldsForPreprFunction(i){
	    	let strOfPreprocessingsChanged = false;
	      	// if prepr function - replace or regexreplace
	        if(i.type !== 'sourceproperty' && (i.getField("function").getDisplayText_() === 'replace' 
	        	|| i.getField("function").getDisplayText_() ==='regexreplace')){
	      	  //add two input fields
	      	  if(!i.getField("textA")){
	          	  var textinput1 = new Blockly.FieldTextInput('A');
	          	  var textinput2 = new Blockly.FieldTextInput('B');
	          	  i.getInput("NAME").appendField(textinput1, 'textA');
	          	  i.getInput("NAME").appendField(textinput2, 'textB');
	          }
	          strOfPreprocessingsChanged = true;
	          
	        } else {
	        	if(i.getField("textA")){
	        		i.getInput("NAME").removeField('textA');
	          		i.getInput("NAME").removeField('textB');
	          	}
	          	strOfPreprocessingsChanged = false;
	        }
	        return strOfPreprocessingsChanged;
	    },
	    getPropPath(i){
			let propPath = '';
			let curBlock = i;
			do{		
				//check next level
				propPath += curBlock.getChildren()[0].getField("path").getDisplayText_() + curBlock.getChildren()[0].getFieldValue("propTitle");	
				if(curBlock.getChildren().length && curBlock.getChildren()[0].type !== "emptyBlock"){
					curBlock = curBlock.getChildren()[0];
				}			
			} while (curBlock.getChildren()[0].getField("enable_propertypath") !== null);
			
			return propPath;
		},
		
	    clearPropertyPath(i){
	    	// i - block
	    	let isCleared = false;
			let curBlock = i;
			do{		
				//check next level
				if(curBlock.getField("enable_propertypath").getValue().toLowerCase() === 'false'){
					console.log("REMOVE");
					if(curBlock.getChildren() && curBlock.getChildren().length && curBlock.getChildren()[0].type === "propertyPath"){
						curBlock.getChildren()[0].dispose(true);
						isCleared = true;
					}
				}
				if(curBlock.getChildren().length && curBlock.getChildren()[0].type !== "emptyBlock"){
					curBlock = curBlock.getChildren()[0];
				}			
			} while (curBlock.getChildren()[0].getField("enable_propertypath") !== null || curBlock.getChildren()[0].length === 0);
			
			return isCleared;
	    },
	    
	    addChainOfPreprocessings(i,renameText){

	      //add fields to prepr functions if needed
	      	let curBlock = i;
	      	let strOfPreprocessings = " AS ";
	      	let propertyForXml = "";
	      	do{		
				//check next level
				let changedPrepr = this.addFieldsForPreprFunction(curBlock);
				
              	if(changedPrepr){
              		strOfPreprocessings += curBlock.getFieldValue('function')+"("+curBlock.getField("textA").text_+","+curBlock.getField("textB").text_+")->";
              	} else {
              		strOfPreprocessings +=  curBlock.getFieldValue('function') + "->";
              	}

				curBlock = curBlock.getChildren()[0];
				if(curBlock.type.indexOf('property') !== -1){
					propertyForXml = curBlock.getFieldValue("propTitle");
				}
				
			} while (curBlock.getChildren()[0].type.indexOf('sourceproperty') !== -1
				|| curBlock.getChildren()[0].type.indexOf('targetproperty') !== -1);
			strOfPreprocessings = strOfPreprocessings.slice(0,-2);

			//with property path
			let curPropBlock = curBlock;
			if(curPropBlock && curPropBlock.getChildren().length && curPropBlock.getChildren().filter(b => b.type === "propertyPath").length){
				do{		
					
					//check next level props
					curPropBlock = curPropBlock.getChildren()[0];
					if(curPropBlock){
						propertyForXml +=  curPropBlock.getField("path").getDisplayText_() +curPropBlock.getFieldValue("propTitle");
					} 
					
				} while (curPropBlock.type.indexOf('emptyBlock') !== -1);
			}
			
			this.addProperies(curBlock,propertyForXml + strOfPreprocessings);
			  
	    }
	}
}
</script>
