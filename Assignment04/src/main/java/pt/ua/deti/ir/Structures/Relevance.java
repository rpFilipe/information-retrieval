
package pt.ua.deti.ir.Structures;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Joana Conde 
 * @author Ricardo Filipe
 */
public class Relevance implements Comparable<Relevance>{
    
    private int queryId, docId, relevance;

    public Relevance(int queryId, int docId, int relevance) {
        this.queryId = queryId;
        this.docId = docId;
        this.relevance = relevance;
    }

    public Relevance(int queryId, int docId) {
        this.queryId = queryId;
        this.docId = docId;
    }

    public int getQueryId() {
        return queryId;
    }

    public int getDocId() {
        return docId;
    }

    public int getRelevance() {
        return relevance;
    }
    
    @Override
    public String toString(){
        return queryId + " " + docId + " " + relevance;
    }

    @Override
    public int compareTo(Relevance o) {
        //Only matter if docId and queryId are the same.
        Relevance r = (Relevance) o;
        System.out.println(this.docId + " / " + r.getDocId());
        System.out.println(this.queryId + " / " + r.getQueryId());
        if(this.docId == r.getDocId() && this.queryId == r.getQueryId())
            return 0;
        System.out.println("sdsdg");
        return 1;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Relevance other = (Relevance) obj;
        if (this.queryId != other.queryId) {
            return false;
        }
        if (this.docId != other.docId) {
            return false;
        }
        return true;
    }
    
    
    
    
}
