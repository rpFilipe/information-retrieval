
import Stemmer.org.tartarus.snowball.SnowballStemmer;


/**
 *
 * @author joana
 */
public class Stemmer {
    /**
     * Stemmer language algorithm.
     */
    private final String language;
    
    /**
     * Construtor do Stemmer que recebe a linguagem a ser usado para o stemming dos termos
     * @param language
     */
    public Stemmer(String language){
        this.language = language;  
    }
    
    /**
     * metodo que retorna o termo ja tokenizado
     * @param token
     * @return token
     */
    public String getStemmer(String token){
        Class stemClass;
        try {
            stemClass = Class.forName("Stemmer.org.tartarus.snowball.ext." + language +"Stemmer");
            //System.out.println(stemClass.toString());
            SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();
            stemmer.setCurrent(token);
            if (stemmer.stem()){
                return stemmer.getCurrent();
            }
                
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {}
        return token;
    } 
}
