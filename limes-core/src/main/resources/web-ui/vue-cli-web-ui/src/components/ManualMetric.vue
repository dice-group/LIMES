<template>
 	<md-tab id="manualMetric" md-label="Manual metric">               
        <DatacanvasComponent 
        v-bind:metrics="metrics" 
        v-bind:deleteOldPrefixes="deleteOldPrefixes" title="Data target"
        ></DatacanvasComponent>
        <!-- metrics UI -->
        <MetricsComponent
          v-bind:metrics="metrics"
        ></MetricsComponent>

        <md-layout>
          <md-layout>
            <md-button class="md-raised md-flex" v-on:click="exportWorkspace()">Export workspace to xml</md-button>
          </md-layout>

          <md-layout>
            <md-input-container>
              <label>Select the file for importing to the workspace</label>
              <md-file v-model="fileWorkspaceForInput" id="fileToLoad" v-on:selected="importWorkspace()"></md-file>
            </md-input-container>
          </md-layout>
        </md-layout>
    </md-tab>
</template>

<script>
import DatacanvasComponent from './DatacanvasComponent.vue'
import MetricsComponent from './MetricsComponent.vue'
import SharedMethods from './SharedMethods.vue'
export default {
	mixins: [SharedMethods],
	props: {
	    metrics: {
	      type: Array,
	    },
	    deleteOldPrefixes:{
	      type: Function,
	    }
	},
	components: {
		DatacanvasComponent, MetricsComponent
	},
  data () {
    return {
    	fileWorkspaceForInput: '',
    }
  },
	methods: {
		// forceFileDownload(str, filename){
	 //      const url = window.URL.createObjectURL(new Blob([str]))
	 //      const link = document.createElement('a')
	 //      link.href = url
	 //      link.setAttribute('download', filename) //or any other extension
	 //      document.body.appendChild(link)
	 //      link.click()
	 //    },
		exportWorkspace(){
			var xml = Blockly.Xml.workspaceToDom(this.$store.state.Workspace);
		    var xml_text = Blockly.Xml.domToPrettyText(xml);//domToText(xml);
		    this.forceFileDownload(xml_text, "workspace.xml");
		},
		importWorkspace(){
			  var fileToLoad = document.getElementById("fileToLoad").files[0];

		      var fileReader = new FileReader();
		      fileReader.onload = (fileLoadedEvent) => {
		          var textFromFileLoaded = fileLoadedEvent.target.result;
		          this.xmlToWorkspace(textFromFileLoaded);
		      };

		      fileReader.readAsText(fileToLoad, "UTF-8");
		},
		// xmlToWorkspace(str){
	 //      var xml = Blockly.Xml.textToDom(str);
	 //      if(str.indexOf('type="start"')!== -1){
	 //        this.$store.state.Workspace.getBlocksByType("start")[0].dispose(true);
	 //      }
	 //      Blockly.Xml.domToWorkspace(xml, this.$store.state.Workspace);
	 //    },
	}
}
</script>
