package me.justinjaques.flowerthingy;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.WaterAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class FlowerThingy extends WaterAbility implements AddonAbility {
    static Color color  = Color.PURPLE;
    private static long COOLDOWN;
    private static long DURATION;
    private static long DELAY;
    private static double SPREAD;
    private long nextFlowerTime;
    private Listener listener;
    static String path = "ExtraAbilities.Viridescent_.Sprite.FlowerThingy.";
    private Permission perm;
    private Location pLocation;
    private long currentTime;
    List<TempBlock> Flowers;

    public TempBlock tempBlock;
    public Block flower;

    private void setFields() {
        COOLDOWN = ConfigManager.defaultConfig.get().getLong(path+"COOLDOWN");
        DURATION = ConfigManager.defaultConfig.get().getLong(path+"DURATION");
        SPREAD = ConfigManager.defaultConfig.get().getLong(path+"SPREAD");
        DELAY = ConfigManager.defaultConfig.get().getLong(path+"DELAY");
    }

    public BlockData getRandomFlower(){
        switch (ThreadLocalRandom.current().nextInt(0, 5)){
            case 0:
                return Material.CORNFLOWER.createBlockData();
            case 1:
                return Material.ALLIUM.createBlockData();
            case 2:
                return Material.PINK_TULIP.createBlockData();
            case 3:
                return Material.BLUE_ORCHID.createBlockData();
            case 4:
                return Material.WHITE_TULIP.createBlockData();
        }
        return null;

    }

    private void removeWithCooldown() {
        remove();
        bPlayer.addCooldown(this);
        for (TempBlock b : Flowers){
            b.revertBlock();
        }
        Flowers.clear();
    }



    public FlowerThingy(Player player) {
        super(player);
        setFields();
        Flowers = new ArrayList<>();
        if(!bPlayer.isOnCooldown(this)) {
            start();
        }
    }

    @Override
    public void load() {
        listener = new FlowerListener();
        ProjectKorra.plugin.getServer().getPluginManager().registerEvents(listener, ProjectKorra.plugin);
        ConfigManager.defaultConfig.get().addDefault(path+"COOLDOWN", 4000);
        ConfigManager.defaultConfig.get().addDefault(path+"DELAY", 2);
        ConfigManager.defaultConfig.get().addDefault(path+"SPREAD", 4);
        ConfigManager.defaultConfig.get().addDefault(path+"DURATION", 4000);
        perm = new Permission("bending.ability.FlowerThingy");
        ProjectKorra.plugin.getServer().getPluginManager().addPermission(perm);
        ConfigManager.defaultConfig.save();

    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(listener);
        ProjectKorra.plugin.getServer().getPluginManager().removePermission(perm);
    }

    @Override
    public String getAuthor() {
        return color + "Viridescent";
    }

    @Override
    public String getVersion() {
        return color + "1.0.0";
    }

    @Override
    public void progress() {
        pLocation = player.getLocation();
        currentTime = System.currentTimeMillis();

        if(currentTime - getStartTime() >= DURATION) {
            removeWithCooldown();
        } else if(!player.isSneaking() && currentTime - getStartTime() < DURATION / 2) {
            removeWithCooldown();
        } else {
            if (currentTime >= nextFlowerTime) {
                nextFlowerTime = currentTime + DELAY;
                double xSpread = ThreadLocalRandom.current().nextDouble(-SPREAD, SPREAD);
                double zSpread = ThreadLocalRandom.current().nextDouble(-SPREAD, SPREAD);
                pLocation.add(xSpread, 0, zSpread);
                Flowers.add(new TempBlock(pLocation.getBlock(), getRandomFlower(), DURATION));
                ParticleEffect.REDSTONE.display(pLocation, 10, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(255, 192, 203), (float) 1.2));
                player.playSound(pLocation, Sound.BLOCK_BREWING_STAND_BREW, 2.0F, 1.0F);
            }
        }

    }

    @Override
    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public long getCooldown() {
        return COOLDOWN;
    }

    @Override
    public String getName() {
        return "FlowerThingy";
    }

    @Override
    public Location getLocation() {
        return null;
    }
}
