package com.mit.features;

import com.mit.features.mining.MiningFeatures;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.MinecraftForge;

public class Features {

  List<Object> allFeatures = new ArrayList<>();
  MiningFeatures miningFeatures = new MiningFeatures();

  public Features() {
    allFeatures.addAll(miningFeatures.getMiningFeatures());
  }

  public List<Object> getList() {
    return allFeatures;
  }
}
