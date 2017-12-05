package Stopwords;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Joana Conde 
 * @author Ricardo Filipe
 */
public class Stopwords {

    TreeSet<String> stopWords;
    final static Charset ENCODING = StandardCharsets.UTF_8;

    public Stopwords(String cLocation) {
        try {
            Path path = Paths.get(cLocation);
            stopWords = new TreeSet(Files.readAllLines(path, ENCODING));
            //System.out.println("List of stop words: " + stopWords.toString());
        } catch (IOException ex) {
            Logger.getLogger(Stopwords.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * metodo que verifica se um termo (token) é uma stopword
     *
     * @param token
     * @return
     */
    public boolean isStopWord(String token) {
        return stopWords.contains(token);
    }
}
