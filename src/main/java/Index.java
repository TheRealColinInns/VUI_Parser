
import java.util.Collections;
import java.util.Map;

import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * Parses strings in order to store the specified metrics, such as word
 * frequency.
 * 
 * @author colininns
 * @version VUI
 */
public class Index {
	/** the index of intents */
	private final Map<String, Integer> intentIndex;
	/** the frequency of each word */
	private final Map<String, Integer> frequency;
	/** the count of all sentences with a wh... word and fare code */
	private int wCount = 0;
	/** the count of all sentences with the word airplane or any of its synonyms */
	private int planeCount = 0;

	/**
	 * Constructor for Index class
	 */
	public Index() {
		this.intentIndex = new TreeMap<String, Integer>();
		this.frequency = new TreeMap<String, Integer>();
	}

	/**
	 * parses a raw string to find desired values
	 * 
	 * @param raw    the raw string
	 * @param intent the previous intent
	 * @return the new intent if the raw string is an intent or {code=null} if not
	 */
	public String parse(String raw, String intent) {
		if (raw.matches("##\\sintent:.+")) {
			return raw.replaceAll("##\\sintent:", "");
		} else if (raw.matches("-\\s.+")) {
			// finds number of entities
			Integer intentCount = (int) raw.chars().filter(ch -> ch == '[').count();
			this.addIntent(intent, intentCount);
			// cleans the raw string
			raw = raw.replaceAll("\\(.*?\\)", "").replaceFirst("-\\s", "").replaceAll("\\[", "").replaceAll("\\]", "");
			// finds phrases with wh... and fare code
			if (this.containsW(raw)) {
				wCount++;
			}
			// finds phrases with the word plane in them
			if (this.findPlane(raw)) {
				planeCount++;
			}
			// adds to the word count
			StringTokenizer content = new StringTokenizer(raw);
			while (content.hasMoreTokens()) {
				this.addWordCount(content.nextToken());
			}
		}
		return null;
	}

	/**
	 * determines if the string contains a wh... word and the phrase fare code
	 * 
	 * @param content the string to look through
	 * @return {code=true} if the string contains the desired phrase and
	 *         {code=false} otherwise
	 */
	private boolean containsW(String content) {
		return content.matches("(?i).*?wh[^.]*?fare code.*") || content.matches("(?i).*?fare code[^.]*?wh.*");
	}

	/**
	 * adds a one to a specific words count
	 * 
	 * @param word the specific word
	 */
	private void addWordCount(String word) {
		if (frequency.putIfAbsent(word, 1) != null) {
			frequency.put(word, frequency.get(word) + 1);
		}
	}

	/**
	 * adds a count to a specific intents count
	 * 
	 * @param intent the specific intent
	 * @param count  the count to add
	 */
	private void addIntent(String intent, Integer count) {
		if (intentIndex.putIfAbsent(intent, count) != null) {
			intentIndex.put(intent, intentIndex.get(intent) + count);
		}
	}

	/**
	 * determines whether a string contains the word plane or its synonyms
	 * 
	 * @param content the string to search through
	 * @return {code=true} if the string contains the word or {code=false} otherwise
	 */
	private boolean findPlane(String content) {
		return content.matches("(?i).*airplane.*") || content.matches("(?i).*aircraft.*")
				|| content.matches("(?i).*airline.*") || content.matches("(?i).*airliner.*")
				|| content.matches("(?i).*jet.*") || content.matches("(?i).*aeroplane.*")
				|| content.matches("(?i).*airbus.*") || content.matches("(?i).*plane.*")
				|| content.matches("(?i).*ship.*") || content.matches("(?i).*cab.*");
	}

	/**
	 * finds the most frequent words
	 * 
	 * @return a set of the most frequent words
	 */
	public Set<String> mostFrequent() {
		if (frequency.size() <= 10) {
			return frequency.keySet();
		} else {
			TreeMap<String, Integer> result = new TreeMap<String, Integer>();
			for (String word : frequency.keySet()) {
				if (result.size() < 10) {
					result.put(word, frequency.get(word));
				} else {
					Pair least = new Pair(true);
					for (String other : result.keySet()) {
						if (result.get(other) < least.getCount()) {
							least.set(other, result.get(other));
						}
					}
					if (frequency.get(word) > least.getCount()) {
						result.remove(least.getWord());
						result.put(word, frequency.get(word));
					}
				}
			}
			return result.keySet();
		}
	}

	/**
	 * finds the least frequent words
	 * 
	 * @return a set of the least frequent words
	 */
	public Set<String> leastFrequent() {
		if (frequency.size() <= 10) {
			return frequency.keySet();
		} else {
			TreeMap<String, Integer> result = new TreeMap<String, Integer>();
			for (String word : frequency.keySet()) {
				if (result.size() < 10) {
					result.put(word, frequency.get(word));
				} else {
					Pair greatest = new Pair(false);
					for (String other : result.keySet()) {
						if (result.get(other) > greatest.getCount()) {
							greatest.set(other, result.get(other));
						}
					}
					if (frequency.get(word) < greatest.getCount()) {
						result.remove(greatest.getWord());
						result.put(word, frequency.get(word));
					}
				}
			}
			return result.keySet();
		}
	}

	/**
	 * getter method for the frequency map
	 * 
	 * @return an unmodifiable map of the word frequencies
	 */
	public Map<String, Integer> getWords() {
		return Collections.unmodifiableMap(frequency);
	}

	/**
	 * getter method for the intents map
	 * 
	 * @return an unmodifiable map of the intent frequencies
	 */
	public Map<String, Integer> getInents() {
		return Collections.unmodifiableMap(intentIndex);
	}

	/**
	 * getter method for the wCount member
	 * 
	 * @return the integer
	 */
	public int getWCount() {
		return wCount;
	}

	/**
	 * getter method for the plane count member
	 * 
	 * @return the integer
	 */
	public int getPlaneCount() {
		return planeCount;
	}

	/**
	 * 
	 * @author colininns
	 *
	 */
	private class Pair {
		/** the word */
		String word;
		/** the count */
		Integer count;

		/**
		 * Constructor for Pair class
		 * 
		 * @param least determines what type of pair, least or greatest
		 */
		public Pair(Boolean least) {
			this.word = null;
			if (least) {
				this.count = Integer.MAX_VALUE;
			} else {
				this.count = 0;
			}
		}

		/**
		 * Sets the word and count for a pair
		 * 
		 * @param word  the word
		 * @param count the count
		 */
		private void set(String word, Integer count) {
			this.word = word;
			this.count = count;
		}

		/**
		 * getter method for the word
		 * 
		 * @return the word
		 */
		private String getWord() {
			return word;
		}

		/**
		 * getter method for the count
		 * 
		 * @return the count
		 */
		private Integer getCount() {
			return count;
		}
	}

}
