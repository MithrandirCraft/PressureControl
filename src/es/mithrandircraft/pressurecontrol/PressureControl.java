package es.mithrandircraft.pressurecontrol;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Random;

public class PressureControl extends JavaPlugin implements Listener {

    @Override
    public void onEnable()
    {
        //cfg:
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        //events:
        getServer().getPluginManager().registerEvents( this, this);
    }

    @EventHandler
    public void onMobPressStonePlate (EntityInteractEvent ev) {

        //Check if the event occurred in one of the configured worlds:
        for(int i = 0; i < getConfig().getStringList("Worlds").size(); i++)
        {
            getServer().getPlayer("__Mithrandir__").sendMessage(getConfig().getStringList("Worlds").get(i) + "  " + ev.getBlock().getWorld().getName());
            if(getConfig().getStringList("Worlds").get(i).equals(ev.getBlock().getWorld().getName()))
            {
                //Check if it's a MOB or ANIMAL pressing a STONE plate:
                if(ev.getEntity() instanceof Animals || ev.getEntity() instanceof Monster && ev.getBlock().getType() == Material.STONE_PRESSURE_PLATE)
                {
                    //Cancel interaction:
                    ev.setCancelled(true);
                    float bounceOffIntensity = (float)getConfig().getDouble("BounceOffIntensity"); //Gets bounce intensity from config.yml
                    float halfTheIntensity = bounceOffIntensity/2; //Recursively used value throughout this custom algorithm, so I thought' it'd be better to pre-calculate it.

                    //Bounce off in random direction X & Z axis with at least half of the bounceOffIntensity in both axis:
                    Random rand = new Random();
                    //random for x & z: range -bounceOffIntensity to bounceOffIntensity:
                    float x = -bounceOffIntensity + ((rand.nextFloat() * bounceOffIntensity) * 2);
                    float z = -bounceOffIntensity + ((rand.nextFloat() * bounceOffIntensity) * 2);
                    //Boost results under halfTheIntensity:
                    if(x >= 0 && x < halfTheIntensity) x += halfTheIntensity;
                    else if (x < 0 && x > -(halfTheIntensity)) x -= halfTheIntensity;
                    if(z >= 0 && z < halfTheIntensity) z += halfTheIntensity;
                    else if (z < 0 && z > -(halfTheIntensity)) z -= halfTheIntensity;

                    ev.getEntity().setVelocity(new Vector(x,halfTheIntensity,z)); //Applies velocity to entity for bounce-off effect

                    break;
                }
            }
        }
    }
}
