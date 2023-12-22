package kz.kaspi;

public class App {
    public static void main(String[] args)
    {
        Parser parser = new Parser();
        parser.initialize("https://kaspi.kz/shop/c/smartphones/?q= %3AavailableInZones%3AMagnum_ZONE1%3Acategory%3ASmartphones%3AmanufacturerName%3AApple&sort=relevance&sc=");
        parser.createFile("iPhones.json");
        parser.parse();

    }
}
