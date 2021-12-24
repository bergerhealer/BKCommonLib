package com.bergerkiller.bukkit.common.server.test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.bergerkiller.mountiplex.reflection.util.FastField;

/**
 * Temporarily disables, and then re-enables, background workers in Minecrafts
 * SystemUtil class (ExecutorService instances)
 */
class BackgroundWorkerDefuser implements AutoCloseable {
    private final List<WorkerInstance> workers = new ArrayList<WorkerInstance>();

    public static BackgroundWorkerDefuser start(Class<?> systemUtilsClass) {
        return new BackgroundWorkerDefuser(systemUtilsClass);
    }

    private BackgroundWorkerDefuser(Class<?> systemUtilsClass) {
        for (Field f : systemUtilsClass.getDeclaredFields()) {
            if (f.getType().equals(ExecutorService.class) && Modifier.isStatic(f.getModifiers())) {
                workers.add(new WorkerInstance(f));
            }
        }

        final ExecutorService noopExecutorService = new NOOPExecutorService();
        for (WorkerInstance worker : workers) {
            worker.set(noopExecutorService);
        }
    }

    @Override
    public void close() {
        for (WorkerInstance worker : workers) {
            worker.set(worker.originalValue);
        }
    }

    private static class WorkerInstance {
        public final FastField<ExecutorService> field;
        public final ExecutorService originalValue;

        public WorkerInstance(Field field) {
            this.field = new FastField<ExecutorService>(field);
            this.originalValue = this.field.get(null);
        }

        public void set(ExecutorService value) {
            field.set(null, value);
        }
    }

    private static class NOOPExecutorService implements ExecutorService {

        @Override
        public void execute(Runnable command) {
            //No-op!
        }

        @Override
        public void shutdown() {
        }

        @Override
        public List<Runnable> shutdownNow() {
            return Collections.emptyList();
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return true;
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public Future<?> submit(Runnable task) {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            throw new UnsupportedOperationException("Not supported");
        }
    }
}
