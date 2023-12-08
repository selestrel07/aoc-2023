package ru.selestrel.challenge.aoc2023.day8;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Puzzle 1
 * <p>
 * You're still riding a camel across Desert Island when you spot a sandstorm quickly approaching. When you turn to warn
 * the Elf, she disappears before your eyes! To be fair, she had just finished warning you about ghosts a few minutes
 * ago.
 * One of the camel's pouches is labeled "maps" - sure enough, it's full of documents (your puzzle input) about how to
 * navigate the desert. At least, you're pretty sure that's what they are; one of the documents contains a list of
 * left/right instructions, and the rest of the documents seem to describe some kind of network of labeled nodes.
 * It seems like you're meant to use the left/right instructions to navigate the network. Perhaps if you have the camel
 * follow the same instructions, you can escape the haunted wasteland!
 * After examining the maps for a bit, two nodes stick out: AAA and ZZZ. You feel like AAA is where you are now, and you
 * have to follow the left/right instructions until you reach ZZZ.
 * This format defines each node of the network individually. For example:
 * RL
 * AAA = (BBB, CCC)
 * BBB = (DDD, EEE)
 * CCC = (ZZZ, GGG)
 * DDD = (DDD, DDD)
 * EEE = (EEE, EEE)
 * GGG = (GGG, GGG)
 * ZZZ = (ZZZ, ZZZ)
 * Starting with AAA, you need to look up the next element based on the next left/right instruction in your input. In
 * this example, start with AAA and go right (R) by choosing the right element of AAA, CCC. Then, L means to choose the
 * left element of CCC, ZZZ. By following the left/right instructions, you reach ZZZ in 2 steps.
 * Of course, you might not find ZZZ right away. If you run out of left/right instructions, repeat the whole sequence of
 * instructions as necessary: RL really means RLRLRLRLRLRLRLRL... and so on. For example, here is a situation that takes
 * 6 steps to reach ZZZ:
 * LLR
 * AAA = (BBB, BBB)
 * BBB = (AAA, ZZZ)
 * ZZZ = (ZZZ, ZZZ)
 * Starting at AAA, follow the left/right instructions. How many steps are required to reach ZZZ?
 * <p>
 * Puzzle 2
 * <p>
 * The sandstorm is upon you and you aren't any closer to escaping the wasteland. You had the camel follow the
 * instructions, but you've barely left your starting position. It's going to take significantly more steps to escape!
 * What if the map isn't for people - what if the map is for ghosts? Are ghosts even bound by the laws of spacetime?
 * Only one way to find out.
 * After examining the maps a bit longer, your attention is drawn to a curious fact: the number of nodes with names
 * ending in A is equal to the number ending in Z! If you were a ghost, you'd probably just start at every node that
 * ends with A and follow all of the paths at the same time until they all simultaneously end up at nodes that end with
 * Z.
 * For example:
 * LR
 * 11A = (11B, XXX)
 * 11B = (XXX, 11Z)
 * 11Z = (11B, XXX)
 * 22A = (22B, XXX)
 * 22B = (22C, 22C)
 * 22C = (22Z, 22Z)
 * 22Z = (22B, 22B)
 * XXX = (XXX, XXX)
 * Here, there are two starting nodes, 11A and 22A (because they both end with A). As you follow each left/right
 * instruction, use that instruction to simultaneously navigate away from both nodes you're currently on. Repeat this
 * process until all of the nodes you're currently on end with Z. (If only some of the nodes you're on end with Z, they
 * act like any other node and you continue as normal.) In this example, you would proceed as follows:
 * Step 0: You are at 11A and 22A.
 * Step 1: You choose all of the left paths, leading you to 11B and 22B.
 * Step 2: You choose all of the right paths, leading you to 11Z and 22C.
 * Step 3: You choose all of the left paths, leading you to 11B and 22Z.
 * Step 4: You choose all of the right paths, leading you to 11Z and 22B.
 * Step 5: You choose all of the left paths, leading you to 11B and 22C.
 * Step 6: You choose all of the right paths, leading you to 11Z and 22Z.
 * So, in this example, you end up entirely on nodes that end in Z after 6 steps.
 * <p>
 * Simultaneously start on every node that ends with A. How many steps does it take before you're only on nodes that
 * end with Z?
 */
public class Main {

    public static void main(String[] args) {

        File input = new File("src/main/java/ru/selestrel/challenge/aoc2023/day8/input.txt");

        Map<String, List<String>> nodes = new HashMap<>();
        String instructions;

        try {
            Scanner scanner = new Scanner(input);
            instructions = scanner.nextLine();
            scanner.nextLine();
            while (scanner.hasNext()) {
                String nextLine = scanner.nextLine();
                LinkedList<String> lineNode = new LinkedList<>(getNodesFromString(nextLine));

                nodes.put(lineNode.removeFirst(), lineNode);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        //Puzzle 1
        System.out.println("First puzzle result: " + getStepCount(nodes, "AAA", instructions, "ZZZ"));

        //Puzzle 2
        //Each node has the same steps count to come to the next node that ends with "Z", e.g. 11A need 5000 steps to
        // come to node 22Z then 5000 steps more to come to node 33Z then 5000 steps more to come to node 44Z and so on
        // So solution is find the count of steps for each start node and then find a least common multiple for all
        // found numbers
        List<String> startNodes = new ArrayList<>(nodes.keySet().stream().filter(node -> node.endsWith("A")).toList());
        List<Integer> loopSize = new ArrayList<>();

        startNodes.forEach(node -> loopSize.add(getStepCount(nodes, node, instructions, "..Z")));

        long result = 1L;
        for (int number : loopSize) {
            result = lcm(result, number);
        }

        System.out.println("Second puzzle result: " + result);
    }

    public static long lcm(long number1, long number2) {
        if (number1 == 0 || number2 == 0)
            return 0;
        else {
            long gcd = gcd(number1, number2);
            return Math.abs(number1 * number2) / gcd;
        }
    }

    public static long gcd(long number1, long number2) {
        if (number1 == 0 || number2 == 0) {
            return number1 + number2;
        } else {
            long absNumber1 = Math.abs(number1);
            long absNumber2 = Math.abs(number2);
            long biggerValue = Math.max(absNumber1, absNumber2);
            long smallerValue = Math.min(absNumber1, absNumber2);
            return gcd(biggerValue % smallerValue, smallerValue);
        }
    }

    private static int getStepCount(Map<String, List<String>> nodes, String currentNode, String instructions, String matcher) {
        int stepCount = 0;
        do {
            currentNode = getNextNode(nodes, currentNode, instructions.charAt(stepCount % instructions.length()));

            stepCount++;
        } while (!currentNode.matches(matcher));

        return stepCount;
    }

    private static String getNextNode(Map<String, List<String>> nodes, String currentNode, char direction) {
        List<String> nodeDirections = nodes.get(currentNode);
        return nodeDirections.get(direction == 'L' ? 0 : 1);
    }

    private static List<String> getNodesFromString(String line) {
        Pattern p = Pattern.compile("[A-Z]{3}");
        Matcher m = p.matcher(line);
        List<String> nodes = new ArrayList<>();

        while (m.find()) {
            nodes.add(m.group());
        }

        return nodes;
    }
}
