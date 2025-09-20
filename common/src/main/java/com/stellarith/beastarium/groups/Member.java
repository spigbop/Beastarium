package com.stellarith.beastarium.groups;

import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class Member {
    public enum Levels {
        Member,
        Admin,
        Owner
    }

    public final UUID uuid;
    public Levels level;

    public Member(UUID uuid) {
        this.uuid = uuid;
        this.level = Levels.Member;
    }

    public Member(UUID uuid, Levels level) {
        this.uuid = uuid;
        this.level = level;
    }

    //public Player getPlayer() {
    //    return
    //}
}
