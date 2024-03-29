package main;

import acs.AntColony;
import acs.Tuple;
import exhaustive.ExhaustiveSearch;
import generator.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Main {

    /**
     * approach, which searches a solution for the traveling salesman problem
     * by trying all possible routes
     */
    private static void exhaustive() {
        int numberOfCities = 10;
        long seed;
        long [] elapsedTimes = new long[10];
        long elapsedSum = 0;
        for(seed=1; seed<=10; seed++) {
            // initial settings
            Matrix matrix = new Matrix(numberOfCities);
            ExhaustiveSearch search = new ExhaustiveSearch(matrix);
            search.matrixInit(seed);
            matrix.printMatrix();
            ArrayList<Integer> unvisited = new ArrayList<>(numberOfCities);
            for (int i = 0; i < numberOfCities; i++) {
                unvisited.add(Integer.valueOf(i));
            }
            // start computing the problem
            long startTime = System.nanoTime();
            search.calculateRoute(unvisited, new ArrayList<>(numberOfCities));
            elapsedTimes[(int)(seed-1)] = System.nanoTime() - startTime;
            elapsedSum += elapsedTimes[(int)(seed-1)];
            // summarize results
            System.out.println("Shortest Distance: " + search.getShortestDistance());
            System.out.println("Shortest Route: " + search.getShortestRoute());
            System.out.println();
        }
        System.out.println("Average Elapsed Time in sec: " + elapsedSum/(1e9*10));
    }

    public static void main(String[] args) {
//        exhaustive();
        if (args.length != 9) {
            // overwrite invalid arguments
            args = new String[]{"20", "1", "200", "10", "0.9", "2.0", "0.1", "0.1", "0.001"};
        }
        int numberCities = Integer.parseInt(args[0]);
        int seed = Integer.parseInt(args[1]);
        int iterations = Integer.parseInt(args[2]);
        // hyperparameter
        int numberAnts = Integer.parseInt(args[3]);
        // importance of exploration vs exploitation
        double q = Double.parseDouble(args[4]);
        // importance of pheromone vs distance
        double beta = Double.parseDouble(args[5]);
        // decay of pheromone (global update rule)
        double alpha = Double.parseDouble(args[6]);
        // decay of pheromone (local update rule)
        double rho = Double.parseDouble(args[7]);
        // initial pheromone per edge
        double tau = Double.parseDouble(args[8]);

        int experimentsNum = 10;
        // list for the best distance of each experiment and their corresponding tour
        List<Integer> distanceResult = new ArrayList(experimentsNum);
        List<List> tourResult = new ArrayList<List>(experimentsNum);
        // four matrices, each comprise a column for each experiment
        // and a row for each measured emergence value (time step)
        // these are needed for computing mean values
        List<List<Tuple>> distanceList = new ArrayList<>(experimentsNum);
        List<List<Tuple>> posEmergList = new ArrayList<>(experimentsNum);
        List<List<Tuple>> routEmergList = new ArrayList<>(experimentsNum);
        List<List<Tuple>> pheroEmergList = new ArrayList<>(experimentsNum);
        // average values for each time step
        List<Double> positionAverage = new ArrayList<>();
        List<Double> routeAverage = new ArrayList<>();
        List<Double> pheromoneAverage = new ArrayList<>();
        // variables for performance measures
        long[] elapsedTime = new long[10];
        long elapsedSum = 0;
        long acsSeed;
        long startTime;
        // create and initiate a single matrix as representation of the distances between all cities
        Matrix.initSeed(seed);
        Matrix<Integer> distanceMatrix = new Matrix<>(numberCities);
        distanceMatrix.matrixInit();
        distanceMatrix.printMatrix();
        // each experiment is executed with different seeds
        for (acsSeed=1; acsSeed<=experimentsNum; acsSeed++) {
            // create a new pheromone matrix
            Matrix<Double> pheromoneMatrix = new Matrix<>(numberCities);
            // start measuring time
            startTime = System.nanoTime();
            AntColony acs = new AntColony(distanceMatrix, pheromoneMatrix, numberAnts, iterations, q, beta, alpha, rho, tau, acsSeed);
            // start computing traveling salesman problem
            acs.antColonySystem();
            //end measuring time
            elapsedTime[(int)(acsSeed-1)] = System.nanoTime() - startTime;
            elapsedSum += elapsedTime[(int)(acsSeed-1)];
            // retrieve and store shortest distance of all iterations
            distanceResult.add(acs.getBestIterationDistance());
            // retrieve and store shortest path of all iterations
            tourResult.add(acs.getBestIterationTour());
            // retrieve and store shortest distance for each iteration
            distanceList.add(acs.getShortestDistances());
            posEmergList.add(acs.getPositionEmergence());
            routEmergList.add(acs.getRouteEmergence());
            pheroEmergList.add(acs.getPheromoneEmergence());
//            System.out.println("Best Iterations Distance: " + acs.getBestIterationDistance());
//            System.out.println("Best Iterations tour: " + acs.getBestIterationTour());
//            System.out.println();
        }
        // a tree set simplifies the calculation of the expected value
        TreeSet<Integer> resultSet = new TreeSet<>(distanceResult);
        Double expectedValue = AntColony.expectedValue(distanceResult,resultSet,10);
        int index = distanceResult.indexOf(resultSet.first());
        // print the results of the experiments
        System.out.println("Average elapsed time in sec: " + elapsedSum/(1e9*10));
        System.out.println("Shortest Distance: " + resultSet.first());
        System.out.println("Expected Value: " + expectedValue);
        System.out.println("Shortest Tour: " + tourResult.get(index));
        // computing average for all experiments
        AntColony.averageValue(distanceList, "distance");
        positionAverage.addAll(AntColony.averageValue(posEmergList, "position"));
        routeAverage.addAll(AntColony.averageValue(routEmergList, "route"));
        pheromoneAverage.addAll(AntColony.averageValue(pheroEmergList, "pheromone"));
        // compute the absolute difference of entropy between first and last iteration
        Double positionAbsolute = Math.abs(positionAverage.get(positionAverage.size()-1)-positionAverage.get(1));
        Double routeAbsolute = Math.abs(routeAverage.get(routeAverage.size()-1)-routeAverage.get(1));
        Double pheromoneAbsolute = Math.abs(pheromoneAverage.get(pheromoneAverage.size()-1)-pheromoneAverage.get(1));
        // write absolute values to log file
        AntColony.writeKiviatCSV(positionAbsolute,routeAbsolute,pheromoneAbsolute);
        return;

    }
}
