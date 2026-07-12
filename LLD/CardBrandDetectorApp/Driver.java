package LLD.CardBrandDetectorApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import LLD.CardBrandDetectorApp.matcher.PrefixLengthMatcher;
import LLD.CardBrandDetectorApp.matcher.RangePrefixMatcher;
import LLD.CardBrandDetectorApp.model.CardBrand;

public class Driver {

    static List<CardBrand> brands = new ArrayList<>();

    private static void initBrands() {
        CardBrand visa = new CardBrand("Visa");
        visa.addMatcher(new PrefixLengthMatcher("4", 13));
        visa.addMatcher(new PrefixLengthMatcher("4", 16));
        visa.addMatcher(new PrefixLengthMatcher("4", 19));
        brands.add(visa);

        CardBrand masterCard = new CardBrand("MasterCard");
        masterCard.addMatcher(new RangePrefixMatcher(51, 55, 2, Arrays.asList(16)));
        masterCard.addMatcher(new RangePrefixMatcher(2221, 2720, 4, Arrays.asList(16)));
        brands.add(masterCard);
    }

    public static void main(String[] a) {
        initBrands();
        CardBrandDetector detector = new CardBrandDetector(brands);
        System.out.println(detector.detect("4111111111111")); // Visa (13-digit)
        System.out.println(detector.detect("4111111111111111")); // Visa (16-digit)
        System.out.println(detector.detect("4111111111111111111"));// Visa (19-digit)
        System.out.println(detector.detect("5500000000000004")); // MasterCard (51-55)
        System.out.println(detector.detect("2221000000000009")); // MasterCard (2221–2720)
        System.out.println(detector.detect("6011000000000004")); // Discover
        System.out.println(detector.detect("340000000000009")); // Amex
        System.out.println(detector.detect("1234567890123456")); // Unknown
    }
}
