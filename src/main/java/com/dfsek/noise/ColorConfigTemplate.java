package com.dfsek.noise;

import com.dfsek.tectonic.annotations.Default;
import com.dfsek.tectonic.annotations.Value;
import com.dfsek.tectonic.config.ConfigTemplate;
import com.dfsek.terra.api.math.ProbabilityCollection;

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
