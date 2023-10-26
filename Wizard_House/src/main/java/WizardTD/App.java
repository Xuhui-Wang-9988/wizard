package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
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

    public static final int newFPS = 120;

    public String configPath;
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.
    int currentWaveIndex = 0;

    private boolean gameOver = false;
    private boolean gameWin = false;

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
    ConfigLoader config;
    List<Waves> wavelist;
    ArrayList<Towers> towersList;
    ArrayList<FreezeTower> freezers;

    Mana mana;

    @Override
    public void setup() {
        frameRate(FPS);
        currentWaveIndex = 0;

        wavelist = new ArrayList<>();
        towersList = new ArrayList<>();
        freezers = new ArrayList<>();
        // Map loader
        config = new ConfigLoader(this);
        config.loadConfig(configPath);
        List<WaveInfo> waveInfoList = config.getWaveInfoList();
        mana = new Mana(this, config);


        maploader = new Board(this,config ,mana, config.getLayout() );
        for (int i = 0; i < waveInfoList.size(); i++) {
            wavelist.add(new Waves(this, waveInfoList, maploader, mana));
        }
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
    @Override
    public void keyPressed(){
        if (gameOver && (key == 'r' || key == 'R')) {
            setup();
            gameWin = false;
            gameOver = false;
        }
        maploader.keyPressed();

    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){}

    @Override
    public void mousePressed(MouseEvent e) {
        maploader.mousePressed(e);
        towersList = maploader.getTowerList();
        freezers = maploader.getFreezers();

    }

    @Override
    public void mouseReleased(MouseEvent e) {}


    /**
     * Draw all elements in the game by current frame.
     */

    private int totalRemoved = 0;
    private int countTotalRemoved1() {
        totalRemoved = 0;
        for (int i = 0; i < wavelist.size(); i++) {
            totalRemoved += wavelist.get(i).getTotalRemoved();
        }
        return totalRemoved;
    }

    @Override
    public void draw() {
        if (gameOver) {
            pushStyle();
            textSize(32);
            fill(114, 227, 111);
            text("YOU LOST", width/2 - 126, height/2 - 114);
            textSize(20);
            text("Press 'r' to restart", width/2 - 142, height/2 - 62);
            popStyle();

        }
        else if (gameWin) {
            maploader.draw();
            pushStyle();
            textSize(32);
            fill(230, 57, 216);
            text("YOU WON", width/2 - 126, height/2 - 114);
            popStyle();
        }
        else if (!maploader.isGamePaused) {
            maploader.draw();
            mana.update();
            int origin = config.getTotoalquality();
            int remove = countTotalRemoved1();
            if (origin == remove) {
                gameWin = true;
            }

            // Update and draw only the current wave
            if (currentWaveIndex < wavelist.size()) {
                Waves currentWave = wavelist.get(currentWaveIndex);
                List<Monsters> activeMonsters = currentWave.getActiveMonsters();

                for (Towers tower : towersList) {
                    tower.setMonstersList(activeMonsters);
                    tower.display();
                }

                for (FreezeTower freezer : freezers) {
                    freezer.setMonstersList(activeMonsters);
                    freezer.display();
                }

                currentWave.update();
                currentWave.updateMonsters();

            }

            if (mana.getCurrentMana() == 0) {
                gameOver = true;

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
