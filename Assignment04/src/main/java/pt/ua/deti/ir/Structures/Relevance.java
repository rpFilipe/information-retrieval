
package pt.ua.deti.ir.Structures;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Joana Conde 
 * @author Ricardo Filipe
 */
public class Relevance {
    
    private int queryId, docId, relevance;

    public Relevance(int queryId, int docId, int relevance) {
        this.queryId = queryId;
        this.docId = docId;
        this.relevance = relevance;
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
    
    
    
}
