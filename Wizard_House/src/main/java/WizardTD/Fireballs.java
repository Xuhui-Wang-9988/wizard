package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;

public class Fireballs {
    float pixelX, pixelY;
    float targetPixelX, targetPixelY;
    float speed;
    Monsters target;
    float damage;
    PApplet pApplet;
    PImage fireballImage;
    float fireballDisplaySize = 8;
    float maxTravelDistance;
    float traveledDistance;

    public Fireballs(PApplet pApplet, float startX, float startY, Monsters target, float damage, float speed, float range) {
        this.pApplet = pApplet;
        this.pixelX = startX;
        this.pixelY = startY + App.TOPBAR;
        this.target = target;
        this.damage = damage;
        this.speed = speed;
        this.targetPixelX = target.getPixelPosition()[0];
        this.targetPixelY = target.getPixelPosition()[1];
        this.fireballImage = pApplet.loadImage("src/main/resources/WizardTD/fireball.png");
        this.maxTravelDistance = range;
        this.traveledDistance = 0.0f;
    }

    public boolean move() {
        // Update the target's pixel position
        targetPixelX = target.getPixelPosition()[0];
        targetPixelY = target.getPixelPosition()[1];

        float dx = targetPixelX - pixelX;
        float dy = targetPixelY - pixelY;

        // Calculate the distance to the target
        float distance = PApplet.dist(pixelX, pixelY, targetPixelX, targetPixelY);

        // Combined radius of fireball and enemy
        float fireballRadius = fireballDisplaySize / 2; // Actual radius of the fireball (half of 6 pixels)
        float enemyRadius = 16.0f; // Radius of the enemy
        float combinedRadius = fireballRadius + enemyRadius;

        // If the fireball is very close to the target, consider it has reached the target
        if (distance <= combinedRadius) {
            pixelX = targetPixelX;
            pixelY = targetPixelY;
            if (!target.isDead()) {
                target.takeDamage((int) damage);
            }
            return true;
        } else {
            // Calculate unit vector
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            float ux = dx / length;
            float uy = dy / length;

            float oldPixelX = pixelX;
            float oldPixelY = pixelY;

            pixelX += ux * speed;
            pixelY += uy * speed;

            traveledDistance += PApplet.dist(oldPixelX, oldPixelY, pixelX, pixelY);

            if (traveledDistance >= maxTravelDistance) {
                return true;
            }

            return false;
        }
    }

    public void display() {
        pApplet.image(fireballImage, pixelX - fireballDisplaySize/2, pixelY - fireballDisplaySize/2, fireballDisplaySize, fireballDisplaySize);
    }
}

