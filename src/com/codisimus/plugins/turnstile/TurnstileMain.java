package com.codisimus.plugins.turnstile;

import com.codisimus.plugins.turnstile.listeners.worldListener;
import com.codisimus.plugins.turnstile.listeners.blockListener;
import com.codisimus.plugins.turnstile.listeners.commandListener;
import com.codisimus.plugins.turnstile.listeners.pluginListener;
import com.codisimus.plugins.turnstile.listeners.playerListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionManager;

/**
 * Loads Plugin and manages Permissions
 *
 * @author Codisimus
 */
public class TurnstileMain extends JavaPlugin {
    public static int cost = 0;
    public static PermissionManager permissions;
    public static PluginManager pm;
    public static Server server;
    public static boolean noFraud;
    public static int timeOut;
    public static boolean useOpenFreeNode;
    public static boolean useMakeFreeNode;
    public static String correct;
    public static String wrong;
    public static String notEnoughMoney;
    public static String displayCost;
    public static String open;
    public static String balanceCleared;
    public static String privateTurnstile;
    public static Properties p;

    @Override
    public void onDisable () {
        //Close all open Turnstiles
        for (Turnstile turnstile: playerListener.openTurnstiles)
            turnstile.close();
    }

    @Override
    public void onEnable () {
        server = getServer();
        pm = server.getPluginManager();
        checkFiles();
        loadConfig();
        SaveSystem.loadFromFile();
        registerEvents();
        getCommand("turnstile").setExecutor(new commandListener());
        System.out.println("Turnstile "+this.getDescription().getVersion()+" is enabled!");
    }

    /**
     * Makes sure all needed files exist
     *
     */
    public void checkFiles() {
        File file = new File("plugins/Turnstile/config.properties");
        if (!file.exists())
            moveFile("config.properties");
    }
    
    /**
     * Moves file from Turnstile.jar to appropriate folder
     * Destination folder is created if it doesn't exist
     * 
     * @param fileName The name of the file to be moved
     */
    public void moveFile(String fileName) {
        try {
            //Retrieve file from this plugin's .jar
            JarFile jar = new JarFile("plugins/Turnstile.jar");
            ZipEntry entry = jar.getEntry(fileName);
            
            //Create the destination folder if it does not exist
            String destination = "plugins/Turnstile/";
            File file = new File(destination.substring(0, destination.length()-1));
            if (!file.exists())
                file.mkdir();
            
            //Copy the file
            File efile = new File(destination, fileName);
            InputStream in = new BufferedInputStream(jar.getInputStream(entry));
            OutputStream out = new BufferedOutputStream(new FileOutputStream(efile));
            byte[] buffer = new byte[2048];
            while (true) {
                int nBytes = in.read(buffer);
                if (nBytes <= 0)
                    break;
                out.write(buffer, 0, nBytes);
            }
            out.flush();
            out.close();
            in.close();
        }
        catch (Exception moveFailed) {
            System.err.println("[ChunkOwn] File Move Failed!");
            moveFailed.printStackTrace();
        }
    }

    /**
     * Loads settings from the config.properties file
     * 
     */
    public void loadConfig() {
        p = new Properties();
        try {
            p.load(new FileInputStream("plugins/Turnstile/config.properties"));
        }
        catch (Exception e) {
        }
        cost = Integer.parseInt(loadValue("CostToMakeTurnstile"));
        Register.economy = loadValue("Economy");
        pluginListener.useBP = Boolean.parseBoolean(loadValue("UseBukkitPermissions"));
        Turnstile.oneWay = Boolean.parseBoolean(loadValue("OneWayTurnstiles"));
        noFraud = Boolean.parseBoolean(loadValue("NoFraud"));
        timeOut = Integer.parseInt(loadValue("AutoCloseTimer"));
        useOpenFreeNode = Boolean.parseBoolean(loadValue("use'openfree'node"));
        useMakeFreeNode = Boolean.parseBoolean(loadValue("use'makefree'node"));
        playerListener.permission = format(loadValue("PermissionMessage"));
        playerListener.locked = format(loadValue("LockedMessage"));
        playerListener.free = format(loadValue("FreeMessage"));
        playerListener.oneWay = format(loadValue("OneWayMessage"));
        correct = format(loadValue("CorrectItemMessage"));
        wrong = format(loadValue("WrongItemMessage"));
        notEnoughMoney = format(loadValue("NotEnoughMoneyMessage"));
        displayCost = format(loadValue("DisplayCostMessage"));
        open = format(loadValue("OpenMessage"));
        balanceCleared = format(loadValue("BalanceClearedMessage"));
        privateTurnstile = format(loadValue("PrivateMessage"));
    }
    
