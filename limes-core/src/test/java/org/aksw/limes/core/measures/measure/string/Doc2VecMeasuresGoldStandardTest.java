/**
 *
 */
package org.aksw.limes.core.measures.measure.string;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.qualititativeMeasures.QualitativeMeasuresEvaluator;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.junit.Before;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * A gold standard test for the doc2vec measure. I use a prepared dataset of normal English and simple English Wikipedia abstracts
 * of about 90 common topics. Clearly the two versions on the same topic should tend to be more similar to each other than to most
 * other articles. Unfortunately, my current doc2vec measure does shows rather small correlations.
 *
 * @author Swante Scholz
 */
public class Doc2VecMeasuresGoldStandardTest {
    
    public static final double epsilon = 0.00001;
    static String basePath = "src/test/resources/";
    
    GoldStandard goldStandard;
    AMapping predictions = MappingFactory.createDefaultMapping();
    
    private String vec2String(INDArray vector) {
        return Arrays.stream(vector.data().asDouble()).mapToObj(it -> ""+it ).collect( Collectors.joining( " " ) );
    }
    
    @Before
    public void setupData() throws IOException {
        Doc2VecMeasure measure = new Doc2VecMeasure(
            Doc2VecMeasure.DEFAULT_PRECOMPUTED_VECTORS_FILE_PATH);
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> sourceUris = new ArrayList<String>();
        ArrayList<String> targetUris = new ArrayList<String>();
        AMapping goldMapping = MappingFactory.createDefaultMapping();
        ArrayList<String> simpleAbstracts = new ArrayList<String>();
        ArrayList<String> normalAbstracts = new ArrayList<String>();
        
        Files.readAllLines(
            new File(basePath, "simple-and-normal-english-wiki-abstracts.csv").toPath()).
            forEach(line -> {
                String[] parts = line.split("\t");
                names.add(parts[0]);
                String sourceUri = "http://a.de/" + parts[0];
                String targetUri = "http://b.de/" + parts[0];
                sourceUris.add(sourceUri);
                targetUris.add(targetUri);
                String simpleAbstract = parts[1];
                String normalAbstract = parts[2];
                simpleAbstracts.add(simpleAbstract);
                normalAbstracts.add(normalAbstract);
                goldMapping.add(sourceUri, targetUri, 1);
            });
        List<INDArray> simpleVectors = simpleAbstracts.stream().map(it ->  measure.inferVector(it)).collect(
            Collectors.toList());
        List<INDArray> normalVectors = normalAbstracts.stream().map(it -> {return measure.inferVector(it);}).collect(
            Collectors.toList());
//        for (int i = 0; i < sourceUris.size(); i++) {
//            System.out.println(names.get(i));
//            System.out.println(Arrays.stream(simpleVectors.get(i).data().asDouble()).mapToObj(it -> ""+it ).collect( Collectors.joining( " " ) ));
//            System.out.println(Arrays.stream(normalVectors.get(i).data().asDouble()).mapToObj(it -> ""+it ).collect( Collectors.joining( " " ) ));
//        }
//        System.exit(0);
        for (int i = 0; i < sourceUris.size(); i++) {
            double bestSim = Double.MAX_VALUE;
            int bestJ = -1;
            INDArray simpleVector = simpleVectors.get(i);
            for (int j = 0; j < targetUris.size(); j++) {
                INDArray normalVector = normalVectors.get(j);
                double sim = Doc2VecMeasure.getSimilarityForInferredVectors(simpleVector, normalVector);
//                System.out.println(vec2String(simpleVector));
//                System.out.println(vec2String(normalVector));
//                System.out.println(sim);
//                System.exit(0);
                /*
-0.045949552208185196 0.11468192934989929 -0.09539033472537994 -0.07275072485208511 0.050668299198150635 -0.037789810448884964 0.04677746817469597 0.022867484018206596 0.10477175563573837 0.08240930736064911 -0.0263932216912508 0.006439305376261473 -0.09940940141677856 0.0819043517112732 0.21302568912506104 -0.034653909504413605 -0.022464875131845474 0.0351354144513607 -0.07489762455224991 0.07821179926395416 -0.008695236407220364 0.08569270372390747 -0.004587483126670122 -0.1013685017824173 -0.07785607129335403 0.12545077502727509 0.06718664616346359 -0.16153624653816223 0.11694519221782684 5.911852931603789E-4 -0.0055073341354727745 -0.20745640993118286 0.016469387337565422 0.02295335754752159 -0.05454182252287865 -0.057958342134952545 -0.05110732093453407 0.03912188112735748 -0.14514414966106415 0.039068516343832016 0.010467752814292908 0.0075712366960942745 0.09107819199562073 0.14743250608444214 0.07914060354232788 -0.08398979157209396 -0.030446214601397514 -0.0770878791809082 0.04317408800125122 -0.02227098122239113 -0.11998841911554337 0.003739316016435623 0.04837822914123535 -0.0026104417629539967 -0.09863342344760895 -0.009326878003776073 0.09130822122097015 0.03791293874382973 0.06196485459804535 0.03893014416098595 0.014705657958984375 -0.09179889410734177 -0.0447700135409832 0.10944151878356934 0.031224314123392105 0.003301850287243724 -0.12875911593437195 -0.028558941558003426 -0.01812359131872654 0.036481067538261414 0.037148866802453995 0.07956085354089737 0.01029525138437748 0.07041559368371964 -0.02712899260222912 0.04168475419282913 -0.009752796962857246 0.029833698645234108 -0.027324149385094643 -0.03884788230061531 0.07761257886886597 -0.06299354881048203 -0.14564357697963715 -0.0686984583735466 -0.057712145149707794 0.09835928678512573 -0.02462431788444519 -0.0017771365819498897 -0.05841311812400818 0.06867367774248123 -0.06943210959434509 -0.025486178696155548 -0.08710572868585587 -0.03992623835802078 -0.11371614784002304 -0.02086048386991024 0.16830766201019287 -0.05193319171667099 -0.16747190058231354 0.06583266705274582
-0.029959339648485184 0.1543588787317276 -0.010094506666064262 -0.09213314950466156 -0.030697496607899666 0.0075641837902367115 -0.037082891911268234 0.00962135661393404 0.013422885909676552 0.11730586737394333 -0.06623925268650055 -0.05396005138754845 -0.2506695091724396 0.14196673035621643 0.2739577889442444 -0.17980000376701355 -0.13809771835803986 0.06535830348730087 -0.13106249272823334 0.17294445633888245 0.02096579782664776 0.11313636600971222 0.008266324177384377 -0.025725096464157104 -0.06301391124725342 0.0634390190243721 0.0663270652294159 -0.18549290299415588 0.12259020656347275 -0.08063599467277527 0.015059789642691612 -0.22851406037807465 0.03929299861192703 0.13788661360740662 -0.10384215414524078 -0.1452621966600418 -0.08415629714727402 -0.1211719661951065 -0.2438793182373047 0.007093794643878937 0.01784351095557213 0.16701962053775787 -0.01772155612707138 0.20402900874614716 0.1602393388748169 -0.06577792763710022 -0.025527343153953552 -0.025651633739471436 0.16409270465373993 -0.05426892265677452 -0.07913366705179214 -0.09604525566101074 0.09872014075517654 -0.09973563253879547 -0.02098122425377369 -0.05983544886112213 0.15143625438213348 0.15195225179195404 0.04089884087443352 0.07322816550731659 -0.04566100612282753 -0.1601247489452362 -0.040976714342832565 0.061776552349328995 0.010770443826913834 -0.09420172870159149 -0.1276191771030426 0.06793800741434097 -0.12303420156240463 0.04707082733511925 0.12012908607721329 0.021975645795464516 0.016698716208338737 0.15288189053535461 -0.09091970324516296 0.0690264105796814 0.001975211314857006 -0.0924728736281395 -0.05934126675128937 -0.014204276725649834 0.08973386883735657 -0.1851918250322342 -0.2117232084274292 0.019503582268953323 0.003844227408990264 0.09101597964763641 -0.015209939330816269 -0.04187901318073273 -0.015894489362835884 0.0042260135523974895 -0.011804236099123955 0.007891014218330383 -0.04508569836616516 0.01563248224556446 -0.059582922607660294 -0.07604151219129562 0.11286736279726028 -0.028669046238064766 -0.07940849661827087 -0.033232130110263824
0.8765852749347687 (java)
0.876585255083 (python new)
0.874614224654 (python)
                 */
                if (sim < bestSim) {
                    bestSim = sim;
                    bestJ = j;
                }
                if (sim > 0.945193769973) {
                    predictions.add(sourceUris.get(i), targetUris.get(j), 1);
                }
            }
//			predictions.add(sourceUris.get(i), targetUris.get(bestJ), 1);
        }
        goldStandard = new GoldStandard(goldMapping, sourceUris, targetUris);
    }
	
