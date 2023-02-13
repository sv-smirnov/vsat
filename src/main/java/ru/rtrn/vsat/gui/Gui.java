package ru.rtrn.vsat.gui;

import org.springframework.beans.factory.annotation.Autowired;
import ru.rtrn.vsat.services.SnmpSevice;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class Gui {

    @Autowired
    private SnmpSevice snmpSevice;
    public TrayIcon trayIcon;

    public Gui() {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/image/iconRed.png"));
            trayIcon = new TrayIcon(image, "VSAT monitoring");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("VSAT monitoring");
        
            tray.add(trayIcon);

            setMenu();
        } catch (AWTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Формирование менюшки для иконки в трее
    public void setMenu() {
        PopupMenu popupMenu = new PopupMenu();
        MenuItem openInBrowser = new MenuItem("in browser");
        MenuItem closeApp = new MenuItem("Close");
        MenuItem aboutItem = new MenuItem("About");

        openInBrowser.addActionListener(e -> {
//            showNotification("Двери закрываются", "Осторожно, не прищемите конечности");
            try {
                openInBrowser("http://localhost:8088");
            } catch (IOException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
        });

        closeApp.addActionListener(e -> {
            try {
                snmpSevice.snmpClose();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            System.exit(0);
        });

        aboutItem.addActionListener(e -> {
            showNotification("VSAT washer", "A washer for VSAT antennas based on the development of the domestic automotive industry in the style of \"I'm an engineer at mom's\"");
        });

        popupMenu.add(openInBrowser);
        popupMenu.add(closeApp);
        popupMenu.add(aboutItem);



        trayIcon.setPopupMenu(popupMenu);
    }

    // метод для открытия браузера
    public void openInBrowser(String url) throws IOException, URISyntaxException {
        Desktop desktop = Desktop.getDesktop();
        desktop.browse(new URL(url).toURI());
    }

    // Метод уведомлений
    public void showNotification(String title, String text) {
        trayIcon.displayMessage(title, text, TrayIcon.MessageType.INFO);
    }
}
