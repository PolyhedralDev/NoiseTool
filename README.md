![](https://i.imgur.com/0CtCgvX.png)

# Noise Tool

This tool allows you to visualise and edit Terra noise configurations. Features include:

* 2D, 3D and Voxel rendering of the noise function itself
* Minimum/Maximum values of the function
* Plots of the distribution of noise values
* Time taken to generate the noise
* YAML editor with syntax highlighting and basic auto-completion.
* Ability to load/save configurations.
* Ability to save renders to images.
* Ability to pan noise renders by dragging.

# Using the tool

## Setup

To use the tool, simply download the latest release and run it. 
An `addons` folder will automatically be created for you.
The Noise Tool does not include any addons by default, so this folder will only have an empty `bootstrap` folder in it.

Copy the `bootstrap` folder and the `config-noise-function` addon from your Terra installation (found in `Terra/addons`) to the Noise Tool’s `addon` folder.
Verify that the file structure in the folder containing the Noise Tool matches this:

```
├── addons
│   ├── bootstrap
│   │   ├── Terra-api-addon-loader-<version>-all.jar
│   │   └── Terra-manifest-addon-loader-<version>-all.jar
│   └── Terra-config-noise-function-<version>-all.jar
└── NoiseTool-<version>-all.jar
```

Once you have verified your file structure is correct, rerun the application.
A window will open up containing an editor on the left, and a render of the noise function on the right.

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