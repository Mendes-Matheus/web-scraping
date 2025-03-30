package br.com.matheusmendes.downloader;

import java.io.File;

public interface IDownloader {
    boolean downloadFile(String fileURL, File destination);
}
