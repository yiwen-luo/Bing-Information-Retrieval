import org.json.*;

import java.io.IOException;

public class Main {
    public static boolean firstRound = true; // mark the first iteration, should be flipped to false in the second iteration
    public static double targetPrecision;

    public static void main(String[] args) {
        JSONObject jobj = new JSONObject();
        //Input format <bing account key> <precision> <query>
        if (args.length != 3) {
            throw new IllegalArgumentException("Input format <bing account key> <precision> <query>");
        }
        String accountKey = args[0];
        targetPrecision = Double.parseDouble(args[1]);
        String query = args[2];
        try {
            BingResult result = new BingResult(query, accountKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
