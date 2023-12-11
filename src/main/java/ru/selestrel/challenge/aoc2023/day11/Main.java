package ru.selestrel.challenge.aoc2023.day11;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Puzzle 1
 *
 * You continue following signs for "Hot Springs" and eventually come across an observatory. The Elf within turns out to
 * be a researcher studying cosmic expansion using the giant telescope here.
 * He doesn't know anything about the missing machine parts; he's only visiting for this research project. However, he
 * confirms that the hot springs are the next-closest area likely to have people; he'll even take you straight there
 * once he's done with today's observation analysis.
 * Maybe you can help him with the analysis to speed things up?
 * The researcher has collected a bunch of data and compiled the data into a single giant image (your puzzle input).
 * The image includes empty space (.) and galaxies (#). For example:
 * ...#......
 * .......#..
 * #.........
 * ..........
 * ......#...
 * .#........
 * .........#
 * ..........
 * .......#..
 * #...#.....
 * The researcher is trying to figure out the sum of the lengths of the shortest path between every pair of galaxies.
 * However, there's a catch: the universe expanded in the time it took the light from those galaxies to reach the
 * observatory.
 * Due to something involving gravitational effects, only some space expands. In fact, the result is that any rows or
 * columns that contain no galaxies should all actually be twice as big.
 * In the above example, three columns and two rows contain no galaxies:
 *    v  v  v
 *  ...#......
 *  .......#..
 *  #.........
 * >..........<
 *  ......#...
 *  .#........
 *  .........#
 * >..........<
 *  .......#..
 *  #...#.....
 *    ^  ^  ^
 * These rows and columns need to be twice as big; the result of cosmic expansion therefore looks like this:
 * ....#........
 * .........#...
 * #............
 * .............
 * .............
 * ........#....
 * .#...........
 * ............#
 * .............
 * .............
 * .........#...
 * #....#.......
 * Equipped with this expanded universe, the shortest path between every pair of galaxies can be found. It can help to
 * assign every galaxy a unique number:
 * ....1........
 * .........2...
 * 3............
 * .............
 * .............
 * ........4....
 * .5...........
 * ............6
 * .............
 * .............
 * .........7...
 * 8....9.......
 * In these 9 galaxies, there are 36 pairs. Only count each pair once; order within the pair doesn't matter. For each
 * pair, find any shortest path between the two galaxies using only steps that move up, down, left, or right exactly
 * one . or # at a time. (The shortest path between two galaxies is allowed to pass through another galaxy.)
 * For example, here is one of the shortest paths between galaxies 5 and 9:
 * ....1........
 * .........2...
 * 3............
 * .............
 * .............
 * ........4....
 * .5...........
 * .##.........6
 * ..##.........
 * ...##........
 * ....##...7...
 * 8....9.......
 * This path has length 9 because it takes a minimum of nine steps to get from galaxy 5 to galaxy 9 (the eight locations
 * marked # plus the step onto galaxy 9 itself). Here are some other example shortest path lengths:
 * Between galaxy 1 and galaxy 7: 15
 * Between galaxy 3 and galaxy 6: 17
 * Between galaxy 8 and galaxy 9: 5
 * In this example, after expanding the universe, the sum of the shortest path between all 36 pairs of galaxies is 374.
 * Expand the universe, then find the length of the shortest path between every pair of galaxies. What is the sum of
 * these lengths?
 *
 * Puzzle 2
 * The galaxies are much older (and thus much farther apart) than the researcher initially estimated.
 * Now, instead of the expansion you did before, make each empty row or column one million times larger. That is, each
 * empty row should be replaced with 1000000 empty rows, and each empty column should be replaced with 1000000 empty
 * columns.
 * (In the example above, if each empty row or column were merely 10 times larger, the sum of the shortest paths between
 * every pair of galaxies would be 1030. If each empty row or column were merely 100 times larger, the sum of the
 * shortest paths between every pair of galaxies would be 8410. However, your universe will need to expand far beyond
 * these values.)
 * Starting with the same initial image, expand the universe according to these new rules, then find the length of the
 * shortest path between every pair of galaxies. What is the sum of these lengths?
 */

public class Main {

    public static void main(String[] args) {

        File input = new File("src/main/java/ru/selestrel/challenge/aoc2023/day11/input.txt");

        List<List<String>> image = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(input);

            while (scanner.hasNext()) {
                image.add(new ArrayList<>(Arrays.stream(scanner.nextLine().split("")).toList()));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<Integer> emptyRowsIndices = IntStream.range(0, image.size())
                .filter(i -> !image.get(i).stream().reduce("", String::concat).contains("#")).boxed().toList();
        List<Integer> emptyColumnIndices = new ArrayList<>(IntStream.range(0, image.get(0).size()).boxed().toList());
        for (List<String> row : image) {
            for (int i = 0; i < row.size(); i++) {
                if (row.get(i).equals("#")) {
                    int finalI = i;
                    emptyColumnIndices.removeIf(index -> index == finalI);
                }
            }
        }

        //Puzzle 1
        List<List<Integer>> galaxiesCoordinates = getGalaxiesCoordinates(image).stream()
                .map(c -> getExpandedGalaxyCoordinates(c, emptyRowsIndices, emptyColumnIndices, 1)).toList();

        System.out.println("First puzzle result: " + LongStream.range(0, galaxiesCoordinates.size())
                .map(i -> calculatePathSum(galaxiesCoordinates, (int) i)).reduce(0, Long::sum)); //9723824

        //Puzzle 2
        List<List<Integer>> galaxiesCoordinates2 = getGalaxiesCoordinates(image).stream()
                .map(c -> getExpandedGalaxyCoordinates(c, emptyRowsIndices, emptyColumnIndices, 999999)).toList();

        System.out.println("Second puzzle result: " + LongStream.range(0, galaxiesCoordinates2.size())
                .map(i -> calculatePathSum(galaxiesCoordinates2, (int) i)).reduce(0, Long::sum)); //9723824
    }

    private static List<Integer> getExpandedGalaxyCoordinates(List<Integer> galaxyCoordinates,
                                                              List<Integer> emptyRowIndices,
                                                              List<Integer> emptyColumnIndices,
                                                              int factor) {
        int coord1 = galaxyCoordinates.get(0);
        int coord2 = galaxyCoordinates.get(1);
        return List.of(coord1 + (int) emptyRowIndices.stream().filter(row -> row < coord1).count() * factor,
                coord2 + (int) emptyColumnIndices.stream().filter(row -> row < coord2).count() * factor);
    }

    private static long calculatePathSum(List<List<Integer>> galaxiesCoordinates, int galaxyIndex) {
        List<Integer> currentGalaxyCoordinates = galaxiesCoordinates.get(galaxyIndex);
        return galaxiesCoordinates.stream().skip(galaxyIndex)
                .map(coords -> calculatePath(currentGalaxyCoordinates, coords))
                .reduce(0L, Long::sum);
    }

    private static long calculatePath(List<Integer> first, List<Integer> last) {
        return Math.abs(first.get(0) - last.get(0)) + Math.abs(first.get(1) - last.get(1));
    }

    private static List<List<Integer>> getGalaxiesCoordinates(List<List<String>> image) {
        List<List<Integer>> coordinates = new ArrayList<>();
        for (int i = 0; i < image.size(); i++) {
            List<String> row = image.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (row.get(j).equals("#")) {
                    coordinates.add(List.of(i, j));
                }
            }
        }

        return coordinates;
    }
}
