package com.stellarith.beastarium.groups;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerGroup {
    private final Set<Member> members;
    
    public String title = "Untitled";

    public PlayerGroup(UUID owner) {
        members = new HashSet<>();
        members.add(new Member(owner, Member.Levels.Owner));
    }

    public PlayerGroup(Member members[]) {
        this.members = new HashSet<>();
        this.members.addAll(Arrays.asList(members));
    }

    public PlayerGroup(HashSet<Member> members) {
        this.members = members;
    }

    public PlayerGroup(UUID owner, String title) {
        members = new HashSet<>();
        members.add(new Member(owner, Member.Levels.Owner));

        this.title = title;
    }

    public Member[] getMembers() {
        return members.toArray(Member[]::new);
    }

    public Member getMember(UUID member) {
        for(Member memberObj : members) {
            if(memberObj.uuid == member)
                return memberObj;
        }
        return null;
    }

    public Member getOwner() {
        Member lastMember = null;

        for(Member member : members) {
            lastMember = member;
            if(member.level == Member.Levels.Owner)
                return lastMember;
        }

        if(lastMember == null)
            return null;

        lastMember.level = Member.Levels.Owner;
        return lastMember;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PlayerGroup withTitle(String title) {
        setTitle(title);
        return this;
    }

    public boolean removeMember(UUID member) {
        for(Member memberObj : members) {
            if(memberObj.uuid == member) {
                this.members.remove(memberObj);
                return true;
            }
        }
        return false;
    }

    public Member addMember(UUID member) {
        Member obj = new Member(member, Member.Levels.Member);
        this.members.add(obj);
        return obj;
    }

    public Member addAdmin(UUID member) {
        Member obj = new Member(member, Member.Levels.Admin);
        this.members.add(obj);
        return obj;
    }

    public PlayerGroup withMember(UUID member) {
        addMember(member);
        return this;
    }

    public PlayerGroup withAdmin(UUID member) {
        addAdmin(member);
        return this;
    }

    public PlayerGroup withMembers(UUID members[]) {
        for(UUID member : members) {
            addMember(member);
        }
        return this;
    }

    public PlayerGroup withAdmins(UUID members[]) {
        for(UUID member : members) {
            addAdmin(member);
        }
        return this;
    }

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();

        tag.putString("Title", title);

        ListTag membersList = new ListTag();
        for(Member member : members) {
            membersList.add(StringTag.valueOf(member.uuid.toString() + "@" + member.level.ordinal()));
        }
        tag.put("Members", membersList);

        return tag;
    }

    public static PlayerGroup fromNbt(CompoundTag tag) {
        HashSet<Member> membersSet = new HashSet<>();

        for(Tag playerTag : tag.getList("Members", Tag.TAG_STRING)) {
            String info[] = playerTag.getAsString().split("@");
            membersSet.add(new Member(UUID.fromString(info[0]), Member.Levels.values()[Integer.parseInt(info[1])]));
        }

        PlayerGroup group = new PlayerGroup(membersSet);

        group.title = tag.getString("Title");

        return group;
    }
}
