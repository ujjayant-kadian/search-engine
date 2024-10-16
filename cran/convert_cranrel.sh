#!/bin/bash

input_file="cranqrel"
output_file="cran.qrels.trec"

awk '{
    relevance = $3
    if (relevance == 1 || relevance == 2 || relevance == 3) {
        print $1 " 0 " $2 " 1"
    } else {
        print $1 " 0 " $2 " 0"
    }
}' "$input_file" > "$output_file"

echo "Conversion complete! Output saved to $output_file"