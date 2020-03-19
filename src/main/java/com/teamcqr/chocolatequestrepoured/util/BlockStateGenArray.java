package com.teamcqr.chocolatequestrepoured.util;

import com.teamcqr.chocolatequestrepoured.structuregen.generation.ExtendedBlockStatePart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class BlockStateGenArray {
    public enum GenerationPhase {
        MAIN,
        POST
    }

    private Map<BlockPos, ExtendedBlockStatePart.ExtendedBlockState> mainMap = new HashMap<>();
    private Map<BlockPos, ExtendedBlockStatePart.ExtendedBlockState> postMap = new HashMap<>();

    public BlockStateGenArray() {
    }

    public Map<BlockPos, ExtendedBlockStatePart.ExtendedBlockState> getMainMap() {
        return mainMap;
    }

    public Map<BlockPos, ExtendedBlockStatePart.ExtendedBlockState> getPostMap()
    {
        return postMap;
    }

    public boolean addBlockState(BlockPos pos, IBlockState blockState, GenerationPhase phase) {
        ExtendedBlockStatePart.ExtendedBlockState extState = new ExtendedBlockStatePart.ExtendedBlockState(blockState, new NBTTagCompound());
        return addInternal(phase, pos, extState, false);
    }

    public boolean forceAddBlockState(BlockPos pos, IBlockState blockState, GenerationPhase phase) {
        ExtendedBlockStatePart.ExtendedBlockState extState = new ExtendedBlockStatePart.ExtendedBlockState(blockState, new NBTTagCompound());
        return addInternal(phase, pos, extState, true);
    }

    public boolean addBlockState(BlockPos pos, IBlockState blockState, NBTTagCompound nbt, GenerationPhase phase) {
        ExtendedBlockStatePart.ExtendedBlockState extState = new ExtendedBlockStatePart.ExtendedBlockState(blockState, nbt);
        return addInternal(phase, pos, extState, false);
    }

    public boolean forceAddBlockState(BlockPos pos, IBlockState blockState, NBTTagCompound nbt, GenerationPhase phase) {
        ExtendedBlockStatePart.ExtendedBlockState extState = new ExtendedBlockStatePart.ExtendedBlockState(blockState, nbt);
        return addInternal(phase, pos, extState, true);
    }

    private boolean addInternal(GenerationPhase phase, BlockPos pos, ExtendedBlockStatePart.ExtendedBlockState extState, boolean overwrite) {
        boolean added = false;
        Map<BlockPos, ExtendedBlockStatePart.ExtendedBlockState> mapToAdd = getMapFromPhase(phase);

        if (overwrite || !mapToAdd.containsKey(pos)) {
            mapToAdd.put(pos, extState);
            added = true;
        }

        return added;
    }

    private Map<BlockPos, ExtendedBlockStatePart.ExtendedBlockState> getMapFromPhase(GenerationPhase phase) {
        switch (phase) {
            case POST:
                return postMap;
            case MAIN:
            default:
                return mainMap;

        }
    }
}