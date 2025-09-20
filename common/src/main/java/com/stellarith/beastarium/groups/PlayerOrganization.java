package com.stellarith.beastarium.groups;

import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class PlayerOrganization extends PlayerGroup {
    private static final String DATA_NAME = "PlayerOrganization";

    public float balance = 0.0F;

    public PlayerOrganization(UUID owner) {
        super(owner);
    }

    public PlayerOrganization(UUID owner, String title) {
        super(owner, title);
    }

    public PlayerOrganization withBalance(float balance) {
        this.balance = balance;
        return this;
    }

    public CompoundTag toNbt() {
        CompoundTag tag = super.toNbt();
        tag.putFloat("Balance", balance);

        return tag;
    }

    public static PlayerOrganization fromNbt(CompoundTag tag) {
        PlayerGroup group = PlayerGroup.fromNbt(tag);
        PlayerOrganization organization = (PlayerOrganization) group;

        organization.balance = tag.getFloat("Balance");

        return organization;
    }
}