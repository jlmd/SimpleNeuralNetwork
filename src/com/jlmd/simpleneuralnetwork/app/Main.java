package com.jlmd.simpleneuralnetwork.app;

import com.jlmd.simpleneuralnetwork.neuralnetwork.NeuralNetwork;
import com.jlmd.simpleneuralnetwork.neuralnetwork.callback.INeuralNetworkCallback;
import com.jlmd.simpleneuralnetwork.neuralnetwork.entity.Error;
import com.jlmd.simpleneuralnetwork.neuralnetwork.entity.Result;
import com.jlmd.simpleneuralnetwork.neuralnetwork.utils.DataUtils;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author jlmd
 */
public class Main {

    public static final Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final static String DATA_DIR_NAME = "data";
    private final static String PREPARED_DATA_DIR_NAME = "prepared_data";
    private final static String MODELS_DIR_NAME = "models";
    public final static String RESULTS_DIR_NAME = "results";

    private final static String LEARNING_INPUT_FILE_NAME = "learning_input.txt";
    private final static String LEARNING_OUTPUT_FILE_NAME = "learning_output.txt";
    private final static String TESTING_FILE_NAME = "testing_data_set.txt";

    // Constants for default behaviour (see batch mode)
    public final static boolean DEFAULT_RANDOMIZE_DATA_ORDER = false;
    public final static double DEFAULT_TRAINING_PERCENTAGE = 0.67;

    // Constants for autoFill mode
    private final static Mode AUTO_MODE = Mode.BATCH;
    private final static String AUTO_FILE_NAME = "004MaxEntropy.txt";
    private final static boolean AUTO_RANDOMIZE_DATA_ORDER = true;
    private final static double AUTO_TRAINING_PERCENTAGE = 0.8;
    private final static boolean AUTO_PROCEED_WITH_TRAINING = true;
    private final static int AUTO_NB_NEURONS = 2;
    private final static int AUTO_MAX_NB_ITERATIONS = 1000;
    private final static boolean AUTO_SAVE_MODEL = true;
    private final static String AUTO_MODEL_NAME = "my_model";
    private final static int[] AUTO_NB_NEURONS_ARRAY = new int[]{1, 2, 3, 5, 8};
    private final static int[] AUTO_MAX_NB_ITERATIONS_ARRAY = new int[]{5, 10, 200, 1000};
    private final static String AUTO_CSV_FILE_NAME = "batch_results";

    private static Result trainedNeuralNet;

