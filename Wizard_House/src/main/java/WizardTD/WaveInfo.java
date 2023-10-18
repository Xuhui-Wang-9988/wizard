package WizardTD;
import processing.data.JSONArray;
import processing.data.JSONObject;

import java.util.List;

public class WaveInfo {
    private int duration;
    private float preWavePause;
    private Monsters monster;
    private int quantity;

    public WaveInfo(int duration, float preWavePause, Monsters monster, int quantity) {
        this.duration = duration;
        this.preWavePause = preWavePause;
        this.monster = monster;
        this.quantity = quantity;
    }

    // Getters

    public float getPreWavePause() {
        return preWavePause;
    }

    public int getDuration() {
        return duration;
    }

    public Monsters getMonster() {
        return monster;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters
    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setPreWavePause(int preWavePause) {
        this.preWavePause = preWavePause;
    }

    public void setMonster(Monsters monster) {
        this.monster = monster;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}


