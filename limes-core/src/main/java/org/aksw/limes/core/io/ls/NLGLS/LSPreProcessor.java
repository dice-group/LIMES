package org.aksw.limes.core.io.ls.NLGLS;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.ls.LinkSpecification;

import simplenlg.features.Feature;
import simplenlg.features.InternalFeature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.XMLLexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;

public class LSPreProcessor {

	protected static String leftProp;
	protected static String rightProp;

	protected static Lexicon lexicon = new XMLLexicon();                      
	protected static NLGFactory nlgFactory = new NLGFactory(lexicon);

	public static CoordinatedPhraseElement sameSubject() throws UnsupportedMLImplementationException {

		CoordinatedPhraseElement sameSubj = nlgFactory.createCoordinatedPhrase("the source","the target");

		return sameSubj;
	}


	public static NPPhraseSpec atomicMeasure(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {
		String atomicMeasureString = linkSpec.getAtomicMeasure();
		NPPhraseSpec n1=nlgFactory.createNounPhrase(atomicMeasureString);

		return n1;
	}
	public static NPPhraseSpec atomicSimilarity(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {

		String atomicMeasureString = linkSpec.getAtomicMeasure();


		NPPhraseSpec n2=nlgFactory.createNounPhrase(atomicMeasureString);
		NPPhraseSpec similarity=nlgFactory.createNounPhrase(" similarity");
		n2.setDeterminer("a");
		n2.addPostModifier(similarity);

		return n2;
	}

	public static String leftProperty(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {

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
	public static String rightProperty(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {

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

	public static CoordinatedPhraseElement coordinate(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {
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

		PhraseElement leftP=nlgFactory.createNounPhrase("the","source");
		leftP.setFeature(Feature.POSSESSIVE, true);
		PhraseElement leftPValue=nlgFactory.createNounPhrase(leftProp);
		leftPValue.setFeature(InternalFeature.SPECIFIER, leftP);

		PhraseElement rightP=nlgFactory.createNounPhrase("the","target");
		rightP.setFeature(Feature.POSSESSIVE, true);
		PhraseElement rightPValue=nlgFactory.createNounPhrase(rightProp);
		rightPValue.setFeature(InternalFeature.SPECIFIER, rightP);

		CoordinatedPhraseElement coordinate_1 = nlgFactory.createCoordinatedPhrase(leftPValue,rightPValue);

		return coordinate_1;
	}
	public static NPPhraseSpec Theta(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {


		double d = linkSpec.getThreshold()*100.00;
		d=Math.round(d*100.00/100.00);

		NPPhraseSpec theta=nlgFactory.createNounPhrase(d +"%");

		PPPhraseSpec pp = nlgFactory.createPrepositionPhrase();
		pp.addComplement(theta);
		pp.setPreposition("of");
		return theta;
	}

	public static NPPhraseSpec resourceValue(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {

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
		resource.addPreModifier("same");
		resource.setFeature(Feature.POSSESSIVE, true);
		NPPhraseSpec resourceValue=nlgFactory.createNounPhrase(leftProp);
		resourceValue.setFeature(InternalFeature.SPECIFIER, resource);

		return resourceValue;
	}



}

