
package pt.ua.deti.ir.Measures;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.stream.Collectors;
import pt.ua.deti.ir.Structures.QueryResult;
import pt.ua.deti.ir.Structures.Relevance;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação
 *
 * @author Joana Conde
 * @author Ricardo Filipe
 */
public class Measures {
    
    //private HashMap<Integer, LinkedList<Posting>> relevanceMap;
    private HashMap<Integer, LinkedList<Integer>> relevanceMap;
    private HashMap<Integer, LinkedList<Relevance>> map_rel;
    private static int i;
    
    public Measures(String fname) throws FileNotFoundException {
        
        File fileidx = new File(fname);
        Scanner fsc = new Scanner(fileidx);
        this.relevanceMap = new HashMap<>();
        this.map_rel = new HashMap<>();
        
        String[] line;
        int currentQueryId = 1;
        int queryId, docId, relevance;
        //LinkedList<Posting> postings = new LinkedList();
        LinkedList<Integer> docs1 = new LinkedList();
        LinkedList<Relevance> docs2 = new LinkedList();
        Relevance rl;
        i = 1;
        //Posting p;
        
        while(fsc.hasNext()) {
            line = fsc.nextLine().split(" ");
            
            queryId = Integer.parseInt(line[0]);
            docId = Integer.parseInt(line[1]);
            relevance = Integer.parseInt(line[2]);

            //p = new Posting(docId, relevance);
            //postings.add(p);
            rl = new Relevance(queryId, docId, relevance);
            
            if (currentQueryId != queryId){
                relevanceMap.put(currentQueryId, docs1);
                map_rel.put(currentQueryId, docs2);
                //postings.clear();
                docs1 = new LinkedList<>();
                docs2 = new LinkedList<>();
                docs1.add(docId);
                docs2.add(rl);
                currentQueryId = queryId;
            }
            else{
                docs1.add(docId);
                docs2.add(rl);
            }
        } 
        
        relevanceMap.put(currentQueryId, docs1);
        map_rel.put(currentQueryId, docs2);
        //showMap();
    }
    
    
    public double precision(TreeSet<QueryResult> qresult){     
        int tp = 0;
        LinkedList<Integer> docs = relevanceMap.get(qresult.first().getQueryId());
        
        if(docs == null)
            return 0.0;
        
        tp = qresult.stream().filter((q) -> (docs.contains(q.getDocId()))).map((_item) -> 1).reduce(tp, Integer::sum);
        
        return (double) tp/(qresult.size());
    }
    
    public double recall(TreeSet<QueryResult> qresult) {
        
        int tp = 0;
        
        LinkedList<Integer> docs = relevanceMap.get(qresult.first().getQueryId());
        
        if(docs == null)
            return 0.0;
        
        tp = qresult.stream().filter((q) -> (docs.contains(q.getDocId()))).map((_item) -> 1).reduce(tp, Integer::sum); 
        int fn = docs.size() - tp;
        
        return (double) (tp/ (tp + fn));
    }
    
    public double fmeasure (double recall, double precision, double beta) {
        
        return (((Math.pow(beta, 2) + 1) * recall * precision) / (recall + (Math.pow(beta, 2) * precision)));
        
    }
    
    public double averagePrecision(TreeSet<QueryResult> qresult){
        
        double avgp = 0.0, tmpavg;
        int i = 1;
        int relevantDocs = 0;
        
        LinkedList<Integer> docs = relevanceMap.get(qresult.first().getQueryId());
        for( QueryResult q : qresult) {
            if(docs.contains(q.getDocId())){
                relevantDocs++;
                tmpavg = relevantDocs/i;
                avgp += tmpavg;
            }
            i++;
        }
        
        if(relevantDocs == 0)
            return 0.0;
        
        return (double) avgp/relevantDocs;
    }
    
    
    public double precisionAtRank(TreeSet<QueryResult> qresult, int rank){     
        int tp = 0;
        LinkedList<Integer> docs = relevanceMap.get(qresult.first().getQueryId());
        
        if(docs == null)
            return 0.0;
        
        qresult = qresult.stream()
                .limit(rank)
                .collect(Collectors.toCollection(TreeSet<QueryResult>::new));
        
        tp = qresult.stream().filter((q) -> (docs.contains(q.getDocId()))).map((_item) -> 1).reduce(tp, Integer::sum); 
        return (double) tp/(qresult.size());
    }
    
