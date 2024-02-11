package eecs.project.gui;

import javax.swing.*;
import java.awt.*;

public class MainUI extends JFrame {

    static ProfileSelect profileSelect = null;

    public MainUI() {
        super("Splash Screen");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(2, 1));

        JButton chooseProfileButton = new JButton("Choose Existing Profile");
        JButton createProfileButton = new JButton("Create New Profile");

        panel.add(chooseProfileButton);
        panel.add(createProfileButton);

        getContentPane().add(panel);

        chooseProfileButton.addActionListener(e -> {
            profileSelect= new ProfileSelect();
            profileSelect.setVisible(true);
            dispose();
        });

        createProfileButton.addActionListener(e -> {
            ProfileCreate profileCreate = new ProfileCreate(true);
            profileCreate.setVisible(true);
        });
    }

    public static void profileSelected() {
        profileSelect.setVisible(false);
        profileSelect.removeAll();
        new Navigation().setVisible(true);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainUI mainUI = new MainUI();
            mainUI.setVisible(true);
        });
    }
}
