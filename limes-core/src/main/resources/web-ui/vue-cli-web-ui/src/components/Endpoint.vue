<template>
  <div>
    <md-layout>
      <md-input-container class="advancedOptions" v-show="advancedOptionsShow">
        <label>ID</label>
        <md-input v-model="source.id" placeholder="ID"></md-input>
      </md-input-container>
    </md-layout>
    <md-layout>
    <div>
      <md-radio v-model="checkboxEndpointAsFile" v-bind:md-value="false" class="md-primary" @change="changeCheckboxEndpoint">Sparql endpoint</md-radio>
      <md-radio v-model="checkboxEndpointAsFile" v-bind:md-value="true" class="md-primary" @change="changeCheckboxEndpoint">Local file</md-radio>
    </div>	
      <md-input-container class="dropdown">
        <label>Endpoint</label>
        <md-input v-model="source.endpoint" @keyup.enter.native="enterEndpointClicked"
        @focus="onFocus" @blur="onBlur" placeholder="Endpoint"
        v-show="!checkboxEndpointAsFile"
        ></md-input>
     
        <div class="dropdown-content"
              v-show="optionsShown">
              <div
                :key="option"
                class="dropdown-item"
                @mousedown="selectOption(option)"
                v-for="option in afterFilteredOptions">
                  {{ option }}
              </div>
              
        </div>
        <md-file v-model="endpointFile" id="endpointFile0" v-on:selected="importEndpointFile()" v-show="checkboxEndpointAsFile"></md-file> 
      </md-input-container>

    </md-layout>
    <md-layout>
      <md-input-container class="advancedOptions" v-show="advancedOptionsShow">
        <label>Var</label>
        <md-input v-model="source.var" placeholder="Var"></md-input>
      </md-input-container>
    </md-layout>
    <md-layout>
      <md-input-container class="advancedOptions" v-show="advancedOptionsShow">
        <label>Pagesize</label>
        <md-input v-model="source.pagesize" placeholder="Pagesize"></md-input>
      </md-input-container>
    </md-layout>
  </div>
</template>

<script>
export default {
  props: {
    source: {
      type: Object,
    },
    advancedOptionsShow: {
      type: Boolean,
    },
    fetchClasses: {
      type: Function,
    },
    classVar: {
      type: String,
    },
    endpointandclasses: {
      type: Object,
    },
    exampleConfigEnable: {
      type: Boolean,
    },
    fetchProperties: {
      type: Function,
    },
  },
  data () {
    return {
      focused: false,
      optionsShown: false,
      classes: [],
      endpoints: [],
      afterFilteredOptions: [],
      checkboxEndpointAsFile: false,
      endpointFile: '',
    }
  },
  beforeMount() {
        fetch('./lod-data.json')
            .then(function(response) {
              return response.json();
             })
            .then((content) => {
              let obj = {};
              for (let prop in content) {
                if(content[prop].sparql.length){
                  for(let i=0; i< content[prop].sparql.length; i++){
                    if(content[prop].sparql[i].status == "OK"){
                      obj[content[prop].sparql[i].access_url] = true;
                    }
                  }
                }
              }
              this.endpoints.push(...Object.keys(obj));
              this.afterFilteredOptions = this.endpoints;
            })                  
  },
  methods: {
    onFocus() {
      this.focused = true;
      this.optionsShown = true;
    },
    onBlur() {
      this.focused = false;
      this.optionsShown = false;
    },
    selectOption(option){
      this.exampleConfigEnable = false;
      this.source.endpoint = option;
      this.source.propertiesForChoice.splice(0);
      this.classVar = '';
      this.checkForSameEndpoints(option);
    },
    enterEndpointClicked(){
      this.exampleConfigEnable = false;
      this.onBlur();
      this.source.propertiesForChoice.splice(0);
      this.classVar = '';
      this.checkForSameEndpoints(this.source.endpoint);
    },
    checkForSameEndpoints(currentOption){
      if(this.endpointandclasses.endpoint !== currentOption){
        this.fetchClasses(currentOption);
      } else {
        this.$emit('repeat-source-classes', this.endpointandclasses.classes);
        this.$emit('repeat-classes', this.endpointandclasses.classes);
      }
    },
    importEndpointFile(){
		var fileToLoad = document.getElementById("endpointFile0").files[0];
		const formData = new FormData();

		formData.append('file', fileToLoad);

		const options = {
		  method: 'POST',
		  body: formData,
		};

		fetch(window.LIMES_SERVER_URL+'/upload', options)
		.then(function (response) {
			return response.json();
		})      
		.then((content) => {
			this.source.endpoint = content.uploads[0].uploadId;
		})

	},
	changeCheckboxEndpoint(){	
		this.checkboxEndpointAsFile = !this.checkboxEndpointAsFile
		this.$store.commit('changeCheckboxEndpointAsFile', this.checkboxEndpointAsFile);
		if(!this.checkboxEndpointAsFile){
			this.source.endpoint = '';
			this.endpointFile = '';
		}
	},
  },
  watch: {
      'source.endpoint': function() {
         this.afterFilteredOptions = this.endpoints.filter(i => {
          return i.toLowerCase().includes(this.source.endpoint.toLowerCase())
        });
        if(this.classVar === '' && this.exampleConfigEnable){
          //this.classVar = this.endpointandclasses.classes;
          this.$emit('toggle-classVar', this.endpointandclasses.classes[0]);
          if(this.endpointandclasses.classes.length){
            this.fetchProperties(this.source.endpoint, this.endpointandclasses.classes[0], this.checkboxEndpointAsFile);
          } else {
            this.fetchClasses(this.source.endpoint, this.checkboxEndpointAsFile);
          }
        }
        if(this.checkboxEndpointAsFile){
			this.fetchClasses(this.source.endpoint, this.checkboxEndpointAsFile);
		}
          
      },
  }
}
</script>
