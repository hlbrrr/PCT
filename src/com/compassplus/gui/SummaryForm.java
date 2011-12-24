package com.compassplus.gui;

import com.compassplus.configurationModel.Currency;
import com.compassplus.configurationModel.Region;
import com.compassplus.configurationModel.SupportPlan;
import com.compassplus.proposalModel.Product;
import com.compassplus.proposalModel.Proposal;
import com.compassplus.utils.CommonUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 14.10.11
 * Time: 1:08
 */
public class SummaryForm {
    private Proposal proposal;
    private JPanel mainPanel;
    private JPanel settingsPanelLeft;
    private JPanel settingsPanelRight;
    private JPanel settingsPanelCenter;
    private JPanel settingsWrap;
    private JComboBox currencyField;
    private JLabel rateLabel;
    private JSpinner rateField;
    private JSpinner planRateField;
    private JComboBox supportPlanField;
    private PCTChangedListener currChanged;
    private PCTChangedListener titleUpdater;
    private PCTChangedListener updated;
    private JPanel productsTable;
    private DecimalFormat df;

    public SummaryForm(Proposal proposal, PCTChangedListener currChanged, DecimalFormat df, PCTChangedListener updated, PCTChangedListener titleUpdater) {
        this.currChanged = currChanged;
        this.titleUpdater = titleUpdater;
        this.updated = updated;
        this.df = df;
        this.proposal = proposal;
        mainPanel = new SummaryJPanel(this);
        settingsPanelLeft = new JPanel();
        settingsPanelCenter = new JPanel();
        settingsPanelRight = new JPanel();
        settingsWrap = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(settingsWrap);
        mainPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
        mainPanel.add(scroll, BorderLayout.CENTER);
        settingsWrap.setLayout(new GridBagLayout());
        productsTable = new JPanel(new GridBagLayout());
        productsTable.setMinimumSize(new Dimension(0, 0));
        productsTable.setBorder(new EmptyBorder(0, 8, 4, 8));
        {
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1 / 3d;
            c.fill = GridBagConstraints.BOTH;
            c.anchor = GridBagConstraints.NORTHWEST;
            settingsWrap.add(settingsPanelLeft, c);

            c.gridx++;
            c.anchor = GridBagConstraints.NORTH;
            settingsWrap.add(settingsPanelCenter, c);

            c.gridx++;
            c.anchor = GridBagConstraints.NORTHEAST;
            settingsWrap.add(settingsPanelRight, c);

            c.gridy = 1;
            c.gridx = 0;
            c.weightx = 1;
            c.weighty = 1;
            c.gridwidth = 3;
            settingsWrap.add(productsTable, c);
        }
        settingsPanelLeft.setLayout(new BoxLayout(settingsPanelLeft, BoxLayout.Y_AXIS));
        settingsPanelLeft.setBorder(new EmptyBorder(4, 4, 4, 4));
        settingsPanelCenter.setLayout(new BoxLayout(settingsPanelCenter, BoxLayout.Y_AXIS));
        settingsPanelCenter.setBorder(new EmptyBorder(4, 4, 4, 4));
        settingsPanelRight.setLayout(new BoxLayout(settingsPanelRight, BoxLayout.Y_AXIS));
        settingsPanelRight.setBorder(new EmptyBorder(4, 4, 4, 4));
        initForm(proposal);
        curChanged();
    }

    public void updateMainTitle(){
        titleUpdater.act(getProposal());
    }

    private void reloadPrices() {
        currChanged.act(this);
        updated.act(this);
    }

