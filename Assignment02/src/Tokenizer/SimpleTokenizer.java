package Tokenizer;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Joana Conde 
 * @author Ricardo Filipe
 *
 * 
 * 
 * A simple tokenizer that splits on whitespace, lowercases
 * tokens, removes all non- alphabetic characters, and keeps only terms with 3
 * or more characters.
 */
public class SimpleTokenizer implements Tokenizer {
    
    private int minTokenLenght;

    public SimpleTokenizer(int minTokenLenght) {
        this.minTokenLenght = minTokenLenght;
    }

    /**
     * @param sInput
     * @return 
     */
    
    @Override
    public List contentProcessor(String sInput) {
       //sInput = sInput.toLowerCase();

        List<String> list = new ArrayList<>(Arrays.asList(sInput.split("[^A-Za-z]")));
        List<String> tmp = new ArrayList<>();
        list.stream()
                .filter((s) -> (s.length() >= minTokenLenght))
                .map(s -> s.toLowerCase())
                .forEachOrdered((s) -> {
            tmp.add(s);
        });
        
        //list.removeAll(tmp);
        return tmp;
    }
}
