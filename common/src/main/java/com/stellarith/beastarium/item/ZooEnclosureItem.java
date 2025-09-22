package com.stellarith.beastarium.item;

import com.stellarith.beastarium.Constants;
import com.stellarith.beastarium.ModColors;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.*;

public class ZooEnclosureItem extends Item {
    public ZooEnclosureItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(
                Component.translatable("item.beastarium.zoo_enclosure.tooltip.0")
                        .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(ModColors.PALE_AQUA))));
        tooltipComponents.add(
                Component.translatable("item.beastarium.zoo_enclosure.tooltip.1")
                        .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(ModColors.PALE_AQUA))));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    private static final int MAX_SEARCH_SIZE = 2048;

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if(level.isClientSide)
            return InteractionResult.PASS;

        if(context.getItemInHand().getOrCreateTag().getBoolean("EnclosureAssigned"))
            return InteractionResult.PASS;

        Constants.LOG.info("Used enclosure on block: " + context.getClickedPos().toShortString());

        Set<BlockPos> searched = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(context.getClickedPos());
        int searches = 0;

        while(!queue.isEmpty()) {
            if(searches > MAX_SEARCH_SIZE) {
                context.getPlayer().displayClientMessage(
                        Component.translatable("item.beastarium.zoo_enclosure.too_big")
                                .withStyle(ChatFormatting.RED),
                        true);
                return InteractionResult.FAIL;
            }

            BlockPos poll = queue.poll();

            if(searched.contains(poll)) continue;
            //if(isFence(poll, level)) continue;

            searched.add(poll);

            surficate(queue, poll.north(), level);
            surficate(queue, poll.east(), level);
            surficate(queue, poll.west(), level);
            surficate(queue, poll.south(), level);

            searches ++;
        }

        context.getPlayer().displayClientMessage(
                Component.translatable("item.beastarium.zoo_enclosure.success", searched.size())
                        .withStyle(ChatFormatting.GREEN),
                true);

        ItemStack stack = context.getItemInHand();
        stack.getOrCreateTag().putBoolean("EnclosureAssigned", true);
        stack.getOrCreateTag().putBoolean("EnchantmentsGlintOverride", true);

        return InteractionResult.SUCCESS;
    }

    private boolean isFence(BlockPos where, Level level) {
        BlockState currentState = level.getBlockState(where);
        VoxelShape currentShape = currentState.getCollisionShape(level, where);
        return !currentShape.isEmpty() && currentShape.max(Direction.Axis.Y) >= 1.0;
    }

    private void surficate(Queue<BlockPos> queue, BlockPos where, Level level) {
        Block currentBlock = level.getBlockState(where).getBlock();

        if (currentBlock == Blocks.AIR) {
            BlockPos below = where.below();
            Block belowBlock = level.getBlockState(below).getBlock();
            if(belowBlock != Blocks.AIR)
                queue.add(below);
        } else {
            BlockPos above = where.above();
            Block aboveBlock = level.getBlockState(above).getBlock();
            if(aboveBlock == Blocks.AIR) {
                queue.add(where);
            } else if(level.getBlockState(above.above()).getBlock() == Blocks.AIR) {
                queue.add(above);
            }
        }
    }
}
