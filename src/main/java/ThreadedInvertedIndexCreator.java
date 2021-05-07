import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * creates an inverted index using multithreading
 * 
 * @author colininns
 *
 */
public class ThreadedInvertedIndexCreator extends InvertedIndexCreator {
	/**
	 * the method that calls all the other methods
	 *
	 * @param myInvertedIndex the array list that will be converted into the more
	 *                        useful data structure
	 * @param inputPath       the path that the arraylist is going to grab the data
	 *                        from
	 * @param workqueue       the word queue that will be used
	 * @throws IOException in case of io exception
	 */
	// TODO InvertedIndex --> ThreadSafeInvertedIndex
	public static void createInvertedIndex(Path inputPath, InvertedIndex myInvertedIndex, WorkQueue workqueue)
			throws IOException {
		if (Files.isDirectory(inputPath)) {
			directoryStemmer(inputPath, myInvertedIndex, workqueue);
			workqueue.finish();
		} else {
			singleFileStemmer(inputPath, myInvertedIndex, workqueue);
			workqueue.finish();
		}

	}

	/**
	 * stems a single file
	 *
	 * @param inputPath       the path that the arraylist is going to grab the data
	 *                        from
	 * @param myInvertedIndex the data structure we are building
	 * @param workqueue       the word queue that will be used
	 * @throws IOException in case unable to parse
	 */
	// TODO InvertedIndex --> ThreadSafeInvertedIndex
	public static void singleFileStemmer(Path inputPath, InvertedIndex myInvertedIndex, WorkQueue workqueue)
			throws IOException {
		Stemmer myStemmer = new SnowballStemmer(DEFAULT);
		String location = inputPath.toString();
		workqueue.execute(new Task(inputPath, myInvertedIndex, myStemmer, location));
	}

	/**
	 * stems a directory
	 *
	 * @param myInvertedIndex the data structure
	 * @param inputPath       the path that the arraylist is going to grab the data
	 *                        from
	 * @param workqueue       the word queue that will be used
	 * @throws IOException it really shouldn't throw tho
	 */
	// TODO InvertedIndex --> ThreadSafeInvertedIndex
	private static void directoryStemmer(Path inputPath, InvertedIndex myInvertedIndex, WorkQueue workqueue)
			throws IOException {
		for (Path currentPath : DirectoryNavigator.findPaths(inputPath)) {
			singleFileStemmer(currentPath, myInvertedIndex, workqueue);
		}

	}

	/**
	 * Task class creates the tasks that will be paralleled
	 * 
	 * @author colininns
	 *
	 */
	public static class Task implements Runnable { // TODO private
		// TODO private, final
		
		/** buffered reader */
		Path inputPath;

		/** the index we write to */
		InvertedIndex myInvertedIndex; 	// TODO InvertedIndex --> ThreadSafeInvertedIndex

		/** the stemmer */
		Stemmer myStemmer;

		/** the location */
		String location;

		/** the counter */
		int counter;

		/**
		 * constructor for task
		 * 
		 * @param inputPath       the location we are getting it from
		 * @param myInvertedIndex the index we are building to
		 * @param stemmer         the stemmer we will use to stem
		 * @param location        the location we found it
		 */
		public Task(Path inputPath, InvertedIndex myInvertedIndex, Stemmer stemmer, String location) {
			this.inputPath = inputPath;
			this.myInvertedIndex = myInvertedIndex;
			this.myStemmer = stemmer;
			this.location = location;
			this.counter = 0;

		}

		@Override
		public void run() {
			// TODO Could have done this: InvertedIndexCreator.singleFileStemmer(inputPath, myInvertedIndex);
			try (BufferedReader myBufferedReader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8);) {
				for (String line = myBufferedReader.readLine(); line != null; line = myBufferedReader.readLine()) {
					for (String word : TextParser.parse(line)) {
						counter++;

						// TODO Blocking add within a loop?
						myInvertedIndex.add(myStemmer.stem(word).toString(), location, counter);

					}
				}
			} catch (IOException e) {
				System.out.println("Unable to read file: " + inputPath);
			}
			
			/*
			 * TODO 
			 * 1) Create local data
			 * 2) Add to the local data
			 * 3) Combine the local and shared data together
			 * 
			 * InvertedIndex local = new InvertedIndex();
			 * InvertedIndexCreator.singleFileStemmer(inputPath, local);
			 * myInvertedIndex.addAll(local);
			 */
		}
	}

}
