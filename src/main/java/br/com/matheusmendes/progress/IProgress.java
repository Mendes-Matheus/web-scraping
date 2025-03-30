package br.com.matheusmendes.progress;

public interface IProgress {
    void reportProgress(String fileName, int percent, double speedKBs);
}
