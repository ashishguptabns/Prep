package LLD.CardBrandDetectorApp.matcher;

public class PrefixLengthMatcher implements IMatcher {

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