    public static void main(String[] args) {
        config();
        InputHandler ih = new InputHandler(new Scanner(System.in));

        Mode mode;  // train or test
        String fileName;  // to be found in directory "data"
        String modelName;  // to be found in directory "models"
        boolean randomizeDataOrder;
        double trainingPercentage;
        boolean proceedWithTraining;
        int nbNeurons;  // in the hidden layer of the neural network
        int maxNbIterations;
        boolean saveModel;

        boolean autoFill = (args.length > 0 && args[0].equals("auto"));

        mode = autoFill ? AUTO_MODE : ih.chooseModeFromUserInput();
        switch (mode) {
            case TRAIN:
                fileName = autoFill ? AUTO_FILE_NAME : ih.chooseFileNameFromUserInput(DATA_DIR_NAME);
                randomizeDataOrder = autoFill ? AUTO_RANDOMIZE_DATA_ORDER : ih.chooseRandomizationFromUserInput();
                trainingPercentage = autoFill ? AUTO_TRAINING_PERCENTAGE : ih.chooseTrainingPartitionFromUserInput();
                // prepare data in two files for learning: learning_input.txt, learning_output.txt
                try {
                    prepareDataForLearning(fileName, randomizeDataOrder, trainingPercentage);
                    LOGGER.info("Successfully prepared data for learning");
                } catch (IOException e) {
                    LOGGER.severe("IO Exception while trying to access " + fileName);
                    System.exit(1);
                }
                proceedWithTraining = autoFill ? AUTO_PROCEED_WITH_TRAINING : ih.chooseProceedWithTrainingFromUserInput();
                if (proceedWithTraining) {
                    nbNeurons = autoFill ? AUTO_NB_NEURONS : ih.chooseNbNeuronsFromUserInput();
                    maxNbIterations = autoFill ? AUTO_MAX_NB_ITERATIONS : ih.chooseMaxNbIterationsFromUserInput();
                    NeuralNetwork neuralNetwork = createNeuralNet(nbNeurons, maxNbIterations);
                    neuralNetwork.startLearning();  // this will fill static variable trainedNeuralNet
                    LOGGER.info("Neural network successfully trained");
                    saveModel = autoFill ? AUTO_SAVE_MODEL : ih.chooseSaveModelFromUserInput();
                    if (saveModel) {
                        modelName = autoFill ? AUTO_MODEL_NAME : ih.chooseModelNameFromUserInput();
                        try {
                            serializeTrainedNetwork(modelName);
                            LOGGER.info("Successfully saved model");
                        } catch (IOException e) {
                            LOGGER.severe("IO Exception while trying to serialize model");
                            System.exit(1);
                        }
                    }
                }
                break;

            case TEST:
                modelName = autoFill ? AUTO_MODEL_NAME + ".ser" : ih.chooseFileNameFromUserInput(MODELS_DIR_NAME);
                try {
                    trainedNeuralNet = deserializeTrainedNetwork(modelName);
                    LOGGER.info("Successfully loaded model");
                } catch (IOException | ClassNotFoundException e) {
                    LOGGER.severe("Exception while trying to deserialize model");
                    e.printStackTrace();
                    System.exit(1);
                }
                TestResult tr = testModel();  // model to test is in static variable trainedNeuralNet
                LOGGER.info("Performed " + tr.getTotalNumberOfTestsPerformed() +
                        " tests. Ratio 'Class 0 / Class 1' is " + tr.getRatioClass0OverClass1());
                LOGGER.info("Precision for Class 0: " + tr.getPrecisionForClass0());
                LOGGER.info("Precision for Class 1: " + tr.getPrecisionForClass1());
                LOGGER.info("Overall precision: " + tr.getOverallPrecision());
                break;

            case BATCH:
                int[] nbNeuronsArray = autoFill ? AUTO_NB_NEURONS_ARRAY : ih.chooseNbNeuronsArrayFromUserInput();
                int[] maxNbIterationsArray = autoFill ? AUTO_MAX_NB_ITERATIONS_ARRAY : ih.chooseMaxNbIterationsArrayFromUserInput();
                LOGGER.info("Training on all data sets from directory " + DATA_DIR_NAME);
                BatchResult br = trainNeuralNetworkWithParameters(nbNeuronsArray, maxNbIterationsArray);
                String csvFileName = autoFill ? AUTO_CSV_FILE_NAME : ih.chooseCsvNameFromUserInput();
                try {
                    br.exportResultsInCsvFile(csvFileName);
                    LOGGER.info("Successfully exported results to CSV");
                } catch (IOException e) {
                    LOGGER.severe("IO Exception while saving results to CSV");
                    System.exit(1);
                }
                break;
        }
    }

    private static BatchResult trainNeuralNetworkWithParameters(int[] nbNeuronsArray, int[] maxNbIterationsArray) {
        BatchResult br = new BatchResult();

        List<String> dataFileNames = Arrays.stream(Objects.requireNonNull((new File(DATA_DIR_NAME)).listFiles())).map(File::getName).collect(Collectors.toList());
        for (String dataFileName : dataFileNames) {
            try {
                prepareDataForLearning(dataFileName, DEFAULT_RANDOMIZE_DATA_ORDER, DEFAULT_TRAINING_PERCENTAGE);
                LOGGER.info("Successfully prepared data from file " + dataFileName + " for learning");
            } catch (IOException e) {
                LOGGER.severe("IO Exception while trying to access " + dataFileName + ". Trying other files");
                continue;
            }
            for (int nbNeurons : nbNeuronsArray) {
                for (int maxNbIterations : maxNbIterationsArray) {
                    LOGGER.info("Training with parameters: " + nbNeurons + " neurons in hidden layer, " + maxNbIterations + " maximum");
                    NeuralNetwork neuralNetwork = createNeuralNet(nbNeurons, maxNbIterations);
                    neuralNetwork.startLearning();  // this will fill static variable trainedNeuralNet
                    ArrayList<Object> testMeasurements = testModel().exportMeasurements();
                    br.addTestCase(dataFileName, nbNeurons, maxNbIterations, testMeasurements);
                }
            }
        }
        return br;
    }

    private static TestResult testModel() {
        float[][] testDataSet = DataUtils.readInputsFromFile(PREPARED_DATA_DIR_NAME + "/" + TESTING_FILE_NAME);
        TestResult tr = new TestResult();
        for (float[] line : testDataSet) {
            float[] input = Arrays.copyOfRange(line, 0, line.length-1);
            int predictedValue = trainedNeuralNet.predictValue(input);
            int correctValue = (int) line[line.length - 1];
            tr.addPrediction(predictedValue, correctValue);
        }
        return tr;
    }

