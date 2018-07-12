package org.ericmoshare.uidgenerator.idgenerator;

/**
 * @author eric.mo
 */
public interface IdGenerator {

    /**
     * Increment the data store field's max value as Long.
     *
     * @return next data store value such as <b>max + 1</b>
     */
    long nextId();

    /**
     * Increment the data store field's max value as String.
     *
     * @return next data store value such as <b>max + 1</b>
     */
    String nextStringValue();
}
