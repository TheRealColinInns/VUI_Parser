import java.util.ArrayList;
import java.util.TreeMap;

public class SearchResults {
	
	private final TreeMap<String, ArrayList<ArrayList<String>>> results;
	
	public SearchResults() {
		results = new TreeMap<String, ArrayList<ArrayList<String>>>();
	}
	
	public void add(String query, ArrayList<ArrayList<String>> queryResult) {
		results.putIfAbsent(query, queryResult);
	}
	
	
}
