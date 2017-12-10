package pt.ua.ir.deti.FeedBack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toMap;
import pt.ua.deti.ir.Structures.QueryResult;
import pt.ua.deti.ir.Structures.Relevance;
import pt.ua.deti.ir.Structures.StringPosting;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação
 *
 * @author Joana Conde
 * @author Ricardo Filipe
 */
public class RocchioFeedBack {

    private HashMap<Integer, LinkedList<StringPosting>> docCache;
    private HashMap<Integer, LinkedList<Relevance>> relevanceMap;
    private final double ALPHA, BETA, THETA;
    private final int corpusSize;

    public RocchioFeedBack(String docCache, String relevance, double alpha, double beta, double theta, int corpusSize) throws FileNotFoundException {
        loadDocCache(docCache);
        loadRelevance(relevance);
        this.ALPHA = alpha;
        this.BETA = beta;
        this.THETA = theta;
        this.corpusSize = corpusSize;
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

    /**
     * Metodo para carregar o gold standart relevance
     *
     * @param fname
     * @throws FileNotFoundException
     */
    private void loadRelevance(String fname) throws FileNotFoundException {
        File fileidx = new File(fname);
        Scanner fsc = new Scanner(fileidx);
        this.relevanceMap = new HashMap<>();

        String[] line;
        int currentQueryId = 1;
        int queryId, docId, relevance;
        Relevance rl;
        LinkedList<Relevance> docs = new LinkedList();

        while (fsc.hasNext()) {
            line = fsc.nextLine().split(" ");

            queryId = Integer.parseInt(line[0]);
            docId = Integer.parseInt(line[1]);
            relevance = Integer.parseInt(line[2]);

            rl = new Relevance(queryId, docId, relevance);

            if (currentQueryId != queryId) {
                relevanceMap.put(currentQueryId, docs);
                docs = new LinkedList();
                docs.add(rl);
                currentQueryId = queryId;
            } else {
                docs.add(rl);
            }
        }

        relevanceMap.put(currentQueryId, docs);
    }

    public Map<String, Double> computeFeedBack(String type, int queryId, Map<String, Double> queryVector, TreeSet<QueryResult> retriveDocs) {

        LinkedList relevantDocs = relevanceMap.get(queryId);
        if (relevantDocs == null) {
            return queryVector;
        }

        //int dr = relevantDocs.size();
        //int dnr = corpusSize - dr;
        

        if (type.equalsIgnoreCase("explicit")) {

            TreeSet<QueryResult> dr = retriveDocs.stream()
                    .filter(e -> relevantDocs.contains(e.getDocId()))
                    .collect(Collectors.toCollection(TreeSet<QueryResult>::new));

            TreeSet<QueryResult> dnr = retriveDocs.stream()
                    .filter(e -> !relevantDocs.contains(e.getDocId()))
                    .collect(Collectors.toCollection(TreeSet<QueryResult>::new));

            
        } // type = implicit
        else {

        }
        return null;
    }

}
