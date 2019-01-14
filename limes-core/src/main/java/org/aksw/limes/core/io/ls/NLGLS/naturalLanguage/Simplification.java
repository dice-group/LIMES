package org.aksw.limes.core.io.ls.NLGLS.naturalLanguage;

public class Simplification {
	
	
	
	
	
	
//	static void sumerazation(LinkSpecification linkspec,ACache source, ACache target, double threshold,int x, String str) throws UnsupportedMLImplementationException {
	//
	//		Lexicon lexicon = new XMLLexicon();                        
	//		NLGFactory nlgFactory = new NLGFactory(lexicon);
	//
	//		SPhraseSpec clause_1 = nlgFactory.createClause();
	//		SPhraseSpec clause_2 = nlgFactory.createClause();
	//		SPhraseSpec clause_3 = nlgFactory.createClause();
	//		SPhraseSpec clause_5 = nlgFactory.createClause();
	//		SPhraseSpec clause_6 = nlgFactory.createClause();
	//		SPhraseSpec clause_7 = nlgFactory.createClause();
	//		SPhraseSpec clause_9 = nlgFactory.createClause();
	//		CoordinatedPhraseElement coordinate_1 = nlgFactory.createCoordinatedPhrase();
	//		CoordinatedPhraseElement coordinate_2 = nlgFactory.createCoordinatedPhrase();
	//
	//		int allMapZise;
	//		allMapZise=slection(linkspec, source, target).getSize();
	//		double percentage=100.0;
	//		double roundPercentage = Math.round(percentage*100.0/100.0);
	//
	//		if(threshold==roundPercentage) {
	//			clause_1.addFrontModifier("when");
	//			clause_1.setSubject("the mapping coverage");
	//			clause_1.setVerb("is");
	//
	//			clause_1.addComplement("equal to "+ threshold+ "%");
	//
	//			clause_2.addComplement("then");
	//			clause_3.setSubject("the description");
	//			clause_3.setVerb("is");
	//			clause_3.addComplement("as follows:");
	//			coordinate_1.addComplement(clause_1);
	//			coordinate_1.addComplement(clause_2);
	//			coordinate_1.addComplement(clause_3);
	//
	//			clause_1=new SPhraseSpec(nlgFactory);
	//			clause_2=new SPhraseSpec(nlgFactory);
	//			clause_3=new SPhraseSpec(nlgFactory);
	//
	//			Realiser realiser_1 = new Realiser(lexicon);
	//			NLGElement realised_1 = realiser_1.realise(coordinate_1);
	//			String realisation_1 = realised_1.getRealisation();
	//			System.out.println(realisation_1);
	//			coordinate_1=new CoordinatedPhraseElement();
	//			fullMeasureNLG(linkspec,0,"");
	//		}
	//
	//		for(int i=0;i<linkspec.getChildren().size();i++)
	//		{
	//			LinkSpecification  linkspecification=linkspec.getChildren().get(i);
	//
	//			//		
	//			//				clause_9.addComplement(str +(i+1));
	//			//				System.out.println("I am here "+str +(i+1));
	//			//System.out.println("we are here");
	//			if(!linkspecification.isAtomic()) {
	//
	//				int    map1=slection(linkspecification, source, target).getSize();
	//				double percentage1=((double) map1/(double) allMapZise)*100.d;
	//				double roundPercentage1 = Math.round(percentage1*100.0/100.0);
	//				if(100.0==threshold) {
	//					clause_5.addFrontModifier("when");
	//					clause_5.setSubject("the mapping coverage");
	//					clause_5.addComplement("equal to "+ threshold+ "%");
	//					clause_6.addComplement("then");
	//					clause_7.setSubject("the description");
	//					clause_7.setVerb("is");
	//					clause_7.addComplement("as follows:");
	//
	//					coordinate_2.addComplement(clause_5);
	//					coordinate_2.addComplement(clause_6);
	//					coordinate_2.addComplement(clause_7);
	//
	//					clause_5=new SPhraseSpec(nlgFactory);
	//					clause_6=new SPhraseSpec(nlgFactory);
	//					clause_7=new SPhraseSpec(nlgFactory);
	//					Realiser realiser_2 = new Realiser(lexicon);
	//					NLGElement realised_2 = realiser_2.realise(coordinate_2);
	//					String realisation_2 = realised_2.getRealisation();
	//					System.out.println(realisation_2);
	//					//System.out.println(" I am here: "+(str +(i+1)));
	//					coordinate_2=new CoordinatedPhraseElement();
	//
	//
	//					//						Realiser realiser_3 = new Realiser(lexicon);
	//					//						NLGElement realised_3 = realiser_3.realise(clause_9);
	//					//						String realisation_3= realised_3.getRealisation();
	//					//						System.out.println(realisation_3);
	//					//						clause_9=new SPhraseSpec( nlgFactory);
	//					fullMeasureNLG(linkspecification, 0, "");
	//					sumerazation(linkspecification, source, target, threshold,i,(str+"1."));
	//
	//				}
	//
	//			}
	//			else {
	//
	//
	//				int    map1=slection(linkspecification, source, target).getSize();
	//				double percentage1=((double) map1/(double) allMapZise)*100.d;
	//				double roundPercentage1 = Math.round(percentage1*100.0/100.0);
	//				// System.out.println("the percentage 2 is: "+roundPercentage1);
	//				if(100.0==threshold) {
	//					clause_5.addFrontModifier("when");
	//					clause_5.setSubject("the mapping coverage");
	//					clause_5.addComplement("equal to "+ threshold+ "%");
	//					clause_6.addComplement("then");
	//					clause_7.setSubject("the description");
	//					clause_7.setVerb("is");
	//					clause_7.addComplement("as follows:");
	//
	//					coordinate_2.addComplement(clause_5);
	//					coordinate_2.addComplement(clause_6);
	//					coordinate_2.addComplement(clause_7);
	//
	//					clause_5=new SPhraseSpec(nlgFactory);
	//					clause_6=new SPhraseSpec(nlgFactory);
	//					clause_7=new SPhraseSpec(nlgFactory);
	//					Realiser realiser_2 = new Realiser(lexicon);
	//					NLGElement realised_2 = realiser_2.realise(coordinate_2);
	//					String realisation_2 = realised_2.getRealisation();
	//					System.out.println(realisation_2);
	//					coordinate_2=new CoordinatedPhraseElement();
	//					//System.out.println(" I am here 1: "+(str +(i+1)));
	//					//clause_9.addComplement(str +(i+1));
	//					//
	//					//						Realiser realiser_3 = new Realiser(lexicon);
	//					//						NLGElement realised_3 = realiser_3.realise(clause_9);
	//					//						String realisation_3= realised_3.getRealisation();
	//					//						System.out.println(realisation_3);
	//					//						clause_9=new SPhraseSpec( nlgFactory);
	//					NLGElement atomicMeasureNLG = atomicMeasureNLG(linkspecification);
	//					Realiser realiser_4 = new Realiser(lexicon);
	//					NLGElement realised_4 = realiser_4.realise(atomicMeasureNLG);
	//					String realisation_4 = realised_4.getRealisation();
	//					System.out.println(realisation_4);
	//
	//				}
	//
	//			}
	//			//}
	//
	//		}
	//
	//
	//	}
	//
	//
	//	/**
	//	 * @param linkSpec
	//	 * @param source
	//	 * @param target
	//	 * @return
	//	 * @throws UnsupportedMLImplementationException
	//	 */
	//	static AMapping slection(LinkSpecification linkSpec, ACache source, ACache target) throws UnsupportedMLImplementationException {
	//
	//		AMLAlgorithm wombat = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
	//				MLImplementationType.SUPERVISED_BATCH);
	//		//        //Especially the source and target caches
	//		wombat.init(null,source, target);
	//		//        //And the training data
	//		MLResults mlModel=new MLResults();
	//		mlModel.setLinkSpecification(linkSpec);
	//		AMapping mapping = wombat.predict(source, target, mlModel);
	//		return mapping;
	//	}

}
