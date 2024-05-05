package com.mit.AI;

import com.mit.util.ThreadUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * @apiNote This class is not done yet. It must be modified to work. Instead of getting center block of each slice,
 * we have to get the platform and vein slices.
 */
public class AIRunnerThread extends Thread {

  final MultiLayerNetwork ai;
  final int cubeSideSize;

  public AIRunnerThread(int cubeSideSize) {
    this.cubeSideSize = cubeSideSize;
    this.ai = new MultiLayerNetwork(AICreatorFactory.prepAIPositionEvaluatorConfiguration());
  }

  public AIRunnerThread(File fileToLoadFrom, int cubeSideSize) throws IOException {
    this.cubeSideSize = cubeSideSize;
    this.ai = ModelSerializer.restoreMultiLayerNetwork(fileToLoadFrom);
  }

  @Override
  public void run() {
    while (true) {
      ThreadUtils.sleep(1000);
    }
  }

  public BlockPos predict(Workload data) {
    final INDArray input = Nd4j.create(1, 1, cubeSideSize, cubeSideSize, cubeSideSize);
    //AIUtils.convertToRelativeData(data.blockData, centerBlock, curRem.curBreaking, curRem.breakProgress, input);

    return predict(input);
  }

  public BlockPos predict(INDArray input) {
    INDArray out = ai.output(input);
    return new BlockPos(out.getDouble(0), out.getDouble(1), out.getDouble(2));
  }
}
