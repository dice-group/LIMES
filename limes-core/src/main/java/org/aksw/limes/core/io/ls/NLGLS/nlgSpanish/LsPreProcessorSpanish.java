package org.aksw.limes.core.io.ls.NLGLS.nlgSpanish;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.ls.LinkSpecification;

import simplenlg.features.Feature;
import simplenlg.features.InternalFeature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.spanish.XMLLexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;

public class LsPreProcessorSpanish {

	protected static String leftProp;
	protected static String rightProp;

	protected static Lexicon lexicon = new XMLLexicon();                     
	protected static NLGFactory nlgFactory = new NLGFactory(lexicon);

	public NPPhraseSpec atomicMeasure(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {
		String atomicMeasureString = linkSpec.getAtomicMeasure();
		NPPhraseSpec n1=nlgFactory.createNounPhrase(atomicMeasureString);

		return n1;
	}
	public  NPPhraseSpec atomicSimilarity(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {

		String atomicMeasureString = linkSpec.getAtomicMeasure();
		atomicMeasureString =convert(atomicMeasureString) ;

		NPPhraseSpec n2=nlgFactory.createNounPhrase(atomicMeasureString);
		NPPhraseSpec similarity=nlgFactory.createNounPhrase(" similar"); // should be written in Spanish
		n2.setDeterminer("una");
		n2.addPostModifier(similarity);

		return n2;
	}

	public  String leftProperty(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {

		String fullExpression = linkSpec.getFullExpression();
		leftProp = linkSpec.getMeasure().substring(fullExpression.indexOf("x")+2,
				fullExpression.indexOf(","));
		if(leftProp.contains("#")) {
			leftProp=leftProp.substring(leftProp.indexOf("#")+1);
			System.out.println(" the p "+leftProp);        
		}

		if(leftProp.contains("_")) {
			leftProp=leftProp.replace("_", " ");
		}

		return leftProp;
	}
	public  String rightProperty(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {

		String fullExpression = linkSpec.getFullExpression();

		rightProp = linkSpec.getMeasure().substring(fullExpression.indexOf("y")+2,
				fullExpression.indexOf(")"));
		if(rightProp.contains("#")) {
			rightProp=rightProp.substring(rightProp.indexOf("#")+1);        
		}

		if(rightProp.contains("_"))
			rightProp=rightProp.replace("_", " ");


		return rightProp;
	}

	public  CoordinatedPhraseElement coordinate(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {
		String fullExpression = linkSpec.getFullExpression();
		leftProp = linkSpec.getMeasure().substring(fullExpression.indexOf("x")+2,
				fullExpression.indexOf(","));
		if(leftProp.contains("#")) {
			leftProp=leftProp.substring(leftProp.indexOf("#")+1);
			System.out.println(" the p "+leftProp);        
		}

		if(leftProp.contains("_")) {
			leftProp=leftProp.replace("_", " ");
		}
		rightProp = linkSpec.getMeasure().substring(fullExpression.indexOf("y")+2,
				fullExpression.indexOf(")"));
		if(rightProp.contains("#")) {
			rightProp=rightProp.substring(rightProp.indexOf("#")+1);        
		}

		if(rightProp.contains("_"))
			rightProp=rightProp.replace("_", " ");

		PhraseElement leftP=nlgFactory.createNounPhrase("the","resource");
		leftP.setFeature(Feature.POSSESSIVE, true);
		PhraseElement leftPValue=nlgFactory.createNounPhrase(leftProp);
		leftPValue.setFeature(InternalFeature.SPECIFIER, leftP);
		leftPValue.addComplement("of the source");
		//PhraseElement leftSource=nlgFactory.createNounPhrase("the","resource");
		PhraseElement rightP=nlgFactory.createNounPhrase("the","resource");
		rightP.setFeature(Feature.POSSESSIVE, true);
		PhraseElement rightPValue=nlgFactory.createNounPhrase(rightProp);
		rightPValue.setFeature(InternalFeature.SPECIFIER, rightP);
		rightPValue.addComplement("of the target");
		CoordinatedPhraseElement coordinate_1 = nlgFactory.createCoordinatedPhrase(leftPValue,rightPValue);

		return coordinate_1;
	}
	public  NPPhraseSpec Theta(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {


		double d = linkSpec.getThreshold()*100;
		d=Math.round(d*100/100);
		int dAsInteger= (int)d;

		NPPhraseSpec theta=nlgFactory.createNounPhrase(dAsInteger +"%");

		PPPhraseSpec pp = nlgFactory.createPrepositionPhrase();
		pp.addComplement(theta);
		pp.setPreposition("of");
		return theta;
	}

	public NPPhraseSpec resourceValue(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {

		String fullExpression = linkSpec.getFullExpression();
		leftProp = linkSpec.getMeasure().substring(fullExpression.indexOf("x")+2,
				fullExpression.indexOf(","));
		if(leftProp.contains("#")) {
			leftProp=leftProp.substring(leftProp.indexOf("#")+1);
			System.out.println(" the p "+leftProp);        
		}

		if(leftProp.contains("_")) {
			leftProp=leftProp.replace("_", " ");
		}
		rightProp = linkSpec.getMeasure().substring(fullExpression.indexOf("y")+2,
				fullExpression.indexOf(")"));
		if(rightProp.contains("#")) {
			rightProp=rightProp.substring(rightProp.indexOf("#")+1);        
		}

		if(rightProp.contains("_"))
			rightProp=rightProp.replace("_", " ");


		NPPhraseSpec resource=nlgFactory.createNounPhrase("resource");
		resource.setFeature(Feature.POSSESSIVE, true);
		NPPhraseSpec resourceValue=nlgFactory.createNounPhrase(leftProp);
		return resourceValue;
	}
	private static String convert(String str) 
	{ 

		// Create a char array of given String 
		char ch[] = str.toCharArray(); 
		for (int i = 0; i < str.length(); i++) { 

			// If first character of a word is found 
			if (i == 0 && ch[i] != ' ' ||  
					ch[i] != ' ' && ch[i - 1] == ' ') { 

				// If it is in lower-case 
				if (ch[i] >= 'a' && ch[i] <= 'z') { 

					// Convert into Upper-case 
					ch[i] = (char)(ch[i] - 'a' + 'A'); 
				} 
			} 

			else if (ch[i] >= 'A' && ch[i] <= 'Z')  

				// Convert into Lower-Case 
				ch[i] = (char)(ch[i] + 'a' - 'A');             
		} 

		// Convert the char array to equivalent String 
		String st = new String(ch); 
		return st; 
	} 


}

