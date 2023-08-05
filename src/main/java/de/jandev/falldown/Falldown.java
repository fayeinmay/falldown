package de.jandev.falldown;

import de.jandev.falldown.command.CommandHandler;
import de.jandev.falldown.listener.*;
import de.jandev.falldown.model.Brewing;
import de.jandev.falldown.model.GameState;
import de.jandev.falldown.model.item.ItemCombinationEntity;
import de.jandev.falldown.model.item.ItemEntity;
import de.jandev.falldown.model.item.enchantment.EnchantmentEntity;
import de.jandev.falldown.model.map.MapEntity;
import de.jandev.falldown.model.map.MapLocationEntity;
import de.jandev.falldown.model.player.PlayerRole;
import de.jandev.falldown.model.player.PlayerType;
import de.jandev.falldown.sql.ExperienceRepository;
import de.jandev.falldown.sql.SQLHelper;
import de.jandev.falldown.task.*;
import de.jandev.falldown.utility.DefaultItemHelper;
import de.jandev.falldown.utility.MapHelper;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Falldown extends JavaPlugin {

    public static final String MAPSETTINGS = "/mapsettings.yml";
    public static final String ITEMSETTINGS = "/itemsettings.yml";
    private static final String CONFIG = "/config.yml";
    private static final Logger LOGGER = Bukkit.getLogger();
    private final List<BukkitTask> taskList = new ArrayList<>();
    private final List<ItemEntity> itemEntities = new ArrayList<>();
    private final List<ItemCombinationEntity> itemCombinationEntities = new ArrayList<>();
    private final List<Player> voted = new ArrayList<>();
    private final Map<Integer, Integer> votes = new HashMap<>(); // Votes for the map from the selection 1-3
    private final Map<Player, PlayerType> players = new HashMap<>();
    private final Map<Player, List<Brewing>> playerBrewings = new HashMap<>();
    private final Map<Player, Integer> spectatorIndex = new HashMap<>();
    private final List<Player> warnList = new ArrayList<>();
    private final Map<Player, Integer> invisibleList = new HashMap<>();
    private final Map<Player, Integer> shieldList = new HashMap<>();
    private final Map<Entity, Integer> wolfList = new HashMap<>();
    public boolean isSQL = false;
    private Scoreboard board = null;
    private ItemEntity superItem = null;
    private FileConfiguration mapConfiguration;
    private FileConfiguration itemConfiguration;
    private MapEntity currentMap = null; // Defines the current map with id as stored in the settings
    private GameState state = GameState.LOBBY;
    private MapHelper mapHelper;
    private ExperienceRepository experienceRepository;
    private Map<Entity, Boolean> crystals = new HashMap<>(); // unused, used

    public static boolean isStringInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static List<String> translateColorList(List<String> texts) {
        List<String> newTexts = new ArrayList<>();
        for (String text : texts) {
            newTexts.add(translateColor(text));
        }
        return newTexts;
    }

    public static String translateColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        ConfigurationSerialization.registerClass(MapEntity.class, "MapEntity");
        ConfigurationSerialization.registerClass(MapLocationEntity.class, "MapLocationEntity");
        ConfigurationSerialization.registerClass(ItemEntity.class, "ItemEntity");
        ConfigurationSerialization.registerClass(EnchantmentEntity.class, "EnchantmentEntity");
        ConfigurationSerialization.registerClass(ItemCombinationEntity.class, "ItemCombinationEntity");

        loadConfig();

        if (!getDataFolder().exists() && !getDataFolder().mkdir()) {
            LOGGER.log(Level.SEVERE, "{0} - Error while creating data directory, please investigate this before further using this plugin!", getDescription().getName());
        }
        createSettingFile(MAPSETTINGS);
        createSettingFile(ITEMSETTINGS);
        mapConfiguration = YamlConfiguration.loadConfiguration(new File(getDataFolder(), MAPSETTINGS));
        itemConfiguration = YamlConfiguration.loadConfiguration(new File(getDataFolder(), ITEMSETTINGS));

        if (itemConfiguration.getConfigurationSection("item") == null) {
            generateDefaultItems();
        }

        loadItems();
        loadSuperItem();
        loadItemCombinations();

        board = Bukkit.getScoreboardManager().getNewScoreboard();
        createScoreboard();

        if (mapConfiguration.getConfigurationSection("map") != null && !Objects.requireNonNull(mapConfiguration.getConfigurationSection("map")).getKeys(false).isEmpty()) {
            registerClasses(true);
            startLobby();
            resetMaps();
        } else {
            LOGGER.log(Level.SEVERE, "{0} - Falldown is not fully set-up, because there is no map configured. Please add a map, then restart/reload your server!", getDescription().getName());
            registerClasses(false);
        }
        LOGGER.log(Level.INFO, "{0} - Enabled!", getDescription().getName());
    }

    @Override
    public void onDisable() {
        super.onDisable();
        LOGGER.log(Level.INFO, "{0} - Disabled!", getDescription().getName());
    }

    public String getConfigString(String message) {
        return translateColor(getConfig().getString(message));
    }

    public GameState getState() {
        return this.state;
    }

    public MapHelper getMapHelper() {
        return this.mapHelper;
    }

    public ExperienceRepository getExperienceRepository() {
        return this.experienceRepository;
    }

    public List<ItemEntity> getItemEntities() {
        return this.itemEntities;
    }

    public List<ItemCombinationEntity> getItemCombinationEntities() {
        return this.itemCombinationEntities;
    }

    public List<Player> getVoted() {
        return this.voted;
    }

    public Map<Integer, Integer> getVotes() {
        return this.votes;
    }

    public MapEntity getCurrentMap() {
        return this.currentMap;
    }

    public void setCurrentMap(MapEntity map) {
        this.currentMap = map;
    }

    public Map<Player, PlayerType> getPlayers() {
        return this.players;
    }

    public Map<Player, List<Brewing>> getPlayerBrewings() {
        return this.playerBrewings;
    }

    public List<Player> getActivePlayers() {
        List<Player> activePlayers = new ArrayList<>();
        for (Map.Entry<Player, PlayerType> entry : getPlayers().entrySet()) {
            if (entry.getValue() == PlayerType.ACTIVE) {
                activePlayers.add(entry.getKey());
            }
        }
        return activePlayers;
    }

    public Map<Player, Integer> getSpectatorIndex() {
        return this.spectatorIndex;
    }

    public Map<Entity, Boolean> getCrystals() {
        return this.crystals;
    }

    public void setCrystals(Map<Entity, Boolean> crystals) {
        this.crystals = crystals;
    }

    public List<Player> getWarnList() {
        return this.warnList;
    }

    public Map<Player, Integer> getInvisibleList() {
        return this.invisibleList;
    }

    public Map<Player, Integer> getShieldList() {
        return this.shieldList;
    }

    public Map<Entity, Integer> getWolfList() {
        return this.wolfList;
    }

    public PlayerRole getPlayerRole(Player p) {
        if (p.isOp()) return PlayerRole.DEVELOPER;
        if (p.hasPermission("falldown.premium")) return PlayerRole.PREMIUM;
        if (p.hasPermission("falldown.youtuber")) return PlayerRole.YOUTUBER;
        if (p.hasPermission("falldown.moderator")) return PlayerRole.MODERATOR;
        if (p.hasPermission("falldown.administrator")) return PlayerRole.ADMINISTRATOR;
        if (p.hasPermission("falldown.owner")) return PlayerRole.OWNER;
        if (p.hasPermission("falldown.developer")) return PlayerRole.DEVELOPER;
        return PlayerRole.USER;
    }

    public ChatColor getPlayerColor(Player p) {
        if (getPlayerRole(p) == PlayerRole.DEVELOPER) return ChatColor.YELLOW;
        if (getPlayerRole(p) == PlayerRole.OWNER) return ChatColor.DARK_RED;
        if (getPlayerRole(p) == PlayerRole.ADMINISTRATOR) return ChatColor.RED;
        if (getPlayerRole(p) == PlayerRole.MODERATOR) return ChatColor.GREEN;
        if (getPlayerRole(p) == PlayerRole.YOUTUBER) return ChatColor.DARK_PURPLE;
        if (getPlayerRole(p) == PlayerRole.PREMIUM) return ChatColor.GOLD;

        return ChatColor.BLUE;
    }

    public ItemEntity getSuperItem() {
        return this.superItem;
    }

    public FileConfiguration getMapConfiguration() {
        return this.mapConfiguration;
    }

    public FileConfiguration getItemConfiguration() {
        return this.itemConfiguration;
    }

    public void saveMapConfiguration() {
        try {
            getMapConfiguration().save(new File(getDataFolder(), MAPSETTINGS));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("{0} - Error while updating map settings file, please investigate this before further using this plugin! Stacktrace: %s", e), getDescription().getName());
        }
    }

    public void saveItemConfiguration() {
        try {
            getItemConfiguration().save(new File(getDataFolder(), ITEMSETTINGS));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("{0} - Error while updating item settings file, please investigate this before further using this plugin! Stacktrace: %s", e), getDescription().getName());
        }
    }

    public void broadcast(String message) {
        for (Player p : getServer().getOnlinePlayers()) {
            p.sendMessage(message);
        }
    }

    public void setScoreboard(Player p) {
        p.setScoreboard(board);
    }

    public void updateScoreboard(int time) {
        DateTimeFormatter dtf = time >= 3600 ? DateTimeFormatter.ofPattern("HH:mm:ss") : DateTimeFormatter.ofPattern("mm:ss");
        String timeString = LocalTime.MIN.plusSeconds(time).format(dtf);
        Objects.requireNonNull(board.getObjective("FallDown")).setDisplayName(getConfigString("setting.scoreboardtitle").replace("%timer%", timeString));
    }

    public void startLobby() {
        // Alternatively use getScheduler().cancelAllTasks(), but this has more control over them
        this.state = GameState.LOBBY;
        killRunningTasks();
        taskList.add(new LobbyTask(this).runTaskTimer(this, 20L, 20L));
    }

    public void startDropPhase() {
        broadcast(getConfigString("message.task.gamestarts"));
        setCurrentMap(getMapHelper().evaluateWinner());

        setCrystals(spawnCrystals());
        getCurrentMap().getDrop().getLocation().getWorld().setTime(14000);

        for (Player p : getServer().getOnlinePlayers()) {
            if (isSQL) {
                try {
                    int exp = getExperienceRepository().getLevel(p.getUniqueId().toString());

                    p.setLevel(exp == -1 ? getConfig().getInt("setting.startlevel") : exp);
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Falldown - SQL Error: \n", e);
                }
            } else {
                p.setLevel(0);
            }
            p.getInventory().clear();
            p.teleport(getCurrentMap().getDrop().getLocation());
            p.getInventory().addItem(getStick());
        }

        startDrop();
    }

    public ItemStack getInfos() {
        ItemStack infos = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) infos.getItemMeta();
        bookMeta.setTitle("Infos");
        bookMeta.setAuthor("JanDev");
        List<String> pages = new ArrayList<>();
        pages.add(getConfigString("message.general.infobook"));
        pages.add(getConfigString("message.general.infobook2"));
        bookMeta.setPages(pages);
        infos.setItemMeta(bookMeta);
        return infos;
    }

    public ItemStack getMinimalSword() {
        ItemStack sword = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setDisplayName("A sword");
        meta.setLore(Collections.singletonList("Better than nothing..."));
        sword.setItemMeta(meta);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        return sword;
    }

    public ItemStack getVote() {
        ItemStack vote = new ItemStack(Material.BOOK);
        ItemMeta voteMeta = vote.getItemMeta();
        voteMeta.setDisplayName(ChatColor.BOLD + "Vote");
        vote.setItemMeta(voteMeta);
        return vote;
    }

    public ItemStack getCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName(ChatColor.AQUA + "Teleporter");
        compass.setItemMeta(compassMeta);
        return compass;
    }

    public void startDrop() {
        this.state = GameState.DROP;
        killRunningTasks();
        taskList.add(new DropTask(this).runTaskTimer(this, 20L, 1L));
        taskList.add(new DropTeleportTask(this).runTaskTimer(this, 20L, 5L));
    }

    public void startGracePeriod() {
        this.state = GameState.GRACE_PERIOD;
        killRunningTasks();
        taskList.add(new GracePeriodTask(this).runTaskTimerAsynchronously(this, 20L, 20L));
    }

    public void startInGame() {
        this.state = GameState.IN_GAME;
        killRunningTasks();
        taskList.add(new InGameTask(this).runTaskTimer(this, 20L, 20L));
    }

    public void startEnding() {
        this.state = GameState.ENDING;
        killRunningTasks();
        taskList.add(new EndingTask(this).runTaskTimer(this, 60L, 20L));
    }

    public void removeEntities(List<Entity> entities) {
        for (Entity current : entities) {
            if (current instanceof Item || current instanceof Mob || current instanceof EnderCrystal) {
                current.remove();
            }
        }
    }

    public void saveExperience(String uuid, int level) {
        try {
            getExperienceRepository().setLevel(uuid, level);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Falldown - SQL Error: \n", e);
        }
    }

    private ItemStack getStick() {
        ItemStack itemStack = new ItemStack(Material.STICK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(getConfigString("setting.stickname"));
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);
        itemStack.addUnsafeEnchantment(Enchantment.LUCK, 1);
        return itemStack;
    }

    private void createScoreboard() {
        Objective obj = board.registerNewObjective("FallDown", "playerKillCount", getConfigString("setting.scoreboardtitle").replace("%timer%", "0:00"));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score onlineName = obj.getScore(ChatColor.GRAY + "↓ Kills ↓");
        onlineName.setScore(100);
    }

    private void createSettingFile(String fileName) {
        File file = new File(getDataFolder(), fileName);
        try {
            if (file.createNewFile()) {
                // Ignore conditional logging, default logging level is info for bukkit
                LOGGER.log(Level.INFO, String.format("%s - Created %s settings file!", getDescription().getName(), fileName));
            } else {
                LOGGER.log(Level.INFO, String.format("%s - Loaded %s settings file!", getDescription().getName(), fileName));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("%s - Error while creating %s settings file, please investigate this before further using this plugin! Stacktrace: %s", getDescription().getName(), fileName, e));
        }
    }

    private void generateDefaultItems() {
        int ie = 1;
        for (ItemEntity e : DefaultItemHelper.getDefaultItems()) {
            itemConfiguration.set("item." + ie, e);
            ie++;
        }

        int ice = 1;
        for (ItemCombinationEntity e : DefaultItemHelper.getDefaultCombinations()) {
            itemConfiguration.set("itemcombination." + ice, e);
            ice++;
        }

        itemConfiguration.set("superitem", DefaultItemHelper.getSuperItem());
        saveItemConfiguration();
    }

    private void loadItems() {
        ConfigurationSection rootConfigurationSection = getItemConfiguration().getConfigurationSection("item");
        Set<String> itemSectionIds = Objects.requireNonNull(rootConfigurationSection).getKeys(false);

        for (String sectionId : itemSectionIds) {
            itemEntities.add((ItemEntity) rootConfigurationSection.get(sectionId));
        }
    }

    private void loadSuperItem() {
        this.superItem = (ItemEntity) getItemConfiguration().get("superitem");
    }

    private void loadItemCombinations() {
        ConfigurationSection rootConfigurationSection = getItemConfiguration().getConfigurationSection("itemcombination");
        Set<String> itemCombinationSectionIds = Objects.requireNonNull(rootConfigurationSection).getKeys(false);

        for (String sectionId : itemCombinationSectionIds) {
            itemCombinationEntities.add((ItemCombinationEntity) rootConfigurationSection.get(sectionId));
        }
    }

    private void killRunningTasks() {
        for (BukkitTask task : taskList) {
            task.cancel();
        }
        taskList.clear();
    }

    private void loadConfig() {
        if ((new File(getDataFolder(), CONFIG)).exists()) {
            getConfig().options().copyDefaults(true);
            LOGGER.log(Level.INFO, "{0} - Loaded configuration!", getDescription().getName());
        } else {
            saveDefaultConfig();
            getConfig().options().copyDefaults(true);
            LOGGER.log(Level.INFO, "{0} - Created new configuration!", getDescription().getName());
        }
    }

    private void registerClasses(boolean fullySetUp) {
        if (getConfig().getBoolean("sql.enable")) {
            try {
                this.experienceRepository = new ExperienceRepository(
                        new SQLHelper(getConfig().getString("sql.host"),
                                getConfig().getInt("sql.port"),
                                getConfig().getString("sql.user"),
                                getConfig().getString("sql.password"),
                                getConfig().getString("sql.database")));

                isSQL = true;
                LOGGER.log(Level.INFO, "Falldown - Successfully connected to SQL!");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Falldown - Cannot connect to SQL session...: \n", e);
            }
        }

        new CommandHandler(this);
        new FalldownListener(this);
        new ChatListener(this);
        new DamageListener(this);
        new InteractListener(this);
        new JoinListener(this);
        new QuitAndDeathListener(this);
        new RespawnListener(this);
        new PremiumJoinListener(this);

        if (fullySetUp) {
            this.mapHelper = new MapHelper(this);
        }
    }

    private Map<Entity, Boolean> spawnCrystals() {
        Map<Entity, Boolean> generatedCrystals = new HashMap<>();
        int crystalSize = getConfig().getInt("setting.crystalsize") * 3; // Space between crystals
        int offset = (crystalSize / 2);

        Location dropLocation = getCurrentMap().getDrop().getLocation();
        dropLocation.setX(dropLocation.getX() - offset);
        dropLocation.setY(dropLocation.getY() - 250); // 750: Player-Spawn, 500: Crystal-Spawn, 450: Reset
        dropLocation.setZ(dropLocation.getZ() - offset);

        for (int x = 0; x < crystalSize; x += 3) {
            for (int z = 0; z < crystalSize; z += 3) {
                if (Math.random() < getConfig().getDouble("setting.crystalspawnpercentage")) {
                    Location loc;
                    if (Math.random() < 0.5) {
                        loc = new Location(dropLocation.getWorld(),
                                dropLocation.getX() + x + Math.random(),
                                dropLocation.getY(),
                                dropLocation.getZ() + z + Math.random());
                    } else {
                        loc = new Location(dropLocation.getWorld(),
                                dropLocation.getX() + x - Math.random(),
                                dropLocation.getY(),
                                dropLocation.getZ() + z - Math.random());
                    }

                    generatedCrystals.put(dropLocation.getWorld().spawnEntity(loc, EntityType.ENDER_CRYSTAL), false);

                    if (Math.random() < 0.10) {
                        loc = new Location(loc.getWorld(),
                                loc.getX() + Math.random(),
                                loc.getY(),
                                loc.getZ() + Math.random());

                        generatedCrystals.put(dropLocation.getWorld().spawnEntity(loc, EntityType.ENDER_CRYSTAL), false);
                    }
                }
            }
        }
        return generatedCrystals;
    }

    private void resetMaps() {
        for (World world : Bukkit.getWorlds()) {
            world.getWorldBorder().reset();
        }

        List<String> worldsReset = new ArrayList<>();

        if (getConfig().getBoolean("backup.enabled")) {
            for (MapEntity map : mapHelper.getAvailableMaps()) {
                World world = map.getEndpoint().getLocation().getWorld();
                if (!worldsReset.contains(world.getName())) {
                    Path currentWorld = world.getWorldFolder().toPath();
                    Bukkit.unloadWorld(world.getName(), false);

                    try {
                        deleteDirectory(currentWorld.toFile());
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, String.format("{0} - Error while trying to delete world! Stacktrace: %s", e), getDescription().getName());
                    }

                    try {
                        copyFolder(currentWorld.resolveSibling(Objects.requireNonNull(getConfig().getString("backup.foldername"))), currentWorld, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, String.format("{0} - Error while trying to copy world! Stacktrace: %s", e), getDescription().getName());
                    }
                    new WorldCreator(world.getName()).createWorld();

                    worldsReset.add(world.getName());
                }
            }
        }
    }

    private void deleteDirectory(File directoryToBeDeleted) throws IOException {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }

        Files.delete(directoryToBeDeleted.toPath());
    }

    private void copyFolder(Path source, Path target, CopyOption... options) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                Files.createDirectories(target.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)), options);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
