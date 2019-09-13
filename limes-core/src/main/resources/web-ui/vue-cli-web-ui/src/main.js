import Vue from 'vue'
import App from './App.vue'

import Vuex from 'vuex'

Vue.use(Vuex);

window.SPARQL_ENDPOINT = "/sparql/";
window.PREPROCESSING_LIST = window.LIMES_SERVER_URL+"/list/preprocessings";
window.RESULT_FILES = window.LIMES_SERVER_URL+"/results/";
window.RESULT_FILE = window.LIMES_SERVER_URL+"/result/";
window.JOB_LOGS = window.LIMES_SERVER_URL+"/logs/";
window.JOB_STATUS = window.LIMES_SERVER_URL+"/status/";

// window.SPARQL_ENDPOINT = "/sparql/";
// window.SPARQL_ENDPOINT = "http://localhost:8080/sparql/";
// window.PREPROCESSING_LIST = "http://localhost:8080/list/preprocessings";
// window.RESULT_FILES = "http://localhost:8080/results/";
// window.RESULT_FILE = "http://localhost:8080/result/";
// window.JOB_LOGS = "http://localhost:8080/logs/";
// window.JOB_STATUS = "http://localhost:8080/status/";

const measures = ['Cosine', 'ExactMatch', 'Jaccard', 'Overlap', 'Jaro', 'JaroWinkler', 
'Levenshtein', 'MongeElkan', 'RatcliffObershelp', 'Soundex', 'Koeln', 'DoubleMetaphone',
'Trigram', 'Qgrams','Euclidean','Manhattan','Geo_Hausdorff','Geo_Max','Geo_Min','Geo_Mean','Geo_Avg',
'Geo_Frechet','Geo_Sum_Of_Min','Geo_Naive_Surjection','Geo_Fair_Surjection','Geo_Link',
'Top_Contains','Top_Covers','Top_Covered_By','Top_Crosses','Top_Disjoint','Top_Equals','Top_Intersects',
'Top_Overlaps','Top_Touches','Top_Within','Tmp_Concurrent','Tmp_Predecessor','Tmp_Successor','Tmp_After',
'Tmp_Before','Tmp_During','Tmp_During_Reverse','Tmp_Equals','Tmp_Finishes','Tmp_Is_Finished_By',
'Tmp_Overlaps','Tmp_Is_Overlapped_By','Tmp_Starts','Tmp_Is_Started_By','Tmp_Meets','Tmp_Is_xBy',
'Set_Jaccard'];
let measureOptionsArray = measures.map(i => [i, i]);

const store = new Vuex.Store({
    state: {
    	operators: ['and','or','minus','xor'],
    	measures: measures,
    	measureOptions: measureOptionsArray,
	    source: {
	        id: 'sourceId',
	        endpoint: '',
	        classes: [],
	        var: '?s',
	        pagesize: 1000,
	        restriction: '?s rdf:type some:Type',
            function: '',
	        type: 'sparql',
	        properties: ['dc:title AS lowercase RENAME name'],
	        optionalProperties: [],//['rdfs:label'],
	        propertiesForChoice: ["a","b","c"],
	        allProperties: [],
            renameName: "",
	      },
	    target: {
	        id: 'targetId',
	        endpoint: '',
	        classes: [],
	        var: '?t',
	        pagesize: 1000,
	        restriction: '?t rdf:type some:Type',
            function: '',
	        type: 'sparql',
	        properties: ['foaf:name AS lowercase RENAME name'],
	        optionalProperties: [],//['rdf:type'],
	        propertiesForChoice: ["a","b","c"],
	        allProperties: [],
            renameName: "",
	      },
        context: {},
        Workspace: null,
        mainSource: false,
	    mainTarget: false,
	    notConnectedToStart: false,
	    checkboxEndpointAsFile: false,
    },
    getters: {
	    getSrcProps: state => () => state.source.properties,
	    getTgtProps: state => () => state.target.properties,
	    getOpSrcProps: state => () => state.source.optionalProperties,
	    getOpTgtProps: state => () => state.target.optionalProperties,
	    getSrcAllProps: state => () => state.source.allProperties,
	    getTgtAllProps: state => () => state.target.allProperties,
	    checkboxEndpoint: state => () => state.checkboxEndpointAsFile,
	},
    mutations: {
        changeSource (state, obj) {
            state.source = obj;
        },
        changeTarget (state, obj) {
            state.target = obj;
        },
        changeSourceRenameName (state, str) {
            state.source.renameName = str;
        },
        changeTargetRenameName (state, str) {
            state.target.renameName = str;
        },
        changeSourceFunction (state, str) {
            state.source.function = str;
        },
        changeTargetFunction (state, str) {
            state.target.function = str;
        },
        changeSourceProps (state, arr) {
            state.source.properties = arr;
        },
        changeTargetProps (state, arr) {
            state.target.properties = arr;
        },
        changeSourceClasses (state, arr) {
            state.source.classes = arr;
        },
        changeTargetClasses (state, arr) {
            state.target.classes = arr;
        },
        changeSourceRestr (state, str) {
            state.source.restriction = str;
        },
        changeTargetRestr (state, str) {
            state.target.restriction = str;
        },
        changeSourceAllProps (state, arr) {
            state.source.allProperties = arr;
        },
        changeTargetAllProps (state, arr) {
            state.target.allProperties = arr;
        },
        changeContext(state, obj) {
            state.context = obj;
        },
        changeWorkspace(state, obj) {
            state.Workspace = obj;
        },
        removeProps (state) {
        	state.source.properties.splice(0);
            state.target.properties.splice(0);
            state.source.optionalProperties.splice(0);
            state.target.optionalProperties.splice(0);
        },
        addSrcProps (state, str) {
            state.source.properties.push(str);
        },
        addTgtProps (state, str) {
            state.target.properties.push(str);
        },
        addOSrcProps (state, str) {
            state.source.optionalProperties.push(str);
        },
        addOTgtProps (state, str) {
            state.target.optionalProperties.push(str);
        },
        changeCheckboxEndpointAsFile(state, bool){
			state.checkboxEndpointAsFile = bool;
		},
    },
});

Vue.config.productionTip = false;
Vue.use(VueMaterial);
new Vue({
	store,
  render: h => h(App),
}).$mount('#app')
