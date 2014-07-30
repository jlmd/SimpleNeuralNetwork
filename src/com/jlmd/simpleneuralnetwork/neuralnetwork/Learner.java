package com.jlmd.simpleneuralnetwork.neuralnetwork;

/**
 * Modify weights adjusting it to expected result, based on result of analyzer
 * @author jlmd
 */
public class Learner {
    private float[] bias;
    private float[] vWeights;
    private float[][] wWeights;
    private float bOut;

    public Learner(float t, float fOut, float[] f, float[] vWeights, float[][] wWeights, float[] bias, float bOut, int neurons, float[] x, int dimension){
        this.bias = new float[neurons];
        this.vWeights = new float[neurons];
        this.wWeights = new float[dimension][neurons];

        initLearn(t, fOut, f, vWeights, wWeights, bias, bOut, neurons, x, dimension);
    }

    /**
     * Initialize the learn
     * @param t Output result
     * @param fOut Out function
     * @param f Functions
     * @param vWeights
     * @param wWeights
     * @param bias
     * @param bOut
     * @param neurons Number of neurons
     * @param x Inputs
     * @param dimension Dimension of inputs
     */
    private void initLearn(float t, float fOut, float[] f, float[] vWeights, float[][] wWeights, float[] bias, float bOut, int neurons, float[] x, int dimension) {
        float error = t - fOut;
        float n = 0.05f;
        float dv;
        float[] dwi = new float[neurons];
        float[][] dw = new float[dimension][neurons];
        float[] dbi = new float[neurons];
        float[] db = new float[neurons];


        // Modify v weights
        dv = fOut * (1-fOut) * error;
        for (int i = 0;i<neurons;i++){
            this.vWeights[i] = vWeights[i] + n * dv * f[i];
        }

        // Modify bias out
        float dbOut = n * dv * 1;
        this.bOut = (bOut + dbOut);

        // Modify w weights
        for (int i = 0;i<neurons;i++){
            dwi[i] = f[i] * (1 - f[i]) * vWeights[i] * dv;
            for (int j = 0;j<dimension;j++){
                dw[j][i] = n * dwi[i] * x[j];
                this.wWeights[j][i] = wWeights[j][i] + dw[j][i];
            }
        }

        // Modify bias
        for (int i = 0;i<neurons;i++){
            dbi[i] = f[i] * (1 - f[i]) * vWeights[i] * dv;
            db[i] = n * dbi[i] * 1;
            this.bias[i] = bias[i] + db[i];
        }
    }

    public float[] getBias() {
        return bias;
    }

    public float[] getVWeights() {
        return vWeights;
    }

    public float[][] getWWeights() {
        return wWeights;
    }

    public float getBOut() {
        return bOut;
    }

}
