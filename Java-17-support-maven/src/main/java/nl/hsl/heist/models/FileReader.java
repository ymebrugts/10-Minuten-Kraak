package nl.hsl.heist.models;

import java.io.*;
/**
 * Custom fileReader (json)
 *
 * @author Jordi
 */
public class FileReader implements Serializable {

	private static final long serialVersionUID = -5720469815191643760L;

	public static String readFile(InputStream pathName) throws IOException {

        System.out.println("got : " + pathName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(pathName));
        StringBuilder out = new StringBuilder();
        String data = "";
        while ((data = reader.readLine()) != null)
            out.append(data);
        reader.close();
        return out.toString();

	}
}
