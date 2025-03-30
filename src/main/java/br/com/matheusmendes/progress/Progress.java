package br.com.matheusmendes.progress;


public class Progress implements IProgress {
    @Override
    public void reportProgress(String fileName, int percent, double speedKBs) {
        System.out.printf("\rBaixando %s: %d%% - Velocidade: %.2f KB/s", fileName, percent, speedKBs);
    }
}
