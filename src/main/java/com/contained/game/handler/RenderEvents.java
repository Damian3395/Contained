package com.contained.game.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;

import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.RenderUtil;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RenderEvents {
	
	public RenderEvents(){
	}
	
	@SubscribeEvent
	public void onEntityRender(RenderLivingEvent.Pre event) {
		// Players in teams will have their team name rendered above
		// their head in-game.
		if (event.entity instanceof EntityPlayer) {
			EntityPlayer p = (EntityPlayer)event.entity;
			
			PlayerTeamIndividual pdata = PlayerTeamIndividual.get(p);
			if (pdata != null && pdata.teamID != null) {
				PlayerTeam team = PlayerTeam.get(pdata.teamID, p.dimension);
				if(team != null)
					RenderUtil.drawNameTag(p, team.getFormatCode()+"§l["+team.displayName+"]", event.x, event.y+0.5, event.z, 64);
			}
			
			//ExtendedPlayer properties = ExtendedPlayer.get(p);
			//if(properties.inGame() && properties.gameMode != Resources.FREE_PLAY){
			//	mc.getTextureManager()
			//	mc.renderEngine.func_110579_a(new ResourceLocation("skins/" + thePlayer.username), (TextureObject) new ThreadDownloadImageData(fullUrl, null, null);
			//}
		}
	}
	
}
