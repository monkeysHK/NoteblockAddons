package com.mhk.nbaddons.setup;

import com.mhk.nbaddons.blocks.NBTNoteBlock;
import com.mhk.nbaddons.blocks.NoteBlockTileEntity;
import com.mhk.nbaddons.nbaddons;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = nbaddons.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Registration {
    public static final DeferredRegister<Block> VANILLA_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "minecraft");
    public static RegistryObject<Block> NOTEBLOCK = VANILLA_BLOCKS.register(
            "note_block", () -> new NBTNoteBlock(AbstractBlock.Properties.from(Blocks.NOTE_BLOCK)));

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, nbaddons.MOD_ID);
    public static RegistryObject<TileEntityType<NoteBlockTileEntity>> NOTEBLOCK_TE = TILE_ENTITIES.register(
            "note_block", () -> TileEntityType.Builder.create(NoteBlockTileEntity::new, NOTEBLOCK.get()).build(null));

    public static final DeferredRegister<Item> VANILLA_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "minecraft");
    public static final RegistryObject<BlockItem> NOTEBLOCK_BI = VANILLA_ITEMS.register("note_block", () ->
            new BlockItem(NOTEBLOCK.get(), new Item.Properties().group(ItemGroup.REDSTONE)));

    public static void register() {
        VANILLA_BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        VANILLA_ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
