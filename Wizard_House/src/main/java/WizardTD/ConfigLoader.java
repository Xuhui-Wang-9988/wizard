package WizardTD;

import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ConfigLoader {
    private PApplet pApplet;
    private List<WaveInfo> waveInfoList = new ArrayList<>();
    private String layout;
    private float initialTowerRange;
    private float initial_tower_firing_speed;
    private float initial_tower_damage;
    private float initial_mana;
    private float initial_mana_cap;
    private float initial_mana_gained_per_second;
    private float tower_cost;
    private float mana_pool_spell_initial_cost;
    private float mana_pool_spell_cost_increase_per_use;
    private float mana_pool_spell_cap_multiplier;
    private float mana_pool_spell_mana_gained_multiplier;

    private int totoalquality; // use for winner check

    public ConfigLoader(PApplet pApplet) {
        this.pApplet = pApplet;
    }

    public void loadConfig(String configPath) {
        JSONObject config = pApplet.loadJSONObject(configPath);

        layout = config.getString("layout");
        initialTowerRange = config.getFloat("initial_tower_range");
        initial_tower_firing_speed = config.getFloat("initial_tower_firing_speed");
        initial_tower_damage = config.getFloat("initial_tower_damage");
        initial_mana = config.getFloat("initial_mana");
        initial_mana_cap = config.getFloat("initial_mana_cap");
        initial_mana_gained_per_second = config.getFloat("initial_mana_gained_per_second");
        tower_cost = config.getFloat("tower_cost");
        mana_pool_spell_initial_cost = config.getFloat("mana_pool_spell_initial_cost");
        mana_pool_spell_cost_increase_per_use = config.getFloat("mana_pool_spell_cost_increase_per_use");
        mana_pool_spell_cap_multiplier = config.getFloat("mana_pool_spell_cap_multiplier");
        mana_pool_spell_mana_gained_multiplier = config.getFloat("mana_pool_spell_mana_gained_multiplier");

        JSONArray waves = config.getJSONArray("waves");
        for (int i = 0; i < waves.size() ; i++) {
            JSONObject wave = waves.getJSONObject(i);
            int duration = wave.getInt("duration");
            int preWavePause = wave.getInt("pre_wave_pause");

            // Accessing monsters within a wave
            JSONArray monsters = wave.getJSONArray("monsters");
            List<Monsters> monstersList = new ArrayList<>();
            List<Integer> quantitiesList = new ArrayList<>();
            for (int j = 0; j < monsters.size(); j++) {
                JSONObject monsterJson = monsters.getJSONObject(j);
                String type = monsterJson.getString("type");
                float HP = monsterJson.getFloat("hp");
                float speed = monsterJson.getFloat("speed");
                float armour = monsterJson.getFloat("armour");
                float manaGainedOnKill = monsterJson.getFloat("mana_gained_on_kill");
                int quantity = monsterJson.getInt("quantity");
                totoalquality+= quantity;

                int[] startPoint = {0, 0};
                Monsters newMonster = new Monsters(pApplet, type, startPoint[0], startPoint[1], speed, HP, armour, manaGainedOnKill);
                monstersList.add(newMonster);
                quantitiesList.add(quantity);
            }
            WaveInfo waveInfo = new WaveInfo(duration, preWavePause, monstersList, quantitiesList);
            waveInfoList.add(waveInfo);
        }
    }

    // Getters
    public List<WaveInfo> getWaveInfoList() {
        return waveInfoList;
    }
    public String getLayout() {
        return layout;
    }
    public float getInitial_mana() {return initial_mana;}
    public float getInitial_mana_cap() {return initial_mana_cap;}
    public float getInitial_mana_gained_per_second() {return initial_mana_gained_per_second;}
    public float getInitialTowerRange() {
        return initialTowerRange;
    }
    public float getManaPoolSpellInitialCost() {
        return mana_pool_spell_initial_cost;
    }
    public float getManaPoolSpellCostIncreasePerUse() {
        return mana_pool_spell_cost_increase_per_use;
    }
    public float getManaPoolSpellCapMultiplier() {
        return mana_pool_spell_cap_multiplier;
    }
    public float getManaPoolSpellManaGainedMultiplier() {
        return mana_pool_spell_mana_gained_multiplier;
    }
    public float getTower_cost() {
        return tower_cost;
    }
    public float getInitial_tower_damage() {
        return initial_tower_damage;
    }
    public float getInitial_tower_firing_speed() {
        return initial_tower_firing_speed;
    }
    public int getTotoalquality() {return totoalquality;}
}

