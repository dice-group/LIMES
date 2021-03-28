package org.aksw.limes.core.io.ls.NLGLS.nlgDE;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.ls.LinkSpecification;


public class runNlgDE {

	//protected static Lexicon lexicon = new XMLLexicon(); 
	public static void main(String[] args) throws UnsupportedMLImplementationException, IOException {
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
		properties.add("comment");
		properties.add("abstract");
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
		properties1.add("lastName");
		properties1.add("brand");

		similarityMeasures.add("trigrams");
		similarityMeasures.add("qgrams");
		
		similarityMeasures1.add("cosine");
		similarityMeasures1.add("jaccard");
		
		similarityMeasures2.add("levenshtein");
		similarityMeasures2.add("overlap");
		similarityMeasures3.add("jaroWinkler");
	
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
												
										//String test=op+"("+op+"("+sim3+"(x."+p+",y."+p+")|"+m+","+op+"("+op+"("+sim+"(x."+p+",y."+p+")|"+m+","+sim+"(x."+p+",y."+p+")|"+m+")|"+m+","+sim+"(x."+p+",y."+p+")|"+m+")|"+m+")|"+m+","+sim+"(x."+p+",y."+p+")|"+m+")";
												
												String firstLevelLS      = op+"("+sim+"(x."+p+",y."+p+")|"+m+","+sim1+"(x."+p+",y."+p+")|"+m1+")";
												String secondLevelLS     = op+"("+sim2+"(x."+p+",y."+p+")|"+m+","+sim1+"(x."+p+",y."+p1+")|"+m1+")";
												String thirdLevelLS      = op+"("+op+"("+sim2+"(x."+p+",y."+p+")|"+m+","+sim1+"(x."+p+",y."+p+")|"+m1+")|"+m+","+op1+"("+sim2+"(x."+p1+",y."+p1+")|"+m+","+sim1+"(x."+p+",y."+p+")|"+m1+")|"+m1+")";
												String fourthLevelLS     = op+"("+op+"("+sim+"(x."+p1+",y."+p1+")|"+m+","+sim1+"(x."+p+",y."+p1+")|"+m1+")|"+m+","+op+"("+sim2+"(x."+p+",y."+p+")|"+m+","+sim+"(x."+p+",y."+p1+")|"+m1+")|"+m+")";
												String fifithLevelLS     = op+"("+op+"("+op+"("+sim+"(x."+p+",y."+p+")|"+m+","+op1+"("+op+"("+sim1+"(x."+p1+",y."+p1+")|"+m1+","+sim+"(x."+p+",y."+p+")|"+m1+")|"+m+","+sim2+"(x."+p1+",y."+p1+")|"+m+")|"+m1+")|"+m1+","+sim2+"(x."+p+",y."+p+")|"+m+")|"+m+","+sim+"(x."+p1+",y."+p1+")|"+m1+")";
												String fifithLevelLS1    = op+"("+op+"("+op1+"("+sim3+"(x."+p+",y."+p+")|"+m+","+op1+"("+op+"("+sim2+"(x."+p1+",y."+p1+")|"+m1+","+sim1+"(x."+p1+",y."+p1+")|"+m1+")|"+m+","+sim3+"(x."+p1+",y."+p+")|"+m+")|"+m1+")|"+m1+","+sim2+"(x."+p1+",y."+p+")|"+m+")|"+m+","+sim3+"(x."+p+",y."+p1+")|"+m1+")";
												String sixthLevelLs      = op+"("+op+"("+sim+"(x."+p+",y."+p+")|"+m+","+op+"("+op+"("+sim2+"(x."+p1+",y."+p1+")|"+m1+","+sim1+"(x."+p+",y."+p+")|"+m+")|"+m+","+sim+"(x."+p+",y."+p+")|"+m1+")|"+m1+")|"+m+","+sim2+"(x."+p+",y."+p+")|"+m1+")";
												String sevniththLevelLs  = op1+"("+op+"("+sim1+"(x."+p+",y."+p1+")|"+m+","+op1+"("+op+"("+sim+"(x."+p+",y."+p1+")|"+m1+","+sim2+"(x."+p1+",y."+p1+")|"+m+")|"+m+","+sim3+"(x."+p+",y."+p1+")|"+m1+")|"+m+")|"+m+","+sim+"(x."+p1+",y."+p1+")|"+m1+")";
												String sixthLevelLs1     = op+"("+op+"("+sim3+"(x."+p+",y."+p+")|"+m+","+op+"("+op1+"("+sim2+"(x."+p+",y."+p1+")|"+m1+","+sim1+"(x."+p+",y."+p+")|"+m+")|"+m+","+sim2+"(x."+p+",y."+p+")|"+m1+")|"+m1+")|"+m+","+sim2+"(x."+p+",y."+p+")|"+m1+")";
												String sevniththLevelLs1 = op1+"("+op+"("+sim2+"(x."+p+",y."+p1+")|"+m+","+op1+"("+op+"("+sim+"(x."+p1+",y."+p1+")|"+m1+","+sim3+"(x."+p+",y."+p+")|"+m+")|"+m+","+sim+"(x."+p+",y."+p1+")|"+m1+")|"+m+")|"+m+","+sim3+"(x."+p1+",y."+p+")|"+m1+")";

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

		
		
			
			
			
			System.out.println(linkspecs.size());

			LsPostProcessorDE lsPostProcessorDe=new LsPostProcessorDE() ;
			LinkSpecification linkSpec=new LinkSpecification();

			File fout = new File("/upb/users/a/afaahmed/profiles/unix/cs/nellie/bio2rdf/100data/germanlsverbalization.csv");
			FileOutputStream fos = new FileOutputStream(fout);

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			//linkSpec.readSpec("", 0.8);
			
			
			
			
			Iterator<String> it = linkspecs.iterator();
			int counter=1;
			while(it.hasNext()){

				String str=it.next();

				linkSpec.readSpec(str, 0.7);
				//System.out.println(linkSpec.getFullExpression());
				//lsPostProcessor.postProcessor(linkSpec);
				lsPostProcessorDe.postProcessor(linkSpec);
				String out=lsPostProcessorDe.realisng();
				bw.write("\""+linkSpec.getFullExpression()+"\""+","+"\""+out+"\"");
				bw.newLine();
				System.out.println(counter ++);

			}
			bw.close();
			//AMapping slection = lsPostProcessorDe.selection(linkSpec,eval.getSourceCache(),eval.getTargetCache());

			//lsPostProcessorDe.summarization(linkSpec,eval.getSourceCache(),eval.getTargetCache(), slection,0.2);

			//lsPostProcessorDe.postProcessor(linkSpec);//summarization(linkSpec,eval.getSourceCache(),eval.getTargetCache(), slection,0.2);
			//System.out.println("----------------------------------LINK SPECIFICATION " + counter++);
			//System.out.println(str);
			//System.out.println("----------------------------------LINK SPECIFICATION VERBALIZATION START------------------\n");
			//lsPostProcessorDe.realisng();

			//System.out.println("----------------------------------LINK SPECIFICATION VERBALIZATION END------------------\n");
		
	}



}
