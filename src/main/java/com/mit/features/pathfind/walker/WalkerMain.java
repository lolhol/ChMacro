package com.mit.features.pathfind.walker;

import com.mit.event.MsEvent;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class WalkerMain {

  boolean state = false;
  List<Vec3> curPath = new ArrayList<>();
  Vec3 curVec = null;

  public void run(List<Vec3> path, boolean walkState) {
    state = walkState;
    curPath = path;
    curVec = path.get(0);
    path.remove(0);
  }

  @SubscribeEvent
  public void onMillisecond(MsEvent event) {
    if (!state) {
    }
  }
}
