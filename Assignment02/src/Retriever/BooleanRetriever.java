
package Retriever;

import Indexer.Indexer;
import Structures.Posting;
import Structures.QueryResult;
import Tokenizer.Tokenizer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.summingInt;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Joana Conde 
 * @author Ricardo Filipe
 */
public class BooleanRetriever {
    
    private Tokenizer tk;
    private Indexer idx;
    private static int queryId;
    
    public BooleanRetriever(Tokenizer tk, Indexer idx){
        this.tk = tk;
        this.idx = idx; 
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
                .orElseGet(Collections::emptyList).stream().filter(( m )-> m != null)
                .collect(Collectors.groupingBy(Posting::getDocId, Collectors.counting()));
            
            qresults.entrySet().forEach((entry) -> {
                queryResults.add(new QueryResult(queryId, entry.getKey(), entry.getValue().intValue()));
            });
            
        }
        
        else if( score == 'b')
        {
           Map<Integer, Integer> qresults = (Map<Integer, Integer>) lconcat.stream()
                .collect(Collectors.groupingBy(Posting::getDocId, summingInt((Posting::getFrequency))));
           
           qresults.entrySet().forEach((entry) -> {
                queryResults.add(new QueryResult(queryId, entry.getKey(), entry.getValue()));
            });
        }
        
        return queryResults;
    }
}
