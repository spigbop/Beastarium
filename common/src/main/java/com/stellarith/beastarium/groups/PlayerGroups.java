package com.stellarith.beastarium.groups;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerGroups extends SavedData {
    public static PlayerGroups inst = null;
    private final Set<PlayerGroup> groups = new HashSet<>();

    public static PlayerGroups load(CompoundTag compoundTag) {
        if(inst == null)
            inst = new PlayerGroups();

        ListTag teamsList = compoundTag.getList("PlayerGroups", Tag.TAG_COMPOUND);
        for(Tag teamTag : teamsList) {
            inst.groups.add(PlayerGroup.fromNbt((CompoundTag) teamTag));
        }

        return inst;
    }

    public void addGroup(PlayerGroup playerGroup) {
        groups.add(playerGroup);
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        ListTag teamsList = new ListTag();
        for(PlayerGroup group : groups) {
            teamsList.add(group.toNbt());
        }
        compoundTag.put("PlayerGroups", teamsList);
        return compoundTag;
    }

    public static PlayerGroups get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                PlayerGroups::load,
                PlayerGroups::new,
                "PlayerGroupData"
        );
    }

    public PlayerGroup getGroup(UUID member) {
        for(PlayerGroup group : groups) {
            for(Member groupMember : group.getMembers()) {
                if(groupMember.uuid.equals(member)) {
                    return group;
                }
            }
        }
        return null;
    }

    public void deleteGroup(PlayerGroup group) {
        groups.remove(group);
        setDirty();
    }
}
