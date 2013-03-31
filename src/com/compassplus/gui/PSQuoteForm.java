package com.compassplus.gui;

import com.compassplus.configurationModel.Recommendation;
import com.compassplus.configurationModel.Service;
import com.compassplus.configurationModel.ServicesGroup;
import com.compassplus.proposalModel.Proposal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: arudin
 * Date: 3/30/13
 * Time: 11:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class PSQuoteForm {
    private JFrame frame;
    private JPanel mainPanel;
    private Proposal proposal;
    private PCTChangedListener titleUpdater;
    private DecimalFormat df;

    public PSQuoteForm(Proposal proposal, PCTChangedListener titleUpdater, DecimalFormat df, JFrame frame) {
        this.titleUpdater = titleUpdater;
        this.proposal = proposal;
        this.frame = frame;
        this.df = df;
        mainPanel = new PSQuoteJPanel(this);
        mainPanel.setLayout(new GridBagLayout());
        initForm();
    }

    public void update(){
        mainPanel.removeAll();
        initForm();
    }

    private void initForm() {
        JPanel modulesC = new JPanel();
        {
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(5, 5, 1, 5);

            JPanel modulesPanel = new JPanel();
            modulesPanel.setLayout(new BorderLayout());
            modulesPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            c = new GridBagConstraints();
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

            modules.add(modulesC, c);
            c.gridy++;
            c.weighty = 1.0;
            modules.add(new JPanel(), c);
        }

        JPanel parent = modulesC;
        GridBagConstraints cg = new GridBagConstraints();
        cg.gridx = 0;
        cg.gridy = 0;
        cg.fill = GridBagConstraints.HORIZONTAL;
        cg.weightx = 1.0;
        cg.gridwidth = 1;
        parent.setLayout(new GridBagLayout());

        boolean first = true;
        for (ServicesGroup sg : proposal.getConfig().getServicesRoot().getGroups()) {
            JPanel serviceGroup = new JPanel();
            if (getFormFromGroup(serviceGroup, sg) > 0) {
            } else {
                continue;
            }
            JPanel labelPanel = new JPanel();
            labelPanel.setLayout(new GridBagLayout());
            labelPanel.setBorder(new EmptyBorder(first ? 10 : 5, 5, 5, 5));
            first = false;
            GridBagConstraints c = new GridBagConstraints();
            JLabel mm = new JLabel("<html><b>[~]</b></html>\"");
            mm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            mm.setBorder(new EmptyBorder(0, 4, 2, 2));
            final JPanel lnk = serviceGroup;
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
            JLabel gl = new JLabel("<html><b>" + sg.getName() + "</b></html>");
            gl.setBorder(new EmptyBorder(0, 4, 2, 0));
            labelPanel.add(gl, c);
            if (!"".equals(sg.getHint())) {
                JLabel hl = new JLabel("<html><b>[?]</b></html>");
                hl.setBorder(new EmptyBorder(0, 4, 2, 2));
                hl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                final String about = sg.getHint();
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
            if (!"".equals(sg.getHint())) {
                c.gridwidth = 3;
            } else {
                c.gridwidth = 2;
            }
            c.weightx = 1;
            c.gridy++;
            c.gridx = 0;
            labelPanel.add(serviceGroup, c);
            parent.add(labelPanel, cg);
            cg.gridy++;
        }
    }

    private int getFormFromGroup(JPanel parent, ServicesGroup sg) {
        int ret = 0;
        parent.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        GridBagConstraints cg = new GridBagConstraints();
        cg.gridx = 0;
        cg.gridy = 0;
        cg.fill = GridBagConstraints.HORIZONTAL;
        cg.weightx = 1.0;
        cg.gridwidth = 1;
        parent.setLayout(new GridBagLayout());

        boolean first = true;
        for (Service s : sg.getServices().values()) {
            JPanel service = new JPanel();
            boolean srvNotEmpty = false;
            if (getFormFromService(service, s) > 0) {
                ret++;
            } else {
                continue;
            }
            JPanel labelPanel = new JPanel();
            labelPanel.setLayout(new GridBagLayout());
            labelPanel.setBorder(new EmptyBorder(first ? 10 : 5, 5, 5, 5));
            first = false;
            GridBagConstraints c = new GridBagConstraints();
            JLabel mm = new JLabel("<html><b>[~]</b></html>\"");
            mm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            mm.setBorder(new EmptyBorder(0, 4, 2, 2));
            final JPanel lnk = service;
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
            JLabel gl = new JLabel("<html><b>" + s.getName() + "</b></html>");
            gl.setBorder(new EmptyBorder(0, 4, 2, 0));
            labelPanel.add(gl, c);
            if (!"".equals(s.getHint())) {
                JLabel hl = new JLabel("<html><b>[?]</b></html>");
                hl.setBorder(new EmptyBorder(0, 4, 2, 2));
                hl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                final String about = s.getHint();
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
            if (!"".equals(s.getHint())) {
                c.gridwidth = 3;
            } else {
                c.gridwidth = 2;
            }
            c.weightx = 1;
            c.gridy++;
            c.gridx = 0;
            labelPanel.add(service, c);
            parent.add(labelPanel, cg);
            cg.gridy++;
        }
        return ret;
    }

    private int getFormFromService(JPanel parent, Service s) {
        int ret = 0;
        parent.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        GridBagConstraints cg = new GridBagConstraints();
        cg.gridx = 0;
        cg.gridy = 0;
        cg.fill = GridBagConstraints.HORIZONTAL;
        cg.weightx = 1.0;
        cg.gridwidth = 1;
        parent.setLayout(new GridBagLayout());

        boolean first = true;
        for (com.compassplus.proposalModel.Service r : proposal.getPSQuote().getServices().values()) {
            if (!r.getService().getKey().equals(s.getKey())) {
                continue;
            } else {
                ret++;
            }
            JPanel recommendation = new JPanel();
            getFormFromRecommendation(recommendation, r);
            JPanel labelPanel = new JPanel();
            labelPanel.setLayout(new GridBagLayout());
            labelPanel.setBorder(new EmptyBorder(first ? 10 : 5, 5, 5, 5));
            first = false;
            GridBagConstraints c = new GridBagConstraints();
            JLabel mm = new JLabel("<html><b>[~]</b></html>\"");
            mm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            mm.setBorder(new EmptyBorder(0, 4, 2, 2));
            final JPanel lnk = recommendation;
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
            JLabel gl = new JLabel("<html><b>" + r.getName() + "</b></html>");
            gl.setBorder(new EmptyBorder(0, 4, 2, 0));
            labelPanel.add(gl, c);
            if (!"".equals(r.getHint())) {
                JLabel hl = new JLabel("<html><b>[?]</b></html>");
                hl.setBorder(new EmptyBorder(0, 4, 2, 2));
                hl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                final String about = r.getHint();
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
            if (!"".equals(r.getHint())) {
                c.gridwidth = 3;
            } else {
                c.gridwidth = 2;
            }
            c.weightx = 1;
            c.gridy++;
            c.gridx = 0;
            labelPanel.add(recommendation, c);
            parent.add(labelPanel, cg);
            cg.gridy++;
        }
        return ret;
    }

    private void getFormFromRecommendation(JPanel parent, com.compassplus.proposalModel.Service s) {

    }

    public JPanel getRoot() {
        return mainPanel;
    }

    private JFrame getFrame() {
        return frame;
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
