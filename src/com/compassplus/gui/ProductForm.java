package com.compassplus.gui;

import com.compassplus.proposalModel.Product;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 14.10.11
 * Time: 1:08
 */
public class ProductForm {
    private Product product;
    private JPanel mainPanel;

    public ProductForm(Product product) {
        this.product = product;
        mainPanel = new ProductJPanel(this);
        mainPanel.setLayout(new GridBagLayout());
        initForm();
    }

    private void initForm() {
        JPanel modulesPanel = new JPanel();
        modulesPanel.setLayout(new BoxLayout(modulesPanel, BoxLayout.Y_AXIS));
        JPanel capacitiesPanel = new JPanel();
        capacitiesPanel.setLayout(new BoxLayout(capacitiesPanel, BoxLayout.Y_AXIS));

        modulesPanel.setBorder(BorderFactory.createTitledBorder("Modules"));

        capacitiesPanel.setBorder(BorderFactory.createTitledBorder("Capacities"));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        mainPanel.add(modulesPanel, c);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        c.weighty = 1.0;
        c.gridx = 1;
        c.gridy = 0;
        mainPanel.add(capacitiesPanel, c);

        modulesPanel.add(new JLabel("asd"));
        modulesPanel.add(new JLabel("asd"));
        modulesPanel.add(new JLabel("asd"));
        modulesPanel.add(new JLabel("afghsd"));
        modulesPanel.add(new JLabel("asd"));
        modulesPanel.add(new JLabel("aasd"));
        modulesPanel.add(new JLabel("aasd"));
        modulesPanel.add(new JLabel("asdaasd"));
        modulesPanel.add(new JLabel("aasd"));
        modulesPanel.add(new JLabel("aasd"));
        modulesPanel.add(new JLabel("asd"));
        modulesPanel.add(new JLabel("aasd"));
        modulesPanel.add(new JLabel("aahgsd"));
        modulesPanel.add(new JLabel("aasd"));
        modulesPanel.add(new JLabel("aasd"));
        modulesPanel.add(new JLabel("asgvdfd"));
        modulesPanel.add(new JLabel("athsd"));
    }

    public Product getProduct() {
        return product;
    }

    public JPanel getRoot() {
        return mainPanel;
    }
}
