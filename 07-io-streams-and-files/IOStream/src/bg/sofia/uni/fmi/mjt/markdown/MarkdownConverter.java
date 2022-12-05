package bg.sofia.uni.fmi.mjt.markdown;

import bg.sofia.uni.fmi.mjt.markdown.models.HtmlTags;
import bg.sofia.uni.fmi.mjt.markdown.models.Markdown;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MarkdownConverter implements MarkdownConverterAPI {

	private static LinkedHashMap<Markdown, HtmlTags> markdownDictionary;

	/**
	 * Descripe the order and what is what
	 */
	public MarkdownConverter() {
		markdownDictionary = new LinkedHashMap<>();

		markdownDictionary.put(new Markdown("#", "^#{1} (?<textWithin>.+)")
				, new HtmlTags("<h1>", "</h1>"));
		markdownDictionary.put(new Markdown("##", "^#{2} (?<textWithin>.+)"),
				new HtmlTags("<h2>", "</h2>"));
		markdownDictionary.put(new Markdown("###", "^#{3} (?<textWithin>.+)"),
				new HtmlTags("<h3>", "</h3>"));
		markdownDictionary.put(new Markdown("####", "^#{4} (?<textWithin>.+)"),
				new HtmlTags("<h4>", "</h4>"));
		markdownDictionary.put(new Markdown("#####", "^#{5} (?<textWithin>.+)"),
				new HtmlTags("<h5>", "</h5>"));
		markdownDictionary.put(new Markdown("######", "^#{6} (?<textWithin>.+)"),
				new HtmlTags("<h6>", "</h6>"));

		markdownDictionary.put(new Markdown("**", "\\*\\*(?<textWithin>.*?)\\*\\*"),
				new HtmlTags("<strong>", "</strong>"));
		markdownDictionary.put(new Markdown("*", "\\*(?<textWithin>.*?)\\*"),
				new HtmlTags("<em>", "</em>"));
		markdownDictionary.put(new Markdown("`", "\\`(?<textWithin>.*?)\\`"),
				new HtmlTags("<code>", "</code>"));

	}

	@Override
	public void convertMarkdown(Reader source, Writer output) {
		StringBuilder content = new StringBuilder();
		content.append(String.format("<html>%s<body>%s", System.lineSeparator(), System.lineSeparator()));

		try (var bufferedReader = new BufferedReader(source)) {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				content.append(convertLine(line));
			}
		} catch (IOException e) {
			throw new IllegalStateException("A problem occurred while reading from a file", e);
		}

		content.append(String.format("</body>%s</html>%s", System.lineSeparator(), System.lineSeparator()));

		try (var bufferedWriter = new BufferedWriter(output)) {
			bufferedWriter.write(content.toString());
			bufferedWriter.flush();
		} catch (IOException e) {
			throw new IllegalStateException("A problem occurred while writing to a file", e);
		}
	}

	@Override
	public void convertMarkdown(Path from, Path to) {

		StringBuilder sb = new StringBuilder();

		sb.append(String.format("<html>%s<body>%s", System.lineSeparator(), System.lineSeparator()));
		sb.append(readFromFile(from));
		sb.append(String.format("</body>%s</html>%s", System.lineSeparator(), System.lineSeparator()));

		writeToFile(to, sb.toString());
	}

	@Override
	public void convertAllMarkdownFiles(Path sourceDir, Path targetDir) {

		if (Files.exists(sourceDir)) {
			File[] allContents = sourceDir
					.toFile()
					.listFiles(pathname -> pathname.getName().endsWith(".md"));

			if (allContents != null) {
				for (File file : allContents) {
					File newFile = new File(
							targetDir.toString(),
							file.getName().replace(".md", ".html")
					);

					convertMarkdown(file.toPath(), newFile.toPath());
				}
			}
		}
	}

	private static void writeToFile(Path filePath, String text) {
		try (var bufferedWriter = Files.newBufferedWriter(filePath)) {
			bufferedWriter.write(text);
			bufferedWriter.flush();
		} catch (IOException e) {
			throw new IllegalStateException("A problem occurred while writing to a file", e);
		}
	}


	private static String readFromFile(Path filePath) {
		StringBuilder content = new StringBuilder();

		try (var bufferedReader = Files.newBufferedReader(filePath)) {
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				content.append(convertLine(line));
			}

		} catch (IOException e) {
			throw new IllegalStateException("A problem occurred while reading from a file", e);
		}

		return content.toString();
	}

	private static String convertLine(String line) {
		String translatedLine = line;

		for (Markdown markdown : markdownDictionary.keySet()) {
			Pattern pat = Pattern.compile(markdown.regex());
			Matcher match = pat.matcher(translatedLine);

			while (match.find()) {
				translatedLine = match.replaceAll(
						String.format("%s%s%s",
								markdownDictionary.get(markdown).open(),
								match.group("textWithin"),
								markdownDictionary.get(markdown).close())
				);
			}
		}
		return translatedLine + System.lineSeparator();
	}

}
