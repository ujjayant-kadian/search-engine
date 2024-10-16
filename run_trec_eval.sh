#!/bin/bash

TREC_EVAL_PATH="/home/azureuser/assignment-1/trec_eval-9.0.7/trec_eval"

QRELS_FILE="cran/cran.qrels.trec"

RESULTS_DIR="results"

OUTPUT_DIR="eval_results"
mkdir -p $OUTPUT_DIR

RESULTS_FILES=(
    "$RESULTS_DIR/Custom_Analyzer_BM25_results.txt"
    "$RESULTS_DIR/Custom_Analyzer_VSM_results.txt"
    "$RESULTS_DIR/Standard_Analyzer_BM25_results.txt"
    "$RESULTS_DIR/Standard_Analyzer_VSM_results.txt"
    "$RESULTS_DIR/Whitespace_Analyzer_BM25_results.txt"
    "$RESULTS_DIR/Whitespace_Analyzer_VSM_results.txt"
)

for results_file in "${RESULTS_FILES[@]}"; do
    output_file="${OUTPUT_DIR}/$(basename ${results_file%.txt})_eval.txt"
    
    echo "Evaluating $results_file ..."
    $TREC_EVAL_PATH $QRELS_FILE $results_file > $output_file
    echo "Evaluation output saved to $output_file"
done
