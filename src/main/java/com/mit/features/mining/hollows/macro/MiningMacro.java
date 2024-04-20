package com.mit.features.mining.hollows.macro;

import com.mit.features.mining.hollows.macro.data.ListUtil;
import com.mit.features.mining.hollows.macro.data.PacketHandlerUtil;
import com.mit.features.mining.hollows.macro.data.TimeUtil;
import com.mit.features.mining.hollows.macro.util.ShifterCallback;
import com.mit.features.mining.hollows.pathfinder.ShiftTo;
import com.mit.features.render.RenderMultipleBlocksMod;
import com.mit.global.Dependencies;
import com.mit.util.*;

import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MiningMacro {

  double ping = 0;
  double miningSpeedNormal = 0;
  double miningSpeedBoost = 0;
  boolean isOn;
  boolean isShifterOn = false;
  MiningMacroUtil util = new MiningMacroUtil();

  BlockPos curMiningBlock = null;
  ListUtil listUtils = new ListUtil();
  boolean isRendered;
  boolean isShiftingToBlock;
  TimeUtil timeUtil = new TimeUtil();
  PacketHandlerUtil packetUtil = new PacketHandlerUtil();
  HashSet<BlockPos> excludedBlocks = new HashSet<>();
  ShiftTo shifter = new ShiftTo();

  public MiningMacro(
    boolean isOn,
    double miningSpeedNormal,
    double miningSpeedBoost,
    double ping,
    long lookRandomizationWindow
  ) {
    this.isOn = isOn;
    this.miningSpeedNormal = miningSpeedNormal;
    this.miningSpeedBoost = miningSpeedBoost;
    this.ping = ping;

    RenderMultipleBlocksMod.renderMultipleBlocks(null, false);
    isRendered = false;

    timeUtil.extraLookTimeWindow = lookRandomizationWindow;
    timeUtil.extraBreakTimeWindow = 200;
    timeUtil.starTimeMsSystem = -1;
    timeUtil.lastMsTime = System.currentTimeMillis();
    // Will need to add this
  }

  @SubscribeEvent
  public void onRender(RenderWorldLastEvent event) {
    if (!isOn) {
      if (packetUtil.isStartSent) {
        Dependencies.mc.thePlayer.swingItem();
        packetUtil.isStartSent = false;
      }
      return;
    }
    KeyBindHandler.setKeyBindState(Dependencies.mc.gameSettings.keyBindSneak, true);
    timeUtil.timeUntilShift += System.currentTimeMillis() - timeUtil.lastMsTime;
    timeUtil.lastMsTime = System.currentTimeMillis();

    //RenderMultipleBlocksMod.renderMultipleBlocks(null, false);

    if (listUtils.possibleBreaks.isEmpty()) {
      util.getAllBlocks(listUtils);
      return;
    } else {
      util.getAllBlocks(listUtils);
      util.updateListData(listUtils, event.partialTicks, excludedBlocks);
      if (listUtils.currentlyPossibleToSee.isEmpty()) {
        listUtils.possibleBreaks.clear();
        return;
      }

      if (curMiningBlock == null) {
        curMiningBlock = listUtils.currentlyPossibleToSee.get(0);
        if (curMiningBlock == null) {
          listUtils.currentlyPossibleToSee.clear();
          return;
        }
        timeUtil.startTimeBreakBlock = System.currentTimeMillis();
        // TODO: add @this support
        timeUtil.projectedEndTimeBreakBlock =
          (long) (
            util.getMiningTimeBlock(curMiningBlock, 1794) + RandomUtils.getRandomTime(0, timeUtil.extraBreakTimeWindow)
          );
      }
    }

    /*if (
      BlockUtils.getBlockType(curMiningBlock) != Blocks.stained_glass &&
      BlockUtils.getBlockType(curMiningBlock) != Blocks.stained_glass_pane
    ) {
      listUtils.currentlyPossibleToSee.remove(curMiningBlock);
      return;
    }*/

    if (
      System.currentTimeMillis() - timeUtil.projectedEndTimeBreakBlock - timeUtil.starTimeMsSystem >= 0 && !isShifterOn
    ) {
      ChatUtils.chat(String.valueOf(timeUtil.timeUntilShift));
      shifter.run(
        curMiningBlock,
        new ShifterCallback() {
          @Override
          public void shifterDone() {
            isShifterOn = false;
            timeUtil.timeUntilShift = 0;
          }
        },
        listUtils.possibleBreaks
      );
      isShifterOn = true;
    }

    if (curMiningBlock != null) {
      Vec3 lookVec = RayTracingUtils.getPossibleLocDefault(
        Dependencies.mc.thePlayer.getPositionVector(),
        curMiningBlock,
        new Block[] { Blocks.air }
      );

      long time = (long) (RandomUtils.getRandomTime(0, timeUtil.extraLookTimeWindow) + 100);
      timeUtil.rotationEndTime = System.currentTimeMillis() + time;

      packetUtil.isStartedRotation = true;

      if (lookVec != null) {
        RotationUtils.smoothLook(RotationUtils.getRotation(lookVec), time);
      }
    }

    if (
      System.currentTimeMillis() - timeUtil.rotationEndTime >= 0 ||
      timeUtil.rotationEndTime - System.currentTimeMillis() <= 200
    ) {
      if (timeUtil.starTimeMsSystem == -1) {
        timeUtil.starTimeMsSystem = System.currentTimeMillis();
      }

      /*ChatUtils.chat(
        String.valueOf(System.currentTimeMillis() - timeUtil.projectedEndTimeBreakBlock - timeUtil.starTimeMsSystem)
      );*/
      if (
        System.currentTimeMillis() - timeUtil.projectedEndTimeBreakBlock - timeUtil.starTimeMsSystem >= 0 ||
        curMiningBlock == null
      ) {
        this.listUtils.currentlyPossibleToSee.remove(curMiningBlock);
        excludedBlocks.add(curMiningBlock);
        this.curMiningBlock = null;
        packetUtil.isStartSent = false;
        packetUtil.isStartedRotation = false;
        timeUtil.starTimeMsSystem = -1;
        //PacketUtils.sendStartPacket(curMiningBlock, PacketUtils.getEnum(curMiningBlock));
      } else {
        if (!packetUtil.isStartSent) {
          packetUtil.isStartSent = true;
          PacketUtils.sendStartPacket(curMiningBlock, PacketUtils.getEnum(curMiningBlock));
          //KeyBindHandler.holdLeftClick();
        }
      }
    }

    if (packetUtil.isStartSent) {
      Dependencies.mc.thePlayer.swingItem();
    }

    /*if (curMiningBlock != null) {

    } else {
      if (packetUtil.isStartSent) {
        PacketUtils.sendAbortPacket(curMiningBlock, );
      }
    }*/

    /*for (BlockPos b : listUtils.currentlyPossibleToSee) {
      Vec3 vec = BlockUtils.fromBPToVec(b);
      if (!RenderMultipleBlocksMod.isRendered(vec)) {
        RenderMultipleBlocksMod.renderMultipleBlocks(vec, true);
      }
    }*/

    isRendered = true;
  }

  @SubscribeEvent
  public void tick(TickEvent.ClientTickEvent event) {
    if (!isOn) return;

    if (timeUtil.timeToClear >= 120) {
      timeUtil.timeToClear = 0;
      excludedBlocks.clear();
    } else {
      timeUtil.timeToClear++;
    }
  }

  private BlockPos getMiningBlock() {
    int i = 0;
    while (i < listUtils.currentlyPossibleToSee.size()) {
      curMiningBlock = listUtils.currentlyPossibleToSee.get(i);
      if (
        MathUtils.distanceFromTo(curMiningBlock, Dependencies.mc.thePlayer.getPosition()) >=
        Dependencies.mc.playerController.getBlockReachDistance()
      ) {
        i++;
        continue;
      }

      return curMiningBlock;
    }

    return null;
  }
}
