package kadianu.group.analyzers;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.CharArraySet;

import java.util.List;
import java.util.Arrays;

public class CustomAnalyzer extends Analyzer {

    // Define your stop words list
    private static final List<String> STOP_WORDS = Arrays.asList(
        "a", "an", "the", "is", "are", "and", "or", "but", "not", "this", "that", 
        "with", "by", "for", "on", "of", "to", "in", "at", "from", "it", "its", 
        "these", "those", "he", "she", "they", "his", "her", "their", "which", 
        "who", "whom", "what", "when", "where", "why", "how", "may", "can", 
        "could", "would", "should", "have", "has", "had", "do", "does", "did", 
        "as", "if", "than", "then", "either", "neither", "any", "some", "such", 
        "many", "much", "more", "most", "same", "other", "another", "each", "few", 
        "less", "least", "all", "both", "either", "neither", "not", "so", "that", 
        "too", "very", "yet", "again", "still", "also", "just", "only", "now", "up", 
        "down", "here", "there", "when", "where", "why", "how", "whether", "while", 
        "after", "before", "during", "until", "against", "among", "between", "around", 
        "about", "above", "below", "across", "through", "out", "in", "off", "over", 
        "under", "without", "within", "towards", "toward", "along", "across", "beyond", 
        "inside", "outside", "several", "particular", "specific", "relative", "general", 
        "significant", "insignificant", "more", "less", "first", "last", "next", 
        "previous", "new", "old", "major", "minor", "several", "possible", "unlike", 
        "certain", "certainly", "actually", "simply", "generally", "often", "usually", 
        "rarely", "seldom", "frequently", "occasionally"
    );
    
    private static final CharArraySet STOP_WORDS_SET = new CharArraySet(STOP_WORDS, true);

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        // Tokenizer: StandardTokenizer
        StandardTokenizer tokenizer = new StandardTokenizer();

        // Filters: LowerCaseFilter, LengthFilter, StopFilter, EdgeNGramFilter
        TokenStream tokenStream = new LowerCaseFilter(tokenizer);
        tokenStream = new LengthFilter(tokenStream, 2, 20);
        tokenStream = new StopFilter(tokenStream, STOP_WORDS_SET);
        tokenStream = new EdgeNGramTokenFilter(tokenStream, 4, 10, true); // Min 1 and Max 10 for EdgeNGram

        return new TokenStreamComponents(tokenizer, tokenStream);
    }
}
