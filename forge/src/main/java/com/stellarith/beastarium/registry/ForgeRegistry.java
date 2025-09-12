package com.stellarith.beastarium.registry;

import com.stellarith.beastarium.Constants;
import com.stellarith.beastarium.item.ModItemGroups;
import com.stellarith.beastarium.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import static com.stellarith.beastarium.registry.AutoRegistry.getObjectsFrom;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeRegistry {
    @SubscribeEvent
    public static void registerAll(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.ITEMS, helper -> {
            getObjectsFrom(ModItems.class, Item.class).forEach((item, name) -> {
                helper.register(new ResourceLocation(Constants.MOD_ID, name), item);
            });
        });

        event.register(BuiltInRegistries.CREATIVE_MODE_TAB.key(), helper -> {
            getObjectsFrom(ModItemGroups.class, CreativeModeTab.class).forEach((group, name) -> {
                helper.register(new ResourceLocation(Constants.MOD_ID, name), group);
            });
        });
    }
}
