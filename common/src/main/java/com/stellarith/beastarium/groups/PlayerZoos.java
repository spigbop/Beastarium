package com.stellarith.beastarium.groups;

import com.stellarith.beastarium.zoo.Enclosure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerZoos extends SavedData {
    private final Set<Zoo> groups = new HashSet<>();

    public static PlayerZoos load(CompoundTag compoundTag) {
        PlayerZoos zoos = new PlayerZoos();

        ListTag enclosuresList = compoundTag.getList("Enclosures", Tag.TAG_COMPOUND);
        for(Tag enclosureTag : enclosuresList) {
            Enclosure.fromNbt((CompoundTag) enclosureTag);
            // auto-loads
        }

        ListTag teamsList = compoundTag.getList("PlayerZoos", Tag.TAG_COMPOUND);
        for(Tag teamTag : teamsList) {
            zoos.groups.add(Zoo.fromNbt((CompoundTag) teamTag));
        }

        return zoos;
    }

    public void addGroup(Zoo Zoo) {
        groups.add(Zoo);
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        ListTag teamsList = new ListTag();
        for(Zoo group : groups) {
            teamsList.add(group.toNbt());
        }
        compoundTag.put("PlayerZoos", teamsList);

        ListTag enclosuresList = new ListTag();
        for(Enclosure enclosure : Enclosure.enclosures.values()) {
            enclosuresList.add(enclosure.toNbt());
        }
        compoundTag.put("Enclosures", enclosuresList);

        return compoundTag;
    }

    public static PlayerZoos get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                PlayerZoos::load,
                PlayerZoos::new,
                "ZooData"
        );
    }

    public Zoo getGroup(UUID member) {
        for(Zoo group : groups) {
            for(Member groupMember : group.getMembers()) {
                if(groupMember.uuid.equals(member)) {
                    return group;
                }
            }
        }
        return null;
    }

    public void deleteGroup(Zoo group) {
        groups.remove(group);
        setDirty();
    }
}
