package ca.bungo.hardcore.modules.generic;

import ca.bungo.hardcore.modules.Module;
import ca.bungo.hardcore.modules.types.interfaces.BuyableModule;

public class LowerHomesCooldownTwoModule extends Module implements BuyableModule {
    public LowerHomesCooldownTwoModule(String moduleName) {
        super(moduleName);
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String depends() {
        return "LowerHomesCooldownOneModule";
    }

    @Override
    public String friendlyName() {
        return "Reduce Home Cooldown Time Again";
    }

    @Override
    public String friendlyDescription() {
        return "Reduce your Home teleport cooldown time by another 5 seconds";
    }
}
