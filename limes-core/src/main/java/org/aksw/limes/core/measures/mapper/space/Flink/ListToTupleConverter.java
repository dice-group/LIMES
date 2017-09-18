package org.aksw.limes.core.measures.mapper.space.Flink;

import java.util.List;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple0;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple10;
import org.apache.flink.api.java.tuple.Tuple11;
import org.apache.flink.api.java.tuple.Tuple12;
import org.apache.flink.api.java.tuple.Tuple13;
import org.apache.flink.api.java.tuple.Tuple14;
import org.apache.flink.api.java.tuple.Tuple15;
import org.apache.flink.api.java.tuple.Tuple16;
import org.apache.flink.api.java.tuple.Tuple17;
import org.apache.flink.api.java.tuple.Tuple18;
import org.apache.flink.api.java.tuple.Tuple19;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple20;
import org.apache.flink.api.java.tuple.Tuple21;
import org.apache.flink.api.java.tuple.Tuple22;
import org.apache.flink.api.java.tuple.Tuple23;
import org.apache.flink.api.java.tuple.Tuple24;
import org.apache.flink.api.java.tuple.Tuple25;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.api.java.tuple.Tuple6;
import org.apache.flink.api.java.tuple.Tuple7;
import org.apache.flink.api.java.tuple.Tuple8;
import org.apache.flink.api.java.tuple.Tuple9;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListToTupleConverter {
    private static final Logger logger = LoggerFactory.getLogger(ListToTupleConverter.class);

    public static Tuple createEmptyNTuple(int n){
		switch(n){
		case 0: return new Tuple0();
		case 1: return new Tuple1<Integer>();
		case 2: return new Tuple2<Integer,Integer>();
		case 3: return new Tuple3<Integer,Integer,Integer>();
		case 4: return new Tuple4<Integer,Integer,Integer,Integer>();
		case 5: return new Tuple5<Integer,Integer,Integer,Integer,Integer>();
		case 6: return new Tuple6<Integer,Integer,Integer,Integer,Integer,Integer>();
		case 7: return new Tuple7<Integer,Integer,Integer,Integer,Integer,Integer,Integer>();
		case 8: return new Tuple8<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>();
		case 9: return new Tuple9<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>();
		case 10: return new Tuple10<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>();
		case 11: return new Tuple11<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>();
		case 12: return new Tuple12<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>();
		case 13: return new Tuple13<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>();
		case 14: return new Tuple14<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>();
		case 15: return new Tuple15<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>();
		case 16: return new Tuple16<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>();
		case 17: return new Tuple17<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>();
		case 18: return new Tuple18<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>();
		case 19: return new Tuple19<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>();
		case 20: return new Tuple20<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>();
		case 21: return new Tuple21<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>();
		case 22: return new Tuple22<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>();
		case 23: return new Tuple23<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>();
		case 24: return new Tuple24<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>();
		case 25: return new Tuple25<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>();
		}
		return null;
    }
	public static Tuple convertToTuple(List<Integer> list){
		if(list == null){
			return null;
		}
		if(list.size() > 25){
			logger.error("Cannot convert list bigger than 25 to Tuple");
			return null;
		}
		Tuple res = createEmptyNTuple(list.size());
		for(int i = 0; i < list.size(); i++){
				res.setField(list.get(i),i);
		}
		return res;
	}
}
