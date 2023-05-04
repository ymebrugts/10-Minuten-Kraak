package nl.hsl.heist.models;

import java.io.Serializable;
/**
 * Points that may belong to a player.
 *
 * @author Joorden, Jordi
 */
public class Points implements Serializable {
    private static final long serialVersionUID = -6762055623952328342L;

    private int potion, fossil, artifact, tome, jewel, three, four, five, curse;

    public Points() {
        this.potion = 0;
        this.fossil = 0;
        this.artifact = 0;
        this.tome = 0;
        this.jewel = 0;
        this.three = 0;
        this.four = 0;
        this.five = 0;
        this.curse = 0;
    }

    public int getSymbol(Symbol sym) {
        int var = 0;
        switch (sym) {
            case POTION:
                var = potion;
                break;
            case FOSSIL:
                var = fossil;
                break;
            case ARTIFACT:
                var = artifact;
                break;
            case TOME:
                var = tome;
                break;
            case JEWEL:
                var = jewel;
                break;
            case THREE:
                var = three;
                break;
            case FOUR:
                var = four;
                break;
            case FIVE:
                var = five;
                break;
            case CURSE:
                var = curse;
                break;
        }
        return var;
    }

    public void zeroCurse() {
        this.curse = 0;
    }
    public void addToPotion(int n) {
        potion += n;
    }
    public void addToFossil(int n) {
        fossil += n;
    }
    public void addToArtifact(int n) {
        artifact += n;
    }
    public void addToTome(int n) {
        tome += n;
    }
    public void addToJewel(int n) {
        jewel += n;
    }
    public void addToThree(int n) {
        three += n;
    }
    public void addToFour(int n) {
        four += n;
    }
    public void addToFive(int n) {
        five += n;
    }
    public void addToCurse(int n) {
        curse += n;
    }
    public void addToAll(int n) {
        potion += n;
        fossil += n;
        artifact += n;
        tome += n;
        jewel += n;
    }
}

