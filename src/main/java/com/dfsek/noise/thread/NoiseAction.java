package com.dfsek.noise.thread;

import com.dfsek.terra.api.math.noise.NoiseSampler;

import java.util.concurrent.RecursiveAction;

public class NoiseAction extends RecursiveAction {
    private final double originX;

    private final double originZ;

    private final double[] result;

    private final NoiseSampler sampler;

    private final double in;

    public NoiseAction(double originX, double originZ, double[] result, NoiseSampler sampler, double in) {
        this.originX = originX;
        this.originZ = originZ;
        this.result = result;
        this.sampler = sampler;
        this.in = in;
    }

    @Override
    protected void compute() {
        for(int i = 0; i <= result.length; i++) {
            result[i] = sampler.getNoise(i + originX, in + originZ);
        }
    }
}
