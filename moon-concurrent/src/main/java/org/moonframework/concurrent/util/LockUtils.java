package org.moonframework.concurrent.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/8/10
 */
public class LockUtils {

    private static final int DEFAULT_TIME = 10;

    public static boolean tryLock(Lock lock, BooleanSupplier supplier) {
        return tryLock(lock, DEFAULT_TIME, supplier);
    }

    public static <T> T tryLock(Lock lock, Supplier<T> supplier) {
        return tryLock(lock, DEFAULT_TIME, supplier);
    }

    /**
     * @param lock     lock
     * @param time     seconds
     * @param supplier supplier
     * @return if success return true
     */
    public static boolean tryLock(Lock lock, long time, BooleanSupplier supplier) {
        try {
            if (lock.tryLock(time, TimeUnit.SECONDS)) {
                try {
                    return supplier.getAsBoolean();
                } finally {
                    lock.unlock();
                }
            } else {
                return false;
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> T tryLock(Lock lock, long time, Supplier<T> supplier) {
        try {
            if (lock.tryLock(time, TimeUnit.SECONDS)) {
                try {
                    return supplier.get();
                } finally {
                    lock.unlock();
                }
            } else {
                return null;
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }


    public static boolean lock(Lock lock, BooleanSupplier supplier) {
        lock.lock();
        try {
            return supplier.getAsBoolean();
        } finally {
            lock.unlock();
        }
    }

    public static <T> T lock(Lock lock, Supplier<T> supplier) {
        lock.lock();
        try {
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }

}
