package br.com.matheusmendes;

import br.com.matheusmendes.compressor.Compressor;
import br.com.matheusmendes.compressor.ICompressor;
import br.com.matheusmendes.downloader.Downloader;
import br.com.matheusmendes.downloader.IDownloader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static br.com.matheusmendes.config.Config.*;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class WebScraper {

    public static void main(String[] args) {
        // Cria instâncias das classes concretas
        IDownloader downloader = new Downloader();
        ICompressor compressor = new Compressor();

        // Cria pasta de download
        createDownloadFolder();

        try {
            Document doc = Jsoup.connect(TARGET_URL).get();
            Elements links = doc.select("a[href$=.pdf]");  // Selecionando apenas links que terminam com .pdf

            // Itera sobre todos os links encontrados
            for (Element link : links) {
                String url = link.absUrl("href");  // Obtém a URL absoluta do link

                // Verifica se o link é um dos arquivos "anexo i" ou "anexo ii"
                if (url.toLowerCase().matches(".*anexo[_\\s]*i.*\\.pdf") || url.toLowerCase().matches(".*anexo[_\\s]*ii.*\\.pdf")) {
                        File file = new File(DOWNLOAD_DIR + File.separator + url.substring(url.lastIndexOf("/") + 1));
                    // Se for um arquivo válido, tenta fazer o download
                    if (downloader.downloadFile(url, file)) {
                        LOGGER.info("Download concluído.");
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao processar a página", e);
        }

        // Compacta os arquivos
        File folder = new File(DOWNLOAD_DIR);
        File zipFile = new File(ZIP_FILE_NAME);
        compressor.compressFiles(folder, zipFile);
    }

    // Cria pasta de download
    private static void createDownloadFolder() {
        File downloadFolder = new File(DOWNLOAD_DIR);
        if (!downloadFolder.exists()) {
            boolean created = downloadFolder.mkdirs();
            if (!created) {
                LOGGER.warning("Falha ao criar diretório de download: " + DOWNLOAD_DIR);
            } else {
                LOGGER.info("Diretório de download criado: " + DOWNLOAD_DIR);
            }
        } else {
            LOGGER.info("O diretório de download já existe: " + DOWNLOAD_DIR);
        }
    }
}
