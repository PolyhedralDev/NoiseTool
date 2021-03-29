![](https://i.imgur.com/0CtCgvX.png)

# Noise Tool

This tool allows you to visualise and edit Terra noise configurations. Features include:

* 2D maps of the noise function itself
* Minimum/Maximum values of the function
* Plots of the distribution of noise values
* Time taken to generate the noise
* YAML editor with syntax highlighting and basic auto-completion.
* Ability to load/save configurations.
* Ability to save renders to images.
* Ability to pan noise renders by dragging.

# Using the tool

To use the tool, simply download the latest release, and run it. A window will open up containing
an editor on the left, and a render of the noise function on the right.

## Reloading the config

This tool allows you to reload the config live. To do so, simply select `Noise > Render`, or press `F5`.

## Setting the seed

To set the noise seed, go to the `Settings` panel on the right side of the application, and set the
"Seed" spinner.

## Plotting distribution

To view the noise distribution, see the "Distribution" panel on the right.

## Loading/Saving configurations

To load/Save configurations, use the `File` menu. Standard keyboard shortcuts (`Ctrl+O`, `Ctrl+S`, `Ctrl+Shift+S`)
are available as well. To save noise renders to an image, select `File > Save Render As` or use `Ctrl+Alt+S`.