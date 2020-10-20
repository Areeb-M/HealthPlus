package com.cubemesh.healthplus.common.block;

import com.cubemesh.healthplus.common.Config;
import net.minecraft.block.*;
import net.minecraft.client.renderer.RenderTypeLookup;
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
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@SuppressWarnings("deprecation")
public class Grime extends Block {
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS_1_8;
    protected static final VoxelShape[] SHAPES = new VoxelShape[]{VoxelShapes.empty(), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};


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

    private BlockState withLayerNum(BlockState state, int n) {
        return state.with(LAYERS, n);
    }

    private int getNumLayers(BlockState state) {
        return state.get(LAYERS);
    }

    /**
     * Borrowed from {@link net.minecraft.block.CarpetBlock}
     *
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return !worldIn.isAirBlock(pos.down());
    }
}
