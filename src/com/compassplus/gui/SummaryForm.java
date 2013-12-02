package com.compassplus.gui;

import com.compassplus.configurationModel.*;
import com.compassplus.proposalModel.OracleLicense;
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
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

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
    private JFrame frame;
    private JComboBox currencyField;
    private JLabel rateLabel;
    private JSpinner rateField;
    private JSpinner planRateField;
    private JComboBox supportPlanField;
    private PCTChangedListener currChanged;
    private PCTChangedListener psFormUpdater;
    private PCTChangedListener titleUpdater;

    private PCTChangedListener updated;
    private JPanel productsTable;
    private JPanel psTable;
    private JPanel oracleTable;
    private DecimalFormat df;
    private JLabel mdLabel;
    private JSpinner mdField;

    public SummaryForm(Proposal proposal, PCTChangedListener currChanged, DecimalFormat df, PCTChangedListener updated, PCTChangedListener titleUpdater, PCTChangedListener psFormUpdater) {
        this.currChanged = currChanged;
        this.titleUpdater = titleUpdater;
        this.psFormUpdater = psFormUpdater;
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
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
        mainPanel.add(scroll, BorderLayout.CENTER);
        settingsWrap.setLayout(new GridBagLayout());
        productsTable = new JPanel(new GridBagLayout());
        productsTable.setMinimumSize(new Dimension(0, 0));
        productsTable.setBorder(new EmptyBorder(10, 8, 4, 8));

        psTable = new JPanel(new GridBagLayout());
        psTable.setMinimumSize(new Dimension(0, 0));
        psTable.setBorder(new EmptyBorder(10, 8, 4, 8));

        oracleTable = new JPanel(new GridBagLayout());
        oracleTable.setMinimumSize(new Dimension(0, 0));
        oracleTable.setBorder(new EmptyBorder(10, 8, 4, 8));
        {
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1 / 3d;
            c.fill = GridBagConstraints.BOTH;
            c.anchor = GridBagConstraints.NORTH;
            settingsWrap.add(settingsPanelLeft, c);

            c.gridx++;
            c.anchor = GridBagConstraints.NORTH;
            settingsWrap.add(settingsPanelCenter, c);

            c.gridx++;
            c.anchor = GridBagConstraints.NORTH;
            settingsWrap.add(settingsPanelRight, c);

            c.gridy = 1;
            c.gridx = 0;
            c.weightx = 1;
            c.weighty = 0;
            c.gridwidth = 3;
            settingsWrap.add(productsTable, c);
            if(!getProposal().getConfig().isSalesSupport()){
                c.gridy++;
                settingsWrap.add(psTable, c);
                c.gridy++;
                settingsWrap.add(oracleTable, c);
            }

            JLabel em = new JLabel("");
            c.weighty = 1.0;   //request any extra vertical space
            c.anchor = GridBagConstraints.PAGE_END; //bottom of space
            c.gridx = 0;       //aligned with button 2
            c.gridy += 1;       //third row
            settingsWrap.add(em, c);
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

    public void updateMainTitle() {
        titleUpdater.act(getProposal());
    }

    private void reloadPrices() {
        currChanged.act(this);
        updated.act(this);
        //if(getProposal().getPSQuote().enabled()){
            mdLabel.setText("Region M/D rate (" + getProposal().getCurrency().getName() + ")");
            mdField.setValue(getProposal().getRegion().getMDRate() * getProposal().getCurrencyRate());
        //}
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

            JSpinner maxSupportDiscountField = new JSpinner(new SpinnerNumberModel(0, 0, 0, 0));
            JLabel maxSupportDiscountLabel = new JLabel("Max. support discount (%)");
            maxSupportDiscountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            maxSupportDiscountField.setValue((int) (getProposal().getConfig().getMaxSupportDiscount() * 100));
            maxSupportDiscountField.setEnabled(false);
            maxSupportDiscountField.setAlignmentX(Component.LEFT_ALIGNMENT);
            maxSupportDiscountField.setMaximumSize(new Dimension(maxSupportDiscountField.getMaximumSize().width, 23));
            maxSupportDiscountField.setMinimumSize(new Dimension(maxSupportDiscountField.getMinimumSize().width, 23));


            JPanel tmpPanel = new JPanel();
            tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.Y_AXIS));
            tmpPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel.add(maxDiscountLabel);
            tmpPanel.add(maxDiscountField);

            JPanel tmpPanel2 = new JPanel();
            tmpPanel2.setLayout(new BoxLayout(tmpPanel2, BoxLayout.Y_AXIS));
            tmpPanel2.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel2.add(maxSupportDiscountLabel);
            tmpPanel2.add(maxSupportDiscountField);


            if (!getProposal().getConfig().isSalesSupport()) {
                settingsPanelCenter.add(tmpPanel);
                settingsPanelRight.add(tmpPanel2);
            }
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
            JSpinner maxDiscountField = new JSpinner(new SpinnerNumberModel(0, 0, 0, 0));
            JLabel maxDiscountLabel = new JLabel("Max. M/D discount (%)");
            maxDiscountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            maxDiscountField.setValue((int) (getProposal().getConfig().getMDRDiscount() * 100));
            maxDiscountField.setEnabled(false);
            maxDiscountField.setAlignmentX(Component.LEFT_ALIGNMENT);
            maxDiscountField.setMaximumSize(new Dimension(maxDiscountField.getMaximumSize().width, 23));
            maxDiscountField.setMinimumSize(new Dimension(maxDiscountField.getMinimumSize().width, 23));

            JSpinner maxSupportDiscountField = new JSpinner(new SpinnerNumberModel(0, 0, 0, 0));
            JLabel maxSupportDiscountLabel = new JLabel("Max. prof. service discount (%)");
            maxSupportDiscountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            maxSupportDiscountField.setValue((int) (getProposal().getConfig().getPSDiscount() * 100));
            maxSupportDiscountField.setEnabled(false);
            maxSupportDiscountField.setAlignmentX(Component.LEFT_ALIGNMENT);
            maxSupportDiscountField.setMaximumSize(new Dimension(maxSupportDiscountField.getMaximumSize().width, 23));
            maxSupportDiscountField.setMinimumSize(new Dimension(maxSupportDiscountField.getMinimumSize().width, 23));


            JPanel tmpPanel = new JPanel();
            tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.Y_AXIS));
            tmpPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel.add(maxDiscountLabel);
            tmpPanel.add(maxDiscountField);

            JPanel tmpPanel2 = new JPanel();
            tmpPanel2.setLayout(new BoxLayout(tmpPanel2, BoxLayout.Y_AXIS));
            tmpPanel2.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel2.add(maxSupportDiscountLabel);
            tmpPanel2.add(maxSupportDiscountField);


            if (!getProposal().getConfig().isSalesSupport()) {
                settingsPanelCenter.add(tmpPanel);
                settingsPanelRight.add(tmpPanel2);
            }
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
            settingsPanelLeft.add(tmpPanel);
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
            settingsPanelCenter.add(tmpPanel);
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

            if (!getProposal().getConfig().isSalesSupport()) {
                settingsPanelRight.add(tmpPanel);
            }
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
            if (!getProposal().getConfig().isSalesSupport()) {
                settingsPanelLeft.add(tmpPanel2);
                settingsPanelLeft.add(tmpPanel);
                JPanel tmpPanelE = new JPanel();
                tmpPanelE.setSize(1, 50);
                settingsPanelCenter.add(tmpPanelE);
            } else {
                JPanel tmpPanelE1 = new JPanel();
                tmpPanelE1.setSize(1, 50);
                settingsPanelCenter.add(tmpPanelE1);
                JPanel tmpPanelE2 = new JPanel();
                tmpPanelE2.setSize(1, 50);
                settingsPanelRight.add(tmpPanelE2);
            }
        }

        {
            mdField = new JSpinner(new SpinnerNumberModel(0d, 0d, 0d, 0d));
            mdLabel = new JLabel("");
            mdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            //mdField.setValue(getProposal().getRegion().getMDRate() * getProposal().getCurrencyRate());
            mdField.setEnabled(false);
            mdField.setAlignmentX(Component.LEFT_ALIGNMENT);
            mdField.setMaximumSize(new Dimension(mdField.getMaximumSize().width, 23));
            mdField.setMinimumSize(new Dimension(mdField.getMinimumSize().width, 23));

            JPanel tmpPanel = new JPanel();
            tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.Y_AXIS));
            tmpPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel.add(mdLabel);
            tmpPanel.add(mdField);

            if (!getProposal().getConfig().isSalesSupport()) {
                //if(getProposal().getPSQuote().enabled()){
                    settingsPanelRight.add(tmpPanel);
               /* }else{
                    JPanel tmpPanelE2 = new JPanel();
                    tmpPanelE2.setSize(1, 50);
                    settingsPanelRight.add(tmpPanelE2);
                }*/
            }
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
        makePSList();
        makeOracleList();
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
        makePSList();
        makeOracleList();
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
                    JLabel label = new JLabel("Product name");
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
                    if (getProposal().getConfig().isSalesSupport()) {
                        panel.setBorder(lborder);
                    }
                    panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                    productsTable.add(panel, c);
                }

                if (!getProposal().getConfig().isSalesSupport()) {
                    {
                        c.gridx++;
                        JLabel label = new JLabel("Global list price");
                        JPanel panel = new JPanel();
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                        productsTable.add(panel, c);
                    }
                    /*if (getProposal().getRegion().getRate() != 1d)*/
                    {
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
                        JLabel label = new JLabel("Mark-up (%)");
                        JPanel panel = new JPanel();
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                        productsTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        JLabel label = new JLabel("Marked-up price");
                        JPanel panel = new JPanel();
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                        productsTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        JLabel label = new JLabel("Prod. disc. (%)");
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
                        JLabel label = new JLabel("Sup. disc. (%)");
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
                }
                final CustomJLabel glmuplabel = new CustomJLabel(new PCTChangedListener() {
                    public void act(Object src) {
                        Double price = 0d;
                        for (Product p : getProposal().getProducts().values()) {
                            price += p.getRegionPrice();
                        }
                        ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?
                                getProposal().getCurrency().getSymbol() + " " : "") + df.format(price) + (getProposal().getCurrency().getSymbol() == null ?
                                " " + getProposal().getCurrency().getName() : ""));
                    }

                    public void setData(String key, Object data) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public Object getData(String key) {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }
                });
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

                    public void setData(String key, Object data) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public Object getData(String key) {
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

                    public void setData(String key, Object data) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public Object getData(String key) {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }
                });
                int lastProductIndex = 0;
                for (Product p : getProposal().getProducts().values()) {
                    lastProductIndex++;
                    if (getProposal().getConfig().isSalesSupport() && lastProductIndex == getProposal().getProducts().values().size()) {
                        border = BorderFactory.createMatteBorder(1, 1, 1, 0, Color.black);
                        lborder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black);
                    }
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
                        if (getProposal().getConfig().isSalesSupport()) {
                            panel.setBorder(lborder);
                        }
                        panel.setBackground(Color.white);
                        productsTable.add(panel, c);
                    }

                    if (!getProposal().getConfig().isSalesSupport()) {
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
                        /*if (getProposal().getRegion().getRate() != 1d)*/
                        {
                            {
                                c.gridx++;
                                JLabel label = new JLabel((p.getProposal().getCurrency().getSymbol() != null ?
                                        p.getProposal().getCurrency().getSymbol() + " " : "") + df.format(p.getRegionPrice(true)) + (p.getProposal().getCurrency().getSymbol() == null ?
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

                        final JSpinner supportDiscount = new JSpinner(new SpinnerNumberModel((prod.getSupportDiscount() * 100), 0d, 100d, 1d));
                        final JSpinner productDiscount = new JSpinner(new SpinnerNumberModel((prod.getDiscount() * 100), 0d, 100d, 1d));
                        final JSpinner markUp = new JSpinner(new SpinnerNumberModel((prod.getMarkUp() - 1) * 100d, 0d, 100d, 1d));

                        final SpinnerNumberModel markUpSNP = new SpinnerNumberModel(prod.getRegionPrice().doubleValue(), prod.getRegionPrice(true).doubleValue(), prod.getRegionPrice(true).doubleValue() * 2, 1000d);
                        final JSpinner markUpSpinner = new JSpinner(markUpSNP);

                        final SpinnerNumberModel productDiscountSpinnerSNP = new SpinnerNumberModel(prod.getEndUserPrice().doubleValue(), 0d, prod.getRegionPrice().doubleValue(), 1000d);
                        final JSpinner productDiscountSpinner = new JSpinner(productDiscountSpinnerSNP);


                        final SpinnerNumberModel supportDiscountSpinnerSNP = new SpinnerNumberModel(prod.getSupportPrice().doubleValue(), 0d, prod.getSupportPriceUndiscounted().doubleValue(), 1000d);
                        final JSpinner supportDiscountSpinner = new JSpinner(supportDiscountSpinnerSNP);


                        final ChangeListener markUpCL = new ChangeListener() {
                            public void stateChanged(ChangeEvent e) {
                                final ChangeEvent ev = e;
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        if (ev.getSource() == markUp) {
                                            prod.setMarkUp(1d + (Double) markUp.getValue() / 100d);
                                            markUpSpinner.setValue(prod.getRegionPrice());
                                        }
                                    }
                                });
                            }
                        };
                        ChangeListener markUpSpinnerCL = new ChangeListener() {
                            public void stateChanged(ChangeEvent e) {
                                final ChangeEvent ev = e;
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        if (ev.getSource() == markUpSpinner) {
                                            markUp.removeChangeListener(markUpCL);
                                            Double mu = (100 * (Double) markUpSpinner.getValue()) / prod.getRegionPrice(true) - 100;
                                            markUp.setValue(mu);
                                            markUp.addChangeListener(markUpCL);

                                            prod.setMarkUp(1d + (Double) markUp.getValue() / 100d);
                                            gleulabel.call();
                                            glsplabel.call();
                                            glmuplabel.call();
                                            productDiscountSpinnerSNP.setMaximum(prod.getRegionPrice());
                                            productDiscountSpinner.setValue(prod.getEndUserPrice());

                                            supportDiscountSpinnerSNP.setMaximum(prod.getSupportPriceUndiscounted());
                                            supportDiscountSpinner.setValue(prod.getSupportPrice());

                                            updated.act(that);
                                            titleUpdater.act(proposal);
                                        }
                                    }
                                });
                            }
                        };
                        markUpSpinner.addChangeListener(markUpSpinnerCL);
                        markUp.addChangeListener(markUpCL);

                        final ChangeListener productDiscountCL = new ChangeListener() {
                            public void stateChanged(ChangeEvent e) {
                                final ChangeEvent ev = e;
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        if (ev.getSource() == productDiscount) {
                                            prod.setDiscount((Double) productDiscount.getValue() / 100d);
                                            productDiscountSpinner.setValue(prod.getEndUserPrice());
                                            //eulabel.call();
                                            //gleulabel.call();
                                            //updated.act(that);
                                            //titleUpdater.act(proposal);
                                        }
                                    }
                                });
                            }
                        };
                        ChangeListener productDiscountSpinnerCL = new ChangeListener() {
                            public void stateChanged(ChangeEvent e) {
                                final ChangeEvent ev = e;
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        if (ev.getSource() == productDiscountSpinner) {
                                            productDiscount.removeChangeListener(productDiscountCL);
                                            Double mu = (1d - ((Double) productDiscountSpinner.getValue()) / prod.getRegionPrice()) * 100;
                                            productDiscount.setValue(mu);
                                            productDiscount.addChangeListener(productDiscountCL);

                                            prod.setDiscount((Double) productDiscount.getValue() / 100d);
                                            //eulabel.call();
                                            gleulabel.call();
                                            updated.act(that);
                                            titleUpdater.act(proposal);
                                        }
                                    }
                                });
                            }
                        };

                        productDiscountSpinner.addChangeListener(productDiscountSpinnerCL);
                        productDiscount.addChangeListener(productDiscountCL);

                        final ChangeListener supportDiscountCL = new ChangeListener() {
                            public void stateChanged(ChangeEvent e) {
                                final ChangeEvent ev = e;
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        if (ev.getSource() == supportDiscount) {
                                            prod.setSupportDiscount((Double) supportDiscount.getValue() / 100d);
                                            supportDiscountSpinner.setValue(prod.getSupportPrice());
                                            /*splabel.call();
                                            glsplabel.call();
                                            updated.act(that);
                                            titleUpdater.act(proposal);*/
                                        }
                                    }
                                });
                            }
                        };
                        ChangeListener supportDiscountSpinnerCL = new ChangeListener() {
                            public void stateChanged(ChangeEvent e) {
                                final ChangeEvent ev = e;
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        if (ev.getSource() == supportDiscountSpinner) {
                                            supportDiscount.removeChangeListener(supportDiscountCL);
                                            Double mu = (1d - ((Double) supportDiscountSpinner.getValue()) / prod.getSupportPriceUndiscounted()) * 100;
                                            supportDiscount.setValue(mu);
                                            supportDiscount.addChangeListener(supportDiscountCL);

                                            prod.setSupportDiscount((Double) supportDiscount.getValue() / 100d);
                                            //splabel.call();
                                            glsplabel.call();
                                            updated.act(that);
                                            titleUpdater.act(proposal);
                                        }
                                    }
                                });
                            }
                        };

                        supportDiscountSpinner.addChangeListener(supportDiscountSpinnerCL);
                        supportDiscount.addChangeListener(supportDiscountCL);

                        {
                            c.gridx++;
                            markUp.setMaximumSize(new Dimension(markUp.getMaximumSize().width, markUp.getMinimumSize().height));

                            JPanel panelW = new JPanel();
                            panelW.setLayout(new BoxLayout(panelW, BoxLayout.Y_AXIS));
                            panelW.setBorder(new EmptyBorder(4, 4, 4, 4));
                            panelW.add(markUp);
                            panelW.setBackground(Color.white);

                            JPanel panel = new JPanel();
                            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                            panel.setMinimumSize(new Dimension(panel.getMinimumSize().width, 32));

                            markUp.setAlignmentX(Component.RIGHT_ALIGNMENT);
                            panel.setPreferredSize(new Dimension(0, 32));
                            panel.add(panelW);
                            panel.setBorder(border);
                            panel.setBackground(Color.white);
                            productsTable.add(panel, c);
                        }
                        {
                            c.gridx++;
                            markUpSpinner.setMaximumSize(new Dimension(markUpSpinner.getMaximumSize().width, markUpSpinner.getMinimumSize().height));

                            JPanel panelW = new JPanel();
                            panelW.setLayout(new BoxLayout(panelW, BoxLayout.Y_AXIS));
                            panelW.setBorder(new EmptyBorder(4, 4, 4, 4));
                            panelW.add(markUpSpinner);
                            panelW.setBackground(Color.white);

                            JPanel panel = new JPanel();
                            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                            panel.setMinimumSize(new Dimension(panel.getMinimumSize().width, 32));

                            markUpSpinner.setAlignmentX(Component.RIGHT_ALIGNMENT);
                            panel.setPreferredSize(new Dimension(0, 32));
                            panel.add(panelW);
                            panel.setBorder(border);
                            panel.setBackground(Color.white);
                            productsTable.add(panel, c);
                        }

                        {
                            c.gridx++;

                            productDiscount.setMaximumSize(new Dimension(productDiscount.getMaximumSize().width, productDiscount.getMinimumSize().height));

                            JPanel panelW = new JPanel();
                            panelW.setLayout(new BoxLayout(panelW, BoxLayout.Y_AXIS));
                            panelW.setBorder(new EmptyBorder(4, 4, 4, 4));
                            panelW.add(productDiscount);
                            panelW.setBackground(Color.white);

                            JPanel panel = new JPanel();
                            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                            panel.setMinimumSize(new Dimension(panel.getMinimumSize().width, 32));

                            productDiscount.setAlignmentX(Component.RIGHT_ALIGNMENT);
                            panel.setPreferredSize(new Dimension(0, 32));
                            panel.add(panelW);
                            panel.setBorder(border);
                            panel.setBackground(Color.white);
                            productsTable.add(panel, c);
                        }
                        {
                            c.gridx++;

                            productDiscountSpinner.setMaximumSize(new Dimension(productDiscountSpinner.getMaximumSize().width, productDiscountSpinner.getMinimumSize().height));

                            JPanel panelW = new JPanel();
                            panelW.setLayout(new BoxLayout(panelW, BoxLayout.Y_AXIS));
                            panelW.setBorder(new EmptyBorder(4, 4, 4, 4));
                            panelW.add(productDiscountSpinner);
                            panelW.setBackground(Color.white);

                            JPanel panel = new JPanel();
                            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                            panel.setMinimumSize(new Dimension(panel.getMinimumSize().width, 32));

                            productDiscountSpinner.setAlignmentX(Component.RIGHT_ALIGNMENT);
                            panel.setPreferredSize(new Dimension(0, 32));
                            panel.add(panelW);
                            panel.setBorder(border);
                            panel.setBackground(Color.white);
                            productsTable.add(panel, c);
                        }

                        {
                            c.gridx++;
                            //supportDiscount = new DiscountJSpinner("Current maximum support discount is ", getRoot(), (int) (prod.getSupportDiscount() * 100), 0, (int) (getProposal().getConfig().getMaxSupportDiscount() * 100), 1, prod, true);
                            supportDiscount.setMaximumSize(new Dimension(supportDiscount.getMaximumSize().width, supportDiscount.getMinimumSize().height));

                            JPanel panelW = new JPanel();
                            panelW.setLayout(new BoxLayout(panelW, BoxLayout.Y_AXIS));
                            panelW.setBorder(new EmptyBorder(4, 4, 4, 4));
                            panelW.add(supportDiscount);
                            panelW.setBackground(Color.white);

                            JPanel panel = new JPanel();
                            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                            panel.setMinimumSize(new Dimension(panel.getMinimumSize().width, 32));

                            supportDiscount.setAlignmentX(Component.RIGHT_ALIGNMENT);
                            panel.setPreferredSize(new Dimension(0, 32));
                            panel.add(panelW);
                            panel.setBorder(border);
                            panel.setBackground(Color.white);
                            productsTable.add(panel, c);
                        }

                        {
                            c.gridx++;
                            //supportDiscount = new DiscountJSpinner("Current maximum support discount is ", getRoot(), (int) (prod.getSupportDiscount() * 100), 0, (int) (getProposal().getConfig().getMaxSupportDiscount() * 100), 1, prod, true);
                            //supportDiscount.addChangeListener(supportDiscountCL);
                            supportDiscountSpinner.setMaximumSize(new Dimension(supportDiscountSpinner.getMaximumSize().width, supportDiscountSpinner.getMinimumSize().height));

                            JPanel panelW = new JPanel();
                            panelW.setLayout(new BoxLayout(panelW, BoxLayout.Y_AXIS));
                            panelW.setBorder(new EmptyBorder(4, 4, 4, 4));
                            panelW.add(supportDiscountSpinner);
                            panelW.setBackground(Color.white);

                            JPanel panel = new JPanel();
                            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                            panel.setMinimumSize(new Dimension(panel.getMinimumSize().width, 32));

                            supportDiscountSpinner.setAlignmentX(Component.RIGHT_ALIGNMENT);
                            panel.setPreferredSize(new Dimension(0, 32));
                            panel.add(panelW);
                            panel.setBorder(lborder);
                            panel.setBackground(Color.white);
                            productsTable.add(panel, c);
                        }
                        //eulabel.call();
                        //splabel.call();
                        //muplabel.call();
                    }
                }

                if (!getProposal().getConfig().isSalesSupport()) {
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
                    /*if (getProposal().getRegion().getRate() != 1d)*/
                    {
                        c.gridx++;
                        JLabel label = new JLabel((getProposal().getCurrency().getSymbol() != null ?
                                getProposal().getCurrency().getSymbol() + " " : "") + df.format(getProposal().getRegionalPrice()) + (getProposal().getCurrency().getSymbol() == null ?
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
                        glmuplabel.setBorder(new EmptyBorder(4, 4, 4, 4));
                        glmuplabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(glmuplabel);
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
                    glmuplabel.call();
                }
            }
        }
    }

    private void makePSList() {
        if (psTable != null) {
            final SummaryForm that = this;
            psTable.removeAll();
            if (getProposal().getPSQuote().enabled()) {
                GridBagConstraints c = new GridBagConstraints();
                c.gridy = 0;
                c.weightx = 1;
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.PAGE_START;
                Border border = BorderFactory.createMatteBorder(1, 1, 0, 0, Color.black);
                Border lborder = BorderFactory.createMatteBorder(1, 1, 0, 1, Color.black);
                {
                    c.gridx = 0;
                    JLabel label = new JLabel("M/D price");
                    JPanel panel = new JPanel();
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                    psTable.add(panel, c);
                }
                {
                    c.gridx++;
                    JLabel label = new JLabel("M/D rate (" + getProposal().getCurrency().getName() + ")");
                    JPanel panel = new JPanel();
                    panel.add(label);
                    panel.setBorder(border);
                    if (getProposal().getConfig().isSalesSupport()) {
                        panel.setBorder(lborder);
                    }
                    panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                    psTable.add(panel, c);
                }
                {
                    c.gridx++;
                    JLabel label = new JLabel("M/D discount (%)");
                    JPanel panel = new JPanel();
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                    psTable.add(panel, c);
                }
                {
                    c.gridx++;
                    JLabel label = new JLabel("PS discount (%)");
                    JPanel panel = new JPanel();
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                    psTable.add(panel, c);
                }
                {
                    c.gridx++;
                    JLabel label = new JLabel("M/D total price");
                    JPanel panel = new JPanel();
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                    psTable.add(panel, c);
                }
                {
                    c.gridx++;
                    JLabel label = new JLabel("TC total price");
                    JPanel panel = new JPanel();
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                    psTable.add(panel, c);
                }
                /*{
                    c.gridx++;
                    JLabel label = new JLabel("Onsite budget");
                    JPanel panel = new JPanel();
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                    psTable.add(panel, c);
                }*/
                {
                    c.gridx++;
                    JLabel label = new JLabel("Total");
                    JPanel panel = new JPanel();
                    panel.add(label);
                    panel.setBorder(lborder);
                    panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                    psTable.add(panel, c);
                }

                final java.util.List<CustomJLabel> lbls = new ArrayList<CustomJLabel>();

                border = BorderFactory.createMatteBorder(1, 1, 1, 0, Color.black);
                lborder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black);

                c.gridy++;
                {
                    c.gridx = 0;
                    CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                        public void act(Object src) {
                            ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?
                                    getProposal().getCurrency().getSymbol() + " " : "") + df.format(getProposal().getPSQuote().getMDPrice()) + (getProposal().getCurrency().getSymbol() == null ?
                                    " " + getProposal().getCurrency().getName() : ""));
                        }

                        public void setData(String key, Object data) {
                            //To change body of implemented methods use File | Settings | File Templates.
                        }

                        public Object getData(String key) {
                            return null;  //To change body of implemented methods use File | Settings | File Templates.
                        }
                    });
                    label.call();
                    lbls.add(label);

                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    psTable.add(panel, c);
                }
                final JSpinner mdRateF = new JSpinner(new SpinnerNumberModel(getProposal().getPSQuote().getMDRate() * getProposal().getCurrencyRate(), 0d, getProposal().getRegion().getMDRate() * getProposal().getCurrencyRate(), 1d));
                final JSpinner mdDiscountF = new JSpinner(new SpinnerNumberModel(getProposal().getPSQuote().getMDDiscount() * 100, 0d, 100d, 1d));

                final ChangeListener mdDiscountFCL = new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        final ChangeEvent ev = e;
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                if (ev.getSource() == mdDiscountF) {
                                    getProposal().getPSQuote().setMDDiscount((Double) mdDiscountF.getValue() / 100d);
                                    mdRateF.setValue(getProposal().getPSQuote().getMDRate()*getProposal().getCurrencyRate());
                                    /*for(CustomJLabel l:lbls){
                                        l.call();
                                    }
                                    psFormUpdater.act(null);*/
                                    updated.act(that);
                                }
                            }
                        });
                    }
                };
                ChangeListener mdRateFCL = new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        final ChangeEvent ev = e;
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                if (ev.getSource() == mdRateF) {
                                    mdDiscountF.removeChangeListener(mdDiscountFCL);
                                    Double mu = 100 * (1 - (Double) mdRateF.getValue() / (getProposal().getRegion().getMDRate()*getProposal().getCurrencyRate()));
                                    mdDiscountF.setValue(mu);
                                    mdDiscountF.addChangeListener(mdDiscountFCL);
                                    getProposal().getPSQuote().setMDDiscount((Double) mdDiscountF.getValue() / 100d);

                                    for(CustomJLabel l:lbls){
                                        l.call();
                                    }
                                    //psFormUpdater.act(null);
                                    //titleUpdater.act(proposal);
                                    updated.act(that);
                                }
                            }
                        });
                    }
                };
                {
                    c.gridx++;

                    mdRateF.setMaximumSize(new Dimension(mdRateF.getMaximumSize().width, mdRateF.getMinimumSize().height));
                    mdRateF.addChangeListener(mdRateFCL);

                    JPanel panelW = new JPanel();
                    panelW.setLayout(new BoxLayout(panelW, BoxLayout.Y_AXIS));
                    panelW.setBorder(new EmptyBorder(4, 4, 4, 4));
                    panelW.add(mdRateF);
                    panelW.setBackground(Color.white);

                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setMinimumSize(new Dimension(panel.getMinimumSize().width, 32));

                    mdRateF.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(panelW);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    psTable.add(panel, c);
                }
                {
                    c.gridx++;

                    mdDiscountF.setMaximumSize(new Dimension(mdDiscountF.getMaximumSize().width, mdDiscountF.getMinimumSize().height));
                    mdDiscountF.addChangeListener(mdDiscountFCL);

                    JPanel panelW = new JPanel();
                    panelW.setLayout(new BoxLayout(panelW, BoxLayout.Y_AXIS));
                    panelW.setBorder(new EmptyBorder(4, 4, 4, 4));
                    panelW.add(mdDiscountF);
                    panelW.setBackground(Color.white);

                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setMinimumSize(new Dimension(panel.getMinimumSize().width, 32));

                    mdDiscountF.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(panelW);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    psTable.add(panel, c);
                }
                {
                    c.gridx++;
                    final JSpinner productDiscount = new JSpinner(new SpinnerNumberModel(getProposal().getPSQuote().getPSDiscount() * 100, 0d, 100d, 1d));
                    productDiscount.setMaximumSize(new Dimension(productDiscount.getMaximumSize().width, productDiscount.getMinimumSize().height));
                    productDiscount.addChangeListener(new ChangeListener() {
                        public void stateChanged(ChangeEvent e) {
                            final ChangeEvent ev = e;
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    if (ev.getSource() == productDiscount) {
                                        getProposal().getPSQuote().setPSDiscount((Double) productDiscount.getValue() / 100d);
                                        for (CustomJLabel l : lbls) {
                                            l.call();
                                        }
                                        titleUpdater.act(proposal);
                                        updated.act(that);
                                    }
                                }
                            });
                        }
                    });

                    JPanel panelW = new JPanel();
                    panelW.setLayout(new BoxLayout(panelW, BoxLayout.Y_AXIS));
                    panelW.setBorder(new EmptyBorder(4, 4, 4, 4));
                    panelW.add(productDiscount);
                    panelW.setBackground(Color.white);

                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setMinimumSize(new Dimension(panel.getMinimumSize().width, 32));

                    productDiscount.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(panelW);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    psTable.add(panel, c);
                }
                {
                    c.gridx++;
                    CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                        public void act(Object src) {
                            ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?
                                    getProposal().getCurrency().getSymbol() + " " : "") + df.format(getProposal().getPSQuote().getMDTotalPrice()) + (getProposal().getCurrency().getSymbol() == null ?
                                    " " + getProposal().getCurrency().getName() : ""));
                        }

                        public void setData(String key, Object data) {
                            //To change body of implemented methods use File | Settings | File Templates.
                        }

                        public Object getData(String key) {
                            return null;  //To change body of implemented methods use File | Settings | File Templates.
                        }
                    });
                    label.call();
                    lbls.add(label);

                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    psTable.add(panel, c);
                }
                {
                    c.gridx++;
                    CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                        public void act(Object src) {
                            ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?
                                    getProposal().getCurrency().getSymbol() + " " : "") + df.format(getProposal().getPSQuote().getTrainingCoursesTotalPrice()) + (getProposal().getCurrency().getSymbol() == null ?
                                    " " + getProposal().getCurrency().getName() : ""));
                        }

                        public void setData(String key, Object data) {
                            //To change body of implemented methods use File | Settings | File Templates.
                        }

                        public Object getData(String key) {
                            return null;  //To change body of implemented methods use File | Settings | File Templates.
                        }
                    });
                    label.call();
                    lbls.add(label);

                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    psTable.add(panel, c);
                }
                /*{
                    c.gridx++;
                    CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                        public void act(Object src) {
                            ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?
                                    getProposal().getCurrency().getSymbol() + " " : "") + df.format(getProposal().getPSQuote().getOnsitePrice()) + (getProposal().getCurrency().getSymbol() == null ?
                                    " " + getProposal().getCurrency().getName() : ""));
                        }

                        public void setData(String key, Object data) {
                            //To change body of implemented methods use File | Settings | File Templates.
                        }

                        public Object getData(String key) {
                            return null;  //To change body of implemented methods use File | Settings | File Templates.
                        }
                    });
                    label.call();
                    lbls.add(label);

                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    psTable.add(panel, c);
                }*/
                {
                    c.gridx++;
                    CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                        public void act(Object src) {
                            ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?
                                    getProposal().getCurrency().getSymbol() + " " : "") + df.format(getProposal().getPSQuote().getGrandTotal()) + (getProposal().getCurrency().getSymbol() == null ?
                                    " " + getProposal().getCurrency().getName() : ""));
                        }

                        public void setData(String key, Object data) {
                            //To change body of implemented methods use File | Settings | File Templates.
                        }

                        public Object getData(String key) {
                            return null;  //To change body of implemented methods use File | Settings | File Templates.
                        }
                    });
                    label.call();
                    lbls.add(label);

                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(lborder);
                    panel.setBackground(Color.white);
                    psTable.add(panel, c);
                }
            }
        }
    }

    private void makeOracleList() {
        if(getProposal().getConfig().isSalesSupport()){
            return;
        }
        if (oracleTable != null) {
            final java.util.List<CustomJLabel> glbls = new ArrayList<CustomJLabel>();
            final SummaryForm that = this;
            oracleTable.removeAll();
            if (getProposal().getOracleQuote().enabled()) {
                GridBagConstraints c = new GridBagConstraints();
                c.gridy = 0;
                c.weightx = 1;
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.PAGE_START;
                Border border = BorderFactory.createMatteBorder(1, 1, 0, 0, Color.black);
                Border lborder = BorderFactory.createMatteBorder(1, 1, 0, 1, Color.black);
                for(OracleLicense lic:getProposal().getOracleQuote().getOracleLicenses().values()){

                    final java.util.List<CustomJLabel> lbls = new ArrayList<CustomJLabel>();
                    final OracleLicense _lic = lic;
                    {
                        GridBagConstraints cc = new GridBagConstraints();
                        cc.weightx = 1.0;
                        cc.gridwidth = 1;
                        cc.gridx = 0;
                        cc.gridy = 0;
                        cc.fill = GridBagConstraints.HORIZONTAL;

                        c.gridwidth = 10;
                        c.gridx = 0;



                        CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                            public void act(Object src) {
                                String jLabelText = _lic.getProduct().getName() + " Oracle Box" + (!_lic.isMemberOfAnotherBox()?(" (Total licenses: " + (int)_lic.getLicCount()) + ")":"") + (_lic.isMemberOfAnotherBox()?(" -> " + _lic.getParentName()):"");

                                CustomJLabel _label = ((CustomJLabel) src);

                                _label.setBorder(new EmptyBorder(4, 4, 4, 4));
                                Font newLabelFont=new Font(_label.getFont().getName(),Font.BOLD,_label.getFont().getSize());

                                if(_lic.isMemberOfAnotherBox()){
                                    jLabelText = "<html><font color=\"gray\">" + jLabelText + "</font></html>";
                                }
                                _label.setText(jLabelText);
                                _label.setFont(newLabelFont);

                            }

                            public void setData(String key, Object data) {
                                //To change body of implemented methods use File | Settings | File Templates.
                            }

                            public Object getData(String key) {
                                return null;  //To change body of implemented methods use File | Settings | File Templates.
                            }
                        });
                        label.call();
                        lbls.add(label);

                        Font newLabelFont=new Font(label.getFont().getName(),Font.BOLD,
                                label.getFont().getSize());

                        Font newLabelFontBtn=new Font(label.getFont().getName(),Font.BOLD,label.getFont().getSize());

                        JPanel panel = new JPanel();
                        //panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        panel.setLayout(new GridBagLayout());
                        panel.add(label, cc);

                        if(_lic.canBeMoved() && !_lic.isMemberOfAnotherBox() && _lic.getChildren().size() == 0){
                            JLabel mlabel = new JLabel("[Move to]");
                            mlabel.setBorder(new EmptyBorder(4, 4, 4, 4));
                            mlabel.setFont(newLabelFontBtn);
                            mlabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                            mlabel.addMouseListener(new MouseListener() {
                                public void mouseClicked(MouseEvent e) {
                                    if(_lic.canBeMoved()){
                                        ArrayList<OracleLicenseStr> licenseArray = new ArrayList<OracleLicenseStr>();
                                        for(OracleLicense ol:getProposal().getOracleQuote().getOracleLicenses().values()){
                                            if(ol.containsAllOptions(_lic)){
                                                licenseArray.add(new OracleLicenseStr(ol));
                                            }
                                        }

                                        final JComboBox licCombo = new JComboBox(licenseArray.toArray());

                                        final JOptionPane licPane = new JOptionPane(
                                            new JComponent[]{
                                                    new JLabel("Move to"), licCombo
                                            },
                                            JOptionPane.QUESTION_MESSAGE,
                                            JOptionPane.OK_CANCEL_OPTION);

                                        final JDialog dialog = new JDialog(getFrame(), "Move to", true);
                                        dialog.setResizable(false);
                                        dialog.addWindowListener(new WindowAdapter() {
                                            public void windowClosing(WindowEvent we) {
                                                dialog.dispose();
                                            }
                                        });
                                        dialog.setContentPane(licPane);
                                        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

                                        licPane.addPropertyChangeListener(
                                            new PropertyChangeListener() {
                                                public void propertyChange(PropertyChangeEvent e) {
                                                    if (licPane.getValue() != null) {
                                                        String prop = e.getPropertyName();
                                                        if (dialog.isVisible()
                                                                && (e.getSource() == licPane)
                                                                && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                                                            try {
                                                                if (licPane.getValue() instanceof Integer) {
                                                                    int value = (Integer) licPane.getValue();
                                                                    if (value == JOptionPane.OK_OPTION) {
                                                                        ((OracleLicenseStr)licCombo.getSelectedItem()).getLicense().addChild(_lic.getProductKey());
                                                                        makeOracleList();
                                                                        updated.act(this);
                                                                        dialog.dispose();
                                                                    } else if (value == JOptionPane.CANCEL_OPTION) {
                                                                        dialog.dispose();
                                                                    }
                                                                }
                                                            } catch (Exception exception) {
                                                                licPane.setValue(null);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        );

                                        dialog.pack();
                                        dialog.setLocationRelativeTo(getRoot());
                                        dialog.setVisible(true);
                                    }
                                }

                                public void mousePressed(MouseEvent e) {
                                }

                                public void mouseReleased(MouseEvent e) {
                                }

                                public void mouseEntered(MouseEvent e) {
                                }

                                public void mouseExited(MouseEvent e) {
                                }
                            });
                            cc.gridx++;
                            cc.weightx = 0d;
                                panel.add(mlabel, cc);
                        }

                        if(_lic.isMemberOfAnotherBox()){
                            JLabel mlabel = new JLabel("[Restore box]");
                            mlabel.setBorder(new EmptyBorder(4, 4, 4, 4));
                            mlabel.setFont(newLabelFontBtn);
                            mlabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                            mlabel.addMouseListener(new MouseListener() {
                                public void mouseClicked(MouseEvent e) {
                                    for(OracleLicense ol:getProposal().getOracleQuote().getOracleLicenses().values()){
                                        ol.delChild(_lic.getProductKey());
                                    }
                                    makeOracleList();
                                    updated.act(this);
                                }

                                public void mousePressed(MouseEvent e) {
                                }

                                public void mouseReleased(MouseEvent e) {
                                }

                                public void mouseEntered(MouseEvent e) {
                                }

                                public void mouseExited(MouseEvent e) {
                                }
                            });
                            cc.gridx++;
                            cc.weightx = 0d;
                            panel.add(mlabel, cc);
                        }

                        panel.setBorder(lborder);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }

                    /*{
                        c.gridwidth = 1;
                        c.gridx++;
                        JLabel label = new JLabel("[Move to]");
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        Font newLabelFont=new Font(label.getFont().getName(),Font.BOLD,label.getFont().getSize());
                        label.setFont(newLabelFont);
                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        panel.add(label);
                        panel.setBorder(lborder);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }*/
                    c.gridwidth = 1;
                    c.weightx = 1;
                    c.gridy++;
                    {
                        c.weightx = 3;
                        c.gridx = 0;
                        JLabel label = new JLabel(" ");
                        JPanel panel = new JPanel();
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                        oracleTable.add(panel, c);
                        c.weightx = 1;
                    }
                    {
                        c.weightx = 2;
                        c.gridx++;
                        JLabel label = new JLabel(" ");
                        JPanel panel = new JPanel();
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                        oracleTable.add(panel, c);
                        c.weightx = 1;
                    }
                    {
                        c.gridx++;
                        JLabel label = new JLabel("License price");
                        JPanel panel = new JPanel();
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        JLabel label = new JLabel("Options price");
                        JPanel panel = new JPanel();
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        JLabel label = new JLabel("Support price");
                        JPanel panel = new JPanel();
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        JLabel label = new JLabel("Total price");
                        JPanel panel = new JPanel();
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        JLabel label = new JLabel("Discount (%)");
                        JPanel panel = new JPanel();
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        JLabel label = new JLabel("CP price");
                        JPanel panel = new JPanel();
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        JLabel label = new JLabel("Customer price");
                        JPanel panel = new JPanel();
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        JLabel label = new JLabel("Margin");
                        JPanel panel = new JPanel();
                        panel.add(label);
                        panel.setBorder(lborder);
                        panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                        oracleTable.add(panel, c);
                    }

                    //border = BorderFactory.createMatteBorder(1, 1, 1, 0, Color.black);
                    //lborder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black);

                    c.gridy++;
                    // row 1
                    {
                        c.gridx = 0;
                        JLabel label = new JLabel("License type");

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        JLabel label = new JLabel(lic.getOracleLicense().getName());

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                            public void act(Object src) {
                                ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?getProposal().getCurrency().getSymbol() + " " : "") +

                                        df.format(_lic.getLicensePrice()) +
                                        //df.format(getProposal().getPSQuote().getMDTotalPrice()) +

                                        (getProposal().getCurrency().getSymbol() == null ? " " + getProposal().getCurrency().getName() : ""));
                            }

                            public void setData(String key, Object data) {
                                //To change body of implemented methods use File | Settings | File Templates.
                            }

                            public Object getData(String key) {
                                return null;  //To change body of implemented methods use File | Settings | File Templates.
                            }
                        });
                        label.call();
                        lbls.add(label);

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                            public void act(Object src) {
                                ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?getProposal().getCurrency().getSymbol() + " " : "") +
                                        df.format(_lic.getOptionsPrice()) +
                                        //df.format(getProposal().getPSQuote().getMDTotalPrice()) +

                                        (getProposal().getCurrency().getSymbol() == null ? " " + getProposal().getCurrency().getName() : ""));
                            }

                            public void setData(String key, Object data) {
                                //To change body of implemented methods use File | Settings | File Templates.
                            }

                            public Object getData(String key) {
                                return null;  //To change body of implemented methods use File | Settings | File Templates.
                            }
                        });
                        label.call();
                        lbls.add(label);

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                            public void act(Object src) {
                                ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?getProposal().getCurrency().getSymbol() + " " : "") +
                                        df.format(_lic.getOracleSupportPrice()) +
                                        //df.format(getProposal().getPSQuote().getMDTotalPrice()) +

                                        (getProposal().getCurrency().getSymbol() == null ? " " + getProposal().getCurrency().getName() : ""));
                            }

                            public void setData(String key, Object data) {
                                //To change body of implemented methods use File | Settings | File Templates.
                            }

                            public Object getData(String key) {
                                return null;  //To change body of implemented methods use File | Settings | File Templates.
                            }
                        });
                        label.call();
                        lbls.add(label);

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                            public void act(Object src) {
                                ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?getProposal().getCurrency().getSymbol() + " " : "") +
                                        df.format(_lic.getOracleTotalPrice()) +
                                        //df.format(getProposal().getPSQuote().getMDTotalPrice()) +

                                        (getProposal().getCurrency().getSymbol() == null ? " " + getProposal().getCurrency().getName() : ""));
                            }

                            public void setData(String key, Object data) {
                                //To change body of implemented methods use File | Settings | File Templates.
                            }

                            public Object getData(String key) {
                                return null;  //To change body of implemented methods use File | Settings | File Templates.
                            }
                        });
                        label.call();
                        lbls.add(label);

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        final JSpinner productDiscount = new JSpinner(new SpinnerNumberModel(_lic.getDiscount().doubleValue() * 100d, 0d, _lic.getCPDiscount(), 1d));
                        productDiscount.setMaximumSize(new Dimension(productDiscount.getMaximumSize().width, productDiscount.getMinimumSize().height));
                        productDiscount.addChangeListener(new ChangeListener() {
                            public void stateChanged(ChangeEvent e) {
                                final ChangeEvent ev = e;
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        if (ev.getSource() == productDiscount) {
                                            _lic.setDiscount((Double) productDiscount.getValue() / 100d);
                                            for (CustomJLabel l : lbls) {
                                                l.call();
                                            }
                                            for (CustomJLabel l : glbls) {
                                                l.call();
                                            }
                                            titleUpdater.act(proposal);
                                            updated.act(that);
                                        }
                                    }
                                });
                            }
                        });

                        if(_lic.isMemberOfAnotherBox()){
                            productDiscount.setEnabled(false);
                        }

                        JPanel panelW = new JPanel();
                        panelW.setLayout(new BoxLayout(panelW, BoxLayout.Y_AXIS));
                        panelW.setBorder(new EmptyBorder(4, 4, 4, 4));
                        panelW.add(productDiscount);
                        panelW.setBackground(Color.white);

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        panel.setMinimumSize(new Dimension(panel.getMinimumSize().width, 32));

                        productDiscount.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(panelW);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                            public void act(Object src) {
                                ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?getProposal().getCurrency().getSymbol() + " " : "") +
                                        df.format(_lic.getOracleCPPrice()) +
                                        //df.format(getProposal().getPSQuote().getMDTotalPrice()) +

                                        (getProposal().getCurrency().getSymbol() == null ? " " + getProposal().getCurrency().getName() : ""));
                            }

                            public void setData(String key, Object data) {
                                //To change body of implemented methods use File | Settings | File Templates.
                            }

                            public Object getData(String key) {
                                return null;  //To change body of implemented methods use File | Settings | File Templates.
                            }
                        });
                        label.call();
                        lbls.add(label);

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                            public void act(Object src) {
                                ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?getProposal().getCurrency().getSymbol() + " " : "") +
                                        df.format(_lic.getOracleCustomerPrice()) +
                                        //df.format(getProposal().getPSQuote().getMDTotalPrice()) +

                                        (getProposal().getCurrency().getSymbol() == null ? " " + getProposal().getCurrency().getName() : ""));
                            }

                            public void setData(String key, Object data) {
                                //To change body of implemented methods use File | Settings | File Templates.
                            }

                            public Object getData(String key) {
                                return null;  //To change body of implemented methods use File | Settings | File Templates.
                            }
                        });
                        label.call();
                        lbls.add(label);

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                            public void act(Object src) {
                                ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?getProposal().getCurrency().getSymbol() + " " : "") +
                                        df.format(_lic.getOracleCPMargin()) +
                                        //df.format(getProposal().getPSQuote().getMDTotalPrice()) +

                                        (getProposal().getCurrency().getSymbol() == null ? " " + getProposal().getCurrency().getName() : ""));
                            }

                            public void setData(String key, Object data) {
                                //To change body of implemented methods use File | Settings | File Templates.
                            }

                            public Object getData(String key) {
                                return null;  //To change body of implemented methods use File | Settings | File Templates.
                            }
                        });
                        label.call();
                        lbls.add(label);

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(lborder);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    c.gridy++;
                    // row 2
                    {
                        c.gridx = 0;
                        JLabel label = new JLabel("Number of cores");

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        final JSpinner cores = new JSpinner(new SpinnerNumberModel(lic.getCores().intValue(), 1, 1000, 1));
                        cores.setMaximumSize(new Dimension(cores.getMaximumSize().width, cores.getMinimumSize().height));
                        cores.addChangeListener(new ChangeListener() {
                            public void stateChanged(ChangeEvent e) {
                                final ChangeEvent ev = e;
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        if (ev.getSource() == cores) {
                                            //getProposal().getPSQuote().setPSDiscount((Double) cores.getValue() / 100d);
                                            _lic.setCores((Integer)cores.getValue());
                                            for (CustomJLabel l : lbls) {
                                                l.call();
                                            }
                                            for (CustomJLabel l : glbls) {
                                                l.call();
                                            }
                                            titleUpdater.act(proposal);
                                            updated.act(that);
                                        }
                                    }
                                });
                            }
                        });

                        if(_lic.isMemberOfAnotherBox()){
                            cores.setEnabled(false);
                        }

                        JPanel panelW = new JPanel();
                        panelW.setLayout(new BoxLayout(panelW, BoxLayout.Y_AXIS));
                        panelW.setBorder(new EmptyBorder(4, 4, 4, 4));
                        panelW.add(cores);
                        panelW.setBackground(Color.white);

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        panel.setMinimumSize(new Dimension(panel.getMinimumSize().width, 32));

                        cores.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(panelW);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    for(int i = 0; i< 8; i++){
                        {
                            c.gridx++;
                            JLabel label = new JLabel(" ");

                            JPanel panel = new JPanel();
                            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                            label.setBorder(new EmptyBorder(4, 4, 4, 4));
                            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                            panel.setPreferredSize(new Dimension(0, 32));
                            panel.add(label);
                            if(i==7){
                                panel.setBorder(lborder);
                            }else{
                                panel.setBorder(border);
                            }
                            panel.setBackground(Color.white);
                            oracleTable.add(panel, c);
                        }
                    }
                    c.gridy++;
                    // row 3
                    {
                        c.gridx = 0;
                        JLabel label = new JLabel("Model");

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        JLabel label = new JLabel(lic.getLicensingModel());

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    for(int i = 0; i< 8; i++){
                        {
                            c.gridx++;
                            JLabel label = new JLabel(" ");

                            JPanel panel = new JPanel();
                            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                            label.setBorder(new EmptyBorder(4, 4, 4, 4));
                            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                            panel.setPreferredSize(new Dimension(0, 32));
                            panel.add(label);
                            if(i==7){
                                panel.setBorder(lborder);
                            }else{
                                panel.setBorder(border);
                            }
                            panel.setBackground(Color.white);
                            oracleTable.add(panel, c);
                        }
                    }
                    c.gridy++;
                    // row 4
                    {
                        c.gridx = 0;
                        JLabel label = new JLabel("Coefficient");

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    /*{
                        c.gridx++;
                        JLabel label = new JLabel(lic.getCoefficient().getName());


                        JComboBox combo = new JComboBox();
                        combo.addActionListener(new ActionListener() {
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

                        combo.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        combo.setMaximumSize(new Dimension(combo.getMaximumSize().width, 23));
                        combo.setMinimumSize(new Dimension(combo.getMinimumSize().width, 23));

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }    */
                    {
                        c.gridx++;
                        JComboBox combo = new JComboBox(_lic.getOracleLicense().getCoefficients().toArray());
                        combo.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                final ActionEvent ev = e;
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        JComboBox src = (JComboBox) ev.getSource();
                                        //getProposal().setSupportPlan((SupportPlan) src.getSelectedItem());
                                        _lic.setCoefficient((Coefficient) src.getSelectedItem());
                                        for (CustomJLabel l : lbls) {
                                            l.call();
                                        }
                                        for (CustomJLabel l : glbls) {
                                            l.call();
                                        }
                                        titleUpdater.act(proposal);
                                        updated.act(that);
                                    }
                                });
                            }
                        });
                        combo.setMaximumSize(new Dimension(combo.getMaximumSize().width, combo.getMinimumSize().height));
                        //combo.setMaximumSize(new Dimension(combo.getMaximumSize().width, 23));
                        //combo.setMinimumSize(new Dimension(combo.getMinimumSize().width, 23));

                        combo.setSelectedItem(_lic.getCoefficient());

                        if(_lic.isMemberOfAnotherBox()){
                            combo.setEnabled(false);
                        }

                        JPanel panelW = new JPanel();
                        panelW.setLayout(new BoxLayout(panelW, BoxLayout.Y_AXIS));
                        panelW.setBorder(new EmptyBorder(4, 4, 4, 4));
                        panelW.add(combo);
                        panelW.setBackground(Color.white);

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        panel.setMinimumSize(new Dimension(panel.getMinimumSize().width, 32));

                        combo.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(panelW);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    for(int i = 0; i< 8; i++){
                        {
                            c.gridx++;
                            JLabel label = new JLabel(" ");

                            JPanel panel = new JPanel();
                            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                            label.setBorder(new EmptyBorder(4, 4, 4, 4));
                            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                            panel.setPreferredSize(new Dimension(0, 32));
                            panel.add(label);
                            if(i==7){
                                panel.setBorder(lborder);
                            }else{
                                panel.setBorder(border);
                            }
                            panel.setBackground(Color.white);
                            oracleTable.add(panel, c);
                        }
                    }
                    c.gridy++;
                    // row 5
                    {
                        c.gridx = 0;
                        JLabel label = new JLabel("Included options");

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        String options = "";
                        for(String s:lic.getProduct().getOracleOptions()){
                            OracleOption op = getProposal().getConfig().getOracleOptions().get(s);
                            options += ", " + op.getShortName();
                        }
                        JLabel label = new JLabel(options.length()>0?options.substring(1):"");

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    for(int i = 0; i< 8; i++){
                        {
                            c.gridx++;
                            JLabel label = new JLabel(" ");

                            JPanel panel = new JPanel();
                            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                            label.setBorder(new EmptyBorder(4, 4, 4, 4));
                            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                            panel.setPreferredSize(new Dimension(0, 32));
                            panel.add(label);
                            if(i==7){
                                panel.setBorder(lborder);
                            }else{
                                panel.setBorder(border);
                            }
                            panel.setBackground(Color.white);
                            oracleTable.add(panel, c);
                        }
                    }
                    c.gridy++;
                    // row 6
                    /*{
                        c.gridx = 0;
                        JLabel label = new JLabel("Support rate");

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        JLabel label = new JLabel(df.format(100d * lic.getOracleLicense().getSupportRate()) + "%");

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    for(int i = 0; i< 8; i++){
                        {
                            c.gridx++;
                            JLabel label = new JLabel(" ");

                            JPanel panel = new JPanel();
                            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                            label.setBorder(new EmptyBorder(4, 4, 4, 4));
                            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                            panel.setPreferredSize(new Dimension(0, 32));
                            panel.add(label);
                            if(i==7){
                                panel.setBorder(lborder);
                            }else{
                                panel.setBorder(border);
                            }
                            panel.setBackground(Color.white);
                            oracleTable.add(panel, c);
                        }
                    }
                    c.gridy++;*/
                    // row 7
                    {
                        c.gridx = 0;
                        JLabel label = new JLabel("Maximum discount");

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        JLabel label = new JLabel(df.format(lic.getCPDiscount()) + "%");

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    for(int i = 0; i< 8; i++){
                        {
                            c.gridx++;
                            JLabel label = new JLabel(" ");

                            JPanel panel = new JPanel();
                            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                            label.setBorder(new EmptyBorder(4, 4, 4, 4));
                            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                            panel.setPreferredSize(new Dimension(0, 32));
                            panel.add(label);
                            if(i==7){
                                panel.setBorder(lborder);
                            }else{
                                panel.setBorder(border);
                            }
                            panel.setBackground(Color.white);
                            oracleTable.add(panel, c);
                        }
                    }
                    c.gridy++;
                    // row 7
                    {
                        c.gridx = 0;
                        JLabel label = new JLabel("Shared");

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        c.gridwidth = 9;
                        String sharedString = "Yes, with";
                        for(String s:lic.getChildren()){
                            sharedString += " " + s + "," ;
                        }
                        JLabel label = new JLabel(lic.isShared()?sharedString.substring(0, sharedString.length() - 1):"No");

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.LEFT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(lborder);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                        c.gridwidth = 1;
                    }
                    /*for(int i = 0; i< 8; i++){
                        {
                            c.gridx++;
                            JLabel label = new JLabel(" ");

                            JPanel panel = new JPanel();
                            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                            label.setBorder(new EmptyBorder(4, 4, 4, 4));
                            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                            panel.setPreferredSize(new Dimension(0, 32));
                            panel.add(label);
                            if(i==7){
                                panel.setBorder(lborder);
                            }else{
                                panel.setBorder(border);
                            }
                            panel.setBackground(Color.white);
                            oracleTable.add(panel, c);
                        }
                    } */
                    c.gridy++;
                }
                if(getProposal()!=null && getProposal().getOracleQuote()!=null && getProposal().getOracleQuote().getOracleLicenses().size() > 0){
                    {
                        c.gridx = 0;
                        c.gridwidth = 10;
                        GridBagConstraints cc = new GridBagConstraints();
                        cc.weightx = 1.0;
                        cc.gridwidth = 1;
                        cc.gridx = 0;
                        cc.gridy = 0;
                        cc.fill = GridBagConstraints.HORIZONTAL;

                        JLabel label = new JLabel(" ");
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        Font newLabelFont=new Font(label.getFont().getName(),Font.BOLD,label.getFont().getSize());

                        label.setFont(newLabelFont);

                        JPanel panel = new JPanel();
                        //panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        panel.setLayout(new GridBagLayout());
                        panel.add(label, cc);


                        panel.setBorder(lborder);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                        c.gridwidth = 1;
                    }
                    c.gridy++;

                    border = BorderFactory.createMatteBorder(1, 1, 1, 0, Color.black);
                    lborder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black);
                    {
                        c.gridx = 0;
                        JLabel label = new JLabel("Total");

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
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
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                            public void act(Object src) {
                                ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?getProposal().getCurrency().getSymbol() + " " : "") +
                                        df.format(getProposal().getOracleQuote().getLicenseTotal()) +
                                        //df.format(getProposal().getPSQuote().getMDTotalPrice()) +

                                        (getProposal().getCurrency().getSymbol() == null ? " " + getProposal().getCurrency().getName() : ""));
                            }

                            public void setData(String key, Object data) {
                                //To change body of implemented methods use File | Settings | File Templates.
                            }

                            public Object getData(String key) {
                                return null;  //To change body of implemented methods use File | Settings | File Templates.
                            }
                        });
                        label.call();
                        glbls.add(label);

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                            public void act(Object src) {
                                ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?getProposal().getCurrency().getSymbol() + " " : "") +
                                        df.format(getProposal().getOracleQuote().getOptionsTotal()) +
                                        //df.format(getProposal().getPSQuote().getMDTotalPrice()) +

                                        (getProposal().getCurrency().getSymbol() == null ? " " + getProposal().getCurrency().getName() : ""));
                            }

                            public void setData(String key, Object data) {
                                //To change body of implemented methods use File | Settings | File Templates.
                            }

                            public Object getData(String key) {
                                return null;  //To change body of implemented methods use File | Settings | File Templates.
                            }
                        });
                        label.call();
                        glbls.add(label);

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                            public void act(Object src) {
                                ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?getProposal().getCurrency().getSymbol() + " " : "") +
                                        df.format(getProposal().getOracleQuote().getSupportTotal()) +
                                        //df.format(getProposal().getPSQuote().getMDTotalPrice()) +

                                        (getProposal().getCurrency().getSymbol() == null ? " " + getProposal().getCurrency().getName() : ""));
                            }

                            public void setData(String key, Object data) {
                                //To change body of implemented methods use File | Settings | File Templates.
                            }

                            public Object getData(String key) {
                                return null;  //To change body of implemented methods use File | Settings | File Templates.
                            }
                        });
                        label.call();
                        glbls.add(label);

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                            public void act(Object src) {
                                ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?getProposal().getCurrency().getSymbol() + " " : "") +
                                        df.format(getProposal().getOracleQuote().getTotalTotal()) +
                                        //df.format(getProposal().getPSQuote().getMDTotalPrice()) +

                                        (getProposal().getCurrency().getSymbol() == null ? " " + getProposal().getCurrency().getName() : ""));
                            }

                            public void setData(String key, Object data) {
                                //To change body of implemented methods use File | Settings | File Templates.
                            }

                            public Object getData(String key) {
                                return null;  //To change body of implemented methods use File | Settings | File Templates.
                            }
                        });
                        label.call();
                        glbls.add(label);

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
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
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                            public void act(Object src) {
                                ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?getProposal().getCurrency().getSymbol() + " " : "") +
                                        df.format(getProposal().getOracleQuote().getCPTotal()) +
                                        //df.format(getProposal().getPSQuote().getMDTotalPrice()) +

                                        (getProposal().getCurrency().getSymbol() == null ? " " + getProposal().getCurrency().getName() : ""));
                            }

                            public void setData(String key, Object data) {
                                //To change body of implemented methods use File | Settings | File Templates.
                            }

                            public Object getData(String key) {
                                return null;  //To change body of implemented methods use File | Settings | File Templates.
                            }
                        });
                        label.call();
                        glbls.add(label);

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                            public void act(Object src) {
                                ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?getProposal().getCurrency().getSymbol() + " " : "") +
                                        df.format(getProposal().getOracleQuote().getCustomerTotal()) +
                                        //df.format(getProposal().getPSQuote().getMDTotalPrice()) +

                                        (getProposal().getCurrency().getSymbol() == null ? " " + getProposal().getCurrency().getName() : ""));
                            }

                            public void setData(String key, Object data) {
                                //To change body of implemented methods use File | Settings | File Templates.
                            }

                            public Object getData(String key) {
                                return null;  //To change body of implemented methods use File | Settings | File Templates.
                            }
                        });
                        label.call();
                        glbls.add(label);

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                    {
                        c.gridx++;
                        CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                            public void act(Object src) {
                                ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?getProposal().getCurrency().getSymbol() + " " : "") +
                                        df.format(getProposal().getOracleQuote().getTotalMargin()) +
                                        //df.format(getProposal().getPSQuote().getMDTotalPrice()) +

                                        (getProposal().getCurrency().getSymbol() == null ? " " + getProposal().getCurrency().getName() : ""));
                            }

                            public void setData(String key, Object data) {
                                //To change body of implemented methods use File | Settings | File Templates.
                            }

                            public Object getData(String key) {
                                return null;  //To change body of implemented methods use File | Settings | File Templates.
                            }
                        });
                        label.call();
                        glbls.add(label);

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(lborder);
                        panel.setBackground(Color.white);
                        oracleTable.add(panel, c);
                    }
                }
            }
        }
    }

    private JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }
}
