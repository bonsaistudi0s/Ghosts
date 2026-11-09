# 1.4.0
- Added new spawn egg textures for the ghost, the seedling ghost and the kodama
- Rebalanced spawns so that kodama are more common than ghosts and seedling ghosts
- Kodama now also populates forests when chunks are generated, regardless of the time of the day (afterwards they still only spawn naturally at night)
- Kodama can now spawn in every forest biome
- Ghosts no longer spawn during the day
- Ghosts and seedling ghosts now take knockback
- Tamed ghosts now try to stay within 30 blocks of where they were set to wander
- Increased the haunted eye log light level from 6 to 8
- Renamed the remaining references to the small ghost as seedling ghost
- Fixed the haunted hanging sign opening the regular sign edit screen (and using its text line size) instead of the hanging sign one

# 1.3.4
- Now all ghosts can be healed using glow berries
- Haunted saplings can now be placed in flower pots
- Fixed a case where a glow berry wasn't consumed when healing a tamed ghost
- Fixed missing crafting recipes for the haunted button and haunted pressure plate

# 1.3.3
- Added Simplified Chinese translations (by Junnaturefox)
- Fixed a case where kodama wouldn't spawn caused by c2me

# 1.3.2
- Reduced spawn rate for ghosts and seedling ghosts
- Changed the name of the small ghost to seedling ghost
- Reduced the strictness of the kodama spawning conditions, so that it spawns with greater probability
- The haunted eye log now points in any direction when it spawns naturally when generating the tree with a sapling
- Reduced the probability of generating a haunted eye log in each log block in a haunted tree from 20% to 10%
- Fixed missing assignment of kodama spawn restrictions in Fabric
- Fixed an issue in Fabric where the haunted boat would have the texture of the oak boat
- Fixed an issue in Fabric where the haunted eye log would only spawn on the first tree of all those generated in that game session

# 1.3.1
- Fixed a crash in fabric caused by an incorrect registry in the permutation of the eye log to its stripped variant

# 1.3.0
- Added a brand-new creative tab for the mod's items
- Entity spawn eggs are no longer automatically to the vanilla's creative mode tabs
- Added haunted tree
- Added haunted eye & calibrated haunted eye blocks, which output a redstone signal when there are entities infront of them
- Small ghosts will now pick up & plant dropped sapling items
- Kodama can now be bartered with using amethyst shards. They'll drop a random sapling (item tagged as such)
- Fixed z fighting on the small ghost plant texture
- Made regular ghosts slightly more transparent
- Small ghosts no longer phase through blocks
- Fixed a case where kodama head will start tweaking after rendering it while being nearby
- Fixed small ghost red hurt overlay not showing up on its body