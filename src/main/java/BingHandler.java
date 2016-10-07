import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class BingHandler {
    private double targetPrecision;
    public BingResult bingResult;

    public static Set<String> stopWordDict;

    public void init(String accountKey, double precision) {
        this.targetPrecision = precision;
        this.bingResult = new BingResult(accountKey, targetPrecision);
        stopWordDict = new HashSet<>();
        try {
            loadStopWords();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BingResult getBingResult(){
        return this.bingResult;
    }

    public void query(String query) {
        try {
            bingResult.performQuery(query);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadStopWords() throws IOException {
        BufferedReader in = new BufferedReader(new FileReader("./stopword.txt"));
        String line = "";
        while ((line = in.readLine()) != null) {
            String parts[] = line.split("\t");
            stopWordDict.add(parts[0]);
        }
        in.close();
    }
}
