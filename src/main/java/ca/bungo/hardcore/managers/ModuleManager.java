package ca.bungo.hardcore.managers;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.modules.Module;
import ca.bungo.hardcore.modules.block.custom.*;
import ca.bungo.hardcore.modules.command.FeedCommandModule;
import ca.bungo.hardcore.modules.command.FixCommandModule;
import ca.bungo.hardcore.modules.command.HomeCommandModule;
import ca.bungo.hardcore.modules.event.RespawnEventModule;
import ca.bungo.hardcore.modules.generic.*;
import ca.bungo.hardcore.modules.item.armorModifiers.ArmorModifiers;
import ca.bungo.hardcore.modules.item.utilityItems.BiometricReader;
import ca.bungo.hardcore.modules.types.classes.CommandModule;
import ca.bungo.hardcore.modules.types.classes.CustomBlockModule;
import ca.bungo.hardcore.modules.types.classes.EventModule;
import ca.bungo.hardcore.modules.types.classes.ItemModule;
import ca.bungo.hardcore.modules.types.interfaces.BuyableModule;
import ca.bungo.hardcore.modules.types.interfaces.CraftableModule;
import ca.bungo.hardcore.modules.item.funItems.LightningRod;
import ca.bungo.hardcore.modules.item.recipeItems.ChargedHeart;
import ca.bungo.hardcore.modules.item.recipeItems.CovalenceDusts;
import ca.bungo.hardcore.modules.item.recipeItems.MysticalFuel;
import ca.bungo.hardcore.modules.item.utilityItems.OreLocators;
import ca.bungo.hardcore.modules.item.weapons.HandCannon;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;

import java.util.*;

public class ModuleManager {

    private final Map<String, Module> moduleMap;

    private final Map<String, ItemModule> itemModuleMap;
    private final Map<String, CustomBlockModule> blockModuleMap;
    private final Map<String, CommandModule> commandModuleMap;
    private final Map<String, EventModule> eventModuleMap;

    public ModuleManager(){
        this.moduleMap = new HashMap<>();
        this.itemModuleMap = new HashMap<>();
        this.commandModuleMap = new HashMap<>();
        this.eventModuleMap = new HashMap<>();
        this.blockModuleMap = new HashMap<>();

        this.loadItemModules();
        this.loadCommandModules();
        this.loadEventModules();
        this.loadCustomBlockModules();
        this.loadGenericModules();


        for(Module module : moduleMap.values()){
            if(module instanceof CraftableModule){
                if(((CraftableModule) module).getMultiRecipe() != null)
                    for(Recipe recipe : ((CraftableModule) module).getMultiRecipe())
                        Bukkit.getServer().addRecipe(recipe);
                Bukkit.getServer().addRecipe(((CraftableModule) module).getItemRecipe());
            }
        }

    }

    private void loadGenericModules(){
        moduleMap.put("BaseModule", new BaseUpgradeModule());

        moduleMap.put("SimpleMagicTools", new MagicToolsModules.SimpleMagicTools("SimpleMagicTools"));
        moduleMap.put("StandardMagicTools", new MagicToolsModules.StandardMagicTools("StandardMagicTools"));
        moduleMap.put("AdvancedMagicTools", new MagicToolsModules.AdvancedMagicTools("AdvancedMagicTools"));

        moduleMap.put("ExtraHomesOneModule", new ExtraHomesOneModule("ExtraHomesOneModule"));
        moduleMap.put("ExtraHomesTwoModule", new ExtraHomesTwoModule("ExtraHomesTwoModule"));
        moduleMap.put("LowerHomesCooldownOneModule", new LowerHomesCooldownOneModule("LowerHomesCooldownOneModule"));
        moduleMap.put("LowerHomesCooldownTwoModule", new LowerHomesCooldownTwoModule("LowerHomesCooldownTwoModule"));

        moduleMap.put("TeleportReducedCooldownOneModule", new TeleportReducedCooldownOneModule("TeleportReducedCooldownOneModule"));
        moduleMap.put("TeleportReducedCooldownTwoModule", new TeleportReducedCooldownTwoModule("TeleportReducedCooldownTwoModule"));

        moduleMap.put("SimpleMagicWeapons", new MagicWeaponsModules.SimpleMagicWeapons("SimpleMagicWeapons"));
        moduleMap.put("StandardMagicWeapons", new MagicWeaponsModules.StandardMagicWeapons("StandardMagicWeapons"));
        moduleMap.put("AdvancedMagicWeapons", new MagicWeaponsModules.AdvancedMagicWeapons("AdvancedMagicWeapons"));

    }

