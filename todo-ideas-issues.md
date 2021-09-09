# Todo
- Screen GUI texture generation.
- User configured screen sizes.
- Different screen type per inventory size, e.g. large inventories could use page screen type whilst smaller ones use single screen type.
- Convert fabric & common sources to yarn then use tiny remapper to remap sources to mojang mappings for forgeSrc folder.
- Write an algorithm to take a set of valid screen sizes and produce a single output which provides the best user experience
    e.g. last page has most slots possible with minimal amount of pages, single page is more square, scrolling page is more square.
# Ideas
- Expose using single screen type for inventory <= 54 slots to custom screen types, opt-in ofc.
# Issues
- Switching from page / scroll screen type to single may crash the game if the single screen cannot display the inventory e.g. too big.
- Default key bind needs changing as it conflicts with movement when amecs is present.
- Scrollbar de-syncs when moved with mouse ( mouse cursor is ahead of scroll thing pos)
- When screen fails to open the inventory is still technically open.
- Some scrolling screens e.g. new 507 have scroll bar icon cut in half.
- Inventory 75, 99, 123, 147 doesn't use single screen type despite having no pages / scroll, because auto page, scroll -> single screen type code is not screen size aware.
# 1.18
- Remove legacy config converter and revert to `ninjaphenix-container-library.json` config name.
- Introduce config v2 with at least one of: user configurable screen sizes, screen specific config objects, different screen types per inventory size.
