package it.hemerald.basementx.api.common;

import java.util.*;

/**
 * ProbabilityCollection for retrieving random elements based on probability.
 * Selection Algorithm Implementation:
 *
 * Elements have a "block" of space, sized based on their probability share
 * "Blocks" start from index 1 and end at the total probability of all elements
 * A random number is selected between 1 and the total probability
 * Which "block" the random number falls in is the element that is selected
 * Therefore "block"s with larger probability have a greater chance of being
 * selected than those with smaller probability.
 *
 * @param <E> Type of elements
 */
public final class ProbabilityCollection<E> {

    private final NavigableSet<ProbabilitySetElement<E>> collection;
    private final SplittableRandom random = new SplittableRandom();

    private int totalProbability;

    /**
     * Construct a new Probability Collection
     */
    public ProbabilityCollection() {
        this.collection = new TreeSet<>(Comparator.comparingInt(ProbabilitySetElement::getIndex));
        this.totalProbability = 0;
    }

    /**
     * @return Number of objects inside the collection
     */
    public int size() {
        return this.collection.size();
    }

    /**
     * @return True if collection contains no elements, else False
     */
    public boolean isEmpty() {
        return this.collection.isEmpty();
    }

    /**
     * @param object object
     * @return True if collection contains the object, else False
     * @throws IllegalArgumentException if object is null
     */
    public boolean contains(E object) {
        if (object == null) {
            throw new IllegalArgumentException("Cannot check if null object is contained in this collection");
        }

        return this.collection.stream().anyMatch(entry -> entry.getObject().equals(object));
    }

    /**
     * @return Iterator over this collection
     */
    public Iterator<ProbabilitySetElement<E>> iterator() {
        return this.collection.iterator();
    }

    /**
     * Add an object to this collection
     *
     * @param object         object. Not null.
     * @param probability share. Must be greater than 0.
     *
     * @throws IllegalArgumentException if object is null
     * @throws IllegalArgumentException if probability <= 0
     */
    public void add(E object, int probability) {
        if (object == null) {
            throw new IllegalArgumentException("Cannot add null object");
        }

        if (probability <= 0) {
            throw new IllegalArgumentException("Probability must be greater than 0");
        }

        ProbabilitySetElement<E> entry = new ProbabilitySetElement<>(object, probability);
        entry.setIndex(this.totalProbability + 1);

        this.collection.add(entry);
        this.totalProbability += probability;
    }

    /**
     * Remove an object from this collection
     *
     * @param object object
     * @return True if object was removed, else False.
     *
     * @throws IllegalArgumentException if object is null
     */
    public boolean remove(E object) {
        if (object == null) {
            throw new IllegalArgumentException("Cannot remove null object");
        }

        Iterator<ProbabilitySetElement<E>> it = this.iterator();
        boolean removed = false;

        // Remove all instances of the object
        while (it.hasNext()) {
            ProbabilitySetElement<E> entry = it.next();
            if (entry.getObject().equals(object)) {
                this.totalProbability -= entry.getProbability();
                it.remove();
                removed = true;
            }
        }

        // Recalculate remaining elements "block" of space: i.e 1-5, 6-10, 11-14
        if (removed) {
            int previousIndex = 0;
            for (ProbabilitySetElement<E> entry : this.collection) {
                previousIndex = entry.setIndex(previousIndex + 1) + (entry.getProbability() - 1);
            }
        }

        return removed;
    }

    /**
     * Remove all objects from this collection
     */
    public void clear() {
        this.collection.clear();
        this.totalProbability = 0;
    }

    /**
     * Get a random object from this collection, based on probability.
     *
     * @return <E> Random object
     *
     * @throws IllegalStateException if this collection is empty
     */
    public E get() {
        if (this.isEmpty()) {
            throw new IllegalStateException("Cannot get an object out of a empty collection");
        }

        ProbabilitySetElement<E> toFind = new ProbabilitySetElement<>(null, 0);
        toFind.setIndex(this.random.nextInt(1, this.totalProbability + 1));

        return Objects.requireNonNull(Objects.requireNonNull(this.collection.floor(toFind)).getObject());
    }

    /**
     * @return Sum of all element's probability
     */
    public int getTotalProbability() {
        return this.totalProbability;
    }

    /**
     * Used internally to store information about a object's state in a collection.
     * Specifically, the probability and index within the collection.
     *
     * Indexes refer to the start position of this element's "block" of space. The
     * space between element "block"s represents their probability of being selected
     *
     * @author Lewys Davies
     *
     * @param <T> Type of element
     */
    public final static class ProbabilitySetElement<T> {
        private final T object;
        private final int probability;
        private int index;

        /**
         * @param object object
         * @param probability share within the collection
         */
        private ProbabilitySetElement(T object, int probability) {
            this.object = object;
            this.probability = probability;
        }

        /**
         * @return <T> The actual object
         */
        public T getObject() {
            return this.object;
        }

        /**
         * @return Probability share in this collection
         */
        public int getProbability() {
            return this.probability;
        }

        // Used internally, see this class's documentation
        private int getIndex() {
            return this.index;
        }

        // Used Internally, see this class's documentation
        private int setIndex(int index) {
            this.index = index;
            return this.index;
        }
    }
}
