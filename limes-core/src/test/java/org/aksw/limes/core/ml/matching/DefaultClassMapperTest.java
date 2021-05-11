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
package org.aksw.limes.core.ml.matching;

import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.query.ModelRegistry;
import org.aksw.limes.core.io.query.QueryModuleFactory;
import org.aksw.limes.core.ml.algorithm.matching.DefaultClassMapper;
import org.apache.jena.rdf.model.Model;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultClassMapperTest {

    KBInfo sKB = new KBInfo();
    KBInfo tKB = new KBInfo();
    Model sModel;
    Model tModel;

    @Before
    public void init() {
        String base = "/datasets/";
        sKB.setEndpoint(LabelBasedClassMapperTest.class.getResource(base+"Persons1/person11.nt").getPath());
        sKB.setGraph(null);
        sKB.setPageSize(1000);
        sKB.setId("person11");
        tKB.setEndpoint(LabelBasedClassMapperTest.class.getResource(base+"Persons1/person12.nt").getPath());
        tKB.setGraph(null);
        tKB.setPageSize(1000);
        tKB.setId("person12");
        QueryModuleFactory.getQueryModule("nt", sKB);
        QueryModuleFactory.getQueryModule("nt", tKB);
        sModel = ModelRegistry.getInstance().getMap().get(sKB.getEndpoint());
        tModel = ModelRegistry.getInstance().getMap().get(tKB.getEndpoint());
    }

    @Test
    public void testGetEntityMapping(){
        DefaultClassMapper mapper = new DefaultClassMapper(sModel, tModel);
        AMapping m = mapper.getEntityMapping(sKB.getEndpoint(), tKB.getEndpoint(),"http://www.okkam.org/ontology_person1.owl#Person", "http://www.okkam.org/ontology_person2.owl#Person");
        AMapping correctMapping = MappingFactory.createDefaultMapping();

        correctMapping.add("http://www.okkam.org/ontology_person1.owl#State","http://www.okkam.org/ontology_person2.owl#Address",387.0);
        correctMapping.add("http://www.okkam.org/ontology_person1.owl#Person","http://www.okkam.org/ontology_person1.owl#Address",0.0);
        correctMapping.add("http://www.okkam.org/ontology_person2.owl#Person","http://www.okkam.org/ontology_person1.owl#Suburb",1.0);
        correctMapping.add("http://www.okkam.org/ontology_person1.owl#Address","http://www.okkam.org/ontology_person2.owl#Person",93.0);
        correctMapping.add("http://www.okkam.org/ontology_person1.owl#Suburb","http://www.okkam.org/ontology_person1.owl#Person",0.0);
        correctMapping.add("http://www.okkam.org/ontology_person2.owl#Address","http://www.okkam.org/ontology_person1.owl#State",387.0);
        assertEquals(correctMapping,m);
    }
}
