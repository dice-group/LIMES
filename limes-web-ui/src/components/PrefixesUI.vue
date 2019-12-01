<template>
<md-whiteframe md-tag="section" style="margin-top: 20px; flex: 1; padding-top: 15px; padding-left: 15px;">
      <!-- <div class="md-title">Prefixes</div> -->
      <div class="md-title" style="display: inline-block;" v-b-tooltip.html.right="tooltipTitle">Prefixes</div>
      <md-layout>
        <div style="padding-top: 20px;" v-if="!prefixes.length">No prefixes defined..</div>
        <md-chip v-for="prefix in prefixes" md-deletable v-on:delete="deleteChip(prefix)" :key="prefix.label" style="margin-left: 3px">
          {{ prefix.label }}
          <md-tooltip md-direction="bottom">{{ prefix.namespace }}</md-tooltip>
        </md-chip>
      </md-layout>

      <md-layout md-gutter="8">
        <md-layout md-flex="33">
          <md-input-container class="dropdown">
            <label>Label</label>
            <md-input class="dropdown-input" v-model="label" @focus="onFocus" @blur="onBlur" placeholder="Label"></md-input>

            <div class="dropdown-content"
                  v-show="optionsShown">
                  <div
                    class="dropdown-item"
                    :key="option"
                    @mousedown="selectOption(option)"
                    v-for="option in afterFilteredOptions">
                      {{ option }}
                  </div>
                  
            </div> 
            

          </md-input-container>
        </md-layout>
        <md-layout md-flex>
          <md-input-container>
            <label>Namespace</label>
            <md-input v-model="namespace" placeholder="Namespace"></md-input>
          </md-input-container>
        </md-layout>
        <md-layout md-flex="25">
          <md-button v-on:click="add()">Add</md-button>
        </md-layout>
      </md-layout>
    </md-whiteframe>
</template>

<script>
export default {
  props: {
    prefixes: {
      type: Array,
    },
    filteredOptions: {
      type: Array,
    },
    deleteChip: {
      type: Function,
    },
    addPrefix: {
      type: Function,
    },
    context: {
      type: Object,
    },
  },

  data() {
    return {
      label: '',
      namespace: '',
      focused: false,
      optionsShown: false,
      afterFilteredOptions: this.filteredOptions,
      tooltipTitle: { title: "<a href='http://dice-group.github.io/LIMES/#/user_manual/running_limes?id=_31-prefixes' target='_blank' style='color: #191970;'>User manual: Prefixes</a>",
      },
    };
  },
  methods: {
    add() {
      this.addPrefix({
        label: this.label,
        namespace: this.namespace,
      });
      // cleanup
      this.namespace = '';
      this.label = '';
    },
    onFocus() {
      this.focused = true;
      this.optionsShown = true;
        

    },
    onBlur() {
      this.focused = false;
      this.optionsShown = false;
    },
    selectOption(option){
      this.label = option;
      this.namespace=this.context[option];
    }
  },
  watch: {
      label: function() {
         this.afterFilteredOptions = this.filteredOptions.filter(i => {
          return i.toLowerCase().includes(this.label.toLowerCase())
        })
      }
  }
}
</script>