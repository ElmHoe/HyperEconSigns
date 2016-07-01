package uk.co.ElmHoe.HyperEconSigns;

import java.text.DecimalFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class WorldListener
  implements Listener
{
  DecimalFormat df = new DecimalFormat("#.##");
  
  @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
  public void onChunkLoad(ChunkLoadEvent e)
  {
    BlockState[] arrayOfBlockState;
    int j = (arrayOfBlockState = e.getChunk().getTileEntities()).length;
    for (int i = 0; i < j; i++)
    {
      BlockState bs = arrayOfBlockState[i];
      try
      {
        if ((bs instanceof Sign))
        {
          Sign csign = (Sign)bs;
          if (csign != null)
          {
            String title = ChatColor.stripColor(csign.getLine(0));
            if (title.equals("[Sell]"))
            {
              double amount = Integer.parseInt(csign.getLine(1));
              String cost_line = csign.getLine(3);
              if (cost_line.contains("$")) {
                cost_line = cost_line.replace("$", "");
              }
              if (!cost_line.contains("/"))
              {
                double cost = Double.parseDouble(cost_line);
                double cost_per = cost / amount;
                
                csign.setLine(0, ChatColor.DARK_BLUE.toString() + "[Sell All]");
                csign.setLine(1, "ALL");
                csign.setLine(3, "$" + this.df.format(cost_per) + "/ea");
                csign.update();
              }
            }
          }
        }
      }
      catch (Exception err)
      {
        Bukkit.getLogger().info("[ERROR] " + bs.getLocation().toString() + " could not be processed by sign converted due to format exception.");
      }
    }
  }
}
