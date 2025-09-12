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
}
