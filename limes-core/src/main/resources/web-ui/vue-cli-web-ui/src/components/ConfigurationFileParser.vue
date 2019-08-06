<template>

</template>

<script>
import SharedMethods from './SharedMethods.vue'
export default {
  mixins: [SharedMethods],
  props: {
  },
  methods: {
    importConfigurationFileFunction(){
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
              let source = {
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
              
      
              if(i.children[6] && i.children[6].tagName === "PROPERTY"){
                source.properties.splice(0);
                source.properties.push(i.children[5].innerHTML);
                source.properties.push(i.children[6].innerHTML);
                source.type = i.children[7].innerHTML;
              } else {
                source.properties.splice(0);
                source.properties.push(i.children[5].innerHTML);
                source.type = i.children[6] ? i.children[6].innerHTML : '';
              }

              this.$store.commit('changeSource', source);
              break;
            }
            case "TARGET":{
              let target = {
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

              if( i.children[6] && i.children[6].tagName === "PROPERTY"){
                target.properties.splice(0);
                target.properties.push(i.children[5].innerHTML);
                target.properties.push(i.children[6].innerHTML);
                target.type = i.children[7].innerHTML;
              } else {
                target.properties.splice(0);
                target.properties.push(i.children[5].innerHTML);
                target.type = i.children[6] ? i.children[6].innerHTML : '';
              }
              this.$store.commit('changeTarget', target);
              break;
            }
            case "METRIC":{
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
            default: {
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

      formPropertyPath(tagObj){
        // property path
        tagObj.srcProp.appendChild(tagObj.fieldSrcPropPath);
        tagObj.srcProp.appendChild(tagObj.valueSrcPropPath);
        tagObj.valueSrcPropPath.appendChild(tagObj.blockSrcPropPath);
        tagObj.blockSrcPropPath.appendChild(tagObj.fieldSrcPath);
        tagObj.blockSrcPropPath.appendChild(tagObj.fieldSrcPathPropTitle);
        tagObj.blockSrcPropPath.appendChild(tagObj.fieldSrcPropPathEnable); 
        tagObj.blockSrcPropPath.appendChild(tagObj.valueEmpty);
        tagObj.valueEmpty.appendChild(tagObj.blockEmpty);
      },

      addPreprocessingsWithProperty(tagObj){
        let pr = tagObj.props.split('AS')[1];
        let renameExists = tagObj.props.split('RENAME')[1];
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
              let prepItems = this.creatingNewPreprocessingBlocklyXML(tagObj.doc,prepf.trim());
              if(index === 0){
                if(renameExists){
                  let renameItems = this.creatingNewRenameBlocklyXML(tagObj.doc, renameExists.trim());
                  tagObj.valueSrcProp.appendChild(renameItems.renameBlock);
                  renameItems.renameBlock.appendChild(renameItems.renameField);
                  renameItems.renameBlock.appendChild(renameItems.renameValue);
                  renameItems.renameValue.appendChild(prepItems.preprocessingsBlock);
                  prepItems.preprocessingsBlock.appendChild(prepItems.preprocessingsField);
                  prepItems.preprocessingsBlock.appendChild(prepItems.preprocessingsValue);
                  lastPrepItems = prepItems;
                } else {
                  tagObj.valueSrcProp.appendChild(prepItems.preprocessingsBlock);
                  prepItems.preprocessingsBlock.appendChild(prepItems.preprocessingsField);
                  prepItems.preprocessingsBlock.appendChild(prepItems.preprocessingsValue);
                  lastPrepItems = prepItems;
                }
              } else {
                if(index === prepArr.length-1){
                  lastPrepItems.preprocessingsValue.appendChild(prepItems.preprocessingsBlock);
                  prepItems.preprocessingsBlock.appendChild(prepItems.preprocessingsField);
                  prepItems.preprocessingsBlock.appendChild(prepItems.preprocessingsValue);
                  prepItems.preprocessingsValue.appendChild(tagObj.srcProp);
                  tagObj.srcProp.appendChild(tagObj.fieldSrcProp);
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
              let prepItems = this.creatingNewRenameBlocklyXML(tagObj.doc, renameExists.trim());
              tagObj.valueSrcProp.appendChild(prepItems.renameBlock);
              prepItems.renameBlock.appendChild(prepItems.renameField);
              prepItems.renameBlock.appendChild(prepItems.renameValue);
              let preprocessingItems = this.creatingNewPreprocessingBlocklyXML(tagObj.doc,prepArr[0].split("RENAME")[0]);
              prepItems.renameValue.appendChild(preprocessingItems.preprocessingsBlock);
              preprocessingItems.preprocessingsBlock.appendChild(preprocessingItems.preprocessingsField);
              preprocessingItems.preprocessingsBlock.appendChild(preprocessingItems.preprocessingsValue);
              preprocessingItems.preprocessingsValue.appendChild(tagObj.srcProp);
              tagObj.srcProp.appendChild(tagObj.fieldSrcProp);

              this.formPropertyPath(tagObj);
            } else {
              let prepItems = this.creatingNewPreprocessingBlocklyXML(tagObj.doc,prepArr[0]);
              tagObj.valueSrcProp.appendChild(prepItems.preprocessingsBlock);
              prepItems.preprocessingsBlock.appendChild(prepItems.preprocessingsField);
              prepItems.preprocessingsBlock.appendChild(prepItems.preprocessingsValue);
              prepItems.preprocessingsValue.appendChild(tagObj.srcProp);
              tagObj.srcProp.appendChild(tagObj.fieldSrcProp);
              this.formPropertyPath(tagObj);
            }
          }

        } else {
          if(renameExists){
            let prepItems = this.creatingNewRenameBlocklyXML(tagObj.doc, renameExists.trim());
            tagObj.valueSrcProp.appendChild(prepItems.renameBlock);
            prepItems.renameBlock.appendChild(prepItems.renameField);
            prepItems.renameBlock.appendChild(prepItems.renameValue);
            prepItems.renameValue.appendChild(tagObj.srcProp);
            tagObj.srcProp.appendChild(tagObj.fieldSrcProp);

            this.formPropertyPath(tagObj);
          } else {
            tagObj.valueSrcProp.appendChild(tagObj.srcProp);
            tagObj.srcProp.appendChild(tagObj.fieldSrcProp);

            this.formPropertyPath(tagObj);
          }
        }
      },

      changeSrcOrTgtWithRename(props){ 
        let pr = props.split(" ");
        let pair = pr.filter(i => i.indexOf(":") !== -1);
        return pair;
      },

      creatingNewDataWithPropertyPath(doc, source, hasPropertyPathS, sourcePropertyPath, propertyName){
        var valueSrcProp = doc.createElement("value");
        valueSrcProp.setAttribute("name", propertyName);

        var srcProp = doc.createElement("block");
        srcProp.setAttribute("type", propertyName.toLowerCase());
        srcProp.setAttribute("id", this.generate_random_string());

        var fieldSrcProp = doc.createElement("field");
        fieldSrcProp.setAttribute("name", "propTitle");
        fieldSrcProp.innerHTML=source;

        // source property path

        var fieldSrcPropPath = doc.createElement("field");
        fieldSrcPropPath.setAttribute("name", "enable_propertypath");
        fieldSrcPropPath.innerHTML=hasPropertyPathS;

        var valueSrcPropPath = doc.createElement("value");
        valueSrcPropPath.setAttribute("name", "propName");

        var blockSrcPropPath = doc.createElement("block");
        blockSrcPropPath.setAttribute("type", "propertyPath");
        blockSrcPropPath.setAttribute("id", this.generate_random_string());
        blockSrcPropPath.setAttribute("movable", "true");

        var fieldSrcPath = doc.createElement("field");
        fieldSrcPath.setAttribute("name", "path");
        fieldSrcPath.innerHTML="sslash";

        var fieldSrcPathPropTitle = doc.createElement("field");
        fieldSrcPathPropTitle.setAttribute("name", "propTitle");
        fieldSrcPathPropTitle.innerHTML=sourcePropertyPath[0]; // so far for one level

        var fieldSrcPropPathEnable = doc.createElement("field");
        fieldSrcPropPathEnable.setAttribute("name", "enable_propertypath");
        fieldSrcPropPathEnable.innerHTML="false"; // so far for one level

        var valueEmpty = doc.createElement("value");
        valueEmpty.setAttribute("name", "propertyPath");

        var blockEmpty = doc.createElement("block");
        blockEmpty.setAttribute("type", "emptyBlock");
        blockEmpty.setAttribute("id", this.generate_random_string());
        blockEmpty.setAttribute("movable", "false");

        return {
          "valueSrcProp": valueSrcProp, 
          "srcProp": srcProp, 
          "fieldSrcProp": fieldSrcProp, 

          "fieldSrcPropPath": fieldSrcPropPath,
          "valueSrcPropPath": valueSrcPropPath,
          "blockSrcPropPath": blockSrcPropPath,
          "fieldSrcPath": fieldSrcPath,
          "fieldSrcPathPropTitle": fieldSrcPathPropTitle,
          "fieldSrcPropPathEnable": fieldSrcPropPathEnable,
          "valueEmpty": valueEmpty,
          "blockEmpty": blockEmpty,
        };
      },

      convertMetricToBlocklyXML(metric){
        //cosine(s.rdfs:subClassOf,t.rdfs:range)
        //let metric = "cosine(s.rdfs:subClassOf,t.rdfs:range)";
        //let operators = ['and','or','minus','xor'];//['and','or','xor','nand'];
        let hasOperator = this.$store.state.operators.filter( i => 
          {
            if(metric.toLowerCase().indexOf(i.toLowerCase()) !== -1){
              let ind = metric.toLowerCase().indexOf(i)+i.length;
              if(metric[ind] === '('){
                return i;
              }
            }
          });
        let hasMeasure = this.$store.state.measures.filter( i => metric.toLowerCase().indexOf(i.toLowerCase()) !== -1);

        let srcAndTgtFromMetric = this.getSrcAndTgtFromMetric(metric,null);
        let source = srcAndTgtFromMetric.src;
        let target = srcAndTgtFromMetric.tgt;
        let source2 = this.getSrcAndTgtFromMetric(metric,srcAndTgtFromMetric.lastBeginAndEnd).src;
        let target2 = this.getSrcAndTgtFromMetric(metric,srcAndTgtFromMetric.lastBeginAndEnd).tgt;

        let hasPropertyPathS = metric.indexOf('/') !== -1;
        let hasPropertyPathT = metric.indexOf('/') !== -1;
        let sourcePropertyPath = [];
        let targetPropertyPath = [];
        let hasPropertyPathS2 = metric.indexOf('/') !== -1;
        let hasPropertyPathT2 = metric.indexOf('/') !== -1;
        let sourcePropertyPath2 = [];
        let targetPropertyPath2 = [];

        
        if(this.$store.state.source.properties[0].indexOf("RENAME") !== -1 || this.$store.state.source.properties[0].indexOf("AS") !== -1){
          source = this.changeSrcOrTgtWithRename(this.$store.state.source.properties[0]);
          if(this.$store.state.source.properties[0].indexOf('/') !== -1){
            hasPropertyPathS = true;
            let arr = source[0].split("/");
            sourcePropertyPath = arr;
            source = sourcePropertyPath.shift();
          }
        }
        if(this.$store.state.target.properties[0].indexOf("RENAME") !== -1 || this.$store.state.target.properties[0].indexOf("AS") !== -1){
          target = this.changeSrcOrTgtWithRename(this.$store.state.target.properties[0]);
          if(this.$store.state.target.properties[0].indexOf('/') !== -1){
            hasPropertyPathT = true;
            let arr = target[0].split("/");
            targetPropertyPath = arr;
            target = targetPropertyPath.shift();
          }
        }
        if(this.$store.state.source.properties[1] 
          && (this.$store.state.source.properties[1].indexOf("RENAME") !== -1 
            || this.$store.state.source.properties[1].indexOf("AS") !== -1)){
          source2 = this.changeSrcOrTgtWithRename(this.$store.state.source.properties[1]);
          if(this.$store.state.source.properties[1].indexOf('/') !== -1){
            hasPropertyPathS2 = true;
            let arr = source2[1].split("/");
            sourcePropertyPath2 = arr;
            source2 = sourcePropertyPath2.shift();
          }
        }
        if(this.$store.state.source.properties[1] 
          && (this.$store.state.source.properties[1].indexOf("RENAME") !== -1
            || this.$store.state.source.properties[1].indexOf("AS") !== -1)){
          target2 = this.changeSrcOrTgtWithRename(this.$store.state.target.properties[1]); 
          if(this.$store.state.target.properties[1].indexOf('/') !== -1){
            hasPropertyPathT2 = true;
            let arr = target[1].split("/");
            targetPropertyPath2 = arr;
            target2 = targetPropertyPath2.shift();
          }
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
        let src = this.creatingNewDataWithPropertyPath(doc, source, hasPropertyPathS, sourcePropertyPath, "sourceProperty");
        let sourceTagsObj = {
          "doc": doc, 
          "valueSrcProp": src.valueSrcProp, 
          "srcProp": src.srcProp, 
          "fieldSrcProp": src.fieldSrcProp, 
          "props": this.$store.state.source.properties[0],

          "fieldSrcPropPath": src.fieldSrcPropPath,
          "valueSrcPropPath": src.valueSrcPropPath,
          "blockSrcPropPath": src.blockSrcPropPath,
          "fieldSrcPath": src.fieldSrcPath,
          "fieldSrcPathPropTitle": src.fieldSrcPathPropTitle,
          "fieldSrcPropPathEnable": src.fieldSrcPropPathEnable,
          "valueEmpty": src.valueEmpty,
          "blockEmpty": src.blockEmpty,
        };

        //tgt
        let tgt = this.creatingNewDataWithPropertyPath(doc, target, hasPropertyPathT, targetPropertyPath, "targetProperty");
        let targetTagsObj = {
          "doc": doc, 
          "valueSrcProp": tgt.valueSrcProp, 
          "srcProp": tgt.srcProp, 
          "fieldSrcProp": tgt.fieldSrcProp, 
          "props": this.$store.state.target.properties[0],

          "fieldSrcPropPath": tgt.fieldSrcPropPath,
          "valueSrcPropPath": tgt.valueSrcPropPath,
          "blockSrcPropPath": tgt.blockSrcPropPath,
          "fieldSrcPath": tgt.fieldSrcPath,
          "fieldSrcPathPropTitle": tgt.fieldSrcPathPropTitle,
          "fieldSrcPropPathEnable": tgt.fieldSrcPropPathEnable,
          "valueEmpty": tgt.valueEmpty,
          "blockEmpty": tgt.blockEmpty,
        };

        //src2

        let src2 = this.creatingNewDataWithPropertyPath(doc, source2, hasPropertyPathS2, sourcePropertyPath2, "sourceProperty");
        let sourceTagsObj2 = {
          "doc": doc, 
          "valueSrcProp": src2.valueSrcProp, 
          "srcProp": src2.srcProp, 
          "fieldSrcProp": src2.fieldSrcProp, 
          "props": this.$store.state.source.properties[0],

          "fieldSrcPropPath": src2.fieldSrcPropPath,
          "valueSrcPropPath": src2.valueSrcPropPath,
          "blockSrcPropPath": src2.blockSrcPropPath,
          "fieldSrcPath": src2.fieldSrcPath,
          "fieldSrcPathPropTitle": src2.fieldSrcPathPropTitle,
          "fieldSrcPropPathEnable": src2.fieldSrcPropPathEnable,
          "valueEmpty": src2.valueEmpty,
          "blockEmpty": src2.blockEmpty,
        };

        //tgt2

        let tgt2 = this.creatingNewDataWithPropertyPath(doc, target2, hasPropertyPathT2, targetPropertyPath2, "targetProperty");
        let targetTagsObj2 = {
          "doc": doc, 
          "valueSrcProp": tgt2.valueSrcProp, 
          "srcProp": tgt2.srcProp, 
          "fieldSrcProp": tgt2.fieldSrcProp, 
          "props": this.$store.state.target.properties[0],

          "fieldSrcPropPath": tgt2.fieldSrcPropPath,
          "valueSrcPropPath": tgt2.valueSrcPropPath,
          "blockSrcPropPath": tgt2.blockSrcPropPath,
          "fieldSrcPath": tgt2.fieldSrcPath,
          "fieldSrcPathPropTitle": tgt2.fieldSrcPathPropTitle,
          "fieldSrcPropPathEnable": tgt2.fieldSrcPropPathEnable,
          "valueEmpty": tgt2.valueEmpty,
          "blockEmpty": tgt2.blockEmpty,
        };


        if(hasOperator.length === 0){
       
          xmlElem.appendChild(startBlock);
          startBlock.appendChild(valueStart);
          valueStart.appendChild(measureBlock);

          measureBlock.appendChild(fieldMeasureList);
          measureBlock.appendChild(fieldEnabledTh);
          measureBlock.appendChild(fieldThreshold);
          measureBlock.appendChild(src.valueSrcProp);
          measureBlock.appendChild(tgt.valueSrcProp);

          this.addPreprocessingsWithProperty(sourceTagsObj);
          this.addPreprocessingsWithProperty(targetTagsObj);

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
          measureBlock.appendChild(src.valueSrcProp);
          measureBlock.appendChild(tgt.valueSrcProp);

          this.addPreprocessingsWithProperty(sourceTagsObj);
          this.addPreprocessingsWithProperty(targetTagsObj);

          valueOpM2.appendChild(measureBlock2);
          measureBlock2.appendChild(fieldMeasureList2);
          measureBlock2.appendChild(fieldEnabledTh2);
          measureBlock2.appendChild(fieldThreshold2);
          measureBlock2.appendChild(src2.valueSrcProp);
          measureBlock2.appendChild(tgt2.valueSrcProp);

          this.addPreprocessingsWithProperty(sourceTagsObj2);
          this.addPreprocessingsWithProperty(targetTagsObj2);

          doc.appendChild(xmlElem);
        }

        
        //console.log((new XMLSerializer()).serializeToString(doc));
        this.xmlToWorkspace((new XMLSerializer()).serializeToString(doc));

      },    
  }
}
</script>