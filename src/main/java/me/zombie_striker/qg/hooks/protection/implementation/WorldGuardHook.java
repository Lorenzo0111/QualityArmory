package me.zombie_striker.qg.hooks.protection.implementation;

import me.zombie_striker.qg.hooks.protection.ProtectionHook;
import org.bukkit.Location;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.flag.WrappedState;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WorldGuardHook implements ProtectionHook {
    private final WorldGuardWrapper worldGuard;
    private final IWrappedFlag<WrappedState> pvp;
    private final IWrappedFlag<WrappedState> explosion;

    public WorldGuardHook() {
        worldGuard = WorldGuardWrapper.getInstance();
        IWrappedFlag<WrappedState> allowFlag = new IWrappedFlag<WrappedState>() {
            @Override
            public String getName() {
                return "ALLOW";
            }

            @Override
            public Optional<WrappedState> getDefaultValue() {
                return Optional.of(WrappedState.ALLOW);
            }
        };

        pvp = worldGuard.getFlag("PVP", WrappedState.class).orElse(allowFlag);
        explosion = worldGuard.getFlag("OTHER_EXPLOSION", WrappedState.class).orElse(allowFlag);
    }

    @Override
    public boolean canPvp(@NotNull Location location) {
        for (IWrappedRegion k : worldGuard.getRegions(location)) {
            WrappedState wrappedState = k.getFlag(pvp).orElse(WrappedState.ALLOW);
            if (wrappedState.equals(WrappedState.DENY)) return false;
        }

        return true;
    }

    @Override
    public boolean canExplode(@NotNull Location location) {
        for (IWrappedRegion k : worldGuard.getRegions(location)) {
            WrappedState wrappedState = k.getFlag(explosion).orElse(WrappedState.ALLOW);
            if (wrappedState.equals(WrappedState.DENY)) return false;
        }

        return true;
    }
}
