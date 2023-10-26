package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.event.MouseEvent;

import java.util.*;
import java.io.*;

// this class is for the basic map loaded for the application
// which starts with loading the grass as the default
// then load anything else should be on the map
// all the map elements have be saved under the resources,
//the same path as movable objects, such as enemy
// during the Board, floor need to be loaded first
// then add elements onto the map: "X" is the path, "S" is the shrub, "W" is the House

public class Board {
    int[][] grid;
    PApplet pApplet;
    String map_resources_path;
    PImage floor; // the default background for the map, which should be "Grass"
    PImage path0, path1, path2, path3; // path0 is the default setting for the path
    PImage shrub; // cells cannot replace by any object
    PImage house; // the end point of the map
    App App;
    ConfigLoader config;
    Mana mana;
    Wizard wizard;
    ManaPool manaPool;
    static final int ROWS = 20;
    static final int COLS = 20;
    private boolean isSpeedUp = false;
    private final EnumSet<GameAction> currentActions = EnumSet.noneOf(GameAction.class);
    public Board(PApplet p, ConfigLoader config, Mana mana, String map_resources_path) {
        if (config == null) {
            throw new IllegalArgumentException("ConfigLoader cannot be null.");
        }
        this.pApplet = p;
        this.map_resources_path = map_resources_path;
        this.config = config;
        this.mana = mana;
        this.manaPool = new ManaPool(mana, pApplet, config);

        App = new App();
        // Converting BufferedImage to PImage
        floor = pApplet.loadImage("src/main/resources/WizardTD/grass.png");
        path0 = pApplet.loadImage("src/main/resources/WizardTD/path0.png");
        path1 = pApplet.loadImage("src/main/resources/WizardTD/path1.png");
        path2 = pApplet.loadImage("src/main/resources/WizardTD/path2.png");
        path3 = pApplet.loadImage("src/main/resources/WizardTD/path3.png");
        shrub = pApplet.loadImage("src/main/resources/WizardTD/shrub.png");
        house = pApplet.loadImage("src/main/resources/WizardTD/wizard_house.png");
        loadMap();
        this.wizard = new Wizard(pApplet, this);
    }

