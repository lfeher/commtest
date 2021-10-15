package testing;

import java.util.List;

public class Main {

    private static final int[] VALUES = {0, 1, 1, 1, 0, 0, 0, 1, 2, 0};

    public static void main(String[] args) {

        //calculateSum(7);
        int moves = moves(List.of(2, 3, 4, 5, 6, 7, 8, 2, 4, 8, 3, 11, 23));
        //int numberOfDroppedRequests = ThrottlingGateway.droppedRequests(List.of(1, 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 7, 7, 11, 11, 11, 11));
        //int numberOfDroppedRequests = ThrottlingGateway.droppedRequests(List.of(62, 62, 62));
        System.out.println(moves);
    }

    private static int calculateSum(int number) {
        return String.valueOf(number).chars().map(x -> x - '0').map(n -> VALUES[n]).sum();
    }

    private static int moves(List<Integer> numbers) {
        int minNumberOfMoves = 0;
        int i = 0;
        int j = numbers.size() - 1;
        while (i < j) {
            while (!isOdd(numbers.get(i))) {
                i++;
            }
            while (isOdd(numbers.get(j))) {
                j--;
            }
            if (i < j) {
                minNumberOfMoves++;
                i++;
                j--;
            }
        }
        return minNumberOfMoves;
    }

    private static boolean isOdd(int number) {
        return number % 2 != 0;
    }
}
