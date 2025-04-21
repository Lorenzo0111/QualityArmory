package me.zombie_striker.qg.hooks.protection.implementation;

import java.util.Optional;

import org.bukkit.Location;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.flag.WrappedState;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import org.jetbrains.annotations.NotNull;

import me.zombie_striker.qg.hooks.protection.ProtectionHook;

public class WorldGuardHook implements ProtectionHook {
    private final WorldGuardWrapper worldGuard;
    private final IWrappedFlag<WrappedState> pvp;
    private final IWrappedFlag<WrappedState> explosion;
    private final IWrappedFlag<WrappedState> blockBreak;

    public WorldGuardHook() {
        this.worldGuard = WorldGuardWrapper.getInstance();
        this.pvp = this.worldGuard.getFlag("PVP", WrappedState.class).orElse(this.createFlag("PVP"));
        this.explosion = this.worldGuard.getFlag("OTHER-EXPLOSION", WrappedState.class).orElse(this.createFlag("OTHER-EXPLOSION"));
        this.blockBreak = this.worldGuard.getFlag("BLOCK-BREAK", WrappedState.class).orElse(this.createFlag("BLOCK-BREAK"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean canPvp(@NotNull final Location location) {
        for (final IWrappedRegion k : this.worldGuard.getRegions(location)) {
            Object wrappedState = k.getFlag(this.pvp).orElse(WrappedState.ALLOW);
            if (wrappedState.getClass().equals(Optional.class)) {
                wrappedState = ((Optional<WrappedState>) wrappedState).orElse(WrappedState.ALLOW);
            }
            if (wrappedState.equals(WrappedState.DENY))
                return false;
        }

        return true;
    }

    @Override
    public boolean canExplode(@NotNull final Location location) {
        for (final IWrappedRegion k : this.worldGuard.getRegions(location)) {
            final WrappedState wrappedState = k.getFlag(this.explosion).orElse(WrappedState.ALLOW);
            if (wrappedState.equals(WrappedState.DENY))
                return false;
        }

        return true;
    }

    @Override
    public boolean canBreak(final Location location) {
        for (final IWrappedRegion k : this.worldGuard.getRegions(location)) {
            final WrappedState wrappedState = k.getFlag(this.blockBreak).orElse(WrappedState.ALLOW);
            if (wrappedState.equals(WrappedState.DENY))
                return false;
        }

        return true;
    }

    private IWrappedFlag<WrappedState> createFlag(final String name) {
        return new IWrappedFlag<WrappedState>() {
            @Override
            public String getName() { return name; }

            @Override
            public Optional<WrappedState> getDefaultValue() { return Optional.of(WrappedState.ALLOW); }
        };
    }
}
