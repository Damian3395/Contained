package com.contained.game.util;


import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ObjectGenerator {
	public boolean generate(String target, World world, int x, int y, int z){
		boolean isEntity=false;
		boolean isBlock=false;
		Entity entityTarget = null;
		Block blockTarget=null;
		Random r=new Random();
		
		switch (target.toLowerCase()) {
		
		/*===========================
		 * passive mobs
		 * ==========================
		 */
		case "bat":
			entityTarget = new EntityBat(world);
			isEntity=true;
			break;
		case "chicken":
			entityTarget = new EntityChicken(world);
			isEntity=true;
			break;
		case "cow":
			entityTarget = new EntityCow(world);
			isEntity=true;
			break;
		case "mooshroom":
			entityTarget = new EntityMooshroom(world);
			isEntity=true;
			break;
		case "pig":
			entityTarget = new EntityPig(world);
			isEntity=true;
			break;
		case "sheep":
			entityTarget = new EntitySheep(world);
			isEntity=true;
			break;
		case "squid":
			entityTarget = new EntitySquid(world);
			isEntity=true;
			break;
		case "villager":
			entityTarget = new EntityVillager(world);
			isEntity=true;
			break;


			
			/*===========================
			 * neutral mobs
			 * ==========================
			 */
		case "cavespider":
			entityTarget = new EntityCaveSpider(world);
			isEntity=true;
			break;
		case "enderman":
			entityTarget = new EntityEnderman(world);
			isEntity=true;
			break;
		case "spider":
			entityTarget = new EntitySpider(world);
			isEntity=true;
			break;
		case "zombiepigman":
			entityTarget = new EntityPigZombie(world);
			isEntity=true;
			break;
		case "pigzombie":
			entityTarget = new EntityPigZombie(world);
			isEntity=true;
			break;
			
			
			
			/*===========================
			 * hostile mobs
			 * ==========================
			 */
		case "blaze":
			entityTarget = new EntityBlaze(world);
			isEntity=true;
			break;
		case "creeper":
			entityTarget = new EntityCreeper(world);
			isEntity=true;
			break;
		case "ghast":
			entityTarget = new EntityGhast(world);
			isEntity=true;
			break;
		case "skeleton":
			entityTarget = new EntitySkeleton(world);
			isEntity=true;
			break;
		case "slime":
			entityTarget = new EntitySlime(world);
			isEntity=true;
			break;
		case "witch":
			entityTarget = new EntityWitch(world);
			isEntity=true;
			break;
		case "zombie":
			entityTarget = new EntityZombie(world);
			isEntity=true;
			break;
		
			
			
			
			/*===========================
			 * tameable mobs
			 * ==========================
			 */
		case "horse":
			entityTarget = new EntityHorse(world);
			isEntity=true;
			break;
		case "ocelot":
			entityTarget = new EntityOcelot(world);
			isEntity=true;
			break;
		case "wolf":
			entityTarget = new EntityWolf(world);
			isEntity=true;
			break;
			

			/*===========================
			 * other mobs
			 * ==========================
			 */
			
		case "irongolem":
			entityTarget = new EntityIronGolem(world);
			isEntity=true;
			break;
				
		case "snowman":
			entityTarget = new EntitySnowman(world);
			isEntity=true;
			break;
			
			
		
			/*===========================
			 * boss
			 * ==========================
			 */
		case "dragon":
			entityTarget = new EntityDragon(world);
			isEntity=true;
			break;
		case "wither":
			entityTarget = new EntityWither(world);
			isEntity=true;
			break;
			
			
			
			
			/*===========================
			 * blocks
			 * ==========================
			 */
		
		
		case "stone":
			blockTarget = Blocks.stone;
			isBlock=true;
			break;
		case "grass":
			blockTarget = Blocks.grass;
			isBlock=true;
			break;
		case "dirt":
			blockTarget = Blocks.dirt;
			isBlock=true;
			break;
		case "water":
			blockTarget = Blocks.water;
			isBlock=true;
			break;
		case "flowingwater":
			blockTarget = Blocks.flowing_water;
			isBlock=true;
			break;
		case "lava":
			blockTarget = Blocks.lava;
			isBlock=true;
			break;
		case "sand":
			blockTarget = Blocks.sand;
			isBlock=true;
			break;
		case "gravel":
			blockTarget = Blocks.gravel;
			isBlock=true;
			break;
		case "log":
			blockTarget = Blocks.log;
			isBlock=true;
			break;
		case "ice":
			blockTarget = Blocks.ice;
			isBlock=true;
			break;
		case "cactus":
			blockTarget = Blocks.cactus;
			isBlock=true;
			break;
		case "goldore":
			blockTarget = Blocks.gold_ore;
			isBlock=true;
			break;
		case "ironore":
			blockTarget = Blocks.iron_ore;
			isBlock=true;
			break;
		default:
			isEntity=false;
			isBlock=false;
			return false;
		}
		if(isEntity){
			entityTarget.setLocationAndAngles(x + r.nextInt(3),
					y, z + r.nextInt(3), 0.0F, 0.0F);
			if (world.spawnEntityInWorld(entityTarget)) {
				return true;
			}else{
				isEntity=false;
				isBlock=false;
				return false;
			}
		}else if(isBlock){
			if (world.setBlock(x + r.nextInt(3),
					y, z + r.nextInt(3), blockTarget)) {
				return true;
			}else{
				isEntity=false;
				isBlock=false;
				return false;
			}	
		}else{
			isEntity=false;
			isBlock=false;
			return false;
		}
		
	}
}
