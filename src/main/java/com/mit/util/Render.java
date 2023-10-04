package com.mit.util;

import com.mit.global.Dependencies;
import com.mit.mixin.RenderManagerAccessor;
import java.awt.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class Render {

  public static void drawBox(
    double x,
    double y,
    double z,
    Color color,
    float width,
    float partialTicks,
    boolean isFill
  ) {
    Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
    double x1 = x - (viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks);
    double y1 = y - (viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks);
    double z1 = z - (viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks);
    GlStateManager.pushMatrix();
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();

    //DDSdfsf
    GlStateManager.disableDepth();
    GlStateManager.disableCull();
    GlStateManager.disableLighting();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GL11.glLineWidth(width);

    AxisAlignedBB bb = new AxisAlignedBB(x1, y1, z1, x1 + 1, y1 + 1, z1 + 1);

    RenderGlobal.drawOutlinedBoundingBox(bb, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

    if (isFill) {
      drawFilledBoundingBox(bb, color.getRGB(), 0.5F);
    }

    GlStateManager.disableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.enableTexture2D();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.popMatrix();
    GlStateManager.enableLighting();
    GlStateManager.enableDepth();
    GlStateManager.enableCull();
  }

  public static void drawFilledInBlock(BlockPos block, Color color, float opas, float partialTicks) {
    Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
    double x1 = block.getX() - (viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks);
    double y1 = block.getY() - (viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks);
    double z1 = block.getZ() - (viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks);

    AxisAlignedBB bb = new AxisAlignedBB(x1, y1, z1, x1 + 1, y1 + 1, z1 + 1);

    drawFilledBoundingBox(bb, color.getRGB(), opas);
  }

  public static void drawFilledBoundingBox(AxisAlignedBB aabb, int color, float opacity) {
    GlStateManager.enableBlend();
    GlStateManager.disableDepth();
    GlStateManager.disableLighting();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.disableTexture2D();
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();

    float a = (color >> 24 & 0xFF) / 255.0F;
    float r = (color >> 16 & 0xFF) / 255.0F;
    float g = (color >> 8 & 0xFF) / 255.0F;
    float b = (color & 0xFF) / 255.0F;

    GlStateManager.color(r, g, b, a * opacity);
    worldrenderer.begin(7, DefaultVertexFormats.POSITION);
    worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
    worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
    worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
    worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
    tessellator.draw();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION);
    worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
    worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
    worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
    worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
    tessellator.draw();
    GlStateManager.color(r, g, b, a * opacity);
    worldrenderer.begin(7, DefaultVertexFormats.POSITION);
    worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
    worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
    worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
    worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
    tessellator.draw();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION);
    worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
    worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
    worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
    worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
    tessellator.draw();
    GlStateManager.color(r, g, b, a * opacity);
    worldrenderer.begin(7, DefaultVertexFormats.POSITION);
    worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
    worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
    worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
    worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
    tessellator.draw();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION);
    worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
    worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
    worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
    worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
    tessellator.draw();
    GlStateManager.color(r, g, b, a);
    RenderGlobal.drawSelectionBoundingBox(aabb);
    GlStateManager.enableTexture2D();
    GlStateManager.enableDepth();
    GlStateManager.disableBlend();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
  }

  public static void drawLine(Vec3 vec1, Vec3 vec2, float width, Color color, float partialTicks) {
    Minecraft mc = Minecraft.getMinecraft();

    Vec3 playerPos = mc.thePlayer.getPositionVector();
    GL11.glPushMatrix();
    GL11.glTranslated(-playerPos.xCoord, -playerPos.yCoord, -playerPos.zCoord);

    GL11.glLineWidth(width); // Set line width (2.0F in this example)
    GL11.glDisable(GL11.GL_TEXTURE_2D); // Disable texture rendering
    GL11.glEnable(GL11.GL_BLEND); // Enable alpha blending
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    GL11.glBegin(GL11.GL_LINES);
    GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), 1); // Set line color (red in this example)

    // Render line
    GL11.glVertex3d(vec1.xCoord, vec1.yCoord, vec1.zCoord);
    GL11.glVertex3d(vec2.xCoord, vec2.yCoord, vec2.zCoord);

    GL11.glEnd();

    GL11.glDisable(GL11.GL_BLEND);
    GL11.glEnable(GL11.GL_TEXTURE_2D);

    GL11.glPopMatrix();
  }

  public static void renderStr(String str, double X, double Y, double Z, float partialTicks, boolean showDist) {
    GlStateManager.alphaFunc(516, 0.1F);

    GlStateManager.pushMatrix();

    Entity viewer = Dependencies.MC.getRenderViewEntity();
    RenderManagerAccessor rm = (RenderManagerAccessor) Dependencies.MC.getRenderManager();

    double x = X - rm.getRenderPosX();
    double y = Y - rm.getRenderPosY();
    double z = Z - rm.getRenderPosZ();

    double distSq = x * x + y * y + z * z;
    double dist = Math.sqrt(distSq);

    if (distSq > 144) {
      x *= 12 / dist;
      y *= 12 / dist;
      z *= 12 / dist;
    }

    GlStateManager.translate(x, y, z);
    GlStateManager.translate(0, viewer.getEyeHeight(), 0);

    drawNametag(str);

    GlStateManager.rotate(-Dependencies.MC.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate(Dependencies.MC.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
    GlStateManager.translate(0, -0.25f, 0);
    GlStateManager.rotate(-Dependencies.MC.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
    GlStateManager.rotate(Dependencies.MC.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);

    if (showDist) {
      drawNametag("§e" + Math.round(dist * 10) / 10 + " blocks");
    }

    GlStateManager.popMatrix();

    GlStateManager.disableLighting();
  }

  public static void drawNametag(String str) {
    FontRenderer fontrenderer = Dependencies.MC.fontRendererObj;
    float f1 = 0.0266666688f;
    GlStateManager.pushMatrix();
    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
    GlStateManager.rotate(-Dependencies.MC.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate(Dependencies.MC.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
    GlStateManager.scale(-f1, -f1, f1);
    GlStateManager.disableLighting();
    GlStateManager.depthMask(false);
    GlStateManager.disableDepth();
    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer bufferBuilder = tessellator.getWorldRenderer();
    int i = 0;

    int j = fontrenderer.getStringWidth(str) / 2;
    GlStateManager.disableTexture2D();
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
    bufferBuilder.pos(-j - 1, -1 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
    bufferBuilder.pos(-j - 1, 8 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
    bufferBuilder.pos(j + 1, 8 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
    bufferBuilder.pos(j + 1, -1 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
    tessellator.draw();
    GlStateManager.enableTexture2D();
    fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, 553648127);
    GlStateManager.depthMask(true);

    fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, -1);

    GlStateManager.enableDepth();
    GlStateManager.enableBlend();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.popMatrix();
  }
}
