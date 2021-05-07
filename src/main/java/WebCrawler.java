
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

	private static HashSet<String> usedUrls;

	/**
	 * crawls through the urls
	 * 
	 * @param seed  the starting url
	 * @param max   the max number of urls
	 * @param queue the work queue
	 * @throws IOException
	 */
	public static void crawl(URL seed, int inputmax, WorkQueue queue, InvertedIndex myInvertedIndex)
			throws IOException {
		usedUrls = new HashSet<String>();
		System.out.println("Task for url: " + seed);
		usedUrls.add(seed.toString());
		queue.execute(new Task(seed, myInvertedIndex, queue, inputmax));
		queue.finish();
	}

	public static class Task implements Runnable {

		private URL seed;

		private InvertedIndex myInvertedIndex;

		private WorkQueue queue;

		private static int max;

		public Task(URL seed, InvertedIndex myInvertedIndex, WorkQueue queue, int inputmax) {
			this.seed = seed;
			this.myInvertedIndex = myInvertedIndex;
			this.queue = queue;
			max = inputmax;
		}

		@Override
		public void run() {
			System.out.println("Max: " + max);
			System.out.println("Size: " + usedUrls.size());
			ArrayList<URL> urlList = new ArrayList<URL>();
			String html = HtmlFetcher.fetch(seed, 3);
			if (html != null) {
				urlList = LinkParser.getValidLinks(seed, html);
				for (URL currentUrl : urlList) {
					synchronized (usedUrls) {
						if (usedUrls.size() < max) {
							if (usedUrls.add(currentUrl.toString())) {
								System.out.println("Task for: " + currentUrl);
								queue.execute(new Task(currentUrl, myInvertedIndex, queue, max));
							} else {
								// System.out.println("Found Duplicate URL");
							}
						} else {
							System.out.println("Terminating... Crawl Limit Reached");
							break;
						}
					}

				}

				// System.out.println(html);
				myInvertedIndex.addAll(Arrays.asList(TextParser.parse(HtmlCleaner.stripHtml(html))), seed.toString());
				// System.out.println("Adding");

			}
		}

	}
}
