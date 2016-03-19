package com.contained.game.ui;

import java.awt.Point;

import com.contained.game.Contained;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.Util;
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
			Point check = new Point(x, z);
			String blockTeam = Contained.territoryData.get(check);

			if (blockTeam == null) {
				Util.displayError(p, "This block must be within a team's territory to use.");
			} else {
				PlayerTeamIndividual playerData = PlayerTeamIndividual.get(p);
				return new GuiTownManage(p.inventory, (TownManageTE)te, blockTeam, playerData.teamID);
			}
		}
		return null;
	}

}