    private static void serializeTrainedNetwork(String modelName) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(MODELS_DIR_NAME + "/" + modelName + ".ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(trainedNeuralNet);
        out.close();
        fileOut.close();
    }

    public static Result deserializeTrainedNetwork(String modelName) throws IOException, ClassNotFoundException {
        Result r;
        FileInputStream fileIn = new FileInputStream(MODELS_DIR_NAME + "/" + modelName);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        r = (Result) in.readObject();
        in.close();
        fileIn.close();
        return r;
    }

    private static NeuralNetwork createNeuralNet(int nbNeurons, int maxNbIterations) {
        float[][] x = DataUtils.readInputsFromFile(PREPARED_DATA_DIR_NAME + "/" + LEARNING_INPUT_FILE_NAME);
        int[] t = DataUtils.readOutputsFromFile(PREPARED_DATA_DIR_NAME + "/" + LEARNING_OUTPUT_FILE_NAME);

        NeuralNetwork neuralNetwork = new NeuralNetwork(x, t, new INeuralNetworkCallback() {
            @Override
            public void success(Result result) {
                LOGGER.info("Training precision: " + result.getSuccessPercentage());
                trainedNeuralNet = result;
            }

            @Override
            public void failure(Error error) {
                LOGGER.severe("Error: " + error.getDescription());
            }
        });

        neuralNetwork.setNeurons(nbNeurons);
        neuralNetwork.setIterationsLimit(maxNbIterations);
        return neuralNetwork;
    }


    /**
     * @param fileName           Name of the data set file to be found in directory "data"
     * @param randomizeDataOrder if true, lines order will be randomized
     * @param trainingPercentage portion of the data set to be used for training vs test
     * @throws IOException In case of problems while reading data set
     *                     <p>
     *                     TODO: normalize input?
     */
    private static void prepareDataForLearning(String fileName, boolean randomizeDataOrder, double trainingPercentage) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(DATA_DIR_NAME + "/" + fileName));
        String currentLine = br.readLine();  // skip header
        int expectedLineLength = currentLine.split(",").length;
        BufferedWriter bwi = new BufferedWriter(new FileWriter(PREPARED_DATA_DIR_NAME + "/" + LEARNING_INPUT_FILE_NAME));
        BufferedWriter bwo = new BufferedWriter(new FileWriter(PREPARED_DATA_DIR_NAME + "/" + LEARNING_OUTPUT_FILE_NAME));
        BufferedWriter bwt = new BufferedWriter(new FileWriter(PREPARED_DATA_DIR_NAME + "/" + TESTING_FILE_NAME));
        ArrayList<String[]> allLinesAsArrays = new ArrayList<>();
        String[] currentLineAsArray;
        int currentLineLength;

        while ((currentLine = br.readLine()) != null) {
            if (currentLine.contains("NA")) {
                // we cannot use this for training
                LOGGER.fine("Invalid line (contains NA) will be ignored: " + currentLine);
                continue;
            }
            currentLineAsArray = currentLine.split(",");
            currentLineLength = currentLineAsArray.length;
            if (currentLineLength != expectedLineLength) {
                // invalid formatting
                LOGGER.fine("Invalid line (invalid format) will be ignored: " + currentLine);
                continue;
            }

            allLinesAsArrays.add(currentLineAsArray);
        }

        if (randomizeDataOrder) {
            Collections.shuffle(allLinesAsArrays);
        }

        int dataSetLength = allLinesAsArrays.size();
        int maxIndexForTraining = (int) (dataSetLength * trainingPercentage);
        for (String[] lineAsArray : allLinesAsArrays.subList(0, maxIndexForTraining)) {
            bwi.write(String.join(",", Arrays.copyOfRange(lineAsArray, 1, expectedLineLength - 1)) + "\n");  // ignore field "id"
            bwo.write(lineAsArray[expectedLineLength - 1] + "\n");  // last field has output
        }
        for (String[] lineAsArray : allLinesAsArrays.subList(maxIndexForTraining, dataSetLength - 1)) {
            bwt.write(String.join(",", Arrays.copyOfRange(lineAsArray, 1, lineAsArray.length)) + "\n");
        }

        bwt.close();
        bwi.close();
        bwo.close();
        br.close();
    }

    private static void config() {
        Locale.setDefault(Locale.ENGLISH);
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s\t%5$s%6$s%n");
    }
}