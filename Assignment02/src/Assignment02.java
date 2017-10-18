
import CorpusReder.CorpusReader;
import Indexer.Indexer;
import Retriever.BooleanRetriever;
import Structures.Document;
import Tokenizer.ComplexTokenizer;
import Tokenizer.SimpleTokenizer;
import Tokenizer.Tokenizer;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
            if (args.length != 6) {
                usage();
                return;
            }
            
            File fqueries = new File(args[4]);
            
            Tokenizer tk;
            Indexer idx = new Indexer(args[3]);
            if(args[5].equalsIgnoreCase("complex"))
                tk = new ComplexTokenizer(args[0], args[1], Integer.parseInt(args[2]));
            else if(args[5].equalsIgnoreCase("simple"))
                tk = new SimpleTokenizer(Integer.parseInt(args[2]));
            else{
                usage();
                return;
            }
            BooleanRetriever br = new BooleanRetriever(tk, idx);
            
            Scanner in = new Scanner(fqueries);
            
            while(in.hasNextLine()){
                String line = in.nextLine();
                System.out.println(line);
                br.search(line);
                
            }
            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Assignment02.class.getName()).log(Level.SEVERE, null, ex);
        } 

    }
    private static void usage() {
        System.err.println("Usage: <index file> <queries file> <tokenizer>");  //TODO
        System.err.println("Example: src/Stopwords/stopwords.txt english 3 index.txt cranfield.queries.txt complex");
    }
    
}
