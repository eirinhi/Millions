package no.ntnu.idatt2003.model.io;

import java.io.IOException;

/**
 * Generic interface for reading and writing data to and from files.
 *
 * @param <T> the type of data handled
 */
public interface FileHandler<T> {

    /**
     * Reads data from the file at the given path.
     *
     * @param filePath path to the file
     * @return the data read from the file
     * @throws IOException if the file could not be read
     */
    T readFromFile(String filePath) throws IOException;

    /**
     * Writes data to the file at the given path.
     *
     * @param data the data to write
     * @param filePath path to the file
     * @throws IOException if the file could not be written
     */
    void writeToFile(T data, String filePath) throws IOException;
}
