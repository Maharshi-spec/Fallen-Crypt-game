# The Fallen Crypt — Updated Build

## Fixes in this version

### 1. Any window/screen size
The game now uses a LibGDX `FitViewport` with a fixed virtual resolution of:

960 x 540

The entire game world is scaled to fit the actual window.

This means:
- small window -> whole game remains visible
- maximized window -> whole game remains visible
- fullscreen -> whole game remains visible
- ultrawide window -> whole game remains visible

The aspect ratio is preserved. If the physical screen has a different aspect ratio, LibGDX may show small black bars instead of cutting the game off.

### 2. Knight facing
The two knights automatically face one another:
- Knight I on the left -> faces RIGHT
- Knight II on the right -> faces LEFT

The helmet visor, shield/arm and sword are mirrored according to direction.

## Replace these files

Replace:
- Main.java
- MainMenuScreen.java
- ScoresScreen.java
- GameScreen.java
- GameOverScreen.java
- Player.java

Keep:
- FlyingSwordAura.java

Put all of them in:
core/src/com/college/mygame/

Then rebuild/run the project.
