import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class SearchQuery {
	public static void exactSearch(InvertedIndex myInvertedIndex,
			WordCount myWordCount, QueryParser myQueryParser) {
		
		for (TreeSet<String> querySet : myQueryParser.get()) {
			String queryText = String.join(" ", querySet);
			for (String wordKey : myInvertedIndex.getWords()) {
				for (String pathKey : myInvertedIndex.getLocations(wordKey)) {
					
				}
			}
		}
		/*
		TreeMap<String, ArrayList<ArrayList<String>>> results = new TreeMap<String, ArrayList<ArrayList<String>>>();
		for (TreeSet<String> query : myQueryParser.get()) {
			ArrayList<ArrayList<String>> queryResult = new ArrayList<ArrayList<String>>();
			Map<String, Integer> pathCount = new HashMap<String, Integer>();
			for (String wordKey : myInvertedIndex.getWords()) {
				for (String pathKey : myInvertedIndex.getLocations(wordKey)) {

					// System.out.println("keySet: "+flippedMap.get(mainKey).keySet());
					for (String queryWord : query) {
						// System.out.println("Result Count Check: "+queryWord+" | "+wordKey);
						if (wordKey.equals(queryWord)) {
							int resultCount = 0;
							if (pathCount.containsKey(pathKey)) {
								if (pathCount.get(pathKey) != null) {
									resultCount = pathCount.get(pathKey);
								} else {
									resultCount = 0;
								}
								resultCount += myInvertedIndex.sizePositions(wordKey, pathKey);
								// System.out.println("Putting 1: "+resultCount+" to path: "+pathKey);
								pathCount.replace(pathKey, resultCount);
							} else {
								// System.out.println("Putting 2: "+mainMap.get(wordKey).get(pathKey).size()+"
								// to path: "+pathKey);
								pathCount.put(pathKey, myInvertedIndex.sizePositions(wordKey, pathKey));
							}

						}

					}

				}
			}

			System.out.println(pathCount.toString());
			for (String countKey : pathCount.keySet()) {
				if (pathCount.get(countKey) > 0) {
					wordCount = myWordCount.get(countKey);
					ArrayList<String> singleQueryResult = new ArrayList<String>();
					singleQueryResult.add(String.valueOf(pathCount.get(countKey) / wordCount));
					singleQueryResult.add(String.valueOf(pathCount.get(countKey)));
					singleQueryResult.add(countKey);
					queryResult.add(singleQueryResult);
				}
			}
			// System.out.println("Before: "+queryResult.toString());
			for (int i = 0; i < queryResult.size(); i++) {
				if (queryResult.get(i).isEmpty()) {
					queryResult.remove(i);
				}
			}
			if(!queryResult.isEmpty()){
				System.out.println("Result: "+queryResult+" for query: "+query);
				SearchQuery.add(resultSorter(queryResult), String.join(" ", query), results);
			}
		}
		return results;
		*/
	}

	private static ArrayList<String> createSingleResult(String location, Integer foundCount, WordCount myWordCount) {
		ArrayList<String> singleResult = new ArrayList<String>();
		singleResult.add(location);
		singleResult.add(foundCount.toString());
		singleResult.add(String.valueOf(foundCount/Double.valueOf(myWordCount.get(location))));
		return singleResult;
	}
	private static ArrayList<ArrayList<String>> resultSorter(ArrayList<ArrayList<String>> helperList) {
		ArrayList<ArrayList<String>> sortedList = new ArrayList<ArrayList<String>>();
		boolean foundPosition = false;
		// System.out.println("Original List: "+helperList.toString());
		sortedList.add(helperList.get(0));
		// System.out.println("Helper size: "+helperList.size());
		int maxCount = helperList.size();
		for (int i = 1; i < helperList.size(); i++) {
			foundPosition = false;
			for (int j = 0; j < sortedList.size(); j++) {
				if (!sortedList.contains(helperList.get(i))) {
					if (Double.compare(Double.valueOf(sortedList.get(j).get(0)),
							Double.valueOf(helperList.get(i).get(0))) < 0) {
						sortedList.add(j, helperList.get(i));
						foundPosition = true;
						maxCount--;
						if (maxCount <= 1) {
							break;
						}
						// System.out.println("New Sorted List 1: "+sortedList.toString());
					} else if (Double.compare(Double.valueOf(sortedList.get(j).get(0)),
							Double.valueOf(helperList.get(i).get(0))) == 0) {
						if (Double.compare(Double.valueOf(sortedList.get(j).get(1)),
								Double.valueOf(helperList.get(i).get(1))) < 0) {
							sortedList.add(j, helperList.get(i));
							foundPosition = true;
							maxCount--;
							if (maxCount <= 1) {
								break;
							}
							// System.out.println("New Sorted List 2: "+sortedList.toString());
						} else if (Double.compare(Double.valueOf(sortedList.get(j).get(1)),
								Double.valueOf(helperList.get(i).get(1))) == 0) {
							if (myCompareTo(helperList.get(i).get(2), sortedList.get(j).get(2)) > 0) {
								sortedList.add(j, helperList.get(i));
								foundPosition = true;
								maxCount--;
								if (maxCount <= 1) {
									break;
								}
								// System.out.println("New Sorted List 3: "+sortedList.toString());
							} else if (myCompareTo(helperList.get(i).get(2), sortedList.get(j).get(2)) == 0) {
								System.out.println("DUPLICATE ERROR| helper: " + helperList.get(i) + "    sorted: "
										+ sortedList.get(j));
							} else {
								/*
								 * sortedList.add(helperList.get(i)); maxCount--; if (maxCount<=2) { break; }
								 */
								// System.out.println("New Sorted List 4: "+sortedList.toString());
							}

						} else {
							/*
							 * sortedList.add(helperList.get(i)); maxCount--; if (maxCount<=2) { break; }
							 */
							// System.out.println("New Sorted List 5: "+sortedList.toString());
						}

					} else {
						/*
						 * sortedList.add(helperList.get(i)); maxCount--; if (maxCount<=2) { break; }
						 */
						// System.out.println("New Sorted List 6: "+sortedList.toString());
					}
				}
			}
			if (!foundPosition) {
				sortedList.add(helperList.get(i));
			}

		}
		// System.out.println("Final List: "+sortedList);
		return sortedList;
	}

	private static void add(ArrayList<ArrayList<String>> results, String query,
			TreeMap<String, ArrayList<ArrayList<String>>> finalResults) {
		finalResults.put(query, results);
	}

	private static int myCompareTo(String firstString, String secondString) {
		/*
		 * firstString = TextParser.clean(firstString); secondString =
		 * TextParser.clean(secondString);
		 */
		return secondString.compareToIgnoreCase(firstString);
	}
	
	private 

	private static boolean partialSearcher(String wordKey, String query) {
		String compareWord;
		if (wordKey.equals(query)) {
			return true;
		}
		if (query.length() < wordKey.length()) {
			compareWord = wordKey.substring(0, query.length());
		} else {
			return false;
		}
		if (compareWord.equals(query)) {
			return true;
		}
		return false;
	}
	

}
