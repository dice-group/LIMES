<template>
	<md-whiteframe md-tag="section" style="flex: 1; padding-top: 15px; padding-left: 15px; padding-right: 15px;">
      <div class="md-title">{{ title }}</div>
      <md-input-container>
        <label>Threshold</label>
        <input type="range" min="0" :max="getMaxV()" step="0.01" v-model.number="data.threshold">
        <md-input v-model="data.threshold" placeholder="Threshold"></md-input>
      </md-input-container>
      <md-input-container>
        <label>File</label>
        <md-input v-model="data.file" placeholder="File"></md-input>
      </md-input-container>
      <md-input-container>
        <label>Relation</label>
        <md-input v-model="data.relation" placeholder="Relation"></md-input>
        <p class="message">{{messageAboutPrefixInRelation}}</p>
      </md-input-container>
    </md-whiteframe>
</template>

<script>

export default {
	components: {

	},
	props: {
		data: {
	      type: Object,
	    },
	    title: {
	      type: String,
	    },
	    acceptance: {
	      type: Object,
	    },
	    review: {
	      type: Object,
	    },
	    prefixes: {
	      type: Object,
	    },

	},
  data () {
    return {
      messageAboutPrefixInRelation: '',
      exPrefixes: [{label: 'owl', namespace: 'http://www.w3.org/2002/07/owl#'}],
    }
  },
	methods: {
		getMaxV(){
	      let maxV;
	      if (this.data.id === 'review'){
	        maxV = this.acceptance.threshold;
	        if(this.review.threshold > maxV){
	          this.review.threshold = maxV;
	        }
	      } else {
	        maxV = 1;
	      }
	      return maxV;
	    }
	},
	watch: {
	    'data.relation': function(){
	      // console.log(this.exPrefixes);
	      this.exPrefixes.forEach(expr => {
	        if(expr.label !== this.acceptance.relation.split(":")[0] && expr.label !== this.review.relation.split(":")[0]){
	          this.$emit('del-exprefix', expr);
	        }
	      });
	      let property = this.data.relation.split(":")> 2 ? this.data.relation.split(":")[1] : this.data.relation.split(":")[2];
	      let label = this.data.relation.split(":").length > 2 ? this.data.relation.split(":")[1] : this.data.relation.split(":")[0];

	      if(label[0] === '/'){ // if label is url
	        let prefixNamespace = label;
	        for(let key in this.prefixes){
	          if (this.prefixes[key] === 'http:'+prefixNamespace){
	            label = key;
	          }
	        }
	        this.data.relation = label+':'+property;
	      } 

	      if(!this.prefixes[label]){
	        this.messageAboutPrefixInRelation = "Prefix is not found";
	      } else {
	        this.messageAboutPrefixInRelation = '';
	        let prefixFromRelation = { 
	          label: label , 
	          namespace: this.prefixes[label]
	        };
	        this.exPrefixes.push(prefixFromRelation);
	        this.$emit('send-prefix', prefixFromRelation);
	      }
	    },
	    'data.threshold': function(){
	      if(this.data.id !== 'review'){
	        if(this.data.threshold > 1){
	          this.$emit('change-threshold', 1);
	        }
	        if(this.data.threshold < 0){
	          this.$emit('change-threshold', 0);
	        }
	      }else {
	        if(this.data.threshold > this.acceptance.threshold){
	          this.$emit('change-revthreshold', this.acceptance.threshold);
	        }
	        if(this.data.threshold < 0){
	          this.$emit('change-revthreshold', 0);
	        }
	      }
    	},
    }
}
</script>
