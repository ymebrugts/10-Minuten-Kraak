package nl.hsl.heist.models;

import java.io.Serializable;
import java.util.List;
/**
 * Card that may belong to a player or the a tile.
 * @author wesley, Jordi
 */
public class Card implements Serializable {

	private static final long serialVersionUID = -2788192491761296604L;

	public enum Moon {
		FULL, HALF, NONE
	}
	
	private String name;
	private String soundPath;
	private List<Integer> points;
	private List<Symbol> symbols;
	private int curses;
	private Moon moon;
	private Command command;
	private String imagePath;

	public Card(String name, String soundPath, int curses, Moon moon, List<Integer> points, List<Symbol> symbols, String imagePath, Command command) {
		this.name = name;
		this.soundPath = soundPath;
		this.curses = curses;
		this.moon = moon;
		this.points = points;
		this.symbols = symbols;
		this.imagePath = imagePath;
		this.command = command;
	}

	public String getName() {
		return name;
	}

	public String getSoundPath() {
		return soundPath;
	}

	public List<Integer> getPoints() {
		return points;
	}

	public List<Symbol> getSymbols() {
		return symbols;
	}

	public int getCurses() {
		return curses;
	}

	public Moon getMoon() {
		return moon;
	}

	public Command getCommand() {
		return command;
	}

	public String getImagePath() {
		return imagePath;
	}

}


