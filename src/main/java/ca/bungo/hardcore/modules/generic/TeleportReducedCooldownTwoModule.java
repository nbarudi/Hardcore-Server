package ca.bungo.hardcore.modules.generic;

import ca.bungo.hardcore.modules.Module;
import ca.bungo.hardcore.modules.types.interfaces.BuyableModule;

public class TeleportReducedCooldownTwoModule extends Module implements BuyableModule {
    public TeleportReducedCooldownTwoModule(String moduleName) {
        super(moduleName);
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String depends() {
        return "TeleportReducedCooldownOneModule";
    }

    @Override
    public String friendlyName() {
        return "Reduce Teleport Time Again";
    }

    @Override
    public String friendlyDescription() {
        return "Reduce your teleport wait time to 5 seconds! (Base 10 seconds)";
    }
}
