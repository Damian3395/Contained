package com.contained.game.minigames;

import java.awt.Color;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

public final class TreasureChestGenerator {
	
	private final int ITEM_COOKED_BEEF=0;
	private final int ITEM_ROTTEN_MEAT=1;
	private final int ITEM_BREAD=2;
	private final int ITEM_IRON_SWORD=3;
	private final int ITEM_DIAMOND_SWORD=4;
	private final int ITEM_BOW=5;
	private final int ITEM_ARROW=6;
//	private final int ITEM_TORCH=7;
	private final int ITEM_IRON_HELMET=7;
	
	private final int ITEM_TYPES=8;		//how many items are defined above
	
	
	private final int ITEM_COOKED_BEEF_LIMIT=2;
	private final int ITEM_ROTTEN_MEAT_LIMIT=3;
	private final int ITEM_BREAD_LIMIT=3;
	private final int ITEM_IRON_SWORD_LIMIT=2;
	private final int ITEM_DIAMOND_SWORD_LIMIT=2;
	private final int ITEM_BOW_LIMIT=2;
	private final int ITEM_ARROW_LIMIT=5;
//	private final int ITEM_TORCH_LIMIT=3;
	private final int ITEM_IRON_HELMET_LIMIT=2;
	
//	private final int ITEM_BEEF=0;
//	private final int ITEM_BEEF=0;
//	private final int ITEM_BEEF=0;
	
	private final int ITEM_LIMIT=8;		// maximum types of items in a chest, you may change it to any number smaller than chest size(64?) 
	private final int MAP_RANGE=20;
	
	private boolean isGameOver=false;
	World world;
	
	public TreasureChestGenerator(World world){
		this.world=world;
	}
	
	public void generateChest(int chestAmount){
		int x,y,z;
		int contentAmount;
		
		Random r = new Random();
		for(int i=0;i<chestAmount;i++){	//generate treasure chests, i controls the #i chest
			System.out.println("generating "+chestAmount+" chests.");
			//2337,4,-245
			x=r.nextInt(this.MAP_RANGE)+2337;
			z=r.nextInt(this.MAP_RANGE)-245;			
			y=this.world.getTopSolidOrLiquidBlock(x, z);		//coordinates to generate chests
			
			this.world.setBlock(x, y, z, Blocks.chest);
//			this.drawChestSignal(x, y, z);
			TileEntityChest chest = (TileEntityChest)this.world.getTileEntity(x, y, z);	//generate a chest
			
			contentAmount=r.nextInt(this.ITEM_LIMIT);		//how many items in this chest
			System.out.println("Chest#"+i+" is generated at ("+x+","+y+","+z+")");
			System.out.println("The content amount in chest#"+i+" is:"+contentAmount);
			for(int j=0;j<contentAmount;j++){		//generate items in #i chest, j controls the #j item
				int itemType = r.nextInt(this.ITEM_TYPES);	// for each content, randomize its item type
				System.out.println("type of item #"+j+" is "+itemType);
				int itemAmount=0;
				ItemStack itemStack=null;
				switch (itemType){
				case ITEM_COOKED_BEEF:
					itemAmount=r.nextInt(this.ITEM_COOKED_BEEF_LIMIT);	// for each item, randomize its amount
					itemStack=new ItemStack(Items.cooked_beef,itemAmount);
				break;
				
				case ITEM_ROTTEN_MEAT:
					itemAmount=r.nextInt(this.ITEM_ROTTEN_MEAT_LIMIT);	// for each item, randomize its amount
					itemStack=new ItemStack(Items.rotten_flesh,itemAmount);
				break;
				
				case ITEM_BREAD:
					itemAmount=r.nextInt(this.ITEM_BREAD_LIMIT);	// for each item, randomize its amount
					itemStack=new ItemStack(Items.bread,itemAmount);
				break;
				
				case ITEM_IRON_SWORD:
					itemAmount=r.nextInt(this.ITEM_IRON_SWORD_LIMIT);	// for each item, randomize its amount
					itemStack=new ItemStack(Items.iron_sword,itemAmount);
				break;
				
				case ITEM_DIAMOND_SWORD:
					itemAmount=r.nextInt(this.ITEM_DIAMOND_SWORD_LIMIT);	// for each item, randomize its amount
					itemStack=new ItemStack(Items.diamond_sword,itemAmount);
				break;
				
				case ITEM_ARROW:
					itemAmount=r.nextInt(this.ITEM_ARROW_LIMIT);	// for each item, randomize its amount
					itemStack=new ItemStack(Items.arrow,itemAmount);
				break;
				
				case ITEM_BOW:
					itemAmount=r.nextInt(this.ITEM_BOW_LIMIT);	// for each item, randomize its amount
					itemStack=new ItemStack(Items.bow,itemAmount);
				break;
				
//				case ITEM_TORCH:
//					itemAmount=r.nextInt(this.ITEM_TORCH_LIMIT);	// for each item, randomize its amount
//					itemStack=new ItemStack(Items.,itemAmount);
				
				case ITEM_IRON_HELMET:
					itemAmount=r.nextInt(this.ITEM_IRON_HELMET_LIMIT);	// for each item, randomize its amount
					itemStack=new ItemStack(Items.iron_helmet,itemAmount);
				break;
				
				default:
					System.out.println("Error in generating items. Please check this.ITEM_TYPES");
					break;
				}
				
				if(itemStack != null && itemAmount > 0){
					System.out.println("number of item #"+j+" is "+itemAmount);
					chest.setInventorySlotContents(j, itemStack);	//place the item #j to slot #j
				}
			}
			
		}
		System.out.println("generate finished.");
		
		
	}
	
	
	
