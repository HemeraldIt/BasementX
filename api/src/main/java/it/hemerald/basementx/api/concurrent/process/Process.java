package it.hemerald.basementx.api.concurrent.process;

public abstract class Process {

    /**
     * Creates a new {@code Process} from a {@link Runnable}
     *
     * @param runnable input runnable
     * @return process
     */
    public static Process fromRunnable(Runnable runnable) {
        return new Process() {
            @Override
            protected void run() {
                runnable.run();
            }
        };
    }

    /**
     * Main logic for running this process.
     *
     * @throws Throwable if anything goes wrong, the implementation can throw any exception.
     */
    protected abstract void run() throws Throwable;

    // ===============================================
    private int processNum;

    final void processNum(int processNum) {
        this.processNum = processNum;
    }

    final void runProcess(ProcessesCompletion completion) {
        try {
            this.run();
        } catch (Throwable error) {
            completion.countDownWithError(new ProcessException("in Process #" + this.processNum, error));
            return;
        }
        completion.countDown();
    }
}
