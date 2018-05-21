package com.jlmd.simpleneuralnetwork.app;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

class BatchResult {

    private ArrayList<String[]> testCases;
    private String[] measurementTitles = null;

    BatchResult() {
        this.testCases = new ArrayList<>();
    }

    public void exportResultsInCsvFile(String fileName) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(Main.RESULTS_DIR_NAME + "/" + fileName + ".csv"));
        if (measurementTitles != null) {
            bw.write("dataFileName,nbNeurons,maxNbIterations," + String.join(",", measurementTitles) + "\n");
        }
        for (String[] testCase : testCases) {
            bw.write(String.join(",", testCase) + "\n");
        }
        bw.close();
    }

    public void addTestCase(String dataFileName, int nbNeurons, int maxNbIterations, ArrayList<Object> testMeasurements) {
        int nbTestMeasurements = testMeasurements.size() / 2;  // we expect [name, value, name, value, ...]
        if (measurementTitles == null) {
            measurementTitles = new String[nbTestMeasurements];
            for (int i = 0; i < nbTestMeasurements; i++) {
                measurementTitles[i] = (String) testMeasurements.get(2 * i);  // only titles
            }
        }
        String[] testCase = new String[3 + measurementTitles.length];
        testCase[0] = dataFileName;
        testCase[1] = "" + nbNeurons;
        testCase[2] = "" + maxNbIterations;
        for (int i = 0; i < nbTestMeasurements; i++) {
            testCase[3 + i] = "" + testMeasurements.get(1 + 2 * i);  // only values
        }

        this.testCases.add(testCase);
    }
}
