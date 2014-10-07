Simple neural network
=========

Simple neural network is a Java project that allow users to easily create a **asynchronous simple neural network**.

This project can be used to **predict** a output based on a initial learning.

Features
----
  - Callback with a result entity
  - Number of neurons customizable
  - Read data from external files
  - Simple predictor usage

Usage
----
This is a simple usage with default configuration:

1. First of all, load input and output data. You can read it from external text file:

    ``` java
float[][] x = DataUtils.readInputsFromFile("data/x.txt");
int[] t = DataUtils.readOutputsFromFile("data/t.txt");
    ```
2. Instantiate new NeuralNetwork and create a new callback to receive response:
    ``` java
NeuralNetwork neuralNetwork = new NeuralNetwork(x, t, new INeuralNetworkCallback() {
            @Override
            public void success(Result result) {
            }

            @Override
            public void failure(Error error) {
            }
        });
    ```

3. Predict a value using Result entity from success response:

    ``` java
    @Override
    public void success(Result result) {
        float[] valueToPredict = new float[] {-1.2f, 0.796f};
        System.out.println("Predicted result: " + result.predictValue(valueToPredict));
    }

    ```
4. Finally, run learning of neural network:
    ``` java
neuralNetwork.startLearning();
    ```

Full example:
``` java
        float[][] x = DataUtils.readInputsFromFile("data/x.txt");
        int[] t = DataUtils.readOutputsFromFile("data/t.txt");

        NeuralNetwork neuralNetwork = new NeuralNetwork(x, t, new INeuralNetworkCallback() {
            @Override
            public void success(Result result) {
                float[] valueToPredict = new float[] {-0.205f, 0.780f};
                System.out.println("Success percentage: " + result.getSuccessPercentage());
                System.out.println("Predicted result: " + result.predictValue(valueToPredict));
            }

            @Override
            public void failure(Error error) {
                System.out.println("Error: " + error.getDescription());
            }
        });

        neuralNetwork.startLearning();
```
Output:
```
Success percentage: 88.4
Predicted result: 1
```
  
You can customize some values as the number of neurons, bucle iterations limit, transfer function and result parser.


Example
----

This project has a example with real data that contains a list of 250 patients with two results of a analysis as inputs and 0 or 1 (depending if the has a disease or not) as output:
````
         Inputs                         Output
------------------------        --------------------
 Result 1       Result 2                Disease
 -----------------------        --------------------
-0.5982         0.9870                     1     
-0.2019         0.6210                     1
 0.1797         0.4518                     0
-0.0982         0.5876                     1
  ...             ...                     ...
````

Using this data in neural network, this project is able to predict the output (disease result) of a patient that isn't in the data list with a minimum **percentage of success of 88%**.

License
----
```
Copyright 2014 José Luis Martín

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

