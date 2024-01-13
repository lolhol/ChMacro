package com.mit.util;

import com.mit.event.PacketReceiveEvent;
import com.mit.global.Dependencies;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PacketUtils {

  private static long lastCheckTime = System.currentTimeMillis();
  private static boolean packetSent = false;
  private static long pingTotl = 0;
  private static int packets = 0;

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

  public static void sendAbortPacket(BlockPos block, EnumFacing enumFacing) {
    Dependencies.mc.thePlayer.sendQueue.addToSendQueue(
      new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, block, enumFacing)
    );
  }

  public static long getPing() {
    if (packets == 0) {
      return -1;
    }

    return pingTotl / packets;
  }

  @SubscribeEvent
  public void onPacket(PacketReceiveEvent event) {
    if (!packetSent) return;
    if (event.packet instanceof S37PacketStatistics) {
      pingTotl += System.currentTimeMillis() - lastCheckTime;
      //ChatUtils.chat(PacketUtils.getPing() + " | " + RotationUtils.getRotation(new Vec3(0, 0, 0)));
      packetSent = false;
    }
  }

  @SubscribeEvent
  public void onTick(TickEvent.ClientTickEvent event) {
    if (Dependencies.mc.thePlayer != null) {
      sendPing();
    }
  }

  private static void sendPing() {
    if (System.currentTimeMillis() - lastCheckTime < 500) return;
    Dependencies.mc.thePlayer.sendQueue.addToSendQueue(
      new C16PacketClientStatus(C16PacketClientStatus.EnumState.REQUEST_STATS)
    );
    lastCheckTime = System.currentTimeMillis();
    packetSent = true;
    packets++;

    if (packets >= 200) {
      packets = 1;
      pingTotl = 0;
    }
  }
}
