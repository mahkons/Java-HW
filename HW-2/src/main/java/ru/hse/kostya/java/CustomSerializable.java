package ru.hse.kostya.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Custom interface for serialization.
 * Contains just two methods for serialization and deserialization,
 *      which takes Input/Output Stream as an argument
 */
public interface CustomSerializable {

    /**
     * Writes object to OutputStream.
     * Strictly adheres to the fixed format.
     * @throws IOException
     */
    void serialize(OutputStream out) throws IOException;

    /**
     * Reads object from InputStream.
     * Consistent with CustomSerializable.serialize function
     */
    void deserialize(InputStream in) throws IOException;

}
