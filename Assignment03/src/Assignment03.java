
import CorpusReder.CorpusReader;
import Indexer.Indexer;
import Structures.Document;
import Tokenizer.ComplexTokenizer;
import Tokenizer.Tokenizer;
import java.util.ArrayList;
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
public class Assignment03 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
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
