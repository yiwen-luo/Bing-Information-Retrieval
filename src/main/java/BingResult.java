import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class BingResult {
    private List<ResultTuple> list, relevantList, irrelevantList;
    private List<List<ResultTuple>> localList = new ArrayList<List<ResultTuple>>(),
            localRelList= new ArrayList<List<ResultTuple>>(),
            localIrrlist= new ArrayList<List<ResultTuple>>();
    private boolean firstRound;
    private double targetPrecision;
    public double actualPrecision;

    private static final int RESULT_NUM = 10;
    private static String accountKey, bingUrl;
    private static String query;

    public BingResult(String accountKey, double targetPrecision) {
        this.accountKey = accountKey;
        this.firstRound = true;
        this.targetPrecision = targetPrecision;
        this.actualPrecision = 0.0;
    }

    public double getActualPrecision() {
        return this.actualPrecision;
    }

    public List<ResultTuple> getList() {
        return this.list;
    }

    public List<ResultTuple> getRelevantList() {
        return this.relevantList;
    }

    public List<ResultTuple> getIrrelevantList() {
        return this.irrelevantList;
    }

    public List<List<ResultTuple>> getLocalList() {
        return this.localList;
    }

    public List<List<ResultTuple>> getLocalRelList() {
        return this.localRelList;
    }

    public List<List<ResultTuple>> getLocalIrrlist() {
        return this.localIrrlist;
    }

    public void performQuery(String query) throws IOException {
        this.list = new ArrayList<>();
        this.relevantList = new ArrayList<>();
        this.irrelevantList = new ArrayList<>();
        this.query = query;
        StringBuilder bingUrlBuilder = new StringBuilder();
        bingUrlBuilder.append("https://api.datamarket.azure.com/Bing/Search/Web?Query=%27");
        bingUrlBuilder.append(query);
        bingUrlBuilder.append("%27&$top=");
        bingUrlBuilder.append(String.valueOf(RESULT_NUM));
        bingUrlBuilder.append("&$format=json");
        this.bingUrl = bingUrlBuilder.toString();

        try {
            queryBing();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.firstRound = false;
        printResult();
    }

    // connect to bing and get the top RESULT_NUM results, and save them as a list of tuples.
    private void queryBing() throws IOException {
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

            // If the results length is less than RESULT_NUM for the first round, exit.
            if (firstRound && resultsLength < RESULT_NUM) {
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
                try {
                    localList.get(i).add(tuple);
                } catch (Exception e) {
                    List tempList = new ArrayList<ResultTuple>();
                    tempList.add(tuple);
                    localList.add(tempList);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void printResult() throws IOException {
        printHeader();
        printAndMarkEntries();
        printFooter();
    }

    private void printHeader() {
        System.out.println("Parameters : ");
        System.out.println("Client Key = " + accountKey);
        System.out.println("Query      = " + query);
        System.out.println("Precision  = " + this.targetPrecision);
        System.out.println("URL: " + bingUrl);
        System.out.println("Total no of results: " + RESULT_NUM);
        System.out.println("Bing Search Results:");
        System.out.println("======================");
    }

    private void printFooter() {
        System.out.println("======================");
        System.out.println("FEEDBACK SUMMARY");
        System.out.println("Query: " + query);
        updatePrecision();
        System.out.printf("Precision: %.1f\n", actualPrecision);
    }

    private void printAndMarkEntries() throws IOException {
        int i = 0;
        for (ResultTuple tuple : list) {
            tuple.printAndMark();
            if (tuple.relevant) {
                relevantList.add(tuple);
                try {
                    localRelList.get(i).add(tuple);
                } catch (Exception e) {
                    List tempList = new ArrayList<ResultTuple>();
                    tempList.add(tuple);
                    localRelList.add(tempList);
                }

            } else {
                irrelevantList.add(tuple);
                try {
                    localIrrlist.get(i).add(tuple);
                } catch (Exception e) {
                    List tempList = new ArrayList<ResultTuple>();
                    tempList.add(tuple);
                    localIrrlist.add(tempList);
                }
            }
            i++;
        }
    }

    private void updatePrecision() {
        if (list.size() == 0) {
            actualPrecision = 0.0;
        }
        int count = 0;
        for (ResultTuple tuple : list) {
            if (tuple.relevant) {
                count++;
            }
        }
        actualPrecision = count / (double) list.size();
    }

    // ResultTuple keeps track of the metadata for each result entry.
    public class ResultTuple {
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
            System.out.println("Result: " + index);
            System.out.println("[");
            System.out.println(" URL: " + url);
            System.out.println(" Title: " + title);
            System.out.println(" Summary: " + summary);
            System.out.println("]");
            System.out.print("Relevant (Y/N)?");

            String input = new BufferedReader(new InputStreamReader(System.in)).readLine();
            this.relevant = input.compareToIgnoreCase("Y") == 0;
        }
    }
}
