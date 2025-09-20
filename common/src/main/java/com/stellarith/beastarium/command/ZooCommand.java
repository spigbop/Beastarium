package com.stellarith.beastarium.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.stellarith.beastarium.groups.*;
import com.stellarith.beastarium.item.PathMakerItem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;

import java.util.*;

public class ZooCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("zoo")
                        .then(Commands.literal("create")
                                .executes(ZooCommand::createTeam))
                        .then(Commands.literal("delete")
                                .executes(ZooCommand::deleteTeam))
                        .then(Commands.literal("info")
                                .executes(ZooCommand::infoTeam))
                        .then(Commands.literal("kick")
                                .then(Commands.argument("target", GameProfileArgument.gameProfile())
                                    .executes(ZooCommand::kickPlayer)))
                        .then(Commands.literal("invite")
                                .then(Commands.argument("target", GameProfileArgument.gameProfile())
                                        .executes(ZooCommand::invitePlayer)))
                        .then(Commands.literal("accept")
                                .then(Commands.argument("target", GameProfileArgument.gameProfile())
                                        .executes(ZooCommand::acceptInvite)))
                        .then(Commands.literal("promote")
                                .then(Commands.argument("target", GameProfileArgument.gameProfile())
                                        .executes(ZooCommand::promotePlayer)))
                        .then(Commands.literal("demote")
                                .then(Commands.argument("target", GameProfileArgument.gameProfile())
                                        .executes(ZooCommand::demotePlayer)))
