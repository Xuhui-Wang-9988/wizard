package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;


public class Towers {

    private int[] gridPosition; // [row, col]
    private float[] pixelPosition; // [x, y]
    private Board board;
    private float cost;
    private float range;
    private float firingSpeed;
    private float damage;

    private PImage[] towerImages;
    private PApplet pApplet;

    private int rangeUpgrade = 0;
    private int speedUpgrade = 0;
    private int damageUpgrade = 0;


    public Towers(PApplet pApplet, Board board, int[] gridPosition, float[] pixelPosition, float cost, float range, float speed, float damage) {
        this.pApplet = pApplet;
        this.board = board;
        this.gridPosition = gridPosition;
        this.pixelPosition = new float[]{gridPosition[1] * App.CELLSIZE + App.CELLSIZE / 2, gridPosition[0] * App.CELLSIZE + App.CELLSIZE / 2}; // Convert grid position to pixel position

        // Initialize tower attributes from ConfigLoader

        this.cost = cost;
        this.range = range;
        this.firingSpeed = speed;
        this.damage = damage;

        towerImages = new PImage[3];
        for (int i = 0; i < 3; i++) {
            towerImages[i] = pApplet.loadImage("src/main/resources/WizardTD/tower" + i + ".png");
        }
    }

    public boolean isAvailablePlace() {
        return board.getGrid()[gridPosition[0]][gridPosition[1]] == 0; // Check if the cell is grass
    }

    public boolean isEnemyInRange(Monsters monster) {
        float[] enemyPixelPosition = monster.getPixelPosition();
        float distance = PApplet.dist(pixelPosition[0], pixelPosition[1], enemyPixelPosition[0], enemyPixelPosition[1]);
        return distance <= this.range;
    }

    //display function need to pick which image is best fit and what should be
    //added depends on the level of upgrade
    public void display() {
        int imageIndex = 0;
        if (speedUpgrade < 1 || rangeUpgrade < 1 || damageUpgrade < 1) {
            imageIndex = 0;
//            pickupSymbol(imageIndex, speedUpgrade, rangeUpgrade, damageUpgrade);
        }
        else if (speedUpgrade < 2 || rangeUpgrade < 2 || damageUpgrade < 2) {
            imageIndex = 1;
//            pickupSymbol(imageIndex, speedUpgrade, rangeUpgrade, damageUpgrade);
        }
        else if (speedUpgrade >= 2 && rangeUpgrade >= 2 && damageUpgrade >= 2) {
            imageIndex = 2;
//            pickupSymbol(imageIndex, speedUpgrade, rangeUpgrade, damageUpgrade);
        }


        pickupSymbol(imageIndex, speedUpgrade, rangeUpgrade, damageUpgrade);
        pApplet.image(towerImages[imageIndex], pixelPosition[0], pixelPosition[1]);

    }
    
    //Once level 1 of all upgrades is reached, 
    // the tower becomes orange (and no longer displays the indicators for level 1, only levels 2+). 
    // Similarly, if level 2 is reached in all upgrades, 
    // then it becomes red (and will only use the individual symbols X, O and blue border for levels 3+). 
    public void pickupSymbol(int imageIndex, int speedUpgrade, int rangeUpgrade, int damageUpgrade) {
        if (speedUpgrade > imageIndex) {
            // The speed upgrade level is denoted by the thickness of a blue border in the centre of the image.
            float borderThickness = speedUpgrade * 2; // Adjust this value as needed
            pApplet.stroke(0, 0, 255); // Blue color
            pApplet.strokeWeight(borderThickness);
            pApplet.noFill();
            pApplet.rect(pixelPosition[0], pixelPosition[1], towerImages[imageIndex].width, towerImages[imageIndex].height);
        }
        if (rangeUpgrade > imageIndex) {
            // The range upgrade level is denoted by magenta “O”s at the top of the image.
            pApplet.fill(255, 0, 255); // Magenta color
            pApplet.textSize(12); // Adjust this value as needed
            pApplet.text(repeat("O", rangeUpgrade), pixelPosition[0], pixelPosition[1] - 10); // Adjust the position as needed
        }
        if (damageUpgrade > imageIndex) {
            // The damage upgrade level is denoted by magenta “X”s at the bottom of the image.
            pApplet.fill(255, 0, 255); // Magenta color
            pApplet.textSize(12); // Adjust this value as needed
            pApplet.text(repeat("X", damageUpgrade), pixelPosition[0], pixelPosition[1] + towerImages[imageIndex].height + 10); // Adjust the position as needed
        }
    }

    public static Towers createTower(PApplet pApplet, Board board, int x, int y, float cost, float range, float speed, float damage) {
        int[] gridPosition = {(int) Math.floor(y / App.CELLSIZE), (int) Math.floor(x / App.CELLSIZE)};
        float[] pixelPosition = new float[]{x, y};

        if (gridPosition[0] < 0 || gridPosition[0] >= 20 || gridPosition[1] < 0 || gridPosition[1] >= 20) {
            return null; // 位置超出范围，返回null
        }

        Towers tower = new Towers(pApplet, board, gridPosition, pixelPosition, cost, range, speed, damage);
        if (tower.isAvailablePlace()) {
            return tower;
        }
        return null;
    }
    private String repeat(String str, int times) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            builder.append(str);
        }
        return builder.toString();
    }
}
