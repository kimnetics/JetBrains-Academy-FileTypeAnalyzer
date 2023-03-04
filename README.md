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

Also, in support of the final phase, the  StreamBuffer was adjusted to pretty much read an entire file into the buffer at once. This made it work more naturally with the Rabin-Karp algorithm examples I saw, which wanted to know the end of file location. StreamBuffer provides a circular buffer and was intended to allow us to not have to read an entire file before we can begin analyzing the file. The Naive and Knuth–Morris–Pratt routines used it to analyze while streaming through a file.

If I were going to make futher improvements to this project, I would see if it would be possible to adjust the Rabin-Karp algorithm to work with an in progress stream. After doing this, I would make StreamBuffer not read very much ahead of the substring index, if it reads ahead at all. I also would like to make StreamBuffer support not having to reread a file if we already have the file loaded from a previous run through.
