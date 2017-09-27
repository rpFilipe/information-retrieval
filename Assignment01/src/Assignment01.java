
import java.io.IOException;
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
public class Assignment01 {

    public static void main(String[] args) {
        
        if(args.length !=3){
            usage();
            return;
        }
        
        try {
            String cLocation = args[0];
            System.out.println("Reading the corpus from " + cLocation);
            CorpusReader cr = new CorpusReader(cLocation);
            //cr.printCorpusDocuments();
            while (cr.hasNext()) {
                cr.next();

            }

            System.out.println("parsing finished");

            cLocation = args[1];
            System.out.println("Loading stopwords list from " + cLocation);
            Tokenizer stk = new SimpleTokenizer();
            Tokenizer ctk = new ComplexTokenizer();
      
            System.out.println("Document Processor started...");
            
            // falta associar aos corpus reader
            try {
                Stopwords sw = new Stopwords(cLocation);
            } catch (IOException ex) {
                Logger.getLogger(Assignment01.class.getName()).log(Level.SEVERE, null, ex);
            }
            Stemmer stemmer = new Stemmer("args[2]"); 

            System.out.println("Document Processor finished...");

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Assignment01.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Assignment01.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void usage()
    {
        System.err.println("Usage: <path to corpus folder> <path to stopwords list file> <language> ");
        System.err.println("Example: <cranfield/> <stopwords/stopwords.txt> <english> ");
    }
}
