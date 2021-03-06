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
    private PSQuoteForm psForm;
    private AuthLevelsForm authForm;
    private boolean changed = false;
    private PCTChangedListener titleUpdater;

    public PSQuoteForm getPSForm(){
        return this.psForm;
    }

    public ProposalForm(Proposal proposal, JFrame frame, PCTChangedListener titleUpdater) {
        this.titleUpdater = titleUpdater;
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
        addPSForm();
        addAuthPage(proposal, titleUpdater/*new PCTChangedListener(){
            public void act(Object src) {
                int ind = productsTabs.indexOfComponent((Component) (summaryForm.getRoot()));
                String append="";
                if(!proposal.isAllAlsDefined()){
                    productsTabs.setForegroundAt(ind, Color.BLACK);
                }else if(proposal.isApproved()){
                    append = " [APPROVED]";
                    productsTabs.setForegroundAt(ind, new Color(0,158,5));
                }else{
                    append = " [REQUIRES APPROVAL]";
                    productsTabs.setForegroundAt(ind, Color.RED);
                }
                productsTabs.setTitleAt(ind, "Summary" + append);
            }

            public void setData(Object data) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public Object getData() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        }*/);
        for (String key : proposal.getConfig().getProducts().keySet()) {
            if (proposal.getProducts().containsKey(key)) {
                addProductForm(proposal.getProducts().get(key));
            }
        }
        mainPanel = new ProposalJPanel(new BorderLayout(), this);
        titleUpdater.setData("productTab", mainPanel);
        titleUpdater.setData("productsTabs", productsTabs);
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


    private void addAuthPage(Proposal proposal, PCTChangedListener alUpdater) {
        if (!proposal.getConfig().isSalesSupport() && proposal.getConfig().getAuthLevels().size() > 0) {
            authForm = new AuthLevelsForm(proposal, alUpdater);
            productsTabs.addTab("Authority levels", authForm.getRoot());
        }
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
                    if (psForm != null) {
                        psForm.update();
                    }
                }
            }

            public void setData(String key, Object data) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public Object getData(String key) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        }, df, new PCTChangedListener() {
            public void act(Object src) {
                if (src instanceof SummaryForm) {
                    changed = true;
                }
            }

            public void setData(String key, Object data) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public Object getData(String key) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        }, titleUpdater, new PCTChangedListener() {
            public void act(Object src) {
                if(psForm!=null){
                    psForm.recalc();
                    psForm.recalcTC();
                }
            }

            public void setData(String key, Object data) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public Object getData(String key) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        }
        );
        summaryForm.setFrame(getFrame());
        titleUpdater.setData("summaryForm", summaryForm);
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
                    titleUpdater.act(proposal);
                    ProductForm pf = (ProductForm) src;
                    summaryForm.update();
                    if (psForm != null) {
                        psForm.update();
                    }
                    Integer ti = productsTabs.indexOfComponent(pf.getRoot());
                    if (ti >= 0) {
                        productsTabs.setTitleAt(ti, pf.getProduct().getName() + (!getProposal().getConfig().isSalesSupport() ? (" (" + (getProposal().getCurrency().getSymbol() != null ?
                                getProposal().getCurrency().getSymbol() + " " : "") + "" + df.format(pf.getProduct().getRegionPrice()) + (getProposal().getCurrency().getSymbol() == null ?
                                " " + getProposal().getCurrency().getName() : "") + ")") : ""));

                    }
                }
            }

            public void setData(String key, Object data) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public Object getData(String key) {
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
        if (psForm != null) {
            psForm.update();
        }
    }

    public JTabbedPane getProductsTabs() {
        return productsTabs;
    }

    private JFrame getFrame() {
        return frame;
    }

    public void addPSForm() {
        if (proposal.getPSQuote().enabled()) {
            psForm = new PSQuoteForm(proposal, new PCTChangedListener() {
                public void act(Object src) {
                    if (src instanceof PSQuoteForm) {
                        titleUpdater.act(proposal);
                        summaryForm.update();
                        Integer ti = productsTabs.indexOfComponent(psForm.getRoot());
                        if (ti >= 0) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Professional services");
                            if(!proposal.getConfig().isSalesSupport()){
                                sb.append(" (");
                                if (proposal.getCurrency().getSymbol() != null) {
                                    sb.append(proposal.getCurrency().getSymbol());
                                    sb.append(" ");
                                }

                                sb.append(df.format(proposal.getPSQuote().getCleanPrice()));
                                if (proposal.getCurrency().getSymbol() == null) {
                                    sb.append(" ");
                                    sb.append(proposal.getCurrency().getName());
                                }
                                sb.append(")");
                            }
                            productsTabs.setTitleAt(ti, sb.toString());

                        }
                    }
                }

                public void setData(String key, Object data) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                public Object getData(String key) {
                    return null;  //To change body of implemented methods use File | Settings | File Templates.
                }
            }, df, getFrame());
            //productsTabs.addTab("PS quote", psForm.getRoot());
            productsTabs.insertTab("Professional service", null, psForm.getRoot(), null, 1);

            psForm.recalc();
            psForm.recalcTC();

            productsTabs.setSelectedComponent(psForm.getRoot());
        }
    }

    public void delPSForm() {
        this.getProposal().delPSQuote();
        productsTabs.remove(psForm.getRoot());
        summaryForm.update();
        psForm = null;
    }
}
