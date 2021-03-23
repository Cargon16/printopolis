package es.ucm.fdi.iw.g06.printopolis.model;

/**
 * Used to json-ize objects
 */
public interface Transferable<T> {
    T toTransfer();
}
