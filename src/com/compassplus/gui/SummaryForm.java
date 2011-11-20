package com.compassplus.gui;

import com.compassplus.configurationModel.Currency;
import com.compassplus.configurationModel.Region;
import com.compassplus.configurationModel.SupportPlan;
import com.compassplus.proposalModel.Product;
import com.compassplus.proposalModel.Proposal;

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
    private JPanel settingsPanel;
    private JComboBox currencyField;
    private JLabel rateLabel;
    private JSpinner rateField;
    private JSpinner planRateField;
    private JComboBox supportPlanField;
    private PCTChangedListener currChanged;
    private JPanel productsTable;
    private DecimalFormat df;

    public SummaryForm(Proposal proposal, PCTChangedListener currChanged, DecimalFormat df) {
        this.currChanged = currChanged;
        this.df = df;
        this.proposal = proposal;
        mainPanel = new SummaryJPanel(this);
        settingsPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(settingsPanel);
        mainPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
        mainPanel.add(scroll, BorderLayout.CENTER);
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
        initForm(proposal);
    }

    private void reloadPrices() {
        currChanged.act(this);
    }

    private void initForm(Proposal proposal) {
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
                }
            });

            clientNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
            clientNameField.setMaximumSize(new Dimension(clientNameField.getMaximumSize().width, clientNameField.getMinimumSize().height));

            JPanel tmpPanel = new JPanel();
            tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.Y_AXIS));
            tmpPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel.add(clientNameLabel);
            tmpPanel.add(clientNameField);
            settingsPanel.add(tmpPanel);
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
                }
            });
            projectNameField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JTextField src = (JTextField) e.getSource();
                    getProposal().setProjectName(src.getText());
                }
            });
            projectNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
            projectNameField.setMaximumSize(new Dimension(projectNameField.getMaximumSize().width, projectNameField.getMinimumSize().height));

            JPanel tmpPanel = new JPanel();
            tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.Y_AXIS));
            tmpPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel.add(projectNameLabel);
            tmpPanel.add(projectNameField);
            settingsPanel.add(tmpPanel);
        }
        {
            JLabel regionLabel = new JLabel("Region");
            regionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JComboBox regionField = new JComboBox(getProposal().getConfig().getRegions().values().toArray());
            regionField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JComboBox src = (JComboBox) e.getSource();
                    getProposal().setRegion((Region) src.getSelectedItem());
                    initCurrency(null);
                    initPlanRate();
                }
            });

            regionField.setSelectedItem(getProposal().getRegion() != null ? getProposal().getRegion() : getProposal().getConfig().getRegions().values().toArray()[0]);

            regionField.setAlignmentX(Component.LEFT_ALIGNMENT);
            regionField.setMaximumSize(new Dimension(regionField.getMaximumSize().width, 23));
            JPanel tmpPanel = new JPanel();
            tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.Y_AXIS));
            tmpPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel.add(regionLabel);
            tmpPanel.add(regionField);
            settingsPanel.add(tmpPanel);
        }
        {
            JLabel dateLabel = new JLabel("Creation date");
            dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextField dateField = new JTextField(getProposal().getDate());
            dateField.setEnabled(false);
            dateField.setAlignmentX(Component.LEFT_ALIGNMENT);
            dateField.setMaximumSize(new Dimension(dateField.getMaximumSize().width, dateField.getMinimumSize().height));

            JPanel tmpPanel = new JPanel();
            tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.Y_AXIS));
            tmpPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel.add(dateLabel);
            tmpPanel.add(dateField);
            settingsPanel.add(tmpPanel);
        }
        {
            JLabel userLabel = new JLabel("Proposal created by");
            userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextField userField = new JTextField(getProposal().getUserName());
            userField.setEnabled(false);
            userField.setAlignmentX(Component.LEFT_ALIGNMENT);
            userField.setMaximumSize(new Dimension(userField.getMaximumSize().width, userField.getMinimumSize().height));

            JPanel tmpPanel = new JPanel();
            tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.Y_AXIS));
            tmpPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel.add(userLabel);
            tmpPanel.add(userField);
            settingsPanel.add(tmpPanel);
        }
        {
            rateLabel = new JLabel();
            rateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            rateField = new JSpinner(new SpinnerNumberModel(0, 0, 10000000d, 0.001d));
            rateField.setMaximumSize(new Dimension(rateField.getMaximumSize().width, rateField.getMinimumSize().height));
            rateField.setAlignmentX(Component.LEFT_ALIGNMENT);

            rateField.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    final ChangeEvent ev = e;
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if (ev.getSource() == rateField) {
                                getProposal().getCurrency().setRate((Double) rateField.getValue());
                                getProposal().setCurrencyRate((Double) rateField.getValue());
                                reloadPrices();
                            }
                        }
                    });
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
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            curChanged();
                        }
                    });
                }
            });

            currencyField.setAlignmentX(Component.LEFT_ALIGNMENT);
            currencyField.setMaximumSize(new Dimension(currencyField.getMaximumSize().width, 23));
            JPanel tmpPanel2 = new JPanel();
            tmpPanel2.setLayout(new BoxLayout(tmpPanel2, BoxLayout.Y_AXIS));
            tmpPanel2.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel2.add(currencyLabel);
            tmpPanel2.add(currencyField);
            settingsPanel.add(tmpPanel2);
            settingsPanel.add(tmpPanel);
        }
        {
            JLabel planRateLabel = new JLabel("Support plan rate");
            planRateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            planRateField = new JSpinner(new SpinnerNumberModel(0d, 0d, 0d, 0d));
            planRateField.setEnabled(false);
            planRateField.setAlignmentX(Component.LEFT_ALIGNMENT);
            planRateField.setMaximumSize(new Dimension(planRateField.getMaximumSize().width, planRateField.getMinimumSize().height));

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
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            JComboBox src = (JComboBox) ev.getSource();
                            getProposal().setSupportPlan((SupportPlan) src.getSelectedItem());
                            initPlanRate();
                        }
                    });
                }
            });

            supportPlanField.setAlignmentX(Component.LEFT_ALIGNMENT);
            supportPlanField.setMaximumSize(new Dimension(supportPlanField.getMaximumSize().width, 23));
            JPanel tmpPanel2 = new JPanel();
            tmpPanel2.setLayout(new BoxLayout(tmpPanel2, BoxLayout.Y_AXIS));
            tmpPanel2.setBorder(new EmptyBorder(4, 4, 4, 4));
            tmpPanel2.add(supportPlanLabel);
            tmpPanel2.add(supportPlanField);
            settingsPanel.add(tmpPanel2);
            settingsPanel.add(tmpPanel);
        }
        {
            JLabel maxDiscountLabel = new JLabel("Maximum discount");
            maxDiscountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JSpinner maxDiscountField = new JSpinner(new SpinnerNumberModel(0d,0d,0d,0d));
            maxDiscountField.setValue(getProposal().getConfig().getMaxDiscount());
            maxDiscountField.setEnabled(false);
            maxDiscountField.setAlignmentX(Component.LEFT_ALIGNMENT);
            maxDiscountField.setMaximumSize(new Dimension(planRateField.getMaximumSize().width, planRateField.getMinimumSize().height));

            JPanel tmpPanel = new JPanel();
            tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.Y_AXIS));
            tmpPanel.setBorder(new EmptyBorder(4, 4, 20, 4));
            tmpPanel.add(maxDiscountLabel);
            tmpPanel.add(maxDiscountField);
            settingsPanel.add(tmpPanel);
        }
        initCurrency(getProposal().getCurrency());
        productsTable = new JPanel(new GridBagLayout());
        productsTable.setMinimumSize(new Dimension(0, 0));
        settingsPanel.add(productsTable);
    }

    private void curChanged() {
        JComboBox src = currencyField;
        getProposal().setCurrency((Currency) src.getSelectedItem());
        rateField.setValue(((Currency) src.getSelectedItem()).getRate());
        rateLabel.setText("Currency rate (USD/" + ((Currency) src.getSelectedItem()).getName() + ")");
        reloadPrices();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initSupportPlans(getProposal().getSupportPlan());
            }
        });
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
                if (sp.getMinPrice() != null && getProposal().isPrimarySale()) {
                    if (getProposal().getPrice() >= sp.getMinPrice() * getProposal().getCurrencyRate()) {
                        supportPlanField.addItem(sp);
                    }
                } else {
                    supportPlanField.addItem(sp);
                }
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
        if (supportPlanField != null) {
            SupportPlan plan = (SupportPlan) supportPlanField.getSelectedItem();
            Double rate = plan.getRate();
            if (plan.getRegionSettings().containsKey(proposal.getRegion().getKey())) {
                rate = plan.getRegionSettings().get(proposal.getRegion().getKey());
            }
            planRateField.setValue(rate);
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
    }

    private void makeProductList() {
        if (productsTable != null) {
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
                    {
                        c.gridx++;
                        JLabel label = new JLabel("Regional list price");
                        JPanel panel = new JPanel();
                        panel.add(label);
                        panel.setBorder(border);
                        panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                        productsTable.add(panel, c);
                    }
                }
                {
                    c.gridx++;
                    JLabel label = new JLabel("Discount");
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
                            price += p.getPrice() * getProposal().getRegion().getRate() - p.getPrice() * getProposal().getRegion().getRate() * p.getDiscount();


                        }
                        ((CustomJLabel) src).setText((getProposal().getCurrency().getSymbol() != null ?
                                getProposal().getCurrency().getSymbol() + " " : "") + df.format(price) + (getProposal().getCurrency().getSymbol() == null ?
                                " " + getProposal().getCurrency().getName() : ""));
                    }
                });
                for (Product p : getProposal().getProducts().values()) {
                    c.gridy++;
                    {
                        c.gridx = 0;
                        JLabel label = new JLabel(p.getName());
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
                            price = prod.getPrice() * getProposal().getRegion().getRate() - prod.getPrice() * getProposal().getRegion().getRate() * prod.getDiscount();

                            ((CustomJLabel) src).setText((prod.getProposal().getCurrency().getSymbol() != null ?
                                    prod.getProposal().getCurrency().getSymbol() + " " : "") + df.format(price) + (prod.getProposal().getCurrency().getSymbol() == null ?
                                    " " + prod.getProposal().getCurrency().getName() : ""));
                        }
                    });
                    {
                        c.gridx++;
                        final JSpinner sp = new JSpinner(new SpinnerNumberModel(prod.getDiscount().doubleValue(), 0d, getProposal().getConfig().getMaxDiscount().doubleValue(), 0.001d));
                        sp.addChangeListener(new ChangeListener() {
                            public void stateChanged(ChangeEvent e) {

                                final ChangeEvent ev = e;
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        if (ev.getSource() == sp) {
                                            prod.setDiscount((Double) sp.getValue());
                                            eulabel.call();
                                            gleulabel.call();
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
                    eulabel.call();
                    {
                        c.gridx++;
                        JLabel label = new JLabel((p.getProposal().getCurrency().getSymbol() != null ?
                                p.getProposal().getCurrency().getSymbol() + " " : "") + df.format(p.getPrice() * getProposal().getRegion().getRate() * (Double) planRateField.getValue()) + (p.getProposal().getCurrency().getSymbol() == null ?
                                " " + p.getProposal().getCurrency().getName() : ""));
                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setBorder(new EmptyBorder(4, 4, 4, 4));
                        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        panel.setPreferredSize(new Dimension(0, 32));
                        panel.add(label);
                        panel.setBorder(lborder);
                        panel.setBackground(Color.white);
                        productsTable.add(panel, c);
                    }
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
                    {
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
                gleulabel.call();
                {
                    c.gridx++;
                    JLabel label = new JLabel((getProposal().getCurrency().getSymbol() != null ?
                            getProposal().getCurrency().getSymbol() + " " : "") + df.format(getProposal().getPrice() * getProposal().getRegion().getRate() *
                            (Double) planRateField.getValue()) + (getProposal().getCurrency().getSymbol() == null ?
                            " " + getProposal().getCurrency().getName() : ""));
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(lborder);
                    panel.setBackground(Color.white);
                    productsTable.add(panel, c);
                }

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
