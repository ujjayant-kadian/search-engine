package kadianu.group.indexer;

import kadianu.group.data.DocumentData;
import kadianu.group.analyzers.CustomAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class IndexCranfield {

    private static final String STANDARD_INDEX_DIR = "index/standard_index";
    private static final String CUSTOM_INDEX_DIR = "index/custom_index";
    private static final String WHITESPACE_INDEX_DIR = "index/whitespace_index";
    private static final String ENGLISH_INDEX_DIR = "index/english_index";
    private static final String CRANFIELD_FILE = "cran/cran.all.1400";

    public static void main(String[] args) {
        try {

            List<DocumentData> documents = parseCranfieldDocuments(CRANFIELD_FILE);

            indexDocuments(new StandardAnalyzer(), STANDARD_INDEX_DIR, documents);
            System.out.println("Standard Indexing completed successfully.");

            indexDocuments(new CustomAnalyzer(), CUSTOM_INDEX_DIR, documents);
            System.out.println("Custom Indexing completed successfully.");

            indexDocuments(new WhitespaceAnalyzer(), WHITESPACE_INDEX_DIR, documents);
            System.out.println("Whitespace Indexing completed successfully.");

            indexDocuments(new EnglishAnalyzer(), ENGLISH_INDEX_DIR, documents);
            System.out.println("English Indexing completed successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void indexDocuments(Analyzer analyzer, String indexDir, List<DocumentData> documents) throws IOException {
        try (FSDirectory directory = FSDirectory.open(Paths.get(indexDir))) {
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            try (IndexWriter indexWriter = new IndexWriter(directory, config)) {
                for (DocumentData docData : documents) {
                    Document doc = new Document();

                    if (docData.getId() == null) {
                        continue;
                    }
                    doc.add(new StringField("id", docData.getId(), Field.Store.YES));

                    String title = docData.getTitle() != null ? docData.getTitle() : "Unknown";
                    doc.add(new TextField("title", title, Field.Store.YES));

                    String author = docData.getAuthor() != null ? docData.getAuthor() : "Unknown";
                    doc.add(new StringField("author", author, Field.Store.YES));
                    
                    String content = docData.getContent() != null ? docData.getContent() : "No Content";
                    doc.add(new TextField("content", content, Field.Store.YES));

                    indexWriter.addDocument(doc);
                }
            }
        }
    }

    private static List<DocumentData> parseCranfieldDocuments(String cranfieldFile) throws IOException {
        List<DocumentData> documents = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(cranfieldFile))) {
            String line;
            DocumentData currentDoc = null;
            StringBuilder contentBuilder = new StringBuilder();
            String currentSection = "";

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(".I")) { // Start of a new document
                    if (currentDoc != null) {
                        currentDoc.setContent(contentBuilder.toString().trim());
                        documents.add(currentDoc);
                        contentBuilder.setLength(0); // Reset the content builder
                    }
                    String id = line.replace(".I", "").trim();
                    currentDoc = new DocumentData();
                    currentDoc.setId(id);
                } else if (line.startsWith(".")) { // Section marker
                    currentSection = line;
                } else {
                    if (currentDoc != null) {
                        switch (currentSection) {
                            case ".T":
                                currentDoc.setTitle(line);
                                break;
                            case ".A":
                                currentDoc.setAuthor(line);
                                break;
                            case ".W":
                                contentBuilder.append(line).append(" ");
                                break;
                            default:
                                // ignore
                                break;
                        }
                    }
                }
            }

            // Add the last document
            if (currentDoc != null) {
                currentDoc.setContent(contentBuilder.toString().trim());
                documents.add(currentDoc);
            }
        }

        return documents;
    }
}
