package ru.selestrel.challenge.aoc2023.day2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Map<String, Integer> bag = Map.of("red", 12, "green", 13, "blue", 14);

        int result = 0;
        int sumOfPower = 0;

        File input = new File("src/main/java/ru/selestrel/challenge/aoc2023/day2/input.txt");
        try {
            Scanner reader = new Scanner(input);

            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                String gameCases = data.split(":")[1];
                Map<String, Integer> fewestCubesMap = composeFewestCubesMap(gameCases);

                //first puzzle
                int gameId = Integer.parseInt(data.split(":")[0].split(" ")[1]);
                boolean isPossible = isGamePossible(bag, fewestCubesMap);
                result += isPossible ? gameId : 0;

                //second puzzle
                sumOfPower += composeFewestCubesMap(gameCases).values().stream()
                        .reduce(1, (a, b) -> a * b);
            }

            reader.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        System.out.println("First puzzle result: " + result);
        System.out.println("Second puzzle result: " + sumOfPower);
    }

    private static Map<String, Integer> composeFewestCubesMap(String gameDescription) {
        Map<String, Integer> map = new HashMap<>();
        Arrays.stream(gameDescription.split(";"))
                .flatMap(c -> Arrays.stream(c.split(","))).map(String::trim)
                .map(c -> c.split(" "))
                .forEach(cube -> {
                    String key = cube[1];
                    Integer value = Integer.valueOf(cube[0]);
                    map.put(cube[1], (map.get(key) == null || map.get(key) < value)
                            ? value : map.get(key));
                });
        return map;
    }

    private static boolean isGamePossible(Map<String, Integer> bag, Map<String, Integer> game) {
        return bag.keySet().stream().allMatch(key -> bag.get(key) >= (game.get(key) == null ? 0 : game.get(key)));
    }
}
