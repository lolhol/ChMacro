package com.mit.AI;

import com.mit.util.BlockUtils;
import com.mit.util.ChatUtils;
import com.mit.util.ThreadUtils;
import java.util.List;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

/**
 * @apiNote This class is not done yet. It must be modified to work. Instead of getting center block of each slice,
 * we have to get the platform and vein slices.
 */
public class AITrainerThread extends Thread {

  final MultiLayerNetwork ai;
  final List<Workload> trainingWorkload = new java.util.ArrayList<>();
  final boolean enableVerbose;
  final int epochsPerWorkload;
  final int cubeSideSize;

  String error = "";

  public AITrainerThread(final MultiLayerNetwork ai, boolean enableVerbose, int epochsPerWorkload, int cubeSize) {
    this.ai = ai;
    this.enableVerbose = enableVerbose;
    this.epochsPerWorkload = epochsPerWorkload;
    this.cubeSideSize = cubeSize;
    ai.init();
  }

  @Override
  public void run() {
    try {
      while (true) {
        if (trainingWorkload.isEmpty()) {
          ThreadUtils.sleep(1000);
          continue;
        }

        ChatUtils.chat("Inserting into model...");

        long startTime = System.currentTimeMillis();

        final Workload curRem = trainingWorkload.remove(0);
        final Vec3 outPut = curRem.position;
        final BlockPos centerBlock = BlockUtils.fromVecToBP(outPut);
        final INDArray input = Nd4j.create(1, 1, cubeSideSize, cubeSideSize, cubeSideSize);
        AIUtils.convertToRelativeData(curRem.blockData, centerBlock, curRem.curBreaking, curRem.breakProgress, input);

        final INDArray output = Nd4j.create(1, 3);
        output.putScalar(0, outPut.xCoord);
        output.putScalar(1, outPut.yCoord);
        output.putScalar(2, outPut.zCoord);

        ai.fit(input, output);
        ChatUtils.chat("Inserted into model. Took " + (System.currentTimeMillis() - startTime) + "ms.");
      }
    } catch (Exception e) {
      ChatUtils.chat("Error in AITrainerThread: " + e.getMessage());
      System.out.println(e.getMessage());
      this.error = e.getMessage();
      e.printStackTrace();
    }
  }

  String isError() {
    return error;
  }

  public void addTrainingWorkload(List<BlockPos> data, Vec3 position, BlockPos curBreaking, int breakProgress) {
    trainingWorkload.add(new Workload(data, position, curBreaking, breakProgress));
  }
}
