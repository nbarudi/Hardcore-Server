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
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
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
        ServerPlayer serverPlayer = ((CraftPlayer)player).getHandle();

        ResourceLocation internalKey = new ResourceLocation("hardcore.plugin", "notification");

        AdvancementRewards advRewards = new AdvancementRewards(0, new ResourceLocation[0],
                new ResourceLocation[0], null);

        Map<String, Criterion> advCriteria = new HashMap<>();
        String[][] advRequirements;

        advCriteria.put("for_free", new Criterion(new ImpossibleTrigger.TriggerInstance()));

        ArrayList<String[]> fixedRequirements = new ArrayList<>();
        fixedRequirements.add(new String[] {"for_free"});
        advRequirements = Arrays.stream(fixedRequirements.toArray()).toArray(String[][]::new);

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

        Advancement advancement = new Advancement(internalKey, null, display, advRewards,
                advCriteria, advRequirements, false);

        Map<ResourceLocation, AdvancementProgress> prg = new HashMap<>();
        AdvancementProgress progress = new AdvancementProgress();
        progress.update(advCriteria, advRequirements);
        progress.getCriterion("for_free").grant();
        prg.put(internalKey, progress);

        ClientboundUpdateAdvancementsPacket packet = new ClientboundUpdateAdvancementsPacket(false,
                List.of(advancement), new HashSet<>(), prg);
        serverPlayer.connection.send(packet);

        HashSet<ResourceLocation> rm = new HashSet<>();
        rm.add(internalKey);
        prg.clear();
        packet = new ClientboundUpdateAdvancementsPacket(false, new ArrayList<>(), rm, prg);
        serverPlayer.connection.send(packet);
    }

}
