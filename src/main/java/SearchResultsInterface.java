import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

/**
 * interface for the results
 * 
 * @author colininns
 *
 */
public interface SearchResultsInterface {
	
	/**
	 * searches the index given a query
	 * 
	 * @param queryPath the file of queries
	 * @param exact     flag tells us what type of search
	 * @param workqueue irrelevant
	 * @throws IOException throws if we can't read the query file
	 */
	public void search(Path queryPath, boolean exact) throws IOException;
	
	
	/**
	 * does a search of a single query line
	 * 
	 * @param queryLine the lin ewe are searching for
	 * @param exact     {code=true} if we are doing an exact search
	 */
	public void search(String queryLine, boolean exact);
	
	/**
	 * gets an unmodifiable key set
	 * 
	 * @return an unmodifiable key set
	 */
	public Set<String> getResultKeySet();
	
	/**
	 * the size at a specific query
	 * 
	 * @param query the specific query
	 * @return the size in integer form
	 */
	public int size(String query);
	
	/**
	 * writes the results
	 * 
	 * @param output the file we are writing to
	 * @throws IOException throws if the file is unreachable
	 */
	public void write(Path output) throws IOException;
}