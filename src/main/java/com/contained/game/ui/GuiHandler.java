package com.contained.game.ui;

import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.world.block.ContainerTownHall;
import com.contained.game.world.block.TownManageTE;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int id, EntityPlayer p, World w, int x, int y, int z) {
		TileEntity te = w.getTileEntity(x, y, z);
		if (te instanceof TownManageTE) {
			return new ContainerTownHall(p.inventory, (TownManageTE)te);
		}
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int id, EntityPlayer p, World w, int x, int y, int z) {
		TileEntity te = w.getTileEntity(x, y, z);
		if (te instanceof TownManageTE) {
			PlayerTeamIndividual playerData = PlayerTeamIndividual.get(p);
			return new GuiTownManage(p.inventory, (TownManageTE)te, playerData.teamID);
		}
		return null;
	}

}
