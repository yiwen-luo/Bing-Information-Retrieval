import java.util.TreeMap;

public class Main {

    public static void main(String[] args) {
        //Input format <bing account key> <precision> <query>
        if (args.length != 3) {
            throw new IllegalArgumentException("Input format <bing account key> <precision> <query>");
        }

        final String bingKey = args[0];
        final double targetPrecision = Double.parseDouble(args[1]);
        String query = args[2];

        BingHandler bingHandler = new BingHandler();
        bingHandler.init(bingKey, targetPrecision);
        bingHandler.query(query);
        BingResult bingResult = bingHandler.getBingResult();
        TreeMap<String, Double> q0 = new TreeMap<>();
        while (bingResult.getActualPrecision() < targetPrecision) {
            System.out.printf("Still below the desired precision of %.1f\n", targetPrecision);
            RocchioObject rocchioObj = new RocchioObject(bingResult.getList(),
                    bingResult.getRelevantList(), bingResult.getIrrelevantList(), bingResult.getLocalList(),
                    bingResult.getLocalRelList(), bingResult.getLocalIrrlist(), query, q0);
            query = rocchioObj.getNewQuery();
            q0 = rocchioObj.getQ0();
            bingHandler.query(query);
        }
        System.out.println("Desired precision reached, done");
    }
}
