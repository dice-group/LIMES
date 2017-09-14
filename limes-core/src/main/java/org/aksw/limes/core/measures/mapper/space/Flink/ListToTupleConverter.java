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

    public static <T> Tuple createEmptyNTuple(int n){
		switch(n){
		case 0: return new Tuple0();
		case 1: return new Tuple1<T>();
		case 2: return new Tuple2<T,T>();
		case 3: return new Tuple3<T,T,T>();
		case 4: return new Tuple4<T,T,T,T>();
		case 5: return new Tuple5<T,T,T,T,T>();
		case 6: return new Tuple6<T,T,T,T,T,T>();
		case 7: return new Tuple7<T,T,T,T,T,T,T>();
		case 8: return new Tuple8<T,T,T,T,T,T,T,T>();
		case 9: return new Tuple9<T,T,T,T,T,T,T,T,T>();
		case 10: return new Tuple10<T,T,T,T,T,T,T,T,T,T>();
		case 11: return new Tuple11<T,T,T,T,T,T,T,T,T,T,T>();
		case 12: return new Tuple12<T,T,T,T,T,T,T,T,T,T,T,T>();
		case 13: return new Tuple13<T,T,T,T,T,T,T,T,T,T,T,T,T>();
		case 14: return new Tuple14<T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
		case 15: return new Tuple15<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
		case 16: return new Tuple16<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
		case 17: return new Tuple17<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
		case 18: return new Tuple18<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
		case 19: return new Tuple19<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
		case 20: return new Tuple20<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
		case 21: return new Tuple21<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
		case 22: return new Tuple22<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
		case 23: return new Tuple23<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
		case 24: return new Tuple24<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
		case 25: return new Tuple25<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
		}
		return null;
    }
	public static <T> Tuple convertToTuple(List<T> list){
		if(list == null){
			return null;
		}
		if(list.size() > 25){
			logger.error("Cannot convert list bigger than 25 to Tuple");
			return null;
		}
		Tuple res = new Tuple0();
		switch(list.size()){
		case 0: return new Tuple0();
		case 1: res = new Tuple1<T>();
				break;
		case 2: res = new Tuple2<T,T>();
				break;
		case 3: res = new Tuple3<T,T,T>();
				break;
		case 4: res = new Tuple4<T,T,T,T>();
				break;
		case 5: res = new Tuple5<T,T,T,T,T>();
				break;
		case 6: res = new Tuple6<T,T,T,T,T,T>();
				break;
		case 7: res = new Tuple7<T,T,T,T,T,T,T>();
				break;
		case 8: res = new Tuple8<T,T,T,T,T,T,T,T>();
				break;
		case 9: res = new Tuple9<T,T,T,T,T,T,T,T,T>();
				break;
		case 10: res = new Tuple10<T,T,T,T,T,T,T,T,T,T>();
				break;
		case 11: res = new Tuple11<T,T,T,T,T,T,T,T,T,T,T>();
				break;
		case 12: res = new Tuple12<T,T,T,T,T,T,T,T,T,T,T,T>();
				break;
		case 13: res = new Tuple13<T,T,T,T,T,T,T,T,T,T,T,T,T>();
				break;
		case 14: res = new Tuple14<T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
				break;
		case 15: res = new Tuple15<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
				break;
		case 16: res = new Tuple16<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
				break;
		case 17: res = new Tuple17<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
				break;
		case 18: res = new Tuple18<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
				break;
		case 19: res = new Tuple19<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
				break;
		case 20: res = new Tuple20<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
				break;
		case 21: res = new Tuple21<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
				break;
		case 22: res = new Tuple22<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
				break;
		case 23: res = new Tuple23<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
				break;
		case 24: res = new Tuple24<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
				break;
		case 25: res = new Tuple25<T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T,T>();
				break;
		}
		for(int i = 0; i < list.size(); i++){
				res.setField(list.get(i),i);
		}
		return res;
	}
}
