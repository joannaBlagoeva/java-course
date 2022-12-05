import bg.sofia.uni.fmi.mjt.markdown.MarkdownConverter;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;

public class Main {
	public static void main(String[] args) {
		MarkdownConverter markdownConverter = new MarkdownConverter();

		var out = new OutputStreamWriter(System.out);
		Reader inputstring = new StringReader("# Test page\n" +
				"## two pages");
		BufferedReader reader = new BufferedReader(inputstring);

		markdownConverter.convertMarkdown(reader, out);

	}
}
