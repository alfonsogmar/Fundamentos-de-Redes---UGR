/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frquizzserver;

/**
 *
 * @author alfonso
 */
public class Player {
    private String userName;
    private int score;
    //private static final int MAXSCORE = 3;
    
    public Player() {
    }
    
    public void logInPlayer(String name) {
        userName = name;
        score = 0;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void addScore(int addedScore) {
        score += addedScore;
    }

    public int getScore() {
        return score;
    }
    
}
