
import CorpusReder.CorpusReader;
import Tokenizer.SimpleTokenizer;
import Tokenizer.ComplexTokenizer;
import Tokenizer.Tokenizer;
import Indexer.Indexer;
import Structures.Document;
import Structures.Posting;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Joana Conde 
 * @author Ricardo Filipe
 */
public class Assignment01 {

    public static void main(String[] args) {

        if (args.length != 5) {
            usage();
            return;
        }

        try {
            Document doc;
            String cLocation = args[0];

            System.out.println("Reading the corpus from " + cLocation);
            System.out.println("Loading stopwords list from " + args[1]);
            CorpusReader cr;
            Tokenizer stk = new SimpleTokenizer(Integer.parseInt(args[3]));
            Tokenizer ctk = new ComplexTokenizer(args[1], args[2], Integer.parseInt(args[3]));
            // estrutura de dados com os tokens
            List<String> tokenList = new ArrayList<>();
            Indexer indx = new Indexer();
            int docId;

            // para testar um numero limitado de corpus
            int count = 0;

            System.out.println("Document Processor initialized...");
            try {
                cr = new CorpusReader(cLocation);
                    
                //cr.printCorpusDocuments();
                while (cr.hasNext()) {
                    doc = ((Document) cr.next());
                    //tokenList = ctk.contentProcessor(doc.getContent());
                    tokenList = stk.contentProcessor(doc.getContent());
                    indx.indexDoc(doc.getDocId(), tokenList);
                     
                }
                indx.saveToFile(args[4]);
                //indx.getTermInOneDoc();
                //indx.getTermHigherFreq();
                System.out.println("Document Processor finished...");
            } catch (SAXException ex) {
                Logger.getLogger(Assignment01.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Assignment01.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void usage() {
        System.err.println("Usage: <path to corpus folder> <path to stopwords list file> <language> <filename to write the resulting index>");
        System.err.println("Example: cranfield/ src/Stopwords/stopwords.txt english  3 test.txt");
    }
}
