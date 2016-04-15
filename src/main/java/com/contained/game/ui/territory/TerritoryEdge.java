package com.contained.game.ui.territory;

public class TerritoryEdge {

	public static final int NORTH = 0;
	public static final int SOUTH = 1;
	public static final int WEST  = 2;
	public static final int EAST  = 3;
	
	public int direction;
	public int blockX;
	public int blockZ;
	
	public TerritoryEdge(int direction, int x, int z) {
		this.direction = direction;
		this.blockX = x;
		this.blockZ = z;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof TerritoryEdge) {
			TerritoryEdge te = (TerritoryEdge)o;
			if (te.blockX == this.blockX && te.blockZ == this.blockZ && te.direction == this.direction)
				return true;
		}
		return false;
	}
	
	@Override 
	public int hashCode() {
		return 0;
	}	
}
