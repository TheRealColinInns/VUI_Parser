import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

public class SearchQuery {
	public static Map<String, ArrayList<String>> exactSearch(Map<String, Map<String, Collection<Integer>>> mainMap, Map<String, Integer> wordCountMap, TreeSet<String> querySet){
		int wordCount;
		ArrayList<String> singleQueryResult = new ArrayList<String>();
		Map<String, ArrayList<String>> queryResult = new HashMap<String, ArrayList<String>>();
		for(String queryWord:querySet) {
			for(String mainKey:mainMap.keySet()) {
				for(String secondaryKey:mainMap.get(mainKey).keySet()) {
					if(queryWord.equals(secondaryKey)) {
						wordCount = wordCountMap.get(mainKey);
						singleQueryResult.add(String.valueOf(wordCount/mainMap.get(secondaryKey).size()));
						singleQueryResult.add(String.valueOf(wordCount));
						singleQueryResult.add(mainKey);
					}
				}
			}
			queryResult.put(queryWord, singleQueryResult);
		}
		return queryResult;
	}
	public ArrayList<Integer> partialSearch(Map<String, Map<String, Collection<Integer>>> mainMap, Map<String, Integer> wordCountMap, TreeSet<String> querySet){
		ArrayList<Integer> queryResult = new ArrayList<Integer>();
		return queryResult;
	}
}
