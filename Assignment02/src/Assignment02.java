
import CorpusReder.CorpusReader;
import Indexer.Indexer;
import Structures.Document;
import Tokenizer.ComplexTokenizer;
import Tokenizer.SimpleTokenizer;
import Tokenizer.Tokenizer;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;




/**
 *
 * @author joana
 */
public class Assignment02 {

    public static void main(String[] args) {

        try {
            if (args.length != 3) {
                usage();
                return;
            }
            
            File index  = new File(args[0]);
            File queries = new File(args[1]);
            
            Indexer idx = new Indexer(args[0]);
            
            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Assignment02.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        

    }
    private static void usage() {
        System.err.println("Usage: <index file> <queries file> <tokenizer>");
        System.err.println("Example: index0.txt queries.txt complex");
    }
    
}
