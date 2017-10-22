import Indexer.Indexer;
import Retriever.BooleanRetriever;
import Structures.QueryResult;
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
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Joana Conde 
 * @author Ricardo Filipe
 */
public class Assignment02 {
    
    public static void main(String[] args) {

        try {
            if (args.length != 3) {
                usage();
                return;
            }
            
            File fqueries = new File(args[1]);
            char score;
            if(args[2].equalsIgnoreCase("a") || args[2].equalsIgnoreCase("b"))
                score = args[2].charAt(0);
            else{
                usage();
                return;
            }
            
            Indexer idx = new Indexer(args[0]);
            BooleanRetriever br = new BooleanRetriever(idx.getTk(), idx);
            
            TreeSet<QueryResult> qresult;
            //cleaning old file
            String outFname= "queryResult_"+score+"_"+idx.getTk().getClass().getSimpleName()+".txt";
            FileOutputStream outstream = new FileOutputStream(outFname);
            outstream.close();
            
            Scanner in = new Scanner(fqueries);
            boolean firstline = true;
            while(in.hasNextLine()){
                String line = in.nextLine();
                qresult = br.search(line, score);
                saveinFile(outFname, qresult, score, firstline);
                firstline = false;
            }
            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Assignment02.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Assignment02.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    private static void usage() {
        System.err.println("Usage: <index file> <queries file> <choose score: a or b>");  
        System.err.println("Example: index_simple.txt cranfield.queries.txt a");
    }
    
    private static void saveinFile(String fname, TreeSet<QueryResult> qresult, char score, boolean firstline){
        try {
            FileOutputStream outstream = new FileOutputStream(fname, true);
            Writer output = new OutputStreamWriter(outstream);
            output = new BufferedWriter(output);
            
            String print;
            if(firstline) {
                print = "query_id\tdoc_id\t\tdoc_score_"+ score+"\n";
                output.write(print);
                output.flush();
            }
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
