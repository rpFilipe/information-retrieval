/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.deti.ir.Structures;

/**
 *
 * @author rpfilipe
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
