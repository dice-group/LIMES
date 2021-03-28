package org.aksw.limes.core.io.ls.NLGLS.nlgEN;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.ls.LinkSpecification;

import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.XMLLexicon;

public class runNlg {

	protected static Lexicon lexicon = new XMLLexicon(); 
	public static void main(String[] args) throws UnsupportedMLImplementationException, IOException {
		/*
		String s="Hallo"+"\t"+"Abdullah"+"\t"+ "Ahmed";
		List<String> temp= Files.readAllLines(Paths.get("/home/abdullah/joie-kdd19/data/dbpedia/db_onto_small_train.txt"));
	for(String t:temp) {
		String[]s1=t.split("\t");
		System.out.println(s1[0]+" "+s1[1]+" "+s1[2]);}*/

		LsPostProcessor lsPostProcessor=new LsPostProcessor() ;
		LinkSpecification linkSpec=new LinkSpecification();
		//EvaluationData eval = DataSetChooser.getData(DataSets.DBLPACM);
		Set<String> linkspecs=new HashSet<String>();
		List<String> operators=new ArrayList<String>();

		List<String> operators1=new ArrayList<String>();
		List<String> properties=new ArrayList<String>();
		List<String> properties1=new ArrayList<String>();
		List<String> similarityMeasures=new ArrayList<String>();
		List<String> similarityMeasures1=new ArrayList<String>();
		List<String> similarityMeasures2=new ArrayList<String>();
		List<String> similarityMeasures3=new ArrayList<String>();
		List<String> measures=new ArrayList<String>();

		List<String> measures1=new ArrayList<String>();
		operators.add("OR");
		operators.add("AND");

		operators1.add("OR");
		operators1.add("AND");
		properties.add("name");
		properties.add("title");
		properties.add("firstName");
		properties.add("givenName");
		//properties.add("label");
		//properties.add("streetname");
		//properties.add("description");
		//properties.add("authors");

		//properties1.add("name");
		//properties1.add("title");
		//properties1.add("firstName");
		//properties1.add("givenName");
		properties1.add("label");
		properties1.add("streetname");
		properties1.add("description");
		properties1.add("authors");
		properties1.add("movie");
		properties1.add("director");

		similarityMeasures.add("trigrams");
		similarityMeasures.add("qgrams");
		
		similarityMeasures1.add("cosine");
		similarityMeasures1.add("jaccard");
		
		similarityMeasures2.add("levenshtein");
		similarityMeasures2.add("overlap");
		similarityMeasures2.add("jaroWinkler");
		similarityMeasures2.add("mongeElkan");


		similarityMeasures3.add("soundex");
		similarityMeasures3.add("ratcliff");
		//similarityMeasures3.add("koelnerPhonetic");



		measures.add("0.45");
		measures.add("0.62");
		//measures.add("0.82");
		measures.add("0.0");
		//measures.add("1.0");

		measures1.add("0.25");
		measures1.add("0.37");
		//measures1.add("0.55");
		//measures1.add("0.0");
		measures1.add("1.0");
		for(String op:operators) {
			for(String op1:operators1) {
				for(String p:properties) {
					for(String p1:properties1) {
						for(String sim:similarityMeasures) {
							for(String sim1: similarityMeasures1) {
								for(String sim2: similarityMeasures2) {
									for(String sim3: similarityMeasures3) {
										for(String m:measures) {
											for(String m1:measures1) {
												
										//String test=op+"("+op+"("+sim3+"(z."+p+",y."+p+")|"+m+","+op+"("+op+"("+sim+"(x."+p+",y."+p+")|"+m+","+sim+"(x."+p+",y."+p+")|"+m+")|"+m+","+sim+"(x."+p+",y."+p+")|"+m+")|"+m+")|"+m+","+sim+"(x."+p+",y."+p+")|"+m+")";
												
												String firstLevelLS      = op+"("+sim+"(z."+p+",y."+p+")|"+m+","+sim1+"(z."+p+",y."+p+")|"+m1+")";
												String secondLevelLS     = op+"("+sim2+"(z."+p+",y."+p+")|"+m+","+sim1+"(z."+p+",y."+p1+")|"+m1+")";
												String thirdLevelLS      = op+"("+op+"("+sim2+"(z."+p+",y."+p+")|"+m+","+sim1+"(z."+p+",y."+p+")|"+m1+")|"+m+","+op1+"("+sim2+"(z."+p1+",y."+p1+")|"+m+","+sim1+"(z."+p+",y."+p+")|"+m1+")|"+m1+")";
												String fourthLevelLS     = op+"("+op+"("+sim+"(z."+p1+",y."+p1+")|"+m+","+sim1+"(z."+p+",y."+p1+")|"+m1+")|"+m+","+op+"("+sim2+"(z."+p+",y."+p+")|"+m+","+sim+"(z."+p+",y."+p1+")|"+m1+")|"+m+")";
												String fifithLevelLS     = op+"("+op+"("+op+"("+sim+"(z."+p+",y."+p+")|"+m+","+op1+"("+op+"("+sim1+"(z."+p1+",y."+p1+")|"+m1+","+sim+"(z."+p+",y."+p+")|"+m1+")|"+m+","+sim2+"(z."+p1+",y."+p1+")|"+m+")|"+m1+")|"+m1+","+sim2+"(z."+p+",y."+p+")|"+m+")|"+m+","+sim+"(z."+p1+",y."+p1+")|"+m1+")";
												String fifithLevelLS1    = op+"("+op+"("+op1+"("+sim3+"(z."+p+",y."+p+")|"+m+","+op1+"("+op+"("+sim2+"(z."+p1+",y."+p1+")|"+m1+","+sim1+"(z."+p1+",y."+p1+")|"+m1+")|"+m+","+sim3+"(z."+p1+",y."+p+")|"+m+")|"+m1+")|"+m1+","+sim2+"(z."+p1+",y."+p+")|"+m+")|"+m+","+sim3+"(z."+p+",y."+p1+")|"+m1+")";
												String sixthLevelLs      = op+"("+op+"("+sim+"(z."+p+",y."+p+")|"+m+","+op+"("+op+"("+sim2+"(z."+p1+",y."+p1+")|"+m1+","+sim1+"(z."+p+",y."+p+")|"+m+")|"+m+","+sim+"(z."+p+",y."+p+")|"+m1+")|"+m1+")|"+m+","+sim2+"(z."+p+",y."+p+")|"+m1+")";
												String sevniththLevelLs  = op1+"("+op+"("+sim1+"(z."+p+",y."+p1+")|"+m+","+op1+"("+op+"("+sim+"(z."+p+",y."+p1+")|"+m1+","+sim2+"(z."+p1+",y."+p1+")|"+m+")|"+m+","+sim3+"(z."+p+",y."+p1+")|"+m1+")|"+m+")|"+m+","+sim+"(z."+p1+",y."+p1+")|"+m1+")";

												String sixthLevelLs1     = op+"("+op+"("+sim3+"(z."+p+",y."+p+")|"+m+","+op+"("+op1+"("+sim2+"(z."+p+",y."+p1+")|"+m1+","+sim1+"(z."+p+",y."+p+")|"+m+")|"+m+","+sim2+"(z."+p+",y."+p+")|"+m1+")|"+m1+")|"+m+","+sim2+"(z."+p+",y."+p+")|"+m1+")";
												String sevniththLevelLs1 = op1+"("+op+"("+sim2+"(z."+p+",y."+p1+")|"+m+","+op1+"("+op+"("+sim+"(z."+p1+",y."+p1+")|"+m1+","+sim3+"(z."+p+",y."+p+")|"+m+")|"+m+","+sim+"(z."+p+",y."+p1+")|"+m1+")|"+m+")|"+m+","+sim3+"(z."+p1+",y."+p+")|"+m1+")";

												linkspecs.add(firstLevelLS);
												linkspecs.add(secondLevelLS);
												linkspecs.add(thirdLevelLS);
												linkspecs.add(fourthLevelLS);
												linkspecs.add(fifithLevelLS);
												linkspecs.add(fifithLevelLS1);
												linkspecs.add(sixthLevelLs);
												linkspecs.add(sixthLevelLs1);
												linkspecs.add(sevniththLevelLs);
												linkspecs.add(sevniththLevelLs1);
												//linkspecs.add(test);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		String ls1="OR(OR(OR(OR(qgrams(x.name,y.description)|0.41,jaccard(x.name,y.name)|0.36)|0.11,OR(euclidean(x.price,y.price)|0.41,cosine(x.name,y.name)|0.41)|0.11)|0.11,qgrams(x.description,y.description)|0.41)|0.2861,jaccard(x.name,y.name)|0.36)";
		String ls2="OR(OR(trigrams(x.description,y.description)|0.63,OR(trigrams(x.description,y.description)|0.7,OR(euclidean(x.price,y.price)|0.49,levenshtein(x.name,y.name)|0.78)|0.61)|0.61)|0.61,qgrams(x.name,y.name)|0.61)";
		String ls3="AND(AND(trigrams(x.title,y.title)|0.38,trigrams(x.title,y.title)|0.38)|0.68,OR(trigrams(x.authors,y.authors)|0.44,jaccard(x.title,y.title)|0.68)|0.68)";
		String ls4="AND(AND(AND(trigrams(x.title,y.title)|0.62,AND(AND(overlap(x.title,y.title)|0.51,cosine(x.authors,y.authors)|0.52)|0.51,overlap(x.title,y.title)|0.51)|0.51)|0.51,cosine(x.authors,y.authors)|0.52)|0.51,overlap(x.venue,y.venue)|0.51)";
		//String ls5=op+"("+op+"("+sim+"(x."+p+",y."+p+")|"+m+","+op+"("+op+"("+sim+"(x."+p+",y."+p+")|"+m+","+sim+"(x."+p+",y."+p+")|"+m+")|"+m+","+sim+"(x."+p+",y."+p+")|"+m+")|"+m+")|"+m+","+sim+"(x."+p+",y."+p+")|"+m+")";


		System.out.println(linkspecs.size());

		File fout = new File("/home/abdullah/dataset/lsverbalization100K.csv");
		FileOutputStream fos = new FileOutputStream(fout);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		//linkSpec.readSpec(ls4, 0.7);
		//String s=lsPostProcessor.postProcessor(linkSpec);
		//System.out.println("sss "+s);
		Iterator<String> it = linkspecs.iterator();
		while(it.hasNext()){

			String str=it.next();

			linkSpec.readSpec(str, 0.7);
			//System.out.println(linkSpec.getFullExpression());
			//lsPostProcessor.postProcessor(linkSpec);
			String postProcessor = lsPostProcessor.postProcessor(linkSpec);
			bw.write(postProcessor);
			bw.newLine();

		}
		bw.close();
		//AMapping slection = lsPostProcessor.selection(linkSpec,eval.getSourceCache(),eval.getTargetCache());

		//lsPostProcessor.summarization(linkSpec,eval.getSourceCache(),eval.getTargetCache(), slection,0.8);
	}



}
