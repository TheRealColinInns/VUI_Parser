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
	 * @param myWordCount     the word count we will add to
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
	 * the method that calls all the other methods
	 *
	 * @param myInvertedIndex the array list that will be converted into the more
	 *                        useful data structure
	 * @param inputPath       the path that the arraylist is going to grab the data
	 *                        from
	 * @param myWordCount     the word count we will add to
	 * @param threads         the number of threads
	 * @throws IOException in case of io exception
	 */
	public static void createInvertedIndex(Path inputPath, InvertedIndex myInvertedIndex, int threads)
			throws IOException {
		if (Files.isDirectory(inputPath)) {
			directoryStemmer(inputPath, myInvertedIndex, threads);
		} else {
			singleFileStemmer(inputPath, myInvertedIndex, threads);
		}
	}

	/**
	 * stems a single file
	 *
	 * @param inputPath       the path that the arraylist is going to grab the data
	 *                        from
	 * @param myInvertedIndex the data structure we are building
	 * @param myWordCount     the word count we will add to
	 * @throws IOException it really shouldn't throw tho
	 */
	public static void singleFileStemmer(Path inputPath, InvertedIndex myInvertedIndex) throws IOException {
		Stemmer myStemmer = new SnowballStemmer(DEFAULT);
		int counter = 0;
		String location = inputPath.toString();
		try (BufferedReader myBufferedReader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8);) {
			for (String line = myBufferedReader.readLine(); line != null; line = myBufferedReader.readLine()) {
				for (String word : TextParser.parse(line)) {
					counter++;
					myInvertedIndex.add(myStemmer.stem(word).toString(), location, counter);
				}
			}
		}
	}

	/**
	 * stems a single file
	 *
	 * @param inputPath       the path that the arraylist is going to grab the data
	 *                        from
	 * @param myInvertedIndex the data structure we are building
	 * @param myWordCount     the word count we will add to
	 * @param threads         the number of threads
	 * @throws IOException it really shouldn't throw tho
	 */
	public static void singleFileStemmer(Path inputPath, InvertedIndex myInvertedIndex, int threads)
			throws IOException {
		Stemmer myStemmer = new SnowballStemmer(DEFAULT);
		String location = inputPath.toString();
		WorkQueue queue = new WorkQueue(threads);
		queue.execute(new Task(inputPath, myInvertedIndex, myStemmer, location, 0));
		queue.join();
	}

	/**
	 * stems a directory
	 *
	 * @param myInvertedIndex the data structure
	 * @param inputPath       the path that the arraylist is going to grab the data
	 *                        from
	 * @param myWordCount     the word count we will add to
	 * @throws IOException it really shouldn't throw tho
	 */
	private static void directoryStemmer(Path inputPath, InvertedIndex myInvertedIndex) throws IOException {
		for (Path currentPath : DirectoryNavigator.findPaths(inputPath)) {
			singleFileStemmer(currentPath, myInvertedIndex);
		}
	}

	/**
	 * stems a directory
	 *
	 * @param myInvertedIndex the data structure
	 * @param inputPath       the path that the arraylist is going to grab the data
	 *                        from
	 * @param myWordCount     the word count we will add to
	 * @param threads         the number of threads
	 * @throws IOException it really shouldn't throw tho
	 */
	private static void directoryStemmer(Path inputPath, InvertedIndex myInvertedIndex, int threads)
			throws IOException {
		for (Path currentPath : DirectoryNavigator.findPaths(inputPath)) {
			singleFileStemmer(currentPath, myInvertedIndex, threads);
		}
	}

	/**
	 * Task class creates the tasks that will be paralleled
	 * 
	 * @author colininns
	 *
	 */
	public static class Task implements Runnable {

		/** buffered reader */
		Path inputPath;

		/** the index we write to */
		InvertedIndex myInvertedIndex;

		/** the stemmer */
		Stemmer myStemmer;

		/** the location */
		String location;

		/** the counter */
		int counter;

		

		/**
		 * constructor for task
		 * 
		 * @param num    the number we are testing
		 * @param primes the results
		 */
		public Task(Path inputPath, InvertedIndex myInvertedIndex, Stemmer stemmer, String location, int counter) {
			this.inputPath = inputPath;
			this.myInvertedIndex = myInvertedIndex;
			this.myStemmer = stemmer;
			this.location = location;
			this.counter = counter;
			System.out.println("Creating Task (Index)");

		}

		@Override
		public void run() {
			try (BufferedReader myBufferedReader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8);) {
				for (String line = myBufferedReader.readLine(); line != null; line = myBufferedReader.readLine()) {
					for (String word : TextParser.parse(line)) {
						counter++;
						synchronized (myInvertedIndex) {
							myInvertedIndex.add(myStemmer.stem(word).toString(), location, counter);
						}
					}
				}
			} catch (IOException e) {
				System.out.println("Unable to read file: "+inputPath);
			}
			System.out.println("Finishing Task (Index)");
		}
	}

}
