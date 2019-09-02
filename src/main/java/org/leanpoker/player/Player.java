package org.leanpoker.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Player {

    static final String VERSION = "2.1.3";

    public static int betRequest(JsonElement request) {
        JsonObject gameState = request.getAsJsonObject();
        JsonArray communityCards = gameState.get("community_cards").getAsJsonArray();
        JsonObject player = gameState.get("players").getAsJsonArray().get(gameState.get("in_action").getAsInt()).getAsJsonObject();
        int currentBuyIn = gameState.get("current_buy_in").getAsInt();
        int stack = player.get("stack").getAsInt();

        if(communityCards.size() > 0) {
            if (checkCards(gameState)) return call(gameState);
            if (hasAtLeastFourSuit(gameState)) return call(gameState);
            else return check();
        } else {
            if(hasAce(gameState) && !(currentBuyIn > (stack * 0.2))) return call(gameState);
            if(currentBuyIn > 400 && !hasHandPair(gameState)) return check();
            else if (currentBuyIn < 400 && !hasHandPair(gameState)) return call(gameState);
            else if (hasHandPair(gameState) && hasHighPairInHand(gameState)) return allIn(gameState);
            return check();
        }
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
        for (int i = 0; i < holeCards.size(); i++) {
            JsonObject card = holeCards.get(i).getAsJsonObject();
            for (int j = 0; j < communityCards.size(); j++) {
                JsonObject communityCard = communityCards.get(j).getAsJsonObject();
                if(card.get("rank").equals(communityCard.get("rank"))) return true;
            }
        }
        return false;
    }

    public static boolean hasHandPair(JsonObject gameState) {
        JsonObject player = gameState.get("players").getAsJsonArray().get(gameState.get("in_action").getAsInt()).getAsJsonObject();
        JsonArray holeCards = player.get("hole_cards").getAsJsonArray();
        return holeCards.get(0).getAsJsonObject().get("rank").equals(holeCards.get(1).getAsJsonObject().get("rank"));
    }

    public static int allIn(JsonObject gameState) {
        return gameState.get("players").getAsJsonArray().get(gameState.get("in_action").getAsInt()).getAsJsonObject().get("stack").getAsInt();
    }

    public static boolean hasAce(JsonObject gameState) {
        JsonObject player = gameState.get("players").getAsJsonArray().get(gameState.get("in_action").getAsInt()).getAsJsonObject();
        JsonArray holeCards = player.get("hole_cards").getAsJsonArray();
        for(int i = 0; i < holeCards.size(); i++) {
            if(holeCards.get(i).getAsJsonObject().get("rank").getAsString().equals("A")) return true;
        }
        return false;
    }

    public static boolean hasAtLeastFourSuit(JsonObject gameState) {
        JsonObject player = gameState.get("players").getAsJsonArray().get(gameState.get("in_action").getAsInt()).getAsJsonObject();
        JsonArray holeCards = player.get("hole_cards").getAsJsonArray();
        JsonArray communityCards = gameState.get("community_cards").getAsJsonArray();
        int counter = 0;

        boolean sameSuit = holeCards.get(0).getAsJsonObject().get("suit").equals(holeCards.get(1).getAsJsonObject().get("suit"));
        if(sameSuit) {
            String suit = holeCards.get(0).getAsJsonObject().get("suit").getAsString();
            for(int i = 0; i < communityCards.size(); i++) {
                if(communityCards.get(i).getAsJsonObject().get("suit").getAsString().equals(suit)) counter++;
            }
        }
        return counter >= 2;
    }

    public static boolean hasHighPairInHand(JsonObject gameState) {
        JsonObject player = gameState.get("players").getAsJsonArray().get(gameState.get("in_action").getAsInt()).getAsJsonObject();
        JsonArray holeCards = player.get("hole_cards").getAsJsonArray();
        String[] highCards = new String[] {"A", "K", "Q", "J", "10"};
        return Arrays.asList(highCards).contains(holeCards.get(0).getAsJsonObject().get("rank").getAsString());
    }
}
