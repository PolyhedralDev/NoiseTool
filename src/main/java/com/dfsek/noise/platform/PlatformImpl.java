package com.dfsek.noise.platform;

import com.dfsek.terra.AbstractPlatform;
import com.dfsek.terra.api.handle.ItemHandle;
import com.dfsek.terra.api.handle.WorldHandle;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class PlatformImpl extends AbstractPlatform {
    public PlatformImpl() {
        loadAddons();
    }

    @Override
    public boolean reload() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull String platformName() {
        return "Noise Tool";
    }

    @Override
    public @NotNull WorldHandle getWorldHandle() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull File getDataFolder() {
        return new File(".");
    }

    @Override
    public @NotNull ItemHandle getItemHandle() {
        throw new UnsupportedOperationException();
    }
}
