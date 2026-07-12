package LLD.CardBrandDetectorApp;

import java.util.List;

import LLD.CardBrandDetectorApp.model.CardBrand;

public class CardBrandDetector {

    List<CardBrand> brands;

    public CardBrandDetector(List<CardBrand> brands) {
        this.brands = brands;
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
