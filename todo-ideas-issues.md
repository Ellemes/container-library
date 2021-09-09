# Todo
- Screen GUI texture generation.
- User configured screen sizes.
- Different screen type per inventory size, e.g. large inventories could use page screen type whilst smaller ones use single screen type.
# Ideas
- Expose using single screen type for inventory <= 54 slots to custom screen types, opt-in ofc.
# Issues
- Switching from page / scroll screen type to single may crash the game if the single screen cannot display the inventory e.g. too big.
- Default key bind needs changing as it conflicts with movement when amecs is present.
- Scrollbar de-syncs when moved with mouse ( mouse cursor is ahead of scroll thing pos)
# 1.18
- Remove legacy config converter and revert to `ninjaphenix-container-library.json` config name.
