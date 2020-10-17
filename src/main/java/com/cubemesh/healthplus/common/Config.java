package com.cubemesh.healthplus.common;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber
public class Config {
    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_MECHANICS = "mechanics";

    public static ForgeConfigSpec SERVER_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    // Block Grime
    public static final String SUBCATEGORY_GRIME = "grime";
    public static ForgeConfigSpec.BooleanValue GRIME_DO_MANURE_EFFECT;
    public static ForgeConfigSpec.BooleanValue GRIME_DO_DECAY;
    public static ForgeConfigSpec.BooleanValue GRIME_DO_ANIMALS_DEFECATE;
    public static ForgeConfigSpec.IntValue GRIME_MANURE_EFFECT_REACH;
    public static ForgeConfigSpec.IntValue GRIME_MANURE_EFFECT_REPETITIONS;

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        SERVER_BUILDER.comment("Settings that deal with mod mechanics").push(CATEGORY_MECHANICS);

        setupGrimeBlockConfiguration(SERVER_BUILDER, CLIENT_BUILDER);

        SERVER_BUILDER.pop();

        SERVER_CONFIG = SERVER_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    private static void setupGrimeBlockConfiguration(ForgeConfigSpec.Builder server, ForgeConfigSpec.Builder client) {
        server.comment("Grime Block Mechanics").push(SUBCATEGORY_GRIME);

        GRIME_DO_ANIMALS_DEFECATE = server.comment("Do animals defecate grime")
                .define("doAnimalsDefecate", true);
        GRIME_DO_DECAY = server.comment("Toggles whether Grime blocks decay")
                .define("doDecay", true);

        GRIME_DO_MANURE_EFFECT = server.comment("Toggles whether the Grime Block applies the bonemeal effect when it decays")
                .define("doManureEffect", true);
        GRIME_MANURE_EFFECT_REACH = server.comment("How far the manure effect reaches within a square centered on the block")
                .defineInRange("manureEffectReach", 5, 1, 12*16);
        GRIME_MANURE_EFFECT_REPETITIONS = server.comment("How many times each decay applies the manure effect")
                .defineInRange("manureEffectRepetitions", 3, 1, 1024);
    }
}
