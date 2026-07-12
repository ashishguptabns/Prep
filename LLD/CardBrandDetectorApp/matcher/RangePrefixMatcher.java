package LLD.CardBrandDetectorApp.matcher;

import java.util.List;

public class RangePrefixMatcher implements IMatcher {

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
