//package org.aksw.limes.core.measures.mapper.space.spark;
//
//import org.apache.spark.SparkEnv;
//import org.apache.spark.serializer.KryoSerializer;
//import scala.reflect.ClassTag;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.Serializable;
//import java.nio.ByteBuffer;
//
///**
// *  Wrapper to use arbitrary objects within Sparks tasks.
// *
// *  Spark expects all objects in the closure to implement Serializable.
// *  Since we do not want to tie ourselves to current implementations and the overhead that changes
// *  of serializable classes incur on libraries, we should use this almost everywhere.
// *
// *  It uses Kryo to efficiently serialize Arbitrary objects to bytes and then implements java
// *  default serialization on top of that.
// *
// */
//public class KryoSerializationWrapper<T> implements Serializable {
//
//    transient private T value;
//
//    private byte[] valueSerialized;
//
//    private Class<? extends T> tClass;
//
//    private transient KryoSerializer ser = null;
//
//    private KryoSerializer getSer() {
//        if (ser == null) {
//            ser = new KryoSerializer(SparkEnv.get().conf());
//        }
//        return ser;
//    }
//
//    private byte[] serialize (T o) {
//        ClassTag<T> tag = scala.reflect.ClassTag$.MODULE$.apply(tClass);
//        return getSer().newInstance().serialize(o, tag).array();
//    }
//
//    private T deserialize (byte[] bytes) {
//        ClassTag<T> tag = scala.reflect.ClassTag$.MODULE$.apply(tClass);
//        return getSer().newInstance().deserialize(ByteBuffer.wrap(bytes), tag);
//    }
//
//
//    public KryoSerializationWrapper(T value, Class<? extends T> tClass) {
//        this.value = value;
//        this.tClass = tClass;
//    }
//
//    public void setValueSerialized(byte[] bytes) {
//        valueSerialized = bytes;
//        value = deserialize(valueSerialized);
//    }
//
//    public byte[] getValueSerialized() {
//        valueSerialized = serialize(value);
//        return valueSerialized;
//    }
//
//    public T get() {
//        return value;
//    }
//
//    private void writeObject(ObjectOutputStream out) throws IOException {
//        getValueSerialized();
//        out.defaultWriteObject();
//    }
//
//    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//        in.defaultReadObject();
//        setValueSerialized(valueSerialized);
//    }
//
//}