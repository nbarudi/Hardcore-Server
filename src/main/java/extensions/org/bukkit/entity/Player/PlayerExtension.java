package extensions.org.bukkit.entity.Player;

import ca.bungo.hardcore.Hardcore;
import ca.bungo.hardcore.managers.PlayerManager;
import ca.bungo.hardcore.types.HardcorePlayer;
import io.papermc.paper.adventure.PaperAdventure;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import net.kyori.adventure.text.Component;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.List;

@Extension
public class PlayerExtension {


    public static HardcorePlayer getHardcorePlayer(@This Player player){
        return PlayerManager.playerList.get(player.getUniqueId().toString());
    }

    public static void registerHardcorePlayer(@This Player player){
        HardcorePlayer hardcorePlayer = getHardcorePlayer(player);
        if(hardcorePlayer != null){
            hardcorePlayer.updatePlayer(player);
        }else{
            FileConfiguration configuration = Hardcore.instance.getConfig();
            if(configuration.getConfigurationSection(player.getUniqueId().toString()) == null || configuration.getConfigurationSection(player.getUniqueId().toString()).getKeys(false).size() <= 1){
                hardcorePlayer = new HardcorePlayer(player);
            }else{
                hardcorePlayer = (HardcorePlayer) configuration.get(player.getUniqueId() + ".hardcore-module");
            }
            assert hardcorePlayer != null;
            Bukkit.getServer().getPluginManager().registerEvents(hardcorePlayer, Hardcore.instance);
        }
        PlayerManager.playerList.put(player.getUniqueId().toString(), hardcorePlayer);
    }

    public static void sendFlare(@This Player player, Component title, Component description){
        //ToDo: MOJANG CHANGE TOO MUCH STUFF... IM DOOMED... Time to do more research!
        ServerPlayer serverPlayer = ((CraftPlayer)player).getHandle();

        DisplayInfo display = new DisplayInfo(
                CraftItemStack.asNMSCopy(new ItemStack(Material.PAPER)),
                PaperAdventure.asVanilla(title.appendNewline().append(description)),
                PaperAdventure.asVanilla(Component.text("Dummy Description")),
                null,
                FrameType.CHALLENGE,
                true,
                false,
                true
        );

        Map<String, Criterion<?>> criteria = new HashMap<>();

        AdvancementRewards advancementRewards = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], null);

        Criterion<ImpossibleTrigger.TriggerInstance> advancementCriteria =
                CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance());

        criteria.put("for_free", advancementCriteria);

        String[][] advRequirements;
        ArrayList<String[]> fixedRequirements = new ArrayList<>();
        fixedRequirements.add(new String[] {"for_free"});
        advRequirements = Arrays.stream(fixedRequirements.toArray()).toArray(String[][]::new);

        AdvancementRequirements requirements = new AdvancementRequirements(advRequirements);


        ResourceLocation internalKey = new ResourceLocation("hardcore.plugin", "notification");
        Map<ResourceLocation, AdvancementProgress> progressMap = new HashMap<>();
        AdvancementProgress progress = new AdvancementProgress();
        progress.update(requirements);
        progress.getCriterion("for_free").grant();
        progressMap.put(internalKey, progress); //Seems this works as intended. It is considered "Done"

        Advancement advancement = new Advancement(Optional.of(internalKey), Optional.of(display),
                advancementRewards, criteria, requirements, false);

        AdvancementHolder holder = new AdvancementHolder(internalKey, advancement); //The advancement seems to be made right
                                                                                    //When converting to bukkit it responds with the right content

        ClientboundUpdateAdvancementsPacket advancementsPacket =
                new ClientboundUpdateAdvancementsPacket(false, List.of(holder), new HashSet<>(), progressMap);
        serverPlayer.connection.send(advancementsPacket); //The client doesn't seem to update advancements for some reason?

        HashSet<ResourceLocation> rm = new HashSet<>();
        rm.add(internalKey);
        progressMap.clear();

        advancementsPacket = new ClientboundUpdateAdvancementsPacket(false, new ArrayList<>(), rm, progressMap);
        serverPlayer.connection.send(advancementsPacket);
    }

}
