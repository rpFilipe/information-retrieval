package Structures;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Joana Conde 
 * @author Ricardo Filipe
 */
public class Posting implements Comparable{
    
    
    private int docId;
    private int frequency;

    public Posting(int docId) {
        this.docId = docId;
        this.frequency = 1;
    }

    public Posting(int docId, Integer frequency) {
        this.docId = docId;
        this.frequency = frequency;
    }

    public int getDocId() {
        return docId;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int compareTo(Object o) {

        Posting p = (Posting) o;

        if(this.docId < p.docId)
            return -1;

        if(this.docId > p.docId)
            return 1;

        return 0;
    }
}
