/**
 * Created by radhikadesai on 11/04/2017.
 */
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author shantonu
 *
 */
public class Logger {
    static FileOutputStream fileOutputStream;
    static OutputStreamWriter outputStreamWriter;

    public static void initial(String str) throws IOException {
        fileOutputStream = new FileOutputStream(str);
        outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
    }

    public static void end() {
        try {
            outputStreamWriter.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void log(String str) {
        try {
            outputStreamWriter.write(str + '\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}