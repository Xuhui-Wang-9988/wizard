package WizardTD;

import processing.core.PApplet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Waves {
    PApplet pApplet;
    List<WaveInfo> wavesInfo;

    Mana mana;
    float preWavePause;
    int duration;
    Board board;
    List<Monsters> activeMonsters = new ArrayList<>();
    int currentMonsterIndex = 1;
    int frameCounter = 0;
    int waveIndex = 0;
    boolean isWaveActive = false;
    boolean isWaveComplete = false;
    WaveInfo currentWaveInfo;
    private int totalRemoved = 0;
    private final Random random = new Random();
    private int currentMonsterTypeIndex = 0;
    private int totalMonstersSpawnedForCurrentType = 0;

    public Waves(PApplet pApplet, List<WaveInfo> wavesInfo, Board board, Mana mana) {
        this.pApplet = pApplet;
        this.wavesInfo = wavesInfo;
        this.board = board;
        this.mana = mana;
        updateCurrentWaveInfo();
    }

    private void updateCurrentWaveInfo() {
        isWaveComplete = false;
        currentMonsterIndex = 0;
        frameCounter = 0;
        if (waveIndex < wavesInfo.size()) {
            currentWaveInfo = wavesInfo.get(waveIndex);
            this.preWavePause = currentWaveInfo.getPreWavePause();
            this.duration = currentWaveInfo.getDuration();
        } else {
            currentWaveInfo = null;
        }
    }

    public void update() {
        // Display the countdown on the top bar
        if (waveIndex < wavesInfo.size()) {
            pApplet.textSize(24);
            if (!isWaveActive) {
                pApplet.pushStyle();
                pApplet.fill(0);
                pApplet.text("Wave " + (waveIndex + 1) + " starts in: " + (int) Math.max(0, (preWavePause - frameCounter / 60)), 10, 30);
                pApplet.popStyle();
            }
        }

        if (!isWaveActive) {
            preWavePause();
        } else {
            spawnMonsters();
        }

        isWaveComplete = !isWaveActive && activeMonsters.isEmpty();
        frameCounter++;
    }

    private void preWavePause() {
        if (frameCounter >= preWavePause * 60 && currentWaveInfo != null) {
            isWaveActive = true;
            frameCounter = 0;
        }
    }

    private void spawnMonsters() {
        if (currentWaveInfo == null || waveIndex >= wavesInfo.size()) return;

        int spawnInterval = (duration * 60) / getTotalMonstersForWave();

        if (frameCounter % spawnInterval == 0 && totalMonstersSpawnedForCurrentType < currentWaveInfo.getQuantities().get(currentMonsterTypeIndex)) {
            Monsters monsterType = currentWaveInfo.getMonsters().get(currentMonsterTypeIndex);
            Monsters newMonster = getMonsters(monsterType);
            activeMonsters.add(newMonster);
            totalMonstersSpawnedForCurrentType++;

            if (totalMonstersSpawnedForCurrentType >= currentWaveInfo.getQuantities().get(currentMonsterTypeIndex)) {
                currentMonsterTypeIndex++;
                totalMonstersSpawnedForCurrentType = 0;
            }

            if (currentMonsterTypeIndex >= currentWaveInfo.getMonsters().size()) {
                isWaveActive = false;
                currentMonsterTypeIndex = 0;  // Reset for the next wave
                waveIndex++;
                updateCurrentWaveInfo();
                frameCounter = 0;  // Reset frameCounter for the next wave's countdown
            }
        }
    }

    private int getTotalMonstersForWave() {
        int total = 0;
        for (int quantity : currentWaveInfo.getQuantities()) {
            total += quantity;
        }
        return total;
    }

    private Monsters getMonsters(Monsters monster) {
        float[][] selectedPaths;
        if (random.nextBoolean()) {
            selectedPaths = Monsters.path1;
        } else {
            selectedPaths = Monsters.path2;
        }
        Monsters newMonster = new Monsters(
                pApplet,
                monster.getType(),
                selectedPaths[0][0],
                selectedPaths[0][1],
                monster.getSpeed(),
                monster.getHP(),
                monster.getArmour(),
                monster.getManaGainedOnKill()
        );
        newMonster.PathSelector(selectedPaths);
        return newMonster;
    }

    public void updateMonsters() {
        Iterator<Monsters> iterator = activeMonsters.iterator();
        while (iterator.hasNext()) {
            Monsters monster = iterator.next();
            monster.move();
            monster.display();
            if (monster.isDead() && !monster.hasAddedMana()) {
                float manaGainedOnKill = mana.getManaGainedMultiplier() * monster.getManaGainedOnKill();
                mana.setCurrentMana(mana.getCurrentMana()+ manaGainedOnKill);
                totalRemoved++;
                monster.setHasAddedMana(true);
            }
            if (monster.hasFinishedDeathAnimation()) {
                iterator.remove();
            } else if (monster.hasReachedEnd(board)) {
                if (!monster.hasReducedMana() && monster.getHP() != 0.0) {
                    mana.setCurrentMana(mana.getCurrentMana() - monster.getHP());
                    totalRemoved++;
                    monster.setHasReducedMana(true);
                }
                iterator.remove();
            }
        }
        if (!isWaveActive && activeMonsters.isEmpty()) {
            isWaveComplete = true;
        }
    }

    public int getTotalRemoved() {return totalRemoved;}

    public List<Monsters> getActiveMonsters() {
        return this.activeMonsters;
    }
}





