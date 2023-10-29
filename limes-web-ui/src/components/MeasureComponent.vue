<template>
</template>

<script>
import Preprocessings from './Preprocessings.vue'
export default {
	mixins: [Preprocessings],
	components: {
		Preprocessings
	},
  data () {
    return {

    }
  },
	methods: {
		showThreshold(i){
		  let isShown = false;
		  if(i){
		    if(i.getField("enable_threshold").getValue().toLowerCase() === 'true'){
		      i.getField("threshold").setVisible(true);
		      i.getField("threshold").forceRerender();
		      isShown = true;
		    } else {
		      i.getField("threshold").setVisible(false);
		      i.getField("threshold").forceRerender();
		      isShown = false;
		    }
		  }
		  return isShown;
		},
		getFromMeasure(i,num){
	      let src;
	      let tgt;
	      i.getChildren().forEach(
	        pr => {
	          pr.setDisabled(false);
	          if(pr.type !== "measure"){
				this.processingPropertyWithPrepFunc(pr); 
			  }             
	      }); 

	      /*let valuesOfBlock = i.toString().split(' ');
	      let values = [];
	      let srcOrTgt;
	      for(let val=0; val<valuesOfBlock.length; val++){
	        if(valuesOfBlock[val] === "As"){
	          srcOrTgt = valuesOfBlock[val-3].toLowerCase();
	          values.push(valuesOfBlock[val+1]);
	        }
	      }

	      if(values.length === 0){
	        values = valuesOfBlock.filter(val => val.indexOf(":") !== -1);
	      } 
	      if(values.length === 1){
	        if(srcOrTgt === 'source'){
	          values.push(valuesOfBlock.filter(val => val.indexOf(":") !== -1)[1]);
	        }
	        if(srcOrTgt === 'target'){
	          values.unshift(valuesOfBlock.filter(val => val.indexOf(":") !== -1)[0]);
	        }
	      }*/
	      let sourceProperty = this.$store.state.source.properties[num] 
	      	|| this.$store.state.source.optionalProperties[num];
	      	

	      if(!sourceProperty && this.$store.state.source.properties.length < 2){
	      	if(i.getChildren().length && i.getChildren()[0].toString().toLowerCase().indexOf("optional") !== -1){
		      	sourceProperty = this.$store.state.source.optionalProperties[num-1];
		    } else {
		      	sourceProperty = this.$store.state.source.properties[num-1];
			}
		  }

	      let targetProperty = this.$store.state.target.properties[num]
	      	|| this.$store.state.target.optionalProperties[num];
	      if(!targetProperty && this.$store.state.target.properties.length < 2){
	      	if(i.getChildren().length && i.getChildren()[0].toString().toLowerCase().indexOf("optional") !== -1){
		      	targetProperty = this.$store.state.target.optionalProperties[num-1];
		    } else {
		      	targetProperty = this.$store.state.target.properties[num-1];
		    } 
		  }

	      if(this.$store.state.source.function && this.$store.state.source.function.length){
	      	sourceProperty = this.$store.state.source.function;
	      }
	      if(this.$store.state.target.function && this.$store.state.target.function.length){
	      	targetProperty = this.$store.state.target.function;
	      }
	      if(sourceProperty){
	      	let srdP = sourceProperty;
			  sourceProperty = sourceProperty.split("AS")[0].trim();
			  if(srdP.indexOf("RENAME") !== -1){
				 sourceProperty = srdP.split("RENAME")[1].trim(); 
				 if(this.$store.state.source.renameName && this.$store.state.source.renameName.length){
				 	sourceProperty = this.$store.state.source.renameName;
				 }
			  }
		  }
		  if(targetProperty){
		  	let tgtP = targetProperty;
			  targetProperty = targetProperty.split("AS")[0].trim();
			  if(tgtP.indexOf("RENAME") !== -1){
				targetProperty = tgtP.split("RENAME")[1].trim();
				if(this.$store.state.target.renameName && this.$store.state.target.renameName.length){
				 	targetProperty = this.$store.state.target.renameName;
				}
			  }
		  }
	      
	      src = sourceProperty;//values[0];
	      tgt = targetProperty;//values[1];

	      return {src:src, tgt:tgt};
	    },
	}
}
</script>
