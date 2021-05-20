import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import java.util.Map;
import java.util.Set;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// More XSS Prevention:
// https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet

// Apache Comments:
// https://commons.apache.org/proper/commons-lang/download_lang.cgi

/**
 * The servlet class responsible for setting up a simple message board.
 *
 * @see MessageServer
 */
public class SearchEngineServlet extends HttpServlet {

	/** Class version for serialization, in [YEAR][TERM] format (unused). */
	private static final long serialVersionUID = 202040;

	/** The title to use for this webpage. */
	private static final String TITLE = "Search Engine";

	/** cookies */
	private static final String ENABLE_COOKIES = "IlikeCookies";

	/** Template for HTML. **/
	private final String htmlTemplate;

	/** Base path with HTML templates. */
	private static final Path BASE = Path.of("src", "main", "resources", "html");

	/** the index we will search through */
	private final ThreadSafeInvertedIndex index;

	private static final HashMap<String, Integer> pop = new HashMap<String, Integer>();

	private static Integer minpop = Integer.MAX_VALUE;

	/**
	 * Initializes this message board. Each message board has its own collection of
	 * messages.
	 * 
	 * @param index the inverted index to search through
	 * 
	 * @throws IOException if unable to read template
	 */
	public SearchEngineServlet(ThreadSafeInvertedIndex index) throws IOException {
		super();
		htmlTemplate = Files.readString(BASE.resolve("index.html"), StandardCharsets.UTF_8);
		this.index = index;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// set content to desired type
		response.setContentType("text/html");

		// initailizes the value and cookie map
		Map<String, String> values = new HashMap<>();
		Map<String, Cookie> cookies = getCookieMap(request);

		values.put("prevquery", "");
		values.put("map", "");

		// if we have tracking
		if (request.getParameter("ok_cookies") != null) {
			// checks if cookie already exists
			if (!cookies.containsKey(ENABLE_COOKIES)) {
				Cookie cookie = new Cookie(ENABLE_COOKIES, Boolean.TRUE.toString());
				cookie.setMaxAge(24 * 60 * 60);
				response.addCookie(cookie);
			} else {
				Cookie extendCookie = cookies.get(ENABLE_COOKIES);
				extendCookie.setMaxAge(24 * 60 * 60);
				response.addCookie(extendCookie);
			}
			if (cookies.containsKey("history")) {
				values.put("history", cookies.get("history").getValue());
			} else {
				values.put("history", "No Search History");
			}
			if (cookies.containsKey("time")) {
				values.put("time", cookies.get("time").getValue());
			} else {
				values.put("time", "No Data");
				Cookie time = new Cookie("time", getDate());
				time.setMaxAge(24 * 60 * 60);
				response.addCookie(time);
			}
			if (pop.isEmpty()) {
				values.put("map", "");
			} else {
				values.put("map", formatPop(pop.keySet()));
			}
		}
		// if we don't have tracking
		if (request.getParameter("no_cookies") != null) {
			clearCookies(request, response);
			values.put("history", "value unavailable w/o cookies");
			values.put("time", "value unavailable w/o cookies");
			values.put("map", "value unavaible w/o cookies");
		}

		// this occurs if the cookie process is skipped
		if (request.getParameter("ok_cookies") == null && request.getParameter("no_cookies") == null) {
			if (cookies.containsKey(ENABLE_COOKIES)) {
				if (cookies.containsKey("history")) {
					values.put("history", cookies.get("history").getValue());
				} else {
					values.put("history", "No Search History");
				}
				if (cookies.containsKey("time")) {
					values.put("time", cookies.get("time").getValue());
				} else {
					values.put("time", "No Data");
					Cookie time = new Cookie("time", getDate());
					time.setMaxAge(24 * 60 * 60);
					response.addCookie(time);
				}
				if (pop.isEmpty()) {
					values.put("map", "");
				} else {
					values.put("map", formatPop(pop.keySet()));
				}
			} else {
				values.put("history", "value unavailable w/o cookies");
				values.put("time", "value unavialable w/o cookies");
				values.put("map", "value unavialable w/o cookies");
			}
		}

		// form set up
		values.put("title", TITLE);
		values.put("thread", Thread.currentThread().getName());
		values.put("method", "POST");
		values.put("action", request.getServletPath());
		values.put("results", "Search Anything :)");

		// generate html from template
		StringSubstitutor replacer = new StringSubstitutor(values);
		String html = replacer.replace(htmlTemplate);

		// output generated html
		PrintWriter out = response.getWriter();
		out.println(html);
		out.flush();

		// finish
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// sets the content to the desired type
		response.setContentType("text/html");

		// initailizes the value and cookie maps and the results list
		Map<String, String> values = new HashMap<>();
		Map<String, Cookie> cookies = getCookieMap(request);
		ArrayList<InvertedIndex.Result> results = new ArrayList<InvertedIndex.Result>();
		HashSet<String> querySet = null;

		// gets the user input
		String query = request.getParameter("query");
		String clearHistory = request.getParameter("history");

		// checks if null before cleaning the query text
		if (query != null) {
			query = StringEscapeUtils.escapeHtml4(query);
			query = TextParser.clean(query);
			querySet = new HashSet<String>();
			for (String queryItem : query.split("\\s+")) {
				querySet.add(queryItem);
			}
			if (cookies.containsKey(ENABLE_COOKIES)) {
				if (!query.isBlank()) {
					if (pop.containsKey(query)) {
						pop.put(query, pop.get(query) + 1);
					} else {
						pop.put(query, 1);
					}
				}
				HashMap<String, Integer> topfive = addTop();
				values.put("map", formatPop(topfive.keySet()));
			} else {
				values.put("map", "value unavailable w/o cookies");
			}
		}

		values.put("prevquery", "");
		// System.out.println("query: "+query);
		// if the clear history button is pressed clear the history
		if (clearHistory != null) {
			this.clearHistory(values, cookies, response);

			if (query != null) {
				values.put("prevquery", query);
			}
		} else {

			if (cookies.containsKey(ENABLE_COOKIES)) {
				// if we are allowed cookies we have to set up our string builders
				StringBuilder output = new StringBuilder();
				StringBuilder history = new StringBuilder();

				// time cookie
				if (cookies.containsKey("time")) {
					values.put("time", cookies.get("time").getValue());
				} else {
					values.put("time", "No Data");
				}

				if (query == null || query.isBlank()) {
					values.put("results", "Can't be Blank :)");
				} else {

					// puts together the history cookie
					if (cookies.containsKey("history") && cookies.get("history").getValue() != null) {
						// inserts into the premade cookie
						response.addCookie(insertHistory(values, cookies, history, querySet));
					} else {
						// creates a new history cookie
						history.append("<ul>");
						for (String singleQuery : query.split("\\s+")) {
							appendList(history, singleQuery + "_" + getDate());
						}
						history.append("</ul>");
						Cookie historyCookie = new Cookie("history", history.toString());
						historyCookie.setMaxAge(24 * 60 * 60);
						response.addCookie(historyCookie);
					}

					// searches for the query
					if (results.addAll(index.search(querySet, false))) {
						output.append(
								"<table style=\"width:100%\"><tr><th>Location</th><th>Count</th><th>Score</th></tr>");
						Iterator resultsLoop = results.listIterator();
						while (resultsLoop.hasNext()) {
							addResult(output, (InvertedIndex.Result) resultsLoop.next());
						}
						output.append("</table>");
						values.put("results", output.toString());
					} else {
						values.put("results", "Zero Results");
					}
				}
				// sets up the history
				if (!history.isEmpty()) {
					values.put("history", history.toString());
				} else {
					if (cookies.containsKey("history")) {
						values.put("history", cookies.get("history").getValue());
					} else {
						values.put("history", "No Search History");
					}
				}

			} else {
				values.put("history", "value unavailable w/o cookies");
				values.put("time", "value unavailable w/o cookies");
				if (query.isBlank()) {
					values.put("results", "Can't be Blank :)");
				} else {
					StringBuilder output = new StringBuilder();
					StringBuilder history = new StringBuilder();
					localSearch(values, results, querySet, output);
				}
			}
		}

		// finish putting in the values
		values.put("title", TITLE);
		values.put("thread", Thread.currentThread().getName());
		values.put("method", "POST");
		values.put("action", request.getServletPath());

		StringSubstitutor replacer = new StringSubstitutor(values);
		String html = replacer.replace(htmlTemplate);

		// output generated html
		PrintWriter out = response.getWriter();
		out.println(html);
		out.flush();

		// finish no redirect
		response.setStatus(HttpServletResponse.SC_OK);
	}

