package com.teamcqr.chocolatequestrepoured.objects.entity.mobs;

import com.teamcqr.chocolatequestrepoured.factions.EDefaultFaction;
import com.teamcqr.chocolatequestrepoured.objects.entity.EBaseHealths;
import com.teamcqr.chocolatequestrepoured.objects.entity.ECQREntityArmPoses;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.EntityAITorchIgniter;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.spells.IEntityAISpellAnimatedVanilla;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;
import com.teamcqr.chocolatequestrepoured.objects.entity.boss.AbstractEntityCQRMageBase;
import com.teamcqr.chocolatequestrepoured.util.IRangedWeapon;

import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.AbstractIllager.IllagerArmPose;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCQRIllager extends AbstractEntityCQR {

	private static final DataParameter<Boolean> IS_AGGRESSIVE = EntityDataManager.<Boolean>createKey(AbstractEntityCQRMageBase.class, DataSerializers.BOOLEAN);

	public EntityCQRIllager(World worldIn) {
		super(worldIn);
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		this.dataManager.register(IS_AGGRESSIVE, false);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		this.tasks.addTask(22, new EntityAITorchIgniter(this));
	}

	@Override
	public void onEntityUpdate() {
		if (!this.world.isRemote) {
			if (this.getAttackTarget() != null && !this.dataManager.get(IS_AGGRESSIVE)) {
				this.dataManager.set(IS_AGGRESSIVE, true);
				this.setArmPose(ECQREntityArmPoses.HOLDING_ITEM);
			} else if (this.getAttackTarget() == null) {
				this.dataManager.set(IS_AGGRESSIVE, false);
				this.setArmPose(ECQREntityArmPoses.NONE);
			}
		}
		super.onEntityUpdate();
	}

	@Override
	public float getBaseHealth() {
		return EBaseHealths.ILLAGER.getValue();
	}

	@Override
	public EDefaultFaction getDefaultFaction() {
		return EDefaultFaction.ILLAGERS;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableList.ENTITIES_VINDICATION_ILLAGER;
	}

	@Override
	public int getTextureCount() {
		return 2;
	}

	public boolean isAggressive() {
		if (!this.world.isRemote) {
			return this.getAttackTarget() != null;
		}
		return this.dataManager.get(IS_AGGRESSIVE);
	}

	@SideOnly(Side.CLIENT)
	public IllagerArmPose getIllagerArmPose() {
		if (this.isAggressive()) {
			if (this.isSpellCharging() && this.isSpellAnimated()) {
				return IllagerArmPose.SPELLCASTING;
			}

			Item active = this.getActiveItemStack().getItem();
			if (active instanceof IRangedWeapon || active instanceof ItemBow) {
				return IllagerArmPose.BOW_AND_ARROW;
			}
			return IllagerArmPose.ATTACKING;
		}
		return IllagerArmPose.CROSSED;
	}

	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.ILLAGER;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.VINDICATION_ILLAGER_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_VINDICATION_ILLAGER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ILLAGER_DEATH;
	}

}
