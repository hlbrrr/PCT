package com.compassplus.gui;

import com.compassplus.proposalModel.Proposal;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 06.10.11
 * Time: 23:08
 */
public class ProposalForm {
    private JPanel mainPanel;
    private Proposal proposal;
    private JTabbedPane productsTabs;
    private ProductForm currentProductForm;

    public ProposalForm(Proposal proposal) {
        this.proposal = proposal;
        productsTabs = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.WRAP_TAB_LAYOUT);
        productsTabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ProductJPanel p = (ProductJPanel) productsTabs.getSelectedComponent();
                if (p != null) {
                    setCurrentProductForm(p.getParentForm());
                } else {
                    setCurrentProductForm(null);
                }
            }
        });
        for (String key : proposal.getConfig().getProducts().keySet()) {
            if (proposal.getProducts().containsKey(key)) {
                addProductForm(new ProductForm(proposal.getProducts().get(key)));
            }
        }
        mainPanel = new ProposalJPanel(new BorderLayout(), this);
        mainPanel.add(productsTabs, BorderLayout.CENTER);
    }

    private void setCurrentProductForm(ProductForm currentProductForm) {
//        if (currentProductForm != null) {
//            proposalMenu.setEnabled(true);
//        } else {
//            proposalMenu.setEnabled(false);
//        }
        this.currentProductForm = currentProductForm;
    }

    public ProductForm getCurrentProductForm() {
        return currentProductForm;
    }

    public JPanel getRoot() {
        return mainPanel;
    }

    public Proposal getProposal() {
        return proposal;
    }

    public void addProductForm(ProductForm productForm) {
        this.getProposal().addProduct(productForm.getProduct());
        productsTabs.addTab(productForm.getProduct().getName(), productForm.getRoot());
        productsTabs.setSelectedComponent(productForm.getRoot());
    }

    public void delProductForm(ProductForm productForm) {
        this.getProposal().delProduct(productForm.getProduct());
        productsTabs.remove(productForm.getRoot());
    }
}
