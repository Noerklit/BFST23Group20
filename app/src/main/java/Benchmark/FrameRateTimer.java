package Benchmark;

//import static Benchmark.BenchmarkUtility.convertMillis;

import javafx.scene.text.Text;

public class FrameRateTimer {
    private static int frameCount = 0;

    private static long startFrameTime;
    private static double savedZoomLevel;
    private static int savedFidelity;

    private static Text fpsText;

    public static void startFrame(double zoomLevel, int fidelity) { // zoom level...
        frameCount++;
        savedZoomLevel = zoomLevel;
        savedFidelity = fidelity;
        startFrameTime = System.currentTimeMillis();
    }
    
    public static void endFrame() {
        long endFrameTime = System.currentTimeMillis();
        long frameTime = endFrameTime - startFrameTime;
        //logFrameInfo(frameTime);
        if (fpsText != null) {
            double fps = 1000./frameTime;
            String output = String.format("Fps: %.2f, fidelity: %s, zoom level: %.2f", fps, savedFidelity, savedZoomLevel);
            fpsText.setText(output);
        }
    }

    private static void logFrameInfo(long frameTime) {
        logSeperator();
        System.out.printf("Frame: %s\n", frameCount);
        System.out.printf("Zoom level: %.2f, fidelity: %s\n", savedZoomLevel, savedFidelity);
        System.out.printf("%03d ms - frametime\n", frameTime);
        logSeperator();
    }
    private static void logSeperator() {
        System.out.println("------------------");
    }

    public static void setFpsText(Text _fpsText){
        fpsText = _fpsText;
    }
}
