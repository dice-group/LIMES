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

// apply vue-material stuff
Vue.use(VueMaterial);
Vue.config.devtools = true;

const makeDatasource = (data, tag) => `<${tag.toUpperCase()}>
  <ID>${data.id}</ID>
  <ENDPOINT>${data.endpoint}</ENDPOINT>
  <VAR>${data.var}</VAR>
  <PAGESIZE>${data.pagesize}</PAGESIZE>
  <RESTRICTION>${data.restriction}</RESTRICTION>
${data.properties.length ? data.properties.map(p => `  <PROPERTY>${p}</PROPERTY>`).join('\n') : ''}
${data.optionalProperties.length ? data.optionalProperties.map(p => `  <OPTIONAL_PROPERTY>${p}</OPTIONAL_PROPERTY>`).join('\n') : ''}
${data.type && data.type.length ? `  <TYPE>${data.type}</TYPE>` : ''}
</${tag.toUpperCase()}>
`;

const makeAccReview = (data, tag) => `<${tag.toUpperCase()}>
<THRESHOLD>${data.threshold}</THRESHOLD>
<FILE>${data.file}</FILE>
<RELATION>${data.relation}</RELATION>
</${tag.toUpperCase()}>
`;

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

// init the app
let app = new Vue({
  el: '#app',
  template: '#mainApp',
  data: {
    // config display
    configText: '',
    // execution progress
    jobId: '',
    jobRunning: false,
    jobShowResult: false,
    jobStatus: '-1',
    jobStatusText: 'loading..',
    jobError: false,
    results: [],
    // config
    prefixes: [{label: 'owl', namespace: 'http://www.w3.org/2002/07/owl#'}],
    allPrefixes: [{label: 'owl', namespace: 'http://www.w3.org/2002/07/owl#'}],
    exPrefixes: [],
    filteredOptions: [],
    context: [],
    source: {
      id: 'sourceId',
      endpoint: '',
      classes: [],
      var: '?s',
      pagesize: 1000,
      restriction: '?s rdf:type some:Type',
      type: 'sparql',
      properties: ['dc:title AS lowercase RENAME name'],
      optionalProperties: [],//['rdfs:label'],
      propertiesForChoice: ["a","b","c"],
      allProperties: [],
    },
    target: {
      id: 'targetId',
      endpoint: '',
      classes: [],
      var: '?t',
      pagesize: 1000,
      restriction: '?t rdf:type some:Type',
      type: 'sparql',
      properties: ['foaf:name AS lowercase RENAME name'],
      optionalProperties: [],//['rdf:type'],
      propertiesForChoice: ["a","b","c"],
      allProperties: [],
    },
    Workspace: null,
    fileWorkspaceForInput: '',
    importWorkspaceString: '',
    configurationFile: '',
    metrics: [''],
    //selectedMeasureOption: '',
    measureOptions: measureOptionsArray,
    //selectedOperatorOption: '',
    //operatorOptions: operatorOptionsArray,
    acceptance: {
      id: 'acceptance',
      threshold: 0.98,
      file: 'accepted.nt',
      relation: 'owl:sameAs',
    },
    review: {
      id: 'review',
      threshold: 0.01,
      file: 'reviewme.nt',
      relation: 'owl:sameAs',
    },
    mlalgorithm: {
      enabled: false,
      name: 'WOMBAT Simple',
      type: 'unsupervised',
      training: 'trainingData.nt',
      parameters: [
        {
          name: 'max execution time in minutes',
          value: 60,
        },
      ],
    },
    execution: {
      rewriter: 'DEFAULT',
      planner: 'DEFAULT',
      engine: 'DEFAULT',
    },
    output: {
      type: 'TAB',
    },
    advancedOptionsShow: false,
    typesOfBlocks: {
      'start': Start,
      'sourceproperty': sourceProperty,
      'targetproperty': targetProperty,
      'optionalsourceproperty': optionalSourceProperty,
      'optionaltargetproperty': optionalTargetProperty,
      'measure': Measure,
      'operator': Operator,
      'preprocessingfunction':PreprocessingFunction,
      'nolangpreprocessingfunction': NolangPreprocessingFunction,
      'renamepreprocessingfunction': RenamePreprocessingFunction,
      'lowercasepreprocessingfunction': LowercasePreprocessingFunction,
      'numberpreprocessingfunction': NumberPreprocessingFunction,
      'cleaniripreprocessingfunction': CleaniriPreprocessingFunction,
      'celsiuspreprocessingfunction': CelsiusPreprocessingFunction,
      'fahrenheitpreprocessingfunction': FahrenheitPreprocessingFunction,
      'removebracespreprocessingfunction': RemovebracesPreprocessingFunction,
      'regularalphabetpreprocessingfunction': RegularAlphabetPreprocessingFunction,
      'uriasstringpreprocessingfunction': UriasstringPreprocessingFunction,
      'uppercasepreprocessingfunction': UppercasePreprocessingFunction,
    },
    mainSource: false,
    mainTarget: false,
    notConnectedToStart: false,
    executedKey: '',
    isDisabledLog: true,
    isDisabled: true,
    availableFiles: [],
    notFoundKeyMessage: '',
    findStatusMessage: '',
    exampleConfigEnable: false,
  },
  watch: {
      'source.properties': function() {
        this.addOldAndNewPrefix(this.source.properties);
      },
      'target.properties': function() {
        this.addOldAndNewPrefix(this.target.properties);
      },
      'source.optionalProperties': function() {
        this.addOldAndNewPrefix(this.source.optionalProperties);
      },
      'target.optionalProperties': function() {
        this.addOldAndNewPrefix(this.target.optionalProperties);
      },
      'source.allProperties': function() {
        this.deleteOldPrefixes();
        this.source.properties.splice(0);
      },
      'target.allProperties': function() {
        this.deleteOldPrefixes();
        this.target.properties.splice(0);
      },
  },
  mounted() {
    const jobIdmatches = /\?jobId=(.+)/.exec(window.location.search);
    if (jobIdmatches) {
      const jobId = jobIdmatches[1];
      this.jobId = jobId;
      this.jobRunning = true;
      setTimeout(() => this.$refs.jobDialog.open(), 10);
      setTimeout(() => this.getStatus(), 1000);

    }

    let blocklyInit = () => {
      //this.convertMetricToBlocklyXML();
          // var this.Workspace = Blockly.inject('blocklyDiv',
          //   {toolbox: document.getElementById('toolbox')});

          var blocklyArea = document.getElementById('blocklyArea');
          var blocklyDiv = document.getElementById('blocklyDiv');
          // var this.Workspace = this.this.Workspace;
          this.Workspace = Blockly.inject(blocklyDiv,
              {toolbox: document.getElementById('toolbox'),
                trashcan: true,
              });
          var onresize = (e) => {
            // Compute the absolute coordinates and dimensions of blocklyArea.
            var element = blocklyArea;
            var x = 0;
            var y = 0;
            do {
              x += element.offsetLeft;
              y += element.offsetTop;
              element = element.offsetParent;
            } while (element);
            // Position blocklyDiv over blocklyArea.
            //blocklyDiv.style.left = x + 'px';
            //blocklyDiv.style.top = y + 'px';
            blocklyDiv.style.width = blocklyArea.offsetWidth + 'px';
            blocklyDiv.style.height = blocklyArea.offsetHeight + 'px';
            Blockly.svgResize(this.Workspace);
          };
          window.addEventListener('resize', onresize, false);
          onresize();
          Blockly.svgResize(this.Workspace);



          var newBlock = this.Workspace.newBlock('start');
          newBlock.setDeletable(false);
          newBlock.initSvg();
          newBlock.render();
          //addRootBlock(Blockly.Blocks['start']);

          let onFirstComment = (event) => {
            // console.log("change");
            //console.log(this.Workspace.getAllBlocks()[0].getField("propTitle").getDisplayText_());
            //console.log(this.Workspace.getTopBlocks());
            this.source.properties.splice(0);
            this.target.properties.splice(0);
            this.source.optionalProperties.splice(0);
            this.target.optionalProperties.splice(0);

            let allBlocks = this.Workspace.getTopBlocks();

            allBlocks.forEach( block => {
              this.notConnectedToStart = false;
              if(block.type === 'start'){
                block.getChildren().forEach(i => { 
                  this.processingPropertyWithPrepFunc(i);
                  i.setDisabled(false);
                  switch(i.type) {

                    case "measure": {

                      let shownM = showThreshold(i);
                      let properties = this.getFromMeasure(i);

                      let threshold = i.getField("threshold").text_;
                      let measureFunc = i.getField("measureList").getDisplayText_();
                      this.metrics.splice(0);
                      if(shownM){
                        this.metrics.push(measureFunc.toLowerCase()+"("+ this.source.var.substr(1)+"."+properties.src+","+ this.target.var.substr(1)+"." +properties.tgt+")|"+threshold);
                      } else {
                        this.metrics.push(measureFunc.toLowerCase()+"("+this.source.var.substr(1)+"."+properties.src+","+this.target.var.substr(1)+"."+properties.tgt+")"); 
                      }
            
                      break;
                    }

                    case "operator": {
                      let firstM = i.getChildren()[0];
                      let secondM = i.getChildren()[1];

                      this.mainSource = false;
                      this.mainTarget = false;

                      let props1 = "", props2 = "";
                      if(firstM && firstM.type === "measure"){
                        props1 = this.getFromMeasure(firstM);
                      }
                      if(secondM && secondM.type === "measure"){
                        props2 = this.getFromMeasure(secondM);
                      }

                      if(firstM){
                        firstM.setDisabled(false);



                        if(secondM && secondM.getChildren().length && (secondM.getChildren()[0].type ==='sourceproperty' || secondM.getChildren()[0].type ==='targetproperty')){
                          let secondMChildren = secondM.getChildren();
                          if(secondMChildren.length){
                            secondMChildren.forEach(m => {
                              if(m.type === 'sourceproperty'){
                                this.mainSource = true;
                              }
                              if(m.type === 'targetproperty'){
                                this.mainTarget = true;
                              }
                            });
                          }

                        } else {

                          if(firstM.getChildren().length){
                            firstM.getChildren().forEach(m => {
                              if(m.type === 'sourceproperty'){
                                this.mainSource = true;
                              }
                              if(m.type === 'targetproperty'){
                                this.mainTarget = true;
                              }
                            });

                          }
                        }
                      }

                      if(secondM){
                        secondM.setDisabled(false);

                        //console.log(this.mainSource,this.mainTarget);


                          if(this.mainSource){
                            secondM.inputList[1].setCheck([
                              "SourceProperty",
                              "PreprocessingFunction",
                              "OptionalSourceProperty"        
                            ]);
                          } else {
                            secondM.inputList[1].setCheck([
                              "SourceProperty",
                              "PreprocessingFunction"   
                            ]);
                          }
            
                          if(this.mainTarget){
                            secondM.inputList[2].setCheck([
                              "TargetProperty",
                              "PreprocessingFunction",
                              "OptionalTargetProperty"        
                            ]);
                          } else {
                            secondM.inputList[2].setCheck([
                              "TargetProperty",
                              "PreprocessingFunction"   
                            ]);
                          }

                          /////

                          if(this.mainSource){
                            firstM.inputList[1].setCheck([
                              "SourceProperty",
                              "PreprocessingFunction",
                              "OptionalSourceProperty"        
                            ]);
                          } else {
                            firstM.inputList[1].setCheck([
                              "SourceProperty",
                              "PreprocessingFunction"   
                            ]);
                          }
            
                          if(this.mainTarget){
                            firstM.inputList[2].setCheck([
                              "TargetProperty",
                              "PreprocessingFunction",
                              "OptionalTargetProperty"        
                            ]);
                          } else {
                            firstM.inputList[2].setCheck([
                              "TargetProperty",
                              "PreprocessingFunction"   
                            ]);
                          }
 

                      }
                      let shownFirstM = showThreshold(firstM);
                      let shownSecondM = showThreshold(secondM);

                      let operator = i.getField("operators").text_;
                      let measureFunc1 = firstM ? firstM.getField("measureList").getDisplayText_() : "undefined";
                      let measureFunc2 = secondM ? secondM.getField("measureList").getDisplayText_() : "undefined";
                      let threshold1 = firstM ? firstM.getField("threshold").text_ : "undefined";
                      let threshold2 = secondM ? secondM.getField("threshold").text_ : "undefined";
                      let varSrc = this.source.var.substr(1)+".";
                      let varTgt = this.target.var.substr(1)+".";
                      this.metrics.splice(0);
                      if(operator[0] !== 'N'){       
                        let str1 = operator + '(' + measureFunc1.toLowerCase()+"("+varSrc+props1.src+","+varTgt+props1.tgt+")"; 
                        let str2 = ',' + measureFunc2.toLowerCase()+"("+varSrc+props2.src+","+varTgt+props2.tgt+")";
                        if(shownFirstM && shownSecondM){
                          this.metrics.push(str1 + "|" + threshold1 + str2 + "|"+threshold2 + ')');
                        } else {

                          if(!shownFirstM && !shownSecondM){
                            this.metrics.push(str1 + str2 + ')');
                          }

                          if(shownFirstM && !shownSecondM){
                            this.metrics.push(str1 + "|" + threshold1 + str2 + ')');
                          } else if(!shownFirstM && shownSecondM) {
                            this.metrics.push(str1 + str2 + "|"+threshold2 + ')');
                          }
                        }



                      } else {
                     
                        let str1 = 'NOT(' + operator.substr(1) + '(' + measureFunc1.toLowerCase()+"("+varSrc+props1.src+","+varTgt+props1.tgt+")"; 
                        let str2 = ',' + measureFunc2.toLowerCase()+"("+varSrc+props2.src+","+varTgt+props2.tgt+")";  
                        if(shownFirstM && shownSecondM){
                          this.metrics.push(str1 + "|" + threshold1 + str2 + "|"+threshold2 + ')))');
                        } else {

                          if(!shownFirstM && !shownSecondM){
                            this.metrics.push(str1 + str2 + ')))');
                          }

                          if(shownFirstM && !shownSecondM){
                            this.metrics.push(str1 + "|" + threshold1 + str2 + ')))');
                          } else if(!shownFirstM && shownSecondM) {
                            this.metrics.push(str1 + str2 + "|"+threshold2 + ')))');
                          }
                        }

                      }




                      break;
                    }

                    default: {
                    }
                  }

                  // if(this.exPrefixes.length){
                  //   this.exPrefixes.forEach(pref => {
                  //     this.prefixes.forEach(pr => {
                  //       if(pref.label === pr.label){
                  //         this.deletePrefix(pr);
                  //       }
                  //     })
                  //   }) 
                  // }

                  // this.exPrefixes.splice(0);
                  this.deleteOldPrefixes();

                  // this.addOldAndNewPrefix(this.source.properties);
                  // this.addOldAndNewPrefix(this.target.properties);

                  // this.addOldAndNewPrefix(this.source.optionalProperties);
                  // this.addOldAndNewPrefix(this.target.optionalProperties);
                
                })
              } else {
                
                if(block.type.indexOf('preprocessing')!== -1){
                  this.notConnectedToStart = true;
                  this.processingPropertyWithPrepFunc(block);
                }
                block.setDisabled(true);
              }
            });



          }
          this.Workspace.addChangeListener(onFirstComment);

          
    };
    setTimeout(blocklyInit, 500);
  },
  beforeMount() {
    let context;
    let filteredOptions;
    fetch('http://prefix.cc/context')
            .then(function(response) {
              return response.json();
             })
            .then((content) => {
              context = content["@context"];
              filteredOptions = Object.keys(context);
              
              this.context = context;
              this.filteredOptions.push(...filteredOptions);
            })
            //.catch( alert );
    fetch(window.PREPROCESSING_LIST)
            .then(function(response) {
              return response.json();
             })
            .then((content) => {
              PreprocessingFunction.args0[0].options.length = 0;
              content.availablePreprocessings.forEach(prepr => {
                if(prepr.name !== "rename"){
                  PreprocessingFunction.args0[0].options.push([prepr.name, prepr.name]);
                }
              });
            })
            //.catch( alert );        
          let typesOfBlocks = this.typesOfBlocks;
          for(let key in typesOfBlocks){
            Blockly.Blocks[key] = {
              init: function() {
                this.jsonInit(typesOfBlocks[key]);              
              }
            };
          }

    Measure.args0[0].options.length = 0;
    this.measureOptions.forEach(i => {
      Measure.args0[0].options.push(i);
    });  

  },
  methods: {
    deletePrefix(prefix) {
      let amountAppearPrefixes = this.allPrefixes.filter(p => p.label === prefix.label).length;
      if(amountAppearPrefixes > 1){
        this.allPrefixes = this.allPrefixes.filter(p => p.label !== prefix.label && p.namespace !== prefix.namespace);
      } else {
        this.prefixes = this.prefixes.filter(p => p.label !== prefix.label && p.namespace !== prefix.namespace);
        this.allPrefixes = this.allPrefixes.filter(p => p.label !== prefix.label && p.namespace !== prefix.namespace);
      }
    },
    addPrefix(prefix) {
      //console.log(prefix);
      // push new prefix
      if(!this.prefixes.some(i => i.label === prefix.label)){
        this.prefixes.push(prefix);
      }
      this.allPrefixes.push(prefix);
    },
    deleteOldPrefixes(){
      if(this.exPrefixes.length){
        this.exPrefixes.forEach(pref => {
          this.prefixes.forEach(pr => {
            if(pref.label === pr.label){
              this.deletePrefix(pr);
            }
          })
        }) 
      }

      this.exPrefixes.splice(0);
    },
    addOldAndNewPrefix(props){
      props.forEach(pr => 
      {
          if(pr){
            let label = pr.split(":")[0];
            let pref = {label: label, namespace: this.context[label]};
            //if(!this.prefixes.some(i => i.label === label)){
              this.exPrefixes.push(pref);
            //}
          
            this.addPrefix({label: label, namespace: this.context[label]});
          }
      });
    },
    addProperies(i,strForXml){
      if(!this.notConnectedToStart){
        if(i.type.indexOf("source") !== -1){
          if(i.type.indexOf("optional") !== -1){
            this.source.optionalProperties.push(strForXml);
          } else {
            this.source.properties.push(strForXml);
          }   
        } else {
          if(i.type.indexOf("optional") !== -1){
            this.target.optionalProperties.push(strForXml);
          } else {
            this.target.properties.push(strForXml);
          }
        }
      }
    },
    addChainOfPreprocessings(i,renameText){
      let arrForXml = i.toString().split(" ").map(pf => pf.toLowerCase()).filter(prepFunc => prepFunc !== "optional" && prepFunc !== "source" && prepFunc !== "target" && prepFunc !== "property" && prepFunc !== "rename" && prepFunc !== "as");
      if(renameText !== null && arrForXml[arrForXml.length-1].toLowerCase() === renameText.toLowerCase()){
        arrForXml.pop();
      }
      let strForXml = arrForXml[arrForXml.length-1]+" AS ";
      arrForXml.pop();
      let strOfPreprocessings = "";
      arrForXml.forEach(prepFunc => {
        strOfPreprocessings += prepFunc + "->";
      });
      strOfPreprocessings = strOfPreprocessings.slice(0,-2);
      if(renameText !== null){
        strForXml += strOfPreprocessings + " RENAME " + renameText;//i.getField("RENAME").getDisplayText_();//rename;
      } else {
        strForXml += strOfPreprocessings;
      }
      let arr = i.toString().split(" ").map(pf => pf.toLowerCase());
      let mainType = arr.filter(type => type === "source" || type === "target");
      let optional = arr.filter(type => type === "optional");
      let type = optional.length ? mainType[0] + " " + optional[0] : mainType[0];
      let child = {type: type};
      this.addProperies(child,strForXml);
    },
    processingPropertyWithPrepFunc(i){
      if(i.getChildren().length){
        if(!this.notConnectedToStart){
          i.setDisabled(false);
        }
        i.getChildren().forEach(child => {
          if(!this.notConnectedToStart){
            child.setDisabled(false);
          }
          if(i.type.indexOf('preprocessing')!== -1){

            //changeOutputOfPreprocessingFunction
            let preprWithParam = i.toString().split(" ");
            let isOptional = preprWithParam.find(val => val.toLowerCase() === "optional");
            let srcOrTgt = preprWithParam.find(val => val.toLowerCase() === "source" || val.toLowerCase() === "target");
            let stringForCheck = srcOrTgt[0].toUpperCase() + srcOrTgt.slice(1);
            if(isOptional){
              i.setOutput(true, isOptional+stringForCheck + "Property");
            } else {
              i.setOutput(true, stringForCheck + "Property");
            }

            // check preprocessing for availability to use optional property
            if(srcOrTgt.toLowerCase() === 'source'){
              this.mainSource = true;
            }
            if(srcOrTgt.toLowerCase() === 'target'){
              this.mainTarget = true;
            }

            if(i.type.indexOf('renamepreprocessing')!== -1){
              if(child.getChildren().length){
                this.addChainOfPreprocessings(i, i.getField("RENAME").getDisplayText_());
              } else {
                let defaultRenameText = child.getFieldValue("propTitle").split(":")[1].toUpperCase();
                if(i.getField("RENAME").getDisplayText_() === "X" || i.getField("RENAME").getDisplayText_().trim() === ''){
                  i.getField("RENAME").setText(defaultRenameText); 
                }

                let renameText = i.getField("RENAME").getDisplayText_();                 
                if(renameText !== defaultRenameText){
                  i.getField("RENAME").setText(renameText); 
                }
                let strForXml = child.getFieldValue("propTitle")+ " RENAME " + i.getField("RENAME").getDisplayText_();
                this.addProperies(child,strForXml);
              }
            } else {



              if(child.getChildren().length){
                this.addChainOfPreprocessings(i, null);
              } else {
                let strForXml = child.getFieldValue("propTitle");
                let strOfPreprocessings = " AS "+ i.getFieldValue('function');
                strForXml += strOfPreprocessings;
                this.addProperies(child,strForXml);
              }
            }
          }
        });
      } else { // not children blocks
        let strForXml = i.getFieldValue("propTitle");
        this.addProperies(i,strForXml);
      }
    },
    getFromMeasure(i){
      let src;
      let tgt;
      i.getChildren().forEach(
        pr => {
          pr.setDisabled(false);
          this.processingPropertyWithPrepFunc(pr);              
      }); 

      let valuesOfBlock = i.toString().split(' ');
      let values = [];
      let srcOrTgt;
      for(let val=0; val<valuesOfBlock.length; val++){
        if(valuesOfBlock[val] === "As"){
          srcOrTgt = valuesOfBlock[val-3].toLowerCase();
          values.push(valuesOfBlock[val+1]);
        }
      }

      if(values.length === 0){
        values = valuesOfBlock.filter(val => val.indexOf(":") !== -1);
      } 
      if(values.length === 1){
        if(srcOrTgt === 'source'){
          values.push(valuesOfBlock.filter(val => val.indexOf(":") !== -1)[1]);
        }
        if(srcOrTgt === 'target'){
          values.unshift(valuesOfBlock.filter(val => val.indexOf(":") !== -1)[0]);
        }
      }
      src = values[0];
      tgt = values[1];

      return {src:src, tgt:tgt};
    },
    manualMetricClicked(index){
      if(index === 0){
        this.mlalgorithm.enabled = false;
      } else {
        this.mlalgorithm.enabled = true;
      }
      this.source.properties.splice(0);
      this.target.properties.splice(0);
      this.source.optionalProperties.splice(0);
      this.target.optionalProperties.splice(0);
      this.deleteOldPrefixes();
    },
    exportWorkspace(){
      var xml = Blockly.Xml.workspaceToDom(this.Workspace);
      var xml_text = Blockly.Xml.domToPrettyText(xml);//domToText(xml);
      this.forceFileDownload(xml_text, "workspace.xml");
    },
    importWorkspace(){

      var fileToLoad = document.getElementById("fileToLoad").files[0];

      var fileReader = new FileReader();
      fileReader.onload = (fileLoadedEvent) => {
          var textFromFileLoaded = fileLoadedEvent.target.result;
          //this.importWorkspaceString = textFromFileLoaded;
          this.xmlToWorkspace(textFromFileLoaded);
      };

      fileReader.readAsText(fileToLoad, "UTF-8");

    },
    importConfigurationFile(){
      var fileToLoad = document.getElementById("fileToLoad0").files[0];

      var fileReader = new FileReader();
      fileReader.onload = (fileLoadedEvent) => {
          var textFromFileLoaded = fileLoadedEvent.target.result;
          this.xmlToHtml(textFromFileLoaded);
      };

      fileReader.readAsText(fileToLoad, "UTF-8");
    },
    xmlToHtml(xmlStr){
      let parser=new DOMParser();
      let xmlDoc=parser.parseFromString(xmlStr,"text/xml");
      this.exampleConfigEnable = true;
      for (let i of xmlDoc.children[0].children) {
        switch(i.tagName){
          case "PREFIX":{
            this.addPrefix({namespace:i.firstElementChild.innerHTML,label:i.lastElementChild.innerHTML});
            break;
          }
          case "SOURCE":{
            this.source = {
              id: i.children[0].innerHTML,
              endpoint: i.children[1].innerHTML,
              var: i.children[2].innerHTML,
              pagesize: i.children[3].innerHTML,
              restriction: i.children[4].innerHTML,
              type: '',
              properties: [],
              optionalProperties: [],
              classes: [],
              propertiesForChoice: [],
            };
            if(i.children[6].tagName === "PROPERTY"){
              this.source.properties.splice(0);
              this.source.properties.push(i.children[5].innerHTML);
              this.source.properties.push(i.children[6].innerHTML);
              this.source.type = i.children[7].innerHTML;
            } else {
              this.source.properties.splice(0);
              this.source.properties.push(i.children[5].innerHTML);
              this.source.type = i.children[6].innerHTML;
            }
            break;
          }
          case "TARGET":{
            this.target = {
              id: i.children[0].innerHTML,
              endpoint: i.children[1].innerHTML,
              var: i.children[2].innerHTML,
              pagesize: i.children[3].innerHTML,
              restriction: i.children[4].innerHTML,
              type: '',
              properties: [],
              optionalProperties: [],
              classes: [],
              propertiesForChoice: [],
            };
            if(i.children[6].tagName === "PROPERTY"){
              this.target.properties.splice(0);
              this.target.properties.push(i.children[5].innerHTML);
              this.target.properties.push(i.children[6].innerHTML);
              this.target.type = i.children[7].innerHTML;
            } else {
              this.target.properties.splice(0);
              this.target.properties.push(i.children[5].innerHTML);
              this.target.type = i.children[6].innerHTML;
            }
            break;
          }
          case "METRIC":{
            //this.metrics.splice(0);
            //this.metrics.push(i.innerHTML.trim());
            this.convertMetricToBlocklyXML(i.innerHTML.trim());
            break;
          }
          case "ACCEPTANCE":{
            this.acceptance = {
              id: 'acceptance',
              threshold: i.children[0].innerHTML,
              file: i.children[1].innerHTML,
              relation: i.children[2].innerHTML,
            };
            break;
          }
          case "REVIEW":{
            this.review = {
              id: 'review',
              threshold: i.children[0].innerHTML,
              file: i.children[1].innerHTML,
              relation: i.children[2].innerHTML,
            };
            break;
          }
          case "EXECUTION":{
            this.execution = {
              rewriter: i.children[0].innerHTML,
              planner: i.children[1].innerHTML,
              engine: i.children[2].innerHTML,
            };
            break;
          }
          case "OUTPUT":{
            this.output = {type: i.innerHTML};
            break;
          }
          defailt: {
            break;
          }
        }
      }   
      
    },
    generate_random_string(){
      let string_length = 10;
      let random_string = '';
      let random_ascii;
      for(let i = 0; i < string_length; i++) {
          random_ascii = Math.floor((Math.random() * 25) + 97);
          random_string += String.fromCharCode(random_ascii)
      }
      return random_string;
    },
    getSrcAndTgtFromMetric(metric, lastBeginAndEnd){
      let sourceBegin = metric.indexOf("s.",lastBeginAndEnd ? lastBeginAndEnd.sourceBegin+1 : 0);
      let sourceEnd = metric.indexOf(",t",lastBeginAndEnd ? lastBeginAndEnd.sourceEnd+1 : 0);
      let source = metric.substring(sourceBegin+2,sourceEnd);

      let targetBegin = metric.indexOf("t.",lastBeginAndEnd ? lastBeginAndEnd.targetBegin+1 : 0);
      let targetEnd = metric.indexOf(")",lastBeginAndEnd ? lastBeginAndEnd.targetEnd+1 : 0);
      let target = metric.substring(targetBegin+2,targetEnd);
      return {
        src: source, 
        tgt: target, 
        lastBeginAndEnd: {
          sourceBegin: sourceBegin,
          sourceEnd: sourceEnd,
          targetBegin: targetBegin,
          targetEnd: targetEnd,
        }
      }
    },
    creatingNewPreprocessingBlocklyXML(doc, functionName){
      // preprocessings
      var preprocessingsBlock = doc.createElement("block");
      preprocessingsBlock.setAttribute("type", "preprocessingfunction");
      preprocessingsBlock.setAttribute("id", this.generate_random_string());

      var preprocessingsField = doc.createElement("field");
      preprocessingsField.setAttribute("name", "function");
      preprocessingsField.innerHTML=functionName;

      var preprocessingsValue = doc.createElement("value");
      preprocessingsValue.setAttribute("name", "NAME");

      return {preprocessingsBlock: preprocessingsBlock,
        preprocessingsField: preprocessingsField,
        preprocessingsValue: preprocessingsValue};

    },
    creatingNewRenameBlocklyXML(doc, renameText){
        var renameBlock = doc.createElement("block");
        renameBlock.setAttribute("type", "renamepreprocessingfunction");
        renameBlock.setAttribute("id", this.generate_random_string());

        var renameField = doc.createElement("field");
        renameField.setAttribute("name", "RENAME");
        renameField.innerHTML=renameText;

        var renameValue = doc.createElement("value");
        renameValue.setAttribute("name", "RENAME");
        return {
          renameBlock: renameBlock,
          renameField: renameField,
          renameValue: renameValue,
        };
    },
    addPreprocessingsWithProperty(doc, valueSrcProp, srcProp, fieldSrcProp, props){
      let pr = props.split('AS')[1];
      let renameExists = props.split('RENAME')[1];
      if(pr){
      let prepArr = [];
      if(renameExists){
        prepArr = pr.split('RENAME')[0].split('-&gt;');
      }  else {
        prepArr = pr.split('-&gt;');
      }           
        if(prepArr.length > 1){
          let lastPrepItems;
          prepArr.forEach((prepf, index) => {
            let prepItems = this.creatingNewPreprocessingBlocklyXML(doc,prepf.trim());
            if(index === 0){
              if(renameExists){
                let renameItems = this.creatingNewRenameBlocklyXML(doc, renameExists.trim());
                valueSrcProp.appendChild(renameItems.renameBlock);
                renameItems.renameBlock.appendChild(renameItems.renameField);
                renameItems.renameBlock.appendChild(renameItems.renameValue);
                renameItems.renameValue.appendChild(prepItems.preprocessingsBlock);
                prepItems.preprocessingsBlock.appendChild(prepItems.preprocessingsField);
                prepItems.preprocessingsBlock.appendChild(prepItems.preprocessingsValue);
                lastPrepItems = prepItems;
              } else {
                valueSrcProp.appendChild(prepItems.preprocessingsBlock);
                prepItems.preprocessingsBlock.appendChild(prepItems.preprocessingsField);
                prepItems.preprocessingsBlock.appendChild(prepItems.preprocessingsValue);
                lastPrepItems = prepItems;
              }
            } else {
              if(index === prepArr.length-1){
                lastPrepItems.preprocessingsValue.appendChild(prepItems.preprocessingsBlock);
                prepItems.preprocessingsBlock.appendChild(prepItems.preprocessingsField);
                prepItems.preprocessingsBlock.appendChild(prepItems.preprocessingsValue);
                prepItems.preprocessingsValue.appendChild(srcProp);
                srcProp.appendChild(fieldSrcProp);
              } else {
                lastPrepItems.preprocessingsValue.appendChild(prepItems.preprocessingsBlock);
                prepItems.preprocessingsBlock.appendChild(prepItems.preprocessingsField);
                prepItems.preprocessingsBlock.appendChild(prepItems.preprocessingsValue);
                lastPrepItems = prepItems;
              }
            }
          });
        } else {
          if(renameExists){
            let prepItems = this.creatingNewRenameBlocklyXML(doc, renameExists.trim());
            valueSrcProp.appendChild(prepItems.renameBlock);
            prepItems.renameBlock.appendChild(prepItems.renameField);
            prepItems.renameBlock.appendChild(prepItems.renameValue);
            let preprocessingItems = this.creatingNewPreprocessingBlocklyXML(doc,prepArr[0].split("RENAME")[0]);
            prepItems.renameValue.appendChild(preprocessingItems.preprocessingsBlock);
            preprocessingItems.preprocessingsBlock.appendChild(preprocessingItems.preprocessingsField);
            preprocessingItems.preprocessingsBlock.appendChild(preprocessingItems.preprocessingsValue);
            preprocessingItems.preprocessingsValue.appendChild(srcProp);
            srcProp.appendChild(fieldSrcProp);
          } else {
            let prepItems = this.creatingNewPreprocessingBlocklyXML(doc,prepArr[0]);
            valueSrcProp.appendChild(prepItems.preprocessingsBlock);
            prepItems.preprocessingsBlock.appendChild(prepItems.preprocessingsField);
            prepItems.preprocessingsBlock.appendChild(prepItems.preprocessingsValue);
            prepItems.preprocessingsValue.appendChild(srcProp);
            srcProp.appendChild(fieldSrcProp);
          }
        }

      } else {
        if(renameExists){
          let prepItems = this.creatingNewRenameBlocklyXML(doc, renameExists.trim());
          valueSrcProp.appendChild(prepItems.renameBlock);
          prepItems.renameBlock.appendChild(prepItems.renameField);
          prepItems.renameBlock.appendChild(prepItems.renameValue);
          prepItems.renameValue.appendChild(srcProp);
          srcProp.appendChild(fieldSrcProp);
        } else {
          valueSrcProp.appendChild(srcProp);
          srcProp.appendChild(fieldSrcProp);
        }
      }
    },
    changeSrcOrTgtWithRename(props){ 
      let pr = props.split(" ");
      let pair = pr.filter(i => i.indexOf(":") !== -1);
      return pair;
    },
    convertMetricToBlocklyXML(metric){
      //cosine(s.rdfs:subClassOf,t.rdfs:range)
      //let metric = "cosine(s.rdfs:subClassOf,t.rdfs:range)";
      let operators = ['and','or','xor','nand'];
      let hasOperator = operators.filter( i => metric.toLowerCase().indexOf(i) !== -1);
      let hasMeasure = measures.filter( i => metric.indexOf(i.toLowerCase()) !== -1);
      
      let srcAndTgtFromMetric = this.getSrcAndTgtFromMetric(metric,null);
      let source = srcAndTgtFromMetric.src;
      let target = srcAndTgtFromMetric.tgt;
      let source2 = this.getSrcAndTgtFromMetric(metric,srcAndTgtFromMetric.lastBeginAndEnd).src;
      let target2 = this.getSrcAndTgtFromMetric(metric,srcAndTgtFromMetric.lastBeginAndEnd).tgt;
      
      if(this.source.properties[0].indexOf("RENAME") !== -1){
        source = this.changeSrcOrTgtWithRename(this.source.properties[0]);
      }
      if(this.source.properties[0].indexOf("RENAME") !== -1){
        target = this.changeSrcOrTgtWithRename(this.target.properties[0]);
      }
      if(this.source.properties[1] && this.source.properties[1].indexOf("RENAME") !== -1){
        source2 = this.changeSrcOrTgtWithRename(this.source.properties[1]);
      }
      if(this.source.properties[1] && this.source.properties[1].indexOf("RENAME") !== -1){
        target2 = this.changeSrcOrTgtWithRename(this.target.properties[1]); 
      }

      var doc = document.implementation.createDocument("http://www.w3.org/1999/xhtml", "", null);
      var xmlElem = doc.createElement("xml");
      xmlElem.setAttribute("xmlns", "http://www.w3.org/1999/xhtml");

      var startBlock = doc.createElement("block");
      startBlock.setAttribute("type", "start");
      startBlock.setAttribute("id", this.generate_random_string());
      startBlock.setAttribute("deletable", "false");
      startBlock.setAttribute("x", "0");
      startBlock.setAttribute("y", "0");

      var valueStart = doc.createElement("value");
      valueStart.setAttribute("name", "NAME");

      //operator
      var operatorBlock = doc.createElement("block");
      operatorBlock.setAttribute("type", "operator");
      operatorBlock.setAttribute("id", this.generate_random_string());

      var fieldOperator = doc.createElement("field");
      fieldOperator.setAttribute("name", "operators");
      fieldOperator.innerHTML= hasOperator[0];

      var valueOpM1 = doc.createElement("value");
      valueOpM1.setAttribute("name", "rename");//?

      var valueOpM2 = doc.createElement("value");
      valueOpM2.setAttribute("name", "NAME");//?

      //measure
      var measureBlock = doc.createElement("block");
      measureBlock.setAttribute("type", "measure");
      measureBlock.setAttribute("id", this.generate_random_string());

      var fieldMeasureList = doc.createElement("field");
      fieldMeasureList.setAttribute("name", "measureList");
      fieldMeasureList.innerHTML= hasMeasure[0];

      var fieldEnabledTh = doc.createElement("field");
      fieldEnabledTh.setAttribute("name", "enable_threshold");
      fieldEnabledTh.innerHTML = "FALSE";

      var fieldThreshold = doc.createElement("field");
      fieldThreshold.setAttribute("name", "threshold");
      fieldThreshold.innerHTML = "0.5";

      //measure2
      var measureBlock2 = doc.createElement("block");
      measureBlock2.setAttribute("type", "measure");
      measureBlock2.setAttribute("id", this.generate_random_string());

      var fieldMeasureList2 = doc.createElement("field");
      fieldMeasureList2.setAttribute("name", "measureList");
      fieldMeasureList2.innerHTML= hasMeasure[1] || hasMeasure[0];

      var fieldEnabledTh2 = doc.createElement("field");
      fieldEnabledTh2.setAttribute("name", "enable_threshold");
      fieldEnabledTh2.innerHTML = "FALSE";

      var fieldThreshold2 = doc.createElement("field");
      fieldThreshold2.setAttribute("name", "threshold");
      fieldThreshold2.innerHTML = "0.5";

      //src

      var valueSrcProp = doc.createElement("value");
      valueSrcProp.setAttribute("name", "sourceProperty");

      var srcProp = doc.createElement("block");
      srcProp.setAttribute("type", "sourceproperty");
      srcProp.setAttribute("id", this.generate_random_string());

      var fieldSrcProp = doc.createElement("field");
      fieldSrcProp.setAttribute("name", "propTitle");
      fieldSrcProp.innerHTML=source;

      //tgt

      var valueTgtProp = doc.createElement("value");
      valueTgtProp.setAttribute("name", "targetProperty");

      var tgtProp = doc.createElement("block");
      tgtProp.setAttribute("type", "targetproperty");
      tgtProp.setAttribute("id", this.generate_random_string());

      var fieldTgtProp = doc.createElement("field");
      fieldTgtProp.setAttribute("name", "propTitle");
      fieldTgtProp.innerHTML=target;

      //src2

      var valueSrcProp2 = doc.createElement("value");
      valueSrcProp2.setAttribute("name", "sourceProperty");

      var srcProp2 = doc.createElement("block");
      srcProp2.setAttribute("type", "sourceproperty");
      srcProp2.setAttribute("id", this.generate_random_string());

      var fieldSrcProp2 = doc.createElement("field");
      fieldSrcProp2.setAttribute("name", "propTitle");
      fieldSrcProp2.innerHTML=source2;

      //tgt2

      var valueTgtProp2 = doc.createElement("value");
      valueTgtProp2.setAttribute("name", "targetProperty");

      var tgtProp2 = doc.createElement("block");
      tgtProp2.setAttribute("type", "targetproperty");
      tgtProp2.setAttribute("id", this.generate_random_string());

      var fieldTgtProp2 = doc.createElement("field");
      fieldTgtProp2.setAttribute("name", "propTitle");
      fieldTgtProp2.innerHTML=target2;

      if(hasOperator.length === 0){
     
        xmlElem.appendChild(startBlock);
        startBlock.appendChild(valueStart);
        valueStart.appendChild(measureBlock);

        measureBlock.appendChild(fieldMeasureList);
        measureBlock.appendChild(fieldEnabledTh);
        measureBlock.appendChild(fieldThreshold);
        measureBlock.appendChild(valueSrcProp);
        measureBlock.appendChild(valueTgtProp);

        this.addPreprocessingsWithProperty(doc, valueSrcProp, srcProp, fieldSrcProp, this.source.properties[0]);
        this.addPreprocessingsWithProperty(doc, valueTgtProp, tgtProp, fieldTgtProp, this.target.properties[0]);

        doc.appendChild(xmlElem);
      } else {
        xmlElem.appendChild(startBlock);
        startBlock.appendChild(valueStart);

        valueStart.appendChild(operatorBlock);
        operatorBlock.appendChild(fieldOperator);
        operatorBlock.appendChild(valueOpM1);
        operatorBlock.appendChild(valueOpM2);

        valueOpM1.appendChild(measureBlock);
        measureBlock.appendChild(fieldMeasureList);
        measureBlock.appendChild(fieldEnabledTh);
        measureBlock.appendChild(fieldThreshold);
        measureBlock.appendChild(valueSrcProp);
        measureBlock.appendChild(valueTgtProp);

        this.addPreprocessingsWithProperty(doc, valueSrcProp, srcProp, fieldSrcProp, this.source.properties[0]);
        this.addPreprocessingsWithProperty(doc, valueTgtProp, tgtProp, fieldTgtProp, this.target.properties[0]);

        valueOpM2.appendChild(measureBlock2);
        measureBlock2.appendChild(fieldMeasureList2);
        measureBlock2.appendChild(fieldEnabledTh2);
        measureBlock2.appendChild(fieldThreshold2);
        measureBlock2.appendChild(valueSrcProp2);
        measureBlock2.appendChild(valueTgtProp2);

        this.addPreprocessingsWithProperty(doc, valueSrcProp2, srcProp2, fieldSrcProp2, this.source.properties[1]);
        this.addPreprocessingsWithProperty(doc, valueTgtProp2, tgtProp2, fieldTgtProp2, this.target.properties[1]);

        doc.appendChild(xmlElem);
      }

      
      //console.log((new XMLSerializer()).serializeToString(doc));
      this.xmlToWorkspace((new XMLSerializer()).serializeToString(doc));

    },

    generateConfig() {
      const configHeader = `<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE LIMES SYSTEM "limes.dtd">
<LIMES>
`;
      const configFooter = `</LIMES>`;
      const prefixes = this.prefixes
        .map(
          p => `<PREFIX>
  <NAMESPACE>${p.namespace}</NAMESPACE>
  <LABEL>${p.label}</LABEL>
</PREFIX>
`
        )
        .join('');

      const src = makeDatasource(this.source, 'SOURCE');
      const target = makeDatasource(this.target, 'TARGET');

      const metrics = !this.mlalgorithm.enabled ? this.metrics
        .map(
          m => `<METRIC>
  ${m}
</METRIC>
`
        )
        .join('') : '';

      const acceptance = makeAccReview(this.acceptance, 'ACCEPTANCE');
      const review = makeAccReview(this.review, 'REVIEW');
      const ml = this.mlalgorithm.enabled
        ? `<MLALGORITHM>
  <NAME>${this.mlalgorithm.name}</NAME>
  <TYPE>${this.mlalgorithm.type}</TYPE>
  ${(this.mlalgorithm.type==='supervised batch' || this.mlalgorithm.type==='supervised active') ? `<TRAINING>${this.mlalgorithm.training}</TRAINING>` : ''}
  ${this.mlalgorithm.parameters
    .map(
      p => `<PARAMETER>
  <NAME>${p.name}</NAME>
  <VALUE>${p.value}</VALUE>
</PARAMETER>`
    )
    .join('\n  ')}
</MLALGORITHM>
`
        : '';

      const execution = `<EXECUTION>
  <REWRITER>${this.execution.rewriter}</REWRITER>
  <PLANNER>${this.execution.planner}</PLANNER>
  <ENGINE>${this.execution.engine}</ENGINE>
</EXECUTION>
`;

      const output = `<OUTPUT>${this.output.type}</OUTPUT>
`;

      const config =
        configHeader + prefixes + src + target + metrics + ml + acceptance + review  + execution + output + configFooter;
      return config;
    },
    showConfig() {
      // generate new config
      const cfg = this.generateConfig();
      // highlight the code
      const h = hljs.highlight('xml', cfg);
      // set text to highlighted html
      this.configText = h.value;
      // show dialog
      this.$refs.configDialog.open();
    },
    closeConfig() {
      this.$refs.configDialog.close();
    },
    showDialogForPreviousRun(){
      this.isDisabledLog = true;
      this.isDisabled = true;
      this.notFoundKeyMessage = '';
      this.executedKey = '';
      this.findStatusMessage = '';
      this.$refs.previousRunDialog.open();
    },
    closePreviousRun(){
      this.$refs.previousRunDialog.close();
    },
    checkExecutedKey(){
      this.isDisabledLog = true;
      this.isDisabled = true;
      this.notFoundKeyMessage = '';
      this.getStatusOfCertainJob();
    },
    getStatusOfCertainJob(){
      fetch(window.JOB_STATUS+this.executedKey)
      .then(r => r.json().then(x => x.status))
      .then(status => {
        if (status.code === -1) {
          this.findStatusMessage = status.description;
        }
        if (status.code === 0) {
          this.findStatusMessage = status.description;
          setTimeout(() => this.getStatusOfCertainJob(), 5000);
        }
        if (status.code === 1) {
          this.findStatusMessage = status.description;
          setTimeout(() => this.getStatusOfCertainJob(), 5000);
        }
        if (status.code === 2) {
          this.findStatusMessage = status.description;
          this.getResultsOfCertainJob();
        }
      })
    },
    getResultsOfCertainJob(){
      fetch(window.RESULT_FILES+this.executedKey)
              .then(function(response) {
                return response.json();
               })
              .then((content) => {
                this.isDisabledLog = false;
                if(content.success && content.availableFiles.length){
                  this.availableFiles.splice(0);
                  this.availableFiles = content.availableFiles;
                  this.isDisabled = false;
                } 
              })
    },
    saveAccepted(){
      if(this.availableFiles.length){
        fetch(window.RESULT_FILE+this.executedKey+"/"+this.availableFiles[1])
        .then(function(response) {
          return response.text();
         })
        .then((content) => {
          this.forceFileDownload(content,this.availableFiles[1]);
        })
      }
    },
    saveReviewed(){
      if(this.availableFiles.length){
        fetch(window.RESULT_FILE+this.executedKey+"/"+this.availableFiles[0])
        .then(function(response) {
          return response.text();
         })
        .then((content) => {
          this.forceFileDownload(content,this.availableFiles[0]);
        })
      }
    },
    forceFileDownload(str, filename){
      const url = window.URL.createObjectURL(new Blob([str]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', filename) //or any other extension
      document.body.appendChild(link)
      link.click()
    },
    saveXML(){
      this.forceFileDownload(this.generateConfig(),'file.xml');
    },
    closeStatus() {
      history.pushState({}, '', '?');
      this.$refs.jobDialog.close();
    },
    execute() {
      this.jobError = false;
      const config = this.generateConfig();
      const configBlob = new Blob([config], {type: 'text/xml'});
      const fd = new FormData();
      fd.append('config_file', configBlob, 'config.xml');
      fetch(window.LIMES_SERVER_URL + '/submit', {
        method: 'post',
        body: fd,
      })
        .then(r => r.json().then(x => x.requestId))
        .then(r => {
          this.jobId = r;
          this.jobRunning = true;
          this.jobError = false;
          this.c = 'Status Loading - waiting for status from server..';
          this.$refs.jobDialog.open();
          history.pushState({jobId: r}, '', `?jobId=${r}`);
          setTimeout(() => this.getStatus(), 1000);
        })
        .catch(e => {
          this.jobError = `Error while starting the job: ${e.toString()}`;
          this.jobRunning = false;
        });
    },
    getStatus() {
      this.jobError = false;
      fetch(window.LIMES_SERVER_URL + '/status/' + this.jobId)
        .then(r => r.json().then(x => x.status))
        .then(status => {
          this.jobError = false;
          this.jobStatus = status.code;
          if (status.code === -1) {
            this.jobRunning = false;
            this.jobStatusText = status.description;
          }
          if (status.code === 0) {
            this.jobRunning = true;
            this.jobStatusText = status.description;
            setTimeout(() => this.getStatus(), 5000);
          }
          if (status.code === 1) {
            this.jobRunning = true;
            this.jobStatusText = status.description;
            setTimeout(() => this.getStatus(), 5000);
          }
          if (status.code === 2) {
            this.jobRunning = false;
            this.jobStatusText = status.description;
            this.getResults();
          }
        })
        .catch(e => {
          this.jobError = `Error getting job status: ${e.toString()}`;
          this.jobRunning = false;
        });
    },
    getResults() {
      this.jobError = false;
      fetch(window.LIMES_SERVER_URL + '/results/' + this.jobId)
        .then(r => r.json())
        .then(results => {
          this.results = results.availableFiles;
          this.jobShowResult = true;
        })
        .catch(e => {
          this.jobError = `Error getting job results: ${e.toString()}`;
        });
    },
    xmlToWorkspace(str){
      var xml = Blockly.Xml.textToDom(str);
      if(str.indexOf('type="start"')!== -1){
        this.Workspace.getBlocksByType("start")[0].dispose(true);
      }
      Blockly.Xml.domToWorkspace(xml, this.Workspace);
    },
    exampleConfig() {
      this.exampleConfigEnable = true;
      this.prefixes = [
        {
          namespace: 'http://geovocab.org/geometry#',
          label: 'geom',
        },
        {
          namespace: 'http://www.opengis.net/ont/geosparql#',
          label: 'geos',
        },
        {
          namespace: 'http://linkedgeodata.org/ontology/',
          label: 'lgdo',
        },
        {
          namespace: 'http://www.w3.org/2000/01/rdf-schema#',
          label: 'rdfs',
        },
      ];
      this.source = {
        id: 'sourceId',
        endpoint: 'http://linkedgeodata.org/sparql',
        var: '?s',
        pagesize: 2000,
        restriction: '?s a lgdo:RelayBox',
        type: '',
        properties: ['geom:geometry/geos:asWKT RENAME polygon'],
        optionalProperties: ['rdfs:label'],
        classes: ['http://linkedgeodata.org/ontology/RelayBox'],
        propertiesForChoice: [],
      };
      this.target = {
        id: 'targetId',
        endpoint: 'http://linkedgeodata.org/sparql',
        var: '?t',
        pagesize: 2000,
        restriction: '?t a lgdo:RelayBox',
        type: '',
        properties: ['geom:geometry/geos:asWKT RENAME polygon'],
        optionalProperties: ['rdfs:label'],
        classes: ['http://linkedgeodata.org/ontology/RelayBox'],
        propertiesForChoice: [],
      };
      this.importWorkspaceString = `
      <xml xmlns="http://www.w3.org/1999/xhtml">
        <block type="start" id="]~iOOuxgG1il)Qn#!5@R" deletable="false" x="0" y="0">
          <value name="NAME">
            <block type="measure" id="KV+e%A!n/j5k2T@D@*GH">
              <field name="measureList">Geo_Hausdorff</field>
              <field name="enable_threshold">FALSE</field>
              <field name="threshold">0.5</field>
              <value name="sourceProperty">
                <block type="renamepreprocessingfunction" id="$J8bghZmrI;ETkue6mwG">
                  <field name="RENAME">polygon</field>
                  <value name="RENAME">
                    <block type="sourceproperty" id="sp">
                      <field name="propTitle">geom:geometry/geos:asWKT</field>
                    </block>
                  </value>
                </block>
              </value>
              <value name="targetProperty">
                <block type="renamepreprocessingfunction" id="rpf">
                  <field name="RENAME">polygon</field>
                  <value name="RENAME">
                    <block type="targetproperty" id="tg">
                      <field name="propTitle">geom:geometry/geos:asWKT</field>
                    </block>
                  </value>
                </block>
              </value>
            </block>
          </value>
        </block>
      </xml>
      `;
      this.xmlToWorkspace(this.importWorkspaceString);
      this.metrics = ['geo_hausdorff(s.polygon, t.polygon)'];
      this.acceptance = {
        id: 'acceptance',
        threshold: 0.9,
        file: 'lgd_relaybox_verynear.nt',
        relation: 'lgdo:near',
      };
      this.review = {
        id: 'review',
        threshold: 0.5,
        file: 'lgd_relaybox_near.nt',
        relation: 'lgdo:near',
      };
      this.execution = {
        rewriter: 'DEFAULT',
        planner: 'DEFAULT',
        engine: 'DEFAULT',
      };
      this.output = {type: 'TAB'};
    },
  },
});

function showThreshold(i){
  let isShown = false;
  if(i){
    if(i.getField("enable_threshold").getValue().toLowerCase() === 'true'){
      i.getField("threshold").setVisible(true);
      i.getField("threshold").forceRerender();
      isShown = true;
    } else {
      i.getField("threshold").setVisible(false);
      i.getField("threshold").forceRerender();
      isShown = false;
    }
  }
  return isShown;
}
