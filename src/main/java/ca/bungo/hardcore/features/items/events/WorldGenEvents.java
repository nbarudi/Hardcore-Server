package ca.bungo.hardcore.features.items.events;

import ca.bungo.hardcore.Hardcore;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Random;

public class WorldGenEvents implements Listener {

    private final ItemStack travelersBook;

    int maxCharacters = 247;

    private final StringBuilder story = new StringBuilder();

    public WorldGenEvents(){
        this.travelersBook = Hardcore.instance.customItemManager.getCustomItem("travelersBook");


        createStory();
        createBook();
    }

    private void createStory(){
        story.append(
                "Chapter 1: My First Discovery... " +
                "Through my travels I have put a lot of work into discovering the mysteries of this world. " +
                "Over all there seems to be this very strange magical energy detectable to, even the most novice of, mages. " +
                "One of these strange phenomenon is the way Lightning interacts with Glass and Copper. " +
                "It seems like, with enough effort, you can in fact trap lightning inside of a piece of glass and use it for the creation of materials. " +
                "From what I have tried, placing a copper rod on top of a piece of glass was enough to show this phenomenon in action. " +
                "Although it required a lot of rain as I had to wait for a heavy storm to see any results. "
        ).append(
                "Chapter 2: Put into Practice... " +
                "Seems my method of collecting lightning works very consistently! As long as I have access to glass I can continue to gain more! " +
                "This has now lead to my first magical creation. The Flash Rod!. " +
                "By making use of the Bottles I obtained from my previous experiment I was able to harness the power of God himself! " +
                "However, this tool was not created easily. " +
                "I had to make use of another material I created previously which I will share in a future Chapter. " +
                "But, by using this tool, created at my worktable, I could replicate the Material required for future creations! " +
                "I wonder what else will come from this tool? "
        ).append(
                "Chapter 3: Magical Dusts... " +
                "Another item I have found by making use of different worldly materials is Covalent dusts. " +
                "These dusts are all crafted using different materials and have their own unique Yields. " +
                "I have only discovered 3 tiers of these dusts and I have named them Low, Medium, and High respectively. " +
                "This material is going to be very important to creating future items. Always try to keep a source on hand. "
        ).append(
                "Chapter 4: Mystical Fuel... " +
                "Attempting to make use of my tools strictly with my will power has found to be very difficult. " +
                "Hell I may have reduced my life span attempting to make use of the Flash Rod so far. " +
                "But, With a little bit of effort, I found out it is possible to create fuel that contains Mystical Energy! " +
                "By making use of normal fuel sources, like Coal, Blaze Rods, or Lava Buckets. " +
                "Then combining those fuel sources with Covalent Dusts I was able to obtain a magical fuel source that could power my tools! " +
                "For this reason, I will not allow others to make use of my tools without having access to this fuel source. "
        ).append(
                "Chapter 5: Controlling the Weather... " +
                "As it turns out, the Flash Rod has a lot more uses then just creating lightning. " +
                "Its power is strong enough to control the weather its self! " +
                "After traveling to the depths of this world to obtain some Blackened Stone I wanted to attempt a creation. " +
                "I decided to try to combine the Blacked Stone with my Flash Rod! At first, it seemed stable. " +
                "It did not take much beyond the rod overloading for me to make a 'Shocking' discovery.. " +
                "I did not give up though! I wanted to try to make this more stable, so I made use of the strongest Covalent dust I owned.. " +
                "That did the trick! By using this new machine I can control the weather its self!" +
                "Rain be no more! "
        ).append(
                "Chapter 6: A Superior Weapon... " +
                "I need a way to defend myself! " +
                "The things I have seen researching this world. It is Unnatural... " +
                "I need a weapon. A ranged weapon. Something that can pack a real punch. " +
                "I have a good idea in mind.. But I can't find a good base for it. " +
                "I need something that can power this weapon. Something with some real &lExplosive&r power..." +
                "I'm unsure what that item is yet, but once I find it.. I will finally be able to stay safe! "
        ).append(
                "Chapter 7: Assistance With Materials... " +
                "By making use of the magical energy within the dusts mentioned in a previous chapter " +
                "I was able to create, by modifying a compass, a method of locating material rich environments! " +
                "Creating this tool will require some level of crafting skill though. Perhaps I will add chapter " +
                "dedicated to teaching users how to obtain these skills... "
        ).append(
                "Chapter 8: Infusing Armors... " +
                "In a similar way of creating netherite armor, I discovered, by making use of special materials and the " +
                "strange magical fuel item, I was able to imbue my armor with magical properties! " +
                "By surrounding a netherite ingot with mystical fuel and also an associated material, for example sugar, " +
                "I was able to imbue my armor with a speed boost! This seems to scale based on the type of Covalent Dust you combine " +
                "with the gear in a Smithing Table! "
        ).append(
                "Chapter 9: Obtaining Skills... " +
                "It's about time I write down my discoveries into easily obtainable skills! " +
                "By creating a custom material table, any user can easily obtain access to the knowledge needed to create some of " +
                "the above mentioned items! Now that being said, these skills are not free.. It will require some effort on the user, " +
                "exploring the environment, fighting some of the creatures in the world, and even each other will all allow you to gain " +
                "experience, quickening your ability to learn the knowledge you seek! " +
                "By combining an observer, some redstone, and some green concrete into a machine, you can make use of the knowledge you gain!"
        ).append(
                "Chapter 10: Self Protection... " +
                "I realized that someone might be monitoring what I do... " +
                "I refuse to allow people close to me in general... " +
                "However, it seems that someone has been going through my things. " +
                "Just the other day, while I was searching for my collection of Covalent Dusts, " +
                "all of them were missing... Someone broke into my home, and stole my materials.. " +
                "With this information, I think its about time I find a way to guard my belongings! " +
                "After killing a large dark creature, known as an Enderman, it dropped one of its eyes. " +
                "After combining this eye with some Blaze Power, it gained a strange ability to locate some kind of structure? " +
                "I knew I could make use of this. By combining this item with some of my remaining Covalent Dust, " +
                "I was able to link the Eye to myself, instead of some random building! " +
                "With this my materials are safe! No one, other then me, can access my storage boxes! "
        ).append(
                "Chapter 11: Seems that wasn't enough... " +
                "Here I thought locking my storage boxes would be enough to deter my enemies... " +
                "That did not seem to be the case.. They still tried to access my home, and began blocking off my items! " +
                "This CANNOT be allowed! I will put a stop to this! What ever it takes! " +
                "By combining the lock I had created with some Higher Tier magic powder! I was able to protect my blocks! " +
                "and this items abilities are very potent. Much more then the lock its self! " +
                "I put this onto a wooden pole, and a flag to mark my territory! That has done it! " +
                "My home is now protected in a short range around my flag! I can finally feel safe! "
        );
    }
    private void createBook(){
        BookMeta.BookMetaBuilder builder = ((BookMeta)travelersBook.getItemMeta()).toBuilder();
        builder.title(("&eA Travelers Notes").convertToComponent());
        builder.author(("&eT&ch&ae &k&b0&50&70&b0&10&20&30&40").convertToComponent());

        StringBuilder page = new StringBuilder();
        String[] words = story.toString().split(" ");

        Random random = new Random();

        int characters = 0;
        for(String word : words){
            int mixer = random.nextInt(0, 5);
            if(mixer >= 3){
                characters += word.length() + 4;
                if(characters <= maxCharacters){
                    page.append("&k").append(word).append("&r ");
                }else{
                    builder.addPage(page.toString().convertToComponent());
                    page = new StringBuilder();
                    page.append("&k").append(word).append("&r ");
                    characters = word.length() + 4;
                }
            }else {
                characters += word.length();
                if(characters <= maxCharacters){
                    page.append(word).append(" ");
                }else{
                    builder.addPage(page.toString().convertToComponent());
                    page = new StringBuilder();
                    page.append(word).append(" ");
                    characters = word.length();
                }
            }
        }

        builder.addPage(page.toString().convertToComponent());

        travelersBook.setItemMeta(builder.build());
    }


    @EventHandler
    public void onWorldGen(LootGenerateEvent event){
        InventoryHolder holder = event.getInventoryHolder();
        Random random = new Random();
        if(holder instanceof Chest){
            int chance = random.nextInt(0, 100);
            if(chance <= 50){
                createBook();
                event.getLoot().add(travelersBook);
            }
        }
    }

}
