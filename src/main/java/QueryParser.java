import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class QueryParser {
	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;
	
	public HashSet<? extends TreeSet<String>> parse(Path fileName){
		HashSet<TreeSet<String>> querySet = new HashSet<TreeSet<String>>();
		try (BufferedReader mybr = Files.newBufferedReader(fileName, StandardCharsets.UTF_8);){
			for(String line = mybr.readLine(); line !=null; line = mybr.readLine()) {
				TreeSet<String> parsed = parseHelper(line);
				if(parsed.size()>0) {
					querySet.add(parsed);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(querySet.toString());
		return querySet;
	}
	private TreeSet<String> parseHelper(String line){
		SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
		TreeSet<String> queryLine = new TreeSet<String>();
		for(String i:TextParser.parse(line)) {
			queryLine.add(stemmer.stem(i).toString());
		}
		return queryLine;
	}
	/*
	public static void main(String args[]) {
		String test = "meep moop im poop 3234 asd#d\thi:)";
		String cleaned = Normalizer.normalize(test, Normalizer.Form.NFD);
		System.out.println(CLEAN_REGEX.matcher(cleaned).replaceAll(""));
	}
	*/

}
