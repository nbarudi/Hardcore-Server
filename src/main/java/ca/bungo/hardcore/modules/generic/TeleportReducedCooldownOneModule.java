package ca.bungo.hardcore.modules.generic;

import ca.bungo.hardcore.modules.Module;
import ca.bungo.hardcore.modules.types.interfaces.BuyableModule;

public class TeleportReducedCooldownOneModule extends Module implements BuyableModule {
    public TeleportReducedCooldownOneModule(String moduleName) {
        super(moduleName);
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String friendlyName() {
        return "Reduce Teleport Time";
    }

    @Override
    public String friendlyDescription() {
        return "Reduce your teleport wait time to 7 seconds! (Base 10 seconds)";
    }
}
