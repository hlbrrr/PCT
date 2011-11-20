package com.compassplus.gui;

import com.compassplus.proposalModel.Product;
import com.compassplus.proposalModel.Proposal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.text.DecimalFormat;

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
    private DecimalFormat df = new DecimalFormat();
    private SummaryForm summaryForm;

    public ProposalForm(Proposal proposal) {
        this.proposal = proposal;
        productsTabs = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.WRAP_TAB_LAYOUT);
        productsTabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (productsTabs.getSelectedComponent() instanceof ProductJPanel) {
                    ProductJPanel p = (ProductJPanel) productsTabs.getSelectedComponent();
                    setCurrentProductForm(p.getParentForm());
                } else {
                    setCurrentProductForm(null);
                }
            }
        });

        addSummaryPage(proposal);
        for (String key : proposal.getConfig().getProducts().keySet()) {
            if (proposal.getProducts().containsKey(key)) {
                addProductForm(proposal.getProducts().get(key));
            }
        }
        mainPanel = new ProposalJPanel(new BorderLayout(), this);
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        mainPanel.add(productsTabs, BorderLayout.CENTER);

        productsTabs.setSelectedIndex(0);
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

    private void addSummaryPage(Proposal proposal) {
        summaryForm = new SummaryForm(proposal, new PCTChangedListener() {
            public void act(Object src) {
                if (src instanceof SummaryForm) {
                    for (Component c : productsTabs.getComponents()) {
                        if (c instanceof ProductJPanel) {
                            ((ProductJPanel) c).getParentForm().update();
                        }
                    }
                }
            }
        }, df);
        productsTabs.addTab("Summary", summaryForm.getRoot());
        productsTabs.setSelectedComponent(summaryForm.getRoot());
    }

    public void addProductForm(Product product) {
        ProductForm productForm = new ProductForm(product, new PCTChangedListener() {
            public void act(Object src) {
                if (src instanceof ProductForm) {
                    ProductForm pf = (ProductForm) src;
                    summaryForm.update();
                    Integer ti = productsTabs.indexOfComponent(pf.getRoot());
                    if (ti >= 0) {
                        productsTabs.setTitleAt(ti, pf.getProduct().getName() + " (" + (getProposal().getCurrency().getSymbol() != null ?
                                getProposal().getCurrency().getSymbol() + " " : "") + "" + df.format(pf.getProduct().getPrice()) + (getProposal().getCurrency().getSymbol() == null ?
                                " " + getProposal().getCurrency().getName() : "") + ")");

                    }
                }
            }
        }, df);
        this.getProposal().addProduct(productForm.getProduct());
        productsTabs.addTab(productForm.getProduct().getName(), productForm.getRoot());
        productsTabs.setSelectedComponent(productForm.getRoot());
        productForm.reloadProductPrice();
    }

    public void delProductForm(ProductForm productForm) {
        this.getProposal().delProduct(productForm.getProduct());
        productsTabs.remove(productForm.getRoot());
        summaryForm.update();
    }

    public JTabbedPane getProductsTabs() {
        return productsTabs;
    }
}
