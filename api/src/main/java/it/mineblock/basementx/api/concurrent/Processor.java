package it.mineblock.basementx.api.concurrent;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public abstract class Processor<T> {

    protected final BlockingDeque<T> deque = new LinkedBlockingDeque<>();
    private final Thread thread;
    private final int delay;

    public Processor() {
        this(0);
    }

    public Processor(int delay) {
        this.delay = delay;

        this.thread = startThread();
    }

    protected Thread startThread() {
        return new Thread(() -> {
           while (!Thread.interrupted()) {
               try {
                   if (checkPreconditions()) {
                       T process = this.deque.poll(10, TimeUnit.SECONDS);

                       if (process != null) {
                           try {
                               compute(process);
                           } catch (Exception e) {
                               e.printStackTrace();
                           }

                           if (delay > 0) Thread.sleep(delay);
                       }
                   }
               } catch (InterruptedException e) {
                   Thread.currentThread().interrupt();
               }
           }
        });
    }

    protected boolean checkPreconditions() {
        return true;
    }

    public void add(final T process) {
        deque.add(process);
    }

    public void addFirst(final T process) {
        deque.addFirst(process);
    }

    public boolean contains(final T object) {
        return deque.contains(object);
    }

    public abstract void compute(final T process);

    public synchronized void start() {
        thread.start();
    }

    public synchronized void stop() {
        stop(false);
    }

    public synchronized void stop(boolean force) {
        if(force) {
            thread.stop();
        } else {
            thread.interrupt();
        }
    }
}
