package com.jlmd.simpleneuralnetwork.app;

import java.util.ArrayList;

/**
 * Simple class to evaluate tests performed on a binary classifier
 * TODO: implement real tools such as precision, recall, false positives, etc.
 */
public class TestResult {

    private int testedInputsForClass0 = 0;
    private int testedInputsForClass1 = 0;
    private int correctPredictionsForClass0 = 0;
    private int correctPredictionsForClass1 = 0;

    public double getPrecisionForClass0() {
        return ((double) this.correctPredictionsForClass0) / this.testedInputsForClass0;
    }

    public double getPrecisionForClass1() {
        return ((double) this.correctPredictionsForClass1) / this.testedInputsForClass1;
    }

    public double getOverallPrecision() {
        return ((double)(this.correctPredictionsForClass0 + this.correctPredictionsForClass1)) / (this.testedInputsForClass0 + this.testedInputsForClass1);
    }

    public double getRatioClass0OverClass1() {
        return ((double) this.testedInputsForClass0) / this.testedInputsForClass1;
    }

    public int getTotalNumberOfTestsPerformed() {
        return this.testedInputsForClass0 + this.testedInputsForClass1;
    }

    public void addPrediction(int predictedClass, int correctClass) {
        if (correctClass == 0) {
            this.testedInputsForClass0++;
            if (predictedClass == 0) {
                this.correctPredictionsForClass0++;
            }
        } else {
            this.testedInputsForClass1++;
            if (predictedClass == 1) {
                this.correctPredictionsForClass1++;
            }
        }
    }

    public ArrayList<Object> exportMeasurements() {
        ArrayList<Object> measurements = new ArrayList<>();
        measurements.add("NbTests");
        measurements.add(this.getTotalNumberOfTestsPerformed());
        measurements.add("RatioClass0OverClass1");
        measurements.add(this.getRatioClass0OverClass1());
        measurements.add("OverallPrecision");
        measurements.add(this.getOverallPrecision());
        measurements.add("PrecisionForClass0");
        measurements.add(this.getPrecisionForClass0());
        measurements.add("PrecisionForClass1");
        measurements.add(this.getPrecisionForClass1());

        return measurements;
    }
}