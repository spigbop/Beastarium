package com.stellarith.beastarium.groups;

import com.stellarith.beastarium.zoo.Enclosure;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Zoo extends PlayerOrganization {
    public List<BlockPos> pathBlocks = new ArrayList<>();
    public List<Enclosure> enclosures = new ArrayList<>();

    public Zoo(UUID owner) {
        super(owner);
    }

    public Zoo(UUID owner, String title) {
        super(owner, title);
    }

    public CompoundTag toNbt() {
        CompoundTag tag = super.toNbt();

        ListTag pathBlocksList = new ListTag();
        for(BlockPos blockPos : pathBlocks) {
            String x = Integer.toString(blockPos.getX());
            String y = Integer.toString(blockPos.getY());
            String z = Integer.toString(blockPos.getZ());
            pathBlocksList.add(StringTag.valueOf(x + "," + y + "," + z));
        }
        tag.put("PathBlocks", pathBlocksList);

        return tag;
    }

    public static Zoo fromNbt(CompoundTag tag) {
        PlayerOrganization organization = PlayerOrganization.fromNbt(tag);
        Zoo zoo = (Zoo) organization;

        ListTag pathBlocksList = tag.getList("PathBlocks", Tag.TAG_COMPOUND);
        for(Tag blockTag : pathBlocksList) {
            String info[] = blockTag.getAsString().split(",");
            int x = Integer.parseInt(info[0]);
            int y = Integer.parseInt(info[1]);
            int z = Integer.parseInt(info[2]);
            zoo.pathBlocks.add(new BlockPos(x, y, z));
        }

        return zoo;
    }

    public void addEnclosure(Enclosure enclosure) {
        enclosures.add(enclosure);
    }

    public void removeEnclosure(Enclosure enclosure) {
        enclosures.remove(enclosure);
    }

    public void removeEnclosure(int enclosureId) {
        int index = 0;
        for(Enclosure enclosure : enclosures) {
            if(enclosure.id() == enclosureId) {
                enclosures.remove(index);
                break;
            }
            index ++;
        }
    }
}
