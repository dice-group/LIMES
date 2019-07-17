<template>
  <div>
    <md-layout>
      <md-input-container class="contColumns">
        <label>Restriction</label>
        <md-input v-model="source.restriction" placeholder="Restriction"></md-input>
      </md-input-container>

      <md-input-container class="contColumns">
        <label>Restriction class</label>
        <md-input class="dropdown" v-model="classVar" @keyup.enter.native="enterClassClicked"
        @focus="onClassFocus" @blur="onClassBlur" placeholder="Select restriction class"></md-input>
     
      <p class="message">{{messageAboutClasses}}</p>
        <div class="dropdown-content"
              v-show="classesShown">
              <div
                class="dropdown-item"
                @mousedown="selectClass(option)"
                v-for="option in afterFilteredClasses">
                  {{ option }}
              </div>
              
        </div> 


      </md-input-container>
      <p class="message">{{messageAboutProps}}</p>

    </md-layout>
    <md-layout>
      <md-input-container class="advancedOptions" v-show="advancedOptionsShow">
        <label>Type (optional)</label>
        <md-input v-model="source.type" placeholder="Type (optional)"></md-input>
      </md-input-container>
    </md-layout>
    <md-layout  class="advancedOptions" v-show="advancedOptionsShow">
      Properties

      <md-chips v-model="source.properties" md-input-placeholder="Add a property"></md-chips>
      

    </md-layout>
    <md-layout class="advancedOptions" v-show="advancedOptionsShow">
      Optional Properties
      <md-chips v-model="source.optionalProperties" md-input-placeholder="Add an optional property"></md-chips>
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
    messageAboutClasses: {
      type: String,
    },
    messageAboutProps: {
      type: String,
    },
    afterFilteredClasses: {
      type: Array,
    },
    fetchProperties: {
      type: Function,
    },
    getPrefix: {
      type: Function,
    },
    classes: {
      type: Array,
    },
    classVarFromExample: {
      type: String,
    },
    prefixes: {
      type: Object,
    },
  },
  data () {
    return {
      focusedClass: false,
      classesShown: false,
      endpoints: [],
      //prefixes: [],
      customPrefixes: {},
      usingPrefix: [],
      exPrefix: [],
      classVar: '',
    }
  },
  methods: {
    onClassFocus() {
      this.focusedClass = true;
      this.classesShown = true;
    },
    onClassBlur() {
      this.focusedClass = false;
      this.classesShown = false;
    },
    selectClass(option){
      this.classVar = option;
      //this.$emit('toggle-classVar', option);
      this.source.propertiesForChoice.splice(0);
      this.fetchProperties(this.source.endpoint, option);
      this.changeRestrictions(option);
    },
    enterClassClicked(){
      this.source.propertiesForChoice.splice(0);
      this.fetchProperties(this.source.endpoint, this.classVar);
      this.changeRestrictions(this.classVar);
    },
    changeRestrictions(option){
      let prefixInfo = this.getPrefix(option);
      this.exPrefix = this.usingPrefix;
      this.usingPrefix = {label: prefixInfo.pair.split(":")[0], namespace: prefixInfo.namespace};
      let curRest = this.source.restriction;
      let rest;
      let restArr = curRest.split(" ");
      if(restArr[1] !== 'rdf:type'){
        restArr[1] = 'rdf:type';
      }
      restArr[2] = prefixInfo.pair;
      rest = restArr.join(" ");
      if(this.source.id === "sourceId"){
        this.$store.commit('changeSourceRestr', rest);
      }

      if(this.source.id === "targetId"){
        this.$store.commit('changeTargetRestr', rest);
      }  

    },
  },
  watch: {

      'classVar': function() {
        let afterFilteredClasses = this.classes.filter(i => {
          return i.toLowerCase().includes(this.classVar.toLowerCase())
        });
        this.$emit('toggle-afterFilteredClasses', afterFilteredClasses);
      },

      'classVarFromExample': function(){
        this.classVar = this.classVarFromExample;
      },

      'usingPrefix': function() {
        this.$emit('toggle-del-exprefix', this.exPrefix);
        this.$emit('toggle-prefix-from-rest', this.usingPrefix);

        let label = this.source.restriction.split(" ")[1].split(":")[0];
        let prefixFromRestriction = { 
          label: label , 
          namespace: this.prefixes[label]
        };
        this.$emit('toggle-prefix-from-rest', prefixFromRestriction);
      }
  }
}
</script>