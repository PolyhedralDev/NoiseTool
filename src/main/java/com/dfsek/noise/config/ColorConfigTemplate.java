package com.dfsek.noise.config;

import com.dfsek.tectonic.api.config.template.ConfigTemplate;
import com.dfsek.tectonic.api.config.template.annotations.Default;
import com.dfsek.tectonic.api.config.template.annotations.Value;
import com.dfsek.terra.api.util.collection.ProbabilityCollection;

public class ColorConfigTemplate implements ConfigTemplate {
    @Value("colors")
    private ProbabilityCollection<Integer> colors;

    @Value("enable")
    @Default
    private boolean enable = false;

    public boolean enable() {
        return enable;
    }

    public ProbabilityCollection<Integer> getColors() {
        return colors;
    }
}
