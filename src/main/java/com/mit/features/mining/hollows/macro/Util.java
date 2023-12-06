package com.mit.features.mining.hollows.macro;

import com.mit.global.Dependencies;
import com.mit.util.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.*;
import tv.twitch.chat.Chat;

public class Util {

  public Map<String, Double> blockHardnessData = new HashMap<>();

  public Block[] blocks2Ignore = new Block[] {
    Blocks.lapis_ore,
    Blocks.diamond_ore,
    Blocks.iron_ore,
    Blocks.coal_ore,
    Blocks.wool,
    Blocks.air,
    Blocks.stone,
  };

  public Util() {
    blockHardnessData.put("amber_block", 3000.0);
    blockHardnessData.put("amber_shard", 3000.0);

    blockHardnessData.put("sapphire_block", 3000.0);
    blockHardnessData.put("sapphire_shard", 3000.0);

    blockHardnessData.put("topaz_block", 3800.0);
    blockHardnessData.put("topaz_shard", 3800.0);

    blockHardnessData.put("jade_block", 3000.0);
    blockHardnessData.put("jade_shard", 3000.0);

    blockHardnessData.put("jasper_block", 4800.0);
    blockHardnessData.put("jasper_shard", 4800.0);

    blockHardnessData.put("amethyst_block", 3000.0);
    blockHardnessData.put("amethyst_shard", 3000.0);

    blockHardnessData.put("ruby_block", 2300.0);
    blockHardnessData.put("ruby_shard", 2300.0);
  }

  public int getAOTVSlot() {
    return -1;
  }

  public int getTicksPerBlock(IBlockState block, double miningSpeed) {
    double res = getTicksPerBreak(getGem(block), miningSpeed);
    ChatUtils.chat(getGem(block) + " | " + miningSpeed + " -> " + res);
    return (int) res;
  }

  public int getTicksPerBreak(String name, double miningSpeed) {
    double ticks = (double) (blockHardnessData.get(name) * 30 / miningSpeed);
    return (int) (Math.max(ticks, 4) * 50);
  }

  public String getGem(IBlockState blockState) {
    Block block = blockState.getBlock();
    if (blockState.getValue(BlockColored.COLOR) == EnumDyeColor.RED) {
      if (block == Blocks.stained_glass_pane) return "ruby_shard"; else return "ruby_block";
    } else if (blockState.getValue(BlockColored.COLOR) == EnumDyeColor.ORANGE) {
      if (block == Blocks.stained_glass_pane) return "amber_shard"; else return "amber_block";
    } else if (blockState.getValue(BlockColored.COLOR) == EnumDyeColor.LIGHT_BLUE) {
      if (block == Blocks.stained_glass_pane) return "sapphire_shard"; else return "sapphire_block";
    } else if (blockState.getValue(BlockColored.COLOR) == EnumDyeColor.YELLOW) {
      if (block == Blocks.stained_glass_pane) return "topaz_shard"; else return "topaz_block";
    } else if (blockState.getValue(BlockColored.COLOR) == EnumDyeColor.LIME) {
      if (block == Blocks.stained_glass_pane) return "jade_shard"; else return "jade_block";
    } else if (blockState.getValue(BlockColored.COLOR) == EnumDyeColor.MAGENTA) {
      if (block == Blocks.stained_glass_pane) return "jasper_shard"; else return "jasper_block";
    } else if (blockState.getValue(BlockColored.COLOR) == EnumDyeColor.PURPLE) {
      if (block == Blocks.stained_glass_pane) return "amethyst_shard"; else return "amethyst_block";
    }

    return "";
  }

  public List<BlockPos> getBlocksAround() {
    return (BlockUtils.getBlocksInRadius(4, 4, 4, Dependencies.mc.thePlayer.getPositionVector())).stream()
      .filter(a ->
        (
          BlockUtils.getBlockType(a) == Blocks.stained_glass_pane || BlockUtils.getBlockType(a) == Blocks.stained_glass
        ) &&
        MathUtils.distanceFromTo(a, Dependencies.mc.thePlayer.getPosition()) <=
        Dependencies.mc.playerController.getBlockReachDistance()
      )
      .collect(Collectors.toList());
  }

  public double getNextRotStart(Vec3 nextLookBlockVec, double rotationTime, Vec3 lookVec) {
    RotationUtils.Rotation rot = RotationUtils.getRotToBlock(nextLookBlockVec);

    double addYaw = rot.yaw / 100;
    double addPitch = rot.pitch / 100;

    double addedYaw = 0;
    double addedPitch = 0;

    Vec3 oneBAway = getPositionOneBlockAway(
      lookVec,
      Dependencies.mc.thePlayer.rotationPitch,
      Dependencies.mc.thePlayer.rotationYaw
    );
    final Vec3 ply = Dependencies.mc.thePlayer.getPositionVector().addVector(0, Dependencies.mc.thePlayer.eyeHeight, 0);
    double[] res = Objects.requireNonNull(
      RayTracingUtils.getCollisionVecs(
        ply.xCoord,
        ply.yCoord,
        ply.zCoord,
        oneBAway.xCoord,
        oneBAway.yCoord,
        oneBAway.zCoord,
        10,
        new Block[] { Blocks.air, Blocks.stone, Blocks.coal_ore }
      )
    )
      .output;
    Vec3 vec = new Vec3(res[0], res[1], res[2]);
    BlockPos bp = new BlockPos(vec.xCoord, vec.yCoord, vec.zCoord);

    while (
      Math.abs(addedYaw) <= Math.abs(rot.yaw) &&
      Math.abs(addedPitch) <= Math.abs(rot.pitch) &&
      bp.equals(BlockUtils.fromVecToBP(lookVec))
    ) {
      addedYaw += addYaw;
      addedPitch += addPitch;

      res =
        Objects.requireNonNull(
          RayTracingUtils.getCollisionVecs(
            ply.xCoord,
            ply.yCoord,
            ply.zCoord,
            oneBAway.xCoord,
            oneBAway.yCoord,
            oneBAway.zCoord,
            10,
            new Block[] { Blocks.air, Blocks.stone, Blocks.coal_ore }
          )
        )
          .output;
      vec = new Vec3(res[0], res[1], res[2]);
      bp = new BlockPos(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    double yawPerMs = rot.yaw / rotationTime;

    return addedYaw / yawPerMs;
  }

  private Vec3 getPositionOneBlockAway(Vec3 entity, float pitch, float yaw) {
    double posX = entity.xCoord;
    double posY = entity.yCoord;
    double posZ = entity.zCoord;

    // Calculate the offset in X, Y, and Z directions based on pitch and yaw
    double offsetHorizontal = 1.0; // Adjust the distance as needed (1 block away)
    double offsetX =
      -MathHelper.sin(yaw / 180.0F * (float) Math.PI) *
      MathHelper.cos(pitch / 180.0F * (float) Math.PI) *
      offsetHorizontal;
    double offsetY = -MathHelper.sin((pitch) / 180.0F * (float) Math.PI) * offsetHorizontal;
    double offsetZ =
      MathHelper.cos(yaw / 180.0F * (float) Math.PI) *
      MathHelper.cos(pitch / 180.0F * (float) Math.PI) *
      offsetHorizontal;

    double newX = posX + offsetX;
    double newY = posY + offsetY;
    double newZ = posZ + offsetZ;

    return new Vec3(newX, newY, newZ);
  }

  public enum MiningState {
    MINING,
    TELEPORTING,
    ROTATING,
  }
}
