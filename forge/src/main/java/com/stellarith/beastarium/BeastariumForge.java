package com.stellarith.beastarium;

import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class BeastariumForge {
    public BeastariumForge() {
        Beastarium.init();
    }
}