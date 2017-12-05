package pt.ua.deti.ir.Structures;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Joana Conde 
 * @author Ricardo Filipe
 */
public class Posting implements Comparable{
    
    
    private int docId;
    private double term_weight;

    public Posting(int docId) {
        this.docId = docId;
        this.term_weight = 0;
    }
    
    public Posting(String s){
        String[] p = s.split(":");
        this.docId = Integer.parseInt(p[0]);
        this.term_weight = Double.parseDouble(p[1]);
    }

    public Posting(int docId, double term_weight) {
        this.docId = docId;
        this.term_weight = term_weight;
    }

    public int getDocId() {
        return docId;
    }

    public double getTermWeigth() {
        return term_weight;
    }

    public void setTermWeigth(double term_weight) {
        this.term_weight = term_weight;
    }

    public int compareTo(Object o) {

        Posting p = (Posting) o;

        if(this.docId < p.docId)
            return -1;

        if(this.docId > p.docId)
            return 1;

        return 0;
    }

    @Override
    public String toString() {
        return "" + docId + ":" + term_weight;
    }
    
    
}