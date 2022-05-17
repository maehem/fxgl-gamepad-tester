/**
 * Test functionality of a USB gamepad for RaspBerryPi(or other Linux)
 *
 * By Mark J Koch - 2021/10
 *
 */
package com.maehem.gamepad;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.dsl.FXGL;
import static com.almasb.fxgl.dsl.FXGL.getGameScene;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiMode;
import com.pi4j.io.spi.SpiProvider;
import com.pi4j.library.pigpio.PiGpio;
import com.pi4j.platform.Platforms;
import com.pi4j.plugin.pigpio.provider.spi.PiGpioSpiProvider;
import com.pi4j.util.Console;
import java.nio.charset.StandardCharsets;
import javafx.scene.Cursor;

/**
 * App
 */
public class App extends GameApplication {

    private boolean titleSet = false;
    //private JoyStick js = new JoyStick();
    Context pi4j;
    Console console = new Console();
    Xra1405Component pad1;
    
    @Override
    protected void initSettings(GameSettings gs) {
        gs.setWidth(1152);
        gs.setHeight(864);

        gs.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {
                return new GamePadMenu();
            }
        });
        //gs.getSceneFactory().
        gs.setMainMenuEnabled(true);
        
        this.pi4j = Pi4J.newAutoContext();
//        var piGpio = PiGpio.newNativeInstance();
//        this.pi4j = Pi4J.newContextBuilder()
//            .noAutoDetect()
//            .add(
//                PiGpioSpiProvider.newInstance(piGpio)
//            )
//            .build();

        Platforms platforms = pi4j.platforms();

        console.box("Pi4J PLATFORMS");
        console.println();
        platforms.describe().print(System.out);
        console.println();
        
         // get a SPI I/O provider from the Pi4J context
        //SpiProvider spiProvider = pi4j.provider("pigpio-spi");

        this.pad1 = new Xra1405Component(pi4j, 0, Xra1405Component.DEFAULT_SPI_BAUD_RATE);
    }

    

    @Override
    protected void initGame() {
        // disable big mouse cursor
        getGameScene().getRoot().setCursor(Cursor.DEFAULT);
        // Set your custom cursor: (searched for in assets/ui/cursors/ )
        // getGameScene().setCursor(String imageName, Point2D hotspot);
        
        
    }

    @Override
    protected void onUpdate(double tpf) {
        // JavaFX and FXGL version information not available until after game inits.
        if (!titleSet) {
            var javaVersion = System.getProperty("java.version");
            var javafxVersion = System.getProperty("javafx.version");
            var fxglVersion = FXGL.getVersion();

            String label = "Basic FXGL App :: Java: " + javaVersion + " with JavaFX: " + javafxVersion + " and FXGL: " + fxglVersion;
            FXGL.getPrimaryStage().setTitle(label);
            titleSet = true;
        }

        console.print("Pad State: " + bytesToHex(new byte[]{pad1.getGpioStateL()}));
        
        
//        System.out.println("Update start.");
//        byte bytes[] = new byte[1024];
//        try {
//            int nBytes = js.read(bytes);
//            if ( nBytes > 0 ) {
//                System.out.println("Joy: " + bytesToHex(bytes));
//            } else {
//                System.out.println(".");
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        System.out.println("Update end.");
    }

    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}
