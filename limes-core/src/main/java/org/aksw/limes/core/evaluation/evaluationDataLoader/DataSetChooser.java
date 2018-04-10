package org.aksw.limes.core.evaluation.evaluationDataLoader;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.aksw.limes.core.evaluation.oracle.IOracle;
import org.aksw.limes.core.evaluation.oracle.OracleFactory;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.config.reader.AConfigurationReader;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.MappingFactory.MappingType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*import de.uni_leipzig.simba.genetics.util.OAEIMappingParser;
import de.uni_leipzig.simba.genetics.util.PropMapper;
import de.uni_leipzig.simba.io.ConfigReader;
import de.uni_leipzig.simba.io.rdfconfig.RDFConfigReader;
import de.uni_leipzig.simba.learning.oracle.oracle.Oracle;
import de.uni_leipzig.simba.learning.oracle.oracle.OracleFactory;
import de.uni_leipzig.simba.selfconfig.Experiment;*/

/**
 * Class to grant central access to evaluation datasets.
 *
 * @author Klaus Lyko
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public class DataSetChooser {

    static Logger logger = LoggerFactory.getLogger(DataSetChooser.class);

    /**
     * provides the information of the link specification path, the gold standard path, the datasets path and additional information of a required dataset
     * @param dataSetName The name of the dataset to get its information given as string
     * @return The parameters to load the dataset
     */
    public static EvaluationData getData(String dataSetName) {
        String d = dataSetName.replaceAll("-", "").toUpperCase();
        HashMap<MapKey, Object> param = new HashMap<MapKey, Object>();
        switch (d) {
        case "PERSONNew":
            param = getPersonNew();
            break;
        case "PERSON1":
            param = getPerson1();
            break;
        case "PERSON2":
            param = getPerson2();
            break;
        case "RESTAURANTS":
            param = getRestaurant();
            break;
        case "RESTAURANTSFIXED":
            param = getRestaurant();
            break;
        case "DBLPACM":
            param = getDBLPACM();
            break;
        case "ABTBUY":
            param = getAbtBuy();
            break;
        case "DBLPSCHOLAR":
            param = getDBLPScholar();
            break;
        case "AMAZONGOOGLEPRODUCTS":
            param = getAmazonGoogleProducts();
            break;
        case "DBPLINKEDMDB":
            param = getDBPediaLinkedMDB();
            break;
        case "DRUGS":
            param = getDrugs();
            break;
        case "PERSON1_CSV":
            param = getPerson1CSV();
            break;
        case "PERSON2_CSV":
            param = getPerson2CSV();
            break;
        case "RESTAURANTS_CSV":
            param = getRestaurantCSV();
            break;
        case "OAEI2014BOOKS":
            param = getOAEI2014Books();
            break;
        /*
         * case "TOWNS": param = getTowns(); break; case "VILLAGES": param =
         * getVillages(); break; case "MOVIES": param = getMovies(); break;
         */
        }
        param.put(MapKey.EVALUATION_RESULTS_FOLDER, getEvalFolder());
        param.put(MapKey.MAX_RUNS, 5);
        EvaluationData data = EvaluationData.buildFromHashMap(param);
        AMapping fixed = fixReferenceMap(data.getReferenceMapping(), data.getSourceCache(), data.getTargetCache());
        if (d.equals("RESTAURANTSFIXED")) {
            data.setReferenceMapping(fixed);
            data.setName("Restaurants_fixed");
        }
        return data;
    }

    /**
     * Central class to configure evaluation datasets.
     *
     * @param a
     *            DataSets enum
     * @return HashMap
     *         <table summary="This table shows the list of information required to load the dataset which are stored in this hash map as a key that represents
     *         the information title and the value of such item">
     *         <caption>Paths and files required to load a dataset</caption>
     *         <tr>
     *         <th>String key</th>
     *         <th>Object data</th>
     *         </tr>
     *         <tr>
     *         <td>MapKey.BASE_FOLDER</td>
     *         <td></td>
     *         </tr>
     *         <tr>
     *         <td>MapKey.DATASET_FOLDER</td>
     *         <td></td>
     *         </tr>
     *         <tr>
     *         <td>MapKey.CONFIG_FILE</td>
     *         <td></td>
     *         </tr>
     *         <tr>
     *         <td>"referencepath"</td>
     *         <td></td>
     *         </tr>
     *         <tr>
     *         <td>MapKey.EVALUATION_RESULTS_FOLDER</td>
     *         <td></td>
     *         </tr>
     *         <tr>
     *         <td>MapKey.EVALUATION_FILENAME</td>
     *         <td></td>
     *         </tr>

     *         <tr>
     *         <td>MapKey.SOURCE_CACHE</td>
     *         <td>Source Cache</td>
     *         </tr>
     *         <tr>
     *         <td>MapKey.TARGET_CACHE</td>
     *         <td>Target Cache</td>
     *         </tr>
     *         <tr>
     *         <td>MapKey.PROPERTY_MAPPING</td>
     *         <td>PopertyMapping</td>
     *         </tr>
     *         <tr>
     *         <td>MapKey.REFERENCE_MAPPING</td>
     *         <td>Gold standard Mapping</td>
     *         </tr>
     *         </table>
     */
    public static EvaluationData getData(DataSets a) {
        HashMap<MapKey, Object> param = new HashMap<MapKey, Object>();
        switch (a) {
        case PERSON1:
            param = getPerson1();
            break;
        case PERSON2:
            param = getPerson2();
            break;
        case RESTAURANTS:
            param = getRestaurant();
            break;
        case RESTAURANTS_FIXED:
            param = getRestaurant();
            break;
        case DBLPACM:
            param = getDBLPACM();
            break;
        case ABTBUY:
            param = getAbtBuy();
            break;
        case DBLPSCHOLAR:
            param = getDBLPScholar();
            break;
        case AMAZONGOOGLEPRODUCTS:
            param = getAmazonGoogleProducts();
            break;
        case DBPLINKEDMDB:
            param = getDBPediaLinkedMDB();
            break;
        case DRUGS:
            param = getDrugs();
            break;
        case PERSON1_CSV:
            param = getPerson1CSV();
            break;
        case PERSON2_CSV:
            param = getPerson2CSV();
            break;
        case RESTAURANTS_CSV:
            param = getRestaurantCSV();
            break;
        case OAEI2014BOOKS:
            param = getOAEI2014Books();
            break;

        }

        param.put(MapKey.EVALUATION_RESULTS_FOLDER, getEvalFolder());
        param.put(MapKey.MAX_RUNS, 5);
        EvaluationData data = EvaluationData.buildFromHashMap(param);
        if (a.equals(DataSets.RESTAURANTS_FIXED)) {
            AMapping fixed = fixReferenceMap(data.getReferenceMapping(), data.getSourceCache(), data.getTargetCache());
            data.setReferenceMapping(fixed);
            data.setName("Restaurants_fixed");
        }
        return data;
    }
    
    private static HashMap<MapKey, Object> getPersonNew() {
        HashMap<MapKey, Object> param = new HashMap<MapKey, Object>();
        // folders & files
        param.put(MapKey.BASE_FOLDER, "src/main/resources/datasets/");
        param.put(MapKey.DATASET_FOLDER, "src/main/resources/datasets/Persons1/");
        param.put(MapKey.CONFIG_FILE, "personsNew.xml");
        param.put(MapKey.REFERENCE_FILE, "dataset11_dataset12_goldstandard_person.xml");
        param.put(MapKey.SOURCE_FILE, "person11.nt");
        param.put(MapKey.TARGET_FILE, "person12.nt");
       // param.put(MapKey.TRAINING, "dataset11_dataset12_training_person.xml");

        String type = "-Person";
        param.put(MapKey.EVALUATION_RESULTS_FOLDER, getEvalFolder());
        param.put(MapKey.EVALUATION_FILENAME, "Pseudo_eval_Persons1.csv");
        param.put(MapKey.NAME, "Persons1");
        // data
        AConfigurationReader cR = new XMLConfigurationReader(
                "" + param.get(MapKey.BASE_FOLDER) + param.get(MapKey.CONFIG_FILE));
        cR.read();
        param.put(MapKey.CONFIG_READER, cR);

        param.put(MapKey.PROPERTY_MAPPING, PropMapper.getPropertyMappingFromFile((String) param.get(MapKey.BASE_FOLDER),
                (String) param.get(MapKey.CONFIG_FILE)));
        param.put(MapKey.SOURCE_CACHE, Experiment.readOAEIFile(
                (String) param.get(MapKey.DATASET_FOLDER) + (String) param.get(MapKey.SOURCE_FILE), type));
        param.put(MapKey.SOURCE_CACHE, Experiment.readOAEIFile(
                (String) param.get(MapKey.DATASET_FOLDER) + (String) param.get(MapKey.SOURCE_FILE), type));

        param.put(MapKey.TARGET_CACHE, Experiment.readOAEIFile(
                (String) param.get(MapKey.DATASET_FOLDER) + (String) param.get(MapKey.TARGET_FILE), type));
        param.put(MapKey.REFERENCE_MAPPING, Experiment.readOAEIMapping(
                (String) param.get(MapKey.DATASET_FOLDER) + (String) param.get(MapKey.REFERENCE_FILE)));

        param.put(MapKey.SOURCE_CLASS, "http://www.okkam.org/ontology_person1.owl#Person");
        param.put(MapKey.TARGET_CLASS, "okkamperson2:Person");

        return param;
    }

    private static HashMap<MapKey, Object> getPerson1() {
        HashMap<MapKey, Object> param = new HashMap<MapKey, Object>();
        // folders & files
        param.put(MapKey.BASE_FOLDER, "src/main/resources/datasets/");
        param.put(MapKey.DATASET_FOLDER, "src/main/resources/datasets/Persons1/");
        param.put(MapKey.CONFIG_FILE, "persons1.xml");
        param.put(MapKey.REFERENCE_FILE, "dataset11_dataset12_goldstandard_person.xml");
        param.put(MapKey.SOURCE_FILE, "person11.nt");
        param.put(MapKey.TARGET_FILE, "person12.nt");
        String type = "-Person";
        param.put(MapKey.EVALUATION_RESULTS_FOLDER, getEvalFolder());
        param.put(MapKey.EVALUATION_FILENAME, "Pseudo_eval_Persons1.csv");
        param.put(MapKey.NAME, "Persons1");
        // data
        AConfigurationReader cR = new XMLConfigurationReader(
                "" + param.get(MapKey.BASE_FOLDER) + param.get(MapKey.CONFIG_FILE));
        cR.read();
        param.put(MapKey.CONFIG_READER, cR);

        param.put(MapKey.PROPERTY_MAPPING, PropMapper.getPropertyMappingFromFile((String) param.get(MapKey.BASE_FOLDER),
                (String) param.get(MapKey.CONFIG_FILE)));
        param.put(MapKey.SOURCE_CACHE, Experiment.readOAEIFile(
                (String) param.get(MapKey.DATASET_FOLDER) + (String) param.get(MapKey.SOURCE_FILE), type));
        param.put(MapKey.SOURCE_CACHE, Experiment.readOAEIFile(
                (String) param.get(MapKey.DATASET_FOLDER) + (String) param.get(MapKey.SOURCE_FILE), type));

        param.put(MapKey.TARGET_CACHE, Experiment.readOAEIFile(
                (String) param.get(MapKey.DATASET_FOLDER) + (String) param.get(MapKey.TARGET_FILE), type));
        param.put(MapKey.REFERENCE_MAPPING, Experiment.readOAEIMapping(
                (String) param.get(MapKey.DATASET_FOLDER) + (String) param.get(MapKey.REFERENCE_FILE)));

        param.put(MapKey.SOURCE_CLASS, "http://www.okkam.org/ontology_person1.owl#Person");
        param.put(MapKey.TARGET_CLASS, "okkamperson2:Person");

        return param;
    }

    private static HashMap<MapKey, Object> getOAEI2014Books() {
        HashMap<MapKey, Object> param = new HashMap<MapKey, Object>();
        // folders & files
        param.put(MapKey.BASE_FOLDER, "src/main/resources/datasets/");
        param.put(MapKey.DATASET_FOLDER, "src/main/resources/datasets/OAEI2014/");
        param.put(MapKey.CONFIG_FILE, "oaei2014_identity.xml");
        param.put(MapKey.REFERENCE_FILE, "oaei2014_identity_mappings.rdf");
        param.put(MapKey.SOURCE_FILE, "oaei2014_identity_a.owl");
        param.put(MapKey.TARGET_FILE, "oaei2014_identity_b.owl");
        param.put(MapKey.EVALUATION_RESULTS_FOLDER, getEvalFolder());
        param.put(MapKey.EVALUATION_FILENAME, "Pseudo_eval_OAEI2014.csv");
        param.put(MapKey.NAME, "OAEI2014");
        // data
        AConfigurationReader cR = new XMLConfigurationReader(
                "" + param.get(MapKey.BASE_FOLDER) + param.get(MapKey.CONFIG_FILE));
        cR.read();
        param.put(MapKey.CONFIG_READER, cR);

        param.put(MapKey.PROPERTY_MAPPING, PropMapper.getPropertyMappingFromFile((String) param.get(MapKey.BASE_FOLDER),
                (String) param.get(MapKey.CONFIG_FILE)));
        param.put(MapKey.SOURCE_CACHE, HybridCache.getData(cR.getConfiguration().getSourceInfo()));
        param.put(MapKey.TARGET_CACHE, HybridCache.getData(cR.getConfiguration().getTargetInfo()));
        OAEIMappingParser mappingParser = new OAEIMappingParser(
                (String) param.get(MapKey.BASE_FOLDER) + (String) param.get(MapKey.REFERENCE_FILE));
        param.put(MapKey.REFERENCE_MAPPING, mappingParser.parseDocument());

        param.put(MapKey.SOURCE_CLASS, "oaei2014:Book");
        param.put(MapKey.TARGET_CLASS, "oaei2014:Book");
        return param;
    }

    private static HashMap<MapKey, Object> getPerson1CSV() {
        HashMap<MapKey, Object> param = new HashMap<MapKey, Object>();
        // folders & files
        param.put(MapKey.BASE_FOLDER, "src/main/resources/datasets/");
        param.put(MapKey.DATASET_FOLDER, "src/main/resources/datasets/Persons1/");
        param.put(MapKey.CONFIG_FILE, "persons1_csv.xml");
        param.put(MapKey.REFERENCE_FILE, "dataset11_dataset12_goldstandard_person.xml.csv");
        param.put(MapKey.SOURCE_FILE, "person11.nt");
        param.put(MapKey.TARGET_FILE, "person12.nt");
        param.put(MapKey.EVALUATION_RESULTS_FOLDER, "resources/results/");
        param.put(MapKey.EVALUATION_FILENAME, "Pseudo_eval_Persons1.csv");
        param.put(MapKey.NAME, "Persons1_CSV");
        AConfigurationReader cR = new XMLConfigurationReader(
                (String) param.get(MapKey.BASE_FOLDER) + (String) param.get(MapKey.CONFIG_FILE));
        cR.read();
        HybridCache sC = HybridCache.getData(cR.getConfiguration().getSourceInfo());
        HybridCache tC = HybridCache.getData(cR.getConfiguration().getTargetInfo());
        // data
        param.put(MapKey.CONFIG_READER, cR);
        param.put(MapKey.PROPERTY_MAPPING, PropMapper.getPropertyMappingFromFile((String) param.get(MapKey.BASE_FOLDER),
                (String) param.get(MapKey.CONFIG_FILE)));
        param.put(MapKey.SOURCE_CACHE, sC);
        param.put(MapKey.TARGET_CACHE, tC);
        IOracle o = OracleFactory
                .getOracle("" + /* param.get(MapKey.BASE_FOLDER)+ */param.get(MapKey.DATASET_FOLDER)
                        + param.get(MapKey.REFERENCE_FILE), "CSV", "simple");
        param.put(MapKey.REFERENCE_MAPPING, o.getMapping());
        return param;
    }

    private static HashMap<MapKey, Object> getPerson2() {
        HashMap<MapKey, Object> param = new HashMap<MapKey, Object>();
        // folders & files
        param.put(MapKey.BASE_FOLDER, "src/main/resources/datasets/");
        param.put(MapKey.DATASET_FOLDER, "src/main/resources/datasets/Persons2/");
        param.put(MapKey.CONFIG_FILE, "persons2.xml");
        param.put(MapKey.REFERENCE_FILE, "dataset21_dataset22_goldstandard_person.xml");
        param.put(MapKey.SOURCE_FILE, "person21.nt");
        param.put(MapKey.TARGET_FILE, "person22.nt");
        String type = "-Person";
        param.put(MapKey.EVALUATION_RESULTS_FOLDER, "resources/results/");
        param.put(MapKey.EVALUATION_FILENAME, "Pseudo_eval_Persons2.csv");
        param.put(MapKey.NAME, "Persons2");
        // data
        // Cache sC =
        // Experiment.readOAEIFile((String)param.get(MapKey.DATASET_FOLDER)+(String)param.get(MapKey.SOURCE_FILE),
        // type);
        // Experiment.toCsvFile(sC,
        // (String)param.get(MapKey.DATASET_FOLDER)+(String)param.get(MapKey.SOURCE_FILE)+".csv");
        // Cache tC =
        // Experiment.readOAEIFile((String)param.get(MapKey.DATASET_FOLDER)+(String)param.get(MapKey.TARGET_FILE),
        // type);
        // Experiment.toCsvFile(tC,
        // (String)param.get(MapKey.DATASET_FOLDER)+(String)param.get(MapKey.TARGET_FILE)+".csv");
        // Experiment.toCsvFile(Experiment.readOAEIMapping((String)param.get(MapKey.DATASET_FOLDER)+(String)param.get(MapKey.REFERENCE_FILE)),
        // (String)param.get(MapKey.DATASET_FOLDER)+(String)param.get(MapKey.REFERENCE_FILE)+".csv");
        AConfigurationReader cR = new XMLConfigurationReader(
                "" + param.get(MapKey.BASE_FOLDER) + param.get(MapKey.CONFIG_FILE));
        cR.read();
        param.put(MapKey.CONFIG_READER, cR);
        param.put(MapKey.PROPERTY_MAPPING, PropMapper.getPropertyMappingFromFile((String) param.get(MapKey.BASE_FOLDER),
                (String) param.get(MapKey.CONFIG_FILE)));
        param.put(MapKey.SOURCE_CACHE, Experiment.readOAEIFile(
                (String) param.get(MapKey.DATASET_FOLDER) + (String) param.get(MapKey.SOURCE_FILE), type));
        param.put(MapKey.TARGET_CACHE, Experiment.readOAEIFile(
                (String) param.get(MapKey.DATASET_FOLDER) + (String) param.get(MapKey.TARGET_FILE), type));
        param.put(MapKey.REFERENCE_MAPPING, Experiment.readOAEIMapping(
                (String) param.get(MapKey.DATASET_FOLDER) + (String) param.get(MapKey.REFERENCE_FILE)));

        param.put(MapKey.SOURCE_CLASS, "http://www.okkam.org/ontology_person1.owl#Person");
        param.put(MapKey.TARGET_CLASS, "okkamperson2:Person");
        return param;
    }

    private static HashMap<MapKey, Object> getPerson2CSV() {
        HashMap<MapKey, Object> param = new HashMap<MapKey, Object>();
        // folders & files
        param.put(MapKey.BASE_FOLDER, "src/main/resources/datasets/");
        param.put(MapKey.DATASET_FOLDER, "src/main/resources/datasets/Persons2/");
        param.put(MapKey.CONFIG_FILE, "persons2_csv.xml");
        param.put(MapKey.REFERENCE_FILE, "dataset21_dataset22_goldstandard_person.xml.csv");
        param.put(MapKey.SOURCE_FILE, "person21.nt");
        param.put(MapKey.TARGET_FILE, "person22.nt");
        param.put(MapKey.EVALUATION_FILENAME, "Pseudo_eval_Persons2.csv");
        param.put(MapKey.NAME, "Persons2_CSV");
        // data
        AConfigurationReader cR = new XMLConfigurationReader(
                (String) param.get(MapKey.BASE_FOLDER) + (String) param.get(MapKey.CONFIG_FILE));
        cR.read();
        HybridCache sC = HybridCache.getData(cR.getConfiguration().getSourceInfo());
        HybridCache tC = HybridCache.getData(cR.getConfiguration().getTargetInfo());

        param.put(MapKey.CONFIG_READER, cR);
        param.put(MapKey.PROPERTY_MAPPING, PropMapper.getPropertyMappingFromFile((String) param.get(MapKey.BASE_FOLDER),
                (String) param.get(MapKey.CONFIG_FILE)));
        param.put(MapKey.SOURCE_CACHE, sC);
        param.put(MapKey.TARGET_CACHE, tC);
        IOracle o = OracleFactory
                .getOracle("" + /* param.get(MapKey.BASE_FOLDER)+ */param.get(MapKey.DATASET_FOLDER)
                        + param.get(MapKey.REFERENCE_FILE), "CSV", "simple");
        param.put(MapKey.REFERENCE_MAPPING, o.getMapping());
        return param;
    }

    private static HashMap<MapKey, Object> getRestaurant() {
        HashMap<MapKey, Object> param = new HashMap<MapKey, Object>();
        // folders & files
        param.put(MapKey.BASE_FOLDER, "src/main/resources/datasets/");
        param.put(MapKey.DATASET_FOLDER, "src/main/resources/datasets/Restaurants/");
        param.put(MapKey.CONFIG_FILE, "restaurants.xml");
        param.put(MapKey.REFERENCE_FILE, "restaurant1_restaurant2_goldstandard.rdf");
        param.put(MapKey.SOURCE_FILE, "restaurant1.nt");
        param.put(MapKey.TARGET_FILE, "restaurant2.nt");
        String type = "-Restaurant";
        param.put(MapKey.EVALUATION_FILENAME, "Pseudo_eval_Restaurants.csv");
        param.put(MapKey.NAME, "Restaurants");
        // data
        // Cache sC =
        // Experiment.readOAEIFile((String)param.get(MapKey.DATASET_FOLDER)+(String)param.get(MapKey.SOURCE_FILE),
        // type);
        // Experiment.toCsvFile(sC,
        // (String)param.get(MapKey.DATASET_FOLDER)+(String)param.get(MapKey.SOURCE_FILE)+".csv");
        // Cache tC =
        // Experiment.readOAEIFile((String)param.get(MapKey.DATASET_FOLDER)+(String)param.get(MapKey.TARGET_FILE),
        // type);
        // Experiment.toCsvFile(tC,
        // (String)param.get(MapKey.DATASET_FOLDER)+(String)param.get(MapKey.TARGET_FILE)+".csv");
        // Experiment.toCsvFile(Experiment.readOAEIMapping((String)param.get(MapKey.DATASET_FOLDER)+(String)param.get(MapKey.REFERENCE_FILE)),
        // (String)param.get(MapKey.DATASET_FOLDER)+(String)param.get(MapKey.REFERENCE_FILE)+".csv");

        AConfigurationReader cR = new XMLConfigurationReader(
                "" + param.get(MapKey.BASE_FOLDER) + param.get(MapKey.CONFIG_FILE));
        cR.read();
        param.put(MapKey.CONFIG_READER, cR);
        param.put(MapKey.PROPERTY_MAPPING, PropMapper.getPropertyMappingFromFile((String) param.get(MapKey.BASE_FOLDER),
                (String) param.get(MapKey.CONFIG_FILE)));
        param.put(MapKey.SOURCE_CACHE,
                Experiment.readOAEIFile(
                        /* (String)param.get(MapKey.BASE_FOLDER)+ */param.get(MapKey.DATASET_FOLDER)
                                + (String) param.get(MapKey.SOURCE_FILE),
                        type));
        param.put(MapKey.TARGET_CACHE,
                Experiment.readOAEIFile(
                        /* (String)param.get(MapKey.BASE_FOLDER)+ */param.get(MapKey.DATASET_FOLDER)
                                + (String) param.get(MapKey.TARGET_FILE),
                        type));
        param.put(MapKey.REFERENCE_MAPPING,
                Experiment.readOAEIMapping(
                        /* (String)param.get(MapKey.BASE_FOLDER)+ */param.get(MapKey.DATASET_FOLDER)
                                + (String) param.get(MapKey.REFERENCE_FILE)));

        param.put(MapKey.SOURCE_CLASS, "http://www.okkam.org/ontology_restaurant1.owl#Restaurant");
        param.put(MapKey.TARGET_CLASS, "http://www.okkam.org/ontology_restaurant2.owl#Restaurant");
        return param;
    }

    private static HashMap<MapKey, Object> getRestaurantCSV() {

        HashMap<MapKey, Object> param = new HashMap<MapKey, Object>();
        // folders & files
        param.put(MapKey.BASE_FOLDER, "src/main/resources/datasets/");
        param.put(MapKey.DATASET_FOLDER, "src/main/resources/datasets/Restaurants/");
        param.put(MapKey.CONFIG_FILE, "restaurants_csv.xml");
        param.put(MapKey.REFERENCE_FILE, "restaurant1_restaurant2_goldstandard.rdf.csv");
        param.put(MapKey.SOURCE_FILE, "restaurant1.nt");
        param.put(MapKey.TARGET_FILE, "restaurant2.nt");
        param.put(MapKey.EVALUATION_RESULTS_FOLDER, "resources/results/");
        param.put(MapKey.EVALUATION_FILENAME, "Pseudo_eval_Restaurants.csv");
        param.put(MapKey.NAME, "Restaurants_CSV");
        // data
        AConfigurationReader cR = new XMLConfigurationReader(
                (String) param.get(MapKey.BASE_FOLDER) + (String) param.get(MapKey.CONFIG_FILE));
        cR.read();
        HybridCache sC = HybridCache.getData(cR.getConfiguration().getSourceInfo());
        HybridCache tC = HybridCache.getData(cR.getConfiguration().getTargetInfo());

        param.put(MapKey.CONFIG_READER, cR);
        param.put(MapKey.PROPERTY_MAPPING, PropMapper.getPropertyMappingFromFile((String) param.get(MapKey.BASE_FOLDER),
                (String) param.get(MapKey.CONFIG_FILE)));
        param.put(MapKey.SOURCE_CACHE, sC);
        param.put(MapKey.TARGET_CACHE, tC);
        IOracle o = OracleFactory
                .getOracle(""/* +param.get(MapKey.BASE_FOLDER) */ + param.get(MapKey.DATASET_FOLDER)
                        + param.get(MapKey.REFERENCE_FILE), "CSV", "simple");
        param.put(MapKey.REFERENCE_MAPPING, o.getMapping());

        param.put(MapKey.SOURCE_CLASS, "http://www.okkam.org/ontology_restaurant1.owl#Restaurant");
        param.put(MapKey.TARGET_CLASS, "http://www.okkam.org/ontology_restaurant2.owl#Restaurant");
        return param;
    }

    private static HashMap<MapKey, Object> getDBLPACM() {
        HashMap<MapKey, Object> param = new HashMap<MapKey, Object>();
        // folders & files
        param.put(MapKey.BASE_FOLDER, "src/main/resources/datasets/"/* "Examples/GeneticEval/" */);
        param.put(MapKey.DATASET_FOLDER, "src/main/resources/datasets/DBLP-ACM/" /* "Datasets/DBLP-ACM/" */);
        param.put(MapKey.CONFIG_FILE, "PublicationData.xml");
        param.put(MapKey.REFERENCE_FILE, "DBLP-ACM_perfectMapping.csv");
        param.put(MapKey.SOURCE_FILE, "ACM.csv");
        param.put(MapKey.TARGET_FILE, "DBLP2.csv");

        param.put(MapKey.EVALUATION_RESULTS_FOLDER, "resources/results/");
        param.put(MapKey.EVALUATION_FILENAME, "Pseudo_eval_DBLP-ACM.csv");
        param.put(MapKey.NAME, "DBLP-ACM");
        // data
        AConfigurationReader cR = new XMLConfigurationReader(
                (String) param.get(MapKey.BASE_FOLDER) + param.get(MapKey.CONFIG_FILE));
        cR.read();
        param.put(MapKey.CONFIG_READER, cR);
        param.put(MapKey.PROPERTY_MAPPING, PropMapper.getPropertyMappingFromFile((String) param.get(MapKey.BASE_FOLDER),
                (String) param.get(MapKey.CONFIG_FILE)));
        param.put(MapKey.SOURCE_CACHE, HybridCache.getData(cR.getConfiguration().getSourceInfo()));
        param.put(MapKey.TARGET_CACHE, HybridCache.getData(cR.getConfiguration().getTargetInfo()));
        param.put(MapKey.REFERENCE_MAPPING,
                OracleFactory
                        .getOracle((String) /* param.get(MapKey.BASE_FOLDER)+ */param.get(MapKey.DATASET_FOLDER)
                                + param.get(MapKey.REFERENCE_FILE), "csv", "simple")
                        .getMapping());
        param.put(MapKey.SOURCE_CLASS, "dblp:book");
        param.put(MapKey.TARGET_CLASS, "acm:book");
        return param;
    }

    private static HashMap<MapKey, Object> getAbtBuy() {
        HashMap<MapKey, Object> param = new HashMap<MapKey, Object>();
        // folders & files
        param.put(MapKey.BASE_FOLDER, "src/main/resources/datasets/"/* "Examples/GeneticEval/" */);
        param.put(MapKey.DATASET_FOLDER, "src/main/resources/datasets/Abt-Buy/" /* "Datasets/Abt-Buy/" */);
        param.put(MapKey.CONFIG_FILE, "Abt-Buy.xml");
        param.put(MapKey.REFERENCE_FILE, "abt_buy_perfectMapping.csv");
        param.put(MapKey.SOURCE_FILE, "Abt.csv");
        param.put(MapKey.TARGET_FILE, "Buy.csv");

        param.put(MapKey.EVALUATION_RESULTS_FOLDER, "resources/results/");
        param.put(MapKey.EVALUATION_FILENAME, "Pseudo_eval_Abt-Buy.csv");
        param.put(MapKey.NAME, "Abt-Buy");
        // data
        AConfigurationReader cR = new XMLConfigurationReader(
                (String) param.get(MapKey.BASE_FOLDER) + param.get(MapKey.CONFIG_FILE));
        cR.read();

        param.put(MapKey.CONFIG_READER, cR);
        param.put(MapKey.PROPERTY_MAPPING, PropMapper.getPropertyMappingFromFile((String) param.get(MapKey.BASE_FOLDER),
                (String) param.get(MapKey.CONFIG_FILE)));
        param.put(MapKey.SOURCE_CACHE, HybridCache.getData(cR.getConfiguration().getSourceInfo()));
        param.put(MapKey.TARGET_CACHE, HybridCache.getData(cR.getConfiguration().getTargetInfo()));
        param.put(MapKey.REFERENCE_MAPPING,
                OracleFactory
                        .getOracle((String) /* param.get(MapKey.BASE_FOLDER)+ */param.get(MapKey.DATASET_FOLDER)
                                + param.get(MapKey.REFERENCE_FILE), "csv", "simple")
                        .getMapping());
        param.put(MapKey.SOURCE_CLASS, "abt:product");
        param.put(MapKey.TARGET_CLASS, "buy:product");
        return param;
    }

    private static HashMap<MapKey, Object> getDBLPScholar() {
        HashMap<MapKey, Object> param = new HashMap<MapKey, Object>();
        // folders & files
        param.put(MapKey.BASE_FOLDER, "src/main/resources/datasets/"/* "Examples/GeneticEval/" */);
        param.put(MapKey.DATASET_FOLDER, "src/main/resources/datasets/DBLP-Scholar/"/* "Datasets/DBLP-Scholar/" */);
        param.put(MapKey.CONFIG_FILE, "DBLP-Scholar.xml");
        param.put(MapKey.REFERENCE_FILE, "DBLP-Scholar_perfectMapping.csv");
        param.put(MapKey.SOURCE_FILE, "DBLP1.csv");
        param.put(MapKey.TARGET_FILE, "Scholar.csv");

        param.put(MapKey.EVALUATION_RESULTS_FOLDER, "resources/results/");
        param.put(MapKey.EVALUATION_FILENAME, "Pseudo_eval_DBLP-Scholar.csv");
        param.put(MapKey.NAME, "DBLP-SCHOLAR");
        // data
        AConfigurationReader cR = new XMLConfigurationReader(
                (String) param.get(MapKey.BASE_FOLDER) + param.get(MapKey.CONFIG_FILE));
        cR.read();

        param.put(MapKey.CONFIG_READER, cR);
        param.put(MapKey.PROPERTY_MAPPING, PropMapper.getPropertyMappingFromFile((String) param.get(MapKey.BASE_FOLDER),
                (String) param.get(MapKey.CONFIG_FILE)));

        param.put(MapKey.SOURCE_CACHE, HybridCache.getData(cR.getConfiguration().getSourceInfo()));
        param.put(MapKey.TARGET_CACHE, HybridCache.getData(cR.getConfiguration().getTargetInfo()));
        param.put(MapKey.REFERENCE_MAPPING,
                OracleFactory
                        .getOracle((String) /* param.get(MapKey.BASE_FOLDER)+ */param.get(MapKey.DATASET_FOLDER)
                                + param.get(MapKey.REFERENCE_FILE), "csv", "simple")
                        .getMapping());

        param.put(MapKey.SOURCE_CLASS, "dblp:book");
        param.put(MapKey.TARGET_CLASS, "scholar:book");
        return param;
    }

    private static HashMap<MapKey, Object> getAmazonGoogleProducts() {
        HashMap<MapKey, Object> param = new HashMap<MapKey, Object>();
        // folders & files
        param.put(MapKey.BASE_FOLDER, "src/main/resources/datasets/");
        param.put(MapKey.DATASET_FOLDER, "src/main/resources/datasets/Amazon-GoogleProducts/");
        param.put(MapKey.CONFIG_FILE, "Amazon-GoogleProducts.xml");
        param.put(MapKey.REFERENCE_FILE, "Amzon_GoogleProducts_perfectMapping.csv");
        param.put(MapKey.SOURCE_FILE, "Amazon.csv");
        param.put(MapKey.TARGET_FILE, "GoogleProducts.csv");

        param.put(MapKey.EVALUATION_RESULTS_FOLDER, "resources/results/");
        param.put(MapKey.EVALUATION_FILENAME, "Pseudo_eval_Amazon-GoogleProducts.csv");
        param.put(MapKey.NAME, "Amazon-GoogleProducts");
        // data
        AConfigurationReader cR = new XMLConfigurationReader(
                (String) param.get(MapKey.BASE_FOLDER) + param.get(MapKey.CONFIG_FILE));
        cR.read();

        param.put(MapKey.CONFIG_READER, cR);
        param.put(MapKey.PROPERTY_MAPPING, PropMapper.getPropertyMappingFromFile((String) param.get(MapKey.BASE_FOLDER),
                (String) param.get(MapKey.CONFIG_FILE)));
        param.put(MapKey.SOURCE_CACHE, HybridCache.getData(cR.getConfiguration().getSourceInfo()));
        param.put(MapKey.TARGET_CACHE, HybridCache.getData(cR.getConfiguration().getTargetInfo()));
        param.put(MapKey.REFERENCE_MAPPING,
                OracleFactory
                        .getOracle((String) /* param.get(MapKey.BASE_FOLDER)+ */param.get(MapKey.DATASET_FOLDER)
                                + param.get(MapKey.REFERENCE_FILE), "csv", "simple")
                        .getMapping());

        param.put(MapKey.SOURCE_CLASS, "amazon:product");
        param.put(MapKey.TARGET_CLASS, "google:product");
        return param;
    }

    private static HashMap<MapKey, Object> getDBPediaLinkedMDB() {
        HashMap<MapKey, Object> param = new HashMap<MapKey, Object>();
        // folders & files
        param.put(MapKey.BASE_FOLDER, "src/main/resources/datasets/");
        param.put(MapKey.DATASET_FOLDER, "src/main/resources/datasets/dbpedia-linkedmdb/");
        param.put(MapKey.CONFIG_FILE, "dbpedia-linkedmdb.xml");
        param.put(MapKey.REFERENCE_FILE, "reference.csv");
        param.put(MapKey.SOURCE_FILE, "source.csv");
        param.put(MapKey.TARGET_FILE, "target.csv");

        param.put(MapKey.EVALUATION_RESULTS_FOLDER, "resources/results/");
        param.put(MapKey.EVALUATION_FILENAME, "Pseudo_eval_DBPedia-LinkedMDB.csv");
        param.put(MapKey.NAME, "DBPedia-LinkedMDB");
        // data
        AConfigurationReader cR = new XMLConfigurationReader(
                (String) param.get(MapKey.BASE_FOLDER) + param.get(MapKey.CONFIG_FILE));
        cR.read();

        param.put(MapKey.CONFIG_READER, cR);
        param.put(MapKey.PROPERTY_MAPPING, PropMapper.getPropertyMappingFromFile((String) param.get(MapKey.BASE_FOLDER),
                (String) param.get(MapKey.CONFIG_FILE)));
        param.put(MapKey.SOURCE_CACHE, HybridCache.getData(cR.getConfiguration().getSourceInfo()));
        param.put(MapKey.TARGET_CACHE, HybridCache.getData(cR.getConfiguration().getTargetInfo()));
        param.put(MapKey.REFERENCE_MAPPING,
                OracleFactory
                        .getOracle((String) /* param.get(MapKey.BASE_FOLDER)+ */param.get(MapKey.DATASET_FOLDER)
                                + param.get(MapKey.REFERENCE_FILE), "tab", "simple")
                        .getMapping());

        param.put(MapKey.SOURCE_CLASS, "dbpedia:film");
        param.put(MapKey.TARGET_CLASS, "linkedmdb:movie");
        return param;
    }

    private static HashMap<MapKey, Object> getDrugs() {
        HashMap<MapKey, Object> param = new HashMap<MapKey, Object>();
        // folders & files
        param.put(MapKey.BASE_FOLDER, "src/main/resources/datasets/");
        param.put(MapKey.DATASET_FOLDER, "src/main/resources/datasets/dailymed-drugbank-ingredients/");
        param.put(MapKey.CONFIG_FILE, "dailymed-drugbank.xml");
        param.put(MapKey.REFERENCE_FILE, "reference2.csv");
        param.put(MapKey.SOURCE_FILE, "source2.csv");
        param.put(MapKey.TARGET_FILE, "target2.csv");

        param.put(MapKey.EVALUATION_RESULTS_FOLDER, "resources/results/");
        param.put(MapKey.EVALUATION_FILENAME, "Pseudo_eval_Drugs.csv");
        param.put(MapKey.NAME, "Drugs");
        // data
        AConfigurationReader cR = new XMLConfigurationReader(
                (String) param.get(MapKey.BASE_FOLDER) + param.get(MapKey.CONFIG_FILE));
        cR.read();

        param.put(MapKey.CONFIG_READER, cR);
        param.put(MapKey.PROPERTY_MAPPING, PropMapper.getPropertyMappingFromFile((String) param.get(MapKey.BASE_FOLDER),
                (String) param.get(MapKey.CONFIG_FILE)));
        param.put(MapKey.SOURCE_CACHE, HybridCache.getData(cR.getConfiguration().getSourceInfo()));
        param.put(MapKey.TARGET_CACHE, HybridCache.getData(cR.getConfiguration().getTargetInfo()));
        param.put(MapKey.REFERENCE_MAPPING,
                OracleFactory
                        .getOracle((String) /* param.get(MapKey.BASE_FOLDER)+ */param.get(MapKey.DATASET_FOLDER)
                                + param.get(MapKey.REFERENCE_FILE), "csv", "simple")
                        .getMapping());

        param.put(MapKey.SOURCE_CLASS, "dailymed:drug");
        param.put(MapKey.TARGET_CLASS, "drugbank:drug");
        return param;
    }

    public static Set<MapKey> getLoggingKeys() {
        HashSet<MapKey> set = new HashSet<MapKey>();
        set.add(MapKey.NAME);
        return set;
    }

    /**
     * Static getter for the common evaluation folder,
     * @return String- the path to the evaluation folder
     */
    public static String getEvalFolder() {
        return "resources/results/";
    }

    /*
     * public static void main(String args[]) { // for(DataSets ds :
     * DataSets.values()) // getData(ds); // // EvaluationData data =
     * DataSetChooser.getData(DataSets.RESTAURANTS); // Mapping ref =
     * fixReferenceMap(data.getReferenceMapping(), data.getSourceCache(),
     * data.getTargetCache()); //
     * System.out.println("orginal.size()="+data.getReferenceMapping().size()+
     * " - fixed.size()="+ref.size());
     * 
     * EvaluationData fixedRest =
     * DataSetChooser.getData(DataSets.RESTAURANTS_FIXED); System.out.println(
     * "RefMap fixed size="+fixedRest.getReferenceMapping().size()); }
     */

    /**
     * Method to remove mapping which corresponding instance doesn't exist.
     *
     * @param original
     *            Mapping original Mapping.
     * @param sC
     *            Source Cache.
     * @param tC
     *            Target Cache.
     * @return A Mapping holding only those mappings of the original for which
     *         instance where found in the source or target Caches.
     */
    public static AMapping fixReferenceMap(AMapping original, ACache sC, ACache tC) {
        int count = 0;
        AMapping fixed = MappingFactory.createMapping(MappingType.MEMORY_MAPPING);
        for (String sk : original.getMap().keySet()) {
            if (sC.containsUri(sk)) {
                for (String tk : original.getMap().get(sk).keySet()) {
                    if (tC.containsUri(tk)) {
                        fixed.add(sk, tk, original.getConfidence(sk, tk));
                    } else {
                        count++;
                    }
                }
            } else {
                count += original.getMap().get(sk).size();
            }
        }
        logger.info("Removed " + count + " mappings as the instances are not found in the Caches");
        return fixed;
    }

    @Test
    public void testAll() {
        try {
            for (DataSets ds : DataSets.values())
                getData(ds);
        } catch (Exception e) {
            assertTrue(false);
        }
        assertTrue(true);
    }

    /**
     * Enumeration of the Hashmap keys for the evaluation datasets.
     *
     * @author Klaus Lyko
     */
    enum MapKey {
        /**
         * Path to the folder holding the configuration XML and Property Mapping
         * file.
         **/
        BASE_FOLDER("basefolder"),
        /**
         * Path to the folder holding the files for source, target dumps
         * specified in the configuration XML.
         **/
        DATASET_FOLDER("datasetfolder"),
        /**
         * Name of the LIMES configuration XML file.
         **/
        CONFIG_FILE("config"),
        /**
         * Name of the file with the reference mapping. Complete Path via
         * concatenation with the BASE_FOLDER.
         **/
        REFERENCE_FILE("reference"),
        /**
         * Name of the file holding the source instances. Complete Path via
         * concatenation with the BASE_FOLDER and DATASET_FOLDER.
         **/
        SOURCE_FILE("file1"),
        /**
         * Name of the file holding the target instances. Complete Path via
         * concatenation with the BASE_FOLDER and DATASET_FOLDER.
         **/
        TARGET_FILE("file1"),
        /**
         * Name of the file holding training data. 
         * Complete Path via concatenation with the BASE_FOLDER and DATASET_FOLDER.
         **/
        TRAINING("training"),
        /**
         * Path to the folder where the result files should be written.
         **/
        EVALUATION_RESULTS_FOLDER("evalfolder"),
        /**
         * Common name of the evaluation result files.
         **/
        EVALUATION_FILENAME("evalfilename"),
        /**
         * Name of the experiment.
         **/
        NAME("name"),
        /**
         * Key of the field holding the Cache of the source.
         **/
        SOURCE_CACHE("sourcecache"),
        /**
         * Key of the field holding the Cache of the target.
         **/
        TARGET_CACHE("targetcache"),
        /**
         * Key of the field holding the PropertyMapping.
         **/
        PROPERTY_MAPPING("propertymapping"),
        /**
         * Key of the field holding the reference mapping.
         **/
        REFERENCE_MAPPING("referencemapping"),
        /**
         * MAX_RUNS
         **/
        MAX_RUNS("maxruns"),
        /**
         * Instance of config reader
         **/
        CONFIG_READER("configreader"),
        /**
         * Name of source class
         */
        SOURCE_CLASS("sourceclass"),
        /**
         * Name of target class
         */
        TARGET_CLASS("targetclass");
        


        private final String key;

        /**
         * @param key
         */
        private MapKey(final String key) {
            this.key = key;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Enum#toString()
         */
        public String toString() {
            return key;
        }
    }

    public enum DataSets {
        PERSON1, PERSON1_CSV, PERSON2, PERSON2_CSV, RESTAURANTS, OAEI2014BOOKS, RESTAURANTS_FIXED, DBLPACM, ABTBUY, DBLPSCHOLAR, AMAZONGOOGLEPRODUCTS, DBPLINKEDMDB, DRUGS, RESTAURANTS_CSV// ,TOWNS,
        // VILLAGES,
        // MOVIES
    }

}
