# WebScraper - Download e Compactação de PDFs

## Descrição
Essa é uma aplicação que realiza o download de arquivos PDF específicos de uma página da web e os compacta em um arquivo ZIP. O programa foi projetado para buscar arquivos **"anexo i"** e **"anexo ii"** do endereço https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos, realizar o download e compacta-los em um arquivo zip.

## Funcionalidades
- Realiza **scraping** de links em uma URL especificada.
- Filtra apenas os arquivos PDF que contêm "anexo i" ou "anexo ii" no nome.
- Realiza o **download** dos PDFs encontrados.
- **Compacta** os arquivos baixados em um arquivo ZIP.
- Exibe o progresso do download e registra logs de eventos.

## Tecnologias Utilizadas
- **Java 17+**
- **JSoup** (para web scraping)
- **Java Util Logging** (para logs)
- **API de Streams e I/O do Java** (para manipulação de arquivos)

## Estrutura do Projeto
```
WebScraper/
├── src/
│   ├── br/com/matheusmendes/
│   │   ├── WebScraper.java
│   │   ├── config/Config.java
│   │   ├── compressorCompressor.java
│   │   ├── compressor/ICompressor.java
│   │   ├── downloader/Downloader.java
│   │   ├── downloader/IDownloader.java
│   │   ├── progress/Progress.java
│   │   ├── progress/IProgress.java
│   ├── downloads/ (Diretório onde os PDFs serão salvos)
│   ├── downloads/anexos.zip (Arquivo ZIP gerado)
├── README.md
├── pom.xml (Caso utilize Maven)
```

## Configuração
A configuração do projeto está definida na classe **Config.java**:
```java
public class Config {
    public static final String DOWNLOAD_DIR = "downloads";
    public static final String ZIP_FILE_NAME = "downloads/anexos.zip";
    public static final String TARGET_URL = "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos";
    public static final int BUFFER_SIZE = 1024;
    public static final int TIMEOUT = 30 * 1000;
}
```

## Como Usar
### 1. Clonar o Repositório
```sh
git clone git@github.com:Mendes-Matheus/web-scraping.git
cd web-scraping
```

### 2. Compilar e Executar
Caso esteja utilizando **Maven**, compile e execute:
```sh
mvn clean package
java -jar target/webscraper.jar
```

### 3. Resultado Esperado
Ao executar o programa:
- Os arquivos **anexo i** e **anexo ii** serão baixados para a pasta `downloads/`.
- Os arquivos baixados serão compactados no arquivo `downloads/anexos.zip`.
- O progresso do download será exibido no console.

