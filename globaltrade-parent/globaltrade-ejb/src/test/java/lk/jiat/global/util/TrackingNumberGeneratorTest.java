package lk.jiat.global.util;

import lk.jiat.global.entity.Carrier;
import lk.jiat.global.entity.Vendor;
import lk.jiat.global.entity.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class TrackingNumberGeneratorTest {

    private Warehouse warehouse;
    private Vendor vendor;
    private Carrier carrier;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse("Colombo Logistics Hub", "Colombo", "Sri Lanka", 10000);
        vendor = new Vendor("Hayleys Logistics", "hayleys@logistics.com", "Sri Lanka");
        carrier = new Carrier("DHL", "Air Freight", "info@dhl.com");
    }

    @Test
    @DisplayName("Should generate tracking number with correct format")
    void generateTrackingNumber_ShouldReturnCorrectFormat() {
        String trackingNumber = TrackingNumberGenerator.generateTrackingNumber(warehouse, vendor, carrier);

        assertNotNull(trackingNumber);
        assertTrue(trackingNumber.matches("^[A-Z0-9]{3}-[A-Z0-9]{3}-[A-Z0-9]{3}-\\d{12}$"),
                "Format should be: WHV-VEN-CAR-YYMMDDHHmmss");
    }

    @Test
    @DisplayName("Should include warehouse code as first letters of each word")
    void generateTrackingNumber_ShouldIncludeWarehouseCode() {
        String trackingNumber = TrackingNumberGenerator.generateTrackingNumber(warehouse, vendor, carrier);
        String[] parts = trackingNumber.split("-");

        assertEquals("CLH", parts[0], "Warehouse code should be 'CLH' for Colombo Logistics Hub");
    }

    @Test
    @DisplayName("Should include vendor code as first letters of each word")
    void generateTrackingNumber_ShouldIncludeVendorCode() {
        String trackingNumber = TrackingNumberGenerator.generateTrackingNumber(warehouse, vendor, carrier);
        String[] parts = trackingNumber.split("-");

        assertEquals("HLY", parts[1], "Vendor code should be 'HLY' for Hayleys Logistics");
    }

    @Test
    @DisplayName("Should use service type for carrier code if available")
    void generateTrackingNumber_ShouldUseServiceTypeForCarrierCode() {
        Carrier airCarrier = new Carrier("Emirates", "Air Freight", "info@emirates.com");
        Carrier seaCarrier = new Carrier("MSC", "Sea Freight", "info@msc.com");

        String airTracking = TrackingNumberGenerator.generateTrackingNumber(warehouse, vendor, airCarrier);
        String seaTracking = TrackingNumberGenerator.generateTrackingNumber(warehouse, vendor, seaCarrier);

        String[] airParts = airTracking.split("-");
        String[] seaParts = seaTracking.split("-");

        assertEquals("AFR", airParts[2], "Air Freight should give 'AFR' code");
        assertEquals("SFA", seaParts[2], "Sea Freight should give 'SFA' code");
    }

    @Test
    @DisplayName("Should use carrier name for code if service type is null or empty")
    void generateTrackingNumber_ShouldUseCarrierNameWhenServiceTypeNull() {
        Carrier noServiceCarrier = new Carrier("DHL", null, "info@dhl.com");

        String trackingNumber = TrackingNumberGenerator.generateTrackingNumber(warehouse, vendor, noServiceCarrier);
        String[] parts = trackingNumber.split("-");

        assertEquals("DHL", parts[2], "Should use carrier name when service type is null");
    }

    @Test
    @DisplayName("Should use carrier name for code if service type is empty")
    void generateTrackingNumber_ShouldUseCarrierNameWhenServiceTypeEmpty() {
        Carrier emptyServiceCarrier = new Carrier("FedEx", "", "info@fedex.com");

        String trackingNumber = TrackingNumberGenerator.generateTrackingNumber(warehouse, vendor, emptyServiceCarrier);
        String[] parts = trackingNumber.split("-");

        assertEquals("FED", parts[2], "Should use carrier name when service type is empty");
    }

    @Test
    @DisplayName("Should use 'NA' for carrier code if carrier is null")
    void generateTrackingNumber_ShouldUseNAWhenCarrierNull() {
        String trackingNumber = TrackingNumberGenerator.generateTrackingNumber(warehouse, vendor, null);
        String[] parts = trackingNumber.split("-");

        assertEquals("NA", parts[2], "Should use 'NA' when carrier is null");
    }

    @Test
    @DisplayName("Should generate unique tracking numbers using timestamp")
    void generateTrackingNumber_ShouldGenerateUniqueNumbers() {
        String tracking1 = TrackingNumberGenerator.generateTrackingNumber(warehouse, vendor, carrier);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String tracking2 = TrackingNumberGenerator.generateTrackingNumber(warehouse, vendor, carrier);

        assertNotEquals(tracking1, tracking2, "Tracking numbers should be unique (different timestamps)");
    }

    @Test
    @DisplayName("Should handle special characters in names")
    void generateTrackingNumber_ShouldHandleSpecialCharacters() {
        Warehouse specialWh = new Warehouse("A&B Logistics Ltd.", "New York", "USA", 5000);
        Vendor specialVendor = new Vendor("Smith & Co. (Pvt) Ltd.", "smith@co.com", "UK");

        String trackingNumber = TrackingNumberGenerator.generateTrackingNumber(specialWh, specialVendor, carrier);

        assertNotNull(trackingNumber);
        assertTrue(trackingNumber.matches("^[A-Z0-9]{3}-[A-Z0-9]{3}-[A-Z0-9]{3}-\\d{12}$"),
                "Should handle special characters in names");
    }

    @Test
    @DisplayName("Should generate short tracking number with 8 char UUID")
    void generateTrackingNumberShort_ShouldReturnShortFormat() {
        String trackingNumber = TrackingNumberGenerator.generateTrackingNumberShort(warehouse, vendor, carrier);

        assertNotNull(trackingNumber);
        assertTrue(trackingNumber.matches("^[A-Z0-9]{3}-[A-Z0-9]{3}-[A-Z0-9]{3}-[A-Z0-9]{8}$"),
                "Short format should be: WHV-VEN-CAR-XXXXXXXX");
    }

    @Test
    @DisplayName("Should generate short tracking number with unique UUID")
    void generateTrackingNumberShort_ShouldGenerateUniqueNumbers() {
        String tracking1 = TrackingNumberGenerator.generateTrackingNumberShort(warehouse, vendor, carrier);
        String tracking2 = TrackingNumberGenerator.generateTrackingNumberShort(warehouse, vendor, carrier);

        assertNotEquals(tracking1, tracking2, "Short tracking numbers should be unique (different UUIDs)");
    }

    @Test
    @DisplayName("Should pad codes to 3 characters when input has fewer words")
    void generateTrackingNumber_ShouldPadToThreeCharacters() {
        Warehouse singleWordWh = new Warehouse("Colombo", "Colombo", "Sri Lanka", 10000);

        String trackingNumber = TrackingNumberGenerator.generateTrackingNumber(singleWordWh, vendor, carrier);
        String[] parts = trackingNumber.split("-");

        assertEquals("COL", parts[0], "Should pad single word to 3 characters");
    }

    @Test
    @DisplayName("Should handle single letter words correctly")
    void generateTrackingNumber_ShouldHandleSingleLetterWords() {
        Warehouse singleLetterWh = new Warehouse("A B C Logistics", "Location", "Country", 10000);

        String trackingNumber = TrackingNumberGenerator.generateTrackingNumber(singleLetterWh, vendor, carrier);
        String[] parts = trackingNumber.split("-");

        assertEquals("ABC", parts[0], "Should handle single letter words");
    }
}