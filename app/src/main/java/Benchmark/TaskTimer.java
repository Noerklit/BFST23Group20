package Benchmark;

import static Benchmark.BenchmarkUtility.convertMillis;

// It currently only prints to standard output. 
// Maybe we should save it to a log file at some point and potentially change the System.out.print... statements.
public class TaskTimer {

    private static long startTime;

    private static int taskCount = 0;

    private static long taskStart;
    private static String taskName;
    private static int taskSubCount = 0;

    private static long currentTime;

    public static void recordTask(String name) {
        currentTime = System.currentTimeMillis();

        if (taskCount == 0) {
            logSeperator();
            startTime = currentTime;
        } else {
            logTaskInfo();
        }

        taskStart = currentTime;
        taskName = name;
        taskSubCount = 0;

        taskCount++;
    }

    public static void endTasks() {
        if (taskCount == 0) return;

        long endTime = currentTime = System.currentTimeMillis();
        logTaskInfo();

        taskSubCount = 0;

        if (taskCount >= 2) {
            logSeperator();
            System.out.printf("%s - Total\n", convertMillis(endTime - startTime));
        }
        logSeperator();

        taskCount = 0;
    }

    public static void incrementSubtask() {
        taskSubCount++;
    }
    
    private static void logSeperator() {
        System.out.println("-----------------");
    }

    private static void logTaskInfo() {
        long deltaTime = currentTime - taskStart;
        String deltaTimeS = convertMillis(deltaTime);

        if (taskSubCount == 0) {
            System.out.printf("%s - %s\n", deltaTimeS, taskName);
        } else {
            long averageTaskTime = Math.round((double)deltaTime / taskSubCount);
            String averageTaskTimeS = convertMillis(averageTaskTime);

            if (averageTaskTime == 0) {
                System.out.printf("%s - %s (count: %,d)\n",
                    deltaTimeS, taskName, taskSubCount);
            } else {
                // This might not be usefull
                System.out.printf("%s - %s (count: %,d) (average subtask time: %s)\n", 
                    deltaTimeS, taskName, taskSubCount, averageTaskTimeS);
            }
        }
    }
}
