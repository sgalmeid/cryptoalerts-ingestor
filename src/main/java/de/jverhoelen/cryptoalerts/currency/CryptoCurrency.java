package de.jverhoelen.cryptoalerts.currency;

public enum CryptoCurrency {
    LTC("Litecoin"),
    DCR("Decred"),
    BTC("Bitcoin"),
    ETC("EthereumClassic"),
    ETH("Ethereum"),
    XRP("Ripple"),
    DASH("Dash"),
    XMR("Monero"),
    BTS("BitShares"),
    DOGE("Dogecoin"),
    GNT("Golem"),
    VTC("Vertcoin"),
    ZEC("zCash"),
    AMP("SynereoAMP"),
    ARDR("Ardor"),
    STEEM("STEEM"),
    STRAT("Stratis"),
    STR("Stellar"),
    SYS("Syscoin"),
    XVC("Vcash"),
    XMP("Primecoin"),
    NEM("NEM"),
    XCP("Counterparty"),
    XBC("BitcoinPlus"),
    VRC("VeriCoin"),
    VIA("Viacoin"),
    SJCX("StorjcoinX"),
    SC("Siacoin"),
    SBD("SteemDollars"),
    RIC("RieCoin"),
    REP("Augur"),
    RADS("Radium"),
    PPC("Peercoin"),
    POT("PotCoin"),
    PINK("PinkCoin"),
    PASC("PascalCoin"),
    OMNI("Omni"),
    NXT("NXT"),
    NXC("Nexium"),
    NOTE("DNotes"),
    NMC("Namecoin"),
    NEOS("Neoscoin"),
    NAV("Navcoin"),
    NAUT("NautilusCoin"),
    MAID("MaidSafeCoin"),
    LSK("Lisk"),
    LBC("LBRYCredits"),
    HUC("Huntercoin"),
    GRC("GridcoinResearch"),
    GNO("Gnosis"),
    FLO("FlorinCoin"),
    FLDC("FoldingCoin"),
    FCT("Factom"),
    EXP("Expanse"),
    EMC2("Einsteinium"),
    DGB("DigiByte"),
    CLAM("CLAMS"),
    BURST("Burst"),
    BTM("Bitmark"),
    BTCD("BitcoinDark"),
    BLK("BlackCoin"),
    BELA("Belacoin"),
    BCY("BitCrystals"),
    BCN("Bytecoin"),
    GAME("GameCredits");

    private String fullName;

    CryptoCurrency(String fullName) {
        this.fullName = fullName;
    }

    public static CryptoCurrency byFullName(String fullName) {
        for (CryptoCurrency currency : values()) {
            if (currency.getFullName().toLowerCase().equals(fullName.toLowerCase())) {
                return currency;
            }
        }

        return null;
    }

    public static CryptoCurrency byShortName(String shortName) {
        for (CryptoCurrency currency : values()) {
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
