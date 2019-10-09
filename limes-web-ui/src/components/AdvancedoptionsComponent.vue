<template>
    <md-whiteframe md-tag="section" style="flex: 1; padding: 15px;">
      <md-switch v-model="advancedOptionsShow" @change="switchCheck" ref="advancedOpts" id="my-advancedChecked" name="my-advancedChecked" class="md-primary">Enable advanced options</md-switch>
    </md-whiteframe>
</template>

<script>

export default {
	components: {
		
	},
	props: {
	    mlalgorithm: {
	      type: Object,
	    },
	    advancedOptionsShow: {
	      type: Boolean,
	    },
	},
  data () {
    return {

    }
  },
	methods: {
		switchCheck(){
	      this.$emit('toggle-advanced-options', !this.advancedOptionsShow)
	    }
	}
}
</script>
