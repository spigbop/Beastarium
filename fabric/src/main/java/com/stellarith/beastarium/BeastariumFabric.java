package com.stellarith.beastarium;

import com.stellarith.beastarium.registry.FabricRegistry;
import net.fabricmc.api.ModInitializer;

public class BeastariumFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Beastarium.init();
        FabricRegistry.register();
    }
}
