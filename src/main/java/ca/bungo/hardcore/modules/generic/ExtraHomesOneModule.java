package ca.bungo.hardcore.modules.generic;

import ca.bungo.hardcore.modules.Module;
import ca.bungo.hardcore.modules.types.interfaces.BuyableModule;

public class ExtraHomesOneModule extends Module implements BuyableModule {
    public ExtraHomesOneModule(String moduleName) {
        super(moduleName);
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String depends() {
        return "HomeCommandModule";
    }

    @Override
    public String friendlyName() {
        return "Unlock 1 Extra Home";
    }

    @Override
    public String friendlyDescription() {
        return "Gain Access to 1 Extra Home";
    }
}
