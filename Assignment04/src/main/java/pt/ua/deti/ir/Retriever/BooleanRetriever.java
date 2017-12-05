
package pt.ua.deti.ir.Retriever;

import pt.ua.deti.ir.Indexer.Indexer;
import pt.ua.deti.ir.Structures.Posting;
import pt.ua.deti.ir.Structures.QueryResult;
import pt.ua.deti.ir.Tokenizer.Tokenizer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.summingDouble;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Joana Conde 
 * @author Ricardo Filipe
 */
public class BooleanRetriever {
    
    private Tokenizer tk;
    private Indexer idx;
    private static int queryId;
    
    public BooleanRetriever(Indexer idx){
        this.idx = idx; 
        this.tk = idx.getTk();
        queryId = 0;
    }
    
    public TreeSet<QueryResult> search(String query, char score){
        queryId++;
        List<String> lquery = tk.contentProcessor(query);
        List<Posting> lconcat = null, l = null;
        
        for(String s : lquery)
        {
            l = idx.getList(s);
            if(l != null && lconcat != null){
                lconcat.addAll(l);
            }
            else if(lconcat == null && l != null){
                lconcat = new LinkedList(l);
            }
        }
        
         TreeSet<QueryResult> queryResults = new TreeSet();
        
        //doc score1
        if( score == 'a')
        {
            Map<Integer, Long> qresults = (Map<Integer, Long>) Optional.ofNullable(lconcat)
                .orElseGet(Collections::emptyList).stream() //.filter(( m )-> m != null)
                .collect(Collectors.groupingBy(Posting::getDocId, Collectors.counting()));
            
            qresults.entrySet().forEach((entry) -> {
                queryResults.add(new QueryResult(queryId, entry.getKey(), entry.getValue().intValue()));
            });
            
        }
        
        else if( score == 'b')
        {
           Map<Integer, Double> qresults = (Map<Integer, Double>) Optional.ofNullable(lconcat)
                .orElseGet(Collections::emptyList).stream()
                .collect(Collectors.groupingBy(Posting::getDocId, summingDouble(Posting::getTermWeigth)));
           
           qresults.entrySet().forEach((entry) -> {
                queryResults.add(new QueryResult(queryId, entry.getKey(), entry.getValue()));
            });
        }
        
        return queryResults;
    }
    
    public TreeSet<QueryResult> search(String query, char score, int limit){
        TreeSet<QueryResult> result = search(query, score);
        
        result = result.stream()
                .limit(limit)
                .collect(Collectors.toCollection(TreeSet<QueryResult>::new));
        
        return result;
    }
}