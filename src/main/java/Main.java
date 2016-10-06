public class Main {

    public static void main(String[] args) {
        //Input format <bing account key> <precision> <query>
        if (args.length != 3) {
            throw new IllegalArgumentException("Input format <bing account key> <precision> <query>");
        }

        final String bingKey = args[0];
        final double targetPrecision = Double.parseDouble(args[1]);
        String query = args[2];

        RetrievalEngine engine = new RetrievalEngine();
        engine.init(bingKey, targetPrecision);
        engine.query(query);
        while (engine.bingResult.actualPrecision < targetPrecision) {
            System.out.printf("Still below the desired precision of %.1f\n", targetPrecision);
            // TODO: Update query here
            engine.query(query);
        }
        System.out.println("Desired precision reached, done");
    }
}
