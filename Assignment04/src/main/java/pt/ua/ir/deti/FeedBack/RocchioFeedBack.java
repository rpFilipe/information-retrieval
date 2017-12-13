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
import java.util.TreeSet;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.summingDouble;
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

    public Map<String, Double> computeFeedBack(String type, int queryId, Map<String, Double> queryVector, TreeSet<QueryResult> retrieveDocs) {

        LinkedList<Relevance> relevantDocs = relevanceMap.get(queryId);
        if (relevantDocs == null) {
            return queryVector;
        }

        //debug
        //System.out.println("relevantDocs: "+ relevantDocs.toString());
        //System.out.println("retriveDocs: "+ retriveDocs.toString());  
        
        //int dr = relevantDocs.size();
        //int dnr = corpusSize - dr;
        
        if (type.equalsIgnoreCase("explicit")) {

            // relevant docs
            TreeSet<QueryResult> dr = new TreeSet<>();
            /*TreeSet<QueryResult> dr = retrieveDocs.stream()
                    .filter(e -> relevantDocs.contains(new Relevance(queryId,e.getDocId())))
                    .collect(Collectors.toCollection(TreeSet<QueryResult>::new));
            */
            
            retrieveDocs.forEach(entry -> {
                relevantDocs.forEach( e ->{
                    if(e.getDocId() == entry.getDocId() && e.getQueryId() == entry.getQueryId()){
                        System.out.println(entry.getDocId());
                        dr.add(entry);
                    }
                });
            });
            
            System.out.println("dr: " + dr.toString());
            
            Map<String, Double> sumWtDR = null;
            
            dr.forEach( entry -> {
                List<StringPosting> strPos = docCache.get(entry.getDocId());
                
                Map<String, Double> tmp = (Map<String, Double>) Optional.ofNullable(strPos)
                .orElseGet(Collections::emptyList).stream()
                .collect(Collectors.groupingBy(StringPosting::getTerm, summingDouble(StringPosting::getTermWeigth)));
                
                tmp.forEach(sumWtDR::putIfAbsent);

            } );
            
            if(sumWtDR != null){
                Map<String, Double> resultDR = sumWtDR.entrySet().stream()
                    .filter(m -> m.getValue() != null)
                    .collect(toMap(Map.Entry::getKey, e -> e.getValue() * BETA));
            }
            
            // non relevant docs
            TreeSet<QueryResult> dnr = retrieveDocs.stream()
                    .filter(e -> !relevantDocs.contains(e.getDocId()))
                    .collect(Collectors.toCollection(TreeSet<QueryResult>::new));

            System.out.println("dnr: " + dnr.toString());
            
            Map<String, Double> sumWtDNR = null;
            
            dnr.forEach( entry -> {
                List<StringPosting> strPos = docCache.get(entry.getDocId());
                
                Map<String, Double> tmp = (Map<String, Double>) Optional.ofNullable(strPos)
                .orElseGet(Collections::emptyList).stream()
                .collect(Collectors.groupingBy(StringPosting::getTerm, summingDouble(StringPosting::getTermWeigth)));
                
                tmp.forEach(sumWtDNR::putIfAbsent);

            } );
            
            if(sumWtDNR != null){
                Map<String, Double> resultDNR = sumWtDNR.entrySet().stream()
                    .collect(toMap(Map.Entry::getKey, e -> e.getValue() * THETA));
            }
            
            
            //query
            Map<String, Double> resultQ = queryVector.entrySet().stream()
                    .collect(toMap(Map.Entry::getKey, e -> e.getValue() * ALPHA));
            
            
            /*resultQ.forEach( (k,v) -> {
                double drWt = resultDR.get(k);
                double
            });*/
            
        } // type = implicit
        else {

        }
        return null;
    }

}
