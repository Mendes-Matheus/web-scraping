package br.com.matheusmendes.config;

import java.util.logging.Logger;

public class Config {
    // Diretório onde os arquivos baixados serão armazenados
    public static final String DOWNLOAD_DIR = "downloads";

    // Nome do arquivo ZIP que será criado para armazenar os arquivos PDF compactados
    public static final String ZIP_FILE_NAME = "downloads/anexos.zip";

    // URL alvo de onde os arquivos PDF serão baixados
    public static final String TARGET_URL = "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos";

    // Tamanho do buffer utilizado para leitura e escrita de arquivos (1 KB)
    public static final int BUFFER_SIZE = 1024;

    // Tempo limite para conexões e leituras (30 segundos)
    public static final int TIMEOUT = 30 * 1000;

    // Logger para registrar informações e erros durante a execução do programa
    public static final Logger LOGGER = Logger.getLogger(Config.class.getName());
}
