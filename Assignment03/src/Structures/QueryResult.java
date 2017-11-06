
package Structures;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Joana Conde 
 * @author Ricardo Filipe
 */
public class QueryResult implements Comparable{
    
    private int queryId;
    private int docId;
    private int score;

    public QueryResult(int queryId, int docId, int score) {
        this.queryId = queryId;
        this.docId = docId;
        this.score = score;
    }

    @Override
    public String toString() {
        return "" + this.queryId + "\t\t" + this.docId + "\t\t" + this.score;
    }

    @Override
    public int compareTo(Object t) {
        QueryResult q = (QueryResult) t;
        
        if(this.queryId > q.queryId)
            return 1;
        
        else if(this.queryId < q.queryId)
            return -1;
        
        // same query
        else{
            if(this.score < q.score)
                return 1;
            
            else if(this.score > q.score)
            return -1;
            
            // same query & same score
            else {
                if(this.docId > q.docId)
                    return 1;
                
                else if(this.docId < q.docId)
                    return -1;
                
                return 0;
            }
        }
        
    }
    
    
    
}
