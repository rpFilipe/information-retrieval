
package pt.ua.deti.ir.Structures;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Joana Conde 
 * @author Ricardo Filipe
 */
public class StringPosting implements Comparable{
    
    
    private String term;
    private double term_weight;
    
    public StringPosting(String s){
        String[] p = s.split(":");
        this.term = p[0];
        this.term_weight = Double.parseDouble(p[1]);
    }

    public StringPosting(String term, double term_weight) {
        this.term = term;
        this.term_weight = term_weight;
    }

    public String getTerm() {
        return term;
    }

    public double getTermWeigth() {
        return term_weight;
    }

    public void setTermWeigth(double term_weight) {
        this.term_weight = term_weight;
    }

    @Override
    public int compareTo(Object o) {

        StringPosting p = (StringPosting) o;

        return (this.term.compareToIgnoreCase(p.term));
    }

    @Override
    public String toString() {
        return "" + term + ":" + term_weight;
    }
    
    
}
