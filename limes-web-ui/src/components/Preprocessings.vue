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
	            // if(srcOrTgt.toLowerCase() === 'source'){
	            //   this.$store.state.mainSource = true;
	            // }
	            // if(srcOrTgt.toLowerCase() === 'target'){
	            //   this.$store.state.mainTarget = true;
	            // }

	            if(i.type.indexOf('renamepreprocessing')!== -1){
	              if(child.type === "preprocessingfunction"){
	                this.addChainOfPreprocessings(i, i.getField("RENAME").getDisplayText_());
	              } else {
	                let defaultRenameText = child.getFieldValue("propTitle") && child.getFieldValue("propTitle").split(":")[1].toUpperCase();
	                i.getField("RENAME").setText(defaultRenameText); 


	                let renameText = i.getField("RENAME").getDisplayText_();                 
	                if(renameText !== defaultRenameText){
	                  i.getField("RENAME").setText(renameText); 
	                }
	                let isCleared = true;
	                if(child.getChildren()[0] && child.getChildren()[0].type === "propertyPath"){
	                	isCleared = this.clearPropertyPath(child);
	                }
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

	              	if(i.getField("complexfunction") && i.getField("complexfunction").getDisplayText_() ==='Concat'){
	              		this.complexFunctionHandler('concat', i); 		
	              	} else
					if(i.getField("complexfunction") && i.getField("complexfunction").getDisplayText_() ==='Split'){
	              		this.complexFunctionHandler('split', i);	
	              	} else
					if(i.getField("complexfunction") && i.getField("complexfunction").getDisplayText_() ==='ToWktPoint'){
	              		this.complexFunctionHandler('toWktPoint', i);	
	              	} else
	              	{

		              	let strOfPreprocessings =  " AS "+ i.getFieldValue('function');

		              	if(i.getField("textA")){
		              		strOfPreprocessings = " AS "+ i.getFieldValue('function')+"("+i.getField("textA").text_+","+i.getField("textB").text_+")";
		              	}
		              	
		              	let isCleared = true;
		              	if(child.getChildren()[0] && child.getChildren()[0].type === "propertyPath"){
		              		isCleared = this.clearPropertyPath(child);
		              	}
						//without PP
						if(isCleared){	
							let strForXml = child.getFieldValue("propTitle");
							//let strOfPreprocessings = " AS "+ i.getFieldValue('function');
							strForXml += strOfPreprocessings;
							this.addProperies(child,strForXml);
						}
		                //with PP
						else 
						if(!isCleared && child.getChildren().length && child.getChildren().filter(b => b.type === "propertyPath").length){

							let strForXml = child.getFieldValue("propTitle");
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

	    complexFunctionHandler(functionName, i){
			let prblock1 = i.getChildren()[0] || null;
      		let prblock2 = i.getChildren()[1] || null;
      		let prop1 = prblock1 ? prblock1.getFieldValue("propTitle") : null;
      		let prop2 = prblock2 ? prblock2.getFieldValue("propTitle") : null;
      		let func = '';
      		let glue = ',';
      		let renameValue = "";
      		if(i.getField("glue_text")){
      			glue = i.getFieldValue("glue_text");
      		}

      		if(functionName === "concat"){
      			//concat(property1, property2, glue=",") RENAME newprop
      			func = functionName+"("+prop1+", "+prop2+', glue="'+glue+'") RENAME '+ i.getFieldValue("RENAME1");
      		} else if(functionName === "split"){
      			//split(property, splitChar=",") RENAME prop1,prop2
      			if(i.getField("enable_A") && i.getField("enable_A").getValue().toLowerCase() === 'true'){
      				renameValue = i.getFieldValue("RENAME1");
      			} else {
      				renameValue = i.getFieldValue("RENAME2");
      			}

      			func = functionName+"("+prop1+', splitChar="'+glue+'") RENAME '+ i.getFieldValue("RENAME1") +","+ i.getFieldValue("RENAME2");
      		}else if(functionName === "toWktPoint"){
				//toWktPoint(property1, property2) RENAME wktPoint
				func = functionName+"("+prop1+", "+prop2+") RENAME "+ i.getFieldValue("RENAME1");
      		}

      		if(prblock1 && prblock1.type.indexOf("source") !== -1){
      			this.$store.commit('changeSourceFunction', func);
      			if(renameValue && renameValue.length){
      				this.$store.commit('changeSourceRenameName', renameValue);
      			}
      		} else if(prblock1 && prblock1.type.indexOf("target") !== -1){
      			this.$store.commit('changeTargetFunction', func);
      			if(renameValue && renameValue.length){
      				this.$store.commit('changeTargetRenameName', renameValue);
      			}
      		}
      		if(prblock1){
      			this.addProperies(prblock1, prop1);
      		}
      		if(prblock2){
      			this.addProperies(prblock2, prop2);
      		}
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
	      	let renameStr = "";
	      	let propertyForXml = "";
	      	do{		
				//check next level
				let changedPrepr = curBlock.getField("textA");
              	if(changedPrepr){
              		strOfPreprocessings += curBlock.getFieldValue('function')+"("+curBlock.getField("textA").text_+","+curBlock.getField("textB").text_+")->";
              	} else 
              	if(curBlock.type === "renamepreprocessingfunction"){
              		renameStr = " RENAME " + curBlock.getField("RENAME").getDisplayText_();
              	}
              	else{
              		strOfPreprocessings +=  curBlock.getFieldValue('function') + "->";
              	}

				curBlock = curBlock.getChildren()[0];
				if(curBlock.type.indexOf('property') !== -1){
					propertyForXml = curBlock.getFieldValue("propTitle");
				}
				
			} while (curBlock.getChildren()[0] && (curBlock.getChildren()[0].type.indexOf('sourceproperty') !== -1
				|| curBlock.getChildren()[0].type.indexOf('targetproperty') !== -1));
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
			
			this.addProperies(curBlock,propertyForXml + strOfPreprocessings+renameStr);
			  
	    }
	}
}
</script>
