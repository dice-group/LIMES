/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.limes.core.evaluation.evaluationDataLoader;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.MappingFactory.MappingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

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
