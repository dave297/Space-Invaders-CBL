# Space Invaders Game

A Space Invaders game written in Java using Swing. This project was developed for the Programming 2IP90 course at TU/e.

Repository: [https://github.com/dave297/Space-Invaders-CBL.git](https://github.com/dave297/Space-Invaders-CBL.git)

## Description

Control a spaceship and shoot down waves of alien invaders. The game has different enemy types that appear as your score increases, making it progressively more challenging.

## Requirements

- Java 8 or higher
- No external libraries needed

## How to Run

1. Download or clone the repository
2. Compile the Java files:
   ```
   javac *.java
   ```
3. Run the game:
   ```
   java Window
   ```

Alternatively, open the project in any Java IDE and run the main method in `Window.java`.

## Controls

- A or Left Arrow: Move left
- D or Right Arrow: Move right  
- Space: Shoot
- ESC: Return to menu

## Game Features

The game has four different enemy types that appear based on your score:

- **Default Invaders** (Green): Basic enemies that move in formation
- **Shooter Invaders** (Purple): Move independently and can shoot back
- **Tank Invaders** (Red): Require 3 hits to destroy
- **Diver Invaders** (Blue): Vibrate for 1.5 seconds then dive straight down

As your score increases, enemy movement speed also increases. The game includes background music and sound effects.

## Testing

To test the game features:

1. Start the game and verify the main menu works
2. Test player movement and shooting
3. Play until you reach different score thresholds to see new enemy types (300 for shooting invaders, 500 for tank invaders, 1000 for diving invaders)
4. Test the menu system with the ESC key
5. Check that the game restarts properly when you lose all lives
