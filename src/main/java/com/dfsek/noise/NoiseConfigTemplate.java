package com.dfsek.noise;

import com.dfsek.tectonic.annotations.Value;
import com.dfsek.tectonic.config.ConfigTemplate;
import com.dfsek.terra.generation.config.NoiseBuilder;

@SuppressWarnings("unused")
public class NoiseConfigTemplate implements ConfigTemplate {
    @Value(".")
    private NoiseBuilder builder;

    public NoiseBuilder getBuilder() {
        return builder;
    }
}
