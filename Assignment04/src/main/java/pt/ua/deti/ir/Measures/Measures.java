
package pt.ua.deti.ir.Measures;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.stream.Collectors;
import pt.ua.deti.ir.Structures.QueryResult;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação
 *
 * @author Joana Conde
 * @author Ricardo Filipe
 */
public class Measures {
    
    //private HashMap<Integer, LinkedList<Posting>> relevanceMap;
    private HashMap<Integer, LinkedList<Integer>> relevanceMap;
    
    public Measures(String fname) throws FileNotFoundException {
        
        File fileidx = new File(fname);
        Scanner fsc = new Scanner(fileidx);
        this.relevanceMap = new HashMap<>();
        
        String[] line;
        int currentQueryId = 1;
        int queryId, docId, relevance;
        //LinkedList<Posting> postings = new LinkedList();
        LinkedList<Integer> docs = new LinkedList();
        //Posting p;
        
        while(fsc.hasNext()) {
            line = fsc.nextLine().split(" ");
            
            queryId = Integer.parseInt(line[0]);
            docId = Integer.parseInt(line[1]);
            //relevance = Integer.parseInt(line[2]);

            //p = new Posting(docId, relevance);
            //postings.add(p);
            if (currentQueryId != queryId){
                relevanceMap.put(currentQueryId, docs);
                //postings.clear();
                docs = new LinkedList<>();
                docs.add(docId);
                currentQueryId = queryId;
            }
            else{
                docs.add(docId);
            }
        } 
        
        relevanceMap.put(currentQueryId, docs);
        
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
        
        LinkedList<Integer> docs = relevanceMap.get(qresult.first().getQueryId());
        
        //System.out.println("Gold Standard: " + docs);
        
        if(docs == null)
            return 0.0;
        

        
        /* // retirar apenas os docs que aparecem com relevancia maior do que 1, portanto os que estao no gold standard
        qresult.stream()
                .filter(e -> docs.contains(e.getDocId()))
                .collect(Collectors.toCollection(TreeSet<QueryResult>::new));
        */
        
        //System.out.println("qresult size: "+ qresult.size());
        //System.out.println("qresult: "+ qresult);
        
        return 0.0;
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
