# Note Block Addons - Elegant Note Block Editing for Vanilla Minecraft  
This note block mod aids note block editing, but not modifying any ranges and instrument of the note blocks. Worlds should be able to loaded from and opened in vanilla without problems.  
  
## <a name="download"></a>Download
Download link is here: https://www.curseforge.com/minecraft/mc-mods/noteblock-addons  
I highly recommend using this mod with the texturepack [3D NoteBlock Display](https://www.creatorlabs.net/downloads/3d-noteblock-displays/) by [CreatorLabs](https://www.creatorlabs.net/).  
This texturepack shows the instrument and note on the Note Block, which can be quite useful when you edit note block.  

## Usage
- Right click on note block - Opens the GUI for changing the instrument or notes. Can use as playable piano as well.  
- The /noteblock command - Opens the GUI for mod settings and usage guide. The configurations are saved.  

## Features 
- Neat interfaces: "Note Block Configuration" and "Note Block Interface"  
- Change instrument for a note block with one click in the interface  
- Visual piano keys and labels indicating notes  
- Scroll to navigate notes - higher or lower - when pointing at a note block  
- Semi-playable 'instruments' in the interface  
- Pick up note block with its note and instrument NBT values (Usage: Ctrl + Middle Click on Creative)  

## Known issues
- Loading vanilla world does not load tile entity (and NBT values) for note blocks correctly without any player interaction - player still need to change the note at least once to "put" the tile entity in place  
  - What it affects: Picking up with NBT values (Ctrl + Middle Click) will not work  
  - What it should not affect: Anything else  
- [Open Issue #1] Game may freeze immediately upon interacting with a note block: the fix is not found  

## Installation
- This mod is a forge mod; forge for 1.16.x must be installed first
- Go to the download link in the **[download](#download)** section and download the .jar file
- Put the mod file into the mod folder (usually in \\AppData\\Roaming\\.minecraft)

## Credits
Much thanks to [CreatorLabs](https://www.creatorlabs.net/) for actually allowing me to include some of their awesome textures from their 3D Noteblock Displays in the mod.
