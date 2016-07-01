package uk.co.ElmHoe.HyperEconSigns;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class HyperEconSigns extends JavaPlugin{

	public static Economy economy = null;
	  public static HashMap<String, Material> ess_materials = new HashMap<String, Material>();
	  public static HyperEconSigns instance;
	  
	  public void onEnable()
	  {
	    instance = this;
	    if (!setupEconomy())
	    {
	      getServer().getPluginManager().disablePlugin(this);
	      return;
	    }
	    saveDefaultConfig();
	    loadEssMaterials();
	    
	    getServer().getPluginManager().registerEvents(new WorldListener(), this);
	    getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
	  }
	  
	  public void onDisable() {}
	  
	  public static void log(String message){
	    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_BLUE + "[HyperEconSigns] " + ChatColor.YELLOW + message);
	  }
	  
	  public void loadEssMaterials(){
		  try{
			  uk.co.ElmHoe.Utilities.ItemUtility.loadItems();
			  HyperEconSigns.log("Item Utilitiy loaded without issue!");
		  }catch (Exception e){
			  HyperEconSigns.log("Failed loading items.");
		  }
	  }
	  
	  private boolean setupEconomy(){
	    RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
	    if (economyProvider != null) {
	      economy = (Economy)economyProvider.getProvider();
	    }
	    return economy != null;
	  }
	}

