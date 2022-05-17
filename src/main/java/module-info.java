module com.maehem.gamepad {
    requires java.logging;
    requires javafx.controls;
    requires com.almasb.fxgl.all;

    // Pi4J MODULES
    requires com.pi4j;
    requires com.pi4j.plugin.pigpio;
    requires com.pi4j.library.pigpio;

    // SLF4J MODULES
    requires org.slf4j;
    requires org.slf4j.simple;

    uses com.pi4j.extension.Extension;
    uses com.pi4j.provider.Provider;

    opens assets.textures;
    
    // allow access to classes in the following namespaces for Pi4J annotation processing
    exports com.maehem.gamepad to com.almasb.fxgl.core;
    //exports com.maehem.gamepad;
}
