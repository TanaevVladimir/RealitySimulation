package com;

import javax.swing.*;
import java.awt.*;

public class InventoryGUI extends JFrame {
    public InventoryGUI() {
        setTitle("Инвентарь");
        setSize(300, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea inventoryArea = new JTextArea();
        inventoryArea.setEditable(false);
        inventoryArea.setText(AlisaAI.getInventory());
        JScrollPane scrollPane = new JScrollPane(inventoryArea);

        add(scrollPane, BorderLayout.CENTER);
    }
}