package kadianu.group.search_engine;

import kadianu.group.data.QueryData;
import org.apache.lucene.analysis.Analyzer;
import kadianu.group.analyzers.CustomAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.*;


import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class SearchEngine {

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

    public static void main(String[] args) throws Exception {
        try {
            List<QueryData> queries = loadQueries("cran/cran.qry");

            for (int i = 0; i < INDEX_DIRS.length; i++) {
                String indexDir = INDEX_DIRS[i];
                String analyzerName = ANALYZER_NAMES[i];
                Analyzer analyzer = ANALYZERS[i];
                
                searchAndSaveResults(indexDir, "BM25", queries, analyzerName, analyzer, new BM25Similarity());
                searchAndSaveResults(indexDir, "VSM", queries, analyzerName, analyzer, new ClassicSimilarity());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void searchAndSaveResults(String indexDir, String scoringApproach, List<QueryData> queries, String analyzerName, Analyzer analyzer, Similarity similarity) throws Exception {
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

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESULTS_DIR + analyzerName.replace(" ", "_") + "_" + scoringApproach + "_results.txt"))) {
                for (QueryData queryData : queries) {
                    Query query = queryParser.parse(sanitizeQuery(queryData.getQueryText()));

                    ScoreDoc[] hits = searcher.search(query, 1000).scoreDocs;

                    for (int rank = 0; rank < hits.length; rank++) {
                        ScoreDoc hit = hits[rank];
                        String docId = reader.document(hit.doc).get("id");
                        
                        writer.write(String.format("%s 0 %s %d %.4f %s_%s%n", queryData.getId(), docId, rank + 1, hit.score, analyzerName, scoringApproach));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<QueryData> loadQueries(String queryFilePath) throws IOException {
        List<QueryData> queries = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(queryFilePath))) {
            String line;
            QueryData currentQuery = null;
            StringBuilder queryText = new StringBuilder();

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(".I")) {
                    if (currentQuery != null) {
                        currentQuery.setQueryText(queryText.toString().trim());
                        queries.add(currentQuery);
                        queryText.setLength(0);
                    }
                    String id = line.replace(".I", "").trim();
                    currentQuery = new QueryData();
                    currentQuery.setId(id);
                } else if (line.startsWith(".W")) {
                    // ignore
                } else {
                    queryText.append(line).append(" ");
                }
            }

            // Add the last query
            if (currentQuery != null) {
                currentQuery.setQueryText(queryText.toString().trim());
                queries.add(currentQuery);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queries;
    }

    private static String sanitizeQuery(String queryText) {
        // remove leading * or ? in each term
        queryText = queryText.replaceAll("[\\*\\?]", "");

        // escape special characters that could interfere with parsing
        queryText = queryText.replaceAll("([+\\-!(){}\\[\\]^\"~*?:\\/\\\\])", "\\\\$1");

        return queryText;


    }
}
