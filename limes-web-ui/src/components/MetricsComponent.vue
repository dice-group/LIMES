<template>
    <md-whiteframe md-tag="section" style="flex: 1; padding-top: 15px; padding-left: 15px; padding-right: 15px;">
      <div class="md-title">Metrics</div>
      <md-chips v-model="metrics" md-static></md-chips>
    </md-whiteframe>
</template>

<script>

export default {
	components: {
		
	},
	props: {
	    metrics: {
	      type: Array,
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
