
package pt.ua.deti.ir.Retriever;

import pt.ua.deti.ir.Indexer.Indexer;
import pt.ua.deti.ir.Structures.Posting;
import pt.ua.deti.ir.Structures.QueryResult;
import pt.ua.deti.ir.Tokenizer.Tokenizer;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author rpfilipe
 */
public class RankedRetriever {

    private Tokenizer tk;
    private Indexer idx;
    private static int queryId;

    public RankedRetriever(Indexer idx) {
        this.idx = idx;
        this.tk = idx.getTk();
        queryId = 0;
    }

    public TreeSet<QueryResult> search(String query) {
        
        double[] scores = new double[idx.getCorpusSize()];   
        queryId++;
        List<String> lquery = tk.contentProcessor(query);
        int querySize = lquery.size();
        //System.out.println("query size: " + querySize);
        
        Map<String, Long> queryTokens = (Map<String, Long>) lquery.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        
        queryTokens.entrySet().forEach(entry -> {
           
            List<Posting> p;
            
            if((p = idx.getList(entry.getKey())) != null){
                
                int tfreq = entry.getValue().intValue();
                double qweight = (1 + Math.log10(tfreq)) * Math.log10(querySize/tfreq); // Wt,q
                
                //System.out.println("tf: " + (1 + Math.log10(tfreq)));
                //System.out.println("idf: " + (Math.log10(tfreq/querySize)));

                //System.out.println("qweight: " + qweight);
                int dft = p.size();
                
                p.forEach((posting) -> {
                    double dweight = posting.getTermWeigth() * Math.log10(1400/dft);
                    scores[posting.getDocId()-1] += qweight * dweight;
                });
            }
        });

        TreeSet<QueryResult> queryResults = new TreeSet();
        
        int i = -1;
        for (double sc : scores) {
            i++;
            if (sc == 0)
                continue;
            queryResults.add(new QueryResult(queryId, i, sc));
        }

        return queryResults;
    }

}
