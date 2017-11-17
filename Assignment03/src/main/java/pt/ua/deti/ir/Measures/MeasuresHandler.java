/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.deti.ir.Measures;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import pt.ua.deti.ir.Structures.QueryResult;

/**
 *
 * @author rpfilipe
 */
public class MeasuresHandler {
    
    private Measures m;
    private int nqueries;
    private double meanPrecision;
    private double meanRecall;
    private double fmeasure;
    private double meanAveragePrecision;
    private double meanPrecisionAtRank10;
    private double meanReciprocalRank;
    
    public MeasuresHandler(String fname) throws FileNotFoundException {
        m = new Measures(fname);
        nqueries = 0;
        meanPrecision = 0;
        meanRecall = 0;
        fmeasure = 0;
        meanAveragePrecision = 0;
        meanPrecisionAtRank10 = 0;
        meanReciprocalRank = 0;
    }
    
    public void computeQueryMeasures(TreeSet<QueryResult> qresult) {
        nqueries++;
        double precision = m.precision(qresult);
        double recall = m.recall(qresult);
        double f_measure = m.fmeasure(recall, precision, 1);
        double precisionAtRank = m.precisionAtRank(qresult, 10);
        double avgprecision = m.averagePrecision(qresult);
        double reciprocalRank = m.reciprocalRank(qresult);
        
        meanPrecision += precision;
        meanRecall += recall;
        fmeasure += f_measure;
        meanAveragePrecision += avgprecision;
        meanPrecisionAtRank10 += precisionAtRank;
        meanReciprocalRank += reciprocalRank;
        
    }
    
    public void computeRetrieverMeasures(){
        System.out.println("Precision: " + meanPrecision/nqueries);
        System.out.println("Recall: " + meanRecall/nqueries);
        System.out.println("F-Measure: " + fmeasure/nqueries);
        System.out.println("Precision at rank 10: " + meanPrecisionAtRank10 /nqueries);
        System.out.println("Mean Average Precision: " + meanAveragePrecision/nqueries);
        System.out.println("Mean Reciprocal Rank: " + meanReciprocalRank/nqueries);
    }
}
