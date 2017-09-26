
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
        try {
            String cLocation = "cranfield/";
            System.out.println("Reading the corpus from " + cLocation);
            CorpusReader cr = new CorpusReader(cLocation);
            //cr.printCorpusDocuments();
            while (cr.hasNext()) {
                //System.out.println(cr.next());
                cr.next();
            }

            System.out.println("parsing finished");

            cLocation = "stopwords/stopwords.txt";
            System.out.println("Loading stopwords list from " + cLocation);
            Tokenizer tk = new Tokenizer(cLocation);
            System.out.println("Document Processor started...");
            // apagar
            //System.out.println(tk.contentProcessor("It is a nice day!").toString());
     
            System.out.println("Document Processor finished...");

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Assignment01.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Assignment01.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}