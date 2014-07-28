package com.jlmd.simpleneuralnetwork.neuralnetwork;

import com.jlmd.simpleneuralnetwork.neuralnetwork.transfer.ITransferFunction;

/**
 * Generate out function using weights and input values
 * @author jlmd
 */
public class Analyzer {
    private float[] fOutArray;
    private float[][] wWeights;
    private float[] vWeights;
    private float[] bias;
    private float fOut;
    private float bOut;
    private int dimension;
    private int neurons;
    private ITransferFunction transferFunction;

    public Analyzer(float[] x, float[][] wWeights, float[] bias, float[] vWeights, float bOut, int neurons, ITransferFunction transferFunction, int dimension){
        this.fOutArray = new float[neurons];
        this.wWeights = wWeights;
        this.bias = bias;
        this.vWeights = vWeights;
        this.bOut = bOut;
        this.neurons = neurons;
        this.transferFunction = transferFunction;
        this.dimension = dimension;
        this.fOut = calculateFOut(x);
    }

    /**
     * Calculate out function
     * @param x Elements array
     * @return float out function
     */
    private float calculateFOut(float[] x){
        for (int i = 0;i<neurons;i++){
            float sum = 0;
            for (int j=0; j<dimension; j++){
                sum = sum + (x[j] * wWeights[j][i]);
            }

            this.fOutArray[i] = transferFunction.transfer(sum + bias[i]);
        }

        this.fOut = 0;
        for (int i = 0;i<neurons;i++){
            this.fOut += fOutArray[i] * vWeights[i];
        }

        return transferFunction.transfer(fOut + bOut);
    }

    public float[] getFOutArray() {
        return fOutArray;
    }

    public float getFOut() {
        return fOut;
    }

    public float getFOut(float[] x) {
        return calculateFOut(x);
    }
}
