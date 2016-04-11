package com.contained.game.ui;

import java.awt.Point;

import com.contained.game.Contained;
import com.contained.game.ui.games.RevivePlayer;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.Util;
import com.contained.game.world.block.ContainerTownHall;
import com.contained.game.world.block.TownManageTE;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	public static final int GUI_SURVEY_ID = 1;
	public static final int GUI_REVIVE_PLAYER = 2;
	
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
		if (id == 1)
			return new GuiSurvey(PlayerTeamIndividual.get(p));
		switch(id){
		case GUI_SURVEY_ID: return new GuiSurvey(PlayerTeamIndividual.get(p));
		case GUI_REVIVE_PLAYER: return new RevivePlayer();
		}
		
		TileEntity te = w.getTileEntity(x, y, z);
		if (te != null && te instanceof TownManageTE) {
			Point check = new Point(x, z);
			String blockTeam = Contained.getTerritoryMap(0).get(check);

			if (blockTeam == null)
				Util.displayError(p, "This block must be within a team's territory to use.");
			else {
				PlayerTeamIndividual playerData = PlayerTeamIndividual.get(p);
				return new GuiTownManage(p.inventory, (TownManageTE)te, blockTeam, playerData.teamID);
			}
		}
		return null;
	}

}
