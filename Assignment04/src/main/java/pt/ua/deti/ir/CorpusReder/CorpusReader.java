package pt.ua.deti.ir.CorpusReder;


import pt.ua.deti.ir.Parsers.DOMParser;
import pt.ua.deti.ir.Structures.Document;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Joana Conde 
 * @author Ricardo Filipe
 */
public class CorpusReader implements Iterator<Object>{

    private String cLocation;
    private File folder;
    private File[] corpus;
    private int currentIndex;
    private int corpusSize;
    private DOMParser parser;
    
    
    public CorpusReader(String cLocation) throws ParserConfigurationException, SAXException {
        this.cLocation = cLocation;
        folder = new File(cLocation);
        // corpus = folder.listFiles((File file) -> file.getName().endsWith(".xml"));
        currentIndex = 0;
        corpus = folder.listFiles();
        Arrays.sort(corpus);
        corpusSize = corpus.length;
        parser = new DOMParser();
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
    
    public String[] getSentences(String content) {
        content = StringUtils.normalizeSpace(content);
        String[] sentences = content.split("\\s\\.\\s");
        sentences[sentences.length -1] = sentences[sentences.length-1].split("\\s\\.")[0];
        return  sentences;
        }

    public int size(){
        return corpusSize;
    }
    
}
