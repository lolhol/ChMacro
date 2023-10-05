package com.mit.features;

import com.mit.features.mining.MiningFeatures;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

public class Features {

  List<Object> allFeatures = new ArrayList<>();

  public Features() {
    allFeatures.addAll(new MiningFeatures().getMiningFeatures());
  }

  public void indexAll() {
    for (Object event : allFeatures) {
      MinecraftForge.EVENT_BUS.register(event);
    }
  }
}
