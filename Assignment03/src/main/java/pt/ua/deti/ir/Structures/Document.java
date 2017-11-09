
package pt.ua.deti.ir.Structures;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Joana Conde 
 * @author Ricardo Filipe
 */
public class Document {

    private int docId;
    private String content;

    public Document(int docId, String content) {
        this.docId = docId;
        this.content = content;
    }

    public int getDocId() {
        return docId;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Document{" + "docId=" + docId + ", content=" + content + '}';
    }

}
