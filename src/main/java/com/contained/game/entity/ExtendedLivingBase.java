package com.contained.game.entity;

import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.EntityUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Extended properties for living entities, but not including players.
 */
public class ExtendedLivingBase implements IExtendedEntityProperties {

	private final static String EXT_PROP_NAME = "ExtendedLivingBase";
	private final EntityLivingBase entity;
	private String teamID = null;
	public boolean forceFireImmunity = false;
	
	public ExtendedLivingBase(EntityLivingBase entity) {
		this.entity = entity;
	}
	
	public static final void register(EntityLivingBase entity) {
		entity.registerExtendedProperties(ExtendedLivingBase.EXT_PROP_NAME, new ExtendedLivingBase(entity));
	}
	
	public static final ExtendedLivingBase get(EntityLivingBase entity) {
		return (ExtendedLivingBase)entity.getExtendedProperties(EXT_PROP_NAME);
	}
	
	public void setTeam(String teamID) {
		this.teamID = teamID;
		if (this.teamID != null && !(entity instanceof EntityPlayer) && entity instanceof EntityCreature) {
			EntityCreature creature = (EntityCreature)entity;
			EntityUtil.applyTeamAI(creature);
			forceFireImmunity = true;
			creature.func_110163_bv(); //Set persistent
		}
	}
	
	public void setTeamFromUsername(String playerName) {
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(playerName);
		if (pdata != null && pdata.teamID != null)
			setTeam(pdata.teamID);
		else
			setTeam(null);
	}
	
	public String getTeam() {
		return this.teamID;
	}
	
	@Override
	public void init(Entity arg0, World arg1) {	
	}

	@Override
	public void loadNBTData(NBTTagCompound ntc) {
		if (ntc.hasKey("extTeam"))
			setTeam(ntc.getString("extTeam"));
		else
			setTeam(null);
	}

	@Override
	public void saveNBTData(NBTTagCompound ntc) {
		if (this.teamID != null)
			ntc.setString("extTeam", this.teamID);
		else if (ntc.hasKey("extTeam"))
			ntc.removeTag("extTeam");
	}

}
