package com.falco.testsupport;

import com.google.common.base.Stopwatch;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class PerformanceUtils {

    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance();


    public static void runSpeedCheck(String msg, Runnable r, int runningTimeSecsLimit, int threadsCount) {
        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            List<Callable<Result>> callablesList = IntStream
                    .rangeClosed(1, threadsCount)
                    .mapToObj(i -> runForNSec(r, runningTimeSecsLimit))
                    .collect(toList());
            List<Future<Result>> futures = Executors.newFixedThreadPool(threadsCount).invokeAll(callablesList);
            int success = 0;
            int errors = 0;
            for (Future<Result> resultFuture : futures) {
                Result result = resultFuture.get();
                success += result.success;
                errors += result.errors;
            }

            long runningTimeSecs = stopwatch.elapsed(TimeUnit.SECONDS);
            System.out.printf("%-" + 60 + "s %s", msg + " speed", numberFormat.format(success / runningTimeSecs));
            System.out.printf("%s %s%n", " tps, errors percentage:", success + errors != 0 ? numberFormat.format(100 * errors / (success + errors)) : "n/a");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void logTime(String msg, String value) {
        logTime(msg, value, 60);
    }

    public static void logTime(String msg, int value) {
        logTime(msg, numberFormat.format(value), 60);
    }

    public static void logTime(String msg, String val, int padding) {
        l(msg, val, padding);
    }

    private static PrintStream l(String msg, String val, int padding) {
        return System.out.printf("%-" + padding + "s %s%n", msg, val);
    }

    private static Callable<Result> runForNSec(final Runnable r, int runningTimeSecs) {
        return () -> {
            Stopwatch stopwatch = Stopwatch.createStarted();
            Result result = new Result();
            while (stopwatch.elapsed(TimeUnit.SECONDS) < runningTimeSecs) {
                try {
                    r.run();
                    result.success();
                } catch (Exception e) {
                    result.error();
                }
            }
            return result;
        };
    }

    private static class Result {
        private int errors;
        private int success;

        public void success() {
            success++;
        }

        public void error() {
            errors++;
        }
    }
}