    public double reciprocalRank(TreeSet<QueryResult> qresult){
        
        LinkedList<Integer> docs = relevanceMap.get(qresult.first().getQueryId());
        
        int i = 1;
        for(QueryResult q : qresult){
            if(docs.contains(q.getDocId()))
                return 1/i;
            i++;
        }
        return 0;
    }
    
    
    public double mean(List<Double> values){
        
        double sum = 0;
        return (values.stream().map((d) -> d).reduce(sum, (accumulator, _item) -> accumulator + _item))/values.size();
    }
    
    public double nDCG(TreeSet<QueryResult> qresult){
        i = 1;
        LinkedList<Relevance> docsRelevantes = map_rel.get(qresult.first().getQueryId());       
        //System.out.println("Gold Standard: " + docsRelevantes);
        
        if(docsRelevantes == null)
            return 0.0;
        /*
        // Helper to get the relevant docIDs to the query
        List<Integer> relDocsId = docsRelevantes.stream()
                .map(doc -> doc.getDocId())
                .collect(Collectors.toList());
        
        // retirar apenas os docs que aparecem com relevancia maior do que 0, portanto os que estao no gold standard
        // para calcular o real dcg
        TreeSet<QueryResult> tmp = qresult.stream()
                .filter(e -> relDocsId.contains(e.getDocId()))
                .collect(Collectors.toCollection(TreeSet<QueryResult>::new));
   
        //System.out.println("docsRelevantes size: "+ docsRelevantes.size());
        //System.out.println("tmp size: "+ tmp.size());
        //System.out.println("tmp: "+ tmp);
                
        // para o caso em que nao devolvemos nenhum doc, o retorno e zero
        // nao precisamos calcular o idcg porque o ndcg vai ser zero
        */
        //if(tmp.isEmpty())
        if(qresult.isEmpty())
            return 0.0;
        
        Map<Integer, Double> dcg = new HashMap<>(); 
        Map<Integer, Integer> tmpIdcg = new HashMap<>();
        
        // calcular dcg e colocar num mapa para depois ordenar e calcular idcg
        //for (QueryResult q : qresult) {
        //for (QueryResult q : tmp) {
        
        qresult.forEach(q -> { 
            docsRelevantes.forEach((rl) -> {
                if(q.getDocId() == rl.getDocId()){
                    double dcgDoc = rl.getRelevance() / (Math.log(i+1) / Math.log(2)); //log base 2
                    dcg.put(q.getDocId(), dcgDoc);
                    //i++;
                }
                tmpIdcg.putIfAbsent(rl.getDocId(), rl.getRelevance());
            });
            i++; //TODO: DUVIDA perguntar ao prof
        });
        
        // somar os dcg de todos os documentos
        double dcgSum = dcg.values().stream().mapToDouble(Number::doubleValue).sum();
        
        //System.out.println(dcg);
        //System.out.println(dcgSum);
        //System.out.println("tmpIdcg: "+tmpIdcg);
        
        i = 1;
        //ordenar para calcular idcg
        Map<Integer, Double> idcg  = tmpIdcg.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, key -> key.getValue() / (Math.log((i++)+1) / Math.log(2))));
                
        //System.out.println(idcg);
        
        double idcgSum = idcg.values().stream().mapToDouble(Number::doubleValue).sum();
        //System.out.println("idcgSum: "+idcgSum);
        
        return dcgSum/idcgSum;
    }
    
    private void showMap(){
        
        for (Map.Entry<Integer, LinkedList<Integer>> entry : relevanceMap.entrySet()) {
            Integer key = entry.getKey();
            LinkedList<Integer> value = entry.getValue();
            String s = "" + key + " -> ";

            for(int v : value)
                s = s + " " + v + " ";
            
            System.out.println(s);
            
        }
        
    }
    
}
