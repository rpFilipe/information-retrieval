package Indexer;


import Structures.Posting;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import static java.util.Comparator.comparingInt;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static java.util.Map.Entry.comparingByValue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Joana Conde 
 * @author Ricardo Filipe
 */
public class Indexer {

    private HashMap<String, LinkedList<Posting>> map;

    public Indexer() {
        this.map = new HashMap<>();
    }
    
    public void indexDoc(int docId, List tokens) {
        
        Map<String, Long> result = (Map<String, Long>) tokens.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        
        for (Map.Entry<String, Long> entry : result.entrySet()) {
            String key = entry.getKey();
            int freq =entry.getValue().intValue();
            
            LinkedList postings = map.get(key);
            
            // First time the term appears
            if(postings == null){
                postings = new LinkedList();
                postings.add(new Posting(docId, freq));
                map.put(key, postings);
            }
            else{
               postings.add(new Posting(docId, freq));
               map.put(key, postings);
            }
            
        
            
        }
        
    }
    
    /**
     * metodo para saber o tamanho do vocabulario
     * @return 
     */
    public int getVocabularySize(){
        Set entries = map.entrySet();
        return entries.size();
    }
    
    /**
     * metodo para listar os 10 primeiros termos (por ordem alfabetica) que aparecem em apenas um documento
     * @return list
     */
    public List getTermInOneDoc(){
        List<String> list = map.entrySet()
                     .stream()
                     .filter(i-> i.getValue().size() == 1)
                     .map(Map.Entry::getKey)
                     .sorted()
                     .collect(Collectors.toList());
        
        return list.subList(0, 10);
    }
    
    /**
     * metodo para listar os 10 primeiros termos com maior frequencia em documentos
     * @return list 
     */
    public List getTermHigherFreq(){;
        List<String> list = map.entrySet()
                     .stream()
                     .sorted(Map.Entry.comparingByValue(Collections.reverseOrder(comparingInt((list1) -> list1.size())))) //comparingInt(List::size)))
                     .map(Map.Entry::getKey)
                     .collect(Collectors.toList());
        
        list = list.subList(0, 10);
        
        return list.subList(0, 10);
    }

    @Override
    public String toString() {
        String print = "";
        TreeSet<String> orderd_tokens = new TreeSet(map.keySet());
        for (String s : orderd_tokens) {
            LinkedList<Posting> tmp = map.get(s);
            print = print + s;
            for (Posting p : tmp) {
                print = print +"," + p.getDocId() + ":" + p.getFrequency();
            }
            print += "\n";
        }

        return print;
    }

    public void saveToFile(String filename) {
        OutputStream outstream;
        try {
            outstream = new FileOutputStream("output/"+filename);
            Writer output = new OutputStreamWriter(outstream);
            output = new BufferedWriter(output);
            output.write(toString());
            output.flush();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}

