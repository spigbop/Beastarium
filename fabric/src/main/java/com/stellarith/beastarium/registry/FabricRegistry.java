package com.stellarith.beastarium.registry;

import com.stellarith.beastarium.Constants;
import com.stellarith.beastarium.item.ModItemGroups;
import com.stellarith.beastarium.item.ModItems;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import static com.stellarith.beastarium.registry.AutoRegistry.getObjectsFrom;

public class FabricRegistry {
    public static void register() {
        getObjectsFrom(ModItems.class, Item.class).forEach((item, name) -> {
            Registry.register(
                    BuiltInRegistries.ITEM,
                    ResourceKey.create(
                            BuiltInRegistries.ITEM.key(),
                            new ResourceLocation(Constants.MOD_ID, name)),
                    item);
        });

        getObjectsFrom(ModItemGroups.class, CreativeModeTab.class).forEach((group, name) -> {
            Registry.register(
                    BuiltInRegistries.CREATIVE_MODE_TAB,
                    ResourceKey.create(
                            BuiltInRegistries.CREATIVE_MODE_TAB.key(),
                            new ResourceLocation(Constants.MOD_ID, name)),
                    group);
        });
    }
}
