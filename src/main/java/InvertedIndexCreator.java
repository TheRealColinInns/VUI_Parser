import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Class responsible for converting an array list into the map data structure
 * desired that will be very important later for creating the json file
 *
 * @author CS 212 Software Development - Colin Inns
 * @author University of San Francisco
 * @version Spring 2021
 */
public class InvertedIndexCreator {

	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * the method that calls all the other methods
	 *
	 * @param myInvertedIndex the array list that will be converted into the more
	 *                        useful data structure
	 * @param inputPath       the path that the arraylist is going to grab the data
	 *                        from
	 * @throws IOException in case of io exception
	 */
	public static void createInvertedIndex(Path inputPath, InvertedIndex myInvertedIndex) throws IOException {
		if (Files.isDirectory(inputPath)) {
			directoryStemmer(inputPath, myInvertedIndex);
		} else {
			singleFileStemmer(inputPath, myInvertedIndex);
		}
	}

	/**
	 * stems a single file
	 *
	 * @param inputPath       the path that the arraylist is going to grab the data
	 *                        from
	 * @param myInvertedIndex the data structure we are building
	 * @throws IOException it really shouldn't throw tho
	 */
	public static void singleFileStemmer(Path inputPath, InvertedIndex myInvertedIndex) throws IOException {
		Stemmer myStemmer = new SnowballStemmer(DEFAULT);
		int counter = 0;
		try (BufferedReader myBufferedReader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8);) {
			for (String line = myBufferedReader.readLine(); line != null; line = myBufferedReader.readLine()) {
				for (String word : TextParser.parse(line)) {
					counter++;
					myInvertedIndex.add(myStemmer.stem(word).toString(), inputPath.toString(), counter);
				}
			}
		}
	}

	/**
	 * stems a directory
	 *
	 * @param myInvertedIndex the data structure
	 * @param inputPath       the path that the arraylist is going to grab the data
	 *                        from
	 * @throws IOException it really shouldn't throw tho
	 */
	private static void directoryStemmer(Path inputPath, InvertedIndex myInvertedIndex) throws IOException {
		for (Path currentPath : DirectoryNavigator.findPaths(inputPath)) {
			singleFileStemmer(currentPath, myInvertedIndex);
		}
	}

}
