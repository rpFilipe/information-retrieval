
import Structures.Posting;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author joana
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

    @Override
    public String toString() {
        String print = "";
        for (Map.Entry<String, LinkedList<Posting>> entry : map.entrySet()) {
            LinkedList<Posting> tmp = entry.getValue();
            print = print + entry.getKey() + " -> ";
            for (Posting p : tmp) {
                print = print + p.getDocId() + ":" + p.getFrequency() + ",";
            }
            print += "\n";
        }

        return print;
    }

    public void saveToFile(String filename) {
        OutputStream outstream;
        try {
            outstream = new FileOutputStream(filename);
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

