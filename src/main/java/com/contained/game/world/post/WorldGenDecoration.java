package com.contained.game.world.post;

import java.util.Random;

import com.contained.game.util.Util;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenDecoration extends WorldGenerator{
	private Block block;

    public WorldGenDecoration(Block b) {
        this.block = b;
    }

    public boolean generate(World w, Random rand, int x, int y, int z){
        Block b;

        do {
            b = w.getBlock(x, y, z);
            if (!(b.isLeaves(w, x, y, z) || b.isAir(w, x, y, z)))
                break;
            --y;
        } while (y > 0);

        for (int i=0; i<4; i++){
            int x1 = x + rand.nextInt(8) - rand.nextInt(8);
            int y1 = y + rand.nextInt(4) - rand.nextInt(4);
            int z1 = z + rand.nextInt(8) - rand.nextInt(8);

            if (w.isAirBlock(x1, y1, z1) && 
            		Util.isSolidBlock(w.getBlock(x1, y1-1, z1))) {
                w.setBlock(x1, y1, z1, this.block, 0, 2);
            }
        }

        return true;
    }
}
