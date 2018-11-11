package org.backend.task.service;


import java.util.concurrent.Callable;

public interface LockingService {
    <T> T invokeConcurrently(Callable<T> task, long ... lockByIds);
}
