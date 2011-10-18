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
    private ArrayList<ModuleJCheckbox> checkBoxes = new ArrayList<ModuleJCheckbox>();
    private PCTChangedListener priceChanged;

    public ProductForm(Product product, PCTChangedListener priceChanged, DecimalFormat df) {
        this.product = product;
        this.df = df;
        this.priceChanged = priceChanged;
        mainPanel = new ProductJPanel(this);
        mainPanel.setLayout(new GridBagLayout());
        initForm();
    }

    private ArrayList<ModuleJCheckbox> getCheckBoxes() {
        return checkBoxes;
    }

    public void reloadProductPrice() {
        priceChanged.act(this);
    }

    private void reloadModulesPrices() {
        for (ModuleJCheckbox cb : getCheckBoxes()) {
            Module m = getProduct().getProduct().getModules().get(cb.getKey());
            cb.setText(m.getName() + " ($" + df.format(m.getPrice(getProduct())) + ")");
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
            final ModuleJCheckbox mc = new ModuleJCheckbox(null, getProduct().getModules().containsKey(key), key);
            checkBoxes.add(mc);
            mc.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getSource() == mc) {
                        ModuleJCheckbox src = (ModuleJCheckbox) e.getSource();
                        if (src.isSelected()) {
                            getProduct().addModule(getProduct().getProduct().getModules().get(src.getKey()), src.getKey());
                        } else {
                            getProduct().delModule(src.getKey());
                        }
                        reloadProductPrice();
                    }
                }
            });
            mc.setMaximumSize(new Dimension(Integer.MAX_VALUE, mc.getMaximumSize().height));

            parent.add(mc);
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
            final CapacityJSpinner cs = new CapacityJSpinner(new SpinnerNumberModel(getProduct().getCapacities().containsKey(key) ? getProduct().getCapacities().get(key).getValue() : 0, 0, Integer.MAX_VALUE, 1), key);
            cs.setMaximumSize(new Dimension(cs.getMaximumSize().width, cs.getMinimumSize().height));
            cs.setAlignmentX(Component.LEFT_ALIGNMENT);
            final JLabel cl = new JLabel();
            cl.setAlignmentX(Component.LEFT_ALIGNMENT);
            ChangeListener changeListener = new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    if (e.getSource() == cs) {
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
                        cl.setText(c.getName() + " ($" + df.format(newPrice) + ")");
                        reloadProductPrice();
                    }
                }
            };
            changeListener.stateChanged(new ChangeEvent(cs));
            cs.addChangeListener(changeListener);
            parent.add(cl);
            parent.add(cs);
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
