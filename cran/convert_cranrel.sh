#!/bin/bash

input_file="cranqrel"
output_file="cran.qrels.trec"

awk '{
    query_id = sprintf("%03d", $1)
    doc_id = $2
    relevance = $3
    if (relevance == 1 || relevance == 2 || relevance == 3) {
        print query_id " 0 " doc_id " " relevance
    } else {
        print query_id " 0 " doc_id " 0"
    }
}' "$input_file" > "$output_file"

echo "Conversion complete! Output saved to $output_file"