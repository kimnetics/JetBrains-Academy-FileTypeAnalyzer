# JetBrains Academy File Type Analyzer Project

An example of a passing solution to the final phase of the JetBrains Academy Java File Type Analyzer project.

## Description

This project is a command line application that scans a directory of files and compares the files against a list of patterns to identify the type of the files based on their contents.

The project introduced several pattern matching algorithms. It started with a Naive approach, moved on to the Knuth–Morris–Pratt algorithm
and then the Rabin–Karp algorithm.

Over several phases, the project also increased the scale of the pattern matching being done. It started with a single pattern against a single file and finished with a list of patterns against a directory of files.

The project introduced multithreading as a way to improve response times of the pattern matching being done. In the final version of the code, pattern matching is done using threads in a thread pool. They monitor and pull files to analyze from a queue loaded by the main program.

## Notes

The relative directory structure was kept the same as the one used in my JetBrains Academy solution.

The program logs to a file called "analyzer.log" in your home folder.

The program uses two command line arguments:
1. Path to a directory of files to analyze.
2. Path to a file containing patterns to apply.

It can be started with a command similar to the following:

```
java analyzer.Main ~/Temp ./resources/patterns.db
```

The patterns.db file in the resource directory was supplied by JetBrains Academy and provides an example of a pattern file.

This program has quite a lot of debug logging available for the Knuth–Morris–Pratt algorithm. It can be turned on by setting the Main.DEBUG_MODE flag to true.

In support of the final phase, the program was hardcoded to only apply the Rabin-Karp algorithm. The previously used Naive and Knuth–Morris–Pratt algorithms remain for possible future use.

Also, in support of the final phase, the  StreamBuffer was adjusted to pretty much read an entire file into the buffer at once. This made it work more naturally with the Rabin-Karp algorithm examples I saw, which wanted to know the end of file location. StreamBuffer provides a circular buffer and was intended to allow us to not have to read an entire file before we can begin analyzing the file. The Naive and Knuth–Morris–Pratt routines used it to analyze while streaming through a file.

If I were going to make futher improvements to this project, I would see if it would be possible to adjust the Rabin-Karp algorithm to work with an in progress stream. After doing this, I would make StreamBuffer not read very much ahead of the substring index, if it reads ahead at all. I also would like to make StreamBuffer support not having to reread a file if we already have the file loaded from a previous run through.
