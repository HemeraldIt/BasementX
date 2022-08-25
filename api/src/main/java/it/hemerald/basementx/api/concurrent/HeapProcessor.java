package it.hemerald.basementx.api.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class HeapProcessor<T> extends Processor<T> {

    private final int maxHeap;

    public HeapProcessor(int maxHeap) {
        this(0, maxHeap);
    }

    public HeapProcessor(int delay, int maxHeap) {
        super(delay);

        this.maxHeap = maxHeap;
    }

    @Override
    protected Thread startThread() {
        return new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    if (deque.size() > 1) {
                        List<T> processes = new ArrayList<>();
                        while (processes.size() < maxHeap && !deque.isEmpty()) processes.add(deque.take());
                        try {
                            computeBatch(processes);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        T process = this.deque.poll(10, TimeUnit.SECONDS);

                        if(process != null) {
                            try {
                                compute(process);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public abstract void computeBatch(List<T> processes);
}
