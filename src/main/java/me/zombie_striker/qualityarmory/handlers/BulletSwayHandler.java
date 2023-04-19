package me.zombie_striker.qualityarmory.handlers;

import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.interfaces.IHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class BulletSwayHandler implements Listener, IHandler {

    private final HashMap<UUID, List<MoveEntry>> lastMoved = new HashMap<>();


    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Vector distanceMoved = event.getFrom().toVector().subtract(event.getTo().toVector());
        MoveEntry moveEntry = new MoveEntry(System.currentTimeMillis(),distanceMoved);
        if(!lastMoved.containsKey(event.getPlayer().getUniqueId())){
            lastMoved.put(event.getPlayer().getUniqueId(),new LinkedList<>());
        }
        List<MoveEntry> list = lastMoved.get(event.getPlayer().getUniqueId());
        list.add(moveEntry);
    }

    public Vector getSway(Player player){
        Vector v = new Vector(0,0.01,0);
        if(lastMoved.containsKey(player.getUniqueId()))
        for(MoveEntry moveEntry : lastMoved.get(player.getUniqueId())){
            Vector temp = moveEntry.getDistanceMoved();
            temp.multiply(1.0*(System.currentTimeMillis()-moveEntry.getTime())/1000.0);
            v.add(temp);
        }
        v=v.normalize();
        return v;
    }

    @Override
    public void init(QAMain main) {
        Bukkit.getPluginManager().registerEvents(this,main);
        new BukkitRunnable(){
            @Override
            public void run() {
                for(Map.Entry<UUID, List<MoveEntry>> entry: lastMoved.entrySet()){
                    UUID player = entry.getKey();
                    for(MoveEntry me : new ArrayList<>(entry.getValue())){
                        if(System.currentTimeMillis()-me.time>500){
                            entry.getValue().remove(me);
                        }
                    }
                }
            }
        }.runTaskTimer(main,1,1);
    }

    public class MoveEntry{
        private final long time;
        private final Vector distanceMoved;

        public MoveEntry(long time, Vector distanceMoved){
            this.distanceMoved  = distanceMoved;
            this.time = time;
        }

        public long getTime() {
            return time;
        }

        public Vector getDistanceMoved() {
            return distanceMoved;
        }
    }
}
