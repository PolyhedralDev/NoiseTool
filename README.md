# Noise Tool

A generic name for a generic program.

This tool allows you to visualise noise. Specifically, you can visualise noise as defined by Terra configuration files.
The tool can show you information about the noise, including:

* 2D maps of the noise function itself
* Minimum/Maximum values of the function
* Plots of the distribution of noise values
* Time taken to generate the noise

# Using the tool

To use the tool, simply download the latest release, and put it in a folder. Run the tool from the command line
via `java -jar NoiseTool-VERSION.jar`. When you run the tool, a GUI will open showing a view of the noise, min/max
values, the seed, and the time taken to generate it. The view is 1024x1024.

When you run the tool it will create a configuration file, `config.yml` in the same directory. This config is identical
to Terra noise configs.
[here](https://github.com/PolyhedralDev/Terra/wiki/Noise-Options).

## Reloading the config

This tool allows you to reload the config live. To do so, simply press `r`.

## Randomizing seed

To randomize the seed, press `s`.

## Plotting distribution

To plot the distribution of the noise function at the bottom of the window, press `d`.

## Colorizing

To colorize noise (useful for visualizing the actual distribution of things in probability collections), create a file
in the same directory as the noise tool called `color.yml`. This file has 2 keys:

* `enable` - Whether to pull colors from a probability collection rather than from noise values.
* `colors` - A probability collection of colors. They are represented in a raw integer format. You may use hexadecimal
  color notation by prefixing hex values with `0x`.

Example `color.yml`:

 ```yaml
colors:
  0xff0000: 1
  0x00ff00: 1
  0x0000ff: 1
  0xffffff: 1
  0x000000: 1
  0xffff00: 1
  0x00ffff: 1
  0xff00ff: 1
enable: true
```