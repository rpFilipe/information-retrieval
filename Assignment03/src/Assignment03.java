
import CorpusReder.CorpusReader;
import Indexer.Indexer;
import Structures.Document;
import Tokenizer.ComplexTokenizer;
import Tokenizer.SimpleTokenizer;
import Tokenizer.Tokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação
 *
 * @author Joana Conde
 * @author Ricardo Filipe
 */
public class Assignment03 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println(args[0]);
        if (args[0].equalsIgnoreCase("index")) {

            try {
                Document doc;
                String cLocation = args[1];
                CorpusReader cr;
                Tokenizer ctk;

                if (args[6].equalsIgnoreCase("complex")) {
                    ctk = new ComplexTokenizer(args[2], args[3], Integer.parseInt(args[4]));
                } else if (args[6].equalsIgnoreCase("simple")) {
                    ctk = new SimpleTokenizer(Integer.parseInt(args[4]));
                } else {
                    usage(args);
                    return;
                }

                System.out.println("Reading the corpus from " + cLocation);
                System.out.println("Loading stopwords list from " + args[1]);

                // estrutura de dados com os tokens
                List<String> tokenList = new ArrayList<>();
                Indexer indx = new Indexer();
                int docId;

                System.out.println("Document Processor initialized...");
                try {
                    cr = new CorpusReader(cLocation);
                    //cr.printCorpusDocuments();

                    while (cr.hasNext()) {
                        doc = ((Document) cr.next());
                        tokenList = ctk.contentProcessor(doc.getContent());
                        indx.indexTerms(doc.getDocId(), tokenList);

                    }
                    indx.saveToFile(args[4]);
                    //indx.getTermInOneDoc();
                    //ndx.getTermHigherFreq();
                    System.out.println("Document Processor finished...");
                } catch (SAXException ex) {
                    Logger.getLogger(Assignment03.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (ParserConfigurationException ex) {
                Logger.getLogger(Assignment03.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                try {
                    Document doc;
                    String cLocation = args[0];
                    CorpusReader cr;
                    Tokenizer ctk;

                    if (args[5].equalsIgnoreCase("complex")) {
                        ctk = new ComplexTokenizer(args[1], args[2], Integer.parseInt(args[3]));
                    } else if (args[5].equalsIgnoreCase("simple")) {
                        ctk = new SimpleTokenizer(Integer.parseInt(args[3]));
                    } else {
                        usage(args);
                        return;
                    }

                    System.out.println("Reading the corpus from " + cLocation);
                    System.out.println("Loading stopwords list from " + args[1]);

                    // estrutura de dados com os tokens
                    List<String> tokenList = new ArrayList<>();
                    Indexer indx = new Indexer();
                    int docId;

                    System.out.println("Document Processor initialized...");
                    try {
                        cr = new CorpusReader(cLocation);
                        //cr.printCorpusDocuments();

                        while (cr.hasNext()) {
                            doc = ((Document) cr.next());
                            tokenList = ctk.contentProcessor(doc.getContent());
                            indx.indexDoc(doc.getDocId(), tokenList);

                        }
                        indx.saveToFile(args[4]);
                        //indx.getTermInOneDoc();
                        //ndx.getTermHigherFreq();
                        System.out.println("Document Processor finished...");
                    } catch (SAXException ex) {
                        Logger.getLogger(Assignment03.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } catch (ParserConfigurationException ex) {
                    Logger.getLogger(Assignment03.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    Document doc;
                    String cLocation = args[0];
                    CorpusReader cr;
                    Tokenizer ctk;

                    ctk = new ComplexTokenizer(args[1], args[2], Integer.parseInt(args[3]));

                    System.out.println("Reading the corpus from " + cLocation);
                    System.out.println("Loading stopwords list from " + args[1]);

                    // estrutura de dados com os tokens
                    List<String> tokenList = new ArrayList<>();
                    Indexer indx = new Indexer();
                    int docId;

                    System.out.println("Document Processor initialized...");
                    try {
                        cr = new CorpusReader(cLocation);
                        //cr.printCorpusDocuments();

                        while (cr.hasNext()) {
                            doc = ((Document) cr.next());
                            tokenList = ctk.contentProcessor(doc.getContent());
                            indx.indexDoc(doc.getDocId(), tokenList);

                        }

                        indx.saveToFile("test.txt");
                        System.out.println("Document Processor finished...");
                    } catch (SAXException ex) {
                        Logger.getLogger(Assignment03.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } catch (ParserConfigurationException ex) {
                    Logger.getLogger(Assignment03.class.getName()).log(Level.SEVERE, null, ex);
                }
                Document doc;
                String cLocation = args[0];
                CorpusReader cr;
                Tokenizer ctk;

                ctk = new ComplexTokenizer(args[1], args[2], Integer.parseInt(args[3]));

                System.out.println("Reading the corpus from " + cLocation);
                System.out.println("Loading stopwords list from " + args[1]);

                // estrutura de dados com os tokens
                List<String> tokenList = new ArrayList<>();
                Indexer indx = new Indexer();
                int docId;

                System.out.println("Document Processor initialized...");
                try {
                    cr = new CorpusReader(cLocation);
                    //cr.printCorpusDocuments();

                    while (cr.hasNext()) {
                        doc = ((Document) cr.next());
                        tokenList = ctk.contentProcessor(doc.getContent());
                        indx.indexDoc(doc.getDocId(), tokenList);

                    }

                    indx.saveToFile("test.txt");
                    System.out.println("Document Processor finished...");
                } catch (SAXException ex) {
                    Logger.getLogger(Assignment03.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (ParserConfigurationException ex) {
                Logger.getLogger(Assignment03.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (args[0].equalsIgnoreCase("search")) {
            try {
                Document doc;
                String cLocation = args[0];
                CorpusReader cr;
                Tokenizer ctk;

                ctk = new ComplexTokenizer(args[1], args[2], Integer.parseInt(args[3]));

                System.out.println("Reading the corpus from " + cLocation);
                System.out.println("Loading stopwords list from " + args[1]);

                // estrutura de dados com os tokens
                List<String> tokenList = new ArrayList<>();
                Indexer indx = new Indexer();
                int docId;

                System.out.println("Document Processor initialized...");
                try {
                    cr = new CorpusReader(cLocation);
                    //cr.printCorpusDocuments();

                    while (cr.hasNext()) {
                        doc = ((Document) cr.next());
                        tokenList = ctk.contentProcessor(doc.getContent());
                        indx.indexDoc(doc.getDocId(), tokenList);

                    }

                    indx.saveToFile("test.txt");
                    System.out.println("Document Processor finished...");
                } catch (SAXException ex) {
                    Logger.getLogger(Assignment03.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (ParserConfigurationException ex) {
                Logger.getLogger(Assignment03.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void usage(String[] args) {
        //TODO
        for (String arg : args) {
            System.out.println(arg);
        }    }

}
