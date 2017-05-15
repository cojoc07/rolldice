package com.binslayer.rolldice;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Arrays;
import java.util.Date;

public final class RollDice extends JavaPlugin {
	static HashMap<String, Long> alreadyUsed;
	static long timelimit = 180000L; //millisecunde
	
	//CHANCES
	static int give_start = 0;
	static int give_end = 7;
	static int nothing_start = 9;
	static int nothing_end = 10;
	static int takeaway = 8;
	
	@Override
	public void onEnable() {
		alreadyUsed = new HashMap<String, Long>();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("roll")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (p.getInventory().firstEmpty() != -1) {
					if (alreadyUsed.containsKey(p.getName()) == true && (new Date().getTime() - alreadyUsed.get(p.getName()) > timelimit ) 
							|| alreadyUsed.containsKey(p.getName()) == false) {
						int choice = randomWithRange(0, 10);
						if (choice >= give_start && choice <= give_end) {
							roll(p);
						}
						else if (choice >= nothing_start && choice <= nothing_end) {
							sender.sendMessage("Ghinion: N-a picat nimic la rulare");
							Bukkit.broadcastMessage("[Roll&Dice] Ghinon pentru " + p.getName() + ".. Si-a incercat norocul insa n-a picat nimic la rulare");
						}
						else if (choice == takeaway) {
							takeItem(p);
						}
						
						alreadyUsed.put(p.getName(), new Date().getTime());
					}
					else if (alreadyUsed.containsKey(p.getName()) == true) {
						int wait = (int)(timelimit - (new Date().getTime() - alreadyUsed.get(p.getName())))/1000;
						sender.sendMessage("ANTISPAM, mai ai de asteptat: " + wait + " secunde!!");
					}
				}
				else {
					sender.sendMessage("AI INVENTARUL PLIN, NU POTI FOLOSI ROLL!");
				}
			}
			else {
				sender.sendMessage("Nu merge din consola ci doar din joc!!");
			}
			return true;
		} 
		return false; 
	}
	
	/*
	 *  Take function
	 */
	public void takeItem(Player p) {
		int itemNumber = randomWithRange(0, p.getInventory().getContents().length-1);
		ItemStack item = null;
		do {
			item = p.getInventory().getContents()[itemNumber];
		}
		while (item == null);
		
		Bukkit.broadcastMessage("[Roll&Dice] GHINION MAXIM pentru "+ p.getName() + "!! A rulat negativ si a pierdut din inventar: " + item.getType().toString());
		p.getInventory().remove(item);
	}
	
	/*
	 *  Roll function
	 */
	public void roll(Player p) {
		Material[] materials = Material.class.getEnumConstants(); //all materials possible
		Material randomMaterial = materials[randomWithRange(0, materials.length-1)];
		ItemStack newItem = new ItemStack(randomMaterial, 1);
		p.getInventory().addItem(newItem);
		Bukkit.broadcastMessage("[Roll&Dice] " + p.getName() + " a rulat si a castigat: " + randomMaterial.toString());
	}
	
	/*
	 *  Random function
	 */
	int randomWithRange(int min, int max)
	{
	   int range = (max - min) + 1;     
	   return (int)(Math.random() * range) + min;
	}
	
	// comentariu de test pt GIT
}