	private static String formatPop(Set<String> input) {
		StringBuilder toret = new StringBuilder();
		toret.append("<ul>");
		for (String item : input) {
			toret.append("<li>");
			toret.append(item);
			toret.append("</li>");
		}
		toret.append("</ul>");
		return toret.toString();
	}

	private HashMap<String, Integer> addTop() {
		// finds top five pop
		HashMap<String, Integer> topfive = new HashMap<String, Integer>();
		for (String queryPop : pop.keySet()) {
			if (topfive.size() < 5) {
				topfive.put(queryPop, pop.get(queryPop));
				if (pop.get(queryPop) < minpop) {
					minpop = pop.get(queryPop);
				}
				System.out.println("Defualt");
			} else {
				if (minpop < pop.get(queryPop)) {
					int min = Integer.MAX_VALUE;
					String key = "ERROR";
					for (String original : topfive.keySet()) {
						if (topfive.get(original) < min) {
							key = original;
							min = topfive.get(original);
						}
					}
					minpop = min;
					topfive.remove(key);
					topfive.put(queryPop, pop.get(queryPop));
				}
			}
		}
		return topfive;
	}

	/**
	 * clears the history cookie specifically
	 * 
	 * @param values  the map of values to insert
	 * @param cookies the map of cookies
	 */
	private void clearHistory(Map<String, String> values, Map<String, Cookie> cookies, HttpServletResponse response) {
		if (cookies.containsKey(ENABLE_COOKIES)) {
			if (cookies.containsKey("time")) {
				values.put("time", cookies.get("time").getValue());
			} else {
				values.put("time", "No Data");
			}
			if (cookies.containsKey("history")) {
				cookies.get("history").setMaxAge(0);
				cookies.get("history").setValue(null);
				response.addCookie(cookies.get("history"));
			}
			values.put("history", "Search History Cleared ;)");
		} else {
			values.put("history", "History not Available w/o cookies");
			values.put("time", "value unavailable w/o cookies");
		}
		values.put("results", "Search Anything :)");
	}

