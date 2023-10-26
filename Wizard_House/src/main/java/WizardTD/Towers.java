package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

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
    private ConfigLoader config;
    private int rangeUpgrade = 0;
    private int speedUpgrade = 0;
    private int damageUpgrade = 0;
    protected List<Monsters> monsters;
    private List<Fireballs> activeFireballs = new ArrayList<>();
    private boolean isInUpgradeMode = false;

    public Towers(PApplet pApplet, Board board, int[] gridPosition, ConfigLoader config) {
        this.pApplet = pApplet;
        this.board = board;
        this.gridPosition = gridPosition;
        this.pixelPosition = new float[]{gridPosition[1] * App.CELLSIZE + App.CELLSIZE / 2,
                gridPosition[0] * App.CELLSIZE + App.CELLSIZE / 2};
        this.config = config;
        this.monsters = new ArrayList<>();
        this.cost = config.getTower_cost();
        this.range = config.getInitialTowerRange();
        this.firingSpeed = config.getInitial_tower_firing_speed();
        this.damage = config.getInitial_tower_damage();

        towerImages = new PImage[3];
        for (int i = 0; i < 3; i++) {
            towerImages[i] = pApplet.loadImage("src/main/resources/WizardTD/tower" + i + ".png");
        }
    }

    public void setMonstersList(List<Monsters> monsters) {
        this.monsters = monsters;
    }

    public Monsters getRandomEnemyInRange() {
        if (this.monsters == null || this.monsters.isEmpty()) {
            return null;
        }

        List<Monsters> enemiesInRange = new ArrayList<>();

        for (Monsters monster : this.monsters) {
            if (!monster.isDead()) {
                float[] enemyPixelPosition = monster.getPixelPosition();
                float distance = PApplet.dist(pixelPosition[0], pixelPosition[1],
                        enemyPixelPosition[0], enemyPixelPosition[1]);
                if (distance <= this.range) {
                    enemiesInRange.add(monster);
                }
            }
        }

        if (enemiesInRange.isEmpty()) {
            return null;
        }

        int randomIndex = (int) (Math.random() * enemiesInRange.size());
        return enemiesInRange.get(randomIndex);
    }

    public Fireballs attack() {
        Monsters target = getRandomEnemyInRange();
        if (target != null) {
            return new Fireballs(pApplet, pixelPosition[0], pixelPosition[1], target, damage, firingSpeed, range);
        }
        return null;
    }

    public void upgradeRange() {
        if (rangeUpgrade < 5) {
            range += 32;
            rangeUpgrade++;
        }
    }

    public void upgradeSpeed() {
        if (speedUpgrade < 5) {
            firingSpeed += 0.5f;
            speedUpgrade++;
        }
    }

    public void upgradeDamage() {
        if (damageUpgrade < 5) {
            damage += damage / 2;
            damageUpgrade++;
        }
    }

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
                pApplet.text("Range: " + (int) getRangeUpgradeCost(), x + 10, y + 15);
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
                pApplet.text("Speed: " + (int) getSpeedUpgradeCost(), x + 10, y + 15);
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
                pApplet.text("Damage: " + (int) getDamageUpgradeCost(), x + 10, y + 15);
            }
            y += 20;
        }

        pApplet.fill(255);
        pApplet.rect(x, y, 100, 20);
        pApplet.fill(0);
        pApplet.text("Total: " + totalCost, x + 10, y + 15);

        pApplet.popStyle();
    }

    public float getTowerInitialCost() {
        return this.cost;
    }

    public float getRangeUpgradeCost() {
        return 20 + rangeUpgrade * 20;
    }

    public float getSpeedUpgradeCost() {
        return 20 + speedUpgrade * 20;
    }

    public float getDamageUpgradeCost() {
        return 20 + damageUpgrade * 20;
    }

    public float getTotalUpgradeCost() {
        EnumSet<Board.GameAction> currentActions = board.getCurrentActions();
        float totalCost = 0;

        for (Board.GameAction action : currentActions) {
            switch (action) {
                case UPGRADE_RANGE:
                    totalCost += getRangeUpgradeCost();
                    break;
                case UPGRADE_SPEED:
                    totalCost += getSpeedUpgradeCost();
                    break;
                case UPGRADE_DAMAGE:
                    totalCost += getDamageUpgradeCost();
                    break;
            }
        }

        return totalCost;
    }

    public boolean isInUpgradeMode() {
        EnumSet<Board.GameAction> currentActions = board.getCurrentActions();
        if (currentActions.contains(Board.GameAction.UPGRADE_RANGE) ||
                currentActions.contains(Board.GameAction.UPGRADE_SPEED) ||
                currentActions.contains(Board.GameAction.UPGRADE_DAMAGE)) {
            return true;
        }
        return false;
    }

    private int frameCounter = 0;

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

        isInUpgradeMode = isInUpgradeMode();
        if (isInUpgradeMode && isMouseOver(pApplet.mouseX, pApplet.mouseY)) {
            displayUpgradeInfoBox();
        }

        frameCounter++;

        if (frameCounter >= 60 * (1 / firingSpeed)) {
            Fireballs fireball = attack();
            if (fireball != null) {
                activeFireballs.add(fireball);
            }
            frameCounter = 0;
        }

        Iterator<Fireballs> iterator = activeFireballs.iterator();
        while (iterator.hasNext()) {
            Fireballs fireball = iterator.next();
            boolean hasReached = fireball.move();
            fireball.display();
            if (hasReached) {
                iterator.remove();
            }
        }
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        float distanceFromCenter = PApplet.dist(mouseX, mouseY, pixelPosition[0], pixelPosition[1] + App.TOPBAR);
        return distanceFromCenter <= towerImages[0].width / 2;
    }

    public void pickupSymbol(int imageIndex, int speedUpgrade, int rangeUpgrade, int damageUpgrade) {
        if (speedUpgrade > imageIndex) {
            pApplet.pushStyle();

            float borderThickness = (speedUpgrade - imageIndex) * 1.02f;
            pApplet.stroke(129, 179, 252);
            pApplet.strokeWeight(borderThickness);
            pApplet.noFill();
            pApplet.rect(pixelPosition[0] - 8, pixelPosition[1] + App.TOPBAR - 7, 15,15);
            pApplet.popStyle();
        }
        if (rangeUpgrade > imageIndex) {

            pApplet.pushStyle();
            pApplet.fill(255, 0, 255);
            pApplet.textSize(8);
            pApplet.text(repeat("O", rangeUpgrade - imageIndex),
                    pixelPosition[0]-15, pixelPosition[1] - 10 + App.TOPBAR);
            pApplet.popStyle();
        }
        if (damageUpgrade > imageIndex) {
            pApplet.pushStyle();
            pApplet.fill(255, 0, 255);
            pApplet.textSize(8);
            pApplet.text(repeat("X", damageUpgrade - imageIndex),
                    pixelPosition[0]-15, pixelPosition[1]
                    + 15 + App.TOPBAR);
            pApplet.popStyle();
        }
    }

    public boolean isAvailablePlace() {
        int[][] grid = board.getGrid();
        int currentCell = grid[gridPosition[0]][gridPosition[1]];
        if (currentCell != 0) {
            return false;
        }

        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};

        for (int i = 0; i < 8; i++) {
            int newRow = gridPosition[0] + dx[i];
            int newCol = gridPosition[1] + dy[i];

            if (newRow >= 0 &&
                    newRow < grid.length &&
                    newCol >= 0 &&
                    newCol < grid[0].length &&
                    grid[newRow][newCol] == 3)
            {
                return false;
            }
        }

        return true;
    }

    public static Towers createTower(PApplet pApplet, Board board, int x, int y, ConfigLoader config) {

        int[] gridPosition = {(int) Math.floor((y - App.TOPBAR) / App.CELLSIZE), (int) Math.floor(x / App.CELLSIZE)};
        float[] pixelPosition = new float[]{
                gridPosition[1] * App.CELLSIZE + App.CELLSIZE / 2,
                gridPosition[0] * App.CELLSIZE + App.CELLSIZE / 2
        };

        if (gridPosition[0] < 0 || gridPosition[0] >= 20 || gridPosition[1] < 0 || gridPosition[1] >= 20) {
            return null;
        }

        Towers tower = new Towers(pApplet, board, gridPosition, config);
        if (tower.isAvailablePlace()) {
            board.getGrid()[gridPosition[0]][gridPosition[1]] = 4;
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
