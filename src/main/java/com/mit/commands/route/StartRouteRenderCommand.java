package com.mit.commands.route;

import com.mit.features.render.RouteRenderer;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import org.jetbrains.annotations.NotNull;

public class StartRouteRenderCommand extends Command {

  public StartRouteRenderCommand() {
    super("renderRoute");
  }

  @DefaultHandler
  public void handle() {
    RouteRenderer.switchRendering();
  }
}
