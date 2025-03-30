package br.com.matheusmendes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class WebScraper {
    // URL alvo de onde os arquivos serão baixados
    private static final String TARGET_URL = "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos";

    // Diretório onde os arquivos serão armazenados
    private static final String DOWNLOAD_DIR = "downloads";

    // Nome do arquivo ZIP onde os arquivos PDF serão compactados
    private static final String ZIP_FILE_NAME = "downloads/anexos.zip";

    // Logger para registrar informações e erros
    private static final Logger LOGGER = Logger.getLogger(WebScraper.class.getName());

    // Constantes para os tamanhos de buffer e tempo de timeout
    private static final int BUFFER_SIZE = 1024;  // Tamanho do buffer (1 KB)
    private static final int TIMEOUT = 30 * 1000;  // Timeout de 30 segundos

    public static void main(String[] args) {
        // Cria o diretório de download caso ele não exista
        createDownloadFolder();

        try {
            // Conexão com a página alvo e leitura do conteúdo HTML
            Document doc = Jsoup.connect(TARGET_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36")
                    .timeout(TIMEOUT)
                    .get();

            // Seleciona todos os links que terminam com .pdf
            Elements links = doc.select("a[href$=.pdf]");

            // Itera sobre todos os links encontrados
            for (Element link : links) {
                String url = link.absUrl("href");  // Obtém a URL absoluta do link
                // Verifica se o link é um dos arquivos "anexo i" ou "anexo ii"
                if (url.toLowerCase().matches(".*anexo[_\\s]*i.*\\.pdf") || url.toLowerCase().matches(".*anexo[_\\s]*ii.*\\.pdf")) {
                    // Se for um arquivo válido, tenta fazer o download
                    downloadFileWithProgress(url);
                }
            }

        } catch (IOException e) {
            // Caso haja erro ao processar a página, registra o erro
            LOGGER.log(Level.SEVERE, "Erro ao processar a página", e);
        }

        // Compacta os arquivos PDF
        compressFiles();
    }

    /**
     * Cria o diretório "downloads" caso ele não exista.
     */
    private static void createDownloadFolder() {
        File downloadFolder = new File(DOWNLOAD_DIR);
        // Verifica se o diretório não existe e tenta criá-lo
        if (!downloadFolder.exists()) {
            if (downloadFolder.mkdirs()) {
                LOGGER.info("Pasta de download criada: " + DOWNLOAD_DIR);
            } else {
                LOGGER.warning("Falha ao criar diretório de download: " + DOWNLOAD_DIR);
            }
        }
    }

    /**
     * Realiza o download de um arquivo exibindo progresso e velocidade de download.
     * Evita duplicação ao verificar se o arquivo já existe no diretório.
     * Retorna `true` se um novo arquivo foi baixado, `false` caso contrário.
     */
    private static boolean downloadFileWithProgress(String fileURL) {
        try {
            // Obtém o nome do arquivo a partir da URL
            String fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
            // Cria um objeto File com o caminho completo para o arquivo de destino
            File file = new File(DOWNLOAD_DIR + File.separator + fileName);

            // Verifica se o arquivo já existe
            if (file.exists()) {
                LOGGER.info("Arquivo já baixado: " + fileName);
                return false;  // Se o arquivo já foi baixado, não faz o download novamente
            }

            // Conexão HTTP para fazer o download do arquivo
            URL url = new URL(fileURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Obtém o tamanho do arquivo para mostrar o progresso
            int fileSize = connection.getContentLength();
            if (fileSize <= 0) {
                LOGGER.warning("Não foi possível obter o tamanho do arquivo: " + fileURL);
                return false;
            }

            // Tenta abrir o fluxo de entrada e saída para transferir os dados
            try (InputStream in = connection.getInputStream();
                 FileOutputStream out = new FileOutputStream(file)) {

                byte[] buffer = new byte[BUFFER_SIZE]; // Buffer de 1 KB para leitura do arquivo
                int bytesRead;
                long totalBytesRead = 0;
                long startTime = System.currentTimeMillis();

                // Enquanto houver dados a serem lidos
                // Esse trecho de código realiza a leitura do arquivo em partes (buffer),
                // escreve essas partes em um arquivo de destino e calcula o progresso do download
                // e a velocidade em tempo real. O progresso é exibido no terminal a cada iteração do loop,
                // proporcionando uma visão contínua da execução do download.
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);  // Escreve os dados no arquivo
                    totalBytesRead += bytesRead;

                    // Calcula a porcentagem de dados baixados
                    int percent = (int) ((totalBytesRead * 100) / fileSize);

                    // Calcula a velocidade de download em KB/s
                    long elapsedTime = System.currentTimeMillis() - startTime + 1; // Evita divisão por zero
                    double speedKBs = (totalBytesRead / 1024.0) / (elapsedTime / 1000.0);

                    // Exibe o progresso e a velocidade no terminal
                    System.out.printf("\rBaixando %s: %d%% - Velocidade: %.2f KB/s", fileName, percent, speedKBs);
                }
            }

            // Exibe a mensagem final de conclusão do download
            LOGGER.info("Download finalizado: " + fileName);
            return true;  // Arquivo foi baixado com sucesso
        } catch (IOException e) {
            // Em caso de erro no download, registra o erro
            LOGGER.log(Level.SEVERE, "Erro ao baixar arquivo: " + fileURL, e);
            return false;  // Falha no download
        }
    }

    /**
     * Compacta os arquivos PDF baixados no diretório de downloads em um único arquivo ZIP.
     */
    private static void compressFiles() {
        File zipFile = new File(ZIP_FILE_NAME);  // Arquivo ZIP onde os PDFs serão compactados
        File folder = new File(DOWNLOAD_DIR);    // Diretório onde os PDFs estão armazenados
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
