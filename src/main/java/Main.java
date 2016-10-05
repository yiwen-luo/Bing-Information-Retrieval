public class Main {


    public static void main(String[] args) {
        //Input format <bing account key> <precision> <query>
        if (args.length != 3) {
            throw new IllegalArgumentException("Input format <bing account key> <precision> <query>");
        }

        RetrievalEngine engine = new RetrievalEngine();
        engine.init(args[0], Double.parseDouble(args[1]));
        engine.query(args[2]);

    }

}
