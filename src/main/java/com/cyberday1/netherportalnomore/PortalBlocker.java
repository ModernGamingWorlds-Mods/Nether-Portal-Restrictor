package com.cyberday1.netherportalnomore;

import net.minecraft.core.BlockPos;
//? if mc: >=1.19 {
import net.minecraft.network.chat.Component;
//?} else
/*import net.minecraft.network.chat.TextComponent;*/
//? if mc: >=1.21 {
import net.minecraft.tags.ItemTags;
//?}
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

//? if mc: <1.21 {
/*import net.minecraftforge.event.entity.player.PlayerInteractEvent;
//? if mc: <1.19 {
import net.minecraftforge.event.world.BlockEvent;
//?} else
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.Tags;*/
//?} else {
import net.neoforged.bus.api.SubscribeEvent;
//? if standalone_ebs {
import net.neoforged.fml.common.EventBusSubscriber;
//?} else
/*import net.neoforged.fml.common.Mod;*/
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
//?}

//? if mc: <1.21 {
/*@Mod.EventBusSubscriber(modid = NetherPortalNoMore.MODID)*/
//?} else if standalone_ebs {
@EventBusSubscriber(modid = NetherPortalNoMore.MODID)
//?} else
/*@Mod.EventBusSubscriber(modid = NetherPortalNoMore.MODID)*/
public final class PortalBlocker {

    private PortalBlocker() {}

    @SubscribeEvent
    public static void onPortalSpawn(BlockEvent.PortalSpawnEvent event) {
        if (!event.getState().is(Blocks.NETHER_PORTAL)) return;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        //? if mc: <1.19 {
        /*Level level = (Level) event.getWorld();*/
        //?} else
        Level level = event.getLevel();
        if (level.isClientSide()) {
            return;
        }

        ItemStack stack = event.getItemStack();
        if (!isPortalLightingItem(stack)) {
            return;
        }

        BlockPos clickedPos = event.getPos();
        BlockState clickedState = level.getBlockState(clickedPos);
        if (!clickedState.is(Blocks.OBSIDIAN)) {
            return;
        }

        BlockPos firePos = clickedPos.relative(event.getFace());
        if (!level.getBlockState(firePos).isAir()) {
            return;
        }

        //? if mc: >=26 {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(
                Component.literal("Nether portals are disabled \u2014 check the modpack guide for alternate Nether access"),
                true
            );
        }
        //?} else if mc: >=1.19 {
        /*event.getEntity().displayClientMessage(
            Component.literal("Nether portals are disabled \u2014 check the modpack guide for alternate Nether access"),
            true
        );*/
        //?} else {
        /*event.getPlayer().displayClientMessage(
            new TextComponent("Nether portals are disabled \u2014 check the modpack guide for alternate Nether access"),
            true
        );*/
        //?}
        level.playSound(null, clickedPos, SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 1.0f, 1.0f);
        event.setCancellationResult(InteractionResult.FAIL);
        event.setCanceled(true);
    }

    private static boolean isPortalLightingItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        //? if mc: >=1.21 {
        if (stack.is(ItemTags.CREEPER_IGNITERS) || stack.is(Tags.Items.TOOLS_IGNITER)) {
            return true;
        }
        //?}

        Item item = stack.getItem();
        return item instanceof FlintAndSteelItem || item instanceof FireChargeItem;
    }
}
