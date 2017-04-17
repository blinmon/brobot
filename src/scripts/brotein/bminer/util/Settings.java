package scripts.brotein.bminer.util;

import org.powerbot.script.AbstractScript;
import org.powerbot.script.rt4.ClientContext;

/**
 * Created by noemailgmail on 4/17/2017.
 */
public class Settings extends AbstractScript<ClientContext> {
    public Settings(){

    }

    public String getDirectory(){
        return getStorageDirectory().toString();
    }

    public static void main(){
        Settings settings = new Settings();
        System.out.println(settings.getDirectory());
    }
}
