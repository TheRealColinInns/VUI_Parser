
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * class that efficiently crawls through the web using a multithreaded aproach
 * 
 * @author colininns
 *
 */
public class WebCrawler {
	/** stores all the urls we used */
	private HashSet<String> usedUrls;

	/**
	 * constructor for web crawler
	 */
	public WebCrawler() {
		usedUrls = new HashSet<String>();
	}

	/**
	 * crawls through from a desired seed
	 * 
	 * @param seed            the starting url
	 * @param inputmax        the max amount of urls
	 * @param queue           the work queue
	 * @param myInvertedIndex the index we add to
	 * @throws IOException in case we ahve a problem reading
	 */
	public void crawl(URL seed, int inputmax, WorkQueue queue, InvertedIndex myInvertedIndex) throws IOException {
		// System.out.println("Task for url: " + seed);
		usedUrls.add(seed.toString());
		queue.execute(new Task(seed, myInvertedIndex, queue, inputmax, usedUrls));
		queue.finish();
		// System.out.println("Finishing");
	}

	/**
	 * Creates a runnable task
	 * 
	 * @author colininns
	 *
	 */
	public static class Task implements Runnable {
		/** the seed url */
		private URL seed;
		/** the index */
		private InvertedIndex myInvertedIndex;
		/** the work queue */
		private WorkQueue queue;
		/** the max urls */
		private Integer max;
		/** the used urls */
		private HashSet<String> usedUrls;

		/**
		 * Constructor for the task
		 * 
		 * @param seed            the seed url
		 * @param myInvertedIndex the index
		 * @param queue           the workqueue
		 * @param inputmax        the max urls
		 * @param usedUrls        the used urls
		 */
		public Task(URL seed, InvertedIndex myInvertedIndex, WorkQueue queue, int inputmax, HashSet<String> usedUrls) {
			this.seed = seed;
			this.myInvertedIndex = myInvertedIndex;
			this.queue = queue;
			this.usedUrls = usedUrls;
			max = inputmax;
		}

		@Override
		public void run() {
			// System.out.println("Max: " + max);
			// System.out.println("Size: " + usedUrls.size());
			ArrayList<URL> urlList = new ArrayList<URL>();
			String html = HtmlFetcher.fetch(seed, 3);
			if (html != null) {
				html = HtmlCleaner.stripBlockElements(html);
				urlList = LinkParser.getValidLinks(seed, html);
				// System.out.println(urlList);
				synchronized (usedUrls) {
					for (URL currentUrl : urlList) {
						if (usedUrls.size() < max) {
							if (usedUrls.add(currentUrl.toString())) {
								// System.out.println("Task for: " + currentUrl);
								
								queue.execute(new Task(currentUrl, myInvertedIndex, queue, max, usedUrls));
								

							} else {
								// System.out.println("Found Duplicate URL");
							}
						} else {
							// System.out.println("Terminating... Crawl Limit Reached");
						}
					}

				}

				// System.out.println(html);
				myInvertedIndex.addAll(Arrays.asList(TextParser.parse(HtmlCleaner.stripHtml(html))), seed.toString());
				// System.out.println("Finishing task for: " + seed);

			}
		}

	}
}
