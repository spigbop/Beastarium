package com.stellarith.beastarium.zoo;

import com.stellarith.beastarium.groups.PlayerZoos;
import com.stellarith.beastarium.groups.Zoo;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;

import java.util.*;

public class Enclosure {
    private static int next = 0;
    public static final Map<Integer, Enclosure> enclosures = new HashMap<>();

    public static Enclosure ofId(int fromId) {
        if(enclosures.containsKey(fromId))
            return enclosures.get(fromId);

        return null;
    }

    public String name = "Untitled Enclosure";

    private final Set<BlockPos> blocks;
    private final int id;

    public int id() {
        return this.id;
    }
    public Set<BlockPos> blocks() {
        return this.blocks;
    }

    public Enclosure(Set<BlockPos> blocks, UUID fromPlayer, ServerLevel level) {
        this.blocks = blocks;

        while(enclosures.containsKey(next))
            next ++;
        this.id = next;

        enclosures.put(next, this);

        Zoo zoo = PlayerZoos.get(level).getGroup(fromPlayer);
        zoo.addEnclosure(this);

        next ++;
    }

    private Enclosure(int id, String name) {
        this.name = name;
        this.blocks = new HashSet<>();
        this.id = id;

        enclosures.put(id, this);
    }

    public static Enclosure fromNbt(CompoundTag compoundTag) {
        int id = compoundTag.getInt("Id");
        String name = compoundTag.getString("Name");

        Enclosure enclosure = new Enclosure(id, name);

        ListTag blocksList = compoundTag.getList("Blocks", Tag.TAG_COMPOUND);
        for(Tag blockTag : blocksList) {
            String info[] = blockTag.getAsString().split(",");
            int x = Integer.parseInt(info[0]);
            int y = Integer.parseInt(info[1]);
            int z = Integer.parseInt(info[2]);
            enclosure.blocks.add(new BlockPos(x, y, z));
        }

        return enclosure;
    }

    public CompoundTag toNbt() {
        CompoundTag compoundTag = new CompoundTag();

        compoundTag.putInt("Id", id);
        compoundTag.putString("Name", name);

        ListTag blocksList = new ListTag();
        for(BlockPos blockPos : blocks) {
            String x = Integer.toString(blockPos.getX());
            String y = Integer.toString(blockPos.getY());
            String z = Integer.toString(blockPos.getZ());
            blocksList.add(StringTag.valueOf(x + "," + y + "," + z));
        }
        compoundTag.put("Blocks", blocksList);

        return compoundTag;
    }
}
