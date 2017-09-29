
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 *
 * @author joana
 * 
 * A tokenizer class that returns the tokens of an input text paying particular
 * attention to characters that need special handling ('.'; ','; '-'; etc.).
 */
public class ComplexTokenizer extends Tokenizer{
    
    public ComplexTokenizer() {
    }
    
    @Override
    public List contentProcessor(String sInput) {
        
        // tratar a string quanto aos caracteres especiais

        List<String> list = new ArrayList<>(Arrays.asList(sInput.split(" +")));

        return list;
    }
}
