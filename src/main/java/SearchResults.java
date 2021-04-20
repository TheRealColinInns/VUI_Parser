import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * Class responsible for converting an array list into the map data structure
 * desired that will be very important later for creating the json file
 *
 * @author CS 212 Software Development - Colin Inns
 * @author University of San Francisco
 * @version Spring 2021
 */
public class SearchResults {

	/**
	 * the results of the search
	 */
	private final TreeMap<String, List<Result>> results;

	/**
	 * the constructor for this class
	 */
	public SearchResults() {
		results = new TreeMap<String, List<Result>>();

	}

	/**
	 * adds a single query result to the results
	 * 
	 * @param query  the location we are storing it at
	 * @param result the value we are storing
	 */
	public void add(String query, List<Result> result) {
		results.putIfAbsent(query, result);
	}

	/**
	 * adds a single result to the list of results
	 * 
	 * @param query  the location we are storing it at
	 * @param result a single result
	 * @return boolean whether it actually added
	 */
	private boolean add(String query, Result result) {
		for (int i = 0; i < this.results.get(query).size(); i++) {
			int comparison = this.results.get(query).get(i).compareTo(result);
			if (comparison > 0) {
				this.results.get(query).add(i, result);
				return true;
			} else if (comparison == 0) {
				return false;
			}
		}

		this.results.get(query).add(result);
		return true;

	}

	/**
	 * adds a location count and score to the list of query results
	 * 
	 * @param query    the query in question
	 * @param location the location the count is from
	 * @param count    the count of all the matched words
	 * @param score    the count divided by the total word count
	 */
	public void add(String query, String location, int count, Double score) {
		if (this.results.containsKey(query)) {
			this.add(query, new Result(location, count, score));
		} else {
			List<Result> result = new ArrayList<Result>();
			result.add(new Result(location, count, score));
			this.add(query, result);
		}
	}

	/**
	 * adds a blank result
	 * 
	 * @param query the query location to add the blank to
	 */
	public void addBlank(String query) {
		if (this.results.containsKey(query)) {
			System.out.println("This isn't blank");
		} else {
			List<Result> result = new ArrayList<Result>();
			this.add(query, result);
		}
	}

	/**
	 * gets an unmodifiable key set
	 * 
	 * @return an unmodifiable key set
	 */
	public Set<String> getResultKeySet() {
		return Collections.unmodifiableSet(results.keySet());
	}

	/**
	 * gets the location that way we don't need to involve Result class
	 * 
	 * @param query the query at where we want to find
	 * @param index the index at where we want to find
	 * @return the location
	 */
	public String getLocation(String query, int index) {
		return this.results.get(query).get(index).getLocation();
	}

	/**
	 * gets the count that way we don't need to involve Result class
	 * 
	 * @param query the query at where we want to find
	 * @param index the index at where we want to find
	 * @return the count
	 */
	public int getCount(String query, int index) {
		return this.results.get(query).get(index).getCount();
	}

	/**
	 * gets the score that way we don't need to involve Result class
	 * 
	 * @param query the query at where we want to find
	 * @param index the index at where we want to find
	 * @return the score
	 */
	public Double getScore(String query, int index) {
		return this.results.get(query).get(index).getScore();
	}

	/**
	 * tests if the results contains an index
	 * 
	 * @param query the key to test
	 * @param index the index to see if exists
	 * @return boolean whether or not it contains the index
	 */
	public boolean containsIndex(String query, int index) {
		if (this.results.get(query).size() - 1 >= index) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * the size at a specific query
	 * 
	 * @param query the specific query
	 * @return the size in integer form
	 */
	public int size(String query) {
		return this.results.get(query).size();
	}

	/**
	 * writes the results
	 * 
	 * @param output the file we are writing to
	 * @throws IOException throws if the file is unreachable
	 */
	public void write(Path output) throws IOException {
		SimpleJsonWriter.asSearchResult(this, output);
	}

	/**
	 * Inner class that stores a single result
	 * 
	 * @author colininns
	 *
	 */
	public class Result implements Comparable<Result> {
		/**
		 * stores where the count came from
		 */
		private String location;
		/**
		 * the amount of hits it found
		 */
		private int count;
		/**
		 * the hits divided by the word count
		 */
		private Double score;

		/**
		 * Constructor for the result
		 * 
		 * @param location the location
		 * @param count    the amount of hits
		 * @param score    the score of the location
		 */
		public Result(String location, int count, Double score) {
			DecimalFormat FORMATTER = new DecimalFormat("0.00000000");
			this.location = location;
			this.count = count;
			this.score = Double.valueOf(FORMATTER.format(score));
		}

		/**
		 * gets the location
		 * 
		 * @return a string of the location
		 */
		public String getLocation() {
			return location;
		}

		/**
		 * gets the count
		 * 
		 * @return an int of the count
		 */
		public int getCount() {
			return count;
		}

		/**
		 * gets the score
		 * 
		 * @return a double of the score
		 */
		public Double getScore() {
			return score;
		}

		@Override
		public int compareTo(Result original) {
			int scoreComparison = Double.compare(original.getScore(), this.getScore());
			if (scoreComparison != 0) {
				return scoreComparison;
			} else {
				int countComparison = Integer.compare(original.getCount(), this.getCount());
				if (countComparison < 0) {
					return countComparison;
				} else {
					return this.getLocation().compareToIgnoreCase(original.getLocation());
				}
			}
		}
	}

}
