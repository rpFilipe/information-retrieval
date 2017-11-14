
package pt.ua.deti.ir.Retriever;

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
        
        //added
       Map<String, Double> res = queryTokens.entrySet().stream()
               .filter(e -> idx.getList(e.getKey()) != null) 
               .collect(toMap(Entry::getKey, e -> (      
                        1 + Math.log10(e.getValue().intValue())) * Math.log10(idx.getCorpusSize()/idx.getList(e.getKey()).size())));  
        
        double qnorm = idx.normalizeDoc(res.values());
        
        res = res.entrySet().stream()
                .collect(toMap(Entry::getKey, e -> e.getValue()/qnorm));
        
        queryTokens.entrySet().forEach(entry -> {
           
            List<Posting> p;
            
            if((p = idx.getList(entry.getKey())) != null){

                p.forEach((posting) -> {
                    double dweight = posting.getTermWeigth();
                    double qweight = entry.getValue();
                    scores[posting.getDocId()-1] += qweight * dweight;
                });
            }
        });
        
        /*queryTokens.entrySet().forEach(entry -> {
           
            List<Posting> p;
            
            if((p = idx.getList(entry.getKey())) != null){
                
                int tfreq = entry.getValue().intValue();
                double qweight = (1 + Math.log10(tfreq)) * Math.log10(querySize/tfreq); // Wt,q
                
                //System.out.println("tf: " + (1 + Math.log10(tfreq)));
                //System.out.println("idf: " + (Math.log10(tfreq/querySize)));

                //System.out.println("qweight: " + qweight);
                
                //TODO: falta normalizar a query!!
                int dft = p.size();
                
                p.forEach((posting) -> {
                    double dweight = posting.getTermWeigth()* Math.log10(1400/dft);
                    scores[posting.getDocId()-1] += qweight * dweight;
                });
            }
        });
        */
        
        TreeSet<QueryResult> queryResults = new TreeSet();
        
        int i = -1;
        for (double sc : scores) {
            i++;
            if (sc == 0)
                continue;
            queryResults.add(new QueryResult(queryId, i, Math.round(sc * 10000.0) / 10000.0));   // round to 4 decimal places
        }

        return queryResults;
    }

}
