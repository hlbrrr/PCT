package com.compassplus.gui;

import com.compassplus.configurationModel.CapacitiesGroup;
import com.compassplus.configurationModel.Capacity;
import com.compassplus.configurationModel.Module;
import com.compassplus.configurationModel.ModulesGroup;
import com.compassplus.proposalModel.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 14.10.11
 * Time: 1:08
 */
public class ProductForm {
    private DecimalFormat df;
    private Product product;
    private JPanel mainPanel;
    private JCheckBox primaryCheckBox;
    private Map<String, ModuleJCheckbox> checkBoxes = new HashMap<String, ModuleJCheckbox>();
    private PCTChangedListener priceChanged;

    public ProductForm(Product product, PCTChangedListener priceChanged, DecimalFormat df) {
        this.product = product;
        this.df = df;
        this.priceChanged = priceChanged;
        mainPanel = new ProductJPanel(this);
        mainPanel.setLayout(new GridBagLayout());
        initForm();
    }

    private Map<String, ModuleJCheckbox> getCheckBoxes() {
        return checkBoxes;
    }

    public void reloadProductPrice() {
        priceChanged.act(this);
    }

    private void reloadModulesPrices() {
        for (ModuleJCheckbox cb : getCheckBoxes().values()) {
            Module m = getProduct().getProduct().getModules().get(cb.getKey());
            cb.setText((m.isDeprecated() ? "[DEPRECATED] " : "") + m.getName() + " ($" + df.format(m.getPrice(getProduct())) + ")");
        }
    }

