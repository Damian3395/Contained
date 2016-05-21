package com.contained.game.ui;

import mantle.client.pages.BookPage;
import net.minecraft.util.StatCollector;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CleanSectionPage extends BookPage
{
    String title;
    String body;

    @Override
    public void readPageFromXML (Element element)
    {
        NodeList nodes = element.getElementsByTagName("title");
        if (nodes != null)
            title = nodes.item(0).getTextContent();

        nodes = element.getElementsByTagName("text");
        if (nodes != null)
            body = nodes.item(0).getTextContent();
    }

    @Override
    public void renderContentLayer (int localWidth, int localHeight, boolean isTranslatable)
    {
        if (isTranslatable)
        {
            title = StatCollector.translateToLocal(title);
            body = StatCollector.translateToLocal(body);
        }
        manual.fonts.drawSplitString("\u00a7n" + title, localWidth + 88 - manual.fonts.getStringWidth(title)/2, localHeight + 4, 178, 0);
        manual.fonts.drawSplitString(body, localWidth, localHeight + 16, 175, 0);
    }
}
