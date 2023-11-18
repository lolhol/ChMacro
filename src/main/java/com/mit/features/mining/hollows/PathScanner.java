package com.mit.features.mining.hollows;

import com.mit.features.render.RenderMultipleBlocksMod;
import com.mit.features.render.RenderOneBlockMod;
import com.mit.global.Dependencies;
import com.mit.util.BlockUtils;
import com.mit.util.ChatUtils;
import com.mit.util.MathUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PathScanner {

  int maxX, maxY, maxZ;
  int[][][] gemPos = new int[][][] {
    {
      { -1, 0, 0 },
      { -1, 0, 1 },
      { -1, 1, 0 },
      { -1, 1, 1 },
      { -1, 2, 1 },
      { -1, 2, 2 },
      { -1, 3, 1 },
      { -1, 3, 2 },
      { -1, 3, 3 },
      { -1, 4, 3 },
      { 0, 0, 0 },
      { 0, 0, 1 },
      { 0, 1, 0 },
      { 0, 1, 1 },
      { 0, 1, 2 },
      { 0, 2, 0 },
      { 0, 2, 1 },
      { 0, 2, 2 },
      { 0, 3, 2 },
      { 0, 3, 3 },
      { 0, 4, 2 },
      { 0, 4, 3 },
      { 0, 4, 4 },
      { 0, 5, 3 },
      { 0, 5, 4 },
      { 0, 5, 5 },
      { 0, 6, 4 },
      { 0, 6, 5 },
    },
  };
  private HashSet<BlockPos> alrSeen = new HashSet<>();
  public List<BlockPos> foundPos = new ArrayList<>();
  public boolean isRunning = false;
  public boolean masterS = false;

  public PathScanner(int maxX, int maxY, int maxZ) {
    this.maxX = maxX;
    this.maxY = maxY;
    this.maxZ = maxZ;

    MinecraftForge.EVENT_BUS.register(this);
  }

  public void scan() {
    new Thread(() -> {
      isRunning = true;
      int count = 0;
      for (int i = -maxX; i <= maxX; i++) {
        for (int j = -maxY; j <= maxY; j++) {
          for (int k = -maxZ; k <= maxZ; k++) {
            BlockPos player = Dependencies.mc.thePlayer.getPosition();
            BlockPos BP = new BlockPos(player.getX() + i, player.getY() + j, player.getZ() + k);

            if (alrSeen.contains(BP)) continue;

            if (
              BlockUtils.getBlockType(BP) != Blocks.stained_glass &&
              BlockUtils.getBlockType(BP) != Blocks.stained_glass_pane
            ) {
              continue;
            }

            Tuple<BlockPos, HashSet<BlockPos>> ret = getParentGems(BP);
            if (ret.getFirst() == null) continue;
            this.alrSeen.addAll(ret.getSecond());
            this.foundPos.add(ret.getFirst());
          }
        }
      }

      ChatUtils.chat(String.valueOf(count));

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      isRunning = false;
    })
      .start();
  }

  @SubscribeEvent
  public void onRender(RenderWorldLastEvent event) {
    if (!masterS) return;
    if (!isRunning) {
      RenderMultipleBlocksMod.renderMultipleBlocks(null, false);
      alrSeen.clear();
      foundPos.forEach(a -> {
        RenderMultipleBlocksMod.renderMultipleBlocks(BlockUtils.fromBPToVec(a), true);
      });

      //ChatUtils.chat(String.valueOf(foundPos.size()));
      foundPos.clear();
      RenderOneBlockMod.renderOneBlock(null, false);

      scan();
    }
  }

  private boolean isGlass(Block b) {
    return b == Blocks.stained_glass || b == Blocks.stained_glass_pane;
  }

  public void printGemAboveP() {
    RenderMultipleBlocksMod.renderMultipleBlocks(null, false);
    BlockPos block = null;

    for (int i = 0; i <= 3; i++) {
      BlockPos BP = Dependencies.mc.thePlayer.getPosition().add(0, i, 0);
      if (BlockUtils.getBlockType(BP) == Blocks.stained_glass) {
        block = BP;
        break;
      }
    }

    if (block == null) return;

    List<int[]> blocks = new ArrayList<>();
    for (BlockPos b : BlockUtils
      .getBlocksInRadius(7, 7, 7, BlockUtils.fromBPToVec(block))
      .stream()
      .filter(a -> {
        return isGlass(BlockUtils.getBlockType(a));
      })
      .collect(Collectors.toList())) {
      RenderMultipleBlocksMod.renderMultipleBlocks(BlockUtils.fromBPToVec(b), true);
      blocks.add(new int[] { b.getX() - block.getX(), b.getY() - block.getY(), b.getZ() - block.getZ() });
    }

    StringBuilder str = new StringBuilder("{");
    ChatUtils.chat(String.valueOf(blocks.size()));
    for (int[] ints : blocks) {
      int x = ints[0];
      int y = ints[1];
      int z = ints[2];
      str.append("{").append(x).append(",").append(y).append(",").append(z).append("}").append(",");
    }
    str.append("}");
    //{[-1, 0, 0][-1, 0, 1][-1, 1, 0][-1, 1, 1][-1, 2, 1][-1, 2, 2][-1, 3, 1][-1, 3, 2][-1, 3, 3][-1, 4, 3][0, 0, 0][0, 0, 1][0, 1, 0][0, 1, 1][0, 1, 2][0, 2, 0][0, 2, 1][0, 2, 2][0, 3, 1][0, 3, 2][0, 3, 3][0, 4, 2][0, 4, 3][0, 4, 4][0, 5, 3][0, 5, 4][0, 5, 5][0, 6, 4][0, 6, 5]}
    System.out.println(str);
  }

  private Tuple<BlockPos, HashSet<BlockPos>> getParentGems(BlockPos init) {
    List<BlockPos> open = new ArrayList<>();
    HashSet<BlockPos> all = new HashSet<>();

    open.add(init);
    all.add(init);

    while (!open.isEmpty()) {
      BlockPos cur = open.remove(0);
      for (BlockPos i : BlockPos.getAllInBox(cur.add(-1, -1, -1), cur.add(1, 1, 1))) {
        if (!isGlass(BlockUtils.getBlockType(i))) continue;
        if (!all.contains(i)) {
          all.add(i);
          open.add(i);
        }
      }
    }

    return new Tuple<>(calcForMostAround(all), all);
  }

  private BlockPos calcForMostAround(HashSet<BlockPos> all) {
    AtomicInteger curMax = new AtomicInteger(-10000);
    AtomicReference<BlockPos> block = new AtomicReference<>();
    all.forEach(a -> {
      AtomicInteger cur = new AtomicInteger();
      all.forEach(b -> {
        if (MathUtils.distanceFromTo(a, b) < 2) {
          cur.addAndGet(1);

          if (a.getY() > b.getY()) {
            cur.addAndGet(-b.getY());
          }
        }
      });

      if (cur.get() > curMax.get()) {
        block.set(a);
        curMax.set(cur.get());
      }
    });

    return block.get();
  }

  private boolean isGem(BlockPos block) {
    for (int[][] c : gemPos) {
      int co = 0;

      for (int[] i : c) {
        if (isGlass(BlockUtils.getBlockType(block.add(i[0], i[1], i[2])))) co++;
      }

      if (co == c.length) {
        return true;
      }
    }

    return false;
  }
}
