package nl.hsl.heist.shared;

/**
 *  Gameconfig, the name says it all
 *  @author wesley
 */
public class GameConfig {

	private static final GameConfig config = new GameConfig();

    private int port = 1099;
    private static String hostName = "localhost";
    private String GameName = "Game";
    public static final int ROWS = 7; // includes LIGHTROWS
    public static final int LIGHTROWS = 2; // are the first 2 rows.
    public static final int COLS = 5;

    public static int getPort() {
        return config.port;
    }

    public static String getHostName() {
        return config.hostName;
    }

    public static String getGameName() {
        return config.GameName;
    }

    public static void setHostName(String name) {
        hostName = name;
    }
}
