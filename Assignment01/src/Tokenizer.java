
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author joanaconde
 */
public class Tokenizer {

    Stopwords stopwords;
    
    /**
     * Construtor do tokenizer
     */
    public Tokenizer(String cLocation) {
        try {
            stopwords = new Stopwords(cLocation);
        } catch (IOException ex) {
            Logger.getLogger(Tokenizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List contentProcessor(String sInput) {
        sInput = sInput.toLowerCase().replaceAll("/[^A-Za-z0-9]/", "");
        
        List<String> list = new ArrayList<>(Arrays.asList(sInput.split(" ")));
        for (Iterator<String> iterator = list.iterator(); iterator.hasNext(); ) {
            String s = iterator.next();
            if(stopwords.isStopWord(s)){
                iterator.remove();
            }
        }
        
        return list;
    }
}
