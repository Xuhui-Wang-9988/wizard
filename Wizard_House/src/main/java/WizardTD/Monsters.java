package WizardTD;
import processing.core.PApplet;
import processing.core.PImage;

public class Monsters {
    String type;
    float x;
    float y;
    float pixelX;
    float pixelY;
    float speed;
    float maxHP;
    float currentHP;
    float armour;
    float manaGainedOnKill;
    PImage[] enemyImages;
    int currentImageIndex = 0;
    int deathAnimationCounter = 0;
    boolean isFrozen;
    private int freezeDurationRemaining;
    private int pathIndex = 0;
    private boolean hasAddedMana = false;
    private boolean hasReducedMana = false;
    static float[][] path1 = {
            {0, 4},
            {4, 4},
            {4, 6},
            {16, 6},
            {16, 9},
            {10, 9},
            {10, 15},
            {3, 15}

    };
    static float[][] path2 = {
            {9, 1},
            {9, 6},
            {16, 6},
            {16, 9},
            {10, 9},
            {10, 15},
            {3, 15}

    };

    PApplet pApplet;

    public Monsters(PApplet pApplet, String type, float startX, float startY,
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
        this.isFrozen = false;
        this.freezeDurationRemaining = 0;
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

    private float[][] path;

    public void PathSelector(float[][] selectedPath) {
        this.path = selectedPath;
    }

    public void freeze(float freezeDuration) {
        isFrozen = true;
        freezeDurationRemaining = Math.round(freezeDuration / (1.0f / 60));
    }

    public void move() {
        if (isFrozen) {
            freezeDurationRemaining--;
            if (freezeDurationRemaining <= 0) {
                isFrozen = false;
            }
        }
        else if (pathIndex < path.length - 1) {
            float targetX = path[pathIndex + 1][0] ;
            float targetY = path[pathIndex + 1][1];

            float targetPixelX = convertToPixelCoordinate(targetX);
            float targetPixelY = convertToPixelCoordinate(targetY);

            float dx = targetPixelX - pixelX;
            float dy = targetPixelY - pixelY;

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
        }
    }

    public boolean playDeathAnimation() {
        if (deathAnimationCounter % 4 == 0) {
            if (currentImageIndex < enemyImages.length - 1) {
                currentImageIndex++;
            } else {
                return true;
            }
        }
        deathAnimationCounter++;
        return false;
    }

    public void display() {
        if (isDead()) {
            boolean animationFinished = playDeathAnimation();
            if (animationFinished) {
                return;
            }
        }

        pApplet.image(enemyImages[currentImageIndex], pixelX, pixelY);
        displaycurrentHPBar();
    }

    private float convertToPixelCoordinate(float gridCoordinate) {
        return gridCoordinate * App.CELLSIZE;
    }

    public void takeDamage(int damage) {
        if (this.currentHP - damage * (1 - armour) < 0) {
            this.currentHP = 0;
        }
        else {
            this.currentHP -= damage * (1 - armour);
        }
    }

    public boolean isDead() {
        return currentHP <= 0;
    }

    private void displaycurrentHPBar() {
        float currentHPPercentage =  currentHP / maxHP;
        pApplet.pushStyle();
        pApplet.noFill();
        pApplet.stroke(0,0,0);
        pApplet.rect(pixelX, pixelY - 10, enemyImages[0].width, 5);
        pApplet.fill(255, 0, 0);
        pApplet.rect(pixelX, pixelY - 10, enemyImages[0].width, 5);
        pApplet.noFill();
        pApplet.fill(0, 255, 0);
        pApplet.rect(pixelX, pixelY - 10, enemyImages[0].width * currentHPPercentage, 5);
        pApplet.popStyle();
    }

    private boolean isDead = false;

    public boolean hasFinishedDeathAnimation() {
        return isDead && deathAnimationCounter / 4 >= enemyImages.length;
    }


    public boolean hasReachedEnd(Board board) {
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
        return currentHP;
    }
    public float getArmour() {
        return armour;
    }
    public float getManaGainedOnKill() {
        return manaGainedOnKill;
    }
    public void setHasAddedMana(boolean hasAddedMana) {
        this.hasAddedMana = hasAddedMana;
    }
    public boolean hasAddedMana() {
        return hasAddedMana;
    }
    public boolean hasReducedMana() {
        return hasReducedMana;
    }
    public void setHasReducedMana(boolean hasReducedMana) {
        this.hasReducedMana = hasReducedMana;
    }
}
