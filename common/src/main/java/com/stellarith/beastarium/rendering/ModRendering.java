package com.stellarith.beastarium.rendering;

import com.mojang.blaze3d.vertex.*;
import com.stellarith.beastarium.groups.PlayerZoos;
import com.stellarith.beastarium.item.ModItems;
import com.stellarith.beastarium.item.PathMakerItem;
import com.stellarith.beastarium.zoo.Enclosure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
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

    private static final byte RENDERING_DISABLED = 0;
    private static final byte RENDERING_PATHS = 1;
    private static final byte RENDERING_ENCLOSURES = 2;

    public static void renderLevelEventHook(PoseStack poseStack, Matrix4f projMatrix) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null || mc.player == null)
            return;

        ItemStack hand = mc.player.getItemInHand(InteractionHand.MAIN_HAND);

        byte mode = RENDERING_DISABLED;
        if(hand.is(ModItems.PATH_MAKER)) {
            mode = RENDERING_PATHS;
        } else if(hand.is(ModItems.ZOO_ENCLOSURE)) {
            mode = RENDERING_ENCLOSURES;
        }

        if(mode == RENDERING_DISABLED)
            return;

        Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        var tesselator = Tesselator.getInstance();
        var buffer = tesselator.getBuilder();
        VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        if(mode == RENDERING_PATHS) {
            for (BlockPos pos : PathMakerItem.pathBlocks) {
                drawBlockOutline(buffer, pos, 0.004D, 1.0F, 0.84F, 0.0F, 1.0F);
            }
        } else {
            if(hand.hasTag() && hand.getTag().contains("EnclosureObjectId")) {
                Enclosure enclosure = Enclosure.ofId(hand.getOrCreateTag().getInt("EnclosureObjectId"));
                if(enclosure != null) {
                    for(BlockPos pos : enclosure.blocks()) {
                        drawBlockOutline(buffer, pos, 0.004D, 0.0F, 0.84F, 1.0F, 1.0F);
                    }
                }
            }
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
