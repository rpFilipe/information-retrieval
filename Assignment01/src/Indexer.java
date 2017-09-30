
import Structures.Posting;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joana
 */
public class Indexer {

    private Map<String, Set<Posting>> map;

    public Indexer() {
        this.map = new TreeMap<>();
    }

    public void addTerm(String keyTerm, Posting p) {
        Set<Posting> pListLoad = new TreeSet<>();
        if (map.containsKey(keyTerm)) {
            pListLoad = map.get(keyTerm);
            for (Posting pload : pListLoad) {
                if (pload.compareTo(p) == 0) { //   .getDocId() == p.getDocId()) {
                    pload.setFrequency(pload.getFrequency() + 1);
                    map.put(keyTerm, pListLoad);
                    break;
                } else {
                    if (pListLoad.add(p)) {
                        map.put(keyTerm, pListLoad);
                        break;
                    }
                }
            }
        } else {
            if (pListLoad.add(p)) {
                map.put(keyTerm, pListLoad);
            }
        }
    }

    @Override
    public String toString() {
        String print = "";
        for (Map.Entry<String, Set<Posting>> entry : map.entrySet()) {
            Set<Posting> tmp = entry.getValue();
            print = print + entry.getKey() + ",";
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

