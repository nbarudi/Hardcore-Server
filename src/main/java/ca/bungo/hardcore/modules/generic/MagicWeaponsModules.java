package ca.bungo.hardcore.modules.generic;

import ca.bungo.hardcore.modules.Module;
import ca.bungo.hardcore.modules.types.interfaces.BuyableModule;

public class MagicWeaponsModules {

    public static class SimpleMagicWeapons extends Module implements BuyableModule {

        public SimpleMagicWeapons(String moduleName) {
            super(moduleName);
        }

        @Override
        public int getCost() {
            return 2;
        }

        @Override
        public String friendlyName() {
            return "Simple Magic Weapons";
        }

        @Override
        public String friendlyDescription() {
            return "Gain the ability to craft Simple Magic Weapons";
        }
    }

    public static class StandardMagicWeapons extends Module implements BuyableModule {

        public StandardMagicWeapons(String moduleName) {
            super(moduleName);
        }

        @Override
        public int getCost() {
            return 3;
        }

        @Override
        public String friendlyName() {
            return "Standard Magic Weapons";
        }

        @Override
        public String friendlyDescription() {
            return "Gain the ability to craft Standard Magic Weapons";
        }

        @Override
        public String depends() {
            return "SimpleMagicWeapons";
        }
    }

    public static class AdvancedMagicWeapons extends Module implements BuyableModule {

        public AdvancedMagicWeapons(String moduleName) {
            super(moduleName);
        }

        @Override
        public int getCost() {
            return 4;
        }

        @Override
        public String friendlyName() {
            return "Advanced Magic Weapons";
        }

        @Override
        public String friendlyDescription() {
            return "Gain the ability to craft Advanced Magic Weapons";
        }

        @Override
        public String depends() {
            return "StandardMagicWeapons";
        }
    }


}
