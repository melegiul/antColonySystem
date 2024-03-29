package exhaustive;

import generator.Matrix;

import java.util.ArrayList;
import java.util.Random;


public class ExhaustiveSearch {
    private Matrix<Integer> matrix;
    private ArrayList<Integer> shortestRoute;
    private int shortestDistance = Integer.MAX_VALUE;

    public ExhaustiveSearch(Matrix matrix) {
        this.matrix = matrix;
    }

    public ArrayList<Integer> getShortestRoute() {
        return shortestRoute;
    }

    public int getShortestDistance() {
        return shortestDistance;
    }

    public void calculateRoute(ArrayList<Integer> unvisited, ArrayList<Integer> currentRoute) {
        if(unvisited.isEmpty()) {
            int currentDistance = calculateDistance(currentRoute);
            if (currentDistance < this.shortestDistance) {
                this.shortestDistance = currentDistance;
                this.shortestRoute = new ArrayList<>(currentRoute);
            }
        } else {
            for(Integer i: unvisited) {
                ArrayList<Integer> nextUnvisited = new ArrayList<>(unvisited);
                ArrayList<Integer> nextCurrentRoute = new ArrayList<>(currentRoute);
                nextUnvisited.remove(i);
                nextCurrentRoute.add(i);
                calculateRoute(nextUnvisited, nextCurrentRoute);
            }
        }
    }

    private Integer calculateDistance(ArrayList<Integer> route) {
        Integer distance = Integer.valueOf(0);
        for(int i=1; i<matrix.getNumberOfCities(); i++) {
            distance = Integer.sum(distance, (int)matrix.getValue(route.get(i-1), route.get(i)));
        }

        return distance;
    }

    public void matrixInit(long seed) {
        Random distance = new Random(seed);
        int col;
        for(int row = 0; row<matrix.getNumberOfCities(); row++) {
            for(col=row+1; col<matrix.getNumberOfCities(); col++) {
                int value = distance.nextInt(1000)+1;
                matrix.setMatrixElement(row, col, value);
                matrix.setMatrixElement(col, row, value);
            }
        }
    }
}
