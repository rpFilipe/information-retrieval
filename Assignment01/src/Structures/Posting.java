package Structures;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rpfilipe
 */
public class Posting implements Comparable{
    
    
    private int docId;
    private int frequency;

    public Posting(int docId) {
        this.docId = docId;
        this.frequency = 1;
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
