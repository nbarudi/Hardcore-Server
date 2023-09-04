package ca.bungo.hardcore.modules.generic;

import ca.bungo.hardcore.modules.Module;
import ca.bungo.hardcore.modules.types.interfaces.BuyableModule;

public class BaseUpgradeModule extends Module implements BuyableModule {

    public BaseUpgradeModule() {
        super("BaseModule");
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String depends() {
        return null;
    }

    @Override
    public String friendlyName() {
        return "Unlock Skills";
    }

    @Override
    public String friendlyDescription() {
        return "Gain access to the Skill tree and upgrade your abilities.";
    }
}
