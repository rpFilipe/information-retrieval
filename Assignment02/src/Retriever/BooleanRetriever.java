
package Retriever;

import Indexer.Indexer;
import Stemmer.Stemmer;
import Stopwords.Stopwords;
import Structures.Posting;
import Tokenizer.ComplexTokenizer;
import Tokenizer.SimpleTokenizer;
import Tokenizer.Tokenizer;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.summingInt;

/**
 *
 * @author joana
 */
public class BooleanRetriever {
    
    private Tokenizer tk;
    Indexer idx;
    
    public BooleanRetriever(Tokenizer tk, Indexer idx){
        this.tk = tk;
        this.idx = idx; 
    }
    
    //retorna uma lista de uma classe queryresults 
    public void search(String query){
        List<String> lquery = tk.contentProcessor(query);
        List<Posting> lconcat = null, l = null;
        
        //System.out.println(lquery.toString());
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
        
        //System.out.println(lconcat.toString());

        //doc score1
        Map<Integer, Long> qresults1 = (Map<Integer, Long>) lconcat.stream()
                .collect(Collectors.groupingBy(Posting::getDocId, Collectors.counting()));
        
        //System.out.println(qresults1.toString());
        
        //doc score2
        Map<Integer, Integer> qresults2 = (Map<Integer, Integer>) lconcat.stream()
                .collect(Collectors.groupingBy(Posting::getDocId, summingInt(Posting::getFrequency)));
        
        //System.out.println(qresults2.toString());
        
    }
}
