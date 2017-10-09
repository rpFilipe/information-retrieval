package Tokenizer;


import Stopwords.Stopwords;
import Stemmer.Stemmer;
import com.sun.xml.internal.ws.util.StringUtils;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Joana Conde 
 * @author Ricardo Filipe
 *
 * 
 * 
 * A tokenizer class that returns the tokens of an input text paying particular
 * attention to characters that need special handling ('.'; ','; '-'; etc.).
 */
public class ComplexTokenizer extends Tokenizer{
    
    private Stopwords stp;
    private Stemmer stm;
    private int minTokenLenght;
    private List<String> createdTokens;
    private Pattern pattern;
    // email regex
    private String regexMail = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";
    //private String regexWord = "([\\D])";

    
    public ComplexTokenizer(String stopWords, String stemmer, int minTokenLenght) {
        stp = new Stopwords(stopWords);
        stm = new Stemmer(stemmer);
        this.minTokenLenght = minTokenLenght;
        this.pattern = Pattern.compile(regexMail);
    }
    
    @Override
    public List contentProcessor(String sInput) {
        
        // tratar a string quanto aos caracteres especiais
        List<String> list = new ArrayList<>(Arrays.asList(sInput.split("[\\s]")));
         
        createdTokens = new ArrayList<>();
        list = list.stream()
                .filter(s-> s.length() >= minTokenLenght)
                .filter(s-> !stp.isStopWord(s))
                .map(s -> s.toLowerCase())
                .map(s -> specialCasesConverter(s))
                .filter(s -> s != null)
                .map(s-> stm.stem(s))
                .collect(Collectors.toList());
        
        createdTokens = createdTokens.stream()
                .filter(s -> s.length() >= minTokenLenght)
                .map(s -> stm.stem(s))
                .collect(Collectors.toList());
        
        return Stream.concat(list.stream(), createdTokens.stream()).collect(Collectors.toList());
    }
    
    private String specialCasesConverter(String s) {
        
            // ignoring all braces, +, /, ='
        s = s.replaceAll("[\\[\\](){}',/\\+=]","");
        
        // check if token is a number
        try{
            double d = Double.parseDouble(s);
            return s;
        }catch(NumberFormatException e){
            // do nothing
        }
        
        
        
        // check if is a monogram
        String monogram = "";
        String[] initials = s.split("\\.");
        for (String initial : initials) {
            if(initial.isEmpty())
                break;
            if(initial.length() == 1 && Character.isLetter(initial.charAt(0)))
                monogram = monogram + initial;
        }
        
        if(monogram.length() == initials.length){
            return monogram;
        }
            
        
        // check if is email
        Matcher matcher = pattern.matcher(s);
        if (matcher.matches()){
            String[] parts = s.split("@");
            // adding the provider
            createdTokens.add(parts[0].split(".")[0]);
            //returning the user
            return parts[0];
        }
        
        // adding words seperated by hyphen (remove all digits)
        if(s.contains("-")){
            String[] hyphenWords = s.split("-");
            ArrayList<String> hyphenTokens = new ArrayList<>(Arrays.asList(hyphenWords));
            
            hyphenTokens.stream()
                    .map(t -> t.replaceAll("^[a-zA-Z]", ""))
                    .filter(t -> t.length() > 0)
                    .collect(Collectors.toList());
            
            createdTokens.addAll(hyphenTokens);
            return s;
        }
        
        // only pass words
        if(s.matches("[a-zA-Z]+"))
            return s;
        return null;
    }
}
