# Noise Tool
A generic name for a generic program.

This tool allows you to visualise noise.
Specifically, you can visualise noise as defined by Terra configuration files. The tool can show you information about
the noise, including:
* 2D maps of the noise function itself
* Minimum/Maximum values of the function
* Plots of the distribution of noise values
* Time taken to generate the noise

# Using the tool
To use the tool, simply download the latest release, and put it in a folder. Create a file in the same directory
called `config.yml`, and put a single noise config inside. Example noise config for 4 octaves of Simplex:
```yaml
dimensions: 2
type: OpenSimplex2
frequency: 0.01
fractal:
  type: FBm
  octaves: 4
```
For more information on noise configs, see the section on the Terra Wiki
[here](https://github.com/PolyhedralDev/Terra/wiki/pack.yml-Options#noise).

Once you have created your configuration, run the tool either from the command line or by double-clicking the JAR.
When you run the tool, a GUI will open showing a view of the noise, min/max values, the seed, and the time taken to
generate it. The view is 1024x1024.

## Reloading the config
This tool allows you to reload the config live. To do so, simply press `r`.

## Randomizing seed
To randomize the seed, press `s`.

## Plotting distribution
To plot the distribution of the noise function at the bottom of the window, press `d`.