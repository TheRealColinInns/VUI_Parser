import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class SearchQuery {

	/**
	 * searches for exact query
	 * 
	 * @param myInvertedIndex the inverted index we will search through
	 * @param myWordCount     the word count we will use for scores
	 * @param myQueryParser   the parser with the queries
	 * @param results         the results
	 */
	public static void exactSearch(InvertedIndex myInvertedIndex, WordCount myWordCount, QueryParser myQueryParser,
			SearchResults results) {
		for (TreeSet<String> querySet : myQueryParser.get()) {
			String queryText = String.join(" ", querySet);
			SingleQueryResult mySingleQueryResult = new SingleQueryResult();
			Map<String, Integer> countsAtLocations = new HashMap<String, Integer>();
			for (String word : myInvertedIndex.getWords()) {
				for (String path : myInvertedIndex.getLocations(word)) {
					for (String query : querySet) {

						if (word.compareToIgnoreCase(query) == 0) {
							if (countsAtLocations.containsKey(path)) {
								countsAtLocations.put(path,
										myInvertedIndex.sizePositions(word, path) + countsAtLocations.get(path));
							} else {
								countsAtLocations.put(path, myInvertedIndex.sizePositions(word, path));
							}
						}
					}
				}
			}

			for (String path : countsAtLocations.keySet()) {
				mySingleQueryResult.add(path, countsAtLocations.get(path), myWordCount);
			}
			results.add(queryText, mySingleQueryResult);
		}
	}

	/**
	 * searches for a partial match of a query
	 * 
	 * @param myInvertedIndex the inverted index we will search through
	 * @param myWordCount     the word count we will use for scores
	 * @param myQueryParser   the parser with the queries
	 * @param results         the results
	 */
	public static void partialSearch(InvertedIndex myInvertedIndex, WordCount myWordCount, QueryParser myQueryParser,
			SearchResults results) {
		for (TreeSet<String> querySet : myQueryParser.get()) {
			String queryText = String.join(" ", querySet);
			SingleQueryResult mySingleQueryResult = new SingleQueryResult();
			Map<String, Integer> countsAtLocations = new HashMap<String, Integer>();
			for (String word : myInvertedIndex.getWords()) {
				for (String path : myInvertedIndex.getLocations(word)) {
					for (String query : querySet) {

						if (partialSearcher(word, query)) {
							if (countsAtLocations.containsKey(path)) {
								countsAtLocations.put(path,
										myInvertedIndex.sizePositions(word, path) + countsAtLocations.get(path));
							} else {
								countsAtLocations.put(path, myInvertedIndex.sizePositions(word, path));
							}
						}
					}
				}
			}

			for (String path : countsAtLocations.keySet()) {
				mySingleQueryResult.add(path, countsAtLocations.get(path), myWordCount);
			}

			results.add(queryText, mySingleQueryResult);
		}
	}

	/**
	 * helper function that searches for a piece of a query in a word
	 * 
	 * @param wordKey the word we are searching through
	 * @param query   the query we are looking for
	 * @return boolean whether or not it is there
	 */
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