    private void loadMap() {
        try {
            Scanner scanner = new Scanner(new File(map_resources_path));
            List<String> lines = new ArrayList<>();
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
            grid = new int[ROWS][COLS];

            for (int r = 0; r < ROWS; r++) {
                char[] chars = lines.get(r).toCharArray();
                for (int c = 0; c < COLS; c++) {
                    if (c < chars.length) {
                        switch (chars[c]) {
                            case 'X':
                                grid[r][c] = 1; // Path
                                break;
                            case 'S':
                                grid[r][c] = 2; // Shrub
                                break;
                            case 'W':
                                grid[r][c] = 3; // House
                                break;
                            default:
                                grid[r][c] = 0; // Default to Grass
                                break;
                        }
                    } else {
                        grid[r][c] = 0; // Grass
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // Handle error - perhaps display a message to the user or use a default map
        }
    }

    public void draw() {


        pApplet.background(131, 112, 75);
        pApplet.noFill(); // Ensure that the rectangles won't be filled
        pApplet.stroke(0); // Set the border color to black

        // Draw the default stage of buttons
        pApplet.rect(650, 60, 45, 45); // "FF" : 2x speed
        pApplet.rect(650, 120, 45, 45); // "P": PAUSE
        pApplet.rect(650, 180, 45, 45); // "T": Build tower
        pApplet.rect(650, 240, 45, 45); // "U 1": Upgrade range
        pApplet.rect(650, 300, 45, 45); // "U 2": Upgrade speed
        pApplet.rect(650, 360, 45, 45); // "U 3": Upgrade damage
        pApplet.rect(650, 420, 45, 45); // "M" : Mana pool cost: 100
        pApplet.rect(650, 480, 45, 45); // "C" : Ice tower / Freezer
        pApplet.rect(730, 470, 20, 20); //TEMP CHEAT

        // Highlight the button based on current action
        pApplet.fill(255, 249, 12); // fill with background colour, Brown
        if (isSpeedUp) {
            pApplet.rect(650, 60, 45, 45);
        }
        if (currentActions.contains(GameAction.PAUSE)) {
            pApplet.rect(650, 120, 45, 45);
        }
        if (currentActions.contains(GameAction.BUILD_TOWER)) {
            pApplet.rect(650, 180, 45, 45);
        }
        if (currentActions.contains(GameAction.UPGRADE_RANGE)) {
            pApplet.rect(650, 240, 45, 45);
        }
        if (currentActions.contains(GameAction.UPGRADE_SPEED)) {
            pApplet.rect(650, 300, 45, 45);
        }
        if (currentActions.contains(GameAction.UPGRADE_DAMAGE)) {
            pApplet.rect(650, 360, 45, 45);
        }
        if (currentActions.contains(GameAction.MANA_POOL)) {
            pApplet.rect(650, 420, 45, 45);
        }
        if (currentActions.contains(GameAction.FREEZER)) {
            pApplet.rect(650, 480, 45, 45);
        }

        pApplet.fill(0); // black for text

        pApplet.textSize(24);
        pApplet.text("FF", 650 + 7, 60 + 30);
        pApplet.text("P", 650 + 14, 120 + 30);
        pApplet.text("T", 650 + 14, 180 + 30);
        pApplet.text("U1", 650 + 7, 240 + 30);
        pApplet.text("U2", 650 + 7, 300 + 30);
        pApplet.text("U3", 650 + 7, 360 + 30);
        pApplet.text("M", 650 + 12, 420 + 30);
        pApplet.text("C", 650 + 12, 480 + 30);
        pApplet.text("?",730 + 4.5f, 470 + 18.5f); //TEMP CHEAT

        pApplet.textSize(11);
        pApplet.text("2x speed", 650 + 45 + 5, 60 + 15);
        pApplet.text("PAUSE", 650 + 45 + 5, 120 + 15);
        pApplet.text("Build", 650 + 45 + 5, 180 + 15);
        pApplet.text("tower", 650 + 45 + 5, 180 + 30);
        pApplet.text("Upgrade", 650 + 45 + 5, 240 + 15);
        pApplet.text("range", 650 + 45 + 5, 240 + 30);
        pApplet.text("Upgrade", 650 + 45 + 5, 300 + 15);
        pApplet.text("speed", 650 + 45 + 5, 300 + 30);
        pApplet.text("Upgrade", 650 + 45 + 5, 360 + 15);
        pApplet.text("damage", 650 + 45 + 5, 360 + 30);
        pApplet.text("Mana pool", 650 + 45 + 5, 420 + 15);
        pApplet.text("Freezer", 650 + 45 + 5, 480 + 20);

        float poolCost = manaPool.getCurrentSpellCost();
        pApplet.text("cost: " + (int) poolCost, 650 + 45 + 5, 420 + 30);

        // draw the mana bar
        manaPool.displayManaBar();

        pApplet.translate(0, App.TOPBAR);
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                int renderX = convertToPixelCoordinate(j);
                int renderY = convertToPixelCoordinate(i);
                pApplet.image(floor, renderX, renderY, App.CELLSIZE, App.CELLSIZE);

                switch (grid[i][j]) {
                    case 1: // Path
                        PImage pathImage = determinePathImage(i, j);
                        pApplet.image(pathImage, j * App.CELLSIZE, i * App.CELLSIZE, App.CELLSIZE, App.CELLSIZE);
                        break;
                    case 2: // Shrub
                        pApplet.image(shrub, j * App.CELLSIZE, i * App.CELLSIZE, App.CELLSIZE, App.CELLSIZE);
                        break;
                }
            }
        }
        wizard.draw();
        pApplet.translate(0, -App.TOPBAR);
    }

    enum GameAction {
        NORMAL,
        SPEED_UP, // "FF" : 2x speed
        PAUSE, // "P": PAUSE
        BUILD_TOWER, // "T": Build tower
        UPGRADE_RANGE, // "U 1": Upgrade range
        UPGRADE_SPEED, // "U 2": Upgrade speed
        UPGRADE_DAMAGE, // "U 3": Upgrade damage
        MANA_POOL, // "M" : Mana pool spell
        FREEZER
    }

    public boolean isGamePaused = false;

    public void keyPressed(){
        GameAction currentAction = null;
        char actionKey = Character.toUpperCase(pApplet.key);
        switch (actionKey) {
            case '?':
                mana.setCurrentMana(mana.getCurrentMana() + 200);
                break;
            case 'T':
                currentAction = (currentAction == GameAction.BUILD_TOWER) ? GameAction.NORMAL : GameAction.BUILD_TOWER;
                break;
            case 'F':
                if (!isSpeedUp) {
                    pApplet.frameRate(WizardTD.App.newFPS);
                    isSpeedUp = true;
                    currentAction = GameAction.SPEED_UP;
                    System.out.println("Speed up!");
                }
                else if (isSpeedUp) {
                    pApplet.frameRate(WizardTD.App.FPS);
                    isSpeedUp = false;
                    currentAction = GameAction.NORMAL;
                    System.out.println("Speed Down!");
                }
                break;
            case 'P':
                pApplet.pushStyle();
                pApplet.fill(255, 249, 12);
                pApplet.stroke(0);
                pApplet.rect(650, 120, 45, 45);
                pApplet.fill(0);
                pApplet.textSize(24);
                pApplet.text("P", 650 + 14, 120 + 30);
                pApplet.popStyle();
                // switch for PAUSE
                isGamePaused = !isGamePaused;
                break;
            case '1':
                currentAction = (currentAction == GameAction.UPGRADE_RANGE) ?
                        GameAction.NORMAL : GameAction.UPGRADE_RANGE;
                break;
            case '2':
                currentAction = (currentAction == GameAction.UPGRADE_SPEED) ?
                        GameAction.NORMAL : GameAction.UPGRADE_SPEED;
                break;
            case '3':
                currentAction = (currentAction == GameAction.UPGRADE_DAMAGE) ?
                        GameAction.NORMAL : GameAction.UPGRADE_DAMAGE;
                break;
            case 'M':
                currentAction = (currentAction == GameAction.MANA_POOL) ?
                        GameAction.NORMAL : GameAction.MANA_POOL;
                break;
            case 'C':
                currentAction = (currentAction == GameAction.FREEZER) ?
                        GameAction.NORMAL : GameAction.FREEZER;
                break;
            default:
                currentAction = GameAction.NORMAL;
                break;
        }
        if (currentAction != null) {
            if (currentAction == GameAction.BUILD_TOWER) {
                currentActions.remove(GameAction.FREEZER);
            } else if (currentAction == GameAction.FREEZER) {
                currentActions.remove(GameAction.BUILD_TOWER);
            }
            if (currentActions.contains(currentAction)) {
                currentActions.remove(currentAction);
            } else {
                currentActions.add(currentAction);
            }
        }
    }
    ArrayList<Towers> towersList = new ArrayList<>();
    ArrayList<FreezeTower> freezers = new ArrayList<>();
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        GameAction currentAction = null;

        //temp CHEAT
        if (x >= 730 && x <= 750) {
            if (y >= 470 && y <= 490) {
                mana.setCurrentMana(mana.getCurrentMana() + 200);
            }
        }

        if (x >= 650 && x <= 695) {
            if (y >= 60 && y <= 105) {
                    if (!isSpeedUp) {
                        pApplet.frameRate(WizardTD.App.newFPS);
                        isSpeedUp = true;
                        currentAction = GameAction.SPEED_UP;
                        System.out.println("Speed up!");
                    }
                    else if (isSpeedUp) {
                        pApplet.frameRate(WizardTD.App.FPS);
                        isSpeedUp = false;
                        currentAction = GameAction.NORMAL;
                        System.out.println("Speed Down!");
                    }
            } else if (y >= 120 && y <= 165) {
                pApplet.pushStyle();
                pApplet.fill(255, 249, 12);
                pApplet.stroke(0);
                pApplet.rect(650, 120, 45, 45);
                pApplet.fill(0);
                pApplet.textSize(24);
                pApplet.text("P", 650 + 14, 120 + 30);
                pApplet.popStyle();
                isGamePaused = !isGamePaused;
                return;
            } else if (y >= 180 && y <= 225) {
                currentAction = (currentAction == GameAction.BUILD_TOWER) ? GameAction.NORMAL : GameAction.BUILD_TOWER;
            } else if (y >= 240 && y <= 285) {
                currentAction = (currentAction == GameAction.UPGRADE_RANGE) ? GameAction.NORMAL : GameAction.UPGRADE_RANGE;
            } else if (y >= 300 && y <= 345) {
                currentAction = (currentAction == GameAction.UPGRADE_SPEED) ? GameAction.NORMAL : GameAction.UPGRADE_SPEED;
            } else if (y >= 360 && y <= 405) {
                currentAction = (currentAction == GameAction.UPGRADE_DAMAGE) ? GameAction.NORMAL : GameAction.UPGRADE_DAMAGE;
            } else if (y >= 420 && y <= 465) {
                currentAction = (currentAction == GameAction.MANA_POOL) ? GameAction.NORMAL : GameAction.MANA_POOL;
            } else if (y >= 480 && y <= 525) {
                currentAction = (currentAction == GameAction.FREEZER) ? GameAction.NORMAL : GameAction.FREEZER;
            }
        }

        if (currentAction != null) {
            if (currentAction == GameAction.BUILD_TOWER) {
                currentActions.remove(GameAction.FREEZER);
            } else if (currentAction == GameAction.FREEZER) {
                currentActions.remove(GameAction.BUILD_TOWER);
            }
            if (currentActions.contains(currentAction)) {
                currentActions.remove(currentAction);
            } else {
                currentActions.add(currentAction);
            }
        }

        if (currentActions.contains(GameAction.MANA_POOL)) {
            manaPool.castManaPoolSpell();
        }

        if (currentActions.contains(GameAction.BUILD_TOWER)) {
            Towers newTower = Towers.createTower(pApplet, this, x, y, config);
            if (newTower != null &&
            newTower.getTowerInitialCost() + newTower.getTotalUpgradeCost()<= mana.getCurrentMana()) {
                mana.useMana(newTower.getTowerInitialCost());
                towersList.add(newTower);
            }
        }

        if (currentActions.contains(GameAction.FREEZER)) {
            FreezeTower newTower = FreezeTower.createTower(pApplet, this, x, y, config);
            if (newTower != null &&
                    newTower.getTowerInitialCost() + newTower.getTotalUpgradeCost()<= mana.getCurrentMana()) {
                mana.useMana(newTower.getTowerInitialCost());
                freezers.add(newTower);
            }
        }

        for (Towers tower : towersList) {
            if (tower.isMouseOver(x, y) &&
            tower.getTotalUpgradeCost() <= mana.getCurrentMana()) {
                if (currentActions.contains(GameAction.UPGRADE_RANGE) ) {
                    mana.useMana(tower.getRangeUpgradeCost());
                    tower.upgradeRange();
                }
                if (currentActions.contains(GameAction.UPGRADE_SPEED)) {
                    mana.useMana(tower.getSpeedUpgradeCost());
                    tower.upgradeSpeed();
                }
                if (currentActions.contains(GameAction.UPGRADE_DAMAGE)) {
                    mana.useMana(tower.getDamageUpgradeCost());
                    tower.upgradeDamage();
                }
                break;
            }
        }

        for (FreezeTower freezer : freezers) {
            if (freezer.isMouseOver(x, y) &&
                    freezer.getTotalUpgradeCost() <= mana.getCurrentMana()) {
                if (currentActions.contains(GameAction.UPGRADE_RANGE) ) {
                    mana.useMana(freezer.getRangeUpgradeCost());
                    freezer.upgradeRange();
                }
                if (currentActions.contains(GameAction.UPGRADE_SPEED)) {
                    mana.useMana(freezer.getSpeedUpgradeCost());
                    freezer.upgradeSpeed();
                }
                if (currentActions.contains(GameAction.UPGRADE_DAMAGE)) {
                    mana.useMana(freezer.getDamageUpgradeCost());
                    freezer.upgradeDamage();
                }
                break;
            }
        }

    }
    public EnumSet<GameAction> getCurrentActions() {
        return currentActions;
    }

