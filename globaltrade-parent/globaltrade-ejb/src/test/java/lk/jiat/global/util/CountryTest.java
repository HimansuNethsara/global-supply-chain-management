package lk.jiat.global.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CountryTest {

    @Test
    @DisplayName("Should have correct countries defined")
    void countries_ShouldContainExpectedCountries() {
        List<String> countries = Country.getAllDisplayNames();

        assertTrue(countries.contains("Sri Lanka"));
        assertTrue(countries.contains("Singapore"));
        assertTrue(countries.contains("UAE"));
        assertTrue(countries.contains("USA"));
        assertTrue(countries.contains("India"));
        assertTrue(countries.contains("China"));
        assertTrue(countries.contains("Japan"));
        assertTrue(countries.contains("Germany"));
    }

    @ParameterizedTest
    @DisplayName("Should map display name to correct enum")
    @CsvSource({
            "Sri Lanka, SRI_LANKA",
            "Singapore, SINGAPORE",
            "UAE, UAE",
            "USA, USA",
            "India, INDIA",
            "China, CHINA",
            "Japan, JAPAN",
            "Germany, GERMANY"
    })
    void fromDisplayName_ShouldMapCorrectly(String displayName, String expectedEnum) {
        Country result = Country.fromDisplayName(displayName);
        assertNotNull(result);
        assertEquals(expectedEnum, result.name());
    }

    @Test
    @DisplayName("Should return null for invalid country")
    void fromDisplayName_ShouldReturnNullForInvalid() {
        Country result = Country.fromDisplayName("Atlantis");
        assertNull(result);
    }

    @Test
    @DisplayName("Should return all display names")
    void getAllDisplayNames_ShouldReturnAllNames() {
        List<String> names = Country.getAllDisplayNames();
        assertEquals(Country.values().length, names.size());
        assertTrue(names.contains("Sri Lanka"));
    }
}