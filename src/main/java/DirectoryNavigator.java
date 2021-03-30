import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Class responsible for navigating the directories found in the command line
 * arguments.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2021
 */
public class DirectoryNavigator {
	/**
	 * Traverses through the directory and its subdirectories, outputting all
	 * paths to the console. For files, also includes the file size in bytes.
	 *
	 * @param start the initial path to traverse
	 * @throws IOException if an I/O error occurs
	 */
	public static void printListing(Path start, ArrayList<String> myStorage) throws IOException {
		// use the Files class to get information about a path
		if (Files.isDirectory(start)) {
			// output trailing slash to indicate directory
			// start directory traversal
			traverseDirectory(start, myStorage);
		}
		else {
			// and to the placeholder arraylist, make sure it is a text file because this is in a directory
			if(start.toString().toLowerCase().endsWith(".txt")||start.toString().toLowerCase().endsWith(".text")) {
				myStorage.addAll(TextFileStemmer.listStems(start));
			}
			
		}
	}
	/**
	 * Traverses through the directory and its subdirectories, outputting all
	 * paths to the console. For files, also includes the file size in bytes.
	 *
	 * @param directory the directory to traverse
	 * @throws IOException if an I/O error occurs
	 */
	private static void traverseDirectory(Path directory, ArrayList<String> myStorage) throws IOException {
		/*
		 * The try-with-resources block makes sure we close the directory stream
		 * when done, to make sure there aren't any issues later when accessing this
		 * directory.
		 *
		 * Note, however, we are still not catching any exceptions. This type of try
		 * block does not have to be accompanied with a catch block. (You should,
		 * however, do something about the exception.)
		 */
		try (DirectoryStream<Path> myDirectoryStream = Files.newDirectoryStream(directory)) {
			// use an enhanced-for or for-each loop for efficiency and simplicity
			for (Path temporaryPath : myDirectoryStream) {
				printListing(temporaryPath, myStorage);
			}
		}
	}
}
