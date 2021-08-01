package com.hquyyp.utils;

import java.util.concurrent.*;


public final class Uninterruptibles {
    public static void awaitUninterruptibly(CountDownLatch latch) {
        boolean interrupted = false;
        while (true) {
            try {
                latch.await();
                return;
            } catch (InterruptedException e) {
            } finally {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }


    public static boolean awaitUninterruptibly(CountDownLatch latch, long timeout, TimeUnit unit) {
        boolean interrupted = false;
        try {
            long remainingNanos = unit.toNanos(timeout);
            long end = System.nanoTime() + remainingNanos;
            while (true) {
                try {
                    return latch.await(remainingNanos, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    interrupted = true;
                    remainingNanos = end - System.nanoTime();
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void joinUninterruptibly(Thread toJoin) {
        boolean interrupted = false;
        while (true) {
            try {
                toJoin.join();
                return;
            } catch (InterruptedException e) {
            } finally {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static <V> V getUninterruptibly(Future<V> future) throws ExecutionException {
        boolean interrupted = false;
        while (true) {
            try {
                return future.get();
            } catch (InterruptedException e) {
            } finally {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static <V> V getUninterruptibly(Future<V> future, long timeout, TimeUnit unit) throws ExecutionException, TimeoutException {
        boolean interrupted = false;
        try {
            long remainingNanos = unit.toNanos(timeout);
            long end = System.nanoTime() + remainingNanos;
            while (true) {
                try {
                    return future.get(remainingNanos, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    interrupted = true;
                    remainingNanos = end - System.nanoTime();
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static <E> E takeUninterruptibly(BlockingQueue<E> queue) {
        boolean interrupted = false;
        while (true) {
            try {
                return queue.take();
            } catch (InterruptedException e) {
            } finally {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static <E> void putUninterruptibly(BlockingQueue<E> queue, E element) {
        boolean interrupted = false;
        while (true) {
            try {
                queue.put(element);
                return;
            } catch (InterruptedException e) {
            }finally {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static void sleepUninterruptibly(long sleepFor, TimeUnit unit) {
        boolean interrupted = false;
        try {
            long remainingNanos = unit.toNanos(sleepFor);
            long end = System.nanoTime() + remainingNanos;
            while (true) {
                try {
                    TimeUnit.NANOSECONDS.sleep(remainingNanos);
                    return;
                } catch (InterruptedException e) {
                    interrupted = true;
                    remainingNanos = end - System.nanoTime();
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }


    public static boolean tryAcquireUninterruptibly(Semaphore semaphore, long timeout, TimeUnit unit) {
        return tryAcquireUninterruptibly(semaphore, 1, timeout, unit);
    }


    public static boolean tryAcquireUninterruptibly(Semaphore semaphore, int permits, long timeout, TimeUnit unit) {
        boolean interrupted = false;
        try {
            long remainingNanos = unit.toNanos(timeout);
            long end = System.nanoTime() + remainingNanos;
            while (true) {
                try {
                    return semaphore.tryAcquire(permits, remainingNanos, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    interrupted = true;
                    remainingNanos = end - System.nanoTime();
                }
            }
        } finally {
            if (interrupted)
              Thread.currentThread().interrupt();
        }
    }
}


/* Location:              F:\freeDemo\士兵对抗系统191.218_V1.0\源代码\scds.jar!\BOOT-INF\classes\com\outsider\hit\scd\\utils\Uninterruptibles.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */