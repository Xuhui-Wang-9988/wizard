package WizardTD;

import processing.core.PApplet;

public class ManaPool {

    private Mana mana;
    private float spellInitialCost;
    private float spellCostIncreasePerUse;
    private float spellCapMultiplier;
    private float spellManaGainedMultiplier;
    private int spellUses;
    private PApplet pApplet;
    ConfigLoader config;

    public ManaPool(Mana mana, PApplet pApplet, ConfigLoader config) {
        this.mana = mana;
        this.pApplet = pApplet;
        this.config = config;

        this.spellInitialCost = config.getManaPoolSpellInitialCost();
        this.spellCostIncreasePerUse = config.getManaPoolSpellCostIncreasePerUse();
        this.spellCapMultiplier = config.getManaPoolSpellCapMultiplier();
        this.spellManaGainedMultiplier = config.getManaPoolSpellManaGainedMultiplier();
        this.spellUses = 0;
    }

    public void castManaPoolSpell() {
        if (mana.getCurrentMana() >= getCurrentSpellCost()) {
            mana.useMana(getCurrentSpellCost());
            spellUses++;
            mana.setManaCap(mana.getManaCap() * spellCapMultiplier);
            mana.setManaPoolMultiplier(1 + (spellManaGainedMultiplier - 1) * spellUses);
        }
    }

    public float getCurrentSpellCost() {
        return spellInitialCost + spellCostIncreasePerUse * spellUses;
    }

    public void displayManaBar() {
        float barWidth = 320;
        float barHeight = 20;
        float xPos = pApplet.width - barWidth -40;
        float yPos = 10;

        pApplet.pushStyle();
        pApplet.stroke(0);
        pApplet.strokeWeight(1.8f);
        pApplet.textSize(22);
        pApplet.fill(255);
        pApplet.rect(xPos, yPos, barWidth, barHeight);
        pApplet.fill(0);
        pApplet.text("MANA:",pApplet.width - barWidth -115, 29);
        pApplet.popStyle();

        float percentageFilled = mana.getCurrentMana() / mana.getManaCap();
        float fillWidth = Math.max(0, barWidth * percentageFilled);

        pApplet.pushStyle();
        pApplet.fill(4, 208, 214);
        pApplet.strokeWeight(1.8f);
        pApplet.stroke(0);
        pApplet.rect(xPos, yPos, fillWidth, barHeight);
        pApplet.textSize(22);
        String manaText = (int) mana.getCurrentMana() + " / " + (int) mana.getManaCap();
        pApplet.fill(0);
        pApplet.text(manaText, xPos + 90, yPos + 19);
        pApplet.popStyle();
    }
}

