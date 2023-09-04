package ca.bungo.hardcore.events;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.features.bosses.Boss;
import ca.bungo.hardcore.types.timings.TickTimer;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class TickerHandlerEvents implements Listener {


    int tickCount = 0;

    @EventHandler
    public void onTick(ServerTickEndEvent event){
        tickCount++;
        Map<Method, Object> tickInstance = Bukkit.getTickingMethods();

        for(Method method : tickInstance.keySet()){
            Object executor = tickInstance.get(method);

            if(executor instanceof Boss boss && boss.getSelf() == null) continue;

            TickTimer timer = method.getDeclaredAnnotation(TickTimer.class);
            int ticks = timer.ticks();
            if(tickCount%ticks == 0){
                try{
                    method.setAccessible(true);
                    method.invoke(executor);
                    method.setAccessible(false);
                } catch(InvocationTargetException | IllegalAccessException exception){
                    Hardcore.instance.getLogger().warning("Failed to execute timer: " + method.getName());
                }
            }
        }
    }

}