    /**
     * Loads the given key and prints error if the key is missing
     *
     * @param key The key to be loaded
     * @return The String value of the loaded key
     */
    public String loadValue(String key) {
        if (!p.containsKey(key)) {
            System.err.println("[Turnstile] Missing value for "+key+" in config file");
            System.err.println("[Turnstile] Please regenerate config file");
        }
        return p.getProperty(key);
    }
    
    /**
     * Registers events for the Turnstile Plugin
     *
     */
    public void registerEvents() {
        playerListener playerListener = new playerListener();
        blockListener blockListener = new blockListener();
        pm.registerEvent(Type.PLUGIN_ENABLE, new pluginListener(), Priority.Monitor, this);
        pm.registerEvent(Type.WORLD_LOAD, new worldListener(), Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
        pm.registerEvent(Type.REDSTONE_CHANGE, blockListener, Priority.Normal, this);
        pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
    }
    
    /**
     * Returns boolean value of whether the given player has the specific permission
     * 
     * @param player The Player who is being checked for permission
     * @param type The String of the permission, ex. admin
     * @return true if the given player has the specific permission
     */
    public static boolean hasPermission(Player player, String type) {
        //Check if a Permission Plugin is present
        if (permissions != null)
            return permissions.has(player, "turnstile."+type);

        //Return Bukkit Permission value
        return player.hasPermission("turnstile."+type);
    }
    
    /**
     * Adds various Unicode characters to a string
     * 
     * @param string The string being formated
     * @return The formatted String
     */
    public static String format(String string) {
        return string.replaceAll("&", "§").replaceAll("<ae>", "æ").replaceAll("<AE>", "Æ")
                .replaceAll("<o/>", "ø").replaceAll("<O/>", "Ø")
                .replaceAll("<a>", "å").replaceAll("<A>", "Å");
    }
    
    /**
     * Checks if the given ID is a Button, Chest, or Pressure Plate
     * 
     * @param target The ID to be checked
     * @return true if the ID is a Button, Chest, or Pressure Plate
     */
    public static boolean isSwitch(int id) {
        switch (id) {
            case 54: return true; //ID is Chest
            case 70: return true; //ID is Stone Plate
            case 72: return true; //ID is Wood Plate
            case 77: return true; //ID is Button
            default: return false;
        }
    }
    
    /**
     * Checks if the given ID is a Door, Fence, Fence Gate, or Trap Door
     * 
     * @param target The ID to be checked
     * @return true if the ID is a Door, Fence, Fence Gate, or Trap Door
     */
    public static boolean isDoor(int id) {
        switch (id) {
            case 64: return true; //ID is Wood Door
            case 71: return true; //ID is Iron Door
            case 324: return true; //ID is Wood Door
            case 330: return true; //ID is Iron Door
            default: return false;
        }
    }
    
    /**
     * Returns whether the given Block is above or below the other given Block
     * 
     * @param blockOne The first Block to be compared
     * @param blockTwo The second Block to be compared
     * @return true if the given Block is above or below the other given Block
     */
    public static boolean areNeighbors(Block blockOne, Block blockTwo) {
        if (blockOne.getWorld() != blockTwo.getWorld())
            return false;
        
        if (blockOne.getX() != blockTwo.getX())
            return false;
        
        if (blockOne.getZ() != blockTwo.getZ())
            return false;
        
        int b = blockOne.getY();
        int y = blockTwo.getY();
        return b == y+1 || b == y-1;
    }
}
