package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RMapper {
    ArrayList<String> source = new ArrayList<String>();
    ArrayList<String> target = new ArrayList<String>();



    public void mapper(String sfile, String tfile) throws Exception {
        ArrayList<String> sourcelist = sourceFileReader(sfile);
        List<String> targetlist = TargetFileReader(tfile);

        for (int i =0; i < sourcelist.size(); i++){
            String tline = targetlist.get(i);
            String sline = sourcelist.get(i);
            if (tline.equals(sline)) {
                System.out.println(sline + "\t \t" + tline);

            }
        }
    }
    /*reading the source file and getting the resource id*/
    public ArrayList<String> sourceFileReader(String filename) throws Exception {
        File file = new File(filename);

        BufferedReader br = new BufferedReader(new FileReader(file));
        //String line = br.readLine();

        String st;
        while ((st = br.readLine()) != null)
            if (st.contains("rdf:resource")) {

                //System.out.println(st.substring(st.indexOf("=") + 2, st.lastIndexOf(">") - 2));
                source.add(st.substring(st.indexOf("=") + 2, st.lastIndexOf(">") - 2));


            }
        //return source;
        //System.out.println(source);
        return source;
    }

    public ArrayList<String> TargetFileReader(String filename) throws Exception {
        File file = new File(filename);

        BufferedReader br = new BufferedReader(new FileReader(file));
        //String line = br.readLine();

        String st;
        while ((st = br.readLine()) != null)
            if (st.contains("rdf:resource")) {

                //System.out.println(st.substring(st.indexOf("=") + 2, st.lastIndexOf(">") - 2));
                target.add(st.substring(st.indexOf("=") + 2, st.lastIndexOf(">") - 2));



            }
        //System.out.println(st);
        return target;
    }
}
