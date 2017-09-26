
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author joana
 */
public class Stopwords {

    List<String> stopWords;
    final static Charset ENCODING = StandardCharsets.UTF_8;

    public Stopwords(String cLocation) throws IOException {
        Path path = Paths.get(cLocation);
        stopWords = Files.readAllLines(path, ENCODING);
        //System.out.println("List of stop words: " + stopWords.toString());
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
