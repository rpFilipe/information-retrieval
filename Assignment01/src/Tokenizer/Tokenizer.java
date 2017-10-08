package Tokenizer;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Joana Conde 
 * @author Ricardo Filipe
 */
public class Tokenizer {

    /**
     * Construtor do tokenizer
     */
    public Tokenizer() {
    }

    public List contentProcessor(String sInput) {

        List<String> list = new ArrayList<>(Arrays.asList(sInput.split(" +")));

        return list;
    }
}
