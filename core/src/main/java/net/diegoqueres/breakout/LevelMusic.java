package net.diegoqueres.breakout;

public enum LevelMusic {
    YEAH(1, "Adam Willoughby - YEAH!.ogg", 0.11f),
    RETRO_PUZZLE(2, "Sirkoto51 - Retro Puzzle Music.ogg", 0.10f),
    ARPEGGIO_85_BPM(3, "DaveJf - Arpeggio 85 bpm.ogg", 0.11f),
    ADVENTURE(4, "Bensound - Adventure.ogg", 0.09f);

    int levelMultiple;
    String fileName;
    float volume;

    LevelMusic(int levelMultiple, String fileName, float volume) {
        this.levelMultiple = levelMultiple;
        this.fileName = fileName;
        this.volume = volume;
    }

    public int getLevelMultiple() {
        return levelMultiple;
    }

    public String getFileName() {
        return fileName;
    }

    public float getVolume() {
        return volume;
    }

    public static LevelMusic getByLevelMultiple(int levelMultiple) {
        for (int i = 0; i < values().length; i++) {
            LevelMusic lm = values()[i];
            if (lm.getLevelMultiple() == levelMultiple)
                return lm;
        }
        return null;
    }
}
