package Benchmark;

public class BenchmarkUtility {
    public static String convertMillis(long millis) {
        long mi = millis % 1000;
        long s = (millis / 1000) % 60;
        long m = (millis / 60_000) % 60; // 60 * 1000 = 60_000
        long h = (millis / 3_600_000); // % 24; // 60 * 60 * 1000 = 3_600_000
        if (h == 0) {
            return String.format("%02d:%02d,%03d", m, s, mi);
        } else {
            return String.format("%d:%02d:%02d,%03d", h, m, s, mi);
        }
    }
}
