package com.stellarith.beastarium.rendering;

import com.mojang.blaze3d.vertex.*;
import com.stellarith.beastarium.groups.PlayerZoos;
import com.stellarith.beastarium.item.ModItems;
import com.stellarith.beastarium.item.PathMakerItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class ModRendering {
    public static final float[][] CUBE_VERTS = {
            // bottom
            {0,0,0},{1,0,0},
            {1,0,0},{1,0,1},
            {1,0,1},{0,0,1},
            {0,0,1},{0,0,0},
            // top
            {0,1,0},{1,1,0},
            {1,1,0},{1,1,1},
            {1,1,1},{0,1,1},
            {0,1,1},{0,1,0},
            // verticals
            {0,0,0},{0,1,0},
            {1,0,0},{1,1,0},
            {1,0,1},{1,1,1},
            {0,0,1},{0,1,1}
    };


    public static void renderLevelEventHook(PoseStack poseStack, Matrix4f projMatrix) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null || mc.player == null)
            return;

        if(!mc.player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.PATH_MAKER))
            return;

        Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        var tesselator = Tesselator.getInstance();
        var buffer = tesselator.getBuilder();
        VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        for(BlockPos pos : PathMakerItem.pathBlocks) {
            drawBlockOutline(buffer, pos, 0.004D, 1.0F, 0.84F, 0.0F, 1.0F);
        }

        vertexBuffer.bind();
        vertexBuffer.upload(buffer.end());

        PoseStack matrix = poseStack;
        matrix.pushPose();
        matrix.translate(-view.x, -view.y, -view.z);
        var shader = GameRenderer.getPositionColorShader();
        vertexBuffer.drawWithShader(matrix.last().pose(), projMatrix, shader);
        matrix.popPose();

        VertexBuffer.unbind();
    }

    private static void drawBlockOutline(BufferBuilder buffer, BlockPos pos, double inflate,
                                        float r, float g, float b, float a) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        for(float[] v : CUBE_VERTS) {
            double inflateX = v[0] - 0.5f < 0.5f ? v[0] - inflate : v[0] + inflate;
            double inflateY = v[1] - 0.5f < 0.5f ? v[1] - inflate : v[1] + inflate;
            double inflateZ = v[2] - 0.5f < 0.5f ? v[2] - inflate : v[2] + inflate;

            buffer.vertex(x + inflateX, y + inflateY, z + inflateZ).color(r, g, b, a).endVertex();
        }
    }
}
