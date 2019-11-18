<template>
    <md-whiteframe md-tag="section" style="flex: 1; padding: 15px;">
      <div class="md-title">Output</div>

      <md-select v-model="output.type" name="output" id="output">
        <md-option value="TAB">TAB</md-option>
        <md-option value="CSV">CSV</md-option>
        <md-option value="TTL">TTL</md-option>
        <md-option value="N3">N3</md-option>
      </md-select>
    </md-whiteframe>
</template>

<script>

export default {
	components: {
		
	},
	props: {
	    output: {
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
