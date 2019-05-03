package com.teamcqr.chocolatequestrepoured.dungeongen.protection;

import com.teamcqr.chocolatequestrepoured.API.events.CQDungeonStructureGenerateEvent;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Copyright (c) 30.04.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public class ProtectionEventHandler {

    @SubscribeEvent
    public void dungeonGenerate(CQDungeonStructureGenerateEvent e) {
        if(e.getDungeon().isProtectedFromModifications()) {
        	ProtectionHandler.PROTECTION_HANDLER.addRegion(new ProtectedRegion(e.getSize().getX(),e.getSize().getY(),e.getSize().getZ(),e.getPos()));
        }
    }

    @SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent e) {
        ProtectionHandler.PROTECTION_HANDLER.check(e);
    }

    @SubscribeEvent
    public void unload(ChunkEvent.Unload e) {
        if(!e.getWorld().isRemote) {
            ProtectionHandler.PROTECTION_HANDLER.checkUnload(e);
        }
    }

    @SubscribeEvent
    public void save(ChunkDataEvent.Save e) {
        if(!e.getWorld().isRemote) {
            ProtectionHandler.PROTECTION_HANDLER.save(e);
        }
    }

    @SubscribeEvent
    public void load(ChunkDataEvent.Load e) {
        if(!e.getWorld().isRemote) {
            ProtectionHandler.PROTECTION_HANDLER.load(e);
        }
    }

    @SubscribeEvent
    public void livingSpawn(LivingSpawnEvent.CheckSpawn e) {
        ProtectionHandler.PROTECTION_HANDLER.checkSpawn(e);
    }
}