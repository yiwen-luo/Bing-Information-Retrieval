import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RocchioObject {

    private WordVector fullList, relList, irrList;
    private List<List<BingResult.ResultTuple>> localFullList, localRelList, localIrrList;
    private String originQuery;

    public RocchioObject(WordVector fullList, WordVector relList, WordVector irrList, String originQuery) {
        this.fullList = fullList;
        this.relList = relList;
        this.irrList = irrList;
        this.originQuery = originQuery;
    }

    public String getNewQuery() {

//        List<Double> tf = calTf();
        return "";

    }

    private TreeMap<String, Double> calTf(List<BingResult.ResultTuple> tuples) {
        WordVector vector = new WordVector(tuples);
        TreeMap<String, Integer> docVector = vector.getDocVector();
        TreeMap<String, Double> result = new TreeMap<>();
        for (Map.Entry<String, Integer> entry : docVector.entrySet()) {
            result.put(entry.getKey(), 0.5D + 0.5D * entry.getValue() / docVector.lastEntry().getValue());
        }
        return result;
    }

    private TreeMap<String, Double> calIdf(){

    }
}
