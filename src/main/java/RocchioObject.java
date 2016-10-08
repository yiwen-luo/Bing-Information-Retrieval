import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RocchioObject {

    private static final double ALPHA = 1.0;
    private static final double BETA = 0.75;
    private static final double GAMMA = 0.15;

    private List<BingResult.ResultTuple> fullList, relList, irrList;
    private List<List<BingResult.ResultTuple>> localFullList, localRelList, localIrrList;
    private String originQuery;
    private TreeMap<String, Double> q0;
    private TreeMap<String, Double> score = new TreeMap<String, Double>();

    public RocchioObject(List<BingResult.ResultTuple> fullList,
                         List<BingResult.ResultTuple> relList,
                         List<BingResult.ResultTuple> irrList,
                         List<List<BingResult.ResultTuple>> localFullList,
                         List<List<BingResult.ResultTuple>> localRelList,
                         List<List<BingResult.ResultTuple>> localIrrList,
                         String originQuery, TreeMap<String, Double> q0) {
        this.fullList = fullList;
        this.relList = relList;
        this.irrList = irrList;
        this.localFullList = localFullList;
        this.localRelList = localRelList;
        this.localIrrList = localIrrList;
        this.originQuery = originQuery;
        this.q0 = q0;
    }

    public String getNewQuery() {
        String result = originQuery;
        WordVector fullVector = new WordVector(fullList);
        WordVector relVector = new WordVector(relList);
        WordVector irrVector = new WordVector(irrList);
        TreeMap<String, Integer> fullDocVector = fullVector.getDocVector();
        TreeMap<String, Integer> relDocVector = relVector.getDocVector();
        TreeMap<String, Integer> irrDocVector = irrVector.getDocVector();

        for (Map.Entry<String, Integer> entry : fullDocVector.entrySet()) {
            score.put(entry.getKey(), calScore(entry.getKey(), relDocVector, irrDocVector));
        }

        int stopCounter = 0;
        String[] originList = originQuery.split("\\+");
        while (stopCounter < 2) {
            String newWord = getMaxKey(score);
            for (int i = 0; i < originList.length; i++) {
                if (originList[i].equals(newWord)) {
                    score.remove(newWord);
                    newWord = getMaxKey(score);
                } else {
                    result += ("+" + newWord);
                    score.remove(newWord);
                    newWord = getMaxKey(score);
                    stopCounter++;
                }
            }
        }
        return result;
    }

    private String getMaxKey(Map<String, Double> map) {
        double maxTemp = 0.0;
        String maxKey = "";
        for (Map.Entry<String, Double> tuple : map.entrySet()) {
            if (tuple.getValue() > maxTemp) {

                maxTemp = tuple.getValue();
                maxKey = tuple.getKey();
            }
        }
        return maxKey;
    }

    public TreeMap<String, Double> getQ0() {
        return this.score;
    }

    private double calScore(String term,
                            TreeMap<String, Integer> relDocVector,
                            TreeMap<String, Integer> irrDocVector) {

        int relCounter = 0, irrCounter = 0;
        double dRel = 0.0, dIrr = 0.0;
        double localScore = 0.0;
        if (q0.containsKey(term)) {
            localScore = ALPHA * q0.get(term);
        }

        TreeMap<String, Double> relIdf = calIdf(relDocVector, localRelList);
        TreeMap<String, Double> irrIdf = calIdf(irrDocVector, localIrrList);

        for (List<BingResult.ResultTuple> localRelVector : localRelList) {
            WordVector localRel = new WordVector(localRelVector);
            TreeMap<String, Integer> localRelDocVector = localRel.getDocVector();
            TreeMap<String, Double> relTf = calTf(localRelDocVector);
            if (localRelDocVector.containsKey(term)) {
                try {
                    dRel += relTf.get(term) * relIdf.get(term);
                    relCounter++;
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        }

        for (List<BingResult.ResultTuple> localIrrVector : localIrrList) {
            WordVector localIrr = new WordVector(localIrrVector);
            TreeMap<String, Integer> localIrrDocVector = localIrr.getDocVector();
            TreeMap<String, Double> irrTf = calTf(localIrrDocVector);
            if (localIrrDocVector.containsKey(term)) {
                try {
                    dIrr += irrTf.get(term) * irrIdf.get(term);
                    irrCounter++;
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        }

        if (relCounter > 0) {
            localScore += BETA * dRel / (double) relCounter;
        }
        if (irrCounter > 0) {
            localScore -= GAMMA * dIrr / (double) irrCounter;
        }
        return localScore;
    }

    private TreeMap<String, Double> calTf(TreeMap<String, Integer> docVector) {
        TreeMap<String, Double> result = new TreeMap<>();
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : docVector.entrySet()) {
            if (entry.getValue() > maxCount)
                maxCount = entry.getValue();
        }
        for (Map.Entry<String, Integer> entry : docVector.entrySet()) {
            result.put(entry.getKey(), 0.5D + 0.5D * entry.getValue() / maxCount);
        }
        return result;
    }

    private TreeMap<String, Double> calIdf(TreeMap<String, Integer> globalDocVector,
                                           List<List<BingResult.ResultTuple>> localTuplesFull) {
        TreeMap<String, Double> result = new TreeMap<>();
        for (Map.Entry<String, Integer> globalTerm : globalDocVector.entrySet()) {
            int counter = 0;
            for (List<BingResult.ResultTuple> localTuples : localTuplesFull) {
                WordVector localVector = new WordVector(localTuples);
                TreeMap<String, Integer> localDocVector = localVector.getDocVector();
                if (localDocVector.containsKey(globalTerm.getKey())) {
                    counter++;
                }
            }
            result.put(globalTerm.getKey(), Math.log((double) localTuplesFull.size() / counter + 1.0));
        }
        return result;
    }
}
