package lk.jiat.global.util;

import lk.jiat.global.entity.Carrier;
import lk.jiat.global.entity.Vendor;
import lk.jiat.global.entity.Warehouse;
import lk.jiat.global.service.ShipmentServiceLocal;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TrackingNumberGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");

    /**
     * Generate a tracking number based on:
     * - Warehouse code (first 3 letters of warehouse name)
     * - Vendor code (first 3 letters of vendor name)
     * - Carrier code (first 3 letters of carrier name OR service type)
     * - Timestamp (yyMMddHHmmss)
     * Format: WHV-VENDOR-CARRIER-TIMESTAMP
     * Example: COL-HAY-SEA-20260823143045
     */
    public static String generateTrackingNumber(Warehouse warehouse, Vendor vendor, Carrier carrier) {
        // Warehouse code - first 3 letters, uppercase
        String warehouseCode = getCode(warehouse.getName(), 3);

        // Vendor code - first 3 letters, uppercase
        String vendorCode = getCode(vendor.getVendorName(), 3);

        // Carrier code - use service type if available, otherwise carrier name
        String carrierCode;
        if (carrier != null) {
            if (carrier.getServiceType() != null && !carrier.getServiceType().isEmpty()) {
                carrierCode = getCode(carrier.getServiceType(), 3);
            } else {
                carrierCode = getCode(carrier.getCarrierName(), 3);
            }
        } else {
            carrierCode = "NA"; // No carrier
        }

        // Timestamp: yyMMddHHmmss
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));

        return String.format("%s-%s-%s-%s", warehouseCode, vendorCode, carrierCode, timestamp);
    }

    /**
     * Generate a shorter tracking number with UUID suffix for uniqueness.
     * Format: WHV-VENDOR-CARRIER-XXXXXXXX (8 char UUID short)
     */
    public static String generateTrackingNumberShort(Warehouse warehouse, Vendor vendor, Carrier carrier) {
        String warehouseCode = getCode(warehouse.getName(), 3);
        String vendorCode = getCode(vendor.getVendorName(), 3);

        String carrierCode;
        if (carrier != null) {
            if (carrier.getServiceType() != null && !carrier.getServiceType().isEmpty()) {
                carrierCode = getCode(carrier.getServiceType(), 3);
            } else {
                carrierCode = getCode(carrier.getCarrierName(), 3);
            }
        } else {
            carrierCode = "NA";
        }

        // 8 character unique ID
        String uniqueId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return String.format("%s-%s-%s-%s", warehouseCode, vendorCode, carrierCode, uniqueId);
    }

    /**
     * Get first N characters of a string, uppercase, removing special characters.
     */
    private static String getCode(String input, int length) {
        if (input == null || input.isEmpty()) {
            return "XXX";
        }

        // Remove special characters and extra spaces
        String cleaned = input.replaceAll("[^a-zA-Z0-9]", " ").trim();

        // Get first N characters from each word
        String[] words = cleaned.split("\\s+");
        StringBuilder code = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                code.append(word.charAt(0));
                if (code.length() >= length) {
                    break;
                }
            }
        }

        // If code is shorter than length, pad with first letters
        while (code.length() < length) {
            code.append(input.charAt(code.length() % input.length()));
        }

        return code.toString().toUpperCase().substring(0, length);
    }

    /**
     * Check if tracking number already exists (to be used with Service).
     */
    public static boolean isTrackingNumberUnique(String trackingNumber, ShipmentServiceLocal shipmentService) {
        if (shipmentService == null) {
            return true;
        }
        return shipmentService.isTrackingNumberUnique(trackingNumber, null);
    }
}