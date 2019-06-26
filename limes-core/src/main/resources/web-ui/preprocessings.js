class Preprocessings extends Properties{

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
    }

	processingPropertyWithPrepFunc(i){

      if(i.getChildren().length){
        if(!this.context.notConnectedToStart){
          i.setDisabled(false);
        }
        i.getChildren().forEach(child => {
          if(!this.context.notConnectedToStart){
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
              this.context.mainSource = true;
            }
            if(srcOrTgt.toLowerCase() === 'target'){
              this.context.mainTarget = true;
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
    }

}