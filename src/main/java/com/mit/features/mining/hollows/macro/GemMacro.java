package com.mit.features.mining.hollows.macro;

import com.mit.event.MsEvent;
import com.mit.global.Dependencies;
import com.mit.util.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GemMacro {

  public boolean isMacroOn;

  private GemMacroConf config;
  private Util utils = new Util();

  private List<BlockPos> blocksAround = new ArrayList<>();

  private BlockPos renderBlock;

  private Vec3 curLookBlockVec;
  private Vec3 nextLookBlockVec;

  private BlockPos curStandOnBlock;

  //private KeyBinding breakKey = Dependencies.mc.gameSettings;

  private int curMiningTime = 0;
  private int neededMiningTime = 0;
  private int timeAdd = 0;

  private long timeToNextBlock;

  // TODO: Needs to be changed later to an autofinding module
  private double miningSpeed = 1660;

  public Util.MiningState miningState = null;

  public GemMacro(GemMacroConf config) {
    this.config = new GemMacroConf(null, true, true, true, 400);
    //MinecraftForge.EVENT_BUS.register(this);
    //curStandOnBlock = BlockUtils.getClosest(config.getPath(), Dependencies.mc.thePlayer.getPosition());
  }

  public void reset() {
    curLookBlockVec = null;
    nextLookBlockVec = null;
    blocksAround = new ArrayList<>();
    isMacroOn = false;
  }

  @SubscribeEvent
  public void onMillisecond(MsEvent event) {
    try {
      if (!isMacroOn || miningState == null) {
        return;
      }

      switch (miningState) {
        case MINING:
          // ---------------------------------------------------------
          {
            if (blocksAround.isEmpty()) {
              getNewList();
            }

            if (curLookBlockVec == null) {
              getNewFirst();
            }

            //ChatUtils.chat(String.valueOf(this.neededMiningTime));
            if (this.curMiningTime > this.neededMiningTime) {
              RotationUtils.smoothLook(RotationUtils.getRotation(nextLookBlockVec), config.rotationTime);
              this.curLookBlockVec = null;
              this.miningState = Util.MiningState.ROTATING;
              curMiningTime = 0;
            }

            this.curMiningTime++;
          }
        // -----------------------------------------------------------
        case ROTATING:
          {
            if (RotationUtils.done) {
              this.miningState = Util.MiningState.MINING;
            }
          }
      }
    } catch (NullPointerException e) {
      System.out.println("NULL!");
    }
  }

  void getNewList() {
    List<BlockPos> newList = utils.getBlocksAround();
    if (newList != null && !newList.isEmpty()) {
      blocksAround = newList;
    } else {
      this.miningState = null;
    }
  }

  void getNewFirst() {
    if (nextLookBlockVec != null) {
      this.curLookBlockVec = this.nextLookBlockVec;
    }

    if (!blocksAround.isEmpty()) {
      BlockPos curBP = blocksAround.remove(0);
      this.renderBlock = curBP;
      this.nextLookBlockVec = bpToVec(curBP);
    }

    if (nextLookBlockVec != null) {
      this.timeAdd = (int) this.config.rotationTime / 2;
    }

    if (curLookBlockVec != null) {
      this.neededMiningTime =
        utils.getTicksPerBlock(
          Dependencies.mc.theWorld.getBlockState(BlockUtils.fromVecToBP(curLookBlockVec)),
          this.miningSpeed
        );

      ChatUtils.chat(String.valueOf(neededMiningTime) + "!!!");
    }
  }

  Vec3 bpToVec(BlockPos bp) {
    return RayTracingUtils.adjustLook(
      Dependencies.mc.thePlayer.getPositionVector().addVector(0, Dependencies.mc.thePlayer.eyeHeight, 0),
      bp,
      utils.blocks2Ignore,
      false
    );
  }

  @SubscribeEvent
  public void onRender(RenderWorldLastEvent event) {
    if (renderBlock == null || !isMacroOn) return;

    Render.drawFilledInBlock(renderBlock, Color.BLUE, 0.5F, event.partialTicks);
  }
}