    public ArrayList<Towers> getTowerList() {
        return towersList;
    }

    public ArrayList<FreezeTower> getFreezers() {
        return freezers;
    }

    private int convertToPixelCoordinate(int gridCoordinate) {
        return gridCoordinate * App.CELLSIZE;
    }
    private PImage determinePathImage(int i, int j) {
        boolean pathAbove = i > 0 && grid[i-1][j] == 1;
        boolean pathBelow = i < grid.length - 1 && grid[i+1][j] == 1;
        boolean pathLeft = j > 0 && grid[i][j-1] == 1;
        boolean pathRight = j < grid[i].length - 1 && grid[i][j+1] == 1;

        boolean obstacleBelow = i < grid.length - 1 && (grid[i+1][j] == 2 || grid[i+1][j] == 3);
        boolean obstacleAbove = i > 0 && (grid[i-1][j] == 2 || grid[i-1][j] == 3);
        boolean obstacleLeft = j > 0 && (grid[i][j-1] == 2 || grid[i][j-1] == 3);
        boolean obstacleRight = j < grid[i].length - 1 && (grid[i][j+1] == 2 || grid[i][j+1] == 3);

        boolean atTopBorder = i == 0;
        boolean atBottomBorder = i == grid.length - 1;
        boolean atLeftBorder = j == 0;
        boolean atRightBorder = j == grid[i].length - 1;

        PImage pathImage = path0; // Default path image
        float rotationAngle = 0; // Default rotation

        // Conditions for paths:
        if (pathAbove && pathBelow && pathLeft && pathRight) {
            pathImage = path3;
        } else if (pathAbove && pathBelow && pathLeft) {
            pathImage = path2;
            rotationAngle = 90;
        } else if (pathAbove && pathLeft && pathRight) {
            pathImage = path2;
            rotationAngle = 180;
        } else if (pathBelow && pathLeft && pathRight) {
            pathImage = path2;
        } else if (pathAbove && pathBelow && pathRight) {
            pathImage = path2;
            rotationAngle = 270;
        } else if (pathAbove && pathBelow) {
            pathImage = path0;
            rotationAngle = 90;
        } else if (pathLeft && pathRight) {
            pathImage = path0;
        } else if (pathBelow && pathLeft) {
            pathImage = path1;
        } else if (pathAbove && pathLeft) {
            pathImage = path1;
            rotationAngle = 90;
        } else if (pathBelow && pathRight) {
            pathImage = path1;
            rotationAngle = 270;
        } else if (pathAbove && pathRight) {
            pathImage = path1;
            rotationAngle = 180;
        }
        // Conditions for shrubs and houses:
        else if (!pathAbove && pathBelow && !pathLeft && !pathRight && obstacleAbove) {
            pathImage = path0;
            rotationAngle = 90;
        }
        else if (pathAbove && !pathBelow && !pathLeft && !pathRight && obstacleBelow) {
            pathImage = path0;
            rotationAngle = 90;
        }
        else if (!pathAbove && !pathBelow && pathLeft && !pathRight && obstacleRight) {
            pathImage = path0;
        }
        else if (!pathAbove && !pathBelow && !pathLeft && pathRight && obstacleLeft) {
            pathImage = path0;
        }

        // Conditions for borders:
        else if (atTopBorder && !pathAbove && pathBelow) {
            pathImage = path0;
            rotationAngle = 90;
        }
        else if (atBottomBorder && pathAbove && !pathBelow) {
            pathImage = path0;
            rotationAngle = 90;
        }
        else if (atLeftBorder && !pathLeft && pathRight) {
            pathImage = path0;
        }
        else if (atRightBorder && pathLeft && !pathRight) {
            pathImage = path0;
        }

        return App.rotateImageByDegrees(pathImage, rotationAngle);
    }

    public int[][] getGrid() {
        return grid;
    }

    public int[] findWizardHousePosition() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 3) { // Assuming 3 represents the Wizard's House
                    return new int[]{i, j}; // Return the position as soon as we find it
                }
            }
        }
        return new int[]{-1, -1};
    }
}
