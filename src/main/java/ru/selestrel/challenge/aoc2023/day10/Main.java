package ru.selestrel.challenge.aoc2023.day10;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static ru.selestrel.challenge.aoc2023.day10.Direction.*;

/**
 * Puzzle 1
 * You use the hang glider to ride the hot air from Desert Island all the way up to the floating metal island. This
 * island is surprisingly cold and there definitely aren't any thermals to glide on, so you leave your hang glider
 * behind.
 * You wander around for a while, but you don't find any people or animals. However, you do occasionally find signposts
 * labeled "Hot Springs" pointing in a seemingly consistent direction; maybe you can find someone at the hot springs and
 * ask them where the desert-machine parts are made.
 * The landscape here is alien; even the flowers and trees are made of metal. As you stop to admire some metal grass,
 * you notice something metallic scurry away in your peripheral vision and jump into a big pipe! It didn't look like any
 * animal you've ever seen; if you want a better look, you'll need to get ahead of it.
 * Scanning the area, you discover that the entire field you're standing on is densely packed with pipes; it was hard to
 * tell at first because they're the same metallic silver color as the "ground". You make a quick sketch of all of the
 * surface pipes you can see (your puzzle input).
 * The pipes are arranged in a two-dimensional grid of tiles:
 * | is a vertical pipe connecting north and south.
 * - is a horizontal pipe connecting east and west.
 * L is a 90-degree bend connecting north and east.
 * J is a 90-degree bend connecting north and west.
 * 7 is a 90-degree bend connecting south and west.
 * F is a 90-degree bend connecting south and east.
 * . is ground; there is no pipe in this tile.
 * S is the starting position of the animal; there is a pipe on this tile, but your sketch doesn't show what shape the
 * pipe has.
 * Based on the acoustics of the animal's scurrying, you're confident the pipe that contains the animal is one large,
 * continuous loop.
 * For example, here is a square loop of pipe:
 * .....
 * .F-7.
 * .|.|.
 * .L-J.
 * .....
 * If the animal had entered this loop in the northwest corner, the sketch would instead look like this:
 * .....
 * .S-7.
 * .|.|.
 * .L-J.
 * .....
 * In the above diagram, the S tile is still a 90-degree F bend: you can tell because of how the adjacent pipes connect
 * to it.
 * Unfortunately, there are also many pipes that aren't connected to the loop! This sketch shows the same loop as above:
 * -L|F7
 * 7S-7|
 * L|7||
 * -L-J|
 * L|-JF
 * In the above diagram, you can still figure out which pipes form the main loop: they're the ones connected to S, pipes
 * those pipes connect to, pipes those pipes connect to, and so on. Every pipe in the main loop connects to its two
 * neighbors (including S, which will have exactly two pipes connecting to it, and which is assumed to connect back to
 * those two pipes).
 * Here is a sketch that contains a slightly more complex main loop:
 * ..F7.
 * .FJ|.
 * SJ.L7
 * |F--J
 * LJ...
 * Here's the same example sketch with the extra, non-main-loop pipe tiles also shown:
 * 7-F7-
 * .FJ|7
 * SJLL7
 * |F--J
 * LJ.LJ
 * If you want to get out ahead of the animal, you should find the tile in the loop that is farthest from the starting
 * position. Because the animal is in the pipe, it doesn't make sense to measure this by direct distance. Instead, you
 * need to find the tile that would take the longest number of steps along the loop to reach from the starting point -
 * regardless of which way around the loop the animal went.
 * In the first example with the square loop:
 * .....
 * .S-7.
 * .|.|.
 * .L-J.
 * .....
 * You can count the distance each tile in the loop is from the starting point like this:
 * .....
 * .012.
 * .1.3.
 * .234.
 * .....
 * In this example, the farthest point from the start is 4 steps away.
 * Here's the more complex loop again:
 * ..F7.
 * .FJ|.
 * SJ.L7
 * |F--J
 * LJ...
 * Here are the distances for each tile on that loop:
 * ..45.
 * .236.
 * 01.78
 * 14567
 * 23...
 * Find the single giant loop starting at S. How many steps along the loop does it take to get from the starting
 * position to the point farthest from the starting position?
 *
 * Puzzle 2
 *
 * You quickly reach the farthest point of the loop, but the animal never emerges. Maybe its nest is within the area
 * enclosed by the loop?
 * To determine whether it's even worth taking the time to search for such a nest, you should calculate how many tiles
 * are contained within the loop. For example:
 * ...........
 * .S-------7.
 * .|F-----7|.
 * .||.....||.
 * .||.....||.
 * .|L-7.F-J|.
 * .|..|.|..|.
 * .L--J.L--J.
 * ...........
 * The above loop encloses merely four tiles - the two pairs of . in the southwest and southeast (marked I below). The
 * middle . tiles (marked O below) are not in the loop. Here is the same loop again with those regions marked:
 * ...........
 * .S-------7.
 * .|F-----7|.
 * .||OOOOO||.
 * .||OOOOO||.
 * .|L-7OF-J|.
 * .|II|O|II|.
 * .L--JOL--J.
 * .....O.....
 * In fact, there doesn't even need to be a full tile path to the outside for tiles to count as outside the loop -
 * squeezing between pipes is also allowed! Here, I is still within the loop and O is still outside the loop:
 * ..........
 * .S------7.
 * .|F----7|.
 * .||OOOO||.
 * .||OOOO||.
 * .|L-7F-J|.
 * .|II||II|.
 * .L--JL--J.
 * ..........
 * In both of the above examples, 4 tiles are enclosed by the loop.
 * Here's a larger example:
 * .F----7F7F7F7F-7....
 * .|F--7||||||||FJ....
 * .||.FJ||||||||L7....
 * FJL7L7LJLJ||LJ.L-7..
 * L--J.L7...LJS7F-7L7.
 * ....F-J..F7FJ|L7L7L7
 * ....L7.F7||L7|.L7L7|
 * .....|FJLJ|FJ|F7|.LJ
 * ....FJL-7.||.||||...
 * ....L---J.LJ.LJLJ...
 * The above sketch has many random bits of ground, some of which are in the loop (I) and some of which are outside
 * it (O):
 * OF----7F7F7F7F-7OOOO
 * O|F--7||||||||FJOOOO
 * O||OFJ||||||||L7OOOO
 * FJL7L7LJLJ||LJIL-7OO
 * L--JOL7IIILJS7F-7L7O
 * OOOOF-JIIF7FJ|L7L7L7
 * OOOOL7IF7||L7|IL7L7|
 * OOOOO|FJLJ|FJ|F7|OLJ
 * OOOOFJL-7O||O||||OOO
 * OOOOL---JOLJOLJLJOOO
 * In this larger example, 8 tiles are enclosed by the loop.
 * Any tile that isn't part of the main loop can count as being enclosed by the loop. Here's another example with many
 * bits of junk pipe lying around that aren't connected to the main loop at all:
 * FF7FSF7F7F7F7F7F---7
 * L|LJ||||||||||||F--J
 * FL-7LJLJ||||||LJL-77
 * F--JF--7||LJLJ7F7FJ-
 * L---JF-JLJ.||-FJLJJ7
 * |F|F-JF---7F7-L7L|7|
 * |FFJF7L7F-JF7|JL---7
 * 7-L-JL7||F7|L7F-7F7|
 * L.L7LFJ|||||FJL7||LJ
 * L7JLJL-JLJLJL--JLJ.L
 * Here are just the tiles that are enclosed by the loop marked with I:
 * FF7FSF7F7F7F7F7F---7
 * L|LJ||||||||||||F--J
 * FL-7LJLJ||||||LJL-77
 * F--JF--7||LJLJIF7FJ-
 * L---JF-JLJIIIIFJLJJ7
 * |F|F-JF---7IIIL7L|7|
 * |FFJF7L7F-JF7IIL---7
 * 7-L-JL7||F7|L7F-7F7|
 * L.L7LFJ|||||FJL7||LJ
 * L7JLJL-JLJLJL--JLJ.L
 * In this last example, 10 tiles are enclosed by the loop.
 * Figure out whether you have time to search for the nest by calculating the area within the loop. How many tiles are
 * enclosed by the loop?
 */

enum Direction {UP, DOWN, LEFT, RIGHT}

record Point(int coordinateX, int coordinateY, Direction direction, String symbol) {
    @Override
    public boolean equals(Object obj) {
        Point that = (Point) obj;
        return this.coordinateX == that.coordinateX && this.coordinateY == that.coordinateY;
    }
}

public class Main {

    public static void main(String[] args) {

        File input = new File("src/main/java/ru/selestrel/challenge/aoc2023/day10/input.txt");
        Point startPosition = null;
        List<List<String>> pipesMap = new ArrayList<>();

        int startYCoordinate = 0;
        String startPositionSymbol = "S";
        Map<Integer, List<Point>> loopMap = new HashMap<>();

        try {
            Scanner scanner = new Scanner(input);

            while (scanner.hasNext()) {
                String nextLine = scanner.nextLine();
                if (nextLine.contains(startPositionSymbol)) {
                    startPosition = new Point(nextLine.indexOf(startPositionSymbol), startYCoordinate,
                            null, startPositionSymbol);
                }
                pipesMap.add(List.of(nextLine.split("")));
                if (startPosition == null) {
                    startYCoordinate++;
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<Point> positions = List.of(new Point(startPosition.coordinateX(), startYCoordinate - 1, UP, "|"),
                new Point(startPosition.coordinateX(), startYCoordinate + 1, DOWN, "|"));
        loopMap.put(startYCoordinate, new ArrayList<>(List.of(new Point(startPosition.coordinateX(),
                startPosition.coordinateY(), UP, startPositionSymbol))));

        //Puzzle 1
        int stepCount = 1;

        do {
            positions.forEach(p -> {
                List<Point> points = loopMap.get(p.coordinateY());
                if (points == null) {
                    loopMap.put(p.coordinateY(), new ArrayList<>(List.of(p)));
                } else {
                    points.add(p);
                }
            });
            positions = List.of(getNextPoint(positions.get(0), pipesMap),
                    getNextPoint(positions.get(1), pipesMap));

            stepCount++;
        } while (!positions.get(0).equals(positions.get(1)));

        System.out.println("First puzzle result: " + stepCount);

        //Puzzle 2
        loopMap.get(positions.get(0).coordinateY()).add(positions.get(0));
        loopMap.values().forEach(v -> v.sort(Comparator.comparing(Point::coordinateX)));

        List<List<Point>> borders = loopMap.values().stream().map(Main::getBorderPoints).toList();

        List<Integer> tiles = borders.stream().map(Main::calculateEnclosedTilesCount).toList();
        System.out.println("Second puzzle result:" + tiles.stream().reduce(0, Integer::sum));
    }

    private static int calculateEnclosedTilesCount(List<Point> points) {
        int result = 0;
        boolean isInner = true;

        for (int i = 1; i < points.size(); i++) {
            Point currentPoint = points.get(i);
            Point previousPoint = points.get(i - 1);
            if (isInner) {
                result += needCalculateDistance(previousPoint, currentPoint)
                        ? currentPoint.coordinateX() - previousPoint.coordinateX() - 1 : 0;
            }
            if (!isSameSpace(previousPoint, currentPoint)) {
                isInner = !isInner;
            }

        }

        return result;
    }

    private static boolean isSameSpace(Point point1, Point point2) {
        return List.of("FJ", "L7").contains(point1.symbol() + point2.symbol());
    }

    private static boolean needCalculateDistance(Point point1, Point point2) {
        return List.of("||", "|L", "|F", "7|", "J|", "7F", "7L", "JF", "JL")
                .contains(point1.symbol() + point2.symbol());
    }

    private static List<Point> getBorderPoints(List<Point> linePoints) {
        return linePoints.stream().filter(point -> !point.symbol().equals("-")).toList();
    }

    private static Point getNextPoint(Point currentPoint, List<List<String>> map) {
        Direction currentPointDirection = currentPoint.direction();
        int[] nextPointCoordinates = switch (currentPointDirection) {
            case UP -> new int[] {currentPoint.coordinateX(), currentPoint.coordinateY() - 1};
            case DOWN -> new int[] {currentPoint.coordinateX(), currentPoint.coordinateY() + 1};
            case LEFT -> new int[] {currentPoint.coordinateX() - 1, currentPoint.coordinateY()};
            default -> new int[] {currentPoint.coordinateX() + 1, currentPoint.coordinateY()};
        };
        String nextPointSymbol = map.get(nextPointCoordinates[1]).get(nextPointCoordinates[0]);
        return new Point(nextPointCoordinates[0], nextPointCoordinates[1],
                getNextPointDirection(currentPointDirection, nextPointSymbol), nextPointSymbol);
    }

    private static Direction getNextPointDirection(Direction currentPointDirection, String nextPointSymbol) {
        return switch (currentPointDirection) {
            case UP -> switch (nextPointSymbol) {
                case "|" -> UP;
                case "F" -> RIGHT;
                case "7" -> LEFT;
                default -> DOWN;
            };
            case DOWN -> switch (nextPointSymbol) {
                case "|" -> DOWN;
                case "J" -> LEFT;
                case "L" -> RIGHT;
                default -> UP;
            };
            case LEFT -> switch (nextPointSymbol) {
                case "-" -> LEFT;
                case "L" -> UP;
                case "F" -> DOWN;
                default -> RIGHT;
            };
            default -> switch (nextPointSymbol) {
                case "-" -> RIGHT;
                case "J" -> UP;
                case "7" -> DOWN;
                default -> LEFT;
            };
        };
    }
}
