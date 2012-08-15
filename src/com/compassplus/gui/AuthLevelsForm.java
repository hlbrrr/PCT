package com.compassplus.gui;

import com.compassplus.configurationModel.*;
import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.proposalModel.Proposal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 14.08.12
 * Time: 22:07
 */
public class AuthLevelsForm {
    private JPanel mainPanel;
    private Proposal proposal;
    private PCTChangedListener alUpdater;

    public AuthLevelsForm(Proposal proposal, PCTChangedListener alUpdater) {
        this.mainPanel = new JPanel();
        this.proposal = proposal;
        this.alUpdater = alUpdater;

        initForm();

    }

    private void initForm() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        JPanel modulesPanel = new JPanel();
        modulesPanel.setLayout(new BorderLayout());

        modulesPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 1;
        mainPanel.add(modulesPanel, c);

        JPanel modules = new JPanel();
        JScrollPane modulesScroll = new JScrollPane(modules);
        modulesScroll.getVerticalScrollBar().setUnitIncrement(16);
        modulesPanel.add(modulesScroll, BorderLayout.CENTER);
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0;
        c.weightx = 1.0;
        modules.setLayout(new GridBagLayout());
        JPanel modulesC = new JPanel();
        modules.add(modulesC, c);
        c.gridy++;
        c.weighty = 1.0;
        modules.add(new JPanel(), c);
        getFormFromModulesGroup(modulesC);
    }

    private void getFormFromModulesGroup(JPanel parent) {
        try {
            getFormFromModulesGroup(parent, null);
        } catch (PCTDataFormatException e) {
        }
    }


    private void getFormFromModulesGroup(JPanel parent, AuthLevel modulesGroup) throws PCTDataFormatException {
        int addedItems = 0;
        int addedGroups = 0;

        if (modulesGroup != null) {
            parent.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        }
        GridBagConstraints cg = new GridBagConstraints();
        cg.gridx = 0;
        cg.gridy = 0;
        cg.fill = GridBagConstraints.HORIZONTAL;
        cg.weightx = 1.0;
        cg.gridwidth = 1;
        parent.setLayout(new GridBagLayout());

        ButtonGroup bg = null;
        if (modulesGroup != null) {
            final AuthLevel al = modulesGroup;
            for (String key : modulesGroup.getLevels().keySet()) {
                AuthLevelLevel m = modulesGroup.getLevels().get(key);
                final String fkey = key;
                final JRadioButton mc;
                mc = new JRadioButton(m.getName(), false);
                mc.setFont(new Font(mc.getFont().getName(), Font.BOLD, mc.getFont().getSize()));
                mc.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent ev) {
                        if (ev.getSource() == mc) {
                            final ItemEvent e = ev;
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    JRadioButton src = (JRadioButton) e.getSource();
                                    try {
                                        if (e.getStateChange() == ItemEvent.SELECTED) {
                                            proposal.getSelectedAls().put(al.getKey(), fkey);
                                            alUpdater.act(proposal);
                                        } else {

                                        }
                                    } catch (Exception er) {
                                        er.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                });
                mc.setBorder(new EmptyBorder(2, 5, 2, 3));
                mc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 23));
                parent.add((Component) mc, cg);
                if (bg == null) {
                    bg = new ModuleButtonGroup();
                }

                bg.add((AbstractButton) mc);

                mc.setSelected(proposal.getSelectedAls().containsKey(modulesGroup.getKey()) && proposal.getSelectedAls().get(modulesGroup.getKey()).equals(key));

                cg.gridx = 0;
                if (!"".equals(m.getDescription())) {
                    cg.gridy++;
                    final JPanel tmpPanel = new JPanel();
                    tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.X_AXIS));
                    final TextNote note = new TextNote(m.getDescription());
                    note.setMaximumSize(new Dimension(700, Integer.MAX_VALUE));
                    tmpPanel.add(note);
                    parent.add(tmpPanel, cg);
                }
                cg.gridy++;
                addedItems++;
            }
        }

        boolean first = true;
        if (modulesGroup == null) {
            for (AuthLevel g : proposal.getConfig().getAuthLevels().values()) {
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
                    //modulesGroupsLinks.put(g, new LnkPanel(modules, mm));
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
                    c.gridwidth = 2;
                    c.weightx = 1;
                    c.gridx = 0;
                    if (!"".equals(g.getDescription())) {
                        c.gridy++;
                        final JPanel tmpPanel = new JPanel();
                        tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.X_AXIS));
                        final TextNote note = new TextNote(g.getDescription());
                        note.setMaximumSize(new Dimension(700, Integer.MAX_VALUE));
                        tmpPanel.add(note);
                        labelPanel.add(tmpPanel, c);
                    }
                    c.gridy++;
                    c.gridx = 0;
                    labelPanel.add(modules, c);
                    parent.add(labelPanel, cg);
                    cg.gridy++;
                    addedGroups++;
                } catch (PCTDataFormatException e) {
                }
            }
        }
        if (addedGroups + addedItems == 0) {
            throw new PCTDataFormatException("Empty group");
        }
    }

    public JPanel getRoot() {
        return mainPanel;
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
    }
}
