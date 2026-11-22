package lk.jiat.global.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Country {
    AUSTRALIA("Australia"),
    BRAZIL("Brazil"),
    CANADA("Canada"),
    CHINA("China"),
    FRANCE("France"),
    GERMANY("Germany"),
    INDIA("India"),
    INDONESIA("Indonesia"),
    ITALY("Italy"),
    JAPAN("Japan"),
    MALAYSIA("Malaysia"),
    MEXICO("Mexico"),
    NETHERLANDS("Netherlands"),
    SAUDI_ARABIA("Saudi Arabia"),
    SINGAPORE("Singapore"),
    SOUTH_AFRICA("South Africa"),
    SOUTH_KOREA("South Korea"),
    SPAIN("Spain"),
    SRI_LANKA("Sri Lanka"),
    SWEDEN("Sweden"),
    SWITZERLAND("Switzerland"),
    THAILAND("Thailand"),
    TURKEY("Turkey"),
    UAE("UAE"),
    UK("UK"),
    USA("USA");

    private final String displayName;

    Country(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Country fromDisplayName(String displayName) {
        for (Country country : values()) {
            if (country.getDisplayName().equals(displayName)) {
                return country;
            }
        }
        return null;
    }

    public static List<String> getAllDisplayNames() {
        return Arrays.stream(values())
                .map(Country::getDisplayName)
                .collect(Collectors.toList());
    }
}