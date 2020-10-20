package com.cubemesh.healthplus.common.block.material;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class MaterialRegistrar {
    public static final Material GRIME = (new Material.Builder(MaterialColor.AIR)).notSolid().doesNotBlockMovement().build();
}
