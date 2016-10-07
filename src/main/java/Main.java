public class Main {

    public static void main(String[] args) {
        //Input format <bing account key> <precision> <query>
        if (args.length != 3) {
            throw new IllegalArgumentException("Input format <bing account key> <precision> <query>");
        }

        final String bingKey = args[0];
        final double targetPrecision = Double.parseDouble(args[1]);
        String query = args[2];

        BingHandler engine = new BingHandler();
        engine.init(bingKey, targetPrecision);
        engine.query(query);
        BingResult bingResult = engine.getBingResult();
        while (bingResult.getActualPrecision() < targetPrecision) {
            System.out.printf("Still below the desired precision of %.1f\n", targetPrecision);
            WordVector fullList = new WordVector(bingResult.getList());
            WordVector relList = new WordVector(bingResult.getRelevantList());
            WordVector irrList = new WordVector(bingResult.getIrrelevantList());
            RocchioObject rocchioObj = new RocchioObject(fullList, relList, irrList, query);
//            query = rocchioObj
            engine.query(query);
        }
        System.out.println("Desired precision reached, done");
    }
}
