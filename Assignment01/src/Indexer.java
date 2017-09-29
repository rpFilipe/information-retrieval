
import Structures.Posting;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joana
 */
public class Indexer {

    private Map<String, List<Posting>> map;

    public Indexer() {
        this.map = new TreeMap<>();
    }

    public void addTerm(String keyTerm, Posting p) {
        List<Posting> pListLoad = new LinkedList<>();
        if (map.containsKey(keyTerm)) {
            pListLoad = map.get(keyTerm);
            for (Posting pload : pListLoad) {
                if (pload.getDocId() == p.getDocId()) {
                    pload.setFrequency(pload.getFrequency() + 1);
                    map.put(keyTerm, pListLoad);
                } else {
                    if (pListLoad.add(new Posting(p.getDocId()))) {
                        //Collections.sort(pListLoad, (Posting lhs, Posting rhs) -> lhs.getDocId() > rhs.getDocId() ? -1 : (lhs.getDocId() < rhs.getDocId()) ? 1 : 0 );
                        Collections.sort(pListLoad, (Posting lhs, Posting rhs) -> lhs.compareTo(rhs));
                        map.put(keyTerm, pListLoad);
                    } else {
                        System.err.println("Error!");
                        return;
                    }
                }
            }
        } else {
            if (pListLoad.add(new Posting(p.getDocId()))) {
                map.put(keyTerm, pListLoad);
            } else {
                System.err.println("Error!");
            }

        }
    }

    @Override
    public String toString() {
        String print = "";
        for (Map.Entry<String, List<Posting>> entry : map.entrySet()) {
            List<Posting> tmp = entry.getValue();
            print = print + "Key: " + entry.getKey();
            for (Posting p : tmp) {
                print = print + "\t\tDocId: " + p.getDocId() + "\tfrequency: " + p.getFrequency() + "\n";
            }
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

