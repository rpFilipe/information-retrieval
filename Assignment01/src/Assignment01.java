
import Structures.Document;
import Structures.Posting;
import java.util.ArrayList;
import java.util.List;
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

        if (args.length != 4) {
            usage();
            return;
        }

        try {
            Document doc;
            String cLocation = args[0];

            System.out.println("Reading the corpus from " + cLocation);
            System.out.println("Loading stopwords list from " + args[1]);
            CorpusReader cr;
            Tokenizer ctk = new ComplexTokenizer(args[1], args[2], 3);
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
                    tokenList = ctk.contentProcessor(doc.getContent());

                    for (String s : tokenList) {
                        docId = doc.getDocId();
                        //System.out.println("Term: "+term + " DocID: "+docId);
                        indx.addTerm(s, new Posting(docId));
                    }
                    //System.out.println(indx.toString());

                    if(count == 50)
                        break;
                    count++;
                     
                }
                indx.saveToFile(args[3]);
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
        System.err.println("Example: cranfield/ stopwords/stopwords.txt english test.txt");
    }
}
