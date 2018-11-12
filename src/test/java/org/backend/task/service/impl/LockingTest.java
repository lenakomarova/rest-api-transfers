package org.backend.task.service.impl;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


public class LockingTest extends AbstractTest {

    @Test
    public void success() {
        Map<Long, AtomicBoolean> runningMap = new ConcurrentHashMap<>();

        Callable<Void> one = createCallable(runningMap, 1);
        Callable<Void> two = createCallable(runningMap, 2);
        Callable<Void> oneAndTwo = createCallable(runningMap, 1, 2);

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        executorService.submit(() -> lockingService.invokeConcurrently(one, 1));
        executorService.submit(() -> lockingService.invokeConcurrently(two, 2));
        executorService.submit(() -> lockingService.invokeConcurrently(oneAndTwo, 1, 2));

    }

    @Test(expected = Exception.class)
    public void fail() {
        lockingService.invokeConcurrently(() -> {
            throw new Exception();
        });
    }

    private Callable<Void> createCallable(Map<Long, AtomicBoolean> runningsMap, long ... ids) {
        return () -> {
            for (long id : ids) {
                Assert.assertTrue(runningsMap.computeIfAbsent(id, key -> new AtomicBoolean()).compareAndSet(false, true));
            }

            for (int i = 0; i < 1000; i ++) {
                for (long id : ids) {
                    Assert.assertTrue(runningsMap.get(id).get());
                }
            }

            for (long id : ids) {
                Assert.assertTrue(runningsMap.get(id).compareAndSet(true, false));
            }

            return null;
        };
    }
}
