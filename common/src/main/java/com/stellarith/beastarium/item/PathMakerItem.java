package com.stellarith.beastarium.item;

import com.stellarith.beastarium.Constants;
import com.stellarith.beastarium.ModColors;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

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

        if(pathBlocks.contains(blockPos)) {
            Constants.LOG.info("Removed path: " + blockPos.toShortString());
            pathBlocks.remove(blockPos);
        } else {
            Constants.LOG.info("Added path: " + blockPos.toShortString());
            pathBlocks.add(blockPos);
        }

        return InteractionResult.SUCCESS;
    }
}
