
import Structures.Posting;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
                print = print + " DocId: " + p.getDocId() + " frequency: " + p.getFrequency() + "; ";
            }
        }

        return print;
    }
}

