package com.mit.features.pathfind.walker;

import com.mit.event.MsEvent;
import com.mit.global.Dependencies;
import com.mit.util.*;
import java.util.*;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tv.twitch.chat.Chat;

public class WalkerMain {

  boolean state = false;
  List<Vec3> curPath = new ArrayList<>();
  Vec3 curVec = null;
  Vec3 prev = null;

  Map<KeyBinding, Boolean> prevKeybinds = new HashMap<>();
  List<KeyBinding> prevPressedKeybinds = new ArrayList<>();

  public void run(List<Vec3> path, boolean walkState) {
    MinecraftForge.EVENT_BUS.register(this);
    state = walkState;
    curPath = path;
    curVec = BlockUtils.getCenteredVec(path.get(0));
    path.remove(0);
    prev = null;
  }

  @SubscribeEvent
  public void onTick(TickEvent.ClientTickEvent event) {
    if (!state) return;

    double curDist = MathUtils.distanceFromToXZ(Dependencies.mc.thePlayer.getPositionVector(), curVec);

    if (curDist < 1 && Dependencies.mc.thePlayer.posY + 0.5 >= curVec.yCoord) {
      nextBlock();
      return;
    }

    RotationUtils.Rotation needed = RotationUtils.getRotation(curVec.addVector(0.5, 0, 0.5));
    needed.pitch = 0.0F;

    if (Dependencies.mc.thePlayer.onGround) {
      RotationUtils.smoothLook(needed, 300);
    }

    if (!isCloseToJump()) {
      HashSet<KeyBinding> neededKeyPresses = VecUtils.getNeededKeyPressesHash(
        Dependencies.mc.thePlayer.getPositionVector(),
        this.curVec
      );

      for (KeyBinding k : KeyBindHandler.getListKeybinds()) {
        KeyBinding.setKeyBindState(k.getKeyCode(), neededKeyPresses.contains(k));
      }
    }

    KeyBindHandler.setKeyBindState(Dependencies.mc.gameSettings.keyBindJump, isCloseToJump());
  }

  void nextBlock() {
    if (curPath.isEmpty()) {
      stop();
      return;
    }

    prev = curVec;
    curVec = BlockUtils.getCenteredVec(curPath.remove(0));
  }

  boolean isCloseToJump() {
    if (
      prev != null &&
      prev.yCoord == curVec.yCoord &&
      !BlockUtils.getBlockType(BlockUtils.fromVecToBP(prev.addVector(0, -1, 0))).getRegistryName().contains("slab")
    ) {
      return (
        Dependencies.mc.thePlayer.posY < curVec.yCoord &&
        !BlockUtils
          .getBlockType(BlockUtils.fromVecToBP(curVec.addVector(0, -1, 0)))
          .getRegistryName()
          .contains("slab") &&
        Dependencies.mc.thePlayer.onGround &&
        MathUtils.distanceFromToXZ(Dependencies.mc.thePlayer.getPositionVector(), curVec) < 3
      );
    }

    return (
      Dependencies.mc.thePlayer.posY < curVec.yCoord &&
      !BlockUtils.getBlockType(BlockUtils.fromVecToBP(curVec.addVector(0, -1, 0))).getRegistryName().contains("slab") &&
      Dependencies.mc.thePlayer.onGround &&
      MathUtils.distanceFromToXZ(Dependencies.mc.thePlayer.getPositionVector(), curVec) < 3
    );
  }

  public void pause() {
    prevKeybinds.put(KeyBindHandler.keybindA, KeyBindHandler.keybindA.isKeyDown());
    prevKeybinds.put(KeyBindHandler.keybindD, KeyBindHandler.keybindD.isKeyDown());
    prevKeybinds.put(KeyBindHandler.keybindS, KeyBindHandler.keybindS.isKeyDown());
    prevKeybinds.put(KeyBindHandler.keybindW, KeyBindHandler.keybindW.isKeyDown());

    prevKeybinds.put(KeyBindHandler.keyBindJump, KeyBindHandler.keyBindJump.isKeyDown());
    prevKeybinds.put(KeyBindHandler.keyBindShift, KeyBindHandler.keyBindShift.isKeyDown());

    KeyBindHandler.resetKeybindState();
    this.state = false;
  }

  public void unpause() {
    prevKeybinds.forEach(KeyBindHandler::setKeyBindState);
    this.state = true;
  }

  void stop() {
    KeyBindHandler.resetKeybindState();
    this.state = false;
    this.curPath = null;
    MinecraftForge.EVENT_BUS.unregister(this);
  }

  public boolean isDone() {
    return curPath == null;
  }
}
