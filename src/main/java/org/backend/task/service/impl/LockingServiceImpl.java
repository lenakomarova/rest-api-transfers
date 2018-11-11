package org.backend.task.service.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.backend.task.service.LockingService;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.LongStream;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class LockingServiceImpl implements LockingService {
    private final Map<Long, Lock> ID_LOCKS_MAP = new ConcurrentHashMap<>();

    @SneakyThrows
    @Override
    public <T> T invokeConcurrently(Callable<T> task, long ... lockByIds) {
        long[] ids = lockByIds.clone();
        Arrays.sort(ids);

        try {
            LongStream.of(ids)
                    .mapToObj(id -> ID_LOCKS_MAP.computeIfAbsent(id, notFoundId -> new ReentrantLock(true)))
                    .forEach(Lock::lock);

            return task.call();
        } finally {
            LongStream.of(ids)
                    .mapToObj(ID_LOCKS_MAP::get)
                    .forEach(Lock::unlock);
        }
    }
}
