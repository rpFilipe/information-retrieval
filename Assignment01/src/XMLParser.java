
import Structures.Document;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rpfilipe
 */
public class XMLParser {

    private SAXParserFactory factory;
    private SAXParser saxParser;
    private DefaultHandler handler;
    private Document doc;

    public XMLParser() throws ParserConfigurationException, SAXException {
        factory = SAXParserFactory.newInstance();
        saxParser = factory.newSAXParser();
        handler = new DefaultHandler() {

            private boolean isDocId;
            private boolean isToIgnore = false;
            private int docId;
            private String content = "";

            @Override
            public void characters(char ch[], int start, int length) {
                if (isDocId) {
                    docId = Integer.parseInt(new String(ch, start, length).trim());
                    isDocId = false;
                } else if (isToIgnore) {
                    isToIgnore = false;
                } else {
                    content = content + " " + new String(ch, start, length).trim();
                }

            }

            @Override
            public void startElement(String uri, String localName, String qname, Attributes atrbts) {
                if (qname.equalsIgnoreCase("DOCNO")) {
                    isDocId = true;
                } //irrelevant sections
                else if (qname.equalsIgnoreCase("AUTHOR") || qname.equalsIgnoreCase("BIBLIO")) {
                    isToIgnore = true;
                }
            }

            @Override
            public void endDocument() {
                doc = new Document(docId, content);
            }

        };
    }

    public Document parseFile(String fname) throws SAXException, IOException {
        saxParser.parse(fname, handler);
        return doc;
    }

}
