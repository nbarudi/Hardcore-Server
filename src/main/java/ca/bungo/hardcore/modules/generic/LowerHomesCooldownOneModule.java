package ca.bungo.hardcore.modules.generic;

import ca.bungo.hardcore.modules.Module;
import ca.bungo.hardcore.modules.types.interfaces.BuyableModule;

public class LowerHomesCooldownOneModule extends Module implements BuyableModule {
    public LowerHomesCooldownOneModule(String moduleName) {
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
        return "Reduce Home Cooldown Time";
    }

    @Override
    public String friendlyDescription() {
        return "Reduce your Home teleport cooldown time by 5 seconds";
    }
}
