import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Demonstrates how to create a simple message board using Jetty and servlets,
 * as well as how to initialize servlets when you need to call its constructor.
 */
public class SearchEngineServer {

	/** The hard-coded port to run this server. */
	public final int PORT;
	
	/** the results we will search with */
	private final ThreadSafeInvertedIndex index;
	
	
	
	/**
	 * constructor for search engine
	 * 
	 * @param port the port number
	 * @param results the results we will search
	 */
	public SearchEngineServer(int port, ThreadSafeInvertedIndex index) {
		PORT = port;
		this.index = index;
	}
	
	/**
	 * Sets up a Jetty server with different servlet instances.
	 *
	 * @param args unused
	 * @throws Exception if unable to start and run server
	 */
	public void createServer() throws Exception {
		System.setProperty("org.eclipse.jetty.LEVEL", "FATAL");
		
		Server server = new Server(PORT);

		ServletHandler handler = new ServletHandler();

		// must use servlet holds when need to call a constructor
		//System.out.println("INDEX: "+index.toString());
		handler.addServletWithMapping(new ServletHolder(new CookieLandingServlet()), "/");
		handler.addServletWithMapping(new ServletHolder(new SearchEngineServlet(index)), "/search");
		//handler.addServletWithMapping(new ServletHolder(new SearchEngineServlet()), "/results");
		

		server.setHandler(handler);
		server.start();
		server.join();

	}
	/*
	public static void main(String[] args) throws Exception {
		System.setProperty("org.eclipse.jetty.LEVEL", "FATAL");
		WorkQueue queue = new WorkQueue();
		Server server = new Server(8080);
		ThreadSafeInvertedIndex index = new ThreadSafeInvertedIndex();
		index.addAll(List.of("hello", "world"), "home");
		index.addAll(List.of("hello", "mom"), "work");

		ServletHandler handler = new ServletHandler();

		// must use servlet holds when need to call a constructor
		handler.addServletWithMapping(new ServletHolder(new SearchEngineServlet(index)), "/search");
		//handler.addServletWithMapping(new ServletHolder(new SearchEngineServlet()), "/results");
		

		server.setHandler(handler);
		server.start();
		server.join();
	}
	*/
	
}

