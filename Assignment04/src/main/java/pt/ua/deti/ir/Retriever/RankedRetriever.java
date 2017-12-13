package pt.ua.deti.ir.Retriever;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import pt.ua.deti.ir.Indexer.Indexer;
import pt.ua.deti.ir.Structures.Posting;
import pt.ua.deti.ir.Structures.QueryResult;
import pt.ua.deti.ir.Tokenizer.Tokenizer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toMap;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import pt.ua.ir.deti.FeedBack.RocchioFeedBack;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação
 *
 * @author Joana Conde
 * @author Ricardo Filipe
 */
public class RankedRetriever {

    private Tokenizer tk;
    private Indexer idx;
    private RocchioFeedBack rfb;
    private Word2Vec vec;
    private static int queryId;
    private final int nSimilar;

    public RankedRetriever(Indexer idx, String sentences) throws FileNotFoundException {
        this.idx = idx;
        this.rfb = new RocchioFeedBack("docCache.idx", "cranfield.query.relevance.txt", 1.0, 1.0, 0.25, idx.getCorpusSize());
        this.tk = idx.getTk();
        trainModel(sentences);
        queryId = 0;
        nSimilar = 3;
    }

    public TreeSet<QueryResult> search(String query, String type, boolean feedback) {

        double[] scores = new double[idx.getCorpusSize()];
        double[] scoreWfeedback = new double[idx.getCorpusSize()];
        queryId++;
        List<String> lquery = tk.contentProcessor(query);

        List<String> raw_query = Arrays.asList(query.split(" "));
        raw_query = expandQuery(raw_query);

        query = "";
        for (String s : raw_query) {
            query = query + " " + s;
        }

        Map<String, Long> queryTokens = (Map<String, Long>) lquery.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<String, Double> res = queryTokens.entrySet().stream()
                .filter(e -> idx.getList(e.getKey()) != null)
                .collect(toMap(Entry::getKey, e -> (1 + Math.log10(e.getValue())) * Math.log10((idx.getCorpusSize() / idx.getList(e.getKey()).size()))));

        double qnorm = idx.normalizeDoc(res.values());

        res = res.entrySet().stream()
                .collect(toMap(Entry::getKey, e -> e.getValue() / qnorm));

        //TODO rocchio feedback
        Map<String, Double> queryVector = res;

        //System.out.println("res: " + res.toString());

        res.entrySet().forEach(entry -> {

            List<Posting> p;

            if ((p = idx.getList(entry.getKey())) != null) {
                p.forEach((posting) -> {
                    double dweight = posting.getTermWeigth();
                    double qweight = entry.getValue();
                    scores[posting.getDocId() - 1] += qweight * dweight;
                });
            }
        });

        TreeSet<QueryResult> queryResults = new TreeSet();

        int i = 0;
        for (double sc : scores) {
            i++;
            if (sc == 0) {
                continue;
            }
            queryResults.add(new QueryResult(queryId, i, sc));
        }

        if (feedback) {
            queryResults = queryResults.stream()
                .limit(10)
                .collect(Collectors.toCollection(TreeSet<QueryResult>::new));
            
            queryVector = rfb.computeFeedBack(type, queryId, queryVector, queryResults);
            //System.out.println("Size after feedback: "+ queryVector.size());
            
            queryVector.entrySet().forEach(entry -> {

                List<Posting> p;

                if ((p = idx.getList(entry.getKey())) != null) {
                    p.forEach((posting) -> {
                        double dweight = posting.getTermWeigth();
                        double qweight = entry.getValue();
                        scoreWfeedback[posting.getDocId() - 1] += qweight * dweight;
                    });
                }
            });

            queryResults = new TreeSet();

            i = 0;
            for (double sc : scoreWfeedback) {
                i++;
                if (sc == 0) {
                    continue;
                }
                queryResults.add(new QueryResult(queryId, i, sc));
            }
        }

        return queryResults;
    }

    public void print(Map<String, Double> res) {
        res.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " - " + entry.getValue());

        });
    }

    private void trainModel(String sentences) throws FileNotFoundException {
        // Gets Path to Text file
        String filePath = new ClassPathResource(sentences).getFile().getAbsolutePath();

        // Strip white space before and after for each line
        SentenceIterator iter = new BasicLineIterator(filePath);
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();

        /*
            CommonPreprocessor will apply the following regex to each token: [\d\.:,"'\(\)\[\]|/?!;]+
            So, effectively all numbers, punctuation symbols and some special symbols are stripped off.
            Additionally it forces lower case for all tokens.
         */
        t.setTokenPreProcessor(new CommonPreprocessor());

        System.out.println("Building model from " + sentences);
        vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .iterations(1)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();
        vec.fit();
    }

    private List<String> expandQuery(List<String> query) {
        List<String> expandedQuery = new LinkedList();
        List<String> lst;

        for (String term : query) {
            lst = new ArrayList(vec.wordsNearest("light", nSimilar));
            //System.out.println(lst);
            expandedQuery.addAll(lst);
        }

        // add initial elements
        expandedQuery.addAll(query);

        return expandedQuery;
    }
}
