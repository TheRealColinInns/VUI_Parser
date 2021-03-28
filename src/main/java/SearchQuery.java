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
		//System.out.println("Query Set: "+querySet);
		
		ArrayList<ArrayList<String>> queryResult = new ArrayList<ArrayList<String>>();
		Map<String, Integer> pathCount = new HashMap<String, Integer>();
		
		
		for(String wordKey:mainMap.keySet()) {
			for(String pathKey:mainMap.get(wordKey).keySet()) {
				
				//System.out.println("keySet: "+flippedMap.get(mainKey).keySet());
				for(String queryWord:querySet) {
					//System.out.println("Result Count Check: "+queryWord+" | "+wordKey);
					if(wordKey.equals(queryWord)) {
						int resultCount = 0;
						if(pathCount.containsKey(pathKey)) {
							//System.out.println("Adding1: "+mainMap.get(wordKey).get(pathKey).size()+" to path: "+pathKey);
							//resultCount = mainMap.get(wordKey).get(pathKey).size();
							//pathCount.put(pathKey, resultCount);
						}
						
						else {
							
							if(pathCount.get(pathKey)!=null) {
								resultCount = pathCount.get(pathKey);
							}
							resultCount+=mainMap.get(wordKey).get(pathKey).size();
							//System.out.println("Adding2: "+resultCount+" to path: "+pathKey);
							pathCount.put(pathKey, resultCount);
						}
						
					}
				
				}
				
			
			}
		}
		//System.out.println(pathCount.toString());
		for(String pathKey:pathCount.keySet()) {
			if(pathCount.get(pathKey)>0) {
				wordCount = wordCountMap.get(pathKey);
				ArrayList<String> singleQueryResult = new ArrayList<String>();
				singleQueryResult.add(String.valueOf(pathCount.get(pathKey)/wordCount));
				singleQueryResult.add(String.valueOf(wordCount));
				singleQueryResult.add(pathKey);
				queryResult.add(singleQueryResult);
			}
		}
		//System.out.println("Before: "+queryResult.toString());
		for(int i = 0; i<queryResult.size(); i++) {
			if(queryResult.get(i).isEmpty()) {
				queryResult.remove(i);
			}
		}
		if(queryResult.isEmpty()) {
			return null;
		}
		//System.out.println("After: "+queryResult.toString());
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
		
		return resultSorter(queryResult);
	}
	private static ArrayList<ArrayList<String>> resultSorter(ArrayList<ArrayList<String>> helperList){
		ArrayList<ArrayList<String>> sortedList = new ArrayList<ArrayList<String>>();
		System.out.println("Original List: "+helperList.toString());
		sortedList.add(helperList.get(0));
		//System.out.println("Helper size: "+helperList.size());
		int maxCount = helperList.size();
		for(int i = 1; i<helperList.size(); i++) {
			for(int j = 0; j<sortedList.size(); j++) {
				if(Double.valueOf(helperList.get(i).get(0))>Double.valueOf(sortedList.get(j).get(0))) {
					sortedList.add(j, helperList.get(i));
					maxCount--;
					if (maxCount<=1) {
						break;
					}
					//System.out.println("New Sorted List 1: "+sortedList.toString());
				}
				else if(Double.valueOf(helperList.get(i).get(0))==Double.valueOf(sortedList.get(j).get(0))) {
					if(Double.valueOf(helperList.get(i).get(1))>Double.valueOf(sortedList.get(j).get(1))) {
						sortedList.add(j, helperList.get(i));
						maxCount--;
						if (maxCount<=1) {
							break;
						}
						//System.out.println("New Sorted List 2: "+sortedList.toString());
					}
					else if(Double.valueOf(helperList.get(i).get(1))==Double.valueOf(sortedList.get(j).get(1))) {
						if(helperList.get(i).get(2).compareTo(sortedList.get(j).get(2))>0) {
							sortedList.add(j, helperList.get(i));
							maxCount--;
							if (maxCount<=1) {
								break;
							}
							//System.out.println("New Sorted List 3: "+sortedList.toString());
						}
						else if(helperList.get(i).get(2).compareTo(sortedList.get(j).get(2))==0) {
							System.out.println("DUPLICATE ERROR| helper: "+helperList.get(i)+"    sorted: "+sortedList.get(j));
						}
						else  {
							sortedList.add(helperList.get(i));
							maxCount--;
							if (maxCount<=1) {
								break;
							}
							//System.out.println("New Sorted List 4: "+sortedList.toString());
						}
						
					}
					else {
						sortedList.add(helperList.get(i));
						maxCount--;
						if (maxCount<=1) {
							break;
						}
						//System.out.println("New Sorted List 5: "+sortedList.toString());
					}
					
				}
				else {
					sortedList.add(helperList.get(i));
					maxCount--;
					if (maxCount<=1) {
						break;
					}
					//System.out.println("New Sorted List 6: "+sortedList.toString());
				}
			}
			
		}
		//System.out.println("Final List: "+sortedList);
		return sortedList;
	}
}






