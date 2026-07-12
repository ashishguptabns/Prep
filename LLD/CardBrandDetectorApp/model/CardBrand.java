package LLD.CardBrandDetectorApp.model;

import java.util.ArrayList;
import java.util.List;

import LLD.CardBrandDetectorApp.matcher.IMatcher;

public class CardBrand {

    private final String name;
    private final List<IMatcher> matchers;

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
