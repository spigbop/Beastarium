package com.stellarith.beastarium.groups;

import java.util.UUID;

public class PlayerInvitation {
    public PlayerGroup toGroup;
    public UUID player;

    public PlayerInvitation(PlayerGroup toGroup, UUID player) {
        this.toGroup = toGroup;
        this.player = player;
    }
}
