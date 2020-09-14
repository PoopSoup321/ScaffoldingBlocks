# ScaffoldingBlocks

ScaffoldingBlocks is a small plugin for [Spigot](https://www.spigotmc.org) Minecraft servers. It allows to use vanilla scaffoldings' behaviors with other blocks. It also allows you to create long bridges with vanilla scaffoldings.

## Usage

Players can bridge long distance with vanilla scaffoldings (7 blocks max without the plugin)... But this is not very safe, because placing another block around, or hitting one of those scaffoldings blocks will break the entire bridge.

So, players could prefer to use other blocks, more solid, which will require a "tool" to react like vanilla scaffoldings. When they hold this tool item in their off hand, players can place blocks from bottom to up, from up/down to side and break them in chain reaction. If they don't hold the tool, those blocks reacts normally and so break one will not destroy the entire bridge / tower.

**By default:** (editable in config)
* The tool item can be a ghast tear or a rabbit foot.
* Alternative scaffolding blocks can be dark prismarine, dried kelp or ladder

(Ladder makes poor bridge but it's cool to place them from the bottom, try it by yourself.)

## Installation

There is no dependencies, simply drop the jar file into your plugin directory, then restart (or reload) your server. You can download the last release here: [ScaffoldingBlocks.jar](https://github.com/arboriginal/ScaffoldingBlocks/releases).

## Configuration

All configuration parameters are explained in this [config.yml](https://github.com/arboriginal/ScaffoldingBlocks/blob/master/src/main/resources/config.yml).

## Commands

There is only one: **/scaffold**, to reload the plugin configuration.

## Permissions

* **scaffold** allows to use the **/scaffold** command
* **sb.vanilla_scaffoldings.bridge** Allows to create long bridges with vanilla scaffoldings
* **sb.alt_scaffolding.break** Allows to break alternative blocks like scaffolding (chain reaction)
* **sb.alt_scaffolding.place** Allows to place alternative blocks like scaffolding (from bottom to up / from top to side)

**/!\ REMEMBER:** To use alternative blocks like scaffoldings, players need the appropriate permission(s) AND have to hold the tool item in his off hand. This prevents them to be forced to always use those particular behaviors when they're doing their classical stuffs.
