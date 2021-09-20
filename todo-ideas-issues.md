# Todo
- Screen GUI texture generation.
- User configured screen sizes.
- Different screen type per inventory size, e.g. large inventories could use page screen type whilst smaller ones use single screen type.
## Maybe todo
- Remove single screen option in favour of new code which resizes screens to biggest avaliable size.
# Known issues
Note: not being fixed for 1.0.0 because I'll likely find more issues or things I'd like to change
- Area under scrollbar and either side of player inventory is considered part of the gui.
- New `prefer_smaller_screens` config entry doesn't get added to existing configs, would require version bump
- Scroll bar click positions aren't really intuitive.
- config ids and internal ids are linked
- Needs proper mod icon
# 1.18
- Revert to `ninjaphenix-container-library.json` config name.
- Introduce config v2 with at least one of: user configurable screen sizes, [screen specific config objects], different screen types per inventory size.
