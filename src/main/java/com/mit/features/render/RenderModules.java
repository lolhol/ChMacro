package com.mit.features.render;

import java.util.ArrayList;
import java.util.List;

public class RenderModules {

  List<Object> renderModulesList = new ArrayList<>();

  public RenderModules() {
    renderModulesList.add(new RenderMultipleBlocksMod());
    renderModulesList.add(new RenderMultipleLines());
    renderModulesList.add(new RenderOneBlockMod());
    renderModulesList.add(new RenderPoints());
    renderModulesList.add(new RenderSingleLineTwoPoints());
  }

  public List<Object> getRenderFeatures() {
    return renderModulesList;
  }
}
