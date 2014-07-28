package com.jlmd.simpleneuralnetwork.neuralnetwork.transfer;

/**
 * Interface for transfer function. The function of this is limit the values of generated output
 * @author jlmd
 */
public interface ITransferFunction {
    /**
     * Calculate the transfer value limited by a function
     * @param value Transfer value
     * @return obtained value
     */
    float transfer(float value);
}
