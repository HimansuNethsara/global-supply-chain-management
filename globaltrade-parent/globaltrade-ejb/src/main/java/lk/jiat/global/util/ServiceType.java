package lk.jiat.global.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ServiceType {
    AIR_FREIGHT("Air Freight", 800.0),
    COLD_CHAIN("Cold Chain Logistics", 50.0),
    CUSTOMS_BROKERAGE("Customs Brokerage", 0.0),
    EXPRESS("Express", 100.0),
    FREIGHT_FORWARDING("Freight Forwarding", 40.0),
    GROUND_SHIPPING("Ground Shipping", 60.0),
    INTERMODAL("Intermodal Transport", 45.0),
    SEA_FREIGHT("Sea Freight", 30.0),
    STANDARD("Standard", 50.0);

    private final String displayName;
    private final double avgSpeedKmh;

    ServiceType(String displayName, double avgSpeedKmh) {
        this.displayName = displayName;
        this.avgSpeedKmh = avgSpeedKmh;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getAvgSpeedKmh() {
        return avgSpeedKmh;
    }

    public static ServiceType fromDisplayName(String displayName) {
        for (ServiceType service : values()) {
            if (service.getDisplayName().equalsIgnoreCase(displayName)) {
                return service;
            }
        }
        return null;
    }

    public static List<String> getAllDisplayNames() {
        return Arrays.stream(values())
                .map(ServiceType::getDisplayName)
                .collect(Collectors.toList());
    }
}