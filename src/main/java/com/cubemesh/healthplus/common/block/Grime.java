package com.cubemesh.healthplus.common.block;

import com.cubemesh.healthplus.HealthPlus;
import com.cubemesh.healthplus.common.Config;
import net.minecraft.block.*;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@SuppressWarnings({"deprecation", "override"})
public class Grime extends Block {
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS_1_8;
    protected static final VoxelShape[] SHAPES = new VoxelShape[]{VoxelShapes.empty(), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};


    public Grime(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(LAYERS, 8));
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPES[state.get(LAYERS)];
    }

    public boolean isTransparent(BlockState blockState) {return true;}

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LAYERS);
    }

    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        if (!Config.GRIME_DO_DECAY.get())
            return;

        int layer = getNumLayers(state);
        if (layer - 1 == 0)
            worldIn.destroyBlock(pos, false);
        else
            worldIn.setBlockState(pos, withLayerNum(state, layer - 1));
        for (int i = 0; i < Config.GRIME_MANURE_EFFECT_REPETITIONS.get(); i++)
            applyManureEffect(state, worldIn, pos, random);
    }

    public static void addGrimeLevels(BlockState state, World worldIn, BlockPos pos, int n) {
        int layer = getNumLayers(state);
        layer = ((layer + n) % 8) + 1;

        worldIn.setBlockState(pos, withLayerNum(state, layer));
    }

    private void applyManureEffect(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        int vx = 0, vz = 0;
        int r = Config.GRIME_MANURE_EFFECT_REACH.get();
        while (vx == 0 && vz == 0)
        {
            vx = random.nextInt(2*r + 1) - 3;
            vz = random.nextInt(2*r + 1) - 3;
        }
        Vector3i arrow = new Vector3i(vx, 0, vz);
        BlockPos target = pos.add(arrow);
        BlockState blockState = worldIn.getBlockState(target);

        // If the targeted block isn't growable, check the block below it
        if (!(blockState.getBlock() instanceof IGrowable))
        {
            target = target.down();
            blockState = worldIn.getBlockState(target);
            // If the block below the target isn't growable, exit the routine
            if (!(blockState.getBlock() instanceof IGrowable))
            {
                return;
            }
        }

        // If the chosen blockState is growable, then grow it
        IGrowable growable = (IGrowable)blockState.getBlock();
        if(growable.canUseBonemeal(worldIn, random, target, blockState)) {
            growable.grow(worldIn, random, target, blockState);
        }
    }

    private static BlockState withLayerNum(BlockState state, int n) {
        return state.with(LAYERS, n);
    }

    private static int getNumLayers(BlockState state) {
        return state.get(LAYERS);
    }

    /**
     * Borrowed from {@link net.minecraft.block.CarpetBlock}
     */
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return !worldIn.isAirBlock(pos.down());
    }

    public static class GrimeLivingUpdateHandler {
        private final Logger LOGGER = LogManager.getLogger();
        @SubscribeEvent
        public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
            if (event.getEntity() instanceof PlayerEntity || event.getEntity() instanceof VillagerEntity)
                return;

            MobEntity entity = (MobEntity) event.getEntityLiving();
            if (!entity.isAggressive())
            {
                BlockState entityAt = entity.world.getBlockState(entity.getPosition());
                float seconds = Config.GRIME_DEFECATION_RATE.get();
                float chanceSeconds = 1.0f/20.0f;
                float roll = entity.world.getRandom().nextFloat();
                int size = entity.world.getRandom().nextInt(Config.GRIME_DEFECATION_SIZE.get()) + 1;
                if (roll < chanceSeconds/seconds) {
                    if (entityAt.isAir()) {
                        entity.world.setBlockState(entity.getPosition(), BlockRegistrar.GRIME.get().getDefaultState()
                                .with(LAYERS, entity.world.getRandom().nextInt(3) + 1));
                    } else if (entityAt.getBlock() instanceof Grime) {
                        Grime.addGrimeLevels(entityAt, entity.world, entity.getPosition(), size);
                    }
                }
            }
        }
    }
}

