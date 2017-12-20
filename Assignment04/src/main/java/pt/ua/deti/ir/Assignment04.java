package pt.ua.deti.ir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import pt.ua.deti.ir.CorpusReder.CorpusReader;
import pt.ua.deti.ir.Indexer.Indexer;
import pt.ua.deti.ir.Measures.MeasuresHandler;
import pt.ua.deti.ir.Retriever.RankedRetriever;
import pt.ua.deti.ir.Structures.Document;
import pt.ua.deti.ir.Structures.QueryResult;
import pt.ua.deti.ir.Tokenizer.ComplexTokenizer;
import pt.ua.deti.ir.Tokenizer.Tokenizer;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação
 *
 * @author Joana Conde
 * @author Ricardo Filipe
 */
public class Assignment04 {

    /**
     * Adapted from Word2VecRawTextExample
     */
    private static final Logger log = LoggerFactory.getLogger(Assignment04.class);

    public static void main(String[] args) throws Exception {

        if (args[0].equalsIgnoreCase("index")) {

            try {
                Document doc;
                String cLocation = args[1];
                CorpusReader cr;
                Tokenizer ctk;
                List<String> sentences = new LinkedList<>();
                ctk = new ComplexTokenizer(args[2], args[3], Integer.parseInt(args[4]));

                System.out.println("Reading the corpus from " + cLocation);
                System.out.println("Loading stopwords list from " + args[1]);

                // estrutura de dados com os tokens
                List<String> tokenList;
                Indexer indx = new Indexer();
                indx.setTokenizer(ctk);
                String[] docSentences;
                int docId;

                System.out.println("Document Processor initialized...");
                try {
                    cr = new CorpusReader(cLocation);
                    //cr.printCorpusDocuments();
                    while (cr.hasNext()) {
                        doc = ((Document) cr.next());

                        // Add document content to the sentences list to be used in training
                        docSentences = cr.getSentences(doc.getContent());
                        sentences.addAll(Arrays.asList(docSentences));

                        tokenList = ctk.contentProcessor(doc.getContent());
                        indx.indexTerms(doc.getDocId(), tokenList);

                    }
                    saveSentecesInFile("cranfield_sentences.txt", sentences);
                    indx.saveToFile(args[5]);
                    indx.saveDocCache("docCache.idx");
                    System.out.println("Document Processor finished...");
                } catch (SAXException ex) {
                    java.util.logging.Logger.getLogger(Assignment04.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (ParserConfigurationException ex) {
                java.util.logging.Logger.getLogger(Assignment04.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (args[0].equalsIgnoreCase("search")) {
            System.out.println(args[2]);
            
            Indexer idx = new Indexer(args[1]);
            MeasuresHandler mh = new MeasuresHandler("cranfield.query.relevance.txt");
            RankedRetriever rr = null;
            
            //query_expansion
            if(args[3].equalsIgnoreCase("false"))
                rr = new RankedRetriever(idx, false);
            else if(args[3].equalsIgnoreCase("true"))
                rr = new RankedRetriever(idx, "cranfield_sentences.txt", true);
            else{
                usage();
                System.exit(0);
            }
            
            TreeSet<QueryResult> qresult = null;
            File fqueries = new File("cranfield.queries.txt");
            Scanner in = new Scanner(fqueries);

            String outFname = "queryResult_ranked_" + idx.getTk().getClass().getSimpleName() + ".txt";
            FileOutputStream outstream = new FileOutputStream(outFname);
            outstream.close();

            boolean firstline = true;
            int queriesPrecessed = 0;
            Timestamp begin = new Timestamp(System.currentTimeMillis());

            while (in.hasNextLine()) {
                String line = in.nextLine();
                
                //rocchio feedback
                if(args[4].equalsIgnoreCase("true"))
                    qresult = rr.search(line, args[2], true);
                else if(args[4].equalsIgnoreCase("false"))
                    qresult = rr.search(line, args[2], false, 30);
                else{
                    usage();
                    System.exit(0);
                }
                
                //mh.computeQueryMeasures(qresult);
                //saveinFile(outFname, qresult, firstline, args[2], args[3]);
                firstline = false;
                queriesPrecessed++;
                //break;
            }
            
            //mh.computeRetrieverMeasures();
            in.close();
            
            Timestamp end = new Timestamp(System.currentTimeMillis());
            double delta = end.getTime() - begin.getTime();
            System.out.println("Queries Processed: " + queriesPrecessed);
            System.out.println("Query Throughput: " + (1 / (delta / queriesPrecessed / 1000)) + " queries per second");
            System.out.println("Query Latency: " + (delta / queriesPrecessed) + " ms");
        }

    }

    private static void usage() {
        System.out.println("Usage:\n"
                + "index cranfield/ src/main/java/pt/ua/deti/ir/Stopwords/stopwords.txt english 3 index.idx complex\n"
                + "or\nsearch index.idx explicit false true\n"
                + "or\nsearch index.idx implicit false true\n"
                + "or\nsearch index.idx <explicit or implicit> ture false");
    }

    private static void saveSentecesInFile(String fname, List<String> sentences) {

        try {

            FileOutputStream outstream = new FileOutputStream(fname);
            Writer output = new OutputStreamWriter(outstream);
            BufferedWriter bufWriter = new BufferedWriter(output);

            sentences.forEach(snt -> {
                try {
                    bufWriter.write(snt + "\n");
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(Assignment04.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            System.err.println("Sentence savev in " + fname);

        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(Assignment04.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void saveinFile(String fname, TreeSet<QueryResult> qresult, boolean firstline, String type, String whatToDo) {
        try {
            FileOutputStream outstream = new FileOutputStream(fname, true);
            Writer output = new OutputStreamWriter(outstream);
            output = new BufferedWriter(output);

            String print;
            if (firstline) {
                if(whatToDo.equalsIgnoreCase("true"))
                    print = "query expansion\n";
                else
                    print = "rocchio feedback relevance "+type+"\n";
                print += "query_id\tdoc_id\t\tdoc_score_\n";
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
            java.util.logging.Logger.getLogger(Assignment04.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Assignment04.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