    private void loadItemModules(){
        itemModuleMap.put("LowCovalanceDust", new CovalenceDusts.LowCovalenceDust("LowCovalanceDust"));
        itemModuleMap.put("MedCovalanceDust", new CovalenceDusts.MedCovalenceDust("MedCovalanceDust"));
        itemModuleMap.put("HighCovalanceDust", new CovalenceDusts.HighCovalenceDust("HighCovalanceDust"));
        itemModuleMap.put("MysticalFuel", new MysticalFuel("MysticalFuel"));
        itemModuleMap.put("OreLocator1", new OreLocators.Tier1("OreLocator1"));
        itemModuleMap.put("OreLocator2", new OreLocators.Tier2("OreLocator2"));
        itemModuleMap.put("OreLocator3", new OreLocators.Tier3("OreLocator3"));
        itemModuleMap.put("LightningRod", new LightningRod("LightningRod"));
        itemModuleMap.put("ChargedHeart", new ChargedHeart("ChargedHeart"));
        itemModuleMap.put("HandCannon", new HandCannon("HandCannon"));

        itemModuleMap.put("BiometricReader", new BiometricReader("BiometricReader"));

        itemModuleMap.put("ArmorSpeedModifier", new ArmorModifiers.ArmorSpeedModifier("ArmorSpeedModifier"));
        itemModuleMap.put("ArmorKnockbackResistModifier", new ArmorModifiers.ArmorKnockbackResistModifier("ArmorKnockbackResistModifier"));

        for(ItemModule module : itemModuleMap.values()){
            Bukkit.getPluginManager().registerEvents(module, Hardcore.instance);
        }

        moduleMap.putAll(itemModuleMap);
    }

    private void loadCustomBlockModules(){
        blockModuleMap.put("WeatherBlock", new WeatherBlock("WeatherBlock"));
        blockModuleMap.put("SuperClockBlock", new SuperClockBlock("SuperClockBlock"));
        blockModuleMap.put("HarvesterBlock", new HarvesterBlock("HarvesterBlock"));
        blockModuleMap.put("PlayerModTableBlock", new PlayerModTableBlock("PlayerModTableBlock"));
        blockModuleMap.put("ObjectLockBlock", new ObjectLockBlock("ObjectLockBlock"));
        blockModuleMap.put("FlagClaimBlock", new FlagClaimBlock("FlagClaimBlock"));

        for(CustomBlockModule module : blockModuleMap.values()){
            Bukkit.getPluginManager().registerEvents(module, Hardcore.instance);
        }

        moduleMap.putAll(blockModuleMap);
    }

    private void loadCommandModules(){

        commandModuleMap.put("FeedCommandModule", new FeedCommandModule("FeedCommandModule", "feed"));
        commandModuleMap.put("HomeCommandModule", new HomeCommandModule("HomeCommandModule"));
        commandModuleMap.put("FixCommandModule", new FixCommandModule("FixCommandModule", "fix"));

        for(CommandModule commandModule : commandModuleMap.values()){
            Hardcore.instance.getServer().getCommandMap().register(commandModule.getModuleName(), commandModule.getModuleCommand());
        }

        moduleMap.putAll(commandModuleMap);
    }

    private void loadEventModules(){

        eventModuleMap.put("RespawnEventModule", new RespawnEventModule("RespawnEventModule"));

        for(EventModule module : eventModuleMap.values()){
            Bukkit.getServer().getPluginManager().registerEvents(module, Hardcore.instance);
        }

        moduleMap.putAll(eventModuleMap);
    }


    public Module getModuleByName(String name){
        for(Module module : this.moduleMap.values()){
            if(module.getModuleName().equalsIgnoreCase(name))
                return module;
        }
        return null;
    }

    public void saveCustomBlocks(){
        for(CustomBlockModule module : blockModuleMap.values()){
            module.saveCustomBlocks();
        }
    }

    public Set<String> getModuleNames(){
        return this.moduleMap.keySet();
    }
    public List<Module> getBuyableModules(){
        List<Module> buyableModules = new ArrayList<>();
        for(Module module : this.moduleMap.values()){
            if(module instanceof BuyableModule)
                buyableModules.add(module);
        }
        return buyableModules;
    }

}
