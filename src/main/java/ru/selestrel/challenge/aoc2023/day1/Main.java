package ru.selestrel.challenge.aoc2023.day1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

enum Digit{
    ZERO("0"),
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9");

    private String digit;

    Digit(String digit) {
        this.digit = digit;
    }

    public String getDigit() {
        return digit;
    }

}

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        File input = new File("src/main/java/ru/selestrel/challenge/aoc2023/day1/input.txt");
        Scanner reader = new Scanner(input);

        long result = 0;
        while (reader.hasNextLine()) {
            String data = reader.nextLine();
            result += Integer.parseInt(findFirstAndLastDigits(data));
        }

        System.out.println(result);

        reader.close();
    }

    private static String findFirstAndLastDigits(String string) {
        List<String> searchValues = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "zero",
                "one", "two", "three", "four", "five", "six", "seven", "eight", "nine");
        String last = "";
        int lastIndex = 0;
        String first = "";
        int firstIndex = string.length();
        for (String search : searchValues) {
            int index = string.indexOf(search);
            if (index != -1 && index <= firstIndex) {
                firstIndex = index;
                first = search;
            }
            index = string.lastIndexOf(search);
            if (index >= lastIndex) {
                lastIndex = index;
                last = search;
            }
        }

        if (first.length() > 1) {
            first = Digit.valueOf(first.toUpperCase()).getDigit();
        }

        if (last.length() > 1) {
            last = Digit.valueOf(last.toUpperCase()).getDigit();
        }

        return first + last;
    }
}
