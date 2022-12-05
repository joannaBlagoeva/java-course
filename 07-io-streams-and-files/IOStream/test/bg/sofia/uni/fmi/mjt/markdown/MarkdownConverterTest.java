package bg.sofia.uni.fmi.mjt.markdown;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MarkdownConverterTest {

	static File mdFile, htmlFile;
	static File tempDirectory;

	@BeforeAll
	public static void setUp() {

		try {
			tempDirectory = Files.createDirectory(Path.of("./tempDirectory")).toFile();
			mdFile = Files.createFile(Path.of(tempDirectory.toPath() + "test.md")).toFile();
			htmlFile = Files.createFile(Path.of(tempDirectory.toPath() + "htmlTest.html")).toFile();

			Files.write(mdFile.toPath(),
					String.format("# header1%s## header2%s", System.lineSeparator(), System.lineSeparator())
							.getBytes());

		} catch (IOException ioe) {
			System.err.println(
					"Error creating and writing temporary test file.");
		}

		mdFile.deleteOnExit();
		htmlFile.deleteOnExit();
		tempDirectory.deleteOnExit();
	}

	public String createHtml(String htmlContent) {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("<html>%s<body>%s", System.lineSeparator(), System.lineSeparator()));
		sb.append(htmlContent);
		sb.append(String.format("</body>%s</html>%s", System.lineSeparator(), System.lineSeparator()));

		return sb.toString();
	}

	@Test
	public void testConvertMarkdownEmpty() {

		Writer outputString = new StringWriter(20);

		Reader inputstring = new StringReader("");
		BufferedReader reader = new BufferedReader(inputstring);

		MarkdownConverter markdownConverter = new MarkdownConverter();
		markdownConverter.convertMarkdown(reader, outputString);

		assertEquals(createHtml(""), outputString.toString(), "Html conversion was incorrect.");
	}

	@Test
	public void testConvertMarkdownWithContent() {

		Writer outputString = new StringWriter(20);

		Reader inputstring = new StringReader("*italic*");
		BufferedReader reader = new BufferedReader(inputstring);

		MarkdownConverter markdownConverter = new MarkdownConverter();
		markdownConverter.convertMarkdown(reader, outputString);

		assertEquals(createHtml("<em>italic</em>" + System.lineSeparator()),
				outputString.toString(),
				"Html conversion was incorrect.");
	}

	@Test
	public void testMarkdownConverterFile() throws IOException {

		MarkdownConverter markdownConverter = new MarkdownConverter();
		markdownConverter.convertMarkdown(mdFile.toPath(), htmlFile.toPath());
		String expectedContent = "<h1>header1</h1>%s<h2>header2</h2>%s";

		assertEquals(
				Arrays.stream(createHtml(
						String.format(expectedContent,
								System.lineSeparator(),
								System.lineSeparator())
				).split(System.lineSeparator())).toList(),
				Files.readAllLines(htmlFile.toPath()),
				"Html conversion was incorrect for files."
		);
	}
}
