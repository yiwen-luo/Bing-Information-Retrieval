import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class WordVector {
    // key -> word in the documents     value -> frequency of the word
    // docVector saves the sum of document vector for relevant document list or irrelevant document list
    public TreeMap<String, Integer> docVector;
    public Set<Character> dict;


    public WordVector(List<BingResult.ResultTuple> list) {
        docVector = new TreeMap<>();
        dict = new HashSet<>();
        dict.add(',');
        dict.add('.');
        dict.add(':');
        dict.add(';');

        for (BingResult.ResultTuple tuple : list) {
            String textContent = tuple.title + " " + tuple.summary;
            String[] text = textContent.split("\\s+");
            for (String word : text) {
                String wordLowerCase = word.toLowerCase();
                boolean isValid = true;
                String newWord = wordLowerCase;
                for (int i = 0; i < wordLowerCase.length(); i++) {
                    if (dict.contains(wordLowerCase.charAt(i)) && i == wordLowerCase.length() - 1) {
                        newWord = wordLowerCase.substring(0, i);
                    } else if ((wordLowerCase.charAt(i) < 'a' || wordLowerCase.charAt(i) > 'z')) {
                        isValid = false;
                        break;
                    }
                }
                if (!isValid) {
                    continue;
                }
                if (BingHandler.stopWordDict.contains(newWord)) {
                    continue;
                }
                if (docVector.containsKey(newWord)) {
                    docVector.put(newWord, docVector.get(newWord) + 1);
                } else {
                    docVector.put(newWord, 1);
                }
            }
        }
    }

    public TreeMap<String, Integer> getDocVector() {
        return this.docVector;
    }
}
