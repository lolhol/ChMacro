package com.mit.features.mining;

import com.mit.features.mining.hollows.GetMetalDetectorChestLoc;
import com.mit.features.mining.hollows.MiningNuker;
import java.util.ArrayList;
import java.util.List;

public class MiningFeatures {

  List<Object> miningFeatures = new ArrayList<>();

  public MiningFeatures() {
    miningFeatures.add(new MiningNuker());
    miningFeatures.add(new GetMetalDetectorChestLoc());
  }

  public List<Object> getMiningFeatures() {
    return miningFeatures;
  }
}
