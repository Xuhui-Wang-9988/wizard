package WizardTD;

import processing.core.PApplet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Waves {
    PApplet pApplet;
    List<WaveInfo> wavesInfo;
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

    public Waves(PApplet pApplet, List<WaveInfo> wavesInfo, Board board) {
        this.pApplet = pApplet;
        this.wavesInfo = wavesInfo;
        this.board = board;
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
        if (!isWaveActive) {
            preWavePause();
        } else {

            spawnMonsters();
        }
        frameCounter++;
    }

    private void preWavePause() {
        if (waveIndex < wavesInfo.size()) {
            pApplet.textSize(24);
            pApplet.text("Wave " + (waveIndex + 1) + " starts: " + (int) ((preWavePause - frameCounter / 60)), 10, 30);
        }
        if (frameCounter >= preWavePause * 60) {
            isWaveActive = true;
            spawnMonsters();  // 立刻开始当前波次
            frameCounter = 0;  // 重置frameCounter以开始下一个波次的倒计时
        }
    }

    private void spawnMonsters() {
        if (currentWaveInfo == null) return;

        if (frameCounter % (duration * 60 / currentWaveInfo.getQuantity()) == 0 && currentMonsterIndex < currentWaveInfo.getQuantity()) {
            Monsters newMonster = getMonsters(currentWaveInfo);
            activeMonsters.add(newMonster);
            currentMonsterIndex++;
        }

        if (currentMonsterIndex >= currentWaveInfo.getQuantity() && activeMonsters.isEmpty()) {
            isWaveComplete = true;
            isWaveActive = false;
            waveIndex++;
            updateCurrentWaveInfo();
        }
    }
    private Random random = new Random();


    private Monsters getMonsters(WaveInfo currentWaveInfo) {
        int[][] selectedPaths;
        if (random.nextBoolean()) {
            selectedPaths = Monsters.path1;
        }
        else {
            selectedPaths = Monsters.path2;
        }
        Monsters newMonster = new Monsters(
                pApplet,
                currentWaveInfo.getMonster().getType(),
                selectedPaths[0][0],
                selectedPaths[0][1],
                currentWaveInfo.getMonster().getSpeed(),
                currentWaveInfo.getMonster().getHP(),
                currentWaveInfo.getMonster().getArmour(),
                currentWaveInfo.getMonster().getManaGainedOnKill()
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

            if (monster.isDead()) {
                iterator.remove();
            } else if (monster.hasReachedEnd(board)) {
                iterator.remove();
            }
        }
    }

    public boolean isWaveComplete() {
        return isWaveComplete;
    }
}





