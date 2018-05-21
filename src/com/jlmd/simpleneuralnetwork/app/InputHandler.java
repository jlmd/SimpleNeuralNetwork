package com.jlmd.simpleneuralnetwork.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class InputHandler {

    private Scanner sc;
    private static Logger LOGGER = Main.LOGGER;

    InputHandler(Scanner sc) {
        this.sc = sc;
    }

    public boolean chooseProceedWithTrainingFromUserInput() {
        return this.getBooleanAnswerFromUserInput("Proceed with training?");
    }

    public String chooseModelNameFromUserInput() {
        LOGGER.info("Please enter a name to save the model under: ");
        return sc.next();
    }

    public boolean chooseSaveModelFromUserInput() {
        return this.getBooleanAnswerFromUserInput("Should the model be saved?");
    }

    public int chooseNbNeuronsFromUserInput() {
        int userChoice;
        do {
            LOGGER.info("How many neurons in hidden layer? ");
            userChoice = sc.nextInt();
        } while (userChoice < 1);

        return userChoice;
    }

    public int chooseMaxNbIterationsFromUserInput() {
        int userChoice;
        do {
            LOGGER.info("How many iterations max? ");
            userChoice = sc.nextInt();
        } while (userChoice < 1);

        return userChoice;
    }

    public Mode chooseModeFromUserInput() {
        String userChoice;
        do {
            LOGGER.info("Which mode? Please enter 'train', 'test', or 'batch': ");
            userChoice = sc.next();
        } while (!Arrays.stream(Mode.values()).map(Mode::getValue).collect(Collectors.toList()).contains(userChoice));

        return Mode.getModeFromString(userChoice);
    }

    public double chooseTrainingPartitionFromUserInput() {
        double userChoice;
        do {
            LOGGER.info("Which percentage of the file should be used for training? (10 to 90): ");
            userChoice = sc.nextDouble();
        } while (userChoice < 10 || userChoice > 90);

        return userChoice / 100; // directly convert into a [0..1] percentage
    }

    public boolean chooseRandomizationFromUserInput() {
        return this.getBooleanAnswerFromUserInput("Should the order of the input be randomized?");
    }

    public String chooseFileNameFromUserInput(String dir) {
        int userChoice;
        File[] filesList;
        do {
            LOGGER.info("Please choose input file in the list");
            File dataDir = new File(dir);
            filesList = dataDir.listFiles();
            int idx = 1;
            for (File file : Objects.requireNonNull(filesList)) {
                LOGGER.info((idx++) + ") " + file.getName());
            }
            LOGGER.info("User choice: ");
            userChoice = sc.nextInt();
        } while (userChoice < 1 || userChoice > filesList.length);

        String chosenFile = filesList[userChoice - 1].getName();
        LOGGER.info("Chosen: " + chosenFile);
        return chosenFile;
    }

    private boolean getBooleanAnswerFromUserInput(String question) {
        char userChoice;
        do {
            LOGGER.info(question + " (y/n): ");
            userChoice = sc.next().charAt(0);
        } while (userChoice != 'y' && userChoice != 'n');

        return (userChoice == 'y');
    }

    public int[] chooseNbNeuronsArrayFromUserInput() {
        return this.getIntArrayAnswerFromUserInput("How many neurons in hidden layer ?", new int[] {2, 3, 5, 10});
    }

    public int[] chooseMaxNbIterationsArrayFromUserInput() {
        return this.getIntArrayAnswerFromUserInput("How many maximum iterations allowed ?", new int[] {10, 100, 1000});
    }

    public String chooseCsvNameFromUserInput() {
        LOGGER.info("Please enter a name to save the results under: ");
        return sc.next();
    }

    private int[] getIntArrayAnswerFromUserInput(String question, int[] example) {
        String[] exampleAsStringArray = Arrays.toString(example).split("[\\[\\]]")[1].split(", ");
        String userChoice;
        ArrayList<Integer> userChoiceAsIntegerArrayList = new ArrayList<>();
        int[] userChoiceAsIntArray;
        boolean validInput;
        do {
            LOGGER.info(question+" Enter integers separated by coma without space (ex. '"+String.join(",", exampleAsStringArray)+"') ");
            userChoice = sc.next();
            try {
                for (String e : userChoice.split(",")) {
                    userChoiceAsIntegerArrayList.add(Integer.parseInt(e));
                }
                validInput = true;
            } catch (NumberFormatException e) {
                userChoiceAsIntegerArrayList.clear();
                validInput = false;
            }
        } while (!validInput);

        userChoiceAsIntArray = new int[userChoiceAsIntegerArrayList.size()];
        for (int i = 0; i < userChoiceAsIntegerArrayList.size(); i++) {
            userChoiceAsIntArray[i] = userChoiceAsIntegerArrayList.get(i);
        }

        return userChoiceAsIntArray;
    }
}
