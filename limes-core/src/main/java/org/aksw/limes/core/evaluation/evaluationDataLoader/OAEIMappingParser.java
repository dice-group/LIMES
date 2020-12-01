package org.aksw.limes.core.evaluation.evaluationDataLoader;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.MappingFactory.MappingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class  is responsible for parsing the mappings generated in the OAEI tests
 *
 * @author klaus
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public class OAEIMappingParser extends DefaultHandler {
    static Logger logger = LoggerFactory.getLogger(OAEIMappingParser.class);


    AMapping m = MappingFactory.createMapping(MappingType.HYBIRD_MAPPING);
    String xmlFile = "";

    String tmpValue;
    String uri1 = "";
    String uri2 = "";
    double idValue = 0d;

    public OAEIMappingParser(String file) {
        this.xmlFile = file;
    }

  /*  
    public static void main(String[] args) {
        OAEIMappingParser parser = new OAEIMappingParser("resources/OAEI2014/oaei2014_identity_mappings.rdf");
        AMapping m = parser.parseDocument();
        System.out.println(m.size());
    }*/
/**
 * 
 * @return AMapping-The mapping of the two datasets
 */
    public AMapping parseDocument() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            parser.parse(xmlFile, this);
        } catch (ParserConfigurationException e) {
            System.err.println("ParserConfig error: ");
            e.printStackTrace();
        } catch (SAXException e) {
            System.err.println("SAXException : xml not well formed");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IO error");
            e.printStackTrace();
        }
        return m;
    }

    @Override
    public void startElement(String s, String s1, String elementName, Attributes attributes) throws SAXException {
        // if current element is book , create new book
        // clear tmpValue on start of element
        if (elementName.equalsIgnoreCase("cell")) {
            uri1 = "";
            uri2 = "";
            idValue = Double.NaN;
        }
        if (elementName.equalsIgnoreCase("entity1"))
            uri1 = attributes.getValue("rdf:resource");
        if (elementName.equalsIgnoreCase("entity2"))
            uri2 = attributes.getValue("rdf:resource");
        if (elementName.equalsIgnoreCase("measure"))
            idValue = Double.NaN;
    }

    @Override
    public void endElement(String s, String s1, String element) throws SAXException {
        // if end of book element add to list
        if (element.equalsIgnoreCase("cell")) {
            //			System.out.println("Parsed ID: "+uri1+" - "+uri2+" := "+idValue);
            m.add(uri1, uri2, idValue);
        }
        if (element.equalsIgnoreCase("measure")) {
            idValue = Double.parseDouble(tmpValue);
        }
    }

    @Override
    public void characters(char[] ac, int i, int j) throws SAXException {
        tmpValue = new String(ac, i, j);
    }

}
