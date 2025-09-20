package com.stellarith.beastarium.item;

import com.stellarith.beastarium.Constants;
import com.stellarith.beastarium.ModColors;
import com.stellarith.beastarium.groups.PlayerZoos;
import com.stellarith.beastarium.groups.Zoo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PathMakerItem extends Item {
    public PathMakerItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(
                Component.translatable("item.beastarium.path_maker.tooltip")
                        .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(ModColors.PALE_AQUA))));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    public static List<BlockPos> pathBlocks = new ArrayList<>();

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos blockPos = context.getClickedPos();

        if(context.getLevel().isClientSide)
            return InteractionResult.PASS;

        Zoo zoo = PlayerZoos.get((ServerLevel) context.getLevel()).getGroup(context.getPlayer().getUUID());
        if(zoo == null) {
            pathBlocks = new ArrayList<>();
            return InteractionResult.PASS;
        }

        if(zoo.pathBlocks.contains(blockPos)) {
            zoo.pathBlocks.remove(blockPos);
        } else {
            zoo.pathBlocks.add(blockPos);
        }

        pathBlocks = zoo.pathBlocks;

        return InteractionResult.SUCCESS;
    }


}
