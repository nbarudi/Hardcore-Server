package extensions.org.bukkit.Bukkit;

import ca.bungo.hardcore.types.timings.TickTimer;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Extension
public class BukkitExtension  {

  private static final Map<Method, Object> tickingMethods = new HashMap<>();

  @Extension
  public static void registerTickTimer(Object tickerInstance){
    Class<?> objectClass = tickerInstance.getClass();

    for(Method method : objectClass.getDeclaredMethods()){
      TickTimer timer = method.getAnnotation(TickTimer.class);
      if(timer == null) continue;
      tickingMethods.put(method, tickerInstance);
    }
  }

  @Extension
  public static Map<Method, Object> getTickingMethods(){
    return tickingMethods;
  }



}