package com.contained.game.item;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.contained.game.Contained;
import com.contained.game.util.Resources;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.books.BookData;
import mantle.books.BookDataStore;
import mantle.client.gui.GuiManual;
import mantle.items.abstracts.CraftingItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Tutorial books with information about the mod's functionality.
 */
public class TutorialBook extends CraftingItem {

	static String[] bookNames = new String[] { "main" };
	static String[] bookTextures = new String[] { "book_main" } ;
	BookData mainBook = new BookData();
	
	public TutorialBook() {
		super(bookNames, bookTextures, "", Resources.MOD_ID, CreativeTabs.tabMisc);
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		setUnlocalizedName(Resources.MOD_ID+".book");
		
		Document mainDoc = loadBookDocument("/assets/"+Resources.MOD_ID+"/books/main.xml", dbf);
		mainBook = initBook(mainBook, Resources.MOD_ID+".book.main", "Tutorial"
							   , side == Side.CLIENT ? mainDoc : null, Resources.MOD_ID+":"+bookTextures[0]);
	}
	
	@SideOnly(Side.CLIENT)
    public void openBook(ItemStack stack, World w, EntityPlayer p) {
    	p.openGui(Contained.instance, mantle.client.MProxyClient.manualGuiID, w, 0, 0, 0);
    	FMLClientHandler.instance().displayGuiScreen(p, new GuiManual(stack, getData(stack)));
    }
	
	private BookData getData(ItemStack book) {
		switch(book.getItemDamage()) {
			default:
				return mainBook;
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World w, EntityPlayer p) {
		if (w.isRemote)
			openBook(stack, w, p);
		return stack;
	}
	
	public static BookData initBook(BookData data, String unlocalizedName, String toolTip, Document xml, String image) {
		data.unlocalizedName = unlocalizedName;
		data.toolTip = unlocalizedName;
		data.modID = Resources.MOD_ID;
		data.itemImage = new ResourceLocation(Resources.MOD_ID, image);
		data.doc = xml;
		BookDataStore.addBook(data);
		return data;
	}
	
	public static Document loadBookDocument(String path, DocumentBuilderFactory dbf) {
        try {
            InputStream stream = Contained.class.getResourceAsStream(path);
            DocumentBuilder dBuilder = dbf.newDocumentBuilder();
            Document doc = dBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }		
	}
}
