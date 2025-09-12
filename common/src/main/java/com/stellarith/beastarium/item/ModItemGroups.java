package com.stellarith.beastarium.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroups {
    public static final CreativeModeTab BEASTARIUM = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 3)
            .title(Component.translatable("itemGroup.beastarium.beastarium"))
            .icon(() -> new ItemStack(ModItems.ZOO_ENCLOSURE))
            .displayItems((params, output) -> {
                output.accept(ModItems.EMERALD_NUGGET);
                output.accept(ModItems.ZOO_ENCLOSURE);
                output.accept(ModItems.PATH_MAKER);
            })
            .build();
}
