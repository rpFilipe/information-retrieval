/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Structures;

/**
 *
 * @author rpfilipe
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
