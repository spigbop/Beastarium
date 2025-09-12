package com.stellarith.beastarium.item;

import com.stellarith.beastarium.ModColors;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class EmeraldNuggetItem extends Item {
    public EmeraldNuggetItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(
                Component.translatable("item.beastarium.emerald_nugget.tooltip")
                        .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(ModColors.PALE_AQUA)))
                        .append(Component.translatable("item.minecraft.emerald")
                                .withStyle(ChatFormatting.DARK_GREEN)));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
