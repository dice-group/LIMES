<template>
    <md-whiteframe md-tag="section" style="flex: 1; padding-top: 15px; padding-left: 15px; padding-right: 15px;" class="advancedOptions" v-show="advancedOptionsShow">
      <div class="md-title">Execution</div>

      <md-layout md-gutter="8" style="padding-top: 10px; padding-bottom: 15px;">
        <md-layout md-flex="50">
          <md-field v-pre>
            <label for="planner">Planner</label>
            <md-select v-model="execution.planner" name="planner" id="planner">
              <md-option value="DEFAULT">Default</md-option>
              <md-option value="HELIOS">Helios</md-option>
              <md-option value="DYNAMIC">Dynamic</md-option>
            </md-select>
          </md-field>
        </md-layout>
        <md-layout md-flex="50">
          <md-field v-pre>
            <label for="engine">Engine</label>
            <md-select v-model="execution.engine" name="engine" id="engine">
              <md-option value="DEFAULT">Default</md-option>
              <md-option value="SIMPLE">Simple</md-option>
            </md-select>
          </md-field>
        </md-layout>
      </md-layout>
    </md-whiteframe>
</template>

<script>

export default {
	components: {

	},
	props: {
	    execution: {
	      type: Object,
	    },
      advancedOptionsShow:{
        type: Boolean,
      },
	},
  data () {
    return {

    }
  },
	methods: {

	},
}
</script>
