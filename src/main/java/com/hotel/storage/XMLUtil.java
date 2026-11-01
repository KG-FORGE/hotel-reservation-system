package com.hotel.storage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Defensive DOM XML Utility.
 * Configured with strict security features to prevent XXE (XML External Entity)
 * and DTD injection vulnerabilities, along with safe atomic file replacement.
 */
public final class XMLUtil {

    private XMLUtil() {
    }

    /**
     * Creates a secure DocumentBuilder instance with XXE protections enabled.
     */
    public static DocumentBuilder createSecureDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        // Security settings to prevent XXE attacks
        try {
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        } catch (ParserConfigurationException ignored) {}

        try {
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        } catch (ParserConfigurationException ignored) {}

        try {
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        } catch (ParserConfigurationException ignored) {}

        try {
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException ignored) {}

        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);

        return factory.newDocumentBuilder();
    }

    /**
     * Parses an XML file into a DOM Document defensively.
     */
    public static Document parseFile(File file) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder builder = createSecureDocumentBuilder();
        return builder.parse(file);
    }

    /**
     * Creates a new empty DOM Document defensively.
     */
    public static Document createNewDocument() throws ParserConfigurationException {
        DocumentBuilder builder = createSecureDocumentBuilder();
        return builder.newDocument();
    }

    /**
     * Atomically writes a DOM Document to the target file.
     * Writes to a temporary file in the same directory first, then replaces target.
     */
    public static void writeDocumentAtomically(Document document, File targetFile) throws Exception {
        File parentDir = targetFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        File tempFile = File.createTempFile("hotel-data-", ".tmp", parentDir);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        } catch (IllegalArgumentException ignored) {}

        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "no");

        try (OutputStream out = new FileOutputStream(tempFile)) {
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(out);
            transformer.transform(source, result);
        }

        // Atomically replace target file
        Path tempPath = tempFile.toPath();
        Path targetPath = targetFile.toPath();

        try {
            Files.move(tempPath, targetPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // Fallback for filesystems that do not support ATOMIC_MOVE across boundaries
            Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            if (Files.exists(tempPath)) {
                try {
                    Files.deleteIfExists(tempPath);
                } catch (IOException ignored) {}
            }
        }
    }

    /**
     * Helper to safely extract child element text content.
     */
    public static String getChildText(Element parent, String tagName) {
        if (parent == null || tagName == null) return "";
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            Node node = list.item(0);
            return node.getTextContent() != null ? node.getTextContent().trim() : "";
        }
        return "";
    }

    /**
     * Helper to safely extract child element text as a list.
     */
    public static List<String> getChildTextList(Element parent, String parentTagName, String childTagName) {
        List<String> results = new ArrayList<>();
        if (parent == null || parentTagName == null || childTagName == null) return results;

        NodeList parentList = parent.getElementsByTagName(parentTagName);
        if (parentList.getLength() > 0) {
            Element container = (Element) parentList.item(0);
            NodeList items = container.getElementsByTagName(childTagName);
            for (int i = 0; i < items.getLength(); i++) {
                String text = items.item(i).getTextContent();
                if (text != null && !text.trim().isEmpty()) {
                    results.add(text.trim());
                }
            }
        }
        return results;
    }

    /**
     * Helper to create a child element with text content.
     */
    public static Element appendChildElement(Document doc, Element parent, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        element.setTextContent(textContent != null ? textContent : "");
        parent.appendChild(element);
        return element;
    }
}
