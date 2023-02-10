package it.hemerald.basementx.api.concurrent.process;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class ResultedProcess<T> {

    private final String identifier;

    /**
     * Base constructor for all {@code ResultedProcess} implementations.
     *
     * @param identifier the identifier of this resulted process. It is good to set it every time
     *                   implementation class(es) are created in order to properly defer results.
     */
    public ResultedProcess(String identifier) {
        this.identifier = Objects.requireNonNull(identifier, "identifier");
    }

    /**
     * Creates a new {@code ResultedProcess} from a {@link Supplier}
     *
     * @param identifier resulted process identifier
     * @param supplier   input supplier
     * @param <T>        result value type parameter
     * @return resulted process
     */
    public static <T> ResultedProcess<T> fromSupplier(String identifier, Supplier<T> supplier) {
        return new ResultedProcess<T>(identifier) {
            @Override
            public T run() {
                return supplier.get();
            }
        };
    }

    /**
     * Main logic for running the process
     *
     * @return a non null result value
     * @throws Throwable if anything goes wrong, the implementation can throw any exception.
     */
    protected abstract T run() throws Throwable;

    // ====================================

    final void runProcess(ResultedProcessesCompletion<T> completion) {
        try {
            T result = this.run();
            if (result != null) {
                completion.countDown(this.identifier, ProcessResult.success(result));
            } else {
                throw new ProcessException(
                        "Null result in ResultedProcess '" + this.identifier + "' without errors!");
            }
        } catch (Throwable e) {
            ProcessException error =
                    new ProcessException("in ResultedProcess '" + this.identifier + "'", e);
            if (e instanceof ProcessException) {
                error = (ProcessException) e;
            }
            completion.countDown(this.identifier, ProcessResult.failure(error));
        }
    }
}
