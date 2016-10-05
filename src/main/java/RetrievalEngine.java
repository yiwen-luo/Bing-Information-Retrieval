import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhangzhiwang on 10/4/16.
 */
public class RetrievalEngine {
    private double targetPrecision;
    public static Set<String> stopWordDict;
    private BingResult bingResult;

    public void init(String accountKey, double precision) {
        this.stopWordDict = new HashSet<>();
        this.targetPrecision = precision;
        this.bingResult = new BingResult(accountKey,targetPrecision);

        try {
            loadStopWords();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            this.stopWordDict.add(parts[0]);
        }
        in.close();
    }
}
