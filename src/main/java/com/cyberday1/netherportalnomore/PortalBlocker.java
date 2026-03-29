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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
//? if mc: >=1.20 {
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
//?}

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

    // Block tag for The Undergarden portal frame blocks (undergarden:portal_frame_blocks).
    // Includes stone brick variants, deepslate variants, and mod-specific blocks.
    //? if mc: <1.20 {
    /*private static final TagKey<Block> UNDERGARDEN_PORTAL_FRAME =
        TagKey.create(net.minecraft.core.Registry.BLOCK_REGISTRY, new ResourceLocation("undergarden", "portal_frame_blocks"));*/
    //?} else if mc: <1.21 {
    /*private static final TagKey<Block> UNDERGARDEN_PORTAL_FRAME =
        TagKey.create(Registries.BLOCK, new ResourceLocation("undergarden", "portal_frame_blocks"));*/
    //?} else {
    private static final TagKey<Block> UNDERGARDEN_PORTAL_FRAME =
        TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("undergarden", "portal_frame_blocks"));
    //?}

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
        if (stack.isEmpty()) {
            return;
        }

        BlockPos clickedPos = event.getPos();
        BlockState clickedState = level.getBlockState(clickedPos);

        // Nether portal: standard igniter used on obsidian, with air on the clicked face
        if (isPortalLightingItem(stack) && clickedState.is(Blocks.OBSIDIAN)) {
            BlockPos firePos = clickedPos.relative(event.getFace());
            if (level.getBlockState(firePos).isAir()) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
            }
            return;
        }

        // The Undergarden portal: catalyst used on any portal frame block
        if (isCatalystItem(stack) && clickedState.is(UNDERGARDEN_PORTAL_FRAME)) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }
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

    // Detects The Undergarden's Catalyst item by registry name to avoid a hard dependency.
    private static boolean isCatalystItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        //? if mc: <1.20 {
        /*ResourceLocation id = net.minecraft.core.Registry.ITEM.getKey(stack.getItem());*/
        //?} else {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        //?}
        return id != null && "undergarden".equals(id.getNamespace()) && "catalyst".equals(id.getPath());
    }
}
