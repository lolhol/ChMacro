package com.mit.AI;

import com.mit.event.BlockBreakStateUpdateEvent;
import com.mit.global.Dependencies;
import com.mit.util.BlockUtils;
import com.mit.util.ChatUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import lombok.Setter;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

public class ArtificialInteli {

  final MultiLayerConfiguration conf = AICreatorFactory.prepAIPositionEvaluatorConfiguration();
  CustomMultiLayerNetwork network;
  AITrainerThread trainerThread;
  AIRunnerThread runnerThread;
  File pathToMcFolder = new File(Dependencies.mc.mcDataDir, "MiningInTwo");
  boolean isFirst = true;

  @Setter
  boolean isTraining = true;

  @Setter
  boolean working;

  public ArtificialInteli(boolean working) {
    this.network = new CustomMultiLayerNetwork(conf);
    this.trainerThread = new AITrainerThread(network, true, 10, 10);
    this.working = working;
  }

  public ArtificialInteli() {
    this(false);
  }

  public String getLatestSavedModel() {
    return network.latestFileSaveName;
  }

  private void makeTrainingWorkload() {
    this.network = new CustomMultiLayerNetwork(conf);
    this.trainerThread = new AITrainerThread(network, true, 10, 10);
  }

  public void saveModel() {
    try {
      File file = new File(pathToMcFolder, System.currentTimeMillis() + "model");
      network.save(file, true);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @SubscribeEvent
  public void onBlockStateUpdate(BlockBreakStateUpdateEvent event) {
    if (event.progress == -1 || !working) return;

    if (isTraining) {
      handleTraining(event);
    } else {}
  }

  void handleTesting(BlockBreakStateUpdateEvent event) {}

  void handleTraining(BlockBreakStateUpdateEvent event) {
    if (!Objects.equals(trainerThread.isError(), "")) {
      System.out.println(trainerThread.isError());
      ChatUtils.chat("!!!");
      makeTrainingWorkload();
      isFirst = true;
    }

    if (isFirst) {
      isFirst = false;
      ChatUtils.chat("starting training...");
      trainerThread.start();
      ChatUtils.chat("doing...");
    }

    final Vec3 positionVec = Dependencies.mc.thePlayer.getPositionVector();

    // FIXME: THIS DOES NOT WORK! we need to get the blocks NOT in radius of the player but around him.
    List<BlockPos> blocksAround = BlockUtils.getBlocksInRadius(5, 5, 5, positionVec);

    trainerThread.addTrainingWorkload(blocksAround, positionVec, event.position, event.progress);
    ChatUtils.chat("progress: " + event.progress);
  }

  BlockPos getCenterPos(List<BlockPos> positions) {
    int totalX = 0;
    int totalY = 0;
    int totalZ = 0;

    for (BlockPos pos : positions) {
      totalX += pos.getX();
      totalY += pos.getY();
      totalZ += pos.getZ();
    }

    return new BlockPos(totalX / positions.size(), totalY / positions.size(), totalZ / positions.size());
  }
}
