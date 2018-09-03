// OJALL MICHAEL OMONDI P15/31821/2015
//Back Propagation_Neural Networks Program.

package backpropagation;

/**
 *
 * @author Ojall Michael
 */

import java.text.*;
import java.util.*;
 
public final class NeuralNetwork {
 
    final boolean isTrained = false;
    final Neuron bias = new Neuron();
    final Random rand = new Random();
    final ArrayList<Neuron> inputLayer = new ArrayList<>();
    final ArrayList<Neuron> hiddenLayer = new ArrayList<>();
    final ArrayList<Neuron> outputLayer = new ArrayList<>();
    final DecimalFormat format;
    final int[] layers;
    final int randomWeightMultiplier = 1;
    final double epsilon = 0.00000000001;
    final double learningRate = 0.5f;
    final double momentum = 0.6f;
 
    // Inputs for xor problem
    final double inputs[ ][ ] = { { 1, 1 },  { 1, 0 },  { 0, 1 },  { 0, 0 } };
                               
    // Corresponding outputs, xor training data
    final double expectedOutputs[ ][ ] = { { 0 },  { 1 }, { 1 },  { 0 } };
                        
    double resultOutputs[ ][ ] = { { -1 },  { -1 },   { -1 },   { -1 } }; 
                          
    double output[ ];
 
    // for weight update all
    final HashMap<String, Double> weightUpdate = new HashMap<>();
 
    public static void main(String[] args) {
        NeuralNetwork nn = new NeuralNetwork(2, 4, 1);
        int maxRuns = 50000;
        double minErrorCondition = 0.001;
        nn.run(maxRuns, minErrorCondition);
    }
 
    public NeuralNetwork(int input, int hidden, int output) {
        this.layers = new int[] { input, hidden, output };
        format = new DecimalFormat("#.0#");
 
        /**
         * Create all neurons and connections Connections are created in the
         * neuron class
         * FORWARD PASS 
         */
        for (int i = 0; i < layers.length; i++) {
            switch (i) {
                case 0:
                    // input layer
                    for (int j = 0; j < layers[i]; j++) {
                        Neuron neuron = new Neuron();
                        inputLayer.add(neuron);
                    }   break;
                case 1:
                    // hidden layer
                    for (int j = 0; j < layers[i]; j++) {
                        Neuron neuron = new Neuron();
                        neuron.addInConnectionsS(inputLayer);
                        neuron.addBiasConnection(bias);
                        hiddenLayer.add(neuron);
                    }   break;
                case 2:
                    // output layer
                    for (int j = 0; j < layers[i]; j++) {
                        Neuron neuron = new Neuron();
                        neuron.addInConnectionsS(hiddenLayer);
                        neuron.addBiasConnection(bias);
                        outputLayer.add(neuron);
                    }   break;
                default:
                    System.out.println("!Error NeuralNetwork init");
                    break;
            }
        }
 
        // initialize random weights for the first round
        hiddenLayer.stream().map((neuron) -> neuron.getAllInConnections()).forEachOrdered((ArrayList<Connection> connections) -> {
            connections.forEach((conn) -> {
                double newWeight = getRandom();
                conn.setWeight(newWeight);
            });
        });
        outputLayer.stream().map((neuron) -> neuron.getAllInConnections()).forEachOrdered((ArrayList<Connection> connections) -> {
            connections.forEach((conn) -> {
                double newWeight = getRandom();
                conn.setWeight(newWeight);
            });
        });
 
        // reset id counters
        Neuron.counter = 0;
        Connection.counter = 0;
 
        if (isTrained) {
            trainedWeights();
            updateAllWeights();
        }
    }
    // random generator
    double getRandom() {
        return randomWeightMultiplier * (rand.nextDouble() * 2 - 1); // [-1;1[
    }
    // Setting input and output layer
    public void setInput(double inputs[]) {
        for (int i = 0; i < inputLayer.size(); i++) {
            inputLayer.get(i).setOutput(inputs[i]);
        }
    }
    public double[] getOutput() {
        double[] outputs = new double[outputLayer.size()];
        for (int i = 0; i < outputLayer.size(); i++)
            outputs[i] = outputLayer.get(i).getOutput();
        return outputs;
    }
    /**
     * Calculate the output of the neural network based on the input 
     * The forward operation
     */
    public void activate() {
        hiddenLayer.forEach((n) -> {
            n.calculateOutput();
        });
        outputLayer.forEach((n) -> {
            n.calculateOutput();
        });
    }
    /**
     * all output propagate back
     * @param expectedOutput
     *            first calculate the partial derivative of the error with
     *            respect to each of the weight leading into the output neurons
     *            bias is also updated here
     */
    public void applyBackpropagation(double expectedOutput[]) {
 
        // error check, normalize value ]0;1[
        for (int i = 0; i < expectedOutput.length; i++) {
            double d = expectedOutput[i];
            if (d < 0 || d > 1) {
                if (d < 0)
                    expectedOutput[i] = 0 + epsilon;
                else
                    expectedOutput[i] = 1 - epsilon;
            }
        }
 
        int i = 0;
        for (Neuron n : outputLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                double ak = n.getOutput();
                double ai = con.leftNeuron.getOutput();
                double desiredOutput = expectedOutput[i];
 
                double partialDerivative = -ak * (1 - ak) * ai
                        * (desiredOutput - ak);
                double deltaWeight = -learningRate * partialDerivative;
                double newWeight = con.getWeight() + deltaWeight;
                con.setDeltaWeight(deltaWeight);
                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
            }
            i++;
        }
 
