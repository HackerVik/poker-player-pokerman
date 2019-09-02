package org.leanpoker.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class Player {

    static final String VERSION = "1.0.5";

    public static int betRequest(JsonElement request) {
        JsonObject gameState = request.getAsJsonObject();
        JsonArray communityCards = gameState.get("community_cards").getAsJsonArray();
        int currentBuyIn = gameState.get("current_buy_in").getAsInt();
        if(currentBuyIn > 400) return check();
        if(communityCards.size() > 0) {
            if (checkCards(gameState)) return call(gameState);
            else return check();
        }
        return call(gameState);
    }

    public static void showdown(JsonElement game) {
    }

    public static int call(JsonObject gameState) {

        int currentBuyIn = gameState.get("current_buy_in").getAsInt();
        int bet = gameState.get("players").getAsJsonArray().get(gameState.get("in_action").getAsInt()).getAsJsonObject().get("bet").getAsInt();
        return  currentBuyIn - bet;
    }

    public static int check() {
        return 0;
    }

    public static boolean checkCards(JsonObject gameState) {
        JsonObject player = gameState.get("players").getAsJsonArray().get(gameState.get("in_action").getAsInt()).getAsJsonObject();
        JsonArray holeCards = player.get("hole_cards").getAsJsonArray();
        JsonArray communityCards = gameState.get("community_cards").getAsJsonArray();
        return hasPair(holeCards, communityCards);
    }

    public static boolean hasPair(JsonArray holeCards, JsonArray communityCards) {
        if(holeCards.get(0).getAsJsonObject().get("rank").equals(holeCards.get(1).getAsJsonObject().get("rank"))) return true;
        for (int i = 0; i < holeCards.size(); i++) {
            JsonObject card = holeCards.get(i).getAsJsonObject();
            for (int j = 0; j < communityCards.size(); j++) {
                JsonObject communityCard = communityCards.get(j).getAsJsonObject();
                if(card.get("rank").equals(communityCard.get("rank"))) return true;
            }
        }
        return false;
    }
}
