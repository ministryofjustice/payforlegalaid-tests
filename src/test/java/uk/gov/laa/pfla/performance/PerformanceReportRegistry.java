package uk.gov.laa.pfla.performance;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;

public class PerformanceReportRegistry {

    private static List<ReportsGet200ResponseReportListInner> reports = List.of();

    // Hardcoded known report IDs by size and format
    // Update these UUIDs to match your target environment (dev/UAT/prod)
    private static final Map<String, String> REPORT_IDS = Map.of(
            "small-csv",   "f46b4d3d-c100-429a-bf9a-6c3305dbdbf5",
            "medium-csv",  "f46b4d3d-c100-429a-bf9a-6c3305dbdbf8",
            "large-csv",   "f46b4d3d-c100-429a-bf9a-6c3305dbdbfb",
            "small-excel", "f46b4d3d-c100-429a-bf9a-223305dbdbfb",
            "medium-excel","7073dd13-e325-4863-a05c-a049a815d1f7",
            "large-excel", "a017241a-359f-4fdb-a0cd-7f28f1946ef1"
    );

    public static void populate(List<ReportsGet200ResponseReportListInner> loaded) {
        reports = loaded;
        System.out.println("DEBUG: PerformanceReportRegistry loaded " + loaded.size() + " reports");
        for (int i = 0; i < loaded.size(); i++) {
            System.out.println("DEBUG: Report[" + i + "] = " + loaded.get(i).getReportName());
        }
    }

    public static Optional<String> getReportIdBySizeAndFormat(String size, String format) {
        String key = size.toLowerCase() + "-" + format.toLowerCase();
        System.out.println("DEBUG: Looking up report for key: " + key);

        String id = REPORT_IDS.get(key);
        if (id == null) {
            System.out.println("DEBUG: No report found for key: " + key);
            return Optional.empty();
        }

        System.out.println("DEBUG: Found report ID: " + id + " for key: " + key);
        return Optional.of(id);
    }

    // Kept for backwards compatibility with any existing usages
    public static Optional<String> getReportIdBySize(String size) {
        if (reports.isEmpty()) return Optional.empty();

        return switch (size.toLowerCase()) {
            case "small"  -> Optional.of(reports.get(0).getId().toString());
            case "medium" -> Optional.of(reports.get(reports.size() / 2).getId().toString());
            case "large"  -> Optional.of(reports.get(reports.size() - 1).getId().toString());
            default -> Optional.empty();
        };
    }
}