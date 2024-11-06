package kadianu.group;

import java.io.File;
import java.util.Scanner;

import kadianu.group.indexer.IndexCranfield;
import kadianu.group.search_engine.InteractiveSearchEngine;
import kadianu.group.search_engine.SearchEngine;

public class Main {
    
    public static void main(String[] args) {
        try {
            cleanDirectory(new File("index/"));
            cleanDirectory(new File("results/"));
            cleanDirectory(new File("eval_results/"));

            System.out.println("Starting document indexing...");
            IndexCranfield.main(null);
            System.out.println("Document indexing completed.");

            System.out.println("Creating the scores for different analyzers...");
            SearchEngine.main(null);
            System.out.println("Result accummulation completed.");

            System.out.println("Running TREC_EVAL on the generated results...");
            runTrecEval();
            System.out.println("TREC_EVAL completed.");

            Scanner scanner = new Scanner(System.in);
            System.out.print("Do you want to start the Interactive Search Engine? (y/n): ");
            String userInput = scanner.nextLine().trim().toLowerCase();

            if (userInput.equals("yes") || userInput.equals("y")) {
                System.out.println("Starting Interactive Search Engine...");
                InteractiveSearchEngine.main(null);
            } else {
                System.out.println("Exiting the program.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runTrecEval() {
        try {
            @SuppressWarnings("deprecation")
            Process process = Runtime.getRuntime().exec("/home/ujjayant-kadian/College/search-engine/run_trec_eval.sh");
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void cleanDirectory(File dir) {
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    cleanDirectory(file);
                }
                file.delete();
            }
        }
    }
}
