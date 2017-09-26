
import Structures.Document;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
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
public class CorpusReader implements Iterator<Object>{

    private String cLocation;
    private File folder;
    private File[] corpus;
    private int currentIndex;
    private int corpusSize;
    private XMLParser parser;
    
    public CorpusReader(String cLocation) throws ParserConfigurationException, SAXException {
        this.cLocation = cLocation;
        folder = new File(cLocation);
        // corpus = folder.listFiles((File file) -> file.getName().endsWith(".xml"));
        currentIndex = 0;
        corpus = folder.listFiles();
        corpusSize = corpus.length;
        parser = new XMLParser();
    }
    
    public void printCorpusDocuments() {
        for (File doc : corpus) {
            System.out.println(doc.getName());
        }
    }
    
    @Override
    public boolean hasNext() {
        return currentIndex < corpusSize && corpus[currentIndex] != null;

    }

    @Override
    public Object next() {
        // Parsing of file
        return parseDocument(corpus[currentIndex++]);
        
        
    }
    
    private Document parseDocument(File doc) {
        try {
            return parser.parseFile(doc.getAbsolutePath());
        } catch (SAXException ex) {
            Logger.getLogger(CorpusReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CorpusReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}