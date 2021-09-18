# Todo
- Screen GUI texture generation.
- User configured screen sizes.
- Different screen type per inventory size, e.g. large inventories could use page screen type whilst smaller ones use single screen type.
# Issues
- Switching from page / scroll screen type to single closed the inventory without any user feedback.
- When screen fails to open the inventory is still technically open.
# 1.18
- Remove legacy config converter and revert to `ninjaphenix-container-library.json` config name.
- Introduce config v2 with at least one of: user configurable screen sizes, screen specific config objects, different screen types per inventory size.
