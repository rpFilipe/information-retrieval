/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.function.Function;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.summingDouble;

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
        
        double[] scores = new double[1400];   
        queryId++;
        List<String> lquery = tk.contentProcessor(query);
        int querySize = lquery.size();
        
        Map<String, Long> queryTokens = (Map<String, Long>) lquery.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        
        queryTokens.entrySet().forEach(entry -> {
           
            List<Posting> p;
            
            if((p = idx.getList(entry.getKey())) != null){
                
                int tfreq = entry.getValue().intValue();
                double qweight = (1 + Math.log10(tfreq)) * Math.log10(tfreq/querySize); // Wt,q
                int dft = p.size();
                
                p.forEach((posting) -> {
                    double dweight = posting.getTermWeigth() * Math.log10(dft/1400);
                    scores[posting.getDocId()] += qweight * dweight;
                });
            }
        });

        TreeSet<QueryResult> queryResults = new TreeSet();
        
        int i = 1;
        for (double sc : scores) {
            queryResults.add(new QueryResult(queryId, i, sc));
            i++;
        }

        return queryResults;
    }

}