	/**
	 * performs a search on the index
	 * 
	 * @param values   the map of values to insert
	 * @param results  the results list
	 * @param querySet the query set
	 * @param output   the string builder
	 */
	private void localSearch(Map<String, String> values, ArrayList<InvertedIndex.Result> results, Set<String> querySet,
			StringBuilder output) {
		if (results.addAll(index.search(querySet, false))) {
			output.append("<table style=\"width:100%\"><tr><th>Location</th><th>Count</th><th>Score</th></tr>");
			Iterator resultsLoop = results.listIterator();
			while (resultsLoop.hasNext()) {
				addResult(output, (InvertedIndex.Result) resultsLoop.next());
			}
			output.append("</table>");
			values.put("results", output.toString());
		} else {
			values.put("results", "Zero Results");
		}
	}

	/**
	 * inserts a history value into an already created cookie
	 * 
	 * @param values  the map of values to be inserted
	 * @param cookies the map of cookies
	 * @param history the string builder of the history
	 * @param query   the query set
	 * @return a cookie with the value inserted
	 */
	private Cookie insertHistory(Map<String, String> values, Map<String, Cookie> cookies, StringBuilder history,
			Set<String> querySet) {
		history.append(cookies.get("history").getValue());
		for (String singleQuery : querySet) {
			history.insert(history.length() - 5, "<li>" + singleQuery + "_" + getDate() + "</li>");
		}
		Cookie historyCookie = new Cookie("history", history.toString());
		historyCookie.setMaxAge(24 * 60 * 60);
		return historyCookie;
	}

	/**
	 * Returns the date and time in a long format. For example: "12:00 am on
	 * Saturday, January 01 2000".
	 *
	 * @return current date and time
	 */
	private static String getDate() {
		String format = "hh:mma_MMMM-dd-yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}

	/**
	 * adds a tag efficiently
	 * 
	 * @param output  the string builder
	 * @param tag     the tag
	 * @param content the content to put in between the tag
	 */
	private static void addTag(StringBuilder output, String tag, String content) {
		output.append("<").append(tag).append(">");
		output.append(content);
		output.append("</").append(tag).append(">");
	}

	/**
	 * adds a link
	 * 
	 * @param output the string builder
	 * @param link   the link to add
	 */
	private static void addLink(StringBuilder output, String link) {
		output.append("<a href=\"").append(link).append("\">");
		output.append(link);
		output.append("</a>");
	}

	/**
	 * adds a single result
	 * 
	 * @param output the string builder
	 * @param result the result to add
	 */
	private static void addResult(StringBuilder output, InvertedIndex.Result result) {
		DecimalFormat FORMATTER = new DecimalFormat("0.00000000");
		output.append("<tr><td>");
		addLink(output, result.getLocation());
		output.append("</td>");
		addTag(output, "td", Integer.toString(result.getCount()));
		addTag(output, "td", FORMATTER.format(result.getScore()));
		output.append("</tr>");
	}

	/**
	 * appends a single list element
	 * 
	 * @param output the string builder
	 * @param item   the element to add
	 */
	private static void appendList(StringBuilder output, String item) {
		output.append("<li>").append(item).append("</li>");
	}

	/**
	 * Gets the cookies from the HTTP request and maps the cookie name to the cookie
	 * object.
	 *
	 * @param request the HTTP request from web server
	 * @return map from cookie key to cookie value
	 */
	public static Map<String, Cookie> getCookieMap(HttpServletRequest request) {
		HashMap<String, Cookie> map = new HashMap<>();
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				map.put(cookie.getName(), cookie);
			}
		}

		return map;
	}

	/**
	 * Clears all of the cookies included in the HTTP request.
	 *
	 * @param request  the HTTP request
	 * @param response the HTTP response
	 */
	public static void clearCookies(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				// update cookie values to trigger delete
				cookie.setValue(null);
				cookie.setMaxAge(0);

				// add new cookie to the response
				response.addCookie(cookie);
			}
		}
	}
}
