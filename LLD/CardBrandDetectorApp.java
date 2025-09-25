package LLD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

interface IMatcher {
    public boolean matches(String number);
}

class CardBrand {
    private final String name;
    private List<IMatcher> matchers;

    public CardBrand(String name) {
        this.name = name;
        this.matchers = new ArrayList<>();
    }

    public boolean matches(String number) {
        for (IMatcher matcher : matchers) {
            if (matcher.matches(number)) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return this.name;
    }

    public void addMatcher(IMatcher matcher) {
        this.matchers.add(matcher);
    }
}

class PrefixLengthMatcher implements IMatcher {
    private final String prefix;
    private final int length;

    public PrefixLengthMatcher(String prefix, int length) {
        this.prefix = prefix;
        this.length = length;
    }

    @Override
    public boolean matches(String number) {
        return number.startsWith(this.prefix) && number.length() == this.length;
    }
}

class RangePrefixMatcher implements IMatcher {
    private final int startRange;
    private final int endRange;
    private final int prefixLength;
    private final List<Integer> validLengths;

    public RangePrefixMatcher(int startRange, int endRange, int prefixLength, List<Integer> validLengths) {
        this.startRange = startRange;
        this.endRange = endRange;
        this.prefixLength = prefixLength;
        this.validLengths = validLengths;
    }

    @Override
    public boolean matches(String number) {
        if (number.length() < this.prefixLength) {
            return false;
        }
        int prefix = Integer.parseInt(number.substring(0, this.prefixLength));
        return prefix >= this.startRange && prefix <= this.endRange && this.validLengths.contains(number.length());
    }

}

class CardBrandDetector {
    List<CardBrand> brands = new ArrayList<>();

    public CardBrandDetector() {
        initBrands();
    }

    private void initBrands() {
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

    public String detect(String number) {
        for (CardBrand brand : brands) {
            if (brand.matches(number)) {
                return brand.getName();
            }
        }
        return "unknown";
    }
}

public class CardBrandDetectorApp {
    public static void main(String[] a) {
        CardBrandDetector detector = new CardBrandDetector();
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
