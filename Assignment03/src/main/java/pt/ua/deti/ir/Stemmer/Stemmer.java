package pt.ua.deti.ir.Stemmer;
import org.tartarus.snowball.SnowballStemmer;


/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Joana Conde 
 * @author Ricardo Filipe
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
    public String stem(String token){
        Class stemClass;
        try {
            stemClass = Class.forName("org.tartarus.snowball.ext." + language +"Stemmer");
            //System.out.println(stemClass.toString());
            SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();
            stemmer.setCurrent(token);
            if (stemmer.stem()){
                return stemmer.getCurrent();
            }
                            
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            System.out.println(ex);
        }   
        
        return token;
    } 
}
