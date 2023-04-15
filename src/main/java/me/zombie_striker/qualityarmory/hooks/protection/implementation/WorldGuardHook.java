package me.zombie_striker.qualityarmory.hooks.protection.implementation;

import me.zombie_striker.qualityarmory.hooks.protection.ProtectionHook;
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
    private final IWrappedFlag<WrappedState> blockBreak;

    public WorldGuardHook() {
        worldGuard = WorldGuardWrapper.getInstance();
        pvp = worldGuard.getFlag("PVP", WrappedState.class).orElse(createFlag("PVP"));
        explosion = worldGuard.getFlag("OTHER-EXPLOSION", WrappedState.class).orElse(createFlag("OTHER-EXPLOSION"));
        blockBreak = worldGuard.getFlag("BLOCK-BREAK", WrappedState.class).orElse(createFlag("BLOCK-BREAK"));
    }

    @Override
    public boolean canPvp(@NotNull Location location) {
        for (IWrappedRegion k : worldGuard.getRegions(location)) {
            Object wrappedState = k.getFlag(pvp).orElse(WrappedState.ALLOW);
            if(wrappedState.getClass().equals(Optional.class)) {
                wrappedState = ((Optional<WrappedState>) wrappedState).orElse(WrappedState.ALLOW);
            }
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

    @Override
    public boolean canBreak(Location location) {
        for (IWrappedRegion k : worldGuard.getRegions(location)) {
            WrappedState wrappedState = k.getFlag(blockBreak).orElse(WrappedState.ALLOW);
            if (wrappedState.equals(WrappedState.DENY)) return false;
        }

        return true;
    }

    private IWrappedFlag<WrappedState> createFlag(String name) {
        return new IWrappedFlag<WrappedState>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Optional<WrappedState> getDefaultValue() {
                return Optional.of(WrappedState.ALLOW);
            }
        };
    }
}
