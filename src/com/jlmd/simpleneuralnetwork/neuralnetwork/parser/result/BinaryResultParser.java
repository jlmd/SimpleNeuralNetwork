package com.jlmd.simpleneuralnetwork.neuralnetwork.parser.result;

/**
 * Binary implementation of result parser
 * @author jlmd
 */
public class BinaryResultParser implements IResultParser<Integer> {
    @Override
    public int countSuccesses(int success, float fOut, float t){
        if ((fOut < 0.5 && t == 0) || (fOut >= 0.5 && t == 1))
            success += 1;

        return success;
    }

    @Override
    public Integer parseResult(float result) {
        return (result < 0.5) ? 0 : 1;
    }
}
