# JetBrains Academy File Type Analyzer Project

An example of a passing solution to the final phase of the JetBrains Academy Java File Type Analyzer project.

## Description

The relative directory structure was kept the same as the one used in my JetBrains Academy solution.

The program logs to a file called "analyzer.log" in your home folder.

The program uses two command line arguments:
1. Path to a directory of files to analyze.
2. Path to a file containing patterns to apply.

The patterns.db file in the resource directory was supplied by JetBrains Academy and provides an example of a pattern file.

This program has quite a lot of debug logging available for the Knuth–Morris–Pratt algorithm. It can be turned on by setting the Main.DEBUG_MODE flag to true.

In support of the final phase, the program was hardcoded to only apply the Rabin-Karp algorithm. The previously used Naive and Knuth–Morris–Pratt algorithms remain for possible future use.
