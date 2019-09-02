package org.leanpoker.player;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class Player {

    static final String VERSION = "1.0.1";

    public static int betRequest(JsonElement request) {
        JsonObject gameState = request.getAsJsonObject();
        return call(gameState);
    }

    public static void showdown(JsonElement game) {
    }

    public static int call(JsonObject gameState) {
        int currentBuyIn = gameState.get("currentÍ_buy_in").getAsInt();
        int bet = gameState.get("players").getAsJsonArray().get(gameState.get("in_action").getAsInt()).getAsJsonObject().get("bet").getAsInt();
        return  currentBuyIn - bet;
    }

    public static int check() {
        return 0;
    }
}
