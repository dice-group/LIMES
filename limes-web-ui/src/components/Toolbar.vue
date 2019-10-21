<template>
  <md-toolbar>
    <md-layout><h2 class="md-title" style="flex: 1" md-flex="60">LIMES Web UI</h2></md-layout>
    <md-layout>

    <md-menu md-size="5" md-align-trigger>
      <md-button md-menu-trigger style="margin-top: 18px;">Try examples</md-button>

      <md-menu-content>
        <md-menu-item v-on:click="exampleConfig()">Try example LGD config</md-menu-item>
        <md-menu-item v-on:click="exampleFilmAndMovieConfig()">Try example Movie and Film config</md-menu-item>
      </md-menu-content>
    </md-menu>

      <md-layout md-flex="40">
        <md-input-container style="margin-bottom: 13px;">
          <label>Select the configuration file</label>
          <md-file v-model="configurationFile" id="fileToLoad0" v-on:selected="importConfigurationFile()"></md-file>
        </md-input-container>
      </md-layout>

      <md-layout md-flex="25">
        <md-menu md-size="4" md-direction="top right" ref="menu">
          <md-button class="md-raised md-primary styleDetailsButton" md-menu-trigger>More details about the project</md-button>

          <md-menu-content>
            <div class="details-card">

              <div class="details-card-info">
                <p class="details-card-name">More details about the project:</p>
                <ul> 
                  <li><a href="https://dice.cs.uni-paderborn.de/projects/active-projects/limes/" target="_blank">Project web site</a></li>
                  <li><a href="http://dice-group.github.io/LIMES/#/user_manual/index" target="_blank">User manual</a></li>
                  <li><a href="https://github.com/dice-group/LIMES" target="_blank">GitHub</a></li>
                </ul>

              </div>
            </div>
          </md-menu-content>
        </md-menu>
      </md-layout> 

    </md-layout>

  </md-toolbar>
</template>

<script>
export default {
  props: {
    exampleConfig: {
      type: Function,
    },
    exampleFilmAndMovieConfig: {
      type: Function,
    },
    importConfigurationFile: {
      type: Function,
    },
    configurationFile: {
      type: String,
    },
  },
  methods:{

  }
}
</script>