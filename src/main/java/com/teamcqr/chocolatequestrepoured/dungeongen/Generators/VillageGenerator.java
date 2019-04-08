package com.teamcqr.chocolatequestrepoured.dungeongen.Generators;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.teamcqr.chocolatequestrepoured.dungeongen.IDungeonGenerator;
import com.teamcqr.chocolatequestrepoured.dungeongen.dungeons.VillageDungeon;
import com.teamcqr.chocolatequestrepoured.util.DungeonGenUtils;
import com.teamcqr.chocolatequestrepoured.util.VectorUtil;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import scala.util.Random;

public class VillageGenerator implements IDungeonGenerator{
	
	private VillageDungeon dungeon;

	private List<File> chosenStructures = new ArrayList<File>();
	private File centerStructure;
	
	private BlockPos startPos;
	private List<BlockPos> structurePosList = new ArrayList<BlockPos>();
	
	public VillageGenerator(VillageDungeon dungeon) {
		this.dungeon = dungeon;
	}
	
	private World worldIn;

	@Override
	public void preProcess(World world, Chunk chunk, int x, int y, int z) {
		// TODO: Calculate positions of structures, then build the support platforms, then calculate 
		// !! IN BUILD STEP !!    PATH BUILDING: First: Chose wether to build x or z first. then build x/z until the destination x/z is reached. then switch to the remaining component and wander to the destination
		BlockPos start = new BlockPos(x, y, z);
		for(int i = 0; i < this.chosenStructures.size(); i++) {
			int vX = DungeonGenUtils.getIntBetweenBorders(this.dungeon.getMinDistance(), this.dungeon.getMaxDistance());
			Vec3i v = new Vec3i(vX, 0, 0);
			Double degrees = ((Integer)new Random().nextInt(360)).doubleValue();
			if(this.dungeon.placeInCircle()) {
				degrees = 360.0 / this.chosenStructures.size();
			}
			v = VectorUtil.rotateVectorAroundY(v, degrees);
			BlockPos newPos = start.add(v);
			int yNew = DungeonGenUtils.getHighestYAt(world.getChunkFromBlockCoords(newPos), newPos.getX(), newPos.getZ(), true);
			
			BlockPos calculatedPos = new BlockPos(newPos.getX(), yNew, newPos.getZ());
			if(!this.structurePosList.contains(calculatedPos)) {
				this.structurePosList.add(calculatedPos);
			}
		}
	}

	@Override
	public void buildStructure(World world, Chunk chunk, int x, int y, int z) {
		//First, build all the support platforms
		
		//then build the paths...
		if(this.structurePosList != null && !this.structurePosList.isEmpty() && this.startPos != null && this.dungeon.buildPaths()) {
			for(BlockPos end : this.structurePosList) {
				this.buildPath(end, this.startPos);
			}
		}
		//And now, build all the structures...
	}

	@Override
	public void postProcess(World world, Chunk chunk, int x, int y, int z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fillChests(World world, Chunk chunk, int x, int y, int z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void placeSpawners(World world, Chunk chunk, int x, int y, int z) {
		// TODO Auto-generated method stub
		
	}
	
	public void addStructure(File f) {
		if(!chosenStructures.contains(f)) {
			chosenStructures.add(f);
		}
	}
	public void setCenterStructure(File f) {
		this.centerStructure = f;
	}
	
	private void buildPath(BlockPos start, BlockPos end) {
		boolean xfirst = new Random().nextBoolean(); 
		if(xfirst) {
			buildX(start, end); 
			buildZ(start.add(end.getX() - start.getX(), 0, 0), end);
		} else {
			buildZ(start, end);
			buildX(start.add(0, 0, end.getZ() - start.getZ()), end);
		}
	}
	
	private void buildX(BlockPos start, BlockPos end) {
		Chunk currChunk = this.worldIn.getChunkFromBlockCoords(start);
		int vX = end.getX() < start.getX() ? -1 : 1;
		int currX = start.getX();
		int z = start.getZ();
		int y = 0;
		do {
			y = DungeonGenUtils.getHighestYAt(currChunk, currX, z, true);
			buildPathSegmentX(new BlockPos(currX, y, z));
			currX += vX;
			currChunk = this.worldIn.getChunkFromBlockCoords(new BlockPos(currX, y, z));
		} while(currX != end.getX());
	}
	
	private void buildZ(BlockPos start, BlockPos end) {
		Chunk currChunk = this.worldIn.getChunkFromBlockCoords(start);
		int vZ = end.getZ() < start.getZ() ? -1 : 1;
		int currZ = start.getZ();
		int x = start.getX();
		int y = 0;
		do {
			y = DungeonGenUtils.getHighestYAt(currChunk, currZ, x, true);
			buildPathSegmentZ(new BlockPos(currZ, y, x));
			currZ += vZ;
			currChunk = this.worldIn.getChunkFromBlockCoords(new BlockPos(x, y, currZ));
		} while(currZ != end.getX());
	}
	
	private void buildPathSegmentX(BlockPos pos) {
		this.worldIn.setBlockState(pos, this.dungeon.getPathMaterial().getDefaultState());
		this.worldIn.setBlockState(pos.north(), this.dungeon.getPathMaterial().getDefaultState());
		this.worldIn.setBlockState(pos.south(), this.dungeon.getPathMaterial().getDefaultState());
		
		supportBlock(pos);
		supportBlock(pos.north());
		supportBlock(pos.south());
	}
	
	private void buildPathSegmentZ(BlockPos pos) {
		this.worldIn.setBlockState(pos, this.dungeon.getPathMaterial().getDefaultState());
		this.worldIn.setBlockState(pos.west(), this.dungeon.getPathMaterial().getDefaultState());
		this.worldIn.setBlockState(pos.east(), this.dungeon.getPathMaterial().getDefaultState());
		
		supportBlock(pos);
		supportBlock(pos.west());
		supportBlock(pos.east());
	}
	
	private void supportBlock(BlockPos pos) {
		int i = 0;
		BlockPos tmpPos = pos.up();
		while(!Block.isEqualTo(this.worldIn.getBlockState(tmpPos).getBlock(), Blocks.AIR) && i <= 3) {
			worldIn.setBlockToAir(tmpPos);
			tmpPos = tmpPos.up();
		}
		tmpPos = pos.down();
		while(Block.isEqualTo(this.worldIn.getBlockState(tmpPos).getBlock(), Blocks.AIR)) {
			this.worldIn.setBlockState(tmpPos, this.dungeon.getPathMaterial().getDefaultState());
			tmpPos = tmpPos.down();
		}
	}
}