package Indexer;


import Structures.Posting;
import java.io.BufferedWriter;
import java.io.File;
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
import java.util.Scanner;
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
    
    public Indexer (String fname) throws FileNotFoundException{

    File fileidx = new File(fname);
    Scanner fsc = new Scanner(fileidx);
    this.map = new HashMap<>();
    
    String line;
    String[] tokens;
    String term;
    LinkedList<Posting> l;

    while(fsc.hasNext()){
        line = fsc.nextLine();
        tokens = line.split(",");
        term = tokens[0];
        l = new LinkedList();
        for(int i = 1; i < tokens.length; i++) {
            l.add(new Posting(tokens[i]));
        }

        map.put(term, l);

    }
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
        
        return list.subList(0, 10);
    }

    /**
     * metodo que grava o vocabulario para o ficheiro, passado como argumento, na pasta output/filename
     * @param filename 
     */
    public void saveToFile(String filename) {
        OutputStream outstream;      
        
        try {
            outstream = new FileOutputStream(filename);
            Writer output = new OutputStreamWriter(outstream);
            output = new BufferedWriter(output);
            TreeSet<String> orderd_tokens = new TreeSet(map.keySet());
            
            for (String s : orderd_tokens) {
                String print = "";
                LinkedList<Posting> tmp = map.get(s);
                print = print + s;
                for (Posting p : tmp) {
                    print = print +"," + p;
                }
                print += "\n";
                output.write(print);
                output.flush();
        }
            
            output.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public List<Posting> getList(String query){
        if(map.containsKey(query))
            return map.get(query);
        return null;
    }
}

