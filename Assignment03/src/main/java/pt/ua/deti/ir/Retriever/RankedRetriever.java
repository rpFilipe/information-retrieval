
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
    private static long queryThroughput;

    public RankedRetriever(Indexer idx) {
        this.idx = idx;
        this.tk = idx.getTk();
        queryId = 0;
        queryThroughput = 0;
    }

    public TreeSet<QueryResult> search(String query) {
        //long startTime = System.currentTimeMillis();
        long startTime = System.nanoTime();
        //System.out.println("Start: "+startTime);
        
        double[] scores = new double[idx.getCorpusSize()];   
        queryId++;
        List<String> lquery = tk.contentProcessor(query);

        Map<String, Long> queryTokens = (Map<String, Long>) lquery.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        
        
        //added
        Map<String, Double> res = queryTokens.entrySet().stream()
               .filter(e -> idx.getList(e.getKey()) != null)  
               .collect(toMap(Entry::getKey, e -> (      
                        1 + Math.log10(e.getValue())) * Math.log10((idx.getCorpusSize()/idx.getList(e.getKey()).size()))));    
       
        double qnorm = idx.normalizeDoc(res.values());
       
        res = res.entrySet().stream()
                .collect(toMap(Entry::getKey, e -> e.getValue()/qnorm));
        
        res.entrySet().forEach(entry -> {
           
            List<Posting> p;
            
            if((p = idx.getList(entry.getKey())) != null){
                
                p.forEach((posting) -> {
                    double dweight = posting.getTermWeigth();
                    double qweight = entry.getValue();
                    scores[posting.getDocId()-1] += qweight * dweight;
                });
            }
        });
        
        TreeSet<QueryResult> queryResults = new TreeSet();
        
        int i = 0;
        for (double sc : scores) {
            i++;
            if (sc == 0)
                continue;
            queryResults.add(new QueryResult(queryId, i, sc));   // round to 4 decimal places
        }

        //long stopTime = System.currentTimeMillis();
        long stopTime = System.nanoTime();
        //System.out.println("Stop: "+stopTime);
        queryThroughput = stopTime - startTime;
        return queryResults;
    }
    
    public TreeSet<QueryResult> search(String query, int limit) {
        
        TreeSet<QueryResult> result = search(query);
        
        result = result.stream()
                .limit(limit)
                .collect(Collectors.toCollection(TreeSet<QueryResult>::new));
        
        return result;
    } 
    
    public void print(Map<String, Double> res){
        res.entrySet().forEach( entry-> {
            System.out.println(entry.getKey() + " - " + entry.getValue());
       
        });
    }
    
    public long getQueryThroughput(){
        return queryThroughput/queryId;
    }

}
