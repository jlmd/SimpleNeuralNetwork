package com.jlmd.simpleneuralnetwork.neuralnetwork.transfer;

import java.io.Serializable;

/**
 * Sigmoid implementation of transfer function that limit the value between 0 and 1
 * @author jlmd
 */
public class SigmoidFunction implements ITransferFunction, Serializable {
    @Override
    public float transfer(float value) {
        return (float)(1/(1+Math.exp(-value)));
    }
}
