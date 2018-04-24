package org.aksw.limes.core.measures.mapper.space.spark;

import com.esotericsoftware.kryo.Kryo;
import org.aksw.limes.core.io.cache.Instance;
import org.apache.spark.serializer.KryoRegistrator;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * LIMES implementation of Spark's KryoRegistrator
 *
 * All classes that will be packaged using the Dataset API's
 * {@link org.apache.spark.sql.Encoders#kryo(Class)} need to be registered here with their
 * corresponding {@link com.esotericsoftware.kryo.Serializer} implementation
 * (the default is {@link com.esotericsoftware.kryo.serializers.FieldSerializer}).
 */
public class LimesKryoRegistrator implements KryoRegistrator {
    public void registerClasses(Kryo kryo) {
        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.register(Instance.class);
    }
}