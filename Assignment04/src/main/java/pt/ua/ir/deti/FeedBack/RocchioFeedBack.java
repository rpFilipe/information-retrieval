package pt.ua.ir.deti.FeedBack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.summingDouble;
import java.util.concurrent.atomic.AtomicInteger;
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

    private HashMap<Integer, TreeSet<StringPosting>> docCache;
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
        TreeSet<StringPosting> l;

        while (fsc.hasNextLine()) {
            line = fsc.nextLine();
            terms = line.split(",");
            docId = Integer.parseInt(terms[0]);
            l = new TreeSet<>();
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

    public Map<String, Double> computeFeedBack(String type, int queryId, Map<String, Double> queryVector, TreeSet<QueryResult> retrieveDocs) {

        // relevant docs
        Map<String, Double> modifiedVector = null;
        Set<Integer> dr;
        Set<Integer> dnr;
        Set<String> queryTerms = queryVector.keySet();
        double[] positiveFeedbackVector = new double[queryTerms.size()];
        double[] negativeFeedbackBector = new double[queryTerms.size()];

        if (type.equalsIgnoreCase("explicit")) {

            LinkedList<Relevance> relevantDocs = relevanceMap.get(queryId);
            if (relevantDocs == null) {
                return queryVector;
            }

            // Helper to get the relevant docIDs to the query
            List<Integer> relDocsId = relevantDocs.stream()
                    .map(doc -> doc.getDocId())
                    .collect(Collectors.toList());

            dr = retrieveDocs.stream()
                    .filter(doc -> relDocsId.contains(doc.getDocId()))
                    .map(doc -> doc.getDocId())
                    .collect(Collectors.toSet());

            dnr = retrieveDocs.stream()
                    .filter(doc -> !relDocsId.contains(doc.getDocId()))
                    .map(doc -> doc.getDocId())
                    .collect(Collectors.toSet());

            TreeSet<StringPosting> postings;
            StringPosting sp, tmp;
            // iterating over all terms in query
            for (String term : queryTerms) {
                System.out.println(term);
                int i = 0;
                for (int docId : dr) {
                    postings = docCache.get(docId);
                    tmp = new StringPosting(term);
                    if (postings.contains(tmp)) {
                        sp = postings.headSet(tmp, true).first();
                        positiveFeedbackVector[i] += sp.getTermWeigth();
                    }
                }

                for (int docId : dnr) {
                    postings = docCache.get(docId);
                    tmp = new StringPosting(term);
                    if (postings.contains(tmp)) {
                        sp = postings.headSet(tmp, true).first();
                        negativeFeedbackBector[i] += sp.getTermWeigth();
                    }
                }
                i++;
            }

            AtomicInteger i = new AtomicInteger(-1);
            modifiedVector = queryVector.entrySet().stream()
                    .collect(toMap(Map.Entry::getKey, e -> {
                        i.getAndIncrement();
                        System.out.println(i.get());
                        return ALPHA * e.getValue()
                                + (BETA / dr.size()) * positiveFeedbackVector[i.get()]
                                - (THETA / dnr.size()) * negativeFeedbackBector[i.get()];
                    }));

        } // type = implicit
        else {
            
            dr = retrieveDocs.stream()
                    .map(doc -> doc.getDocId())
                    .collect(Collectors.toSet());

            TreeSet<StringPosting> postings;
            StringPosting sp, tmp;
            // iterating over all terms in query
            for (String term : queryTerms) {
                System.out.println(term);
                int i = 0;
                for (int docId : dr) {
                    postings = docCache.get(docId);
                    tmp = new StringPosting(term);
                    if (postings.contains(tmp)) {
                        sp = postings.headSet(tmp, true).first();
                        positiveFeedbackVector[i] += sp.getTermWeigth();
                    }
                }

                i++;
            }

            //THETA is zero
            AtomicInteger i = new AtomicInteger(-1);
            modifiedVector = queryVector.entrySet().stream()
                    .collect(toMap(Map.Entry::getKey, e -> {
                        i.getAndIncrement();
                        System.out.println(i.get());
                        return ALPHA * e.getValue()
                                + (BETA / dr.size()) * positiveFeedbackVector[i.get()];
                    }));
            
        }
        return modifiedVector;
    }

}
