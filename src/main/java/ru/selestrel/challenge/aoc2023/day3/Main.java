package ru.selestrel.challenge.aoc2023.day3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Puzzle 1
 *
 * You and the Elf eventually reach a gondola lift station; he says the gondola lift will take you up to the water
 * source, but this is as far as he can bring you. You go inside.
 * It doesn't take long to find the gondolas, but there seems to be a problem: they're not moving.
 * "Aaah!"
 * You turn around to see a slightly-greasy Elf with a wrench and a look of surprise. "Sorry, I wasn't expecting anyone!
 * The gondola lift isn't working right now; it'll still be a while before I can fix it." You offer to help.
 * The engineer explains that an engine part seems to be missing from the engine, but nobody can figure out which one.
 * If you can add up all the part numbers in the engine schematic, it should be easy to work out which part is missing.
 * The engine schematic (your puzzle input) consists of a visual representation of the engine. There are lots of numbers
 * and symbols you don't really understand, but apparently any number adjacent to a symbol, even diagonally, is a
 * "part number" and should be included in your sum.
 *
 * (Periods (.) do not count as a symbol.)
 * Here is an example engine schematic:
 * 467..114..
 * ...*......
 * ..35..633.
 * ......#...
 * 617*......
 * .....+.58.
 * ..592.....
 * ......755.
 * ...$.*....
 * .664.598..
 *
 * In this schematic, two numbers are not part numbers because they are not adjacent to a symbol:
 * 114 (top right) and 58 (middle right). Every other number is adjacent to a symbol and so is a
 * part number; their sum is 4361.
 *
 * Puzzle 2
 * The engineer finds the missing part and installs it in the engine! As the engine springs to life,
 * you jump in the closest gondola, finally ready to ascend to the water source.
 * You don't seem to be going very fast, though. Maybe something is still wrong? Fortunately, the gondola has a phone
 * labeled "help", so you pick it up and the engineer answers.
 * Before you can explain the situation, she suggests that you look out the window. There stands the engineer, holding
 * a phone in one hand and waving with the other. You're going so slowly that you haven't even left the station. You
 * exit the gondola.
 * The missing part wasn't the only issue - one of the gears in the engine is wrong. A gear is any * symbol that is
 * adjacent to exactly two part numbers. Its gear ratio is the result of multiplying those two numbers together.
 * This time, you need to find the gear ratio of every gear and add them all up so that the engineer can figure out
 * which gear needs to be replaced.
 *
 * Consider the same engine schematic again:
 *
 * 467..114..
 * ...*......
 * ..35..633.
 * ......#...
 * 617*......
 * .....+.58.
 * ..592.....
 * ......755.
 * ...$.*....
 * .664.598..
 * In this schematic, there are two gears. The first is in the top left; it has part numbers 467 and 35, so its gear
 * ratio is 16345. The second gear is in the lower right; its gear ratio is 451490. (The * adjacent to 617 is not a gear
 * because it is only adjacent to one part number.) Adding up all the gear ratios produces 467835.
 *
 * What is the sum of all the gear ratios in your engine schematic?
 */

public class Main {

    private static final String NOT_SYMBOLS = ".0123456789";
    private static final String DIGITS = "0123456789";

