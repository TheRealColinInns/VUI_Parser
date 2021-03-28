import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class SearchQuery {
	public static ArrayList<ArrayList<String>> exactSearch(Map<String, Map<String, Collection<Integer>>> mainMap, Map<String, Integer> wordCountMap, TreeSet<String> querySet){
		double wordCount;
		ArrayList<ArrayList<String>> queryResult = new ArrayList<ArrayList<String>>();
		for(String wordKey:mainMap.keySet()) {
			
			for(String pathKey:mainMap.get(wordKey).keySet()) {
				//System.out.println("keySet: "+flippedMap.get(mainKey).keySet());
				int resultCount = 0;
				for(String queryWord:querySet) {
					//System.out.println("Result Count Check: "+queryWord+" | "+wordKey);
					if(wordKey.equals(queryWord)) {
						resultCount+=mainMap.get(wordKey).get(pathKey).size();
					}
				
				}
				if(resultCount>0) {
					wordCount = wordCountMap.get(pathKey);
					ArrayList<String> singleQueryResult = new ArrayList<String>();
					singleQueryResult.add(String.valueOf(resultCount/wordCount));
					singleQueryResult.add(String.valueOf(wordCount));
					singleQueryResult.add(pathKey);
					queryResult.add(singleQueryResult);
				}
			
			}
		}
		System.out.println("Before: "+queryResult.toString());
		for(int i = 0; i<queryResult.size(); i++) {
			if(queryResult.get(i).isEmpty()) {
				queryResult.remove(i);
			}
		}
		if(queryResult.isEmpty()) {
			return null;
		}
		System.out.println("After: "+queryResult.toString());
		return resultSorter(queryResult);
	}
	public static ArrayList<ArrayList<String>> partialSearch(Map<String, Map<String, Collection<Integer>>> mainMap, Map<String, Integer> wordCountMap, TreeSet<String> querySet){
		double wordCount;
		ArrayList<ArrayList<String>> queryResult = new ArrayList<ArrayList<String>>();
		for(String wordKey:mainMap.keySet()) {
			//System.out.println("Main Key: "+mainKey);
			for(String pathKey:mainMap.get(wordKey).keySet()) {
				//System.out.println("keySet: "+flippedMap.get(mainKey).keySet());
				int resultCount = 0;
				for(String queryWord:querySet) {
					//System.out.println("Result Count Check: "+queryWord+" | "+secondaryKey);
					if(wordKey.contains(queryWord)) {
						resultCount+=mainMap.get(wordKey).get(pathKey).size();
					}
				
				}
				if(resultCount>0) {
					wordCount = wordCountMap.get(pathKey);
					ArrayList<String> singleQueryResult = new ArrayList<String>();
					singleQueryResult.add(String.valueOf(resultCount/wordCount));
					singleQueryResult.add(String.valueOf(wordCount));
					singleQueryResult.add(pathKey);
					queryResult.add(singleQueryResult);
				}
			
			}
		}
		for(int i = 0; i<queryResult.size(); i++) {
			if(queryResult.get(i).isEmpty()) {
				queryResult.remove(i);
			}
		}
		return resultSorter(queryResult);
	}
	private static ArrayList<ArrayList<String>> resultSorter(ArrayList<ArrayList<String>> helperList){
		ArrayList<ArrayList<String>> sortedList = new ArrayList<ArrayList<String>>();
		System.out.println("Original List: "+helperList.toString());
		sortedList.add(helperList.get(0));
		for(int i = 1; i<helperList.size(); i++) {
			for(int j = 0; j<sortedList.size(); j++) {
				if(Double.valueOf(helperList.get(i).get(0))>Double.valueOf(sortedList.get(j).get(0))) {
					sortedList.add(j, helperList.get(i));
					//System.out.println("New Sorted List 1: "+sortedList.toString());
				}
				else if(Double.valueOf(helperList.get(i).get(0))==Double.valueOf(sortedList.get(j).get(0))) {
					if(Double.valueOf(helperList.get(i).get(1))>Double.valueOf(sortedList.get(j).get(1))) {
						sortedList.add(j, helperList.get(i));
						//System.out.println("New Sorted List 2: "+sortedList.toString());
					}
					else if(Double.valueOf(helperList.get(i).get(1))==Double.valueOf(sortedList.get(j).get(1))) {
						if(helperList.get(i).get(2).compareTo(sortedList.get(j).get(2))>0) {
							sortedList.add(j, helperList.get(i));
							//System.out.println("New Sorted List 3: "+sortedList.toString());
						}
						else if(helperList.get(i).get(2).compareTo(sortedList.get(j).get(2))==0) {
							System.out.println("DUPLICATE ERROR| helper: "+helperList.get(i)+"    sorted: "+sortedList.get(j));
						}
						
					}
					
				}
			}
			
		}
		System.out.println("Final List: "+sortedList);
		return sortedList;
	}
}






