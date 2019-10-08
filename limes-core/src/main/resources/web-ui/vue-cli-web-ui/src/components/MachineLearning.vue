<template>
	<md-tab id="machineLearning" md-label="Machine learning">
	  <!-- machine learning UI -->
	    <MLcomponent 
	    v-bind:data="mlalgorithm" 
	    ></MLcomponent>
	</md-tab>
</template>

<script>
import MLcomponent from './MLcomponent.vue'
export default {
	components: {
		MLcomponent
	},
	props: {
	    mlalgorithm: {
	      type: Object,
	    },
	},
  data () {
    return {

    }
  },
	methods: {

	}
}
</script>
