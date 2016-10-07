import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class WordVector {
    // key -> word in the documents     value -> frequency of the word
    // docVector saves the sum of document vector for relevant document list or irrelevant document list
    public TreeMap<String, Integer> docVector;

    public WordVector(List<BingResult.ResultTuple> list) {
        docVector = new TreeMap<>();
        for (BingResult.ResultTuple tuple : list) {
            String textContent = tuple.title + tuple.summary;
            String[] text = textContent.split("\\s+");
            for (String word : text) {
                String wordLowerCase = word.toLowerCase();
                boolean isValid = true;
                for (Character ch : wordLowerCase.toCharArray()) {
                    if (!Character.isAlphabetic(ch)) {
                        isValid = false;
                        break;
                    }
                }
                if (!isValid) {
                    continue;
                }
                if (BingHandler.stopWordDict.contains(wordLowerCase)) {
                    continue;
                }
                if (docVector.containsKey(wordLowerCase)) {
                    docVector.put(wordLowerCase, docVector.get(wordLowerCase) + 1);
                } else {
                    docVector.put(wordLowerCase, 1);
                }
            }
        }
    }

    public TreeMap<String, Integer> getDocVector(){
        return this.docVector;
    }
}
