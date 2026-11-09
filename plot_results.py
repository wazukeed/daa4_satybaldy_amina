
import csv
from collections import defaultdict
from pathlib import Path
from statistics import median

import matplotlib.pyplot as plt

root = Path(__file__).resolve().parent
plots_dir = root / "results" / "plots"
plots_dir.mkdir(parents=True, exist_ok=True)

with (root / "results" / "results.csv").open() as file:
    rows = list(csv.DictReader(file))

data = defaultdict(list)

for row in rows:
    key = (
        row["family"],
        row["algorithm"],
        row["input"],
        int(row["n"])
    )
    data[key].append(row)


def plot(family, metric, algorithms, types,
         filename, title, log=False):

    fig, ax = plt.subplots(figsize=(8, 5))

    for algorithm in algorithms:
        for input_type in types:

            points = sorted(
                (
                    n,
                    median(float(row[metric]) for row in runs)
                )
                for (fam, alg, typ, n), runs in data.items()
                if fam == family
                and alg == algorithm
                and typ == input_type
            )

            if points:
                ax.plot(
                    [x for x, y in points],
                    [y for x, y in points],
                    marker="o",
                    label=f"{algorithm} / {input_type}"
                )

    ax.set_title(title)
    ax.set_xlabel("Input length n (characters)")

    if metric == "time_ms":
        ax.set_ylabel("Median time (ms)")
    else:
        ax.set_ylabel("Operations (count)")

    if log:
        ax.set_yscale("log")

    ax.grid(alpha=0.3)
    ax.legend()
    fig.tight_layout()

    fig.savefig(plots_dir / filename, dpi=160)
    plt.close(fig)


plot(
    "edit", "operations",
    ["recursive"],
    ["random", "adversarial"],
    "01_recursive_calls.png",
    "Recursive edit distance: measured calls",
    True
)

plot(
    "edit", "time_ms",
    ["table", "two_rows"],
    ["random", "adversarial"],
    "02_dp_time.png",
    "Dynamic programming: median runtime"
)

plot(
    "search", "operations",
    ["naive", "kmp"],
    ["random", "adversarial"],
    "03_search_comparisons.png",
    "String search: measured comparisons"
)

plot(
    "search", "time_ms",
    ["naive", "kmp"],
    ["adversarial"],
    "04_search_time.png",
    "Adversarial string search: median runtime"
)

print("Saved 4 graphs to results/plots/")
