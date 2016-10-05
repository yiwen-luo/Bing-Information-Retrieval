import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.json.*;
import org.apache.commons.codec.binary.Base64;
import java.util.*;
/**
 * Created by zhangzhiwang on 10/3/16.
 */
public class BingResult {
    public static final int NUMBER = 10;
    public static String accountKey, bingUrl;
    public static String query;
    public List<ResultTuple> list, relevantList, irrelevantList;
    public Iterator<ResultTuple> iterator;

    public BingResult(String query, String accountKey) throws IOException {
        this.list = new ArrayList<>();
        this.relevantList = new ArrayList<>();
        this.irrelevantList = new ArrayList<>();
        this.accountKey = accountKey;
        this.query = query;
        StringBuilder bingUrlBuilder = new StringBuilder();
        bingUrlBuilder.append("https://api.datamarket.azure.com/Bing/Search/Web?Query=%27");
        bingUrlBuilder.append(query);
        bingUrlBuilder.append("%27&$top=");
        bingUrlBuilder.append(String.valueOf(NUMBER));
        bingUrlBuilder.append("&$format=json");
        this.bingUrl = bingUrlBuilder.toString();

        try {
            connectAndGetTenEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.iterator = list.iterator();
        printResult();
    }
    // connect to bing and get the top 10 results, and save them as a list of tuples.
    public void connectAndGetTenEntry() throws IOException {
        byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
        String accountKeyEnc = new String(accountKeyBytes);

        URL url = new URL(bingUrl);
        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);

        try (final BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
            String inputLine;
            final StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            final JSONObject json = new JSONObject(response.toString());
            final JSONObject d = json.getJSONObject("d");
            final JSONArray results = d.getJSONArray("results");
            final int resultsLength = results.length();
            // If the results length is less than 10 for the first round, exit.
            if (Main.firstRound && resultsLength < 10) {
                System.out.println("Less than ten results were found, the program will exit.");
                System.exit(-1);
            }
            for (int i = 0; i < resultsLength; i++) {
                final JSONObject aResult = results.getJSONObject(i);
                String Url = (String) aResult.get("Url");
                String title = (String) aResult.get("Title");
                String description = (String) aResult.get("Description");

                ResultTuple tuple = new ResultTuple(Url, title, description, i + 1);
                list.add(tuple);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void printAndMarkOneEntry() throws IOException {
        if (iterator.hasNext()) {
            ResultTuple tuple = iterator.next();
            tuple.printAndMark();
            if (tuple.relevant) {
                relevantList.add(tuple);
            } else {
                irrelevantList.add(tuple);
            }
        }
    }
    // For test purpose
    public void printResult() throws IOException {
        System.out.printf("Parameters : \nQuery = %s\nPrecision = %f\nURL: %s\nTotal no of results: %d\nBing Result Search\n", query, Main.targetPrecision, bingUrl, list.size());
        for (int i = 0; i < list.size(); i++) {
            printAndMarkOneEntry();
        }
        System.out.println("================================");
        System.out.printf("Query : %s\nPrecision : %f\n", query, calculatePrecision());

    }

    public double calculatePrecision() {
        int count = 0;
        if (list.size() == 0) {
            return 0;
        }
        for (ResultTuple tuple : list) {
            if (tuple.relevant) {
                count++;
            }
        }
        return (double) count / (double) list.size();
    }
    // ResultTuple keeps track of the metadata for each result entry.
    public class ResultTuple{
        public String summary, url, title;
        public int index;
        public boolean relevant;

        public ResultTuple(String url, String title, String summary, int index) {
            this.url = url;
            this.title = title;
            this.summary = summary;
            this.index = index;
            this.relevant = false;
        }

        public void printAndMark() throws IOException {
            System.out.printf("Result %d:\n[\n\tTitle  : %s\n\tURL    : %s\n\tSummary: %s\n]\nIs this page relevant? [y/n]\n", this.index, this.title, this.url, this.summary);
            String input = new BufferedReader(new InputStreamReader(System.in)).readLine();
            this.relevant = input.compareToIgnoreCase("Y") == 0;
        }
    }
}
