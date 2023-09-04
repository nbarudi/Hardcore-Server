package ca.bungo.hardcore.modules.generic;

import ca.bungo.hardcore.modules.Module;
import ca.bungo.hardcore.modules.types.interfaces.BuyableModule;

public class MagicToolsModules {

    public static class SimpleMagicTools extends Module implements BuyableModule {

        public SimpleMagicTools(String moduleName) {
            super(moduleName);
        }

        @Override
        public int getCost() {
            return 2;
        }

        @Override
        public String friendlyName() {
            return "Simple Magic Tools";
        }

        @Override
        public String friendlyDescription() {
            return "Gain the ability to craft Simple Magic Tools";
        }
    }

    public static class StandardMagicTools extends Module implements BuyableModule {

        public StandardMagicTools(String moduleName) {
            super(moduleName);
        }

        @Override
        public int getCost() {
            return 3;
        }

        @Override
        public String friendlyName() {
            return "Standard Magic Tools";
        }

        @Override
        public String friendlyDescription() {
            return "Gain the ability to craft Standard Magic Tools";
        }

        @Override
        public String depends() {
            return "SimpleMagicTools";
        }
    }

    public static class AdvancedMagicTools extends Module implements BuyableModule {

        public AdvancedMagicTools(String moduleName) {
            super(moduleName);
        }

        @Override
        public int getCost() {
            return 4;
        }

        @Override
        public String friendlyName() {
            return "Advanced Magic Tools";
        }

        @Override
        public String friendlyDescription() {
            return "Gain the ability to craft Advanced Magic Tools";
        }

        @Override
        public String depends() {
            return "StandardMagicTools";
        }
    }

}
