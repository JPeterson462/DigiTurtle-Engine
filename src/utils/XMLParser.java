package utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads an XML file and stores all the data in {@link XMLNode} objects,
 * allowing for easy access to the data contained in the XML file.
 * 
 * @author Karl (ThinMatrix)
 *
 */
public class XMLParser {

	private static final Pattern DATA = Pattern.compile(">(.+?)<");
	private static final Pattern START_TAG = Pattern.compile("<(.+?)>");
	private static final Pattern ATTR_NAME = Pattern.compile("(.+?)=");
	private static final Pattern ATTR_VAL = Pattern.compile("\"(.+?)\"");
	private static final Pattern CLOSED = Pattern.compile("(</|/>)");

	/**
	 * Reads an XML file and stores all the data in {@link XMLNode} objects,
	 * allowing for easy access to the data contained in the XML file.
	 * 
	 * @param file - the XML file
	 * @return The root node of the XML structure.
	 */
	public static XMLNode loadXmlFile(InputStream file) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(file));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Can't find the XML file");
			System.exit(0);
			return null;
		}
		try {
			reader.readLine();
			XMLNode node = loadNode(reader);
			reader.close();
			return node;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error with XML file format");
			System.exit(0);
			return null;
		}
	}

	private static XMLNode loadNode(BufferedReader reader) throws Exception {
		String line = reader.readLine().trim();
		if (line.startsWith("</")) {
			return null;
		}
		String[] startTagParts = getStartTag(line).split(" ");
		XMLNode node = new XMLNode(startTagParts[0].replace("/", ""));
		addAttributes(startTagParts, node);
		addData(line, node);
		if (CLOSED.matcher(line).find()) {
			return node;
		}
		XMLNode child = null;
		while ((child = loadNode(reader)) != null) {
			node.addChild(child);
		}
		return node;
	}

	private static void addData(String line, XMLNode node) {
		Matcher matcher = DATA.matcher(line);
		if (matcher.find()) {
			node.setData(matcher.group(1));
		}
	}

	private static void addAttributes(String[] titleParts, XMLNode node) {
		for (int i = 1; i < titleParts.length; i++) {
			if (titleParts[i].contains("=")) {
				addAttribute(titleParts[i], node);
			}
		}
	}

	private static void addAttribute(String attributeLine, XMLNode node) {
		Matcher nameMatch = ATTR_NAME.matcher(attributeLine);
		nameMatch.find();
		Matcher valMatch = ATTR_VAL.matcher(attributeLine);
		valMatch.find();
		node.addAttribute(nameMatch.group(1), valMatch.group(1));
	}

	private static String getStartTag(String line) {
		Matcher match = START_TAG.matcher(line);
		match.find();
		return match.group(1);
	}

}
