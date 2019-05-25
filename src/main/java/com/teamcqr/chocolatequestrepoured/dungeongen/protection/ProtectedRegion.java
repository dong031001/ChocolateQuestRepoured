package com.teamcqr.chocolatequestrepoured.dungeongen.protection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.PortalSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Copyright (c) 29.04.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public class ProtectedRegion {
    private AxisAlignedBB boundingBox;
    private BlockPos center;
    private BlockPos min;
    private BlockPos max;
    private boolean enabled = true;
    private UUID dungeonUUID;

    public ProtectedRegion(BlockPos center, double width, double height, double depth,UUID uuid) {
        this.center = center;
        this.boundingBox = new AxisAlignedBB(new BlockPos(center.getX()-width/2,center.getY()-height/2,center.getZ()-depth/2));
        this.min = new BlockPos(boundingBox.minX,boundingBox.minY,boundingBox.minZ);
        this.max = new BlockPos(boundingBox.maxX,boundingBox.maxY,boundingBox.maxZ);
        this.dungeonUUID = uuid;
    }

    public ProtectedRegion(BlockPos min, BlockPos max,UUID uuid) {
        this.boundingBox = new AxisAlignedBB(min,max);
        this.center = new BlockPos(boundingBox.getCenter());
        this.min = new BlockPos(boundingBox.minX,boundingBox.minY,boundingBox.minZ);
        this.max = new BlockPos(boundingBox.maxX,boundingBox.maxY,boundingBox.maxZ);
        this.dungeonUUID = uuid;
    }

    public ProtectedRegion(double width, double height, double depth, BlockPos min,UUID uuid) {
        this.boundingBox = new AxisAlignedBB(min,new BlockPos(min.getX()+width,min.getY()+height,min.getZ()+depth));
        this.center = new BlockPos(boundingBox.getCenter());
        this.min = new BlockPos(boundingBox.minX,boundingBox.minY,boundingBox.minZ);
        this.max = new BlockPos(boundingBox.maxX,boundingBox.maxY,boundingBox.maxZ);
        this.dungeonUUID = uuid;
    }

    public ProtectedRegion(NBTTagCompound tag) {
        BlockPos min = new BlockPos(tag.getDouble("min.x"),tag.getDouble("min.y"),tag.getDouble("min.z"));
        BlockPos max = new BlockPos(tag.getDouble("max.x"),tag.getDouble("max.y"),tag.getDouble("max.z"));
        boolean e = tag.getBoolean("enabled");

        this.max = max;
        this.min = min;
        this.boundingBox = new AxisAlignedBB(min,max);
        this.enabled = e;
        this.center = new BlockPos(boundingBox.getCenter());
        this.dungeonUUID = tag.getUniqueId("dungeonUUID");
    }

    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    public BlockPos getCenter() {
        return center;
    }

    public boolean isBlockInRegion(BlockPos blockPos) {
        return boundingBox.intersects(blockPos.getX(),blockPos.getY(),blockPos.getZ(),blockPos.getX()+1,blockPos.getY()+1,blockPos.getZ()+1);
    }

    public boolean isEntityInRegion(EntityLivingBase e) {
        return boundingBox.intersects(e.getEntityBoundingBox());
    }

    public BlockPos getMax() {
        return max;
    }

    public BlockPos getMin() {
        return min;
    }

    public boolean isEnabled() {
        return enabled;
    }


    //Checks if block is in area and is breakable
    public void checkBreakEvent(BlockEvent.BreakEvent e) {
        if(enabled && !e.getPlayer().isCreative()) {
            if(isBlockInRegion(e.getPos())) {
                e.setCanceled(true);
            }
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public NBTTagCompound save() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setDouble("min.x",min.getX());
        tag.setDouble("min.y",min.getY());
        tag.setDouble("min.z",min.getZ());

        tag.setDouble("max.x",max.getX());
        tag.setDouble("max.y",max.getY());
        tag.setDouble("max.z",max.getZ());

        tag.setBoolean("enabled",enabled);

        tag.setUniqueId("dungeonUUID",dungeonUUID);
        return tag;
    }

    public void checkSpawnEvent(LivingSpawnEvent.CheckSpawn e) {
        if(enabled) {
            if(isEntityInRegion(e.getEntityLiving())) {
                e.setResult(Event.Result.DENY);
            }
        }
    }

	public void checkPortalEvent(PortalSpawnEvent e) {
		if(enabled) {
			if(isBlockInRegion(e.getPos())) {
				e.setResult(Event.Result.DENY);
			}
		}
	}
	
	public List<Chunk> getChunks(World world) {
		List<Chunk> chunks = new ArrayList<Chunk>();
		
		BlockPos p1 = new BlockPos(this.min.getX(), 100, this.min.getZ());
		BlockPos p2 = new BlockPos(this.max.getX(), 100, this.max.getZ());
		
		for(BlockPos blockpos : BlockPos.getAllInBox(p1, p2)) {
			Chunk chunk = world.getChunkFromBlockCoords(blockpos);
			if(chunks.isEmpty() || !chunks.contains(chunk)) {
				chunks.add(chunk);
			}
		}
		
		return chunks;
	}

    public UUID getDungeonUUID() {
        return dungeonUUID;
    }
}
