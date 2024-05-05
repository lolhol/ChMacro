package com.mit.AI;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ops.impl.layers.convolution.Pooling3D;
import org.nd4j.linalg.learning.config.Adam;

public class AICreatorFactory {

  public static MultiLayerConfiguration prepAIPositionEvaluatorConfiguration() {
    return new NeuralNetConfiguration.Builder()
      .seed(1)
      .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
      .updater(new Adam())
      .weightInit(WeightInit.XAVIER)
      .list()
      .layer(
        0,
        new Convolution3D.Builder()
          .kernelSize(3, 3, 3)
          .stride(1, 1, 1)
          .nIn(1)
          .nOut(32)
          .activation(Activation.RELU)
          .build()
      )
      .layer(
        1,
        new Convolution3D.Builder().kernelSize(3, 3, 3).stride(1, 1, 1).nOut(64).activation(Activation.RELU).build()
      )
      .layer(
        2,
        new Convolution3D.Builder().kernelSize(3, 3, 3).stride(1, 1, 1).nOut(128).activation(Activation.SOFTMAX).build()
      )
      .layer(
        3,
        new GlobalPoolingLayer.Builder() // Global pooling layer to flatten the output
          .poolingType(PoolingType.MAX)
          .build()
      )
      .layer(4, new DenseLayer.Builder().activation(Activation.RELU).nIn(128).nOut(64).build())
      .layer(5, new DenseLayer.Builder().activation(Activation.RELU).nIn(64).nOut(32).build())
      .layer(6, new OutputLayer.Builder().activation(Activation.SOFTMAX).nIn(32).nOut(3).build())
      .setInputType(InputType.convolutional3D(Convolution3D.DataFormat.NCDHW, 10, 10, 10, 1))
      .build();
  }
}
