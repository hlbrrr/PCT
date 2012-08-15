package com.compassplus.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TextNote extends JTextArea {
    public TextNote(String text) {
        super(text);
        setBackground(null);
        setEditable(false);
        setBorder(new EmptyBorder(0, 30, 10, 10));
        setLineWrap(true);
        setWrapStyleWord(true);
        setFocusable(false);
        JLabel tt = new JLabel();
        //Font f = new Font(tt.getFont().getName(), Font.ITALIC,tt.getFont().getSize());
        setFont(tt.getFont());
    }
}