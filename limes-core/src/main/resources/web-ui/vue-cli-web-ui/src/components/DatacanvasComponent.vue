<template>
	<md-layout>
      <div id="blocklyArea" style="height: 500px; width: 100%;"></div>
      <div id="blocklyDiv" style="position: absolute"></div>
	    <xml id="toolbox" ref=toolbox style="display: none"> 
	          <block type="sourceproperty"></block>
	          <block type="targetproperty"></block>
	          <block type="optionalsourceproperty"></block>
	          <block type="optionaltargetproperty"></block>
	          <block type="measure"></block>
	          <block type="operator"></block>
	          <block type="preprocessingfunction"></block>
	          <block type="renamepreprocessingfunction"></block>
	    </xml> 
    </md-layout>
</template>

<script>
import MeasureComponent from './MeasureComponent.vue'	
import Preprocessings from './Preprocessings.vue'
export default {
	mixins: [Preprocessings, MeasureComponent],
	components: {
		Preprocessings
	},
	props: {
	    metrics: {
	      type: Array,
	    },
	    deleteOldPrefixes:{
	      type: Function,
	    }
	},
  data () {
    return {
    	Workspace: null,
    	measureOptions: [],
    	typesOfBlocks: {
	      'start': Start,
        'emptyBlock': emptyBlock,
        'propertyPath': propertyPath,
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
    }
  },
  beforeMount() {

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
    this.$store.state.measureOptions.forEach(i => {
      Measure.args0[0].options.push(i);
    });  

    let params = Object.keys(MLParameters.WOMBAT).map(function(i) {
      return {
        name: i,
        value: MLParameters.WOMBAT[i].default || MLParameters.WOMBAT[i],
      } 
    });

  },
  mounted() {
    let blocklyInit = () => {
      //this.convertMetricToBlocklyXML();
          // var this.Workspace = Blockly.inject('blocklyDiv',
          //   {toolbox: document.getElementById('toolbox')});
          var blocklyArea = document.getElementById('blocklyArea');
          var blocklyDiv = document.getElementById('blocklyDiv');
          // var this.Workspace = this.this.Workspace;
          this.Workspace = Blockly.inject(blocklyDiv,
              {toolbox: document.getElementById('toolbox'),
              //scrollbars: true,
               zoom:
                 {controls: true,
                  wheel: true,
                  startScale: 1.0,
                  maxScale: 3,
                  minScale: 0.3,
                  scaleSpeed: 1.2},
                trashcan: true,
              });
          //console.log(this.Workspace);
          //new Blockly.ScrollbarPair(this.Workspace);
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
            console.log("change");


            // this.$store.state.source.properties.splice(0);
            // this.$store.state.target.properties.splice(0);
            // this.$store.state.source.optionalProperties.splice(0);
            // this.$store.state.target.optionalProperties.splice(0);

            let allBlocks = this.Workspace.getTopBlocks();
            //console.log(this.Workspace.getAllBlocks());

            allBlocks.forEach( block => {
              this.notConnectedToStart = false;
              if(block.type === 'start'){
                block.getChildren().forEach(i => { 
                  //this.processingPropertyWithPrepFunc(i);
                  i.setDisabled(false);
                  switch(i.type) {

                    case "measure": {

                      let shownM = this.showThreshold(i);
                      this.$store.commit('removeProps');
                      let properties = this.getFromMeasure(i,0);

                      let threshold = i.getField("threshold").text_;
                      let measureFunc = i.getField("measureList").getDisplayText_();
                      this.metrics.splice(0);
                      if(shownM){
                        this.metrics.push(measureFunc.toLowerCase()+"("+ this.$store.state.source.var.substr(1)+"."+properties.src+","+ this.$store.state.target.var.substr(1)+"." +properties.tgt+")|"+threshold);
                      } else {
                        this.metrics.push(measureFunc.toLowerCase()+"("+this.$store.state.source.var.substr(1)+"."+properties.src+","+this.$store.state.target.var.substr(1)+"."+properties.tgt+")"); 
                      }
            
                      break;
                    }

                    case "operator": {
                      let firstM = i.getChildren()[0];
                      let secondM = i.getChildren()[1];

                      this.mainSource = false;
                      this.mainTarget = false;

                      let props1 = "", props2 = "";
                      this.$store.commit('removeProps');
                      if(firstM && firstM.type === "measure"){
                        props1 = this.getFromMeasure(firstM,0);
                      }
                      if(secondM && secondM.type === "measure"){
                        props2 = this.getFromMeasure(secondM,1);
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
                      let shownFirstM = this.showThreshold(firstM);
                      let shownSecondM = this.showThreshold(secondM);

                      let operator = i.getField("operators").text_;
                      let measureFunc1 = firstM ? firstM.getField("measureList").getDisplayText_() : "undefined";
                      let measureFunc2 = secondM ? secondM.getField("measureList").getDisplayText_() : "undefined";
                      let threshold1 = firstM ? firstM.getField("threshold").text_ : "undefined";
                      let threshold2 = secondM ? secondM.getField("threshold").text_ : "undefined";
                      let varSrc = this.$store.state.source.var.substr(1)+".";
                      let varTgt = this.$store.state.target.var.substr(1)+".";
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



                      } 
                      // else {
                     
                      //   let str1 = 'NOT(' + operator.substr(1) + '(' + measureFunc1.toLowerCase()+"("+varSrc+props1.src+","+varTgt+props1.tgt+")"; 
                      //   let str2 = ',' + measureFunc2.toLowerCase()+"("+varSrc+props2.src+","+varTgt+props2.tgt+")";  
                      //   if(shownFirstM && shownSecondM){
                      //     this.metrics.push(str1 + "|" + threshold1 + str2 + "|"+threshold2 + ')))');
                      //   } else {

                      //     if(!shownFirstM && !shownSecondM){
                      //       this.metrics.push(str1 + str2 + ')))');
                      //     }

                      //     if(shownFirstM && !shownSecondM){
                      //       this.metrics.push(str1 + "|" + threshold1 + str2 + ')))');
                      //     } else if(!shownFirstM && shownSecondM) {
                      //       this.metrics.push(str1 + str2 + "|"+threshold2 + ')))');
                      //     }
                      //   }

                      // }




                      break;
                    }

                    default: {
                    }
                  }


                  // this.deleteOldPrefixes();
                
                })
              } else {
                
                if(block.type.indexOf('preprocessing')!== -1){
                  this.notConnectedToStart = true;
                  this.processingPropertyWithPrepFunc(block);
                }
                block.setDisabled(true);
              }
            });

          this.$store.commit('changeWorkspace', this.Workspace);

          }
          this.Workspace.addChangeListener(onFirstComment);

          
    };
    setTimeout(blocklyInit, 500);
  },
	methods: {
		// addTodo () {

		// 	const trimmedText = this.newTodoText.trim()
		// 	if (trimmedText) {
		// 		this.todos.push({
		// 			id: nextTodoId++,
		// 			text: trimmedText
		// 		})
		// 		this.newTodoText = ''
		// 	}
		// 	console.log(this.todos);
		// },
		// removeTodo (idToRemove) {
		// 	this.todos = this.todos.filter(todo => {
		// 		return todo.id !== idToRemove
		// 	})
		// }
	}
}
</script>
