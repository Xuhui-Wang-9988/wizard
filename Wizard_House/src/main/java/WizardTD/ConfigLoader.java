package WizardTD;

import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ConfigLoader {
    private PApplet pApplet;
    private List<WaveInfo> waveInfoList = new ArrayList<>();

    private Monsters monster;

//    private WaveInfo waves;

    // Other config variables...
    private String layout;
    private float initialTowerRange;
    // ... add other config variables here
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
    private int duration;
    private int preWavePause;
    private String type;
    private float HP;
    private float speed;
    private float armour;
    private float mana_gained_on_kill;
    private int quantity;

    public ConfigLoader(PApplet pApplet) {
        this.pApplet = pApplet;
    }

    public void loadConfig(String configPath) {
        // Load and parse the JSON data
        JSONObject config = pApplet.loadJSONObject(configPath);

        // Accessing basic attributes
        layout = config.getString("layout");
        initialTowerRange = config.getFloat("initial_tower_range");
        // ... load other config variables here
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

        // Accessing array of waves
        JSONArray waves = config.getJSONArray("waves");
        for (int i = 0; i < waves.size() ; i++) {
            JSONObject wave = waves.getJSONObject(i);
            int duration = wave.getInt("duration");
            int preWavePause = wave.getInt("pre_wave_pause");

            // Accessing monsters within a wave
            JSONArray monsters = wave.getJSONArray("monsters");
            for (int j = 0; j < monsters.size(); j++) {
                JSONObject monster = monsters.getJSONObject(j);
                String type = monster.getString("type");
                float HP = monster.getFloat("hp");
                float speed = monster.getFloat("speed");
                float armour = monster.getFloat("armour");
                float manaGainedOnKill = monster.getFloat("mana_gained_on_kill");
                int quantity = monster.getInt("quantity");

                int[] startPoint = {0, 0};
                Monsters newMonster = new Monsters(pApplet, type, startPoint[0], startPoint[1], speed, HP, armour, manaGainedOnKill);
                WaveInfo waveInfo = new WaveInfo(duration, preWavePause, newMonster, quantity);
                waveInfoList.add(waveInfo);
            }

        }
    }

    // Getter methods to access the config values
    public List<WaveInfo> getWaveInfoList() {
        return waveInfoList;
    }

    public String getLayout() {
        return layout;
    }

    public float getInitialTowerRange() {
        return initialTowerRange;
    }

    // ... add other getter methods here
//    public List<Monsters> getWaveInfoList() {
//        return waveInfoList;
//    }
}

