package ru.hse.kostya.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ISerializable {

    void serialize(OutputStream out) throws IOException;
    void deserialize(InputStream in) throws IOException;

}
