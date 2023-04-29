package nl.hsl.heist.models;

import nl.hsl.heist.shared.RemotePlayer;

import java.io.Serializable;
/**
 * Players that are linked to the server.
 *
 * @author Wesley
 */
public class RegisteredPlayer implements Serializable {

    static final long serialVersionUID = 3489597384523L;

	private RemotePlayer remotePlayer;
	private String name;
	private int id;
	private boolean started;
	
	public RegisteredPlayer(RemotePlayer remotePlayer, int id, String name) {
		this.remotePlayer = remotePlayer;
		this.id = id;
		this.name = name;
	}
	
	public RemotePlayer getRemotePlayer() {
		return remotePlayer;
	}
	public void setPlayer(RemotePlayer player) {
		this.remotePlayer = player;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public void gameHasStarted() {
		this.started = true;
	}

}
