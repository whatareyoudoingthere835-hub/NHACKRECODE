package aethereal.core.models;
import aethereal.*;
import aethereal.features.modules.Module;
import aethereal.features.modules.*;
import aethereal.features.modules.combat.*;
import aethereal.features.modules.movement.*;
import aethereal.features.modules.player.*;
import aethereal.features.modules.render.*;
import aethereal.features.modules.misc.*;
import aethereal.features.modules.earnings.*;
import aethereal.features.modules.autobuy.*;
import aethereal.features.commands.*;
import aethereal.gui.*;
import aethereal.graphics.*;
import aethereal.system.config.*;
import aethereal.system.events.*;
import aethereal.system.network.*;
import aethereal.system.resources.*;
import aethereal.core.models.*;
import aethereal.core.types.*;
import aethereal.core.accessors.*;
import aethereal.core.annotations.*;
import aethereal.utils.*;
import aethereal.utils.math.*;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;

public class TrayNotifier {
    public static void send(String str, String str2) {
        if (SystemTray.isSupported()) {
            try {
                SystemTray systemTray= SystemTray.getSystemTray();
                TrayIcon trayIcon= new TrayIcon(new BufferedImage(1, 1, 2));
                systemTray.add(trayIcon);
                trayIcon.displayMessage(str, str2, TrayIcon.MessageType.NONE);
                systemTray.remove(trayIcon);
            } catch (Exception e) {
            }
        }
    }
}
