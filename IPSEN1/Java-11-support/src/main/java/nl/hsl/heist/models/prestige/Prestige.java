package nl.hsl.heist.models.prestige;

import java.io.Serializable;
/**
 * Parent prestige class for other prestiges
 * @author Jordi
 */
public abstract class Prestige implements Serializable {

    static final long serialVersionUID = 28934935L;

    private int points;

    public Prestige(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }
}
