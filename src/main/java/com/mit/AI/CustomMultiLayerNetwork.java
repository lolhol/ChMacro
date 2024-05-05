package com.mit.AI;

import java.io.File;
import java.io.IOException;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

// l8tr?
public class CustomMultiLayerNetwork extends MultiLayerNetwork {

  public String latestFileSaveName = "";

  public CustomMultiLayerNetwork(MultiLayerConfiguration configuration) {
    super(configuration);
  }

  @Override
  public void save(File path) throws IOException {
    path.createNewFile();
    save(path, true);
    latestFileSaveName = path.getName();
  }
}
