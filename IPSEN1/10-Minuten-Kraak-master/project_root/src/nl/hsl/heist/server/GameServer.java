package nl.hsl.heist.server;

import nl.hsl.heist.controllers.GameController;
import nl.hsl.heist.shared.GameAction;
import nl.hsl.heist.shared.GameConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Run the server.
 *
 * @author Wesley
 */
public class GameServer {


	public static void main(String[] args) throws UnknownHostException {
		System.out.println(InetAddress.getLocalHost().getHostAddress());
		System.setProperty("java.rmi.server.hostname", InetAddress.getLocalHost().getHostAddress());
		new GameServer().runServer();
	}

	public GameServer() {
		/*
		writePolicy();
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		*/
	}

	/**
	 * Run the server functions
	 */
	private void runServer() {
		try {
			// Create Game instance
			GameController gameController = new GameController();
			// Create server Game stub and cast to remote object
			GameAction gameStub = (GameAction) UnicastRemoteObject.exportObject(gameController, 0);
			System.out.println("[+] game stub created");
			Registry registry = LocateRegistry.createRegistry(GameConfig.getPort());
			System.out.println("[+] created server register");
			registry.rebind("Game", gameStub);
			System.out.println("[+] game bound - server now running...");
            gameController.broadcastGameState();
		} catch (Exception e) {
			System.out.println("[!] EXCEPTION: " + e);
		}

	}

	public void writePolicy() {
		
		String fileName = "C:\\Projects\\Houst\\gameserver.policy";
		
		String content = "grant codeBase \"file:///Projects//Houst//ws//Houst//bin/-\" {\n" +
		"    permission java.security.AllPermission;\n" +
		"};";

		BufferedWriter bw = null;
		FileWriter fw = null;
		URL url = null;
		
		try {
			fw = new FileWriter(fileName);
			bw = new BufferedWriter(fw);
			bw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) bw.close();
				if (fw != null) fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		try {
			url = new File(fileName).toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		System.err.println(url.toExternalForm());
		
		System.setProperty("java.security.policy", url.toExternalForm());
	}

}
