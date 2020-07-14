package serlizables;

import java.io.Serializable;

public class GameStatistics implements Serializable {
    private int totalOnlineWins;
    private int totalOnlineLosses;
    private int singlePlayerWins;
    private int singlePlayerLosses;

    public GameStatistics(int totalOnlineWins, int totalOnlineLosses, int singlePlayerWins, int singlePlayerLosses) {
        this.totalOnlineWins = totalOnlineWins;
        this.totalOnlineLosses = totalOnlineLosses;
        this.singlePlayerWins = singlePlayerWins;
        this.singlePlayerLosses = singlePlayerLosses;
    }

    public int getTotalOnlineWins() {
        return totalOnlineWins;
    }

    public int getTotalOnlineLosses() {
        return totalOnlineLosses;
    }

    public int getSinglePlayerWins() {
        return singlePlayerWins;
    }

    public int getSinglePlayerLosses() {
        return singlePlayerLosses;
    }

    public void addTotalOnlineWins() {
        totalOnlineWins++;
    }

    public void addTotalOnlineLosses() {
        totalOnlineLosses++;
    }

    public void addSinglePlayerWins() {
        singlePlayerWins++;
    }

    public void addSinglePlayerLosses() {
        singlePlayerLosses++;
    }
}
