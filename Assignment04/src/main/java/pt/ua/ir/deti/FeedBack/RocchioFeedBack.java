package pt.ua.ir.deti.FeedBack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
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

        Map<String, Double> modifiedQueryVector = new HashMap<>();
        Set<Integer> dr;
        Set<Integer> dnr;
        Map<String, Double> positiveFeedbackVector = new HashMap<>();
        Map<String, Double> negativeFeedbackVector = new HashMap<>();
        
        modifiedQueryVector.putAll(queryVector);
        //System.out.println("modified: "+modifiedQueryVector);

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

            // Adding all document vector terms
            for (int docId : dr) {
                postings = docCache.get(docId);

                postings.forEach((p) -> {
                    positiveFeedbackVector.putIfAbsent(p.getTerm(), p.getTermWeigth());
                    positiveFeedbackVector.computeIfPresent(p.getTerm(), (k, v) -> v + p.getTermWeigth());
                });
            }

//            System.out.println("queryvector");
//            print(queryVector);
//
//            System.out.println("positiveFeedbackVector");
//            System.out.println("positiveFeedbackVector size: " + positiveFeedbackVector.size());
//            print(positiveFeedbackVector);

            for (int docId : dnr) {
                postings = docCache.get(docId);

                postings.forEach((p) -> {
                    negativeFeedbackVector.putIfAbsent(p.getTerm(), p.getTermWeigth());  // retirei o -
                    negativeFeedbackVector.computeIfPresent(p.getTerm(), (k, v) -> v - p.getTermWeigth());
                });
            }

            /*
            iterating over the positiveFeedbackVector and checking the values in the modifiedQueryVector
            if they dont exist just add them to the modifiedQueryVector
            if they exist compute the sum with the stored value
             */
            positiveFeedbackVector.forEach((t, u) -> {
                modifiedQueryVector.putIfAbsent(t, u * BETA);
                modifiedQueryVector.computeIfPresent(t, (k, v) -> v + u * BETA);
            });

            /*
            iterating over the negativeFeedbackVector and checking the values in the modifiedQueryVector
            if they dont exist just add them to the modifiedQueryVector
            if they exist compute the sum with the stored value
             */
            negativeFeedbackVector.forEach((t, u) -> {
                modifiedQueryVector.putIfAbsent(t, u * THETA);
                modifiedQueryVector.computeIfPresent(t, (k, v) -> v + u * THETA);
            });
            
//            System.out.println("modifiedQueryVector");
//            print(modifiedQueryVector);

        } // type = implicit
        else {
            
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
            
            /*
            iterating over the positiveFeedbackVector and checking the values in the modifiedQueryVector
            if they dont exist just add them to the modifiedQueryVector
            if they exist compute the sum with the stored value
             */
            positiveFeedbackVector.forEach((t, u) -> {
                modifiedQueryVector.putIfAbsent(t, u * BETA);
                modifiedQueryVector.computeIfPresent(t, (k, v) -> v + u * BETA);
            });


        }
        
        Map <String, Double> modifiedQueryVectorRet = new HashMap<>(), tmp = new HashMap<>();     
        
        // retirar apenas os 4 termos com maior peso e que nao estao na query
        tmp = modifiedQueryVector.entrySet().stream()
               .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
               .filter((k) -> !queryVector.containsKey(k.getKey()))
               .limit(4) // apenas os 4 com maior peso
               .collect(Collectors.toMap(Entry::getKey, Entry::getValue,  // colocar num mapa
                                (e1, e2) -> e1, LinkedHashMap::new));

        // retirar os que estao na query
        modifiedQueryVectorRet = modifiedQueryVector.entrySet().stream()
               .filter((k) -> queryVector.containsKey(k.getKey()))
               .collect(Collectors.toMap(Entry::getKey, Entry::getValue,  
                                (e1, e2) -> e1, LinkedHashMap::new));
        
        // devolver os termos que estao na query + 4 de maior peso que nao estao na query
        modifiedQueryVectorRet.putAll(tmp);
       
        
        return modifiedQueryVectorRet;
    }

    public void print(Map<String, Double> res) {
        res.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " - " + entry.getValue());

        });
    }

}
