package br.com.matheusmendes.compressor;

import static br.com.matheusmendes.config.Config.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Compressor implements ICompressor {

    @Override
    public void compressFiles(File folder, File zipFile) {
        zipFile = new File(ZIP_FILE_NAME);  // Arquivo ZIP onde os PDFs serão compactados
        folder = new File(DOWNLOAD_DIR);    // Diretório onde os PDFs estão armazenados
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        if (files == null || files.length == 0) {
            LOGGER.warning("Nenhum arquivo PDF encontrado para compactação.");
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[BUFFER_SIZE];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        zos.write(buffer, 0, bytesRead);
                    }

                    zos.closeEntry();
                    LOGGER.info("Arquivo compactado: " + file.getName());
                }
            }

            LOGGER.info("Compactação concluída. Arquivo criado: " + ZIP_FILE_NAME);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao compactar arquivos", e);
        }
    }
}
