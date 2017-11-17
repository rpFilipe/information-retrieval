package pt.ua.deti.ir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import pt.ua.deti.ir.CorpusReder.CorpusReader;
import pt.ua.deti.ir.Indexer.Indexer;
import pt.ua.deti.ir.Structures.Document;
import pt.ua.deti.ir.Tokenizer.ComplexTokenizer;
import pt.ua.deti.ir.Tokenizer.SimpleTokenizer;
import pt.ua.deti.ir.Tokenizer.Tokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import pt.ua.deti.ir.Measures.MeasuresHandler;
import pt.ua.deti.ir.Retriever.BooleanRetriever;
import pt.ua.deti.ir.Retriever.RankedRetriever;
import pt.ua.deti.ir.Structures.QueryResult;

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
                    usage();
                    return;
                }

                System.out.println("Reading the corpus from " + cLocation);
                System.out.println("Loading stopwords list from " + args[1]);

                // estrutura de dados com os tokens
                List<String> tokenList = new ArrayList<>();
                Indexer indx = new Indexer();
                indx.setTokenizer(ctk);
                int docId;

                System.out.println("Document Processor initialized...");
                try {
                    cr = new CorpusReader(cLocation);
                    //cr.printCorpusDocuments();

                    while (cr.hasNext()) {
                        doc = ((Document) cr.next());
                        tokenList = ctk.contentProcessor(doc.getContent());
                        //List testlist = ctk.contentProcessor("cats");
                        //System.out.println(testlist.toString());
                        indx.indexTerms(doc.getDocId(), tokenList);

                    }
                    indx.saveToFile(args[5]);
                    System.out.println("Document Processor finished...");
                } catch (SAXException ex) {
                    Logger.getLogger(Assignment03.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (ParserConfigurationException ex) {
                Logger.getLogger(Assignment03.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (args[0].equalsIgnoreCase("search")) {
            try {

                File fqueries = new File(args[2]);
                MeasuresHandler mh = new MeasuresHandler("cranfield.query.relevance.txt");
                Indexer idx = new Indexer(args[1]);
                RankedRetriever rr = new RankedRetriever(idx);

                TreeSet<QueryResult> qresult;
                //cleaning old file
                String outFname = "queryResult_ranked_" + idx.getTk().getClass().getSimpleName() + ".txt";
                FileOutputStream outstream = new FileOutputStream(outFname);
                outstream.close();

                Scanner in = new Scanner(fqueries);
                boolean firstline = true;
                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    qresult = rr.search(line);
                    mh.computeQueryMeasures(qresult);
                    saveinFile(outFname, qresult, firstline);
                    firstline = false;
                }
                mh.computeRetrieverMeasures();
                System.out.println("Query throughput: "+rr.getQueryThroughput() + "ns");
                in.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Assignment03.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Assignment03.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else if(args[0].equalsIgnoreCase("search2")){
            try {
            
            
            File fqueries = new File(args[2]);
            char score;
            if(args[3].equalsIgnoreCase("a") || args[3].equalsIgnoreCase("b"))
                score = args[3].charAt(0);
            else{
                usage();
                return;
            }
            
            MeasuresHandler mh = new MeasuresHandler("cranfield.query.relevance.txt");
            Indexer idx = new Indexer(args[1]);
            BooleanRetriever br = new BooleanRetriever(idx);
            RankedRetriever rr = new RankedRetriever(idx);
            
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
                mh.computeQueryMeasures(qresult);
                saveinFile(outFname, qresult, score, firstline);
                firstline = false;
            }
            
            mh.computeRetrieverMeasures();
            // TODO System.out.println("Query throughput: "+rr.getQueryThroughput() + "ns");
            in.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Assignment03.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Assignment03.class.getName()).log(Level.SEVERE, null, ex);
        }

        }else{
            usage();
        }
    }

    private static void usage() {
        System.out.println("Usage:\n"
                + "index cranfield/ src/main/java/pt/ua/deti/ir/Stopwords/stopwords.txt english 3 index.idx complex\n"
                + "or\nsearch index.idx cranfield.queries.txt"
                + "or\nsearch2 index_complex.txt cranfield.queries.txt a\n"
                + "or\nsearch2 index_complex.txt cranfield.queries.txt b");
    }

    private static void saveinFile(String fname, TreeSet<QueryResult> qresult, boolean firstline) {
        try {
            FileOutputStream outstream = new FileOutputStream(fname, true);
            Writer output = new OutputStreamWriter(outstream);
            output = new BufferedWriter(output);

            String print;
            if (firstline) {
                print = "query_id\tdoc_id\t\tdoc_score_\n";
                output.write(print);
                output.flush();
            }
            for (QueryResult q : qresult) {
                print = q.toString();
                print += "\n";
                output.write(print);
                output.flush();
            }
            outstream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Assignment03.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Assignment03.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            Logger.getLogger(Assignment03.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Assignment03.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
