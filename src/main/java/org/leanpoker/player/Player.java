package org.leanpoker.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

public class Player {

    static final String VERSION = "2.2.2";

    public static int betRequest(JsonElement request) {
        JsonObject gameState = request.getAsJsonObject();
        JsonArray communityCards = gameState.get("community_cards").getAsJsonArray();
        JsonArray players = gameState.get("players").getAsJsonArray();
        JsonObject player = players.get(gameState.get("in_action").getAsInt()).getAsJsonObject();
        int currentBuyIn = gameState.get("current_buy_in").getAsInt();
        int stack = player.get("stack").getAsInt();

        if(getActivePlayersNum(gameState) <= 3 || hasAce(gameState)) {
            if (communityCards.size() > 0) {
                if (checkCards(gameState)) return call(gameState);
                if (hasAtLeastFourSuit(gameState)) return call(gameState);
                else return check();
            } else {
                if (hasHandPair(gameState) && hasHighPairInHand(gameState)) return allIn(gameState);
                if (hasAce(gameState) && !(currentBuyIn > (stack * 0.2))) return call(gameState);
                if (currentBuyIn > 400 && !hasHandPair(gameState)) return check();
                return check();
            }
        }
        return check();
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

    public static int getActivePlayersNum(JsonObject gameState) {
        JsonArray players = gameState.get("players").getAsJsonArray();
        int counter = 0;
        for (int i = 0; i < players.size(); i++) {
            if(players.get(i).getAsJsonObject().get("status").getAsString().equals("active")) counter++;
        }
        return counter;
    }

    public static boolean isCallLegJavaBet(JsonObject gameState) {
        JsonArray players = gameState.get("players").getAsJsonArray();
        boolean isHigh = false;
        for (int i = 0; i < players.size(); i++) {
            if(players.get(i).getAsJsonObject().get("name").getAsString().equals("LegJava")
                    && players.get(i).getAsJsonObject().get("bet").getAsInt() > 700) {
                isHigh = true;
            }
        }
        if(isHigh) {
            for (int i = 0; i < players.size(); i++) {
                if (!(players.get(i).getAsJsonObject().get("name").getAsString().equals("LegJava"))
                        && players.get(i).getAsJsonObject().get("bet").getAsInt() > 700) {
                        return false;
                }
            }
        return true;
        }
        return false;
    }
}
