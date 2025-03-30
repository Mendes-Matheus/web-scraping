package br.com.matheusmendes.compressor;

import java.io.File;

public interface ICompressor {
    void compressFiles(File folder, File zipFile);
}
