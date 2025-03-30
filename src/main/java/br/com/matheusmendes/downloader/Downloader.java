package br.com.matheusmendes.downloader;

import br.com.matheusmendes.progress.IProgress;
import br.com.matheusmendes.progress.Progress;
import static br.com.matheusmendes.config.Config.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;


public class Downloader implements IDownloader {


    @Override
    public boolean downloadFile(String fileURL, File destination) {
        // Verifica se o arquivo já existe no destino
        if (destination.exists()) {
            LOGGER.info("Arquivo já existe: " + destination.getName());
            return false;  // Arquivo já existente, não será baixado novamente
        }

        try {
            // Obtém a URL do arquivo
            URL url = new URL(fileURL);
            // Abre a conexão HTTP
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);

            // Obtém o tamanho do arquivo
            int fileSize = connection.getContentLength();
            if (fileSize <= 0) {
                LOGGER.warning("Tamanho do arquivo inválido: " + fileURL);
                return false;
            }

            // Realiza o download do arquivo
            try (InputStream in = connection.getInputStream();
                 FileOutputStream out = new FileOutputStream(destination)) {

                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                long totalBytesRead = 0;
                long startTime = System.currentTimeMillis();

                // Enquanto houver dados para ler
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;

                    // Calcula o progresso e a velocidade de download
                    int percent = (int) ((totalBytesRead * 100) / fileSize);
                    long elapsedTime = System.currentTimeMillis() - startTime + 1;
                    double speedKBs = (totalBytesRead / 1024.0) / (elapsedTime / 1000.0);

                    // Delegate para exibir o progresso
                    IProgress reporter = new Progress();
                    reporter.reportProgress(destination.getName(), percent, speedKBs);
                }

            }
            return true; // Download bem-sucedido
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao baixar o arquivo: " + fileURL, e);
            return false;
        }
    }
}
