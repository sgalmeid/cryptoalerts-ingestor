package de.jverhoelen.cryptoalerts.currency;

public enum ExchangeCurrency {
    USDT("$"),
    BTC("BTC");

    private String fullName;

    ExchangeCurrency(String fullName) {
        this.fullName = fullName;
    }

    public static ExchangeCurrency byFullName(String shortName) {
        for (ExchangeCurrency currency : values()) {
            if (currency.getFullName().toLowerCase().equals(shortName.toLowerCase())) {
                return currency;
            }
        }

        return null;
    }

    public static ExchangeCurrency byShortName(String shortName) {
        for (ExchangeCurrency currency : values()) {
            if (currency.name().toLowerCase().equals(shortName.toLowerCase())) {
                return currency;
            }
        }

        return null;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
