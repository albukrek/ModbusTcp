package ModbusTcp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class Props {

    private Properties prop;
    private FileInputStream input;
    private FileOutputStream output;
    private String path;

    public Props(String path) {
        this.path = path;
        init();

    }

    private void init() {
        try {
            input = new FileInputStream(path);
            prop = new Properties();
            prop.load(input);
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStr(String key) {
        return prop.getProperty(key);
    }
    public int getInt(String key) {
        return Integer.parseInt(prop.getProperty(key));
    }

    public void setStrElement(String key, String element) {
        try {
            output = new FileOutputStream(path);
            prop.setProperty(key, element);
            prop.store(output, null);
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}