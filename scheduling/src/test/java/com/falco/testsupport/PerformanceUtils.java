package com.falco.testsupport;

import com.falco.appointment.scheduling.ReservationPerformanceTest;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractFuture;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

public class PerformanceUtils {

    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance();


    public static void runSpeedCheck(Runnable r, String s, int runningTimeSecs, int threadsCount) {
        try {

            Callable<Result>[] callables = new Callable[threadsCount];
            Arrays.fill(callables, aaa(r, runningTimeSecs));
            List<Callable<Result>> callablesList = asList(callables);
            List<Future<Result>> futures = null;
            if (threadsCount > 1) {
                futures = Executors
                        .newFixedThreadPool(threadsCount)
                        .invokeAll(callablesList);
            } else {
                futures = asList(
                        new AbstractFuture<Result>() {
                            @Override
                            public Result get() throws InterruptedException, ExecutionException {
                                try {
                                    return aaa(r, runningTimeSecs).call();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                );
            }
            int success = 0;
            int errors = 0;
            for (Future<Result> resultFuture : futures) {
                Result result = resultFuture.get();
                success += result.success;
                errors += result.errors;
            }

//            l(s + " speed", numberFormat.format(success / runningTimeSecs), 60);
            System.out.printf("%-" + 60 + "s %s", s + " speed", numberFormat.format(success / runningTimeSecs));
            System.out.printf("%s %s%n", " tps, errors percentage:", success + errors != 0 ? numberFormat.format(100 * errors / (success + errors)) : "n/a");
//            logTime(s + " speed", success / runningTimeSecs);
//            logTime(s + " successes/ errors ", numberFormat.format(success) + "/" + numberFormat.format(errors));
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

    private static Callable<Result> aaa(final Runnable r, int runningTimeSecs) {
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
