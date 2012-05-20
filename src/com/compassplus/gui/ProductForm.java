package com.compassplus.gui;

import com.compassplus.configurationModel.*;
import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.proposalModel.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
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
    private JComboBox licenseModel;
    private JFrame frame;
    private Map<String, CapacityJSpinner> spinners = new HashMap<String, CapacityJSpinner>();
    private Map<String, ModuleJButton> checkBoxes = new HashMap<String, ModuleJButton>();
    private ArrayList<ModuleJButton> checkBoxesToCheck = new ArrayList<ModuleJButton>();
    private PCTChangedListener priceChanged;

    public ProductForm(Product product, PCTChangedListener priceChanged, DecimalFormat df, JFrame frame) {
        this.frame = frame;
        this.product = product;
        this.df = df;
        this.priceChanged = priceChanged;
        mainPanel = new ProductJPanel(this);
        mainPanel.setLayout(new GridBagLayout());
        initForm();
        for (ModuleJButton mjb : checkBoxesToCheck) {
            boolean groupEmpty = true;
            for (AbstractButton ab : mjb.getGroup().buttons) {
                if (ab.isSelected()) {
                    groupEmpty = false;
                    break;
                }
            }
            if (groupEmpty) {
                mjb.setSelected(true, false);
            }
        }
        checkBoxesToCheck.clear();
    }

    private Map<String, ModuleJButton> getCheckBoxes() {
        return checkBoxes;
    }

    private Map<String, CapacityJSpinner> getSpinners() {
        return spinners;
    }

    public void reloadProductPrice() {
        priceChanged.act(this);
    }

    private void reloadModulesPrices() {
        StringBuilder sb = new StringBuilder();
        for (ModuleJButton cb : getCheckBoxes().values()) {
            Module m = getProduct().getProduct().getModules().get(cb.getKey());
            sb.setLength(0);
            sb.append(m.isDeprecated() ? "[DEPRECATED] " : "");
            sb.append(m.getName());
            sb.append(" (");
            if (product.getProposal().getCurrency().getSymbol() != null) {
                sb.append(product.getProposal().getCurrency().getSymbol());
                sb.append(" ");
            }
            sb.append(df.format(m.getRegionalPrice(getProduct())));
            if (product.getProposal().getCurrency().getSymbol() == null) {
                sb.append(" ");
                sb.append(product.getProposal().getCurrency().getName());
            }
            sb.append(")");
            cb.setText(sb.toString());
            cb.setActionCommand(sb.toString());
        }
    }

    private void reloadCapacitiesPrices() {
        for (CapacityJSpinner cs : getSpinners().values()) {
            reloadCapacitiesPrices(cs);
        }
    }

    private void reloadCapacitiesPrices(CapacityJSpinner cas) {
        Double newPrice = 0d;

        Capacity c = getProduct().getProduct().getCapacities().get(cas.getKey());
        com.compassplus.proposalModel.Capacity cP = getProduct().getCapacities().get(cas.getKey());
        if (cP != null) {
            newPrice = cP.getRegionalPrice(getProduct());
        }
        StringBuilder sb = new StringBuilder();
        sb.append(c.isDeprecated() ? "[DEPRECATED] " : "");
        sb.append(c.getName());
        sb.append(" (");
        if (product.getProposal().getCurrency().getSymbol() != null) {
            sb.append(product.getProposal().getCurrency().getSymbol());
            sb.append(" ");
        }
        sb.append(df.format(newPrice));
        if (product.getProposal().getCurrency().getSymbol() == null) {
            sb.append(" ");
            sb.append(product.getProposal().getCurrency().getName());
        }
        sb.append(")");
        cas.getLabel().setText(sb.toString());
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
        c.insets = new Insets(5, 5, 1, 5);
        mainPanel.add(primaryCheckBox, c);
        final JPanel capacitiesC = new JPanel();
        if (getProduct().getLicense() != null) {
            licenseModel = new JComboBox(product.getProduct().getLicenses().values().toArray());
            licenseModel.setSelectedItem(getProduct().getLicense());
            licenseModel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    final ActionEvent e = ev;
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            JComboBox src = (JComboBox) e.getSource();
                            getProduct().setLicense((License) src.getSelectedItem());
                            capacitiesC.removeAll();
                            getFormFromCapacitiesGroup(capacitiesC);
                            capacitiesC.updateUI();
                            reloadModulesPrices();
                            reloadProductPrice();
                        }
                    });
                }
            });
            c.gridx = 1;
            mainPanel.add(licenseModel, c);
        }

        JPanel modulesPanel = new JPanel();
        modulesPanel.setLayout(new BorderLayout());
        JPanel capacitiesPanel = new JPanel();
        capacitiesPanel.setLayout(new BorderLayout());

        // modulesPanel.add(new JLabel("Modules"), BorderLayout.NORTH);
        modulesPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        //capacitiesPanel.add(new JLabel("Capacities"), BorderLayout.NORTH);
        capacitiesPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0.5;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 1;
        mainPanel.add(modulesPanel, c);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHEAST;
        c.weightx = 0.5;
        c.weighty = 1.0;
        c.gridx = 1;
        c.gridy = 1;
        mainPanel.add(capacitiesPanel, c);

        JPanel modules = new JPanel();
        JPanel capacities = new JPanel();
        JScrollPane modulesScroll = new JScrollPane(modules);
        modulesScroll.getVerticalScrollBar().setUnitIncrement(16);
        JScrollPane capacitiesScroll = new JScrollPane(capacities);
        capacitiesScroll.getVerticalScrollBar().setUnitIncrement(16);
        modulesPanel.add(modulesScroll, BorderLayout.CENTER);
        capacitiesPanel.add(capacitiesScroll, BorderLayout.CENTER);
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0;
        c.weightx = 1.0;
        modules.setLayout(new GridBagLayout());
        capacities.setLayout(new GridBagLayout());
        JPanel modulesC = new JPanel();
        modules.add(modulesC, c);
        capacities.add(capacitiesC, c);
        c.gridy++;
        c.weighty = 1.0;
        modules.add(new JPanel(), c);
        capacities.add(new JPanel(), c);
        getFormFromModulesGroup(modulesC);

        getFormFromCapacitiesGroup(capacitiesC);
        reloadModulesPrices();
    }

    private void getFormFromModulesGroup(JPanel parent) {
        try {
            getFormFromModulesGroup(parent, null);
        } catch (PCTDataFormatException e) {
        }
    }

    private void getFormFromModulesGroup(JPanel parent, ModulesGroup modulesGroup) throws PCTDataFormatException {
        int addedItems = 0;
        int addedGroups = 0;

        boolean isRoot = false;

        if (modulesGroup == null) {
            isRoot = true;
            modulesGroup = getProduct().getProduct().getModulesRoot();
        } else {
            parent.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        }
        GridBagConstraints cg = new GridBagConstraints();
        cg.gridx = 0;
        cg.gridy = 0;
        cg.fill = GridBagConstraints.HORIZONTAL;
        cg.weightx = 1.0;
        cg.gridwidth = 2;
        parent.setLayout(new GridBagLayout());


        /*if (!modulesGroup.isRadioButtonGroup() || true) {*/
        ButtonGroup bg = null;
        boolean firstModule = true;
        for (String key : modulesGroup.getModules().keySet()) {
            Module m = modulesGroup.getModules().get(key);
            if (!m.isDeprecated() || getProduct().getModules().containsKey(key)) {
                final ModuleJButton mc;
                if (modulesGroup.isRadioButtonGroup()) {
                    mc = new ModuleJRadio(null, getProduct().getModules().containsKey(key), key, this);
                } else {
                    mc = new ModuleJCheckbox(null, getProduct().getModules().containsKey(key), key, this);
                }
                getCheckBoxes().put(key, mc);
                mc.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent ev) {
                        if (ev.getSource() == mc) {
                            final ItemEvent e = ev;
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    ModuleJButton src = (ModuleJButton) e.getSource();
                                    try {
                                        if (e.getStateChange() == ItemEvent.SELECTED) {
                                            if (getProduct().getProduct().getModules().get(src.getKey()).isDeprecated()) {
                                                JOptionPane.showMessageDialog(getRoot(), "Deprecated module can't be enabled.", "Error", JOptionPane.INFORMATION_MESSAGE);
                                                src.setSelected(false, true);
                                                throw new PCTDataFormatException("");
                                            }
                                            ModuleJButton depMod = null;
                                            /* проверка на возможность выключения мешающего модуля */
                                            if (src.getGroup() != null) {
                                                for (AbstractButton ab : src.getGroup().buttons) {
                                                    if (ab.isSelected() && ab instanceof ModuleJButton && !((ModuleJButton) ab).getKey().equals(src.getKey())) {
                                                        depMod = (ModuleJButton) ab;
                                                        break;
                                                    }
                                                }
                                                if (depMod != null) {
                                                    ArrayList<String> requireThisKeys = new ArrayList<String>();
                                                    for (String key : getProduct().getModules().keySet()) {
                                                        for (String rkey : getProduct().getProduct().getModules().get(key).getRequireModules()) {
                                                            String rkeys[] = rkey.split("\\s+");
                                                            boolean req = false;
                                                            boolean hasAnother = false;
                                                            for (int i = 0; i < rkeys.length; i++) {
                                                                if (depMod.getKey().equals(rkeys[i])) {
                                                                    req = true;
                                                                } else if (getProduct().getModules().containsKey(rkeys[i]) || src.getKey().equals(rkeys[i])) {
                                                                    hasAnother = true;
                                                                }
                                                            }
                                                            if(req && !hasAnother){
                                                                requireThisKeys.add(key);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    if (requireThisKeys.size() > 0) {
                                                        StringBuilder sb = new StringBuilder("You are trying to disable module that required by following module(s):");
                                                        for (String key : requireThisKeys) {
                                                            sb.append("\n").append(getProduct().getProduct().getModules().get(key).getPath());
                                                        }
                                                        sb.append("\n\nYou should disable dependent module(s) first, then try again.");
                                                        JOptionPane.showMessageDialog(getRoot(), sb.toString(), "Error", JOptionPane.INFORMATION_MESSAGE);

                                                        src.setSelected(false, true);
                                                        throw new PCTDataFormatException("");
                                                    }
                                                }

                                            }
                                            /* список модулей которые мешают текущему */
                                            ArrayList<String> excludeKeys = new ArrayList<String>(0);
                                            for (String key : getProduct().getProduct().getModules().get(src.getKey()).getExcludeModules()) {
                                                if (getProduct().getModules().containsKey(key) && getProduct().getProduct().getModules().containsKey(key)) {
                                                    excludeKeys.add(key);
                                                }
                                            }
                                            /* список модулей которым мешает текущий */
                                            ArrayList<String> excludeThisKeys = new ArrayList<String>(0);
                                            for (String key : getProduct().getModules().keySet()) {
                                                if (getProduct().getProduct().getModules().get(key).getExcludeModules().contains(src.getKey())) {
                                                    excludeThisKeys.add(key);
                                                }
                                            }
                                            /* удалили смежные радиобаттоны */
                                            if (src.getGroup() != null) {
                                                for (AbstractButton mjb : src.getGroup().buttons) {
                                                    if (mjb != null && mjb instanceof ModuleJButton) {
                                                        excludeThisKeys.remove(((ModuleJButton) mjb).getKey());
                                                        excludeKeys.remove(((ModuleJButton) mjb).getKey());
                                                    }
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
                                                throw new PCTDataFormatException("");
                                            } else {
                                                ArrayList<String> requireKeys = new ArrayList<String>(0);
                                                for (String key : getProduct().getProduct().getModules().get(src.getKey()).getRequireModules()) {
                                                    String keys[] = key.split("\\s+");
                                                    boolean contains = false;
                                                    for (int i = 0; i < keys.length; i++) {
                                                        if (getProduct().getModules().containsKey(keys[i]) && getProduct().getProduct().getModules().containsKey(keys[i])) {
                                                            contains = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!contains) {
                                                        requireKeys.add(key);
                                                    }
                                                }
                                                if (requireKeys.size() > 0) {
                                                    StringBuilder sb = new StringBuilder("Selected module requires following module(s):");
                                                    for (String key : requireKeys) {
                                                        String keys[] = key.split("\\s+");
                                                        for (int i = 0; i < keys.length; i++) {
                                                            if (i == 0) {
                                                                sb.append("\n");
                                                            } else {
                                                                sb.append(" or ");
                                                            }
                                                            sb.append(getProduct().getProduct().getModules().get(keys[i]).getPath());
                                                        }
                                                    }
                                                    sb.append("\n\nWe will try to automatically resolve conflict");
                                                    int ret = JOptionPane.showOptionDialog(getRoot(), sb.toString(), "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                                                    if (ret == JOptionPane.OK_OPTION) {
                                                        requireKeys.add(src.getKey());
                                                        ArrayList<String> cantBeEnabled = new ArrayList<String>(0);
                                                        for (String key : requireKeys) {
                                                            if (key.split("\\s+").length > 1 || !getProduct().canBeEnabled(key, requireKeys)) {
                                                                cantBeEnabled.add(key);
                                                            }
                                                        }
                                                        if (cantBeEnabled.size() > 0) {
                                                            StringBuilder sbm = new StringBuilder("Automatic resolution failed. Following module(s) can't be enabled because of complicated dependencies:");
                                                            for (String key : cantBeEnabled) {
                                                                String keys[] = key.split("\\s+");
                                                                for (int i = 0; i < keys.length; i++) {
                                                                    if (i == 0) {
                                                                        sbm.append("\n");
                                                                    } else {
                                                                        sbm.append(" or ");
                                                                    }
                                                                    sbm.append(getProduct().getProduct().getModules().get(keys[i]).getPath());
                                                                }
                                                            }
                                                            sbm.append("\n\nYou should enable required module(s) manually, then try again.");
                                                            JOptionPane.showMessageDialog(getRoot(), sbm.toString(), "Error", JOptionPane.INFORMATION_MESSAGE);
                                                            src.setSelected(false, true);
                                                            throw new PCTDataFormatException("");
                                                        } else {
                                                            for (String key : requireKeys) {
                                                                if (!key.equals(src.getKey())) {
                                                                    getProduct().addModule(getProduct().getProduct().getModules().get(key), key);
                                                                    for (RequireCapacity rc : getProduct().getProduct().getModules().get(key).getRequireCapacities().values()) {
                                                                        if (getProduct().getProduct().getCapacities().containsKey(rc.getKey())) {
                                                                            if (rc.isIncremental()) {
                                                                                getSpinners().get(rc.getKey()).addIncr(rc.getValue());
                                                                                if (rc.isFreeOfCharge()) {
                                                                                    getSpinners().get(rc.getKey()).addFoc(rc.getValue());
                                                                                }
                                                                            } else {
                                                                                getSpinners().get(rc.getKey()).addMin(rc.getValue());
                                                                            }
                                                                        }
                                                                    }
                                                                    getCheckBoxes().get(key).setSelected(true, true);
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        src.setSelected(false, true);
                                                        throw new PCTDataFormatException("");
                                                    }
                                                }
                                            }
                                            /* тут теоретически можно включить выбраный модуль , но сначала надо было проверить можно ли безболезненно выключить
                                            * исключаемый модуль, и все остальные проверки производить с учетом того что он выключен
                                            * */
                                            if (src.getGroup() != null && depMod != null) {
                                                depMod.setSelected(false, true);
                                                getProduct().delModule(depMod.getKey());
                                                for (RequireCapacity rc : getProduct().getProduct().getModules().get(depMod.getKey()).getRequireCapacities().values()) {
                                                    if (getProduct().getProduct().getCapacities().containsKey(rc.getKey())) {
                                                        if (rc.isIncremental()) {
                                                            getSpinners().get(rc.getKey()).delIncr(rc.getValue());
                                                            if (rc.isFreeOfCharge()) {
                                                                getSpinners().get(rc.getKey()).delFoc(rc.getValue());
                                                            }
                                                        } else {
                                                            getSpinners().get(rc.getKey()).delMin(rc.getValue());
                                                        }
                                                    }
                                                }

                                            }
                                            getProduct().addModule(getProduct().getProduct().getModules().get(src.getKey()), src.getKey());
                                            for (RequireCapacity rc : getProduct().getProduct().getModules().get(src.getKey()).getRequireCapacities().values()) {
                                                if (getProduct().getProduct().getCapacities().containsKey(rc.getKey())) {
                                                    if (rc.isIncremental()) {
                                                        getSpinners().get(rc.getKey()).addIncr(rc.getValue());
                                                        if (rc.isFreeOfCharge()) {
                                                            getSpinners().get(rc.getKey()).addFoc(rc.getValue());
                                                        }
                                                    } else {
                                                        getSpinners().get(rc.getKey()).addMin(rc.getValue());
                                                    }
                                                }
                                            }
                                        } else {
                                            // запрет выключения радиобаттона
                                            /*if (src.getGroup() != null) {
                                                src.setSelected(true, true);
                                                throw new PCTDataFormatException("");
                                            }*/
                                            ArrayList<String> requireThisKeys = new ArrayList<String>();
                                            for (String key : getProduct().getModules().keySet()) {
                                                for (String rkey : getProduct().getProduct().getModules().get(key).getRequireModules()) {
                                                    String rkeys[] = rkey.split("\\s+");
                                                    boolean req = false;
                                                    boolean hasAnother = false;
                                                    for (int i = 0; i < rkeys.length; i++) {
                                                        if (src.getKey().equals(rkeys[i])) {
                                                            req = true;
                                                        } else if (getProduct().getModules().containsKey(rkeys[i])) {
                                                            hasAnother = true;
                                                        }
                                                    }
                                                    if(req && !hasAnother){
                                                        requireThisKeys.add(key);
                                                        break;
                                                    }
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
                                                throw new PCTDataFormatException("");
                                            }
                                            getProduct().delModule(src.getKey());
                                            for (RequireCapacity rc : getProduct().getProduct().getModules().get(src.getKey()).getRequireCapacities().values()) {
                                                if (getProduct().getProduct().getCapacities().containsKey(rc.getKey())) {
                                                    if (rc.isIncremental()) {
                                                        getSpinners().get(rc.getKey()).delIncr(rc.getValue());
                                                        if (rc.isFreeOfCharge()) {
                                                            getSpinners().get(rc.getKey()).delFoc(rc.getValue());
                                                        }
                                                    } else {
                                                        getSpinners().get(rc.getKey()).delMin(rc.getValue());
                                                    }
                                                }
                                            }
                                        }
                                        reloadProductPrice();
                                    } catch (PCTDataFormatException er) {
                                        //src.dropOldSelected();
                                    }
                                }
                            });
                        }
                    }
                });
                if (!"".equals(m.getHint())) {
                    cg.gridwidth = 1;
                }
                mc.setBorder(new EmptyBorder(2, 5, 2, 3));
                mc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 23));
                parent.add((Component) mc, cg);
                if (modulesGroup.isRadioButtonGroup()) {
                    if (bg == null) {
                        bg = new ModuleButtonGroup();
                    }
                    /*if (modulesGroup.getDefaultModuleKey() != null && !modulesGroup.getDefaultModuleKey().equals("")) {
                        if (key.equals(modulesGroup.getDefaultModuleKey())) {
                            checkBoxesToCheck.add(mc);
                        }
                    } else if (firstModule) {
                        checkBoxesToCheck.add(mc);
                    }*/
                    bg.add((AbstractButton) mc);
                }
                if (!"".equals(m.getHint())) {
                    cg.weightx = 0;
                    cg.gridx++;
                    JLabel hl = new JLabel("<html>[?]</html>");
                    hl.setBorder(new EmptyBorder(0, 5, 2, 2));
                    hl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    final String about = m.getHint();
                    hl.addMouseListener(new MouseListener() {
                        public void mouseClicked(MouseEvent e) {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    showHint(about);
                                }
                            });
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
                    parent.add(hl, cg);
                    cg.weightx = 1.0;
                    cg.gridwidth = 2;
                    cg.gridx = 0;
                }
                cg.gridy++;
                addedItems++;
            }
            firstModule = false;
        }

        boolean first = true;
        for (ModulesGroup g : modulesGroup.getGroups()) {
            JPanel modules = null;
            try {
                modules = new JPanel();
                getFormFromModulesGroup(modules, g);
                JPanel labelPanel = new JPanel();
                labelPanel.setLayout(new GridBagLayout());
                labelPanel.setBorder(new EmptyBorder(first ? 10 : 5, 5, 5, 5));
                first = false;
                GridBagConstraints c = new GridBagConstraints();

                JLabel mm = new JLabel("<html><b>[~]</b></html>\"");
                mm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                mm.setBorder(new EmptyBorder(0, 4, 2, 2));
                final JPanel lnk = modules;
                mm.addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent e) {
                        final JLabel that = ((JLabel) e.getSource());
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                if (lnk.isVisible()) {
                                    that.setText("<html><b>[+]</b></html>\"");
                                } else {
                                    that.setText("<html><b>[~]</b></html>\"");
                                }
                                lnk.setVisible(!lnk.isVisible());
                            }
                        });
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
                c.weightx = 0;
                c.gridwidth = 1;
                c.gridx = 0;
                c.gridy = 0;
                c.fill = GridBagConstraints.HORIZONTAL;
                labelPanel.add(mm, c);
                c.gridx++;
                c.weightx = 1.0;
                JLabel gl = new JLabel("<html><b>" + g.getName() + "</b></html>");
                gl.setBorder(new EmptyBorder(0, 4, 2, 0));
                labelPanel.add(gl, c);
                if (!"".equals(g.getHint())) {
                    JLabel hl = new JLabel("<html><b>[?]</b></html>");
                    hl.setBorder(new EmptyBorder(0, 4, 2, 2));
                    hl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    final String about = g.getHint();
                    hl.addMouseListener(new MouseListener() {
                        public void mouseClicked(MouseEvent e) {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    showHint(about);
                                }
                            });
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
                    c.gridx++;
                    c.weightx = 0;
                    labelPanel.add(hl, c);
                }

                if (!"".equals(g.getHint())) {
                    c.gridwidth = 3;
                } else {
                    c.gridwidth = 2;
                }
                c.weightx = 1;
                c.gridy++;
                c.gridx = 0;
                labelPanel.add(modules, c);
                parent.add(labelPanel, cg);
                cg.gridy++;
                addedGroups++;
            } catch (PCTDataFormatException e) {
            }
        }
        if (addedGroups + addedItems == 0) {
            throw new PCTDataFormatException("Empty group");
        }
    }

    private void getFormFromCapacitiesGroup(JPanel parent) {
        try {
            getSpinners().clear();
            getFormFromCapacitiesGroup(parent, null);
        } catch (PCTDataFormatException e) {

        }
    }

    private void getFormFromCapacitiesGroup(JPanel parent, CapacitiesGroup capacitiesGroup) throws PCTDataFormatException {
        int addedItems = 0;
        int addedGroups = 0;

        boolean isRoot = false;
        if (capacitiesGroup == null) {
            isRoot = true;
            capacitiesGroup = getProduct().getProduct().getCapacitiesRoot();
            //parent.setBorder(new EmptyBorder(5, 5, 5, 5));
        } else {
            //parent.setBorder(BorderFactory.createTitledBorder(capacitiesGroup.getName()));
            parent.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        }
        GridBagConstraints cg = new GridBagConstraints();
        cg.gridx = 0;
        cg.gridy = 0;
        cg.fill = GridBagConstraints.HORIZONTAL;
        cg.weightx = 1.0;
        cg.gridwidth = 2;
        parent.setLayout(new GridBagLayout());

        for (String key : capacitiesGroup.getCapacities().keySet()) {
            final Capacity c = capacitiesGroup.getCapacities().get(key);
            if (!c.isDeprecated() || getProduct().getCapacities().containsKey(key)) {
                final JLabel cl = new JLabel();
                final JLabel cl1 = new JLabel();
                final JLabel cl2 = new JLabel();
                final JLabel cl3 = new JLabel();
                final CapacityJSpinner cs = new CapacityJSpinner(getProduct().getCapacities().containsKey(key) ? getProduct().getCapacities().get(key) : null, c.isDeprecated(), key, this, cl);
                cs.setMaximumSize(new Dimension(cs.getMaximumSize().width, cs.getMinimumSize().height));
                getSpinners().put(key, cs);
                final PCTChangedListener changeTitles = new PCTChangedListener() {
                    public void act(Object srcObj) {
                        //System.out.println("z");
                        CapacityJSpinner src = cs;
                        cl1.setText("");
                        cl2.setText("");
                        cl3.setText("");
                        if (getProduct().getCapacities().containsKey(src.getKey())) {
                            com.compassplus.proposalModel.Capacity c = getProduct().getCapacities().get(src.getKey());
                            if ((c.getVal() - c.getFoc() - c.getUser()) > 0) {
                                cl1.setText("Required by module(s): " + (c.getVal() - c.getFoc() - c.getUser()));
                            }
                            if (c.getFoc() > 0) {
                                cl2.setText("Free Of Charge: " + c.getFoc());
                            }
                            /*if ((c.getVal() - c.getUser()) > 0) {
                                cl3.setText("Total: " + c.getVal());
                            }*/
                        }
                        reloadCapacitiesPrices(cs);
                    }

                    public void setData(Object data) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public Object getData() {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }
                };
                ChangeListener changeListener = new ChangeListener() {
                    public void stateChanged(ChangeEvent ev) {
                        if (ev.getSource() == cs) {
                            final ChangeEvent e = ev;
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    changeTitles.act(null);
                                    reloadProductPrice();
                                }
                            });
                        }
                    }
                };
                cs.recalc();
                changeTitles.act(null);
                cs.addChangeListener(changeListener);
                JPanel tmpPanel = new JPanel();
                {
                    tmpPanel.setLayout(new GridBagLayout());
                    GridBagConstraints cc = new GridBagConstraints();
                    cc.gridx = 0;
                    cc.gridy = 0;
                    cc.gridwidth = 2;
                    cc.fill = GridBagConstraints.HORIZONTAL;
                    cc.weightx = 1.0;
                    tmpPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
                    if (!"".equals(c.getHint())) {
                        cc.gridwidth = 1;
                    }
                    tmpPanel.add(cl, cc);
                    if (!"".equals(c.getHint())) {
                        cc.weightx = 0;
                        cc.gridx++;
                        JLabel hl = new JLabel("<html>[?]</html>");
                        //hl.setBorder(new EmptyBorder(0, 5, 2, 2));
                        hl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        final String about = c.getHint();
                        hl.addMouseListener(new MouseListener() {
                            public void mouseClicked(MouseEvent e) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        showHint(about);
                                    }
                                });
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
                        tmpPanel.add(hl, cc);
                        cc.weightx = 1.0;
                        cc.gridwidth = 2;
                        cc.gridx = 0;
                    }
                    cc.gridy++;
                    tmpPanel.add(cl1, cc);
                    cc.gridy++;
                    tmpPanel.add(cl2, cc);
                    cc.gridy++;
                    tmpPanel.add(cl3, cc);
                    cc.gridy++;
                    tmpPanel.add(cs, cc);
                }
                //String licenseKey = getProduct().getProduct().getCapacities().get(c.getKey()).getLicenseKey();

                //if (licenseKey.equals("") || getProduct().getLicense() != null && licenseKey.equals(getProduct().getLicense().getKey())) {
                if (getProduct().getProduct().getCapacities().get(c.getKey()).checkLicenseKey(getProduct().getLicense() != null ? getProduct().getLicense().getKey() : null)) {
                    parent.add(tmpPanel, cg);
                    cg.gridy++;
                    addedItems++;
                }
            }
        }
        boolean first = true;
        for (CapacitiesGroup g : capacitiesGroup.getGroups()) {
            JPanel capacities = null;
            try {
                capacities = new JPanel();
                getFormFromCapacitiesGroup(capacities, g);

                JPanel labelPanel = new JPanel();
                labelPanel.setLayout(new GridBagLayout());
                labelPanel.setBorder(new EmptyBorder(first ? 10 : 5, 5, 5, 5));
                first = false;
                GridBagConstraints c = new GridBagConstraints();

                JLabel mm = new JLabel("<html><b>[~]</b></html>\"");
                mm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                mm.setBorder(new EmptyBorder(0, 4, 2, 2));
                final JPanel lnk = capacities;
                mm.addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent e) {
                        final JLabel that = ((JLabel) e.getSource());
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                if (lnk.isVisible()) {
                                    that.setText("<html><b>[+]</b></html>\"");
                                } else {
                                    that.setText("<html><b>[~]</b></html>\"");
                                }
                                lnk.setVisible(!lnk.isVisible());
                            }
                        });
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
                c.weightx = 0;
                c.gridwidth = 1;
                c.gridx = 0;
                c.gridy = 0;
                c.fill = GridBagConstraints.HORIZONTAL;
                labelPanel.add(mm, c);
                c.gridx++;
                c.weightx = 1.0;
                JLabel gl = new JLabel("<html><b>" + g.getName() + "</b></html>");
                gl.setBorder(new EmptyBorder(0, 4, 2, 0));

                labelPanel.add(gl, c);
                if (!"".equals(g.getHint())) {
                    JLabel hl = new JLabel("<html><b>[?]</b></html>");
                    hl.setBorder(new EmptyBorder(0, 4, 2, 2));
                    hl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    final String about = g.getHint();
                    hl.addMouseListener(new MouseListener() {
                        public void mouseClicked(MouseEvent e) {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    showHint(about);
                                }
                            });
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
                    c.gridx++;
                    c.weightx = 0;
                    labelPanel.add(hl, c);
                }
                if (!"".equals(g.getHint())) {
                    c.gridwidth = 3;
                } else {
                    c.gridwidth = 2;
                }
                c.weightx = 1;
                c.gridy++;
                c.gridx = 0;
                labelPanel.add(capacities, c);
                parent.add(labelPanel, cg);
                cg.gridy++;
                addedGroups++;
            } catch (PCTDataFormatException e) {
            }
        }

        if (addedGroups + addedItems == 0) {
            throw new PCTDataFormatException("Empty group");
        }
    }

    public Product getProduct() {
        return product;
    }

    public JPanel getRoot() {
        return mainPanel;
    }

    public void update() {
        this.reloadModulesPrices();
        this.reloadCapacitiesPrices();
        this.reloadProductPrice();
    }

    private void showHint(String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setColumns(35);
        textArea.setRows(10);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        JScrollPane spane = new JScrollPane(textArea);
        textArea.setFont(spane.getFont());
        JOptionPane.showMessageDialog(
                null, spane, "Help", JOptionPane.INFORMATION_MESSAGE);
/*
        final JOptionPane optionPane = new JOptionPane(
                new JComponent[]{
                        spane,
                },
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION);
        final JDialog dialog = new JDialog(getFrame(), "Help", true);
        dialog.setResizable(true);
        dialog.setContentPane(optionPane);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        optionPane.addPropertyChangeListener(
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent e) {
                        String prop = e.getPropertyName();
                        if (dialog.isVisible()
                                && (e.getSource() == optionPane)
                                && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                            dialog.setVisible(false);
                        }
                    }
                });

        dialog.pack();
        dialog.setLocationRelativeTo(getRoot());
        dialog.setVisible(true);*/
    }

    private JFrame getFrame() {
        return frame;
    }
}
