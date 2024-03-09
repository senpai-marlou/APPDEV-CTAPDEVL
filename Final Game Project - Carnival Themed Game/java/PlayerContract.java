package com.loompa.tapandshoot;

import android.provider.BaseColumns;

public final class PlayerContract {

    private PlayerContract() {} // Private constructor to prevent instantiation

    public static class PlayerEntry implements BaseColumns {
        public static final String TABLE_NAME = "players";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_HIGH_SCORE = "high_score";
    }
}

