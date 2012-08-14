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
    private JFrame frame;
    private JPanel mainPanel;
    private Proposal proposal;
    private JTabbedPane productsTabs;
    private ProductForm currentProductForm;
    private DecimalFormat df = new DecimalFormat();
    private SummaryForm summaryForm;
    private AuthLevelsForm authForm;
    private boolean changed = false;

    public ProposalForm(Proposal proposal, JFrame frame, PCTChangedListener titleUpdater) {
        this.frame = frame;
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

        addSummaryPage(proposal, titleUpdater);
        addAuthPage(proposal);
        for (String key : proposal.getConfig().getProducts().keySet()) {
            if (proposal.getProducts().containsKey(key)) {
                addProductForm(proposal.getProducts().get(key));
            }
        }
        mainPanel = new ProposalJPanel(new BorderLayout(), this);
        titleUpdater.setData(mainPanel);
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        mainPanel.add(productsTabs, BorderLayout.CENTER);

        productsTabs.setSelectedIndex(0);
    }

    public void updateMainTitle() {
        summaryForm.updateMainTitle();
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


    private void addAuthPage(Proposal proposal) {
        authForm = new AuthLevelsForm(proposal);
        productsTabs.addTab("Authority Levels", authForm.getRoot());
    }

    private void addSummaryPage(Proposal proposal, PCTChangedListener titleUpdater) {
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

            public void setData(Object data) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public Object getData() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        }, df, new PCTChangedListener() {
            public void act(Object src) {
                if (src instanceof SummaryForm) {
                    changed = true;
                }
            }

            public void setData(Object data) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public Object getData() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        }, titleUpdater
        );
        productsTabs.addTab("Summary", summaryForm.getRoot());
        productsTabs.setSelectedComponent(summaryForm.getRoot());
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public boolean isChanged() {
        return this.changed;
    }

    public void addProductForm(Product product) {
        ProductForm productForm = new ProductForm(product, new PCTChangedListener() {
            public void act(Object src) {
                if (src instanceof ProductForm) {
                    ProductForm pf = (ProductForm) src;
                    summaryForm.update();
                    Integer ti = productsTabs.indexOfComponent(pf.getRoot());
                    if (ti >= 0) {
                        productsTabs.setTitleAt(ti, pf.getProduct().getName() + (!getProposal().getConfig().isSalesSupport()?(" (" + (getProposal().getCurrency().getSymbol() != null ?
                                getProposal().getCurrency().getSymbol() + " " : "") + "" + df.format(pf.getProduct().getRegionPrice()) + (getProposal().getCurrency().getSymbol() == null ?
                                " " + getProposal().getCurrency().getName() : "") + ")"):""));

                    }
                }
            }

            public void setData(Object data) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public Object getData() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        }, df, getFrame());
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

    private JFrame getFrame() {
        return frame;
    }
}
