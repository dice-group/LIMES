package org.aksw.limes.core.measures.mapper.topology.im;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Matcher {

	public int maxSize = 1000;
	//private int j;
	private final List<Map<String, Set<String>>> result;
	private List<MBBIndex> scheduled;

	public Matcher(List<Map<String, Set<String>>>result) {

		this.result = result;
		this.scheduled = new ArrayList<>();

	}
	

	//    public void run() {
	//        Map<String, Set<String>> temp = new HashMap<>();
	//
	//        for (int i = 0; i < scheduled.size(); i += 2) {
	//            RADON_2.MBBIndex s = scheduled.get(i);
	//            RADON_2.MBBIndex t = scheduled.get(i + 1);
	//            List<Boolean>allRelations=new ArrayList<Boolean>();
	//            allRelations=relate1(s.polygon, t.polygon);
	//
	//            if (allRelations.get(0)==true) {
	//                if (!temp.containsKey(s.origin_uri)) {
	//                    temp.put(s.origin_uri, new HashSet<>());
	//                }
	//                temp.get(s.origin_uri).add(t.origin_uri);
	//            }
	//
	//        }
	//        synchronized (result) {
	//            result.add(temp);
	//        }
	//    }

	public void schedule(MBBIndex s, MBBIndex t) {
		scheduled.add(s);
		scheduled.add(t);
	}

	public int size() {
		return scheduled.size();
	}




}
