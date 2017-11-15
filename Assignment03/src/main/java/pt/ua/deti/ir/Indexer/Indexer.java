package pt.ua.deti.ir.Indexer;


import pt.ua.deti.ir.Structures.Posting;
import pt.ua.deti.ir.Tokenizer.ComplexTokenizer;
import pt.ua.deti.ir.Tokenizer.SimpleTokenizer;
import pt.ua.deti.ir.Tokenizer.Tokenizer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import static java.util.Comparator.comparingInt;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toMap;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Joana Conde 
 * @author Ricardo Filipe
 */
public class Indexer {

    private HashMap<String, LinkedList<Posting>> map;
    private Tokenizer tk;
    private int corpusSize;

    public Indexer() {
        this.map = new HashMap<>();
        this.corpusSize = 0;
    }
    
    public Indexer (String fname) throws FileNotFoundException{

        File fileidx = new File(fname);
        Scanner fsc = new Scanner(fileidx);
        this.map = new HashMap<>();

        String line;
        String[] tokens;
        String term;
        LinkedList<Posting> l;

        String tkData = fsc.nextLine();
        String parsetk = tkData.split(", ")[0];
        if(parsetk.equalsIgnoreCase("complex")){
            String swPath = tkData.split(", ")[1];
            String stLanguage = tkData.split(", ")[2];
            int minlength = Integer.parseInt(tkData.split(", ")[3]);
            this.corpusSize = Integer.parseInt(tkData.split(", ")[4]);
            tk = new ComplexTokenizer(swPath,stLanguage,minlength);
        }
        else if(parsetk.equalsIgnoreCase("simple")){
            int minlength = Integer.parseInt(tkData.split(", ")[1]);
            tk = new SimpleTokenizer(minlength);
        }
        else{
            System.out.println("ERRO! Tokenizer not recognized!");
            System.exit(-1);
        }

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
    
     public void indexTerms(int docId, List tokens) {
         
         this.corpusSize++;
        
        Map<String, Long> result = (Map<String, Long>) tokens.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        
        Map<String, Double> res = result.entrySet().stream()
                .collect(toMap(Entry::getKey, e -> 1 + Math.log10(e.getValue())));
        
        double norm = normalizeDoc(res.values());
        
        res = res.entrySet().stream()
                .collect(toMap(Entry::getKey, e -> e.getValue()/norm));
        
        
        for (Map.Entry<String, Double> entry : res.entrySet()) {
            String key = entry.getKey();
             
            LinkedList postings = map.get(key);
            
            // First time the term appears
            if(postings == null){
                postings = new LinkedList();
                postings.add(new Posting(docId, entry.getValue()));  
                map.put(key, postings);
            }
            else{
               postings.add(new Posting(docId, entry.getValue()));
               map.put(key, postings);
            }
            
        }
     }
     
     public double normalizeDoc(Collection c){
         double norm = 0;
         
         for (Object object : c) {
             double tmp = (double) object;
             norm += tmp * tmp;
         }
         
         return Math.sqrt(norm);
     }
        
    
    public void indexDoc(int docId, List tokens) {
        
        this.corpusSize++;
        
        Map<String, Long> result = (Map<String, Long>) tokens.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        
        result.entrySet().forEach((entry) -> {
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
        });
        
    }

    
    public Tokenizer getTk() {
        return tk;
    }
    
    public void setTokenizer(Tokenizer tk){
        this.tk = tk;
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
    public List getTermHigherFreq(){
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
            
            output.write(tk.toString() +  ", " + this.corpusSize + "\n");
            
            //String print = 
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
    
    public int getCorpusSize(){
        return this.corpusSize;
    }
}