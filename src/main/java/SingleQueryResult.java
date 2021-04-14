import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SingleQueryResult {
	/**
	 * a single query result
	 */
	List<ArrayList<String>> singleQueryResult;

	/**
	 * constructor for class
	 */
	public SingleQueryResult() {
		singleQueryResult = new ArrayList<ArrayList<String>>();
	}

	/**
	 * contains method for class
	 * 
	 * @param index the index we are looking for
	 * @return boolean whether or not it contains the index
	 */
	public boolean contains(int index) {
		if (this.singleQueryResult.size() > index) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * get method for this class
	 * 
	 * @param index the index we are getting from
	 * @return an unmodifiable list of the single query results
	 */
	public List<String> get(int index) {
		return Collections.unmodifiableList(this.singleQueryResult.get(index));
	}

	/**
	 * adds an array list to larger array list
	 * 
	 * @param input the array list we are adding
	 */
	public void add(ArrayList<String> input) {
		boolean keepItClean = true;
		for (int i = 0; i < this.singleQueryResult.size(); i++) {
			if (compare(this.singleQueryResult.get(i), input) > 0) {
				this.singleQueryResult.add(i, input);
				keepItClean = false;
				break;
			}
		}
		if (keepItClean) {
			this.singleQueryResult.add(input);
		}
	}

	/**
	 * size method for class
	 * 
	 * @return the size of the singleQueryResult
	 */
	public int size() {
		return this.singleQueryResult.size();
	}

	/**
	 * creates an array list to add up above
	 * 
	 * @param location    the path
	 * @param foundCount  the amount found
	 * @param myWordCount the word count
	 */
	public void add(String location, Integer foundCount, WordCount myWordCount) {
		ArrayList<String> singleResult = new ArrayList<String>();
		DecimalFormat FORMATTER = new DecimalFormat("0.00000000");
		singleResult.add(location);
		singleResult.add(foundCount.toString());
		singleResult.add(String.valueOf(FORMATTER.format(foundCount / Double.valueOf(myWordCount.get(location)))));
		// System.out.println("Single Result: "+singleResult.toString());
		this.add(singleResult);

	}

	@Override
	public String toString() {
		return this.singleQueryResult.toString();
	}

	/**
	 * compares according to instructions
	 * 
	 * @param original the array list already in the data structure
	 * @param input    the array list we are attempting to add
	 * @return an int depending on if it is less than or greater than
	 */
	private static int compare(ArrayList<String> original, ArrayList<String> input) {
		int scoreComparison = Double.compare(Double.valueOf(original.get(2)), Double.valueOf(input.get(2)));
		if (scoreComparison < 0) {
			return 1;
		} else if (scoreComparison > 0) {
			return -1;
		} else {
			int countComparison = Integer.compare(Integer.valueOf(original.get(1)), Integer.valueOf(input.get(1)));
			if (countComparison < 0) {
				return 1;
			} else if (countComparison > 0) {
				return -1;
			} else {
				int locationComparison = original.get(0).compareToIgnoreCase(input.get(0));
				if (locationComparison < 0) {
					return -1;
				} else if (locationComparison > 0) {
					return 1;
				} else {
					return 0;
				}
			}
		}
	}

}
