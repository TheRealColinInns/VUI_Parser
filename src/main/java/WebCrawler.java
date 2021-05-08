
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

	private HashSet<String> usedUrls;
	
	public WebCrawler() {
		usedUrls = new HashSet<String>();
	}
	/**
	 * crawls through the urls
	 * 
	 * @param seed  the starting url
	 * @param max   the max number of urls
	 * @param queue the work queue
	 * @throws IOException
	 */
	public void crawl(URL seed, int inputmax, WorkQueue queue, InvertedIndex myInvertedIndex)
			throws IOException {
		//System.out.println("Task for url: " + seed);
		usedUrls.add(seed.toString());
		queue.execute(new Task(seed, myInvertedIndex, queue, inputmax, usedUrls));

		queue.finish();
		System.out.println("Finishing");
	}

	public static class Task implements Runnable {

		private URL seed;

		private InvertedIndex myInvertedIndex;

		private WorkQueue queue;

		private Integer max;
		
		private HashSet<String> usedUrls;

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
								//System.out.println("Task for: " + currentUrl);
								synchronized(queue) {
									queue.execute(new Task(currentUrl, myInvertedIndex, queue, max, usedUrls));
								}

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
				//System.out.println("Finishing task for: " + seed);

			}
		}

	}
}
