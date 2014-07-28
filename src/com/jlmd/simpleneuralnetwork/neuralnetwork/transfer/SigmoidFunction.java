package com.jlmd.simpleneuralnetwork.neuralnetwork.transfer;

/**
 * Sigmoid implementation of transfer function that limit the value between 0 and 1
 * @author jlmd
 */
public class SigmoidFunction implements ITransferFunction {
    @Override
    public float transfer(float value) {
        return (float)(1/(1+Math.exp(-value)));
    }
}
