package uk.co.ElmHoe.HyperEconSigns;

import java.text.DecimalFormat;
import uk.co.ElmHoe.Utilities.StringUtility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

public class PlayerListener
  implements Listener
{
  private HyperEconSigns plugin;
  
  public PlayerListener(HyperEconSigns aes)
  {
    this.plugin = aes;
  }
  
  DecimalFormat df = new DecimalFormat("#.##");
  
  @EventHandler
  public void onSignChangeEvent(SignChangeEvent e){
	  if (((e.getLine(0).contains("[Sell]")) || (e.getLine(0).contains("[Sell All]"))) && (!e.getPlayer().isOp())){
		  e.setCancelled(true);
		  return;
	  }
  }
  

  
@SuppressWarnings("deprecation")
@EventHandler(priority=EventPriority.HIGH)
  public void onPlayerInteract(PlayerInteractEvent e)
  {
    Block block = e.getClickedBlock();
    if (block == null) {
      return;
    }
    Player pl = e.getPlayer();
    Material mat = block.getType();
    int id = 0;
    if ((mat == Material.SIGN_POST) || (mat == Material.WALL_SIGN) || (mat == Material.SIGN)){
    	try{
    		Sign csign = (Sign)block.getState();
    		if (csign == null) {
    			return;
    		}

    		String title = ChatColor.stripColor(csign.getLine(0));
    		if ((title.contains("Sell]")) || (title.equalsIgnoreCase("[sa]"))){
    			e.setCancelled(true);
    			boolean use_cost_each = false;
    			double amount = 0.0D;
    			if (csign.getLine(1).equalsIgnoreCase("ALL")) {
    				amount = 1.0D;
    			} else {
    				amount = Integer.parseInt(csign.getLine(1));
    			}
    			String product_line = csign.getLine(2).toUpperCase();
    			Material m = null;
    			short durability = 0;
    			if (HyperEconSigns.ess_materials.containsKey(product_line)){
    				m = (Material)HyperEconSigns.ess_materials.get(product_line);
    			
    			
    			}else if (product_line.contains(":")){
    				durability = Short.parseShort(product_line.split(":")[1]);
    				id = Integer.parseInt(product_line.split(":")[0]);

    				m = (Material)Material.getMaterial(id);
    			
    			}else{
    				
    				
            m = (Material)Material.getMaterial(product_line);
          }
          String cost_line = csign.getLine(3);
          if (cost_line.contains("$")) {
            cost_line = cost_line.replace("$", "");
          }
          if (cost_line.contains("/ea"))
          {
            cost_line = cost_line.split("/")[0];
            use_cost_each = true;
          }
          double cost = Double.parseDouble(cost_line);
          
          double cost_per = cost;
          if (!use_cost_each) {
            cost_per = cost / amount;
          }
          int quantityFound = 0;
          double multiplier = uk.co.ElmHoe.Utilities.MultiplierUtility.onMultiCheck(pl);

          for (ItemStack is : pl.getInventory()) {
            if (is != null) {
              if((is.getType() == m) && (is.getDurability() == durability)) {
                quantityFound += is.getAmount();
              }else if (is.getTypeId() == id){
            	  quantityFound += is.getAmount();
            	  id = 0;
              }
            }
          }
          pl.getInventory().remove(m);
          pl.sendMessage("");
          if (quantityFound <= 0)
          {
            pl.sendMessage(ChatColor.RED + "You do " + ChatColor.UNDERLINE + "not" + ChatColor.RED + " have any " + ChatColor.RED + m.name() + ChatColor.RED + " to sell!");
            return;
          }
          double reward = cost_per * quantityFound * multiplier;
          
          HyperEconSigns.economy.depositPlayer(pl, reward);
          pl.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "+ $" 
          + this.df.format(reward) + ChatColor.DARK_GRAY.toString() + ChatColor.BOLD.toString() 
          + " [" + ChatColor.AQUA.toString() + quantityFound + "x " + m.name() 
          + ChatColor.DARK_GRAY.toString() + ChatColor.BOLD.toString() + "]"
          + "\n" + StringUtility.format("&a&lMultiplier x " + multiplier));
          
          pl.updateInventory();
          
          pl.sendMessage("");
          pl.playSound(pl.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.5F);
          
          int f_quantityFound = quantityFound;
          double f_reward = reward;
          Material f_m = m;
          
          pl.playSound(pl.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 0.25F);
          
          Hologram hg = HologramsAPI.createHologram(plugin, pl.getLocation().add(0.0D, 3.0D, 0.0D));
          hg.appendTextLine(StringUtility.format("&a&l+ $" + this.df.format(f_reward)));
          hg.insertTextLine(1, StringUtility.format("&b + " + f_quantityFound + "x " + f_m.name()));
          hg.insertTextLine(2, StringUtility.format("&a&l"+ "Multiplier x &b&l" + multiplier));
          
          Bukkit.getScheduler().runTaskLater(HyperEconSigns.instance, new Runnable(){
            public void run(){
            	hg.delete();
            }
          }, 60L);
          if (!use_cost_each)
          {
            csign.setLine(0, StringUtility.format("&7[&6&lHyperSell&7]"));
            csign.setLine(1, "ALL");
            csign.setLine(3, "$" + this.df.format(cost_per) + "");
            csign.update();
            
            
            
          }
        }
      }
      catch (Exception err)
      {
        err.printStackTrace();
      }
    }
  }
}