	private void drawChestSignal(float x, float y, float z) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
		Tessellator tes = Tessellator.instance;
		
		tes.startDrawing(GL11.GL_TRIANGLE_STRIP);
//		float alpha = 1f-(Util.euclidDist(x, z, x, z)/128.0f);
		tes.setColorRGBA_I(Color.green.hashCode(), (int)(48f));
		tes.addVertex(x, y, z);
		tes.draw();
		
		
//		for (TerritoryEdge te : teamEdges.keySet()) {
//			PlayerTeam t = PlayerTeam.get(teamEdges.get(te));
//			
//			if(t != null){
//				float margin = 0.01f;
//				float alpha = 1f-(Util.euclidDist(te.blockX, te.blockZ, x, z)/128.0f);
//				if (alpha <= 0)
//					continue;
//			
//				float x1 = te.blockX-x+margin;
//				float x2 = x1+1.0f-margin*2;
//				float y1 = 0-y;
//				float y2 = 255-y;
//				float z1 = te.blockZ-z+margin;
//				float z2 = z1+1.0f-margin*2;
//			
//				tes.startDrawing(GL11.GL_TRIANGLE_STRIP);
//				tes.setColorRGBA_I(t.getColor(), (int)(48f * alpha));
//
//				if (te.direction == TerritoryEdge.NORTH) {
//					tes.addVertex(x2, y2, z2); tes.addVertex(x1, y2, z2);
//					tes.addVertex(x2, y1, z2); tes.addVertex(x1, y1, z2);
//				}
//				else if (te.direction == TerritoryEdge.SOUTH) {
//					tes.addVertex(x2, y2, z1); tes.addVertex(x1, y2, z1);
//					tes.addVertex(x2, y1, z1); tes.addVertex(x1, y1, z1);
//				}
//				else if (te.direction == TerritoryEdge.WEST) {
//					tes.addVertex(x1, y2, z2); tes.addVertex(x1, y2, z1);
//					tes.addVertex(x1, y1, z2); tes.addVertex(x1, y1, z1);
//				}
//				else if (te.direction == TerritoryEdge.EAST) {
//					tes.addVertex(x2, y2, z2); tes.addVertex(x2, y2, z1);
//					tes.addVertex(x2, y1, z2); tes.addVertex(x2, y1, z1);
//				}
//
//				tes.draw();
//			}
//		}
		
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
}
