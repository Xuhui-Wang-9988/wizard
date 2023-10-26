package WizardTD;

import java.util.List;

public class WaveInfo {
    private int duration;
    private float preWavePause;
    private List<Monsters> monsters;
    private List<Integer> quantities;

    public WaveInfo(int duration, float preWavePause, List<Monsters> monsters, List<Integer> quantities) {
        this.duration = duration;
        this.preWavePause = preWavePause;
        this.monsters = monsters;
        this.quantities = quantities;
    }

    public float getPreWavePause() {
        return preWavePause;
    }
    public int getDuration() {
        return duration;
    }
    public List<Monsters> getMonsters() {return monsters;}
    public List<Integer> getQuantities() {
        return quantities;
    }

}


