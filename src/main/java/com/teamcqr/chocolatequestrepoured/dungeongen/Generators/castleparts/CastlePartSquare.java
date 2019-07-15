package com.teamcqr.chocolatequestrepoured.dungeongen.Generators.castleparts;

import com.teamcqr.chocolatequestrepoured.dungeongen.Generators.castleparts.addons.CastleAddonRoof;
import com.teamcqr.chocolatequestrepoured.dungeongen.dungeons.CastleDungeon;
import com.teamcqr.chocolatequestrepoured.util.BlockInfo;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Copyright (c) 01.06.2019 Developed by KalgogSmash:
 * https://github.com/kalgogsmash
 */
public class CastlePartSquare implements ICastlePart
{
    private BlockPos start;
    private int sizeX;
    private int sizeY;
    private int sizeZ;
    private int floors;
    private EnumFacing facing;
    private CastleDungeon dungeon;
    private Random random;
    private int startLayer;
    private boolean isTopFloor;

    public CastlePartSquare(BlockPos origin, int sizeX, int sizeZ, int floors, CastleDungeon dungeon, EnumFacing facing, int startLayer)
    {
        this.dungeon = dungeon;
        this.floors = floors;
        this.random = this.dungeon.getRandom();

        this.start = origin;
        this.sizeX = sizeX;
        this.sizeY = this.dungeon.getFloorHeight() * floors;
        this.sizeZ = sizeZ;
        this.facing = facing;
        this.startLayer = startLayer;
        this.isTopFloor = false;
    }

    @Override
    public void generatePart(World world)
    {
        int roomsX = Math.max(1, sizeX / dungeon.getRoomSize());
        int roomsZ = Math.max(1, sizeZ / dungeon.getRoomSize());
        int roomSizeX = sizeX / roomsX;
        int roomSizeZ = sizeZ / roomsZ;
        //int lastRoomOffsetX = sizeX - roomsX * roomSizeX;
        //int lastRoomOffsetZ = sizeZ - roomsZ * roomSizeZ;
        int currentY;
        int floorHeight = dungeon.getFloorHeight();
        int x = start.getX();
        int y = start.getY();
        int z = start.getZ();
        IBlockState blockToBuild;

        ArrayList<BlockInfo> buildList = new ArrayList<>();

        System.out.println("Building a square part at " + x + ", " + y + ", " + z + ". sizeX = " + sizeX + ", sizeZ = " + sizeZ + ". Floors = " + floors + ". Facing = " + facing.toString());

        //for each floor
        for (int currentFloor = 0; currentFloor < floors; currentFloor++)
        {
            currentY = y + currentFloor * (floorHeight + 1);

            //over the entire x/z area
            for (int i = 0; i < sizeX; i++)
            {
                for (int j = 0; j < sizeZ; j++)
                {
                    // place a floor
                    blockToBuild = this.dungeon.getFloorBlock().getDefaultState();
                    buildList.add(new BlockInfo(x + i, currentY, z + j, blockToBuild));
                    // place a ceiling
                    blockToBuild = this.dungeon.getWallBlock().getDefaultState();
                    buildList.add(new BlockInfo(x + i, currentY + floorHeight, z + j, blockToBuild));

                }
            }
            //Build walls

            //Add x walls
            for (int i = 0; i < sizeX; i++)
            {
                for (int j = 0; j < floorHeight; j++)
                {
                    if ((i % 4 == 0) &&
                            (i != 0) &&
                            (i != sizeX - 1) &&
                            ((j == floorHeight / 2) || (j - 1 == floorHeight / 2)))
                    {
                        blockToBuild = Blocks.GLASS_PANE.getDefaultState();
                        buildList.add(new BlockInfo(x + i, currentY + j, z, blockToBuild));
                        buildList.add(new BlockInfo(x + i, currentY + j, z + sizeZ - 1, blockToBuild));
                    }
                    else
                    {
                        blockToBuild = dungeon.getWallBlock().getDefaultState();
                        buildList.add(new BlockInfo(x + i, currentY + j, z, blockToBuild));
                        buildList.add(new BlockInfo(x + i, currentY + j, z + sizeZ - 1, blockToBuild));
                    }
                }
            }
            //Add z walls
            for (int i = 0; i < sizeZ; i++)
            {
                for (int j = 0; j < floorHeight; j++)
                {
                    if ((i % 4 == 0) &&
                            (i != 0) &&
                            (i != sizeX - 1) &&
                            ((j == floorHeight / 2) || (j - 1 == floorHeight / 2)))
                    {
                        blockToBuild = Blocks.GLASS_PANE.getDefaultState();
                        buildList.add(new BlockInfo(x, currentY + j, z + i, blockToBuild));
                        buildList.add(new BlockInfo(x + sizeX - 1, currentY + j, z + i, blockToBuild));
                    }
                    else
                    {
                        blockToBuild = dungeon.getWallBlock().getDefaultState();
                        buildList.add(new BlockInfo(x, currentY + j, z + i, blockToBuild));
                        buildList.add(new BlockInfo(x + sizeX - 1, currentY + j, z + i, blockToBuild));
                    }
                }
            }
        }

        //Build the roof
        currentY = y + floors * (floorHeight + 1);

        // Always make walkable if there is more castle above; otherwise 50% chance of walkable
        CastleAddonRoof.RoofType roofType;
        if (isTopFloor && random.nextBoolean())
        {
            System.out.println("Adding 4 sided roof");
            roofType = CastleAddonRoof.RoofType.FOURSIDED;
        }
        else
        {
            System.out.println("Adding walkable roof");
            roofType = CastleAddonRoof.RoofType.WALKABLE;
        }
        CastleAddonRoof roof = new CastleAddonRoof(x, currentY, z, sizeX, sizeZ, roofType, facing);
        roof.generate(buildList);


        if(!buildList.isEmpty()) {
            for(BlockInfo blockPlace : buildList) {
                blockPlace.build(world);
            }
        }
    }

    @Override
    public boolean isTower()
    {
        return false;
    }

    @Override
    public void setAsTopFloor()
    {
        this.isTopFloor = true;
    }

    @Override
    public int getStartLayer()
    {
        return this.startLayer;
    }
}