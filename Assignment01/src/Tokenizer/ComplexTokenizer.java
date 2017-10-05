package Tokenizer;


import Stopwords.Stopwords;
import Stemmer.Stemmer;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


/**
 *
 * @author joana
 * 
 * A tokenizer class that returns the tokens of an input text paying particular
 * attention to characters that need special handling ('.'; ','; '-'; etc.).
 */
public class ComplexTokenizer extends Tokenizer{
    
    private Stopwords stp;
    private Stemmer stm;
    private int minTokenLenght;
    
    public ComplexTokenizer(String stopWords, String stemmer, int minTokenLenght) {
        stp = new Stopwords(stopWords);
        stm = new Stemmer(stemmer);
        this.minTokenLenght = minTokenLenght;
    }
    
    @Override
    public List contentProcessor(String sInput) {
        
        // tratar a string quanto aos caracteres especiais
        List<String> list = new ArrayList<>(Arrays.asList(sInput.split("[^A-Za-z]")));
        
        list = list.stream()
                .filter(s-> s.length() >= minTokenLenght)
                .filter(s-> !stp.isStopWord(s))
                .map(s-> stm.stem(s))
                .collect(Collectors.toList());
        return list;
    }
}
