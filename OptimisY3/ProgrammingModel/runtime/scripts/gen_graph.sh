#!/bin/sh

dotGraph=$1
pdfGraph=$dotGraph.pdf
dot -Tpdf $dotGraph > $pdfGraph
acroread $pdfGraph

