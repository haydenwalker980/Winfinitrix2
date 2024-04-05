package us.windevsystems.winfinitrix2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Winfinitrix2 extends JavaPlugin {
    ZonedDateTime now = ZonedDateTime.now();
    DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String date = this.now.format(this.dateformat);
    DateTimeFormatter timeformat = DateTimeFormatter.ofPattern("HH:mm:ss z");
    String time = this.now.format(this.timeformat);
    private boolean isWhitelisted = false;
    public String version = "1.1.3";
    public void onEnable() {
        UUID serverUUID;
        Bukkit.getServer().getLogger().info("[Winfinitricks] Connected to Sentry");
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            Bukkit.getServer().getLogger().info("[Winfinitricks] Data folder does not exist, creating...");
            dataFolder.mkdir();
            Bukkit.getServer().getLogger().info("[Winfinitricks] Data folder OK");
        } else {
            Bukkit.getServer().getLogger().info("[Winfinitricks] Data folder OK");
        }
        File configFile = new File(dataFolder, "wt2.yml");
        if (!configFile.exists()) {
            try {
                Bukkit.getServer().getLogger().info("[Winfinitricks] wt2.yml does not exist, creating...");
                configFile.createNewFile();
                Bukkit.getServer().getLogger().info("[Winfinitricks] wt2.yml OK");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getServer().getLogger().info("[Winfinitricks] wt2.yml OK");
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        Bukkit.getServer().getLogger().info("[Winfinitricks] Configuration loaded");
        if (config.contains("uuid")) {
            serverUUID = UUID.fromString(config.getString("uuid"));
        } else {
            Bukkit.getServer().getLogger().info("[Winfinitricks] Generating a wt2.yml file (as this is the first run)");
            serverUUID = UUID.randomUUID();
            config.set("uuid", serverUUID.toString());
            try {
                File WtrxConfig = new File(getDataFolder(), "wt2.yml");
                PrintWriter CfgGen = new PrintWriter(new FileWriter(WtrxConfig, true));
                CfgGen.println("# Winfinitricks config file");
                CfgGen.println("# Generated on " + this.date.toString() + " at " + this.time.toString() + "during first plugin run");
                CfgGen.println("");
                CfgGen.println("# This server's UUID.");
                CfgGen.println("# Do not modify this value when using the default C&C server");
                CfgGen.println("# or your server wont be whitelisted anymore and its not gonna");
                CfgGen.println("# be my (Win's) problem. You have been warned.");
                CfgGen.println("# (you can ignore this tirade if you have your own C&C server");
                CfgGen.println("# implementation)");
                CfgGen.println("uuid: " + serverUUID.toString());
                CfgGen.println("");
                CfgGen.println("# Whether or not to check for updates on server start");
                CfgGen.println("updateOnServerStart: true");
                CfgGen.println("");
                CfgGen.println("# Whether or not to download updates on server start");
                CfgGen.println("downloadOnServerStart: true");
                CfgGen.println("");
                CfgGen.println("# Bypass the downloadOnServerStart major version restriction.");
                CfgGen.println("# This is a horrible idea, as major versions are incompatible/breaking changes according to semantic versioning.");
                CfgGen.println("# If you're sure you want to do this, set this to true.");
                CfgGen.println("");
                CfgGen.println("# Alternative C&C server");
                CfgGen.println("altCncServer: null");
                CfgGen.println("# Whether to disable validation of the altCncServer variable (if enabled, you will need to enter the full path to your check endpoint minus the UUID.");
                CfgGen.println("# This should only be done if the first part of your domain name is literally \"check\" or you're running a custom server implementation.");
                CfgGen.println("disableCustomCncServerValidation: false");
                CfgGen.println("");
                CfgGen.println("# Custom command classes. This doesn't do anything (yet) because i cba to code it in");
                CfgGen.println("customCmdClasses: []");
                CfgGen.println("# End of config file. Put any custom config options below this line.");
                CfgGen.close();
                Bukkit.getServer().getLogger().info("[Winfinitricks] Generated wtrx.yml.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            String whitelistCheckUrl = "HEHEHEHA";
            if (config.contains("altCncServer")) {
                if (config.getString("altCncServer") != null && !config.getString("altCncServer").isEmpty() && config.getBoolean("disableCustomCncServerValidation")) {
                    whitelistCheckUrl = config.getString("altCncServer") + "/check?sid=" + config.getString("altCncServer");
                    Bukkit.getLogger().warning("[Winfinitricks] Warning: you have disabled validation for the altCncServer variable.");
                    Bukkit.getLogger().warning("[Winfinitricks] This should not normally be done unless the first part of your domain name is literally \"check\" or you're running a custom server implementation.");
                    Bukkit.getLogger().warning("[Winfinitricks] Once again, in order for whitelisting to work, the full path to the check endpoint of your C&C server should be in wtrx.yml, including the URL parameter to specify the UUID.");
                    Bukkit.getLogger().warning("[Winfinitricks] (but obviously, minus the UUID - the plugin will always handle that)");
                    Bukkit.getServer().getLogger().info("[Winfinitricks] Using alternate C&C server: " + config.getString("altCncServer") + "(as set in wtrx.yml)");
                } else if (config.getString("altCncServer").contains("/check")) {
                    Bukkit.getServer().getLogger().severe("[Winfinitricks] Error: Looks like your alternative C&C server URL is invalid.");
                    Bukkit.getServer().getLogger().severe("[Winfinitricks] Either the first part of your domain name is literally \"check\" or you included the path in your URL. Don't do that, the plugin does so automatically!");
                    Bukkit.getServer().getLogger().severe("[Winfinitricks] If the former's the case, set disableCustomCncServerValidation to true in wtrx.yml.");
                    Bukkit.getServer().getLogger().severe("[Winfinitricks] Fair warning that you will need to input the full path of your custom C&C server's check endpoint (minus the UUID, the plugin always handles that) into the config file.");
                    Bukkit.getServer().getLogger().severe("[Winfinitricks] Whitelist failure. Disabling.");
                    Bukkit.getServer().getPluginManager().disablePlugin((Plugin)this);
                } else {
                    whitelistCheckUrl = config.getString("altCncServer") + "/check?sid=" + config.getString("altCncServer");
                    Bukkit.getServer().getLogger().info("[Winfinitricks] Using alternate C&C server: " + config.getString("altCncServer") + "(as set in wtrx.yml)");
                }
            } else {
                whitelistCheckUrl = "https://wtrx-cnc.windevsystems.us/check?sid=" + serverUUID.toString();
            }
            URL whitelistUrl = new URL(whitelistCheckUrl);
            HttpURLConnection conn = (HttpURLConnection)whitelistUrl.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null)
                result.append(line);
            rd.close();
            if (responseCode == 200) {
                this.isWhitelisted = Boolean.parseBoolean(result.toString());
            } else if (responseCode == 530) {
                Bukkit.getServer().getLogger().info("[Winfinitricks] Cloudflare Error - might not be Win's fault, but still ask her by emailing hwalker@windevsystems.us");
                Bukkit.getServer().getLogger().info("[Winfinitricks] Whitelist failure. Disabling.");
               // Bukkit.getServer().getPluginManager().disablePlugin((Plugin)this);
            } else if (responseCode == 500) {
                Bukkit.getServer().getLogger().info("[Winfinitricks] Win fucked up (Internal Server Error) - email hwalker@windevsystems.us");
                Bukkit.getServer().getLogger().info("[Winfinitricks] Whitelist failure. Disabling.");
              //  Bukkit.getServer().getPluginManager().disablePlugin((Plugin)this);
            } else if (responseCode == 502) {
                Bukkit.getServer().getLogger().info("[Winfinitricks] Bad gateway - The server's not on, email Win");
                Bukkit.getServer().getLogger().info("[Winfinitricks] Whitelist failure. Disabling.");
              //  Bukkit.getServer().getPluginManager().disablePlugin((Plugin)this);
            } else {
                Bukkit.getServer().getLogger().info("[Winfinitricks] Command and control server connection failure. Code " + responseCode);
                Bukkit.getServer().getLogger().info("[Winfinitricks] Whitelist failure. Disabling.");
               // Bukkit.getServer().getPluginManager().disablePlugin((Plugin)this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getServer().getLogger().info("[Winfinitricks] Whitelist failure. Disabling.");
            // Bukkit.getServer().getPluginManager().disablePlugin((Plugin)this);
        }
        // Stand-in replacement for licensing check while I reimplement it
        // I don't want to unwrap it because why fw something that alr works
        if (1 == 1) {
            Bukkit.getServer().getLogger().info("[Winfinitricks] Whitelisted!");
            String[] dimwit = new String[0];
            Bukkit.getServer().getLogger().info("           _     |  Winfinitricks [TWO!] v" + this.version);
            Bukkit.getServer().getLogger().info("   __ __ _| |_   |  Copyright 2024 Windev Systems Ltd");
            Bukkit.getServer().getLogger().info("   \\ V  V /  _|  |  UUID: " + serverUUID.toString());
            Bukkit.getServer().getLogger().info("    \\_/\\_/ \\__|  |  ");
            // RIP GitHub Enterprise Server Oct '23 - Dec '23
            //            if (config.contains("updateOnServerStart") && config.getBoolean("updateOnServerStart")) {
            //                Bukkit.getServer().getLogger().info("[Winfinitricks] Checking for updates...");
            //                try {
            //                    URL updateCheckUrl = new URL("https://raw.gh.windevsystems.us/hwalker/Winfinitricks/main/releases/version.yml");
            //                    BufferedReader rd = new BufferedReader(new InputStreamReader(updateCheckUrl.openStream()));
            //                    StringBuilder result = new StringBuilder();
            //                    String line;
            //                    while ((line = rd.readLine()) != null)
            //                        result.append(line);
            //                    rd.close();
            //                    String[] lines = result.toString().split("\n");
            //                    String versionLine = lines[1];
            //                    String majorLine = lines[2];
            //                    String minorLine = lines[3];
            //                    String patchLine = lines[4];
            //                    String[] versionLineSplit = versionLine.split(": ");
            //                    String[] majorLineSplit = majorLine.split(": ");
            //                    String[] minorLineSplit = minorLine.split(": ");
            //                    String[] patchLineSplit = patchLine.split(": ");
            //                    String upstreamVersion = versionLineSplit[1];
            //                    String upstreamMajor = majorLineSplit[1];
            //                    String upstreamMinor = minorLineSplit[1];
            //                    String upstreamPatch = patchLineSplit[1];
            //                    if (!upstreamVersion.equals(this.version)) {
            //                        Bukkit.getServer().getLogger().info("[Winfinitricks] Update available! Current version: " + this.version + ", latest version: " + upstreamVersion);
            //                            } else if (!upstreamMajor.equals(this.major)) {
            //                                Bukkit.getServer().getLogger().info("[Winfinitricks] This is a MAJOR release, meaning there are breaking changes! Auto-download has been disabled - please manually verify that you can upgrade, then run /wtrx-upgrade");
            //                                Bukkit.getServer().getLogger().info("[Winfinitricks] Error auto-upgrading: breaking changes. Continuing enable process.");
            //                            }
            //                        } else {
            //                            Bukkit.getServer().getLogger().info("[Winfinitricks] You have opted out of auto-downloading updates. Run /wtrx-upgrade to download the latest version.");
            //                        }
            //                    } else {
            //                        Bukkit.getServer().getLogger().info("[Winfinitricks] No updates available.");
            //                    }
            //                } catch (Exception e) {
            //                    e.printStackTrace();
            //                }
            //            }
            getCommand("fling").setExecutor(new us.windevsystems.winfinitrix2.FlingCommandExecutor());
            getCommand("ctb").setExecutor(new us.windevsystems.winfinitrix2.CtbCommandExecutor());
        } else {
            Bukkit.getServer().getLogger().severe("[Winfinitricks] This server is not authorized to run Winfinitricks.");
            Bukkit.getServer().getPluginManager().disablePlugin((Plugin)this);
        }
    }
}