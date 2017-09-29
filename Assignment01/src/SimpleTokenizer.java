
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author joanaconde 
 * 
 * A simple tokenizer that splits on whitespace, lowercases
 * tokens, removes all non- alphabetic characters, and keeps only terms with 3
 * or more characters.
 */
public class SimpleTokenizer extends Tokenizer {

    public SimpleTokenizer() {
    }

    /**
     * @param sInput
     * @return 
     */
    
    @Override
    public List contentProcessor(String sInput) {
       sInput = sInput.toLowerCase().replaceAll("[^A-Za-z0-9]", " ");

        List<String> list = new ArrayList<>(Arrays.asList(sInput.split(" +")));
        List<String> tmp = new ArrayList<>();
        list.stream().filter((s) -> (s.length() < 3)).forEachOrdered((s) -> {
            tmp.add(s);
        });
        
        list.removeAll(tmp);
    }
}
