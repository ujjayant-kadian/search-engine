package kadianu.group;

import kadianu.group.analyzers.CustomAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CustomAnalyzerTest {

    private Analyzer analyzer;

    @Before
    public void setUp() {
        analyzer = new CustomAnalyzer();
    }

    @After
    public void tearDown() throws IOException {
        analyzer.close();
    }

    private List<String> analyzeText(Analyzer analyzer, String text) throws IOException {
        try (TokenStream tokenStream = analyzer.tokenStream("field", text)) {
            CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            List<String> tokens = new java.util.ArrayList<>();
            while (tokenStream.incrementToken()) {
                tokens.add(attr.toString());
            }
            tokenStream.end();
            return tokens;
        }
    }

    // Test cases

    /**
     * Test that stop words are removed from the token stream.
     */
    @Test
    public void testStopWordsRemoval() throws IOException {
        String text = "This is a simple test to verify that stop words are removed.";
        List<String> tokens = analyzeText(analyzer, text);
        List<String> expectedTokens = Arrays.asList(
                "simp", "simpl", "simple",
                "test",
                "veri", "verif", "verify",
                "stop",
                "word", "words",
                "remo", "remov", "remove", "removed"
        );
        assertEquals(expectedTokens, tokens);
    }

    /**
     * Test that all tokens are converted to lowercase.
     */
    @Test
    public void testLowerCaseConversion() throws IOException {
        String text = "Apache Lucene Analyzer TEST";
        List<String> tokens = analyzeText(analyzer, text);
        List<String> expectedTokens = Arrays.asList(
                "apac", "apach", "apache",
                "luce", "lucen", "lucene",
                "anal", "analy", "analyz", "analyze", "analyzer",
                "test"
        );
        assertEquals(expectedTokens, tokens);
    }

    /**
     * Test that analyzer handles empty strings.
     */
    @Test
    public void testEmptyString() throws IOException {
        String text = "";
        List<String> tokens = analyzeText(analyzer, text);
        assertTrue("Token list should be empty for empty input.", tokens.isEmpty());
    }

    /**
     * Test that analyzer handles strings with only stop words.
     */
    @Test
    public void testOnlyStopWords() throws IOException {
        String text = "the and is but or";
        List<String> tokens = analyzeText(analyzer, text);
        assertTrue("Token list should be empty for input containing only stop words.", tokens.isEmpty());
    }

    /**
     * Test that the analyzer preserves tokens correctly.
     */
    @Test
    public void testPreserveOriginalTokens() throws IOException {
        String text = "hello";
        List<String> tokens = analyzeText(analyzer, text);
        List<String> expectedTokens = Arrays.asList(
                "hell", "hello"
        );
        assertEquals(expectedTokens, tokens);
    }

    /**
     * Test that analyzer does not generate n-grams longer than the token.
     */
    @Test
    public void testMaxNGramLength() throws IOException {
        String text = "hi";
        List<String> tokens = analyzeText(analyzer, text);
        List<String> expectedTokens = Arrays.asList("hi");
        assertEquals(expectedTokens, tokens);
    }

    /**
     * Test that analyzer handles punctuation correctly.
     */
    @Test
    public void testPunctuationHandling() throws IOException {
        String text = "Hello, world! This is a test.";
        List<String> tokens = analyzeText(analyzer, text);
        List<String> expectedTokens = Arrays.asList(
                "hell", "hello",
                "worl", "world",
                "test"
        );
        assertEquals(expectedTokens, tokens);
    }

    /**
     * Test that no single character tokens are produced.
     */
    @Test
    public void testNoSingleCharacterTokens() throws IOException {
        String text = "a s t example";
        List<String> tokens = analyzeText(analyzer, text);
        List<String> expectedTokens = Arrays.asList(
                "exam", "examp", "exampl", "example"
        );
        assertEquals(expectedTokens, tokens);
    }
}

