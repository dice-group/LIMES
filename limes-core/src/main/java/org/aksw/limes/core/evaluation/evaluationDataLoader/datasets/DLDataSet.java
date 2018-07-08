package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSet;
import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;

public class DLDataSet extends BaseDataSet {


    String configfile = null;


    @Override
    public String getName() {
        return "dumpfile";
    }

    @Override
    public String getDataSetFolder() {
        return getBaseFolder() + "DumpFile/";
    }

    @Override
    public String getConfigFile() {
        return "dumpfile.xml";
    }

    @Override
    public String getReferenceFile() {

//        call the goldstandard file generator
        String referencefile = null;
            GoldStandardGenerator gsgenerator = new GoldStandardGenerator();
        try {
            referencefile = gsgenerator.fileCheck();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return referencefile;
    }

    @Override
    public String getSourceFile() {
        /*call the dumpfile generator to get the source file path*/
        String sourcefile = null;
        Evaluation ev = new Evaluation();
         sourcefile = ev.queryExecution();
        return sourcefile;
    }

    @Override
    public String getTargetFile() {
        /*call the dumpfile generator to grt the target file path*/
        String targetfile = null;
        Evaluation ev = new Evaluation();
        targetfile = ev.queryExecution();
        return targetfile;
    }

    @Override
    public String getSourceClass() {
        String sourceclass = "http://www.w3.org/2002/07/owl#Thing";
        return sourceclass;
    }

    @Override
    public String getTargetClass() {
        String targetclass = "http://www.w3.org/2002/07/owl#Thing";
        return targetclass;
    }

    @Override
    public String getEvaluationFilename() {
        return "Pseudo_eval_DumpFile.csv";
    }

    @Override
    public String getOAEIType() {
//        DLDataSetIO datasetio = new DLDataSetIO;
//        datasetio.loadMapping(configfile,datasetfolder);
        return null;
    }

    @Override
    public IDataSetIO getIO() {
        return new DLDataSetIO();
    }
}
