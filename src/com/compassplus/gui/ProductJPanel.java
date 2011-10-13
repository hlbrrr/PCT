package com.compassplus.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 14.10.11
 * Time: 1:15
 */
public class ProductJPanel extends JPanel {
    private ProductForm parentForm;

    public ProductJPanel(ProductForm parentForm) {
        super();
        this.parentForm = parentForm;
    }

    public ProductJPanel(LayoutManager layout, ProductForm parentForm) {
        super(layout);
        this.parentForm = parentForm;
    }

    public ProductForm getParentForm() {
        return parentForm;
    }
}
