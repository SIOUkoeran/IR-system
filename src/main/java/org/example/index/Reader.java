package org.example.index;

import java.util.List;

/**
 * data input file reader interface
 */
public interface Reader {

    List<String> read(String path, int size);
}
