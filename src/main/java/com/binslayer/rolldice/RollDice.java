package com.binslayer.rolldice;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public final class RollDice extends JavaPlugin {
	static ArrayList<Material> excludedMaterials;
	
	static HashMap<String, Long> alreadyUsed;
	// static long timelimit = 180000L;
	static long timelimit = 180000L;
	Random r = new Random();

	static int give_start = 0;
	static int give_end = 7;
	static int nothing_start = 9;
	static int nothing_end = 10;
	static int takeaway = 8;

	static int upsetti_baghetti = 11;

	public void onEnable() {
		alreadyUsed = new HashMap();
	}
	
	static {
		excludedMaterials = new ArrayList<Material>();
		excludedMaterials.add(Material.ELYTRA);
		excludedMaterials.add(Material.RECORD_3);
		excludedMaterials.add(Material.RECORD_4);
		excludedMaterials.add(Material.RECORD_5);
		excludedMaterials.add(Material.RECORD_6);
		excludedMaterials.add(Material.RECORD_7);
		excludedMaterials.add(Material.RECORD_8);
		excludedMaterials.add(Material.RECORD_9);
		excludedMaterials.add(Material.RECORD_10);
		excludedMaterials.add(Material.RECORD_11);
		excludedMaterials.add(Material.RECORD_12);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("roll")) {
			if ((sender instanceof Player)) {
				Player p = (Player) sender;

				if (p.getInventory().firstEmpty() != -1) {
					if (((alreadyUsed.containsKey(p.getName()))
							&& (new Date().getTime() - ((Long) alreadyUsed.get(p.getName())).longValue() > timelimit))
							|| (!alreadyUsed.containsKey(p.getName()))) {
						int choice = randomRangeBetter(0,12);
						if ((choice >= give_start) && (choice <= give_end)) {
							roll(p);
						} else if ((choice >= nothing_start) && (choice <= nothing_end)) {
							sender.sendMessage("Ghinion: N-a picat nimic la rulare");
							Bukkit.broadcastMessage("[Roll&Dice] Ghinon pentru " + p.getName()
									+ ".. Si-a incercat norocul insa n-a picat nimic la rulare");
						} else if (choice == takeaway) {
							takeItem(p);
						} else if (choice == upsetti_baghetti) {
							//p.getWorld().createExplosion(p.getLocation(), 5.0F);
							
							spawnRandomMob(p);
							
							Bukkit.broadcastMessage("[Roll&Dice] Destul de rau pentru " + p.getName() + ". A castigat un mob");
						}

						alreadyUsed.put(p.getName(), Long.valueOf(new Date().getTime()));
					} else if (alreadyUsed.containsKey(p.getName())) {
						int wait = (int) (timelimit
								- (new Date().getTime() - ((Long) alreadyUsed.get(p.getName())).longValue())) / 1000;
						sender.sendMessage("ANTISPAM, mai ai de asteptat: " + wait + " secunde!!");
					}
				} else {
					sender.sendMessage("AI INVENTARUL PLIN, NU POTI FOLOSI ROLL!");
				}
			} else {
				sender.sendMessage("Nu merge din consola ci doar din joc!!");
			}
			return true;
		}
		return false;
	}

	public void takeItem(Player p) {
		
		ArrayList<Integer> arrItemsNotNull = new ArrayList<Integer>();
		for (int i=0; i<p.getInventory().getContents().length; i++) {
			if (p.getInventory().getContents()[i] != null) {
				arrItemsNotNull.add(i);
			}
		}
		int itemNumber = randomRangeBetter(0, arrItemsNotNull.size());
		ItemStack item = p.getInventory().getContents()[arrItemsNotNull.get(itemNumber)];

		Bukkit.broadcastMessage("[Roll&Dice] GHINION MAXIM pentru " + p.getName()
				+ "!! A rulat negativ si a pierdut din inventar: " + item.getType().toString());
		p.getInventory().remove(item);
	}

	public void roll(Player p) {
		Material[] materials = (Material[]) Material.class.getEnumConstants();
		Material randomMaterial = materials[randomRangeBetter(0, materials.length)];
		//e posibil ca materialul care a picat sa nu fie un item de tinut in inventar, caz in care cauti unul
		//care sa fie mereu diferit de o lista de excluderi
		while (excludedMaterials.contains(randomMaterial)) {
			randomMaterial = materials[randomRangeBetter(0, materials.length)];
		}
		ItemStack newItem = new ItemStack(randomMaterial, 1);
		p.getInventory().addItem(new ItemStack[] { newItem });
		Bukkit.broadcastMessage("[Roll&Dice] " + p.getName() + " a rulat si a castigat: " + randomMaterial.toString());
	}
	
	
	/* marginea de sus este exclusiva*/
	int randomRangeBetter(int min, int max){
		int randomNum = r.nextInt((max - min)) + min;
		return randomNum;
	 }
	
	public void spawnRandomMob(Player p){
		EntityType[] t = new EntityType[5];
		t[0] = EntityType.SKELETON;
		t[1] = EntityType.ZOMBIE;
		t[2] = EntityType.VINDICATOR;
		t[3] = EntityType.EVOKER;
		t[4] = EntityType.STRAY;
		
		int randomMob = r.nextInt(5);
		
        World world = p.getWorld();
        Location targetLocation = p.getLocation();
        world.spawnEntity(targetLocation, t[randomMob]);
	}
}

