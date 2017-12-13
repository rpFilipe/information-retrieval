package pt.ua.deti.ir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import pt.ua.deti.ir.CorpusReder.CorpusReader;
import pt.ua.deti.ir.Indexer.Indexer;
import pt.ua.deti.ir.Retriever.RankedRetriever;
import pt.ua.deti.ir.Structures.Document;
import pt.ua.deti.ir.Structures.QueryResult;
import pt.ua.deti.ir.Tokenizer.ComplexTokenizer;
import pt.ua.deti.ir.Tokenizer.Tokenizer;
import pt.ua.ir.deti.FeedBack.RocchioFeedBack;

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

        } else if(args[0].equalsIgnoreCase("query_expansion")){
            
            //Rocchio feedback
            Indexer idx = new Indexer(args[1]);
            RankedRetriever rr = new RankedRetriever(idx, "cranfield_sentences.txt");
            Map<String,Double> qresult;
            File fqueries = new File("cranfield.queries.txt");
            Scanner in = new Scanner(fqueries);

            while (in.hasNextLine()) {
                String line = in.nextLine();
                qresult = rr.search(line, "explicit");

            }
            in.close();
            
            
            
            //Query expansion
            
            Word2Vec vec = new Word2Vec();
            log.info("Writing word vectors to text file....");

            // Prints out the closest 10 words to "day". An example on what to do with these Word Vectors.
            log.info("Closest Words:");
            Collection<String> lst = vec.wordsNearest("day", 10);
            System.out.println("10 Words closest to 'day': " + lst);
        }

    }

    private static void usage() {
        System.out.println("Usage:\n"
                + "index cranfield/ src/main/java/pt/ua/deti/ir/Stopwords/stopwords.txt english 3 index.idx complex\n"
                + "or\nsearch index.idx cranfield.queries.txt <optional limit>\n"
                + "or\nsearch2 index_complex.txt cranfield.queries.txt a <optional limit>\n"
                + "or\nsearch2 index_complex.txt cranfield.queries.txt b <optional limit>");
    }

    private static void saveSentecesInFile(String fname, List<String> sentences) {

        try {

            FileOutputStream outstream = new FileOutputStream(fname);
            Writer output = new OutputStreamWriter(outstream);
            BufferedWriter bufWriter = new BufferedWriter(output);

            sentences.forEach(snt -> {
                try {
                    bufWriter.write(snt+"\n");
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(Assignment04.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            System.err.println("Sentence savev in " + fname);

        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(Assignment04.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
