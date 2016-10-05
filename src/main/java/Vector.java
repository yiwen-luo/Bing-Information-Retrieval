/**
 * Created by zhangzhiwang on 10/4/16.
 */
import java.util.*;

public class Vector {
    // key -> word in the documents     value -> frequency of the word
    // docVector saves the sum of document vector for relevant document list or irrelevant document list
    public TreeMap<String, Integer> docVector;

    public Vector(List<BingResult.ResultTuple> list) {
        docVector = new TreeMap<>();
        for (BingResult.ResultTuple tuple : list) {
            String textContent = tuple.title + tuple.summary;
            String[] text = textContent.split("\\W");
            for (String word : text) {
                String wordLowerCase = word.toLowerCase();
                if (docVector.containsKey(wordLowerCase)) {
                    docVector.put(wordLowerCase, docVector.get(wordLowerCase) + 1);
                } else {
                    docVector.put(wordLowerCase, 1);
                }
            }
        }
    }
}
