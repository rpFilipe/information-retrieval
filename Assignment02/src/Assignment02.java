import Indexer.Indexer;
import Retriever.BooleanRetriever;
import Structures.QueryResult;
import Tokenizer.ComplexTokenizer;
import Tokenizer.SimpleTokenizer;
import Tokenizer.Tokenizer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;




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
            String outFname= "queryResult.txt";
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
            
            TreeSet<QueryResult> qresult;
            //cleaning old file
            FileOutputStream outstream = new FileOutputStream(outFname);
            outstream.close();
            
            Scanner in = new Scanner(fqueries);
            while(in.hasNextLine()){
                String line = in.nextLine();
                qresult = br.search(line, 'a');
                saveinFile(outFname, qresult);
            }
            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Assignment02.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Assignment02.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    private static void usage() {
        System.err.println("Usage: <index file> <queries file> <tokenizer>");  //TODO
        System.err.println("Example: src/Stopwords/stopwords.txt english 3 index.txt cranfield.queries.txt complex");
    }
    
    private static void saveinFile(String fname, TreeSet<QueryResult> qresult){
        try {
            FileOutputStream outstream = new FileOutputStream(fname, true);
            Writer output = new OutputStreamWriter(outstream);
            output = new BufferedWriter(output);
            
            String print;
            for(QueryResult q : qresult){
                print = q.toString();
                print += "\n";
                output.write(print);
                output.flush();
            }
            outstream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Assignment02.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Assignment02.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
