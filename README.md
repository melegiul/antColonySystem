# AntColonySystem

An algorithm for solving the Traveling Salesman Problem (TSP) based on 
"Ant Colony System: A Cooperative Learning Approach to the Traveling Salesman Problem"
Marco Dorigo and Luca Maria Gambardella.

The project is divided in four modules:
1. The main module acts as a controler and delegates the sequence
of the other modules, which the main module depends on.
2. The tspGenerator module is responsible for creating problem instances.
Each problem instance is represented by a generic matrix.
3. The ants module encapsulates all methods, which deal with computing
the solution for a specific problem instance.
4. The exhaustive module is not incorporated in the jar file. It has the same
purpose like the ants module, but uses a different approach.

The size of a problem instance and other parameters can be customized.
For trying out the algorithm just execute the acstsp.jar file
in directory out/artifacts (Java 11 required).

During execution the problem instance and the shortest distance are printed
to the console and the measured entropy values are written to the log files.