    public static void main(String[] args) {

        File input = new File("src/main/java/ru/selestrel/challenge/aoc2023/day3/input.txt");
        Scanner reader;

        int sumOfParts = 0;
        int sumOfGearRatio = 0;
        try {
            reader = new Scanner(input);
            String previous = null;
            String current = reader.nextLine();
            String next = reader.hasNext() ? reader.nextLine() : null;
            do {
                sumOfParts += getSumOfParts(previous, current, next);
                sumOfGearRatio += getSumOfGearRatios(previous, current, next);
                previous = current;
                current = next;
                next = reader.hasNext() ? reader.nextLine() : null;
            } while (current != null);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        System.out.println(sumOfParts);
        System.out.println(sumOfGearRatio);

        reader.close();
    }

    /**
     * Calculate sum of part numbers in current line. Previous and next lines are needed to find part numbers that
     * connected to top/bottom symbols
     *
     * @param previous previous line of input
     * @param current current line of input
     * @param next next lint of input
     * @return sum of part numbers
     */
    private static int getSumOfParts(String previous, String current, String next) {
        int sumOfLineParts = 0;
        int stringLength = current.length();
        int numberStart = -1;
        int numberEnd = -1;
        boolean isConnected = false;

        for (int i = 0; i < stringLength; i++) {
            char ch = current.charAt(i);

            if (Character.isDigit(ch)) {
                //check is any digit of the number connected to symbol
                isConnected = isConnected || verifySymbolsAround(current, i, false)
                        || (previous != null && verifySymbolsAround(previous, i, true))
                        || (next != null && verifySymbolsAround(next, i, true));
                //remember the number start
                numberStart = numberStart == -1 ? i : numberStart;
                //remember and update number end
                numberEnd = i;
                //add numbers at the end of the line
                if (i == stringLength - 1) {
                    sumOfLineParts += isConnected ? Integer.parseInt(current.substring(numberStart, numberEnd + 1)) : 0;
                }
            } else  {
                //add numbers to overall sum
                sumOfLineParts += isConnected ? Integer.parseInt(current.substring(numberStart, numberEnd + 1)) : 0;
                //restore default parameter values
                isConnected = false;
                numberStart = -1;
                numberEnd = -1;
            }
        }

        return sumOfLineParts;
    }

    /**
     * Calculate sum of gear ratios in current line. Previous and next lines are needed to find gears that connected to
     * top/bottom parts
     *
     * @param previous previous line of input
     * @param current current line of input
     * @param next next lint of input
     * @return sum of gear ratios
     */
    private static int getSumOfGearRatios(String previous, String current, String next) {
        List<String> adjustedNumbers = new ArrayList<>();
        int sumOfGearRatiosInLine = 0;

        for (int i = 0; i < current.length(); i++) {
            if (current.charAt(i) == '*') {
                if (previous != null) {
                    adjustedNumbers.addAll(getAdjustedNumbers(previous, i));
                }
                adjustedNumbers.addAll(getAdjustedNumbers(current, i));
                if (adjustedNumbers.size() < 3 && next != null) {
                    adjustedNumbers.addAll(getAdjustedNumbers(next, i));
                }
                if (adjustedNumbers.size() == 2) {
                    sumOfGearRatiosInLine += adjustedNumbers.stream().map(Integer::parseInt)
                            .reduce(1, (acc, a) -> acc * a);
                }
                adjustedNumbers.clear();
            }
        }

        return sumOfGearRatiosInLine;
    }

    /**
     * Find all numbers adjusted to index (e.g. number last digit in index - 1 position, number first digit in index + 1
     * position or any number digit in index position)
     *
     * @return list of adjusted numbers (1 or 2 or null)
     */
    private static List<String> getAdjustedNumbers(String line, int index) {
        List<String> adjustedNumbers = new ArrayList<>();

        if (isCharacterInSequence(DIGITS, line.charAt(index))) {
            adjustedNumbers.add(extractNumberFromString(line, index));
        } else {
            adjustedNumbers.add(extractNumberFromString(line, index - 1));
            adjustedNumbers.add(extractNumberFromString(line, index + 1));
        }

        return adjustedNumbers.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Extract number that have digit in index position
     *
     * @return number as string
     */
    private static String extractNumberFromString(String line, int index) {
        String number = null;
        int lineLength = line.length();
        if (index >= 0 && index < lineLength && isCharacterInSequence(DIGITS, line.charAt(index))) {
            int numberStart = index;
            int numberEnd = index;
            while (true) {
                int exit = 0;
                if (numberStart > -1 && isCharacterInSequence(DIGITS, line.charAt(numberStart))) {
                    numberStart = numberStart - 1;
                    exit = exit + numberStart == -1 ? 0 : 1;
                }
                if (numberEnd < lineLength && isCharacterInSequence(DIGITS, line.charAt(numberEnd))) {
                    numberEnd = numberEnd + 1;
                    exit = exit + numberEnd == lineLength ? 0 : 1;
                }
                if (exit == 0) {
                    break;
                }
            }
            number = line.substring(numberStart + 1, numberEnd);
        }

        return number;
    }

    /**
     * Verify is any character around our is a symbol (3 positions: index - 1, index, index + 1)
     * @param lineToVerify string to verify
     * @param index central position
     * @param verifyIndexPosition false if central position should not be verified
     * @return boolean value is positions contains symbol
     */
    private static boolean verifySymbolsAround(String lineToVerify, int index, boolean verifyIndexPosition) {
        int prevIndex = index - 1;
        int nextIndex = index + 1;
        return (prevIndex > 0 && !isCharacterInSequence(NOT_SYMBOLS, lineToVerify.charAt(prevIndex)))
                || (verifyIndexPosition && !isCharacterInSequence(NOT_SYMBOLS, lineToVerify.charAt(index)))
                || (nextIndex < lineToVerify.length() && !isCharacterInSequence(NOT_SYMBOLS, lineToVerify.charAt(nextIndex)));
    }

    /**
     * @return true if character in the sequence
     */
    private static boolean isCharacterInSequence(String sequence, char character) {
        return sequence.indexOf(character) != -1;
    }
}
