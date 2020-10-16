package com.cubemesh.healthplus.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IBlockReader;
import net.minecraft.block.IGrowable;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@SuppressWarnings("deprecation")
public class Grime extends Block {
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS_1_8;
    protected static final VoxelShape[] SHAPES = new VoxelShape[]{VoxelShapes.empty(), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

    private static int manureEffectReach = 5;
    private static int manureEffectRepetition = 3;

    public Grime(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(LAYERS, Integer.valueOf(8)));
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPES[state.get(LAYERS)];
    }

    public boolean isTransparent(BlockState blockState) {return true;}

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LAYERS);
    }

    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        int layer = getNumLayers(state);
        if (layer - 1 == 0)
            worldIn.destroyBlock(pos, false);
        else
            worldIn.setBlockState(pos, withLayerNum(state, layer - 1));
        for (int i = 0; i < manureEffectRepetition; i++)
            applyManureEffect(state, worldIn, pos, random);
    }

    private void applyManureEffect(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        int vx = 0, vz = 0;
        while (vx == 0 && vz == 0)
        {
            vx = random.nextInt(2*manureEffectReach + 1) - 3;
            vz = random.nextInt(2*manureEffectReach + 1) - 3;
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

    private BlockState withLayerNum(BlockState state, int n) {
        return state.with(LAYERS, n);
    }

    private int getNumLayers(BlockState state) {
        return state.get(LAYERS);
    }
}