    private void initForm(Proposal proposal) {
        final SummaryForm that = this;
        {
            JLabel userLabel = new JLabel("Proposal created by");
            userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextField userField = new JTextField(getProposal().getUserName());
            userField.setEnabled(false);
            userField.setAlignmentX(Component.LEFT_ALIGNMENT);
            userField.setMaximumSize(new Dimension(userField.getMaximumSize().width, 23));
            userField.setMinimumSize(new Dimension(userField.getMinimumSize().width, 23));

            JPanel tmpPanel = new JPanel();
            tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.Y_AXIS));
            tmpPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel.add(userLabel);
            tmpPanel.add(userField);
            settingsPanelLeft.add(tmpPanel);
        }
        {
            JSpinner maxDiscountField = new JSpinner(new SpinnerNumberModel(0, 0, 0, 0));
            JLabel maxDiscountLabel = new JLabel("Max. product discount (%)");
            maxDiscountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            maxDiscountField.setValue((int) (getProposal().getConfig().getMaxDiscount() * 100));
            maxDiscountField.setEnabled(false);
            maxDiscountField.setAlignmentX(Component.LEFT_ALIGNMENT);
            maxDiscountField.setMaximumSize(new Dimension(maxDiscountField.getMaximumSize().width, 23));
            maxDiscountField.setMinimumSize(new Dimension(maxDiscountField.getMinimumSize().width, 23));

            JPanel tmpPanel = new JPanel();
            tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.Y_AXIS));
            tmpPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel.add(maxDiscountLabel);
            tmpPanel.add(maxDiscountField);

            JSpinner maxSupportDiscountField = new JSpinner(new SpinnerNumberModel(0, 0, 0, 0));
            JLabel maxSupportDiscountLabel = new JLabel("Max. support discount (%)");
            maxSupportDiscountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            maxSupportDiscountField.setValue((int) (getProposal().getConfig().getMaxSupportDiscount() * 100));
            maxSupportDiscountField.setEnabled(false);
            maxSupportDiscountField.setAlignmentX(Component.LEFT_ALIGNMENT);
            maxSupportDiscountField.setMaximumSize(new Dimension(maxSupportDiscountField.getMaximumSize().width, 23));
            maxSupportDiscountField.setMinimumSize(new Dimension(maxSupportDiscountField.getMinimumSize().width, 23));

            JPanel tmpPanel2 = new JPanel();
            tmpPanel2.setLayout(new BoxLayout(tmpPanel2, BoxLayout.Y_AXIS));
            tmpPanel2.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel2.add(maxSupportDiscountLabel);
            tmpPanel2.add(maxSupportDiscountField);


            /*JPanel discounts = new JPanel(new GridBagLayout());

            {
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = 0;
                c.gridy = 0;
                c.weightx = 0.5;
                c.fill = GridBagConstraints.BOTH;
                c.anchor = GridBagConstraints.NORTHWEST;
                discounts.add(tmpPanel, c);

                c.gridx = 1;
                c.anchor = GridBagConstraints.NORTHEAST;
                discounts.add(tmpPanel2, c);

            }*/


            settingsPanelCenter.add(tmpPanel);
            settingsPanelRight.add(tmpPanel2);
        }
        {
            JLabel clientNameLabel = new JLabel("Client name");
            clientNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            final JTextField clientNameField = new JTextField(getProposal().getClientName());
            clientNameField.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    changed();
                }

                public void removeUpdate(DocumentEvent e) {
                    changed();
                }

                public void changedUpdate(DocumentEvent e) {
                    changed();
                }

                public void changed() {
                    getProposal().setClientName(clientNameField.getText());
                    updated.act(that);
                    updateMainTitle();
                }
            });

            clientNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
            clientNameField.setMaximumSize(new Dimension(clientNameField.getMaximumSize().width, 23));
            clientNameField.setMinimumSize(new Dimension(clientNameField.getMinimumSize().width, 23));

            JPanel tmpPanel = new JPanel();
            tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.Y_AXIS));
            tmpPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel.add(clientNameLabel);
            tmpPanel.add(clientNameField);
            settingsPanelLeft.add(tmpPanel);
        }
        {
            JLabel projectNameLabel = new JLabel("Project name");
            projectNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            final JTextField projectNameField = new JTextField(getProposal().getProjectName());
            projectNameField.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    changed();
                }

                public void removeUpdate(DocumentEvent e) {
                    changed();
                }

                public void changedUpdate(DocumentEvent e) {
                    changed();
                }

                public void changed() {
                    getProposal().setProjectName(projectNameField.getText());
                    updated.act(that);
                    updateMainTitle();
                }
            });
            projectNameField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JTextField src = (JTextField) e.getSource();
                    getProposal().setProjectName(src.getText());
                }
            });
            projectNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
            projectNameField.setMaximumSize(new Dimension(projectNameField.getMaximumSize().width, 23));
            projectNameField.setMinimumSize(new Dimension(projectNameField.getMinimumSize().width, 23));

            JPanel tmpPanel = new JPanel();
            tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.Y_AXIS));
            tmpPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel.add(projectNameLabel);
            tmpPanel.add(projectNameField);
            settingsPanelCenter.add(tmpPanel);
        }
        {
            JLabel regionLabel = new JLabel("Region");
            regionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JComboBox regionField = new JComboBox(getProposal().getConfig().getRegions().values().toArray());
            regionField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JComboBox src = (JComboBox) e.getSource();
                    getProposal().setRegion((Region) src.getSelectedItem());
                    initCurrency(getProposal().getConfig().getCurrencies().get(getProposal().getRegion().getDefaultCurrencyName()));
                    initPlanRate();
                }
            });

            regionField.setSelectedItem(getProposal().getRegion() != null ? getProposal().getRegion() : getProposal().getConfig().getRegions().values().toArray()[0]);

            regionField.setAlignmentX(Component.LEFT_ALIGNMENT);
            regionField.setMaximumSize(new Dimension(regionField.getMaximumSize().width, 23));
            regionField.setMinimumSize(new Dimension(regionField.getMinimumSize().width, 23));
            JPanel tmpPanel = new JPanel();
            tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.Y_AXIS));
            tmpPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel.add(regionLabel);
            tmpPanel.add(regionField);
            settingsPanelLeft.add(tmpPanel);
        }
        {
            JLabel dateLabel = new JLabel("Creation date");
            dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextField dateField = new JTextField(getProposal().getDate());
            dateField.setEnabled(false);
            dateField.setAlignmentX(Component.LEFT_ALIGNMENT);
            dateField.setMaximumSize(new Dimension(dateField.getMaximumSize().width, 23));
            dateField.setMinimumSize(new Dimension(dateField.getMinimumSize().width, 23));

            JPanel tmpPanel = new JPanel();
            tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.Y_AXIS));
            tmpPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel.add(dateLabel);
            tmpPanel.add(dateField);
            settingsPanelRight.add(tmpPanel);
        }
        {
            rateLabel = new JLabel();
            rateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            rateField = new JSpinner(new SpinnerNumberModel(0, 0, 10000000d, 0.01d));
            rateField.setMaximumSize(new Dimension(rateField.getMaximumSize().width, 23));
            rateField.setMinimumSize(new Dimension(rateField.getMinimumSize().width, 23));
            rateField.setAlignmentX(Component.LEFT_ALIGNMENT);

            rateField.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    final ChangeEvent ev = e;
                    //SwingUtilities.invokeLater(new Runnable() {
                    // public void run() {
                    if (ev.getSource() == rateField) {
                        getProposal().getCurrency().setRate((Double) rateField.getValue());
                        getProposal().setCurrencyRate((Double) rateField.getValue());
                        reloadPrices();
                    }
                    //  }
                    //});
                }
            });

            if (getProposal().getCurrency() != null) {
                getProposal().getCurrency().setRate(getProposal().getCurrencyRate());
            }

            JPanel tmpPanel = new JPanel();
            tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.Y_AXIS));
            tmpPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel.add(rateLabel);
            tmpPanel.add(rateField);

            JLabel currencyLabel = new JLabel("Currency");
            currencyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            currencyField = new JComboBox();
            currencyField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    final ActionEvent ev = e;
                    //SwingUtilities.invokeLater(new Runnable() {
                    //   public void run() {
                    curChanged();
                    //     }
                    //});
                }
            });

            currencyField.setAlignmentX(Component.LEFT_ALIGNMENT);
            currencyField.setMaximumSize(new Dimension(currencyField.getMaximumSize().width, 23));
            currencyField.setMinimumSize(new Dimension(currencyField.getMinimumSize().width, 23));
            JPanel tmpPanel2 = new JPanel();
            tmpPanel2.setLayout(new BoxLayout(tmpPanel2, BoxLayout.Y_AXIS));
            tmpPanel2.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel2.add(currencyLabel);
            tmpPanel2.add(currencyField);
            settingsPanelCenter.add(tmpPanel2);
            settingsPanelRight.add(tmpPanel);
        }


        {
            JLabel planRateLabel = new JLabel("Support plan rate (%)");
            planRateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            planRateField = new JSpinner(new SpinnerNumberModel(0, 0, 0, 0));
            planRateField.setEnabled(false);
            planRateField.setAlignmentX(Component.LEFT_ALIGNMENT);
            planRateField.setMaximumSize(new Dimension(planRateField.getMaximumSize().width, 23));
            planRateField.setMinimumSize(new Dimension(planRateField.getMinimumSize().width, 23));

            JPanel tmpPanel = new JPanel();
            tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.Y_AXIS));
            tmpPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel.add(planRateLabel);
            tmpPanel.add(planRateField);

            JLabel supportPlanLabel = new JLabel("Support plan");
            supportPlanLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            supportPlanField = new JComboBox();
            supportPlanField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    final ActionEvent ev = e;
                    //SwingUtilities.invokeLater(new Runnable() {
                    //public void run() {
                    JComboBox src = (JComboBox) ev.getSource();
                    getProposal().setSupportPlan((SupportPlan) src.getSelectedItem());
                    initPlanRate();
                    updated.act(that);
                    //}
                    //});
                }
            });

            supportPlanField.setAlignmentX(Component.LEFT_ALIGNMENT);
            supportPlanField.setMaximumSize(new Dimension(supportPlanField.getMaximumSize().width, 23));
            supportPlanField.setMinimumSize(new Dimension(supportPlanField.getMinimumSize().width, 23));
            JPanel tmpPanel2 = new JPanel();
            tmpPanel2.setLayout(new BoxLayout(tmpPanel2, BoxLayout.Y_AXIS));
            tmpPanel2.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel2.add(supportPlanLabel);
            tmpPanel2.add(supportPlanField);
            settingsPanelLeft.add(tmpPanel2);
            settingsPanelCenter.add(tmpPanel);
        }
        //maxDiscountField.setMaximumSize(new Dimension(planRateField.getMaximumSize().width, planRateField.getMinimumSize().height));
        initCurrency(getProposal().getCurrency());
    }

    private void curChanged() {
        //SwingUtilities.invokeLater(new Runnable() {
        //public void run() {
        JComboBox src = currencyField;
        getProposal().setCurrency((Currency) src.getSelectedItem());
        if (src.getSelectedItem() != null) {
            rateField.setValue(((Currency) src.getSelectedItem()).getRate());
            rateLabel.setText("Currency rate (USD/" + ((Currency) src.getSelectedItem()).getName() + ")");
            reloadPrices();

            initSupportPlans(getProposal().getSupportPlan());
        }
        // }
        //});
    }

    private void initCurrency(Object curr) {
        if (currencyField != null) {
            Object oldCurrency = curr != null ? curr : currencyField.getSelectedItem();
            currencyField.removeAllItems();
            for (Currency c : getProposal().getConfig().getCurrencies().values()) {
                if (c.getAllowedRegions().size() > 0) {
                    for (String r : c.getAllowedRegions()) {
                        if (r.equals(getProposal().getRegion().getKey())) {
                            currencyField.addItem(c);
                            break;
                        }
                    }
                } else {
                    currencyField.addItem(c);
                }
            }
            if (oldCurrency != null) {
                currencyField.setSelectedItem(oldCurrency);
            }
            curChanged();
        }
    }

    private void initSupportPlans(Object plan) {
        if (supportPlanField != null) {
            Object oldPlan = plan != null ? plan : supportPlanField.getSelectedItem();
            supportPlanField.removeAllItems();
            SupportPlan defaultPlan = null;
            for (SupportPlan sp : getProposal().getConfig().getSupportPlans().values()) {
                supportPlanField.addItem(sp);
                if (sp.isDefault()) {
                    defaultPlan = sp;
                }
            }
            if (oldPlan != null) {
                supportPlanField.setSelectedItem(oldPlan);
            } else if (defaultPlan != null) {
                supportPlanField.setSelectedItem(defaultPlan);
            }
        }
    }

    private void initPlanRate() {
        if (planRateField != null) {
            planRateField.setValue((int) (proposal.getSupportRate() * 100));
        }
        makeProductList();
    }

    public JPanel getRoot() {
        return mainPanel;
    }

    private Proposal getProposal() {
        return this.proposal;
    }

    public void update() {
        initSupportPlans(null);
        makeProductList();
        updated.act(this);
    }

    private void makeProductList() {
        if (productsTable != null) {
            final SummaryForm that = this;
            productsTable.removeAll();
            if (getProposal().getProducts().size() > 0) {
                int size = getProposal().getProducts().size() + 1;
                GridBagConstraints c = new GridBagConstraints();
                c.gridy = 0;
                c.weightx = 1;
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.PAGE_START;
                Border border = BorderFactory.createMatteBorder(1, 1, 0, 0, Color.black);
                Border lborder = BorderFactory.createMatteBorder(1, 1, 0, 1, Color.black);
                {
                    c.gridx = 0;
                    JLabel label = new JLabel("Product");
                    JPanel panel = new JPanel();
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                    productsTable.add(panel, c);
                }
                {
                    c.gridx++;
                    JLabel label = new JLabel("Sale");
                    JPanel panel = new JPanel();
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                    productsTable.add(panel, c);
                }
                {
                    c.gridx++;
                    JLabel label = new JLabel("Global list price");
                    JPanel panel = new JPanel();
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                    productsTable.add(panel, c);
                }
                if (getProposal().getRegion().getRate() != 1d) {
                    c.gridx++;
                    JLabel label = new JLabel("Regional list price");
                    JPanel panel = new JPanel();
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                    productsTable.add(panel, c);
                }
                {
                    c.gridx++;
                    JLabel label = new JLabel("Product discount (%)");
                    JPanel panel = new JPanel();
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                    productsTable.add(panel, c);
                }
                {
                    c.gridx++;
                    JLabel label = new JLabel("End user price");
                    JPanel panel = new JPanel();
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                    productsTable.add(panel, c);
                }
                {
                    c.gridx++;
                    JLabel label = new JLabel("Support discount (%)");
                    JPanel panel = new JPanel();
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                    productsTable.add(panel, c);
                }
                {
                    c.gridx++;
                    JLabel label = new JLabel("Support price");
                    JPanel panel = new JPanel();
                    panel.add(label);
                    panel.setBorder(lborder);
                    panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                    productsTable.add(panel, c);
                }
                final CustomJLabel gleulabel = new CustomJLabel(new PCTChangedListener() {
                    public void act(Object src) {
                        Double price = 0d;
                        for (Product p : getProposal().getProducts().values()) {
                            price += p.getEndUserPrice();
                        }
                        ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?
                                getProposal().getCurrency().getSymbol() + " " : "") + df.format(price) + (getProposal().getCurrency().getSymbol() == null ?
                                " " + getProposal().getCurrency().getName() : ""));
                    }

                    public void setData(Object data) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public Object getData() {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }
                });
                final CustomJLabel glsplabel = new CustomJLabel(new PCTChangedListener() {
                    public void act(Object src) {
                        if (planRateField != null) {
                            Double price = 0d;
                            for (Product p : getProposal().getProducts().values()) {
                                price += p.getSupportPrice();
                            }

                            if (getProposal().isPrimarySale() && getProposal().getSupportPlan() != null && getProposal().getSupportPlan().getMinPrice() != null && getProposal().getSupportPlan().getMinPrice() > 0
                                    && price < CommonUtils.getInstance().toNextThousand(getProposal().getSupportPlan().getMinPrice() * getProposal().getCurrencyRate())) {
                                price = CommonUtils.getInstance().toNextThousand(getProposal().getSupportPlan().getMinPrice() * getProposal().getCurrencyRate());
                                //panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                                ((CustomJLabel) src).setForeground(Color.red);
                            }

                            ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?
                                    getProposal().getCurrency().getSymbol() + " " : "") + df.format(price) + (getProposal().getCurrency().getSymbol() == null ?
                                    " " + getProposal().getCurrency().getName() : ""));
                        }
                    }

                    public void setData(Object data) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public Object getData() {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }
                });
                for (Product p : getProposal().getProducts().values()) {
                    c.gridy++;
                    {
                        c.gridx = 0;
                        JLabel label = new JLabel(!"".equals(p.getProduct().getShortName()) ? p.getProduct().getShortName() : p.getName());
                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        productsTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        JLabel label = new JLabel(p.getSecondarySale() ? "upgrade" : "initial");
                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        productsTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        JLabel label = new JLabel((p.getProposal().getCurrency().getSymbol() != null ?
                                p.getProposal().getCurrency().getSymbol() + " " : "") + df.format(p.getPrice()) + (p.getProposal().getCurrency().getSymbol() == null ?
                                " " + p.getProposal().getCurrency().getName() : ""));
                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        productsTable.add(panel, c);
                    }
                    if (getProposal().getRegion().getRate() != 1d) {
                        {
                            c.gridx++;
                            JLabel label = new JLabel((p.getProposal().getCurrency().getSymbol() != null ?
                                    p.getProposal().getCurrency().getSymbol() + " " : "") + df.format(p.getPrice() * getProposal().getRegion().getRate()) + (p.getProposal().getCurrency().getSymbol() == null ?
                                    " " + p.getProposal().getCurrency().getName() : ""));
                            JPanel panel = new JPanel();
                            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                            label.setBorder(new EmptyBorder(4, 4, 4, 4));
                            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                            panel.setPreferredSize(new Dimension(0, 32));
                            panel.add(label);
                            panel.setBorder(border);
                            panel.setBackground(Color.white);
                            productsTable.add(panel, c);
                        }
                    }
                    final Product prod = p;
                    final CustomJLabel eulabel = new CustomJLabel(new PCTChangedListener() {
                        public void act(Object src) {
                            Double price = 0d;
                            price = prod.getEndUserPrice();

                            ((CustomJLabel) src).setText((prod.getProposal().getCurrency().getSymbol() != null ?
                                    prod.getProposal().getCurrency().getSymbol() + " " : "") + df.format(price) + (prod.getProposal().getCurrency().getSymbol() == null ?
                                    " " + prod.getProposal().getCurrency().getName() : ""));
                        }

                        public void setData(Object data) {
                            //To change body of implemented methods use File | Settings | File Templates.
                        }

                        public Object getData() {
                            return null;  //To change body of implemented methods use File | Settings | File Templates.
                        }
                    });
                    {
                        c.gridx++;
                        final JSpinner sp = new DiscountJSpinner("Maximum product discount is ", getRoot(), (int) (prod.getDiscount() * 100), 0, (int) (getProposal().getConfig().getMaxDiscount() * 100), 1);

                        sp.addChangeListener(new ChangeListener() {
                            public void stateChanged(ChangeEvent e) {
                                final ChangeEvent ev = e;
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        if (ev.getSource() == sp) {
                                            prod.setDiscount((Integer) sp.getValue() / 100d);
                                            eulabel.call();
                                            gleulabel.call();
                                            updated.act(that);
                                        }
                                    }
                                });
                            }
                        });
                        sp.setMaximumSize(new Dimension(sp.getMaximumSize().width, sp.getMinimumSize().height));

                        JPanel panelW = new JPanel();
                        panelW.setLayout(new BoxLayout(panelW, BoxLayout.Y_AXIS));
                        panelW.setBorder(new EmptyBorder(4, 4, 4, 4));
                        panelW.add(sp);
                        panelW.setBackground(Color.white);

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        panel.setMinimumSize(new Dimension(panel.getMinimumSize().width, 32));

                        sp.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(panelW);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        productsTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        eulabel.setBorder(new EmptyBorder(4, 4, 4, 4));
                        eulabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(eulabel);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        productsTable.add(panel, c);
                    }
                    final CustomJLabel splabel = new CustomJLabel(new PCTChangedListener() {
                        public void act(Object src) {
                            if (planRateField != null) {
                                Double price = prod.getSupportPrice();

                                ((CustomJLabel) src).setText((prod.getProposal().getCurrency().getSymbol() != null ?
                                        prod.getProposal().getCurrency().getSymbol() + " " : "") + df.format(price) + (prod.getProposal().getCurrency().getSymbol() == null ?
                                        " " + prod.getProposal().getCurrency().getName() : ""));
                            }
                        }

                        public void setData(Object data) {
                            //To change body of implemented methods use File | Settings | File Templates.
                        }

                        public Object getData() {
                            return null;  //To change body of implemented methods use File | Settings | File Templates.
                        }
                    });
                    {
                        c.gridx++;
                        final JSpinner sp = new DiscountJSpinner("Maximum support discount is ", getRoot(), (int) (prod.getSupportDiscount() * 100), 0, (int) (getProposal().getConfig().getMaxSupportDiscount() * 100), 1);
                        sp.addChangeListener(new ChangeListener() {
                            public void stateChanged(ChangeEvent e) {
                                final ChangeEvent ev = e;
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        if (ev.getSource() == sp) {
                                            prod.setSupportDiscount((Integer) sp.getValue() / 100d);
                                            splabel.call();
                                            glsplabel.call();
                                            updated.act(that);
                                        }
                                    }
                                });
                            }
                        });
                        sp.setMaximumSize(new Dimension(sp.getMaximumSize().width, sp.getMinimumSize().height));

                        JPanel panelW = new JPanel();
                        panelW.setLayout(new BoxLayout(panelW, BoxLayout.Y_AXIS));
                        panelW.setBorder(new EmptyBorder(4, 4, 4, 4));
                        panelW.add(sp);
                        panelW.setBackground(Color.white);

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        panel.setMinimumSize(new Dimension(panel.getMinimumSize().width, 32));

                        sp.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(panelW);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        productsTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        /*if (planRateField != null) {
                            label.setText((p.getProposal().getCurrency().getSymbol() != null ?
                                    p.getProposal().getCurrency().getSymbol() + " " : "") + df.format(p.getPrice() * getProposal().getRegion().getRate() * ((Integer) planRateField.getValue() / 100d)) + (p.getProposal().getCurrency().getSymbol() == null ?
                                    " " + p.getProposal().getCurrency().getName() : ""));
                        }*/
                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        splabel.setBorder(new EmptyBorder(4, 4, 4, 4));
                        splabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(splabel);
                        panel.setBorder(lborder);
                        panel.setBackground(Color.white);
                        productsTable.add(panel, c);
                    }
                    eulabel.call();
                    splabel.call();
                }
                c.gridy++;
                c.weightx = 1;
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.PAGE_START;
                border = BorderFactory.createMatteBorder(1, 1, 1, 0, Color.black);
                lborder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black);
                {
                    c.gridx = 0;
                    JLabel label = new JLabel("");
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    productsTable.add(panel, c);
                }
                {
                    c.gridx++;
                    JLabel label = new JLabel("");
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    productsTable.add(panel, c);
                }
                {
                    c.gridx++;
                    JLabel label = new JLabel((getProposal().getCurrency().getSymbol() != null ?
                            getProposal().getCurrency().getSymbol() + " " : "") + df.format(getProposal().getPrice()) + (getProposal().getCurrency().getSymbol() == null ?
                            " " + getProposal().getCurrency().getName() : ""));
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    productsTable.add(panel, c);
                }
                if (getProposal().getRegion().getRate() != 1d) {
                    c.gridx++;
                    JLabel label = new JLabel((getProposal().getCurrency().getSymbol() != null ?
                            getProposal().getCurrency().getSymbol() + " " : "") + df.format(getProposal().getPrice() * getProposal().getRegion().getRate()) + (getProposal().getCurrency().getSymbol() == null ?
                            " " + getProposal().getCurrency().getName() : ""));
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    productsTable.add(panel, c);
                }
                {
                    c.gridx++;
                    JLabel label = new JLabel("");
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    productsTable.add(panel, c);
                }
                {
                    c.gridx++;
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    gleulabel.setBorder(new EmptyBorder(4, 4, 4, 4));
                    gleulabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(gleulabel);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    productsTable.add(panel, c);
                }
                {
                    c.gridx++;
                    JLabel label = new JLabel("");
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    productsTable.add(panel, c);
                }
                {
                    c.gridx++;
                    JPanel panel = new JPanel();

                    panel.setBackground(Color.white);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    glsplabel.setBorder(new EmptyBorder(4, 4, 4, 4));
                    glsplabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(glsplabel);
                    panel.setBorder(lborder);
                    productsTable.add(panel, c);
                }
                gleulabel.call();
                glsplabel.call();

                JLabel em = new JLabel("");
                c.weighty = 1.0;   //request any extra vertical space
                c.anchor = GridBagConstraints.PAGE_END; //bottom of space
                c.gridx = 0;       //aligned with button 2
                c.gridy += 1;       //third row
                productsTable.add(em, c);
            }
        }
    }
}
