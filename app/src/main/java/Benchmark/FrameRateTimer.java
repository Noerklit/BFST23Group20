package Benchmark;

//import static Benchmark.BenchmarkUtility.convertMillis;

public class FrameRateTimer {
    private static int frameCount = 0;

    private static long startFrameTime;
    private static double savedZoomLevel;
    private static int savedFidelity;

    public static void startFrame(double zoomLevel, int fidelity) { // zoom level...
        frameCount++;
        savedZoomLevel = zoomLevel;
        savedFidelity = fidelity;
        startFrameTime = System.currentTimeMillis();
    }
    
    public static void endFrame() {
        logSeperator();
        System.out.printf("Frame: %s\n", frameCount);
        System.out.printf("Zoom level: %.2f, fidelity: %s\n", savedZoomLevel, savedFidelity);
        long endFrameTime = System.currentTimeMillis();
        long frameTime = endFrameTime - startFrameTime;
        System.out.printf("%03d ms - frametime\n", frameTime);
        logSeperator();
    }

    private static void logSeperator() {
        System.out.println("------------------");
    }
}
