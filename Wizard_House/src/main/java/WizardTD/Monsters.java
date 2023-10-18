package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;

public class Monsters {
    String type;
    int x, y; // Grid Position
    float pixelX;
    float pixelY; // pixel Position
    float speed;
    float maxHP;
    float currentHP;
    float armour;
    float manaGainedOnKill;
    PImage[] enemyImages;
    int currentImageIndex = 0;
    int deathAnimationCounter = 0;

    private int pathIndex = 0; // 用于跟踪怪物当前在路径上的位置

    static int[][] path1 = {
            {0, 4},
            {4, 4},
            {4, 6},
            {16, 6},
            {16, 9},
            {10, 9},
            {10, 15},
            {3, 15}

    };
    static int[][] path2 = {
            {9, 1},
            {9, 6},
            {16, 6},
            {16, 9},
            {10, 9},
            {10, 15},
            {3, 15}

    };


    PApplet pApplet;

    public Monsters(PApplet pApplet, String type, int startX, int startY,
                    float speed, float HP, float armour, float manaGainedOnKill) {
        this.pApplet = pApplet;
        this.type = type;
        this.x = startX;
        this.y = startY;
        this.pixelX = convertToPixelCoordinate(startX);
        this.pixelY = convertToPixelCoordinate(startY);
        this.speed = speed;
        this.maxHP = HP;
        this.currentHP = HP;
        this.armour = armour;
        this.manaGainedOnKill = manaGainedOnKill;
        String[] imagePaths = {
                "src/main/resources/WizardTD/gremlin.png",
                "src/main/resources/WizardTD/gremlin1.png",
                "src/main/resources/WizardTD/gremlin2.png",
                "src/main/resources/WizardTD/gremlin3.png",
                "src/main/resources/WizardTD/gremlin4.png",
                "src/main/resources/WizardTD/gremlin5.png"};
        this.enemyImages = new PImage[imagePaths.length];

        for (int i = 0; i < imagePaths.length; i++) {
            this.enemyImages[i] = pApplet.loadImage(imagePaths[i]);
        }
    }

    private int[][] path;

    public void PathSelector(int[][] selectedPath) {
        this.path = selectedPath;
    }

    public void move() {
        if (pathIndex < path.length - 1) {
            int targetX = path[pathIndex + 1][0] ;
            int targetY = path[pathIndex + 1][1];

            float targetPixelX = convertToPixelCoordinate(targetX);
            float targetPixelY = convertToPixelCoordinate(targetY);

            float dx = targetPixelX - pixelX;
            float dy = targetPixelY - pixelY;

            // 计算单位向量
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            float ux = dx / length;
            float uy = dy / length;

            pixelX += ux * speed;
            pixelY += uy * speed;

            if (Math.abs(pixelX - targetPixelX) < speed && Math.abs(pixelY - targetPixelY) < speed) {
                pathIndex++;
                x = targetX;
                y = targetY;
                pixelX = x * App.CELLSIZE;
                pixelY = y * App.CELLSIZE;
            }
        } /*else {
            // 如果怪物已经到达路径的终点，你可以在这里添加其他的逻辑
            // 例如，减少玩家的生命值或者移除这个怪物对象
        }*/
    }

    public void display() {
        if (isDead()) {
            if (currentImageIndex < enemyImages.length - 1 && deathAnimationCounter % 4 == 0) {
                currentImageIndex++;
            }
            deathAnimationCounter++;
        }

        // Convert grid coordinates to pixel coordinates for drawing

        pApplet.image(enemyImages[currentImageIndex], pixelX, pixelY);
        displaycurrentHPBar();
    }

    private float convertToPixelCoordinate(int gridCoordinate) {
        return gridCoordinate * App.CELLSIZE;
    }


    public void takeDamage(int damage) {
        currentHP -= damage * (1 - armour); // Reduce currentHP by the damage amount, considering armour
    }

    public boolean isDead() {
        return currentHP <= 0;
    }

    private void displaycurrentHPBar() {
        float currentHPPercentage = (float) currentHP / maxHP;
        pApplet.fill(255, 0, 0); // Red color for the background of the currentHP bar
        pApplet.rect(pixelX, pixelY - 10, enemyImages[0].width, 5); // Draw background
        pApplet.fill(0, 255, 0); // Green color for the actual currentHP bar
        pApplet.rect(pixelX, pixelY - 10, enemyImages[0].width * currentHPPercentage, 5); // Draw currentHP
    }


    public boolean hasReachedEnd(Board board) {
//        int[] wizardHousePosition = board.findWizardHousePosition();

        // Check if the monster's position is the same as the wizard house's position
//        return x == wizardHousePosition[1] && y == wizardHousePosition[0];
        return x == 3 && y == 15;
    }

    public float[] getPixelPosition() {
        return new float[] {pixelX, pixelY};
    }


    public float getSpeed() {
        return speed;
    }

    public String getType() {
        return type;
    }

    public float getHP() {
        return maxHP;
    }

    public float getArmour() {
        return armour;
    }

    public float getManaGainedOnKill() {
        return manaGainedOnKill;
    }
}