    private void initForm() {
        primaryCheckBox = new JCheckBox("Primary sale", !getProduct().getSecondarySale());
        primaryCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                JCheckBox src = (JCheckBox) e.getSource();
                getProduct().setSecondarySale(!src.isSelected());
                reloadModulesPrices();
                reloadProductPrice();
            }
        });
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        mainPanel.add(primaryCheckBox, c);

        JPanel modulesPanel = new JPanel();
        modulesPanel.setLayout(new BorderLayout());
        JPanel capacitiesPanel = new JPanel();
        capacitiesPanel.setLayout(new BorderLayout());

        modulesPanel.add(new JLabel("Modules"), BorderLayout.NORTH);
        modulesPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        capacitiesPanel.add(new JLabel("Capacities"), BorderLayout.NORTH);
        capacitiesPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 1;
        mainPanel.add(modulesPanel, c);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        c.weighty = 1.0;
        c.gridx = 1;
        c.gridy = 1;
        mainPanel.add(capacitiesPanel, c);

        JPanel modules = new JPanel();
        JScrollPane modulesScroll = new JScrollPane(modules);
        modulesPanel.add(modulesScroll, BorderLayout.CENTER);
        getFormFromModulesGroup(modules);

        JPanel capacities = new JPanel();
        capacities.setLayout(new GridLayout(0, 1));
        JScrollPane capacitiesScroll = new JScrollPane(capacities);
        capacitiesPanel.add(capacitiesScroll, BorderLayout.CENTER);
        getFormFromCapacitiesGroup(capacities);

        reloadModulesPrices();
    }

    private void getFormFromModulesGroup(JPanel parent) {
        getFormFromModulesGroup(parent, null);
    }

    private void getFormFromModulesGroup(JPanel parent, ModulesGroup modulesGroup) {

        boolean isRoot = false;
        if (modulesGroup == null) {
            isRoot = true;
            modulesGroup = getProduct().getProduct().getModulesRoot();
            parent.setBorder(new EmptyBorder(5, 5, 5, 5));
        } else {
            parent.setBorder(BorderFactory.createTitledBorder(modulesGroup.getName()));
        }

        parent.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));

        for (String key : modulesGroup.getModules().keySet()) {
            Module m = modulesGroup.getModules().get(key);
            if (!m.isDeprecated() || getProduct().getModules().containsKey(key)) {
                final ModuleJCheckbox mc = new ModuleJCheckbox(null, getProduct().getModules().containsKey(key), key);
                checkBoxes.put(key, mc);
                mc.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent ev) {
                        if (ev.getSource() == mc) {
                            final ItemEvent e = ev;
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    ModuleJCheckbox src = (ModuleJCheckbox) e.getSource();
                                    if (e.getStateChange() == ItemEvent.SELECTED) {
                                        if (getProduct().getProduct().getModules().get(src.getKey()).isDeprecated()) {
                                            JOptionPane.showMessageDialog(getRoot(), "Deprecated module can't be enabled.", "Error", JOptionPane.INFORMATION_MESSAGE);
                                            src.setSelected(false, true);
                                            return;
                                        }
                                        ArrayList<String> excludeKeys = new ArrayList<String>(0);
                                        for (String key : getProduct().getProduct().getModules().get(src.getKey()).getExcludeModules()) {
                                            if (getProduct().getModules().containsKey(key)) {
                                                excludeKeys.add(key);
                                            }
                                        }
                                        ArrayList<String> excludeThisKeys = new ArrayList<String>(0);
                                        for (String key : getProduct().getModules().keySet()) {
                                            if (getProduct().getProduct().getModules().get(key).getExcludeModules().contains(src.getKey())) {
                                                excludeThisKeys.add(key);
                                            }
                                        }
                                        if (excludeKeys.size() > 0 || excludeThisKeys.size() > 0) {
                                            StringBuilder sb = new StringBuilder("Selected module can't be enabled at the same time with following module(s):");
                                            for (String key : excludeKeys) {
                                                sb.append("\n").append(getProduct().getProduct().getModules().get(key).getPath());
                                            }
                                            for (String key : excludeThisKeys) {
                                                if (!excludeKeys.contains(key)) {
                                                    sb.append("\n").append(getProduct().getProduct().getModules().get(key).getPath());
                                                }
                                            }
                                            sb.append("\n\nYou should disable conflict module(s) first, then try again.");
                                            JOptionPane.showMessageDialog(getRoot(), sb.toString(), "Error", JOptionPane.INFORMATION_MESSAGE);

                                            src.setSelected(false, true);
                                            return;
                                        } else {
                                            ArrayList<String> requireKeys = new ArrayList<String>(0);
                                            for (String key : getProduct().getProduct().getModules().get(src.getKey()).getRequireModules()) {
                                                if (!getProduct().getModules().containsKey(key)) {
                                                    requireKeys.add(key);
                                                }
                                            }
                                            if (requireKeys.size() > 0) {
                                                StringBuilder sb = new StringBuilder("Selected module requires following module(s):");
                                                for (String key : requireKeys) {
                                                    sb.append("\n").append(getProduct().getProduct().getModules().get(key).getPath());
                                                }
                                                sb.append("\n\nWe will try to automatically resolve conflict");
                                                int ret = JOptionPane.showOptionDialog(getRoot(), sb.toString(), "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                                                if (ret == JOptionPane.OK_OPTION) {
                                                    requireKeys.add(src.getKey());
                                                    ArrayList<String> cantBeEnabled = new ArrayList<String>(0);
                                                    for (String key : requireKeys) {
                                                        if (!getProduct().canBeEnabled(key, requireKeys)) {
                                                            cantBeEnabled.add(key);
                                                        }
                                                    }
                                                    if (cantBeEnabled.size() > 0) {
                                                        StringBuilder sbm = new StringBuilder("Automatic resolution failed. Following module(s) can't be enabled because of complicated dependencies:");
                                                        for (String key : cantBeEnabled) {
                                                            sbm.append("\n").append(getProduct().getProduct().getModules().get(key).getPath());
                                                        }
                                                        sbm.append("\n\nYou should enable required module(s) manually, then try again.");
                                                        JOptionPane.showMessageDialog(getRoot(), sbm.toString(), "Error", JOptionPane.INFORMATION_MESSAGE);
                                                        src.setSelected(false, true);
                                                        return;
                                                    } else {
                                                        for (String key : requireKeys) {
                                                            if (!key.equals(src.getKey())) {
                                                                getProduct().addModule(getProduct().getProduct().getModules().get(key), key);
                                                                getCheckBoxes().get(key).setSelected(true, true);
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    src.setSelected(false, true);
                                                    return;
                                                }
                                            }
                                        }
                                        getProduct().addModule(getProduct().getProduct().getModules().get(src.getKey()), src.getKey());
                                    } else {
                                        ArrayList<String> requireThisKeys = new ArrayList<String>();
                                        for (String key : getProduct().getModules().keySet()) {
                                            if (getProduct().getProduct().getModules().get(key).getRequireModules().contains(src.getKey())) {
                                                requireThisKeys.add(key);
                                            }
                                        }
                                        if (requireThisKeys.size() > 0) {
                                            StringBuilder sb = new StringBuilder("You are trying to disable module that required by following module(s):");
                                            for (String key : requireThisKeys) {
                                                sb.append("\n").append(getProduct().getProduct().getModules().get(key).getPath());
                                            }
                                            sb.append("\n\nYou should disable dependent module(s) first, then try again.");
                                            JOptionPane.showMessageDialog(getRoot(), sb.toString(), "Error", JOptionPane.INFORMATION_MESSAGE);
                                            src.setSelected(true, true);
                                            return;
                                        }

                                        getProduct().delModule(src.getKey());
                                    }
                                    reloadProductPrice();
                                }
                            });
                        }
                    }
                });
                parent.add(mc);
                mc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 23));
            }

        }
        for (ModulesGroup g : modulesGroup.getGroups()) {
            JPanel modules = new JPanel();
            getFormFromModulesGroup(modules, g);
            parent.add(modules);
        }
    }

    private void getFormFromCapacitiesGroup(JPanel parent) {
        getFormFromCapacitiesGroup(parent, null);
    }

    private void getFormFromCapacitiesGroup(JPanel parent, CapacitiesGroup capacitiesGroup) {

        boolean isRoot = false;
        if (capacitiesGroup == null) {
            isRoot = true;
            capacitiesGroup = getProduct().getProduct().getCapacitiesRoot();
            parent.setBorder(new EmptyBorder(5, 5, 5, 5));
        } else {
            parent.setBorder(BorderFactory.createTitledBorder(capacitiesGroup.getName()));
        }

        parent.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));

        for (String key : capacitiesGroup.getCapacities().keySet()) {
            final Capacity c = capacitiesGroup.getCapacities().get(key);
            if (!c.isDeprecated() || getProduct().getCapacities().containsKey(key)) {
                final CapacityJSpinner cs = new CapacityJSpinner(getProduct().getCapacities().containsKey(key) ? getProduct().getCapacities().get(key).getValue() : 0, getRoot(), c.isDeprecated(), key);
                cs.setMaximumSize(new Dimension(cs.getMaximumSize().width, cs.getMinimumSize().height));
                cs.setAlignmentX(Component.LEFT_ALIGNMENT);
                final JLabel cl = new JLabel();
                cl.setAlignmentX(Component.LEFT_ALIGNMENT);
                ChangeListener changeListener = new ChangeListener() {
                    public void stateChanged(ChangeEvent ev) {
                        if (ev.getSource() == cs) {
                            final ChangeEvent e = ev;
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    CapacityJSpinner src = (CapacityJSpinner) e.getSource();
                                    if (src.getValue() instanceof Integer && (Integer) src.getValue() > 0) {
                                        if (!getProduct().getCapacities().containsKey(src.getKey())) {
                                            Capacity tmpCapacity = getProduct().getProduct().getCapacities().get(src.getKey());
                                            getProduct().addCapacity(tmpCapacity, src.getKey());
                                        }
                                        getProduct().getCapacities().get(src.getKey()).setValue((Integer) src.getValue());
                                    } else {
                                        getProduct().delCapacity(src.getKey());
                                    }
                                    Double newPrice = 0d;
                                    if (getProduct().getCapacities().containsKey(src.getKey())) {
                                        com.compassplus.proposalModel.Capacity c = getProduct().getCapacities().get(src.getKey());
                                        newPrice = c.getPrice();
                                    }
                                    cl.setText((c.isDeprecated() ? "[DEPRECATED] " : "") + c.getName() + " ($" + df.format(newPrice) + ")");
                                    reloadProductPrice();
                                }
                            });
                        }
                    }
                };
                cl.setText((c.isDeprecated() ? "[DEPRECATED] " : "") + c.getName() + " ($" + df.format(getProduct().getCapacities().containsKey(key) ? getProduct().getCapacities().get(key).getPrice() : 0d) + ")");

                cs.addChangeListener(changeListener);
                JPanel tmpPanel = new JPanel();
                tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.Y_AXIS));
                tmpPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
                tmpPanel.add(cl);
                tmpPanel.add(cs);
                parent.add(tmpPanel);
            }
        }
        for (CapacitiesGroup g : capacitiesGroup.getGroups()) {
            JPanel capacities = new JPanel();
            getFormFromCapacitiesGroup(capacities, g);
            parent.add(capacities);
        }
    }

    public Product getProduct() {
        return product;
    }

    public JPanel getRoot() {
        return mainPanel;
    }
}
