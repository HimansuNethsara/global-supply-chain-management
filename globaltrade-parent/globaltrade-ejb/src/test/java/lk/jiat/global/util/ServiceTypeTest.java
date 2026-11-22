package lk.jiat.global.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTypeTest {

    @Test
    @DisplayName("Should have correct service types defined")
    void serviceTypes_ShouldContainExpectedTypes() {
        List<String> names = ServiceType.getAllDisplayNames();

        assertTrue(names.contains("Air Freight"));
        assertTrue(names.contains("Sea Freight"));
        assertTrue(names.contains("Ground Shipping"));
        assertTrue(names.contains("Express"));
        assertTrue(names.contains("Standard"));
        assertTrue(names.contains("Intermodal Transport"));
    }

    @ParameterizedTest
    @DisplayName("Should map display name to correct enum")
    @CsvSource({
            "Air Freight, AIR_FREIGHT",
            "Sea Freight, SEA_FREIGHT",
            "Ground Shipping, GROUND_SHIPPING",
            "Express, EXPRESS",
            "Standard, STANDARD"
    })
    void fromDisplayName_ShouldMapCorrectly(String displayName, String expectedEnum) {
        ServiceType result = ServiceType.fromDisplayName(displayName);
        assertNotNull(result);
        assertEquals(expectedEnum, result.name());
    }

    @Test
    @DisplayName("Should return null for invalid display name")
    void fromDisplayName_ShouldReturnNullForInvalid() {
        ServiceType result = ServiceType.fromDisplayName("Invalid Service");
        assertNull(result);
    }

    @ParameterizedTest
    @DisplayName("Should have correct average speeds for each service type")
    @CsvSource({
            "Air Freight, 800.0",
            "Sea Freight, 30.0",
            "Ground Shipping, 60.0",
            "Express, 100.0",
            "Standard, 50.0",
            "Intermodal Transport, 45.0"
    })
    void getAvgSpeedKmh_ShouldReturnCorrectSpeed(String displayName, double expectedSpeed) {
        ServiceType type = ServiceType.fromDisplayName(displayName);
        assertNotNull(type);
        assertEquals(expectedSpeed, type.getAvgSpeedKmh(), 0.01);
    }

    @Test
    @DisplayName("Should return all display names")
    void getAllDisplayNames_ShouldReturnAllNames() {
        List<String> names = ServiceType.getAllDisplayNames();
        assertEquals(ServiceType.values().length, names.size());
        assertTrue(names.contains("Air Freight"));
        assertTrue(names.contains("Sea Freight"));
    }
}