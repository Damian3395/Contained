package com.contained.game.ui;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;

import com.contained.game.data.Data;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.util.RenderUtil;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class DataVisualization extends Gui{
	private Minecraft mc;
	
	public boolean guiRender = false;
	private int chartDataPoints = 100;
	private long nextChartUpdate;
	private boolean initialized = false;
	private HashMap<Integer, LinkedList<Double>> chartData;
	
	public DataVisualization(Minecraft mc) {
		super();
		this.mc = mc;
		nextChartUpdate = System.currentTimeMillis();
		chartData = new HashMap<Integer, LinkedList<Double>>();
		for(int i=0; i<Data.occupationNames.length;i++)
			chartData.put(i, new LinkedList<Double>());
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderLevel(RenderGameOverlayEvent event) {
		if (event.isCancelable() || event.type != ElementType.EXPERIENCE)
			return;
		
		EntityClientPlayerMP p = mc.thePlayer;
		int[] occupationValues = ExtendedPlayer.get(p).getOccupationValues();
		
		if (guiRender) {
			displayMode2D();
			
			//Draw occupation pie chart
			int x = 20;
			int y = 20;
			int diameter = 100;
			drawPieChart(x, y, diameter, occupationValues, Data.occupationNames);
			
			//Draw item usage statistics			
			drawText("Own Items Used: "+ExtendedPlayer.get(p).usedOwnItems, 
					x, y+diameter+20, 0, Color.getHSBColor(0.0f, 0.0f, 0.85f).hashCode());
			drawText("Other's Items Used: "+ExtendedPlayer.get(p).usedOthersItems, 
					x, y+diameter+20, 1, Color.getHSBColor(0.0f, 0.0f, 0.85f).hashCode());
			drawText("Items Used by Others: "+ExtendedPlayer.get(p).usedByOthers, 
					x, y+diameter+20, 2, Color.getHSBColor(0.0f, 0.0f, 0.85f).hashCode());
			
			//Relative occupation value over time
			drawLineGraph(x, y+diameter+50, mc.displayWidth-x*2-20, (int)(diameter*1.5), chartDataPoints, 3,
					Data.convertBaseline(chartData.get(Data.MINING).toArray(new Double[chartData.get(Data.MINING).size()])),
					Data.convertBaseline(chartData.get(Data.COOKING).toArray(new Double[chartData.get(Data.COOKING).size()])),
					Data.convertBaseline(chartData.get(Data.FARMING).toArray(new Double[chartData.get(Data.FARMING).size()])),
					Data.convertBaseline(chartData.get(Data.FISHING).toArray(new Double[chartData.get(Data.FISHING).size()])),
					Data.convertBaseline(chartData.get(Data.LUMBER).toArray(new Double[chartData.get(Data.LUMBER).size()])),
					Data.convertBaseline(chartData.get(Data.FIGHTER).toArray(new Double[chartData.get(Data.FIGHTER).size()])),
					Data.convertBaseline(chartData.get(Data.POTION).toArray(new Double[chartData.get(Data.POTION).size()])),
					Data.convertBaseline(chartData.get(Data.BUILDING).toArray(new Double[chartData.get(Data.BUILDING).size()])),
					Data.convertBaseline(chartData.get(Data.MACHINE).toArray(new Double[chartData.get(Data.MACHINE).size()])),
					Data.convertBaseline(chartData.get(Data.TRANSPORT).toArray(new Double[chartData.get(Data.TRANSPORT).size()])));
			
			resetDisplay();
		}
		
		//Update chart with a new data point once every 10 seconds
		if (System.currentTimeMillis() >= nextChartUpdate) {
			nextChartUpdate = System.currentTimeMillis()+(10*1000);
			for(int i=0; i<occupationValues.length; i++) {
				LinkedList<Double> dataPoints = chartData.get(i);
				dataPoints.addLast((double)occupationValues[i]);
				if (dataPoints.size() > chartDataPoints)
					dataPoints.removeFirst();
				if (!initialized)
					dataPoints.removeFirst();
			}
			if (!initialized)
				initialized = true;
		}
	}
	
	/**
	 * Draws a pie chart to the screen, with an associated graph key.
	 * 
	 * @param x 			Top-left position on screen to draw the chart (in pixels) 
	 * @param y				Top-left position on screen to draw the chart (in pixels)
	 * @param diameter 		Relative size of the pie chart (in pixels)
	 * @param values 		Array of values to visualize.
	 * @param captions 		Parallel array of "names" for those values (displayed in the key)
	 */
	private void drawPieChart(int x, int y, int diameter, int[] values, String[] captions) {
		double[] copyVals = new double[values.length];
		for(int i=0; i<values.length; i++)
			copyVals[i] = values[i];
		drawPieChart(x, y, diameter, copyVals, captions);
	}
	
	private void drawPieChart(int x, int y, int diameter, double[] values, String[] captions) {
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
        GL11.glLineWidth(1.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator tessellator = Tessellator.instance;
        double d0 = 0.0D;
        int xx = diameter + x;
        int yy = diameter/2 + y;
        
        //Determine max value
        double maxValue = 0;
        for(int i = 0; i < values.length; i++)
        	maxValue += values[i];
        
        //Draw Pie Chart
        for (int l = 0; l < values.length; ++l) {
        	double percent = (values[l]/maxValue)*100.0D;
            int i1 = MathHelper.floor_double(percent / 4.0D) + 1;
            tessellator.startDrawing(6);
            tessellator.setColorOpaque_I(RenderUtil.hueGrad(l, values.length).hashCode());
            tessellator.addVertex((double)xx, (double)yy, 0.0D);
            float f, f1, f2;

            for (int j1 = i1; j1 >= 0; --j1)
            {
                f = (float)((d0 + percent * (double)j1 / (double)i1) * Math.PI * 2.0D / 100.0D);
                f1 = MathHelper.sin(f) * (float)diameter;
                f2 = MathHelper.cos(f) * (float)diameter * 0.5F;
                tessellator.addVertex((double)((float)xx + f1), (double)((float)yy - f2), 0.0D);
            }

            tessellator.draw();
            tessellator.startDrawing(5);
            tessellator.setColorOpaque_I((RenderUtil.hueGrad(l, values.length).hashCode() & 16711422) >> 1);

            for (int j1 = i1; j1 >= 0; --j1){
                f = (float)((d0 + percent * (double)j1 / (double)i1) * Math.PI * 2.0D / 100.0D);
                f1 = MathHelper.sin(f) * (float)diameter;
                f2 = MathHelper.cos(f) * (float)diameter * 0.5F;
                tessellator.addVertex((double)((float)xx + f1), (double)((float)yy - f2), 0.0D);
                tessellator.addVertex((double)((float)xx + f1), (double)((float)yy - f2 + 10.0F), 0.0D);
            }

            tessellator.draw();
            d0 += percent;
        }
        
        //Draw Graph Key
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        for (int i = 0; i < values.length; i++){
            mc.fontRenderer.drawStringWithShadow(captions[i] + ": " + values[i],
            		xx+diameter+10, y+i*8, RenderUtil.hueGrad(i, values.length).hashCode());
        }
	}
	
	/**
	 * Draws a line graph to the screen (supports multiple lines). Horizontal axis
	 * corresponds to the array indices, vertical axis corresponds to the values
	 * in the array(s).
	 * 
	 * @param x 				Top-left position on screen to draw the graph (in pixels) 
	 * @param y 				Top-left position on screen to draw the graph (in pixels)
	 * @param width 			Size of the graph (in pixels)
	 * @param height 			Size of the graph (in pixels)
	 * @param numElements		Number of data points to visualize along the horizontal axis (number of array indices)
	 * @param numGuidelines     Number of numerical guidelines to show along the vertical axis (min and max values will always be shown)
	 * @param values 			Arrays of values to visualize. Each array corresponds to a single line on the graph.
	 */
	private void drawLineGraph(int x, int y, int width, int height, int numElements, int numGuidelines, Double[]... values) {
		//Determine min and max values
		double minElement = Double.MAX_VALUE;
		double maxElement = -Double.MAX_VALUE;
		for (Double[] valList : values) {
			for(int j=Data.swStart(valList.length, numElements); 
					j<Data.swEnd(valList.length, numElements); j++) {
				if (valList[j] < minElement)
					minElement = valList[j];
				if (valList[j] > maxElement)
					maxElement = valList[j];
			}
		}
		
		if (minElement == Double.MAX_VALUE)
			minElement = 0;
		if (maxElement == -Double.MAX_VALUE)
			maxElement = 0;
		
		//Draw axes and guidelines
		int guideColor = Color.getHSBColor(0.0f, 0.0f, 0.85f).hashCode();
		drawVerticalLine(x, y, y+height, guideColor);
		for(int i=0; i<numGuidelines+2; i++) {
			double proportion = (double)height/(double)(numGuidelines+1);
			drawHorizontalLine(x, x+width, y+(int)(i*proportion), guideColor);
			drawText(""+(maxElement-((maxElement-minElement)*((i*proportion)/(double)height))),
					x+width+5, y+(int)(i*proportion)-4, 0, guideColor);
		}
		
		//Draw graph
		GL11.glLineWidth(10.0f);
		double yRange = maxElement-minElement;
		if (yRange == 0)
			yRange = 1;
		double xInc = (double)width/(double)numElements;
		double yInc = (double)height/yRange;
				
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		for (int i=0; i<values.length; i++) {
			Tessellator t = Tessellator.instance;
			t.startDrawing(GL11.GL_LINE_STRIP);
			Color lineCol = RenderUtil.hueGrad(i, values.length);
			t.setColorOpaque(lineCol.getRed(),lineCol.getGreen(),lineCol.getBlue());
			GL11.glLineWidth(2.0f);
			
			//Calculate vertex coords for line list
			int startInd = Data.swStart(values[i].length, numElements);
			for(int j=startInd; 
					j<Data.swEnd(values[i].length, numElements); j++) {
				int absInd = j-startInd;
				t.addVertex(x+xInc*absInd, y+height-yInc*(values[i][j]-minElement)+i*0.5D, 0.0D);
			}
				
			//Draw line list
			t.draw();
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	private void drawText(String text, int x, int y, int lineOffset, int color) {	
		mc.fontRenderer.drawStringWithShadow(text, x, y+lineOffset*8, color);
	}
	
	//Set up display for rendering unscaled orthographic 2D graphics to the screen
	private void displayMode2D() {
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glLoadIdentity();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glOrtho(0.0D, (double)mc.displayWidth, (double)mc.displayHeight, 0.0, 1000.0D, 3000.0D);
	}
	
	//Reset display back to the normal Minecraft rendering
	private void resetDisplay() {
		ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, (double)scaledresolution.getScaledWidth(), (double)scaledresolution.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
	}
	
	public static MovingObjectPosition getLookBlock(World world, EntityPlayer entity, float range) {
		Vec3 posVec = Vec3.createVectorHelper(entity.posX, entity.posY /*+ entity.getEyeHeight()*/, entity.posZ);
		Vec3 lookVec = entity.getLookVec();
		MovingObjectPosition mop = world.rayTraceBlocks(posVec, lookVec);

		if (mop == null || (float)mop.hitVec.distanceTo(posVec) > range) {
			return null;
		} else 
			return mop;
	}
	
	public static float vec3Dist(int x1, int y1, int z1, int x2, int y2, int z2) {
		return (float)Math.sqrt(Math.pow((double)(x2-x1), 2.0)+Math.pow((double)(y2-y1), 2.0)+Math.pow((double)(z2-z1), 2.0));
	}
}
