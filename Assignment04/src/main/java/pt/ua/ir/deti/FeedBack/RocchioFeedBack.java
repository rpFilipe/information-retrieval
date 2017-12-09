/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.ir.deti.FeedBack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import pt.ua.deti.ir.Structures.QueryResult;
import pt.ua.deti.ir.Structures.Relevance;
import pt.ua.deti.ir.Structures.StringPosting;

/**
 *
 * @author rpfilipe
 */
public class RocchioFeedBack {

    private HashMap<Integer, LinkedList<StringPosting>> docCache;
    private HashMap<Integer, LinkedList<Relevance>> relevanceMap;
    private final double alpha, beta, theta;

    public RocchioFeedBack(String docCache, String relevance, double alpha, double beta, double theta) throws FileNotFoundException {
        loadDocCache(docCache);
        loadRelevance(relevance);
        this.alpha = alpha;
        this.beta = beta;
        this.theta = theta;
    }

    private void loadDocCache(String fname) throws FileNotFoundException {
        File fileidx = new File(fname);
        Scanner fsc = new Scanner(fileidx);
        this.docCache = new HashMap<>();

        String line;
        String[] terms;
        int docId;
        LinkedList<StringPosting> l;

        while (fsc.hasNextLine()) {
            line = fsc.nextLine();
            terms = line.split(",");
            docId = Integer.parseInt(terms[0]);
            l = new LinkedList<>();
            for (int i = 1; i < terms.length; i++) {
                l.add(new StringPosting(terms[i]));
            }
            docCache.put(docId, l);
        }
    }

    private void loadRelevance(String fname) throws FileNotFoundException {
        File fileidx = new File(fname);
        Scanner fsc = new Scanner(fileidx);
        this.relevanceMap = new HashMap<>();

        String[] line;
        int currentQueryId = 1;
        int queryId, docId, relevance;
        LinkedList<Relevance> docs;

        while (fsc.hasNext()) {
            line = fsc.nextLine().split(" ");
            queryId = Integer.parseInt(line[0]);
            docId = Integer.parseInt(line[1]);
            relevance = Integer.parseInt(line[2]);

            if (relevanceMap.containsKey(docId)) {
                docs = relevanceMap.get(docId);
                docs.add(new Relevance(queryId, docId, relevance));
                relevanceMap.put(docId, docs);
            } else {
                docs = new LinkedList<>();
                docs.add(new Relevance(queryId, docId, relevance));
                relevanceMap.put(docId, docs);
            }
        }
    }

    public double computeFeedBack(String type, double queryScore, QueryResult qr) {

        if (type.equalsIgnoreCase("explicit")) {

        } // type = implicit
        else {

        }
        return 0;
    }

}
