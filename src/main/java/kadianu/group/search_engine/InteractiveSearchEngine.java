package kadianu.group.search_engine;

import kadianu.group.data.SearchResultData;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import kadianu.group.analyzers.CustomAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.*;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class InteractiveSearchEngine {

    private static final String[] INDEX_DIRS = {
            "index/standard_index",
            "index/custom_index",
            "index/whitespace_index",
            "index/english_index"
    };

    private static final String[] ANALYZER_NAMES = {
            "Standard Analyzer",
            "Custom Analyzer",
            "Whitespace Analyzer",
            "English Analyzer"
    };

    private static final Analyzer[] ANALYZERS = {
            new StandardAnalyzer(),
            new CustomAnalyzer(),
            new WhitespaceAnalyzer(),
            new EnglishAnalyzer()
    };

    private static final String RESULTS_DIR = "results/";
    
    private static final String[] SCORING_MODELS = {
            "BM25",
            "Vector Space Model (VSM)"
    };

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Interactive Search Engine!");

        System.out.println("Select an analyzer:");
        for (int i = 0; i < ANALYZER_NAMES.length; i++) {
            System.out.println((i + 1) + ". " + ANALYZER_NAMES[i]);
        }
        System.out.print("Enter the number of your choice: ");
        int analyzerChoice = scanner.nextInt() - 1;

        if (analyzerChoice < 0 || analyzerChoice >= ANALYZER_NAMES.length) {
            System.out.println("Invalid choice. Exiting.");
            return;
        }

        Analyzer selectedAnalyzer = ANALYZERS[analyzerChoice];
        String indexDir = INDEX_DIRS[analyzerChoice];

        System.out.println("Select a scoring model:");
        for (int i = 0; i < SCORING_MODELS.length; i++) {
            System.out.println((i + 1) + ". " + SCORING_MODELS[i]);
        }
        System.out.print("Enter the number of your choice: ");
        int scoringModelChoice = scanner.nextInt() - 1;

        if (scoringModelChoice < 0 || scoringModelChoice >= SCORING_MODELS.length) {
            System.out.println("Invalid choice. Exiting.");
            return;
        }

        scanner.nextLine();
        while (true) {
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
            System.out.print("\nEnter your query (or type 'exit' to quit): ");
            String userQuery = scanner.nextLine().trim();

            if (userQuery.equalsIgnoreCase("exit")) {
                System.out.println("Exiting Interactive Search Engine!");
                break;
            }


            List<SearchResultData> results = searchIndex(indexDir, selectedAnalyzer, userQuery, scoringModelChoice);


            System.out.println("Search Results:");
            if (results.isEmpty()) {
                System.out.println("No results found.");
            } else {
                for (SearchResultData result : results) {
                    System.out.println("Document ID: " + result.getId());
                    System.out.println("Score: " + result.getScore());
                    System.out.println("Title: " + result.getTitle());
                    System.out.println("Content: " + result.getContent());
                    System.out.println("----------------------------------------");
                }
            }
        }

        scanner.close();
    }

    private static List<SearchResultData> searchIndex(String indexDir, Analyzer analyzer, String userQuery, int scoringModelChoice) throws Exception {
        List<SearchResultData> results = new ArrayList<>();
        Similarity similarity = scoringModelChoice == 0 ? new BM25Similarity() : new ClassicSimilarity();

        try (FSDirectory directory = FSDirectory.open(Paths.get(indexDir));
             DirectoryReader reader = DirectoryReader.open(directory)) {

            IndexSearcher searcher = new IndexSearcher(reader);
            searcher.setSimilarity(similarity);
            
            Map<String, Float> boosts = new HashMap<>();
            boosts.put("title", 1.5f);
            boosts.put("author", 0.5f);
            boosts.put("content", 1.5f);

            MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
                new String[]{"title", "author", "content"}, 
                analyzer, 
                boosts
            );
            
            Query query = queryParser.parse(sanitizeQuery(userQuery));


            ScoreDoc[] hits = searcher.search(query, 50).scoreDocs;

            for (ScoreDoc hit : hits) {
                Document doc = reader.document(hit.doc);
                String docId = doc.get("id");
                float score = hit.score;
                String title = doc.get("title");
                String snippet = doc.get("content").length() > 100 ? doc.get("content").substring(0, 100) + "..." : doc.get("content");
                results.add(new SearchResultData(docId, score, title, snippet));
            }
        }

        return results;
    }

    private static String sanitizeQuery(String queryText) {
        // remove leading * or ? in each term
        queryText = queryText.replaceAll("[\\*\\?]", "");

        // escape special characters that could interfere with parsing
        queryText = queryText.replaceAll("([+\\-!(){}\\[\\]^\"~*?:\\/\\\\])", "\\\\$1");

        return queryText;
    }
}
