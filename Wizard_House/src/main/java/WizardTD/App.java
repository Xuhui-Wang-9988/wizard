package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 32;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;

    public static int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH*CELLSIZE+TOPBAR;

    public static final int FPS = 60;

    public String configPath;

    public Random random = new Random();
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.


    int currentWaveIndex = 0;
    int currentMonsterIndex = 0;
    int monsterSpawnTimer = 0;

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player, enemies and map elements.
     */


    Board maploader;
    Wizard wizard;

    JSONArray waves;

    List<Waves> wavelist = new ArrayList<>();
    List<WaveInfo> waveInfoList = new ArrayList<>();

    ArrayList<Towers> towersList = new ArrayList<>();


    ArrayList<Monsters> enemies = new ArrayList<>();



    @Override
    public void setup() {
        frameRate(FPS);
        // Map loader
        ConfigLoader config = new ConfigLoader(this);
        config.loadConfig(configPath);
        List<WaveInfo> waveInfoList = config.getWaveInfoList();
        for (int i = 0; i < waveInfoList.size(); i ++) {
//            System.out.println(waveInfoList.get(i).getDuration()+ "\n");
        }

        System.out.println("waveInfoList Size:"+ waveInfoList.size()+ "\n");

        maploader = new Board(this, config.getLayout() );
        wizard = new Wizard(this, maploader);

        // add waves
        for (int i = 0; i < waveInfoList.size(); i++) {
//            Waves currentWave = new Waves(this, waveInfoList, i, maploader);
            wavelist.add(new Waves(this, waveInfoList, maploader));
        }

        System.out.println("wavelist size: "+wavelist.size() + "\n");



    }

    // setting for user's behaviour
    boolean isBuilderTowerPressed = false;
    boolean isSpeedUpPressed = false;
    boolean isPausePressed = false;
    boolean isUpgradeRangePressed = false;
    boolean isUpgradeSpeedPressed = false;
    boolean isUpgradeDamagePressed = false;
    boolean isManaPoolPressed = false;

    enum GameAction {
        NORMAL,
        SPEED_UP, // "FF" : 2x speed
        PAUSE, // "P": PAUSE
        BUILD_TOWER, // "T": Build tower
        UPGRADE_RANGE, // "U 1": Upgrade range
        UPGRADE_SPEED, // "U 2": Upgrade speed
        UPGRADE_DAMAGE, // "U 3": Upgrade damage
        MANA_POOL // "M" : Mana pool cost: 100
    }
    private GameAction currentAction = GameAction.NORMAL;


    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(){
        char actionKey = Character.toUpperCase(key);
        switch (actionKey) {
            case 'T':
                currentAction = GameAction.BUILD_TOWER;
                isBuilderTowerPressed = true;
                break;
            case 'F':
                currentAction = GameAction.SPEED_UP;
                isSpeedUpPressed = true;
                break;
            case 'P':
                currentAction = GameAction.PAUSE;
                isPausePressed = true;
                break;
            case '1':
                currentAction = GameAction.UPGRADE_RANGE;
                isUpgradeRangePressed = true;
                break;
            case '2':
                currentAction = GameAction.UPGRADE_SPEED;
                isUpgradeSpeedPressed = true;
                break;
            case '3':
                currentAction = GameAction.UPGRADE_DAMAGE;
                isUpgradeDamagePressed = true;
                break;
            case 'M':
                currentAction = GameAction.MANA_POOL;
                isManaPoolPressed = true;
                break;
            default:
                currentAction = GameAction.NORMAL;
                break;
        }

        if (isBuilderTowerPressed) {
            Towers tower = Towers.createTower(this, maploader, mouseX, mouseY, 100, 100, 1, 10); // You can adjust the tower attributes as needed
            if (tower != null) {
                towersList.add(tower);
            }
        }

    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        char actionKey = Character.toUpperCase(key);
        switch (actionKey) {
            case 'T':
                isBuilderTowerPressed = false;
                break;
            case 'F':
                isSpeedUpPressed = false;
                break;
            case 'P':
                isPausePressed = false;
                break;
            case '1':
                isUpgradeRangePressed = false;
                break;
            case '2':
                isUpgradeSpeedPressed = false;
                break;
            case '3':
                isUpgradeDamagePressed = false;
                break;
            case 'M':
                isManaPoolPressed = false;
                break;
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (x >= 650 && x <= 695) {
            if (y >= 60 && y <= 105) {
                currentAction = GameAction.SPEED_UP;
                isSpeedUpPressed = true;
            } else if (y >= 120 && y <= 165) {
                currentAction = GameAction.PAUSE;
                isPausePressed = true;
            } else if (y >= 180 && y <= 225) {
                currentAction = GameAction.BUILD_TOWER;
                isBuilderTowerPressed = true;
            } else if (y >= 240 && y <= 285) {
                currentAction = GameAction.UPGRADE_RANGE;
                isUpgradeRangePressed = true;
            } else if (y >= 300 && y <= 345) {
                currentAction = GameAction.UPGRADE_SPEED;
                isUpgradeSpeedPressed = true;
            } else if (y >= 360 && y <= 405) {
                currentAction = GameAction.UPGRADE_DAMAGE;
                isUpgradeDamagePressed = true;
            } else if (y >= 420 && y <= 465) {
                currentAction = GameAction.MANA_POOL;
                isManaPoolPressed = true;
            }
        }

        Towers newTower = Towers.createTower(this, maploader, x, y, 100, 100, 1, 10);
        if (newTower != null) {
            towersList.add(newTower);
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (x >= 650 && x <= 695) {
            if (y >= 60 && y <= 105) {
                isSpeedUpPressed = false;
            } else if (y >= 120 && y <= 165) {
                isPausePressed = false;
            } else if (y >= 180 && y <= 225) {
                isBuilderTowerPressed = false;
            } else if (y >= 240 && y <= 285) {
                isUpgradeRangePressed = false;
            } else if (y >= 300 && y <= 345) {
                isUpgradeSpeedPressed = false;
            } else if (y >= 360 && y <= 405) {
                isUpgradeDamagePressed = false;
            } else if (y >= 420 && y <= 465) {
                isManaPoolPressed = false;
            }
        }

    }

    /*@Override
    public void mouseDragged(MouseEvent e) {

    }*/

    int poolCost = 100;


    /**
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() {
        background(131,112,75);

        fill(255, 249, 12); // the lower case of button, fill with yellow
        if (!isSpeedUpPressed) rect(650,60,45,45); // "FF" : 2x speed
        if (!isPausePressed) rect(650,120,45,45); // "P": PAUSE
        if (!isBuilderTowerPressed) rect(650,180,45,45); // "T": Build tower
        if (!isUpgradeRangePressed) rect(650,240,45,45); // "U 1": Upgrade range
        if (!isUpgradeSpeedPressed) rect(650,300,45,45); // "U 2": Upgrade speed
        if (!isUpgradeDamagePressed) rect(650,360,45,45); // "U 3": Upgrade damage
        if (!isManaPoolPressed) rect(650,420,45,45); // "M" : Mana pool cost: 100

        //once click on the button, the lower case will be shown
        fill(131, 112, 75); // fill with background colour, Brown

        if (isSpeedUpPressed) rect(650,60,45,45); // "FF" : 2x speed
        if (isPausePressed) rect(650,120,45,45); // "P": PAUSE
        if (isBuilderTowerPressed) rect(650,180,45,45); // "T": Build tower
        if (isUpgradeRangePressed) rect(650,240,45,45); // "U 1": Upgrade range
        if (isUpgradeSpeedPressed) rect(650,300,45,45); // "U 2": Upgrade speed
        if (isUpgradeDamagePressed) rect(650,360,45,45); // "U 3": Upgrade damage
        if (isManaPoolPressed) rect(650,420,45,45); // "M" : Mana pool cost: 100



        fill(0); // black for text

        textSize(24);
        text("FF", 650 + 7, 60 + 30);
        text("P", 650 + 14, 120 + 30);
        text("T", 650 + 14, 180 + 30);
        text("U1", 650 + 7, 240 + 30);
        text("U2", 650 + 7, 300 + 30);
        text("U3", 650 + 7, 360 + 30);
        text("M", 650 + 12, 420 + 30);




        textSize(11);
        text("2x speed", 650 + 45 + 5, 60 + 15);
        text("PAUSE", 650 + 45 + 5, 120 + 15);
        text("Build", 650 + 45 + 5, 180 + 15);
        text("tower", 650 + 45 + 5, 180 + 30);
        text("Upgrade", 650 + 45 + 5, 240 + 15);
        text("range", 650 + 45 + 5, 240 + 30);
        text("Upgrade", 650 + 45 + 5, 300 + 15);
        text("speed", 650 + 45 + 5, 300 + 30);
        text("Upgrade", 650 + 45 + 5, 360 + 15);
        text("damage", 650 + 45 + 5, 360 + 30);
        text("Mana pool", 650 + 45 + 5, 420 + 15);
        text("cost " + poolCost, 650 + 45 + 5, 420 + 30);



        maploader.draw();
        wizard.draw();

        for (Towers tower : towersList) {
            tower.display();
        }

        // Update and draw only the current wave
        if (currentWaveIndex < wavelist.size()) {
            Waves currentWave = wavelist.get(currentWaveIndex);;
            currentWave.update();
            currentWave.updateMonsters();

            // Check if the wave is complete and prepare the next wave if needed
            if (currentWave.isWaveComplete()) {
//                currentWaveIndex++;
            }
        }
    }


    public static void main(String[] args) {
        PApplet.main("WizardTD.App");
    }

    /**
     * Source: https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java
     * @param pimg The image to be rotated
     * @param angle between 0 and 360 degrees
     * @return the new rotated image
     */
    public PImage rotateImageByDegrees(PImage pimg, double angle) {
        BufferedImage img = (BufferedImage) pimg.getNative();
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        PImage result = this.createImage(newWidth, newHeight, RGB);
        //BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        BufferedImage rotated = (BufferedImage) result.getNative();
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                result.set(i, j, rotated.getRGB(i, j));
            }
        }

        return result;
    }
}
