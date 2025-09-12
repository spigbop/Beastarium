package com.stellarith.beastarium.item;

import com.stellarith.beastarium.Constants;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class ItemEventsForge {
    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        Player entity = event.getEntity();

        if(entity.level().isClientSide)
            return;

        ItemStack stack = event.getItem().getItem();
        if(stack.getItem() == ModItems.EMERALD_NUGGET.asItem() || stack.getItem() == Items.EMERALD) {
            event.setCanceled(true);

            if(!entity.getInventory().add(stack)) {
                return;
            }

            //event.getItem().discard();
            entity.level().playSound(
                    null,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    SoundEvents.NOTE_BLOCK_BELL.get(),
                    SoundSource.PLAYERS,
                    1.0F, 2.0F
            );
        }
    }
}
