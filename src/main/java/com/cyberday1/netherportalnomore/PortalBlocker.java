package com.cyberday1.netherportalnomore;

import net.minecraft.core.BlockPos;
//? if mc: >=1.21 {
import net.minecraft.tags.ItemTags;
//?}
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

//? if forge {
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

//? if forge {
/*@Mod.EventBusSubscriber(modid = NetherPortalNoMore.MODID)*/
//?} else if standalone_ebs {
@EventBusSubscriber(modid = NetherPortalNoMore.MODID)
//?} else
/*@Mod.EventBusSubscriber(modid = NetherPortalNoMore.MODID)*/
public final class PortalBlocker {

    private PortalBlocker() {}

    @SubscribeEvent
    public static void onPortalSpawn(BlockEvent.PortalSpawnEvent event) {
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
