/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parsers;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author rpfilipe
 */
public class DOMParser {
    
    private DocumentBuilderFactory dbFactory;
    private DocumentBuilder dBuilder;
    
    public DOMParser() throws ParserConfigurationException{
        dbFactory = DocumentBuilderFactory.newInstance();
	dBuilder = dbFactory.newDocumentBuilder();
    }
    
    public Structures.Document parseFile(String fname) throws SAXException, IOException{
        
        Document doc = dBuilder.parse(fname);
        doc.getDocumentElement().normalize();
        
        int docId = Integer.parseInt(((Element)doc.getElementsByTagName("DOCNO").item(0)).getTextContent().trim());
        String content = ((Element) doc.getElementsByTagName("TITLE").item(0)).getTextContent();
        content += ((Element) doc.getElementsByTagName("TEXT").item(0)).getTextContent();

        return new Structures.Document(docId, content);
    }
    
	
    
}
