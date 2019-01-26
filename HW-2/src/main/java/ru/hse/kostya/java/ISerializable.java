package ru.hse.kostya.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Custom interface for serialization.
 * Contains just two methods for serialization and deserialization,
 *      which takes Input/Output Stream as an argument
 */
public interface ISerializable {

    void serialize(OutputStream out) throws IOException;

    void deserialize(InputStream in) throws IOException;

}
