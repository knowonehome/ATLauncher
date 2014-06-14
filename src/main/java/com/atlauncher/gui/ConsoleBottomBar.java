/**
 * Copyright 2013-2014 by ATLauncher and Contributors
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */
package com.atlauncher.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.atlauncher.App;
import com.atlauncher.data.Constants;
import com.atlauncher.data.LogMessageType;
import com.atlauncher.gui.components.BottomBar;
import com.atlauncher.thread.PasteUpload;
import com.atlauncher.utils.Utils;

@SuppressWarnings("serial")
public class ConsoleBottomBar extends BottomBar {

    private JPanel leftSide;

    private JButton clear;
    private JButton copyLog;
    private JButton uploadLog;
    private JButton killMinecraft;

    public ConsoleBottomBar() {
        setBorder(BorderFactory.createEtchedBorder());
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 50)); // Make the bottom bar at least 50 pixels high

        createButtons(); // Create the buttons
        setupActionListeners(); // Setup Action Listeners

        leftSide = new JPanel();
        leftSide.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(0, 5, 0, 0);
        leftSide.add(clear, gbc);
        gbc.gridx++;
        leftSide.add(copyLog, gbc);
        gbc.gridx++;
        leftSide.add(uploadLog, gbc);
        gbc.gridx++;
        leftSide.add(killMinecraft, gbc);

        add(leftSide, BorderLayout.WEST);
        add(rightSide, BorderLayout.EAST);
    }

    /**
     * Sets up the action listeners on the buttons
     */
    private void setupActionListeners() {
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                App.settings.clearConsole();
                App.settings.log("Console Cleared");
            }
        });
        copyLog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                App.settings.log("Copied Log To Clipboard");
                StringSelection text = new StringSelection(App.settings.getLog());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(text, null);
            }
        });
        uploadLog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    String result = App.TASKPOOL.submit(new PasteUpload()).get();
                    if (result.contains(Constants.PASTE_CHECK_URL)) {
                        App.settings.log("Log uploaded and link copied to clipboard: " + result);
                        App.TOASTER.pop("Log uploaded and link copied to clipboard: " + result);
                        StringSelection text = new StringSelection(result);
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(text, null);
                    } else {
                        App.settings
                                .log("Log failed to upload: " + result, LogMessageType.error, false);
                        App.TOASTER.popError("Log failed to upload");
                    }
                } catch(Exception ex){
                    ex.printStackTrace(System.err);
                }
            }
        });
        killMinecraft.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                int ret = JOptionPane.showConfirmDialog(App.settings.getParent(), "<html><center>"
                                + App.settings.getLocalizedString("console.killsure", "<br/><br/>")
                                + "</center>" + "</html>", App.settings.getLocalizedString("console.kill"),
                        JOptionPane.YES_NO_OPTION);
                if (ret == JOptionPane.YES_OPTION) {
                    App.settings.killMinecraft();
                    killMinecraft.setVisible(false);
                }
            }
        });
    }

    /**
     * Creates the JButton's for use in the bar
     */
    private void createButtons() {
        clear = new JButton("Clear");
        copyLog = new JButton("Copy Log");
        uploadLog = new JButton("Upload Log");

        killMinecraft = new JButton("Kill Minecraft");
        killMinecraft.setVisible(false);
    }

    public void showKillMinecraft() {
        killMinecraft.setVisible(true);
    }

    public void hideKillMinecraft() {
        killMinecraft.setVisible(false);
    }

    public void setupLanguage() {
        clear.setText(App.settings.getLocalizedString("console.clear"));
        copyLog.setText(App.settings.getLocalizedString("console.copy"));
        uploadLog.setText(App.settings.getLocalizedString("console.upload"));
        killMinecraft.setText(App.settings.getLocalizedString("console.kill"));
    }
}