        // update weights for the hidden layer
        hiddenLayer.forEach((Neuron n) -> {
            ArrayList<Connection> connections = n.getAllInConnections();
            connections.forEach((con) -> {
                double aj = n.getOutput();
                double ai = con.leftNeuron.getOutput();
                double sumKoutputs = 0;
                int j = 0;
                for (Neuron out_neu : outputLayer) {
                    double wjk = out_neu.getConnection(n.id).getWeight();
                    double desiredOutput = (double) expectedOutput[j];
                    double ak = out_neu.getOutput();
                    j++;
                    sumKoutputs = sumKoutputs
                            + (-(desiredOutput - ak) * ak * (1 - ak) * wjk);
                }
                
                double partialDerivative = aj * (1 - aj) * ai * sumKoutputs;
                double deltaWeight = -learningRate * partialDerivative;
                double newWeight = con.getWeight() + deltaWeight;
                con.setDeltaWeight(deltaWeight);
                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
            });
        });
    }
 
    void run(int maxSteps, double minError) {
        int i;
        // Train neural network until minimum Error reached or maximum steps exceeded
        double error = 1;
        for (i = 0; i < maxSteps && error > minError; i++) {
            error = 0;
            for (int p = 0; p < inputs.length; p++) {
                setInput(inputs[p]);
 
                activate();
 
                output = getOutput();
                resultOutputs[p] = output;
 
                for (int j = 0; j < expectedOutputs[p].length; j++) {
                    double err = Math.pow(output[j] - expectedOutputs[p][j], 2);
                    error += err;
                }
 
                applyBackpropagation(expectedOutputs[p]);
            }
        }
 
        printResult();
         
        System.out.println("Sum of squared errors = " + error);
        
        if (i == maxSteps) {
            System.out.println("!Error training try again");
        } else {
            printAllWeights();
            printWeightUpdate();
        }
    }
     
    void printResult()
    {
        System.out.println("Forward Pass Training");
        for (int p = 0; p < inputs.length; p++) {
            System.out.print("INPUTS: ");
            for (int x = 0; x < layers[0]; x++) {
                System.out.print(inputs[p][x] + " ");
            }
 
            System.out.print("Expected Result: ");
            for (int x = 0; x < layers[2]; x++) {
                System.out.print(expectedOutputs[p][x] + " ");
            }
 
            System.out.print("Actual Result: ");
            for (int x = 0; x < layers[2]; x++) {
                System.out.print(resultOutputs[p][x] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
 
    String weightKey(int neuronId, int conId) {
        return "N" + neuronId + "_C" + conId;
    }
 
    /**
     * Take from hash table and put into all weights
     */
    public void updateAllWeights() {
        
        // update weights for the output layer
        
        outputLayer.forEach((Neuron n) -> {
            ArrayList<Connection> connections = n.getAllInConnections();
            connections.forEach((con) -> {
                String key = weightKey(n.id, con.id);
                double newWeight = weightUpdate.get(key);
                con.setWeight(newWeight);
            });
        });
        
        // update weights for the hidden layer
        
        hiddenLayer.forEach((n) -> {
            ArrayList<Connection> connections = n.getAllInConnections();
            connections.forEach((con) -> {
                String key = weightKey(n.id, con.id);
                double newWeight = weightUpdate.get(key);
                con.setWeight(newWeight);
            });
        });
    }
 
    // trained data
    void trainedWeights() {
        weightUpdate.clear();
         
        weightUpdate.put(weightKey(3, 0), 1.03);
        weightUpdate.put(weightKey(3, 1), 1.13);
        weightUpdate.put(weightKey(3, 2), -0.97);
        weightUpdate.put(weightKey(4, 3), 7.24);
        weightUpdate.put(weightKey(4, 4), -3.71);
        weightUpdate.put(weightKey(4, 5), -0.51);
        weightUpdate.put(weightKey(5, 6), -3.28);
        weightUpdate.put(weightKey(5, 7), 7.29);
        weightUpdate.put(weightKey(5, 8), -0.05);
        weightUpdate.put(weightKey(6, 9), 5.86);
        weightUpdate.put(weightKey(6, 10), 6.03);
        weightUpdate.put(weightKey(6, 11), 0.71);
        weightUpdate.put(weightKey(7, 12), 2.19);
        weightUpdate.put(weightKey(7, 13), -8.82);
        weightUpdate.put(weightKey(7, 14), -8.84);
        weightUpdate.put(weightKey(7, 15), 11.81);
        weightUpdate.put(weightKey(7, 16), 0.44);
    }
 
    public void printWeightUpdate() {
        System.out.println("Printed Weight Update");
        // weights for the hidden layer
        hiddenLayer.stream().map((n) -> n.getAllInConnections()).forEachOrdered((ArrayList<Connection> connections) -> {
            connections.forEach((con) -> {
                String w = format.format(con.getWeight());
                System.out.println("Hidden_Layer_Weight Update: Connection:(" + con.id + "),New_Weight: " + w + ");");
            });
        });
        // weights for the output layer
        outputLayer.stream().map((n) -> n.getAllInConnections()).forEachOrdered((ArrayList<Connection> connections) -> {
            connections.forEach((con) -> {
                String w = format.format(con.getWeight());
                System.out.println("Output_layer_Weight Update: Connection:(" + con.id + "),New_Weight: " + w + ");");
            });
        });
        System.out.println();
    }
 
    public void printAllWeights() {
        System.out.println("All Weights");
        // weights for the hidden layer
        hiddenLayer.forEach((Neuron n) -> {
            ArrayList<Connection> connections = n.getAllInConnections();
            connections.forEach((con) -> {
                double w = con.getWeight();
                System.out.println("Hidden Layer=" + n.id + " connection_number=" + con.id + " Weight=" + w);
            });
        });
        // weights for the output layer
        outputLayer.forEach((Neuron n) -> {
            ArrayList<Connection> connections = n.getAllInConnections();
            connections.forEach((con) -> {
                double w = con.getWeight();
                System.out.println("outputlayer=" + n.id + " connection_number=" + con.id + " weight=" + w);
            });
        });
        System.out.println();
    }
}
