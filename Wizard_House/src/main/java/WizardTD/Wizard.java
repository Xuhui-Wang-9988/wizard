package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;

public class Wizard {
    PApplet pApplet;
    Board board;
    PImage houseImage;
    int[] housePosition;
    int houseSize = 48;

    public Wizard(PApplet p, Board b) {
        this.pApplet = p;
        this.board = b;
        this.houseImage = pApplet.loadImage("src/main/resources/WizardTD/wizard_house.png");
        this.housePosition = board.findWizardHousePosition();
    }

    public void draw() {
        if (housePosition[0] != -1 && housePosition[1] != -1) {
            int renderX = housePosition[1] * App.CELLSIZE - (houseSize - App.CELLSIZE) / 2;
            int renderY = housePosition[0] * App.CELLSIZE - (houseSize - App.CELLSIZE) + 5;
            pApplet.image(houseImage, renderX, renderY, houseSize, houseSize);
        }
    }
}