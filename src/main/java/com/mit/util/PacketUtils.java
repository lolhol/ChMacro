package com.mit.util;

import com.mit.global.Dependencies;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class PacketUtils {

  public static EnumFacing getEnum(BlockPos block) {
    MovingObjectPosition fake = Dependencies.mc.objectMouseOver;
    fake.hitVec = new Vec3(block);
    return fake.sideHit;
  }

  public static void sendStartPacket(BlockPos block, EnumFacing enumFacing) {
    Dependencies.mc.thePlayer.sendQueue.addToSendQueue(
      new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, block, enumFacing)
    );
    Dependencies.mc.thePlayer.swingItem();
  }

  public static void sendStopPacket(BlockPos block, EnumFacing enumFacing) {
    Dependencies.mc.thePlayer.sendQueue.addToSendQueue(
      new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, block, enumFacing)
    );
  }
}
