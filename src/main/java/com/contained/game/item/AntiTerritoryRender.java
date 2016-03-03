package com.contained.game.item;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.contained.game.data.Data;
import com.contained.game.user.PlayerTeam;

/**
 * Render a square on the anti-territory item icons that shows the color
 * of the team they are linked to.
 */
public class AntiTerritoryRender implements IItemRenderer {
		private RenderItem render = new RenderItem();
		
		@Override
		public boolean handleRenderType(ItemStack itemStack, ItemRenderType type) {
			return type == ItemRenderType.INVENTORY;
		}

		@Override
		public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
			return false;
		}

		@Override
		public void renderItem(ItemRenderType type, ItemStack itemStack, Object... data) {
			int x = 9;
			int y = 9;
			int w = 6;
			int h = 6;
			
			GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
			
			IIcon icon = itemStack.getIconIndex();
			render.renderIcon(0, 0, icon, 16, 16);
					
			GL11.glDisable(GL11.GL_TEXTURE_2D);			
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawing(GL11.GL_QUADS);
			tessellator.setColorRGBA(0, 0, 0, 255);
			tessellator.addVertex(x,   y,   0);
			tessellator.addVertex(x,   y+h, 0);
			tessellator.addVertex(x+w, y+h, 0);
			tessellator.addVertex(x+w, y,   0);
			tessellator.draw();
			
			int teamColor;
			NBTTagCompound itemData = Data.getTagCompound(itemStack);	
			String teamToRemove = itemData.getString("teamOwner");
			if (teamToRemove == null || teamToRemove.equals(""))
				teamColor = 0xFFFFFF;
			else {
				PlayerTeam team = PlayerTeam.get(teamToRemove);
				if (team == null)
					teamColor = 0xFFFFFF;
				else
					teamColor = team.getColor();
			}
			
			tessellator.startDrawing(GL11.GL_QUADS);
			tessellator.setColorRGBA_I(teamColor, 255);
			tessellator.addVertex(x+1,   y+1,   0);
			tessellator.addVertex(x+1,   y+h-1, 0);
			tessellator.addVertex(x+w-1, y+h-1, 0);
			tessellator.addVertex(x+w-1, y+1,   0);
			tessellator.draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_LIGHTING);	
		}
}
