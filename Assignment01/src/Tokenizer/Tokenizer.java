package Tokenizer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joana
 */
public class Tokenizer {

    /**
     * Construtor do tokenizer
     */
    public Tokenizer() {
    }

    public List contentProcessor(String sInput) {

        List<String> list = new ArrayList<>(Arrays.asList(sInput.split(" ")));

        return list;
    }
}
