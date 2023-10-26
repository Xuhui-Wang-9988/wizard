package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class FreezeTower extends Towers {
    private PApplet pApplet;
    private Board board;
    private PImage[] towerImages;
    private ConfigLoader config;
    private float[] pixelPosition;
    private float cost;
    private int rangeUpgrade = 0;
    private int speedUpgrade = 0;
    private int damageUpgrade = 0;
    private float freezeDuration;
    private float firingInterval;
    private float range;
    private int frameCounter = 0;
    private boolean isInUpgradeMode = false;
    private float attackAnimationDuration = 0;

    public FreezeTower(PApplet pApplet, Board board, int[] gridPosition, float[] pixelPosition, ConfigLoader config) {
        super(pApplet, board, gridPosition, config);
        this.pApplet = pApplet;
        this.board = board;
        this.pixelPosition = pixelPosition;
        this.config = config;
        this.cost = config.getTower_cost() * 3;
        this.range = config.getInitialTowerRange();
        this.firingInterval = 8;
        this.freezeDuration = 3;
        towerImages = new PImage[3];
        for (int i = 0; i < 3; i++) {
            towerImages[i] = pApplet.loadImage("src/main/resources/WizardTD/tower" + i + ".png");
        }
        pApplet.loadPixels();
        for (int i = 0; i < towerImages.length; i++) {
            towerImages[i].loadPixels();
            for (int j = 0; j < towerImages[i].pixels.length; j++) {
                int pixelColor = towerImages[i].pixels[j];
                int r = (pixelColor >> 16) & 0xFF;
                int g = (pixelColor >> 8) & 0xFF;
                int b = pixelColor & 0xFF;

                r *= 0.5f;
                g *= 0.5f;

                towerImages[i].pixels[j] = pApplet.color(r, g, b);
            }
            towerImages[i].updatePixels();
        }
        pApplet.updatePixels();

    }

    @Override
    public void upgradeRange() {
        if (rangeUpgrade < 5) {
            rangeUpgrade++;
            this.range += 32;
        }
    }

    @Override
    public void upgradeSpeed() {
        if (speedUpgrade < 5) {
            speedUpgrade++;
            this.firingInterval -= 0.5f;
        }
    }

    @Override
    public void upgradeDamage() {
        if (damageUpgrade < 5) {
            damageUpgrade++;
            this.freezeDuration += 0.2f;
        }
    }

    @Override
    public void displayUpgradeInfoBox() {
        EnumSet<Board.GameAction> currentActions = board.getCurrentActions();

        int x = 650;
        int y = 520;
        int totalCost = (int) getTotalUpgradeCost();
        pApplet.pushStyle();
        pApplet.fill(255);
        pApplet.stroke(0);
        pApplet.strokeWeight(1.5f);
        pApplet.textSize(12);

        pApplet.rect(x, y, 100, 20);
        pApplet.fill(0);
        pApplet.text("Upgrade cost", x + 10, y + 15);
        y += 20;

        if (currentActions.contains(Board.GameAction.UPGRADE_RANGE)) {
            pApplet.fill(255);
            pApplet.rect(x, y, 100, 20);
            pApplet.fill(0);
            if (rangeUpgrade == 5) {
                pApplet.text("Max level!!! ", x + 10, y + 15);
                totalCost -= 120;
            }
            else {
                pApplet.text("Range: " + (int) super.getRangeUpgradeCost(), x + 10, y + 15);
            }
            y += 20;
        }

        if (currentActions.contains(Board.GameAction.UPGRADE_SPEED)) {
            pApplet.fill(255);
            pApplet.rect(x, y, 100, 20);
            pApplet.fill(0);
            if (speedUpgrade == 5) {
                pApplet.text("Max level!!! ", x + 10, y + 15);
                totalCost -= 120;
            }
            else {
                pApplet.text("Interval: " + (int) getSpeedUpgradeCost(), x + 10, y + 15);
            }
            y += 20;
        }

        if (currentActions.contains(Board.GameAction.UPGRADE_DAMAGE)) {
            pApplet.fill(255);
            pApplet.rect(x, y, 100, 20);
            pApplet.fill(0);
            if (damageUpgrade == 5) {
                pApplet.text("Max level!!! ", x + 10, y + 15);
                totalCost -= 120;
            }
            else {
                pApplet.text("Duration: " + (int) getDamageUpgradeCost(), x + 10, y + 15);
            }
            y += 20;
        }

        pApplet.fill(255);
        pApplet.rect(x, y, 100, 20);
        pApplet.fill(0);
        pApplet.text("Total: " + totalCost, x + 10, y + 15);

        pApplet.popStyle();
    }

    @Override
    public float getRangeUpgradeCost() {
        return 20 + rangeUpgrade * 20;
    }

    @Override
    public float getSpeedUpgradeCost() {
        return 20 + speedUpgrade * 20;
    }

    @Override
    public float getDamageUpgradeCost() {
        return 20 + damageUpgrade * 20;
    }

    public List<Monsters> getAllEnemiesInRange() {
        List<Monsters> enemiesInRange = new ArrayList<>();

        if (this.monsters == null || this.monsters.isEmpty()) {
            return enemiesInRange;
        }
        for (Monsters monster : this.monsters) {
            if (!monster.isDead()) {
                float[] enemyPixelPosition = monster.getPixelPosition();
                float distance = PApplet.dist(pixelPosition[0], pixelPosition[1],
                        enemyPixelPosition[0], enemyPixelPosition[1] - App.TOPBAR);
                System.out.println("Distance to monster: " + distance + ", Tower range: " + this.range);
                if (distance <= this.range) {
                    enemiesInRange.add(monster);
                }
            }
        }
        return enemiesInRange;
    }

    @Override
    public Fireballs attack() {

        List<Monsters> enemiesInRange = getAllEnemiesInRange();
        for (Monsters enemy : enemiesInRange) {
            if (!enemy.isFrozen) {
                enemy.freeze(freezeDuration);
            }
        }
        return null;
    }

    @Override
    public void pickupSymbol(int imageIndex, int speedUpgrade, int rangeUpgrade, int damageUpgrade) {
        super.pickupSymbol(imageIndex, speedUpgrade, rangeUpgrade, damageUpgrade);
    }

    @Override
    public boolean isAvailablePlace() {
        return super.isAvailablePlace();
    }

    @Override
    public void display() {
        int imageIndex = 0;
        if (speedUpgrade < 1 || rangeUpgrade < 1 || damageUpgrade < 1) {
            imageIndex = 0;
        }
        else if (speedUpgrade < 2 || rangeUpgrade < 2 || damageUpgrade < 2) {
            imageIndex = 1;
        }
        else if (speedUpgrade >= 2 && rangeUpgrade >= 2 && damageUpgrade >= 2) {
            imageIndex = 2;
        }

        float imageX = pixelPosition[0] - towerImages[imageIndex].width / 2;
        float imageY = pixelPosition[1] - towerImages[imageIndex].height / 2 + App.TOPBAR;
        pApplet.image(towerImages[imageIndex], imageX, imageY);
        pickupSymbol(imageIndex, speedUpgrade, rangeUpgrade, damageUpgrade);

        float distanceFromCenter = PApplet.dist(pApplet.mouseX, pApplet.mouseY, pixelPosition[0], pixelPosition[1] + App.TOPBAR);
        if (distanceFromCenter <= towerImages[0].width / 2) {
            pApplet.noFill();
            pApplet.stroke(255, 255, 0);
            pApplet.ellipse(pixelPosition[0], pixelPosition[1] + App.TOPBAR, range * 2, range * 2);
        }
        isInUpgradeMode = super.isInUpgradeMode();
        if (isInUpgradeMode && isMouseOver(pApplet.mouseX, pApplet.mouseY)) {
            displayUpgradeInfoBox();
        }

        frameCounter++;
        if (frameCounter >= 60 * firingInterval) {
            attackAnimationDuration = 30;
            attack();
            frameCounter = 0;
        }

        if (attackAnimationDuration > 0) {
            pApplet.fill(4, 208, 214, 128);
            pApplet.strokeWeight(1.5f);
            pApplet.stroke(4, 208, 214);
            pApplet.ellipse(pixelPosition[0], pixelPosition[1] + App.TOPBAR, range * 2, range * 2);
            attackAnimationDuration--;
        }
    }

    public static FreezeTower createTower(PApplet pApplet, Board board, int x, int y, ConfigLoader config) {
        int[] gridPosition = {(int) Math.floor((y - App.TOPBAR) / App.CELLSIZE), (int) Math.floor(x / App.CELLSIZE)};
        float[] pixelPosition = new float[]{
                gridPosition[1] * App.CELLSIZE + App.CELLSIZE / 2,
                gridPosition[0] * App.CELLSIZE + App.CELLSIZE / 2
        };

        if (gridPosition[0] < 0 || gridPosition[0] >= 20 || gridPosition[1] < 0 || gridPosition[1] >= 20) {
            return null;
        }

        FreezeTower tower = new FreezeTower(pApplet, board, gridPosition, pixelPosition, config);
        if (tower.isAvailablePlace()) {
            board.getGrid()[gridPosition[0]][gridPosition[1]] = 4;
            return tower;
        }
        return null;
    }

    @Override
    public float getTowerInitialCost() {
        return cost;
    }
}


