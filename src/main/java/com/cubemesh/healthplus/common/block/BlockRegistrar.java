package com.cubemesh.healthplus.common.block;

import com.cubemesh.healthplus.HealthPlus;
import com.cubemesh.healthplus.common.block.material.MaterialRegistrar;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockRegistrar {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, HealthPlus.MODID);
    public static final DeferredRegister<Item> BLOCKITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HealthPlus.MODID);

    public static void init()
    {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCKITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    /*
            Block Registration
     */
    public static final RegistryObject<Block> GRIME = BLOCKS.register("grime", () ->
            new Grime(
                AbstractBlock.Properties.create(MaterialRegistrar.GRIME)
                        .doesNotBlockMovement()
                        .speedFactor(0.5f)
                        .hardnessAndResistance(0.5f, 0.1f)
                        .tickRandomly()
            )
    );

    /*
            BlockItem Registration
     */
    public static final RegistryObject<Item> GRIME_ITEM = BLOCKITEMS.register("grime", () ->
            new BlockItem(
                GRIME.get(), new Item.Properties().group(ItemGroup.DECORATIONS)
            )
    );
}
