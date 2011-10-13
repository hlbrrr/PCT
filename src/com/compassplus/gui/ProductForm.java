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
        mainPanel = new ProductJPanel(new BorderLayout(), this);
    }

    public Product getProduct() {
        return product;
    }

    public JPanel getRoot() {
        return mainPanel;
    }
}
