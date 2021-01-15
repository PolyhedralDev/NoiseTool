package com.dfsek.noise;

import com.dfsek.tectonic.annotations.Value;
import com.dfsek.tectonic.config.ConfigTemplate;
import com.dfsek.terra.api.math.ProbabilityCollection;

public class ColorConfigTemplate implements ConfigTemplate {
    @Value(".")
    private ProbabilityCollection<Integer> colors;

    public ProbabilityCollection<Integer> getColors() {
        return colors;
    }
}
