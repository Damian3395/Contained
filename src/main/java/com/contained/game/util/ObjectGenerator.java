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

		/*===========================
		 * passive mobs
		 * ==========================
		 */
		if (target.toLowerCase().equals("bat")) {
			entityTarget = new EntityBat(world);
			isEntity=true;
		}
		else if (target.toLowerCase().equals("chicken")) {
			entityTarget = new EntityChicken(world);
			isEntity=true;
		}
		else if (target.toLowerCase().equals("cow")) {
			entityTarget = new EntityCow(world);
			isEntity=true;
		}
		else if (target.toLowerCase().equals("mooshroom")) {
			entityTarget = new EntityMooshroom(world);
			isEntity=true;
		}
		else if (target.toLowerCase().equals("pig")) {
			entityTarget = new EntityPig(world);
			isEntity=true;
		}
		else if (target.toLowerCase().equals("sheep")) {
			entityTarget = new EntitySheep(world);
			isEntity=true;
		}
		else if (target.toLowerCase().equals("squid")) {
			entityTarget = new EntitySquid(world);
			isEntity=true;
		}
		else if (target.toLowerCase().equals("villager")) {
			entityTarget = new EntityVillager(world);
			isEntity=true;
		}



		/*===========================
		 * neutral mobs
		 * ==========================
		 */
		else if (target.toLowerCase().equals("cavespider")) {
			entityTarget = new EntityCaveSpider(world);
			isEntity=true;
		}
		else if (target.toLowerCase().equals("enderman")) {
			entityTarget = new EntityEnderman(world);
			isEntity=true;
		}
		else if (target.toLowerCase().equals("spider")) {
			entityTarget = new EntitySpider(world);
			isEntity=true;
		}
		else if (target.toLowerCase().equals("zombiepigman")) {
			entityTarget = new EntityPigZombie(world);
			isEntity=true;
		}
		else if (target.toLowerCase().equals("pigzombie")) {
			entityTarget = new EntityPigZombie(world);
			isEntity=true;
		}



		/*===========================
		 * hostile mobs
		 * ==========================
		 */
		else if (target.toLowerCase().equals("blaze")) {
			entityTarget = new EntityBlaze(world);
			isEntity=true;
		}
		else if (target.toLowerCase().equals("creeper")) {
			entityTarget = new EntityCreeper(world);
			isEntity=true;
		}
		else if (target.toLowerCase().equals("ghast")) {
			entityTarget = new EntityGhast(world);
			isEntity=true;
		}
		else if (target.toLowerCase().equals("skeleton")) {
			entityTarget = new EntitySkeleton(world);
			isEntity=true;
		}
		else if (target.toLowerCase().equals("slime")) {
			entityTarget = new EntitySlime(world);
			isEntity=true;
		}
		else if (target.toLowerCase().equals("witch")) {
			entityTarget = new EntityWitch(world);
			isEntity=true;
		}
		else if (target.toLowerCase().equals("zombie")) {
			entityTarget = new EntityZombie(world);
			isEntity=true;
		}




		/*===========================
		 * tameable mobs
		 * ==========================
		 */
		else if (target.toLowerCase().equals("horse")) {
			entityTarget = new EntityHorse(world);
			isEntity=true;
		}
		else if (target.toLowerCase().equals("ocelot")) {
			entityTarget = new EntityOcelot(world);
			isEntity=true;
		}
		else if (target.toLowerCase().equals("wolf")) {
			entityTarget = new EntityWolf(world);
			isEntity=true;
		}


		/*===========================
		 * other mobs
		 * ==========================
		 */

		else if (target.toLowerCase().equals("irongolem")) {
			entityTarget = new EntityIronGolem(world);
			isEntity=true;
		}

		else if (target.toLowerCase().equals("snowman")) {
			entityTarget = new EntitySnowman(world);
			isEntity=true;
		}



		/*===========================
		 * boss
		 * ==========================
		 */
		else if (target.toLowerCase().equals("dragon")) {
			entityTarget = new EntityDragon(world);
			isEntity=true;
		}
		else if (target.toLowerCase().equals("wither")) {
			entityTarget = new EntityWither(world);
			isEntity=true;
		}




		/*===========================
		 * blocks
		 * ==========================
		 */


		else if (target.toLowerCase().equals("stone")) {
			blockTarget = Blocks.stone;
			isBlock=true;
		}
		else if (target.toLowerCase().equals("grass")) {
			blockTarget = Blocks.grass;
			isBlock=true;
		}
		else if (target.toLowerCase().equals("dirt")) {
			blockTarget = Blocks.dirt;
			isBlock=true;
		}
		else if (target.toLowerCase().equals("water")) {
			blockTarget = Blocks.water;
			isBlock=true;
		}
		else if (target.toLowerCase().equals("flowingwater")) {
			blockTarget = Blocks.flowing_water;
			isBlock=true;
		}
		else if (target.toLowerCase().equals("lava")) {
			blockTarget = Blocks.lava;
			isBlock=true;
		}
		else if (target.toLowerCase().equals("sand")) {
			blockTarget = Blocks.sand;
			isBlock=true;
		}
		else if (target.toLowerCase().equals("gravel")) {
			blockTarget = Blocks.gravel;
			isBlock=true;
		}
		else if (target.toLowerCase().equals("log")) {
			blockTarget = Blocks.log;
			isBlock=true;
		}
		else if (target.toLowerCase().equals("ice")) {
			blockTarget = Blocks.ice;
			isBlock=true;
		}
		else if (target.toLowerCase().equals("cactus")) {
			blockTarget = Blocks.cactus;
			isBlock=true;
		}
		else if (target.toLowerCase().equals("goldore")) {
			blockTarget = Blocks.gold_ore;
			isBlock=true;
		}
		else if (target.toLowerCase().equals("ironore")) {
			blockTarget = Blocks.iron_ore;
			isBlock=true;
		}
		else {
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