//                        .then(Commands.literal("transferownership")
//                                .then(Commands.argument("target", GameProfileArgument.gameProfile())
//                                .executes(ZooCommand::transferOwnership)))
//                        .then(Commands.literal("balance")
//                                .then(Commands.literal("add")
//                                        .executes(ZooCommand::addBalance))
//                                .then(Commands.literal("remove")
//                                        .executes(ZooCommand::removeBalance))
//                                .then(Commands.literal("set")
//                                        .executes(ZooCommand::setBalance)))
        );
    }

    private static int createTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        ServerPlayer player = source.getPlayerOrException();
        PlayerZoos groups = PlayerZoos.get(source.getLevel());
        UUID ownerUUID = player.getUUID();

        if (groups.getGroup(ownerUUID) != null) {
            source.sendFailure(Component.literal("You already have a zoo!"));
            return 0;
        }

        Zoo team = new Zoo(ownerUUID);
        groups.addGroup(team);
        PathMakerItem.pathBlocks = team.pathBlocks;

        source.sendSuccess(() -> Component.literal("Zoo created successfully!"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int deleteTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        ServerPlayer player = source.getPlayerOrException();
        PlayerZoos groups = PlayerZoos.get(source.getLevel());

        UUID ownerUUID = player.getUUID();
        Zoo team = groups.getGroup(ownerUUID);

        if (team == null) {
            source.sendFailure(Component.literal("You don't have a zoo!"));
            return 0;
        }

        groups.deleteGroup(team);
        PathMakerItem.pathBlocks = new ArrayList<>();

        source.sendSuccess(() -> Component.literal("Zoo deleted successfully!"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int infoTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        ServerPlayer player = source.getPlayerOrException();
        PlayerZoos groups = PlayerZoos.get(source.getLevel());

        UUID ownerUUID = player.getUUID();
        Zoo team = (Zoo) groups.getGroup(ownerUUID);

        if (team == null) {
            source.sendFailure(Component.literal("You don't have a zoo!"));
            return 0;
        }

        source.sendSuccess(() -> Component.literal("Zoo Info:"), true);

        player.sendSystemMessage(Component.literal("  Zoo Name: " + team.title));
        player.sendSystemMessage(Component.literal("  Balance: " + team.balance + " emeralds"));
        Member[] members = team.getMembers();
        GameProfileCache gameProfileCache = source.getServer().getProfileCache();
        player.sendSystemMessage(Component.literal("  Members (" + members.length + "):"));
        for(Member member : members) {
            Optional<GameProfile> profile = gameProfileCache.get(member.uuid);
            if(profile.isPresent()) {
                player.sendSystemMessage(Component.literal("    " + profile.get().getName() + " (" + member.level.toString() + ")"));
            } else {
                player.sendSystemMessage(Component.literal("    " + member.uuid.toString() + " (" + member.level.toString() + ")"));
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    public static List<UUID> kickNotifications = new ArrayList<>();

    private static int kickPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        ServerPlayer player = source.getPlayerOrException();
        PlayerZoos groups = PlayerZoos.get(source.getLevel());

        UUID ownerUUID = player.getUUID();
        Zoo team = (Zoo) groups.getGroup(ownerUUID);

        if (team == null) {
            source.sendFailure(Component.literal("You don't have a zoo!"));
            return 0;
        }

        Collection<GameProfile> profiles = null;

        try {
            profiles = GameProfileArgument.getGameProfiles(context, "target");
        } catch (CommandSyntaxException e) {
            source.sendFailure(Component.literal("You must include a player!"));
            return 0;
        }

        Member[] members = team.getMembers();

        for (GameProfile profile : profiles) {
            UUID targetUUID = profile.getId();

            if(targetUUID == player.getUUID()) {
                source.sendFailure(Component.literal("You can't kick yourself!"));
                return 0;
            }

            boolean hasPlayer = false;

            for(Member member : members) {
                if(member.uuid == targetUUID) {
                    hasPlayer = true;
                    break;
                }
            }

            String name = profile.getName();

            if(!hasPlayer) {
                source.sendFailure(Component.literal(name + " is not a member!"));
                return 0;
            }

            Member selfMember = team.getMember(player.getUUID());
            Member targetMember = team.getMember(targetUUID);

            if(selfMember.level.ordinal() <= targetMember.level.ordinal()) {
                source.sendFailure(Component.literal(name + " is your boss!"));
                return 0;
            }

            team.removeMember(targetUUID);
            source.sendSuccess(() -> Component.literal("Kicked player " + name + " from the zoo."), true);

            ServerPlayer target = source.getServer().getPlayerList().getPlayer(profile.getId());

            if (target != null) {
                // Player is online
                target.sendSystemMessage(Component.literal("You got kicked from your zoo!"));
            } else {
                // Player is offline
                kickNotifications.add(targetUUID);
            }

            return Command.SINGLE_SUCCESS;
        }

        return 0;
    }

    private static final HashSet<PlayerInvitation> invitations = new HashSet<>();

    private static int invitePlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        ServerPlayer player = source.getPlayerOrException();
        PlayerZoos groups = PlayerZoos.get(source.getLevel());

        UUID ownerUUID = player.getUUID();
        Zoo team = (Zoo) groups.getGroup(ownerUUID);

        if (team == null) {
            source.sendFailure(Component.literal("You don't have a zoo!"));
            return 0;
        }

        Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(context, "target");
        Member[] members = team.getMembers();

        HashSet<Integer> results = new HashSet<>();

        for (GameProfile profile : profiles) {
            UUID targetUUID = profile.getId();

            if(targetUUID == player.getUUID()) {
                source.sendFailure(Component.literal("Go get some friends."));
                results.add(0);
                continue;
            }

            boolean hasPlayer = false;

            for(Member member : members) {
                if(member.uuid == targetUUID) {
                    hasPlayer = true;
                    break;
                }
            }

            String name = profile.getName();

            if(hasPlayer) {
                source.sendFailure(Component.literal(name + " is already in the zoo."));
                results.add(0);
                continue;
            }

            invitations.add(new PlayerInvitation(team, targetUUID));
            source.sendSuccess(() -> Component.literal("Invited player " + name + " to the zoo."), true);

            ServerPlayer target = source.getServer().getPlayerList().getPlayer(profile.getId());

            if (target != null) {
                // Player is online
                target.sendSystemMessage(Component.literal("You got invited to " + player.getName() + "'s zoo!"));
            }

            results.add(Command.SINGLE_SUCCESS);
        }

        results.add(0);
        return Collections.max(results);
    }

    private static int acceptInvite(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        ServerPlayer player = source.getPlayerOrException();
        PlayerZoos groups = PlayerZoos.get(source.getLevel());

        UUID ownerUUID = player.getUUID();
        Zoo team = (Zoo) groups.getGroup(ownerUUID);

        if (team != null) {
            source.sendFailure(Component.literal("You already have a zoo!"));
            return 0;
        }

        Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(context, "target");

        HashSet<Integer> results = new HashSet<>();

        for (GameProfile profile : profiles) {
            UUID targetUUID = profile.getId();

            if(targetUUID == player.getUUID()) {
                source.sendFailure(Component.literal("Go get some friends."));
                results.add(0);
                continue;
            }

            Zoo group = groups.getGroup(targetUUID);
            String name = profile.getName();

            for(PlayerInvitation invitation : invitations) {
                if(invitation.toGroup.equals(group)) {
                    invitations.remove(invitation);
                    group.addMember(player.getUUID());
                    PathMakerItem.pathBlocks = group.pathBlocks;
                    source.sendSuccess(() -> Component.literal("Accepted " + name + "'s invite."), true);

                    ServerPlayer target = source.getServer().getPlayerList().getPlayer(profile.getId());

                    if (target != null) {
                        // Player is online
                        target.sendSystemMessage(Component.literal(player.getName() + " accepted your invite!"));
                    }

                    results.add(Command.SINGLE_SUCCESS);
                }
            }
        }

        results.add(0);
        return Collections.max(results);
    }

    private static int promotePlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        ServerPlayer player = source.getPlayerOrException();
        PlayerZoos groups = PlayerZoos.get(source.getLevel());

        UUID ownerUUID = player.getUUID();
        Zoo team = (Zoo) groups.getGroup(ownerUUID);

        if (team == null) {
            source.sendFailure(Component.literal("You don't have a zoo!"));
            return 0;
        }

        if(team.getOwner().uuid != player.getUUID()) {
            source.sendFailure(Component.literal("You are not the owner."));
            return 0;
        }

        Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(context, "target");

        HashSet<Integer> results = new HashSet<>();

        for (GameProfile profile : profiles) {
            UUID targetUUID = profile.getId();

            if(targetUUID == player.getUUID()) {
                source.sendFailure(Component.literal("You can't promote yourself."));
                results.add(0);
                continue;
            }

            Zoo group = groups.getGroup(targetUUID);
            String name = profile.getName();

            if(group == null || !group.equals(team)) {
                source.sendFailure(Component.literal(name + " is not in your zoo."));
                results.add(0);
                continue;
            }

            Member member = group.getMember(targetUUID);

            if(member.level == Member.Levels.Member) {
                member.level = Member.Levels.Admin;
                source.sendSuccess(() -> Component.literal("Promoted " + name + " to admin."), true);

                ServerPlayer target = source.getServer().getPlayerList().getPlayer(profile.getId());

                if (target != null) {
                    // Player is online
                    target.sendSystemMessage(Component.literal("You have been promoted to admin in your zoo!"));
                }

                results.add(Command.SINGLE_SUCCESS);
            } else {
                source.sendFailure(Component.literal(name + " is already high enough."));
            }
        }

        results.add(0);
        return Collections.max(results);
    }

    private static int demotePlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        ServerPlayer player = source.getPlayerOrException();
        PlayerZoos groups = PlayerZoos.get(source.getLevel());

        UUID ownerUUID = player.getUUID();
        Zoo team = (Zoo) groups.getGroup(ownerUUID);

        if (team == null) {
            source.sendFailure(Component.literal("You don't have a zoo!"));
            return 0;
        }

        if(team.getOwner().uuid != player.getUUID()) {
            source.sendFailure(Component.literal("You are not the owner."));
            return 0;
        }

        Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(context, "target");

        HashSet<Integer> results = new HashSet<>();

        for (GameProfile profile : profiles) {
            UUID targetUUID = profile.getId();

            if(targetUUID == player.getUUID()) {
                source.sendFailure(Component.literal("You can't demote yourself."));
                results.add(0);
                continue;
            }

            Zoo group = groups.getGroup(targetUUID);
            String name = profile.getName();

            if(group == null || !group.equals(team)) {
                source.sendFailure(Component.literal(name + " is not in your zoo."));
                results.add(0);
                continue;
            }

            Member member = group.getMember(targetUUID);

            if(member.level == Member.Levels.Member) {
                member.level = Member.Levels.Admin;
                source.sendSuccess(() -> Component.literal("Demoted " + name + " to member."), true);

                ServerPlayer target = source.getServer().getPlayerList().getPlayer(profile.getId());

                if (target != null) {
                    // Player is online
                    target.sendSystemMessage(Component.literal("You have been demoted to member in your zoo!"));
                }

                results.add(Command.SINGLE_SUCCESS);
            } else {
                source.sendFailure(Component.literal(name + " is already low enough."));
            }
        }

        results.add(0);
        return Collections.max(results);
    }
}
