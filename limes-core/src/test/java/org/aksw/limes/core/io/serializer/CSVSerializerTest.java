package org.aksw.limes.core.io.serializer;

import java.util.HashMap;
import java.util.Map.Entry;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;


public class CSVSerializerTest {

    public static void main(String args[]) {
        AMapping m = MappingFactory.createDefaultMapping();
        m.add("foo:a", "foo:b", 1d);
        m.add("aa", "bb", 1d);
        m.add("foo:aaaa", "foo:bb", 0.8d);

        ISerializer serial = SerializerFactory.createSerializer("csv");

        String fileName = System.getProperty("user.home") + "/";
        fileName += "test";
        serial.open(fileName);
        String predicate = "foo:sameAs";
        HashMap<String, String> prefixes = new HashMap<String, String>();
        prefixes.put("foo", "http://example.com/");
        serial.setPrefixes(prefixes);
        for (String uri1 : m.getMap().keySet()) {
            for (Entry<String, Double> e : m.getMap().get(uri1).entrySet()) {
                serial.printStatement(uri1, predicate, e.getKey(), e.getValue());
            }
        }
        serial.close();
    }

}
