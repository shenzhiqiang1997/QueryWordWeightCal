package client;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import util.WeightUtil;

public class Client {
	public static void main(String[] args) {
		Map<String, Double> wordWeights=WeightUtil.calWeight("blue trouser with pockets");
		
		Iterator<Entry<String,Double>> iterator=wordWeights.entrySet().iterator();
		while(iterator.hasNext()) {
			System.out.println(iterator.next());
		}
	}
}
