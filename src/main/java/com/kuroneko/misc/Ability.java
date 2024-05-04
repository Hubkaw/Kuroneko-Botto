package com.kuroneko.misc;

import java.util.List;

public enum Ability {
    WW("Walka wręcz"),
    US("Umiejętność Strzelecka"),
    K("Krzepa"),
    ODP("Odporność"),
    ZR("Zręczność"),
    INT("Inteligencja"),
    SW("Siła Woli"),
    OGD("Ogłada");

    public final String fullName;

    Ability(String name) {
        this.fullName = name;
    }

    public static List<Ability> getAbilities() {
        return List.of(WW, US, K, ODP, ZR, INT, SW, OGD);
    }
}
