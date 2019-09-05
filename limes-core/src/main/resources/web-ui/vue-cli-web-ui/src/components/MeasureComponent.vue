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
	      let sourceProperty = this.$store.state.source.properties[num];
	      let targetProperty = this.$store.state.target.properties[num];
	      if(this.$store.state.source.function && this.$store.state.source.function.length){
	      	sourceProperty = this.$store.state.source.function;
	      }
	      if(this.$store.state.target.function && this.$store.state.target.function.length){
	      	targetProperty = this.$store.state.target.function;
	      }
	      if(sourceProperty){
			  sourceProperty = sourceProperty.split("AS")[0].trim();
			  if(sourceProperty.indexOf("RENAME") !== -1){
				 sourceProperty = sourceProperty.split("RENAME")[1].trim(); 
			  }
		  }
		  if(targetProperty){
			  targetProperty = targetProperty.split("AS")[0].trim();
			  if(targetProperty.indexOf("RENAME") !== -1){
				targetProperty = targetProperty.split("RENAME")[1].trim();
			  }
		  }
	      
	      src = sourceProperty;//values[0];
	      tgt = targetProperty;//values[1];

	      return {src:src, tgt:tgt};
	    },
	}
}
</script>
