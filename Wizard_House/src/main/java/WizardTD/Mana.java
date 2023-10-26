package WizardTD;
import processing.core.PApplet;
public class Mana {
    private float currentMana;
    private float manaCap;
    private float manaGainedPerSecond;
    PApplet pApplet;
    ConfigLoader config;
    private float manaPoolMultiplier;

    public Mana(PApplet pApplet, ConfigLoader config) {
        this.pApplet = pApplet;
        this.config = config;

        this.currentMana = config.getInitial_mana();
        this.manaCap = config.getInitial_mana_cap();
        this.manaGainedPerSecond = config.getInitial_mana_gained_per_second();
        this.manaPoolMultiplier = 1;
    }

    public void update() {
        currentMana += manaGainedPerSecond / 60.0f;
        if(currentMana > manaCap) {
            currentMana = manaCap;
        }
        if(currentMana < 0) {
            currentMana = 0;
        }
    }

    public void setManaCap(float manaCap) {
        this.manaCap = manaCap;
    }

    public void setManaPoolMultiplier(float multiplier) {
        this.manaPoolMultiplier = multiplier;
    }

    public void increaseManaGainedMultiplier(float manaPoolMultiplier) {
        this.manaGainedPerSecond *= manaPoolMultiplier;
    }

    public void useMana(float amount) {
        currentMana -= amount;
        if(currentMana < 0) {
            currentMana = 0;
        }
    }
    public float getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(float currentMana) {
        this.currentMana = currentMana;
    }

    public float getManaCap() {
        return manaCap;
    }

    public float getManaGainedMultiplier() {
        return manaPoolMultiplier;
    }
}
