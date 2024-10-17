import matplotlib.pyplot as plt
import numpy as np
import os

output_dir = "eval_results_plots"
os.makedirs(output_dir, exist_ok=True)

analyzers = ['Custom_BM25', 'Custom_VSM', 'English_BM25', 'English_VSM', 
             'Standard_BM25', 'Standard_VSM', 'Whitespace_BM25', 'Whitespace_VSM']

map_scores = [0.0062, 0.0065, 0.0076, 0.0064, 0.0087, 0.0062, 0.0087, 0.0063]
p_5_scores = [0.0000, 0.0000, 0.0021, 0.0000, 0.0021, 0.0000, 0.0021, 0.0021]
p_20_scores = [0.0011, 0.0011, 0.0037, 0.0016, 0.0059, 0.0048, 0.0074, 0.0043]
p_100_scores = [0.0040, 0.0047, 0.0047, 0.0046, 0.0051, 0.0036, 0.0052, 0.0040]
p_1000_scores = [0.0036, 0.0036, 0.0035, 0.0035, 0.0037, 0.0038, 0.0038, 0.0036]

def plot_bargraph(metric_name, values, y_label, file_name):
    fig, ax = plt.subplots(figsize=(10, 6))
    
    bars = ax.bar(analyzers, values, color='skyblue')

    ax.set_title(f'{metric_name} for Different Analyzers')
    ax.set_xlabel('Analyzers')
    ax.set_ylabel(y_label)

    ax.set_ylim(0, max(values) * 1.5) 

    ax.set_xticks(range(len(analyzers)))
    ax.set_xticklabels(analyzers, rotation=45, ha="right")

    for bar in bars:
        yval = bar.get_height()
        ax.text(bar.get_x() + bar.get_width()/2, yval + 0.0002, round(yval, 4), ha='center', va='bottom')

    plt.tight_layout()
    plt.savefig(os.path.join(output_dir, file_name))
    plt.close()


plot_bargraph('MAP Scores', map_scores, 'Mean Average Precision (MAP)', 'map_scores.png')
plot_bargraph('Precision at Rank 5 Scores', p_5_scores, 'P_5', 'p_5_scores.png')
plot_bargraph('Precision at Rank 20 Scores', p_20_scores, 'P_20', 'p_20_scores.png')
plot_bargraph('Precision at Rank 100 Scores', p_100_scores, 'P_100', 'p_100_scores.png')
plot_bargraph('Precision at Rank 1000 Scores', p_1000_scores, 'P_1000', 'p_1000_scores.png')

print(f'Plots saved in {output_dir}')
