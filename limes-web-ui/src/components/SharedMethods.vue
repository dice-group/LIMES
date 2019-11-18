<template>

</template>

<script>
export default {
	components: {
	},
	props: {
	},
  data () {
    return {

    }
  },
	methods: {
		forceFileDownload(str, filename){
	      const url = window.URL.createObjectURL(new Blob([str]))
	      const link = document.createElement('a')
	      link.href = url
	      link.setAttribute('download', filename) //or any other extension
	      document.body.appendChild(link)
	      link.click()
	    },
	    xmlToWorkspace(str){
	      var xml = Blockly.Xml.textToDom(str);
	      if(str.indexOf('type="start"')!== -1){
	        this.$store.state.Workspace.getBlocksByType("start")[0].dispose(true);
	      }
	      Blockly.Xml.domToWorkspace(xml, this.$store.state.Workspace);
	    },
	}
}
</script>
