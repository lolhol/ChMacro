package com.mit.features;

import com.mit.features.mining.MiningFeatures;
import com.mit.features.render.RenderModules;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.MinecraftForge;

public class Features {

  List<Object> allFeatures = new ArrayList<>();
  MiningFeatures miningFeatures = new MiningFeatures();
  RenderModules renderModules = new RenderModules();

  public Features() {
    allFeatures.addAll(miningFeatures.getMiningFeatures());
    allFeatures.addAll(renderModules.getRenderFeatures());
  }

  public List<Object> getList() {
    return allFeatures;
  }
}
