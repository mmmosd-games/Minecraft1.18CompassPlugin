package mmmosd.kro;

import org.bukkit.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

public final class kr extends JavaPlugin implements Listener, CommandExecutor {

    HashMap<UUID, Boolean> isfinding = new HashMap<UUID, Boolean>();
//    HashMap<UUID, Boolean> isdisplay = new HashMap<UUID, Boolean>();
//    HashMap<UUID, String> loc = new HashMap<UUID, String>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("compass").setExecutor(this);
        saveConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player p = e.getPlayer();
//        isdisplay.put(p.getUniqueId(), false);
        isfinding.put(p.getUniqueId(), false);
//        loc.put(p.getUniqueId(), "");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = (Player) sender;
        if (args[0].equals("set") && !args[1].equals("")) {
//            p.sendMessage("좌표가 저장되었습니다. (" + ChatColor.RED + "X: " + p.getLocation().getBlockX() + ChatColor.GREEN +  " Y: " + p.getLocation().getBlockY() + ChatColor.BLUE +  " Z: " + p.getLocation().getBlockZ() + ")");
            p.sendMessage("좌표가 저장되었습니다.");
            p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 5, 1);
            File f = new File(getDataFolder(), "/" + args[1] +".txt");
            saveFile(f, p.getLocation().getBlockX() + "/" + p.getLocation().getBlockY() + "/" + p.getLocation().getBlockZ());
        }

        if (args[0].equals("help")) {
            p.sendMessage("/compass help (도움말 입니다.)");
            p.sendMessage("/compass set name (name이라는 좌표를 저장합니다.)");
            p.sendMessage("/compass remove name (name이라는 좌표를 저장합니다.)");
            p.sendMessage("/compass find name (나침반이 name이라는 좌표를 가리킵니다.)");
            p.sendMessage("/compass list (좌표들의 목록을 불러옵니다.)");
            p.sendMessage("/compass lose (길 찾기를 취소합니다.)");
//            p.sendMessage("/location display true (액션바의 좌표를 켭니다.)");
//            p.sendMessage("/location display false (액션바의 좌표를 끕니다.)");
        }

        if (args[0].equals("list")) {
            String[] list = getDataFolder().list();
            String list1 = "";
            for(int i = 0; i < list.length; i++) {
                if (!list[i].equals("config.yml")) {
                    list[i] = list[i].substring(0, list[i].length() - 4);
                    if (i < list.length - 1) {
                        list1 = list1 + list[i] + ", ";
                    }else {
                        list1 = list1 + list[i];
                    }
                }
                else {
                    list[i] = "";
                }
            }

            if (!list1.equals("")) {
                p.sendMessage("저장된 좌표: " + list1);
            }
            else  {
                p.sendMessage(ChatColor.RED + "저장된 좌표가 없습니다.");
            }
        }

        if (args[0].equals("remove") && !args[1].equals("")) {
            File f = new File(getDataFolder(), "/" + args[1] + ".txt");
            boolean d = false;
            while (!d) {
                d = f.delete();
                if (d) {
                    break;
                }
            }
            p.sendMessage("성공적으로 삭제하였습니다.");
            p.playSound(p.getLocation(), Sound.ITEM_BOOK_PUT, 5, 1);
        }

        if (args[0].equals("find") && !args[1].equals("")) {
            UUID u = p.getUniqueId();
            File f = new File(getDataFolder(), "/" + args[1] +".txt");
            try {
                if (p.getInventory().getItemInMainHand().getType().equals(Material.COMPASS)) {
                    BufferedReader reader = new BufferedReader(new FileReader(f));
                    String[] location = reader.readLine().split("/");
                    p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 5, 1);
                    isfinding.put(u, true);
//                loc.put(u, ChatColor.RED + "X: " + location[0].toString() + ChatColor.GREEN + " Y: " + location[1].toString() + ChatColor.BLUE + " Z: " + location[2].toString());
                    Location loc = new Location(p.getWorld(),Double.parseDouble(location[0]), Double.parseDouble(location[1]), Double.parseDouble(location[2]));
//                p.setCompassTarget(loc);
                    p.sendMessage("길 찾기를 시작합니다.");
                    ItemStack compass = p.getInventory().getItemInMainHand();
                    CompassMeta compassMeta = (CompassMeta) compass.getItemMeta();
                    compassMeta.setLodestoneTracked(false);
                    compassMeta.setLodestone(loc);
                    compass.setItemMeta(compassMeta);
                    p.getInventory().setItemInMainHand(compass);
                }
                else if (p.getInventory().getItemInOffHand().getType().equals(Material.COMPASS))  {
                    BufferedReader reader = new BufferedReader(new FileReader(f));
                    String[] location = reader.readLine().split("/");
                    p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 5, 1);
                    isfinding.put(u, true);
//                loc.put(u, ChatColor.RED + "X: " + location[0].toString() + ChatColor.GREEN + " Y: " + location[1].toString() + ChatColor.BLUE + " Z: " + location[2].toString());
                    Location loc = new Location(p.getWorld(),Double.parseDouble(location[0]), Double.parseDouble(location[1]), Double.parseDouble(location[2]));
//                p.setCompassTarget(loc);
                    p.sendMessage("길 찾기를 시작합니다.");
                    ItemStack compass = p.getInventory().getItemInOffHand();
                    CompassMeta compassMeta = (CompassMeta) compass.getItemMeta();
                    compassMeta.setLodestoneTracked(false);
                    compassMeta.setLodestone(loc);
                    compass.setItemMeta(compassMeta);
                    p.getInventory().setItemInOffHand(compass);
                }
                else {
                    p.sendMessage(ChatColor.RED + "나침반을 왼손 또는 오른손에 착용하십시오.");
                }
            } catch (FileNotFoundException e3) {
                e3.printStackTrace();
                p.sendMessage(ChatColor.RED + args[1] + " 은/는 존재하지 않는 좌표입니다.");
            } catch (IOException e4) {
                e4.printStackTrace();
                p.sendMessage(ChatColor.RED + args[1] + " 은/는 존재하지 않는 좌표입니다.");
            }
        }

        if (args[0].equals("lose")) {
            p.sendMessage("길 찾기를 취소했습니다");
            UUID u = p.getUniqueId();
            isfinding.put(u, false);
//            loc.put(u, "");
        }

        if (args[0].equals("display")) {
            if (args[1].equals("true")) {
//                isdisplay.put(p.getUniqueId(), true);
            }
            else  {
//                isdisplay.put(p.getUniqueId(), false);
            }
        }
        return false;
    }

    @EventHandler
    public void move(PlayerMoveEvent m) {
        Player p = m.getPlayer();
//        if (isdisplay.get(p.getUniqueId())) {
            if (isfinding.get(p.getUniqueId())) {
//                p.sendActionBar( ChatColor.RED + "X: " + p.getLocation().getBlockX() + ChatColor.GREEN +  " Y: " + p.getLocation().getBlockY() + ChatColor.BLUE +  " Z: " + p.getLocation().getBlockZ() + ChatColor.GRAY + " | (" + loc.get(p.getUniqueId()) + ChatColor.GRAY + ")" );
            }
            else {
//                p.sendActionBar(ChatColor.RED + "X: " + p.getLocation().getBlockX() + ChatColor.GREEN + " Y: " + p.getLocation().getBlockY() + ChatColor.BLUE + " Z: " + p.getLocation().getBlockZ());
            }
//        }
        p.getWorld().setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
    }

    public void saveFile(File f, String s) {
        try {
            f.createNewFile();
            FileWriter writer = new FileWriter(f, false);
            writer.write(s);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
