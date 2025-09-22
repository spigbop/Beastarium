package com.stellarith.beastarium.item;

import net.minecraft.world.item.Item;

public class ModItems {
    public static final Item EMERALD_NUGGET = new EmeraldNuggetItem(new Item.Properties());
    public static final Item ZOO_ENCLOSURE = new ZooEnclosureItem(new Item.Properties().stacksTo(1));
    public static final Item PATH_MAKER = new PathMakerItem(new Item.Properties().stacksTo(1));
}