	/*
	with 9.55:
	precision: 0.011111111111111112
recall: 0.05555555555555555
fmeasure: 0.018518518518518517
accuracy: 0.9345679012345679
pprecision: 0.10555555555555556
precall: 0.5277777777777778
pfmeasure: 0.17592592592592593
	
	with threshold 0.7
	precision: 0.015741270749856897
	recall: 0.6111111111111112
	fmeasure: 0.030691964285714284
	accuracy: 0.5711111111111111
	pprecision: 0.024899828277046364
	precall: 0.9666666666666667
	pfmeasure: 0.04854910714285714
	
	with threshold 0.154033065905:
	precision: 0.01111934766493699
	recall: 1.0
	fmeasure: 0.021994134897360705
	accuracy: 0.011851851851851851
	pprecision: 0.01111934766493699
	precall: 1.0
	pfmeasure: 0.021994134897360705
	 */
    
    @Test
    public void testGoldenStandard() {
        Set<EvaluatorType> measures = initEvalMeasures();
        
        Map<EvaluatorType, Double> calculations = testQualitativeEvaluator(predictions,
            goldStandard, measures);
        
        double precision = calculations.get(EvaluatorType.PRECISION);
        System.out.println("precision: " + precision);
//		assertEquals(0.7, precision, epsilon);
        
        double recall = calculations.get(EvaluatorType.RECALL);
        System.out.println("recall: " + recall);
//		assertEquals(0.7, recall, epsilon);
        
        double fmeasure = calculations.get(EvaluatorType.F_MEASURE);
        System.out.println("fmeasure: " + fmeasure);
//		assertEquals(0.7, fmeasure, epsilon);
        
        double accuracy = calculations.get(EvaluatorType.ACCURACY);
        System.out.println("accuracy: " + accuracy);
//		assertEquals(0.94, accuracy, epsilon);
        
        double pprecision = calculations.get(EvaluatorType.P_PRECISION);
        System.out.println("pprecision: " + pprecision);
//		assertEquals(0.8, pprecision, epsilon);
        
        double precall = calculations.get(EvaluatorType.P_RECALL);
        System.out.println("precall: " + precall);
//		assertEquals(0.8, precall, epsilon);
        
        double pfmeasure = calculations.get(EvaluatorType.PF_MEASURE);
        System.out.println("pfmeasure: " + pfmeasure);
//		assertTrue(pfmeasure > 0.7 && pfmeasure < 0.9);
    }
    
    private Map<EvaluatorType, Double> testQualitativeEvaluator(AMapping predictions,
        GoldStandard gs, Set<EvaluatorType> evaluationMeasures) {
        return new QualitativeMeasuresEvaluator().evaluate(predictions, gs, evaluationMeasures);
    }
    
    private Set<EvaluatorType> initEvalMeasures() {
        Set<EvaluatorType> measures = new HashSet<EvaluatorType>();
        measures.add(EvaluatorType.PRECISION);
        measures.add(EvaluatorType.RECALL);
        measures.add(EvaluatorType.F_MEASURE);
        measures.add(EvaluatorType.ACCURACY);
        measures.add(EvaluatorType.P_PRECISION);
        measures.add(EvaluatorType.P_RECALL);
        measures.add(EvaluatorType.PF_MEASURE);
        return measures;
    }
    
}
