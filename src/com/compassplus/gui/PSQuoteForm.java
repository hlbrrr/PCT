package com.compassplus.gui;

import com.compassplus.configurationModel.Recommendation;
import com.compassplus.configurationModel.Service;
import com.compassplus.configurationModel.ServicesGroup;
import com.compassplus.proposalModel.Proposal;
import com.compassplus.proposalModel.TrainingCourse;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

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
    private JScrollPane modulesScroll;
    private DecimalFormat df;
    private java.util.List<CustomJLabel> labelsToUpdate = new ArrayList<CustomJLabel>();
    private java.util.List<CustomJLabel> tcLabelsToUpdate = new ArrayList<CustomJLabel>();
    private java.util.List<CustomJLabel> priceLabels = new ArrayList<CustomJLabel>();

    public PSQuoteForm(Proposal proposal, PCTChangedListener titleUpdater, DecimalFormat df, JFrame frame) {
        this.titleUpdater = titleUpdater;
        this.proposal = proposal;
        this.frame = frame;
        this.df = df;
        mainPanel = new PSQuoteJPanel(this);
        mainPanel.setLayout(new GridBagLayout());
        initForm();
        //titleUpdater.act(this);
    }

    public void recalc(){
        for(CustomJLabel c:labelsToUpdate){
            c.call();
        }
        for(CustomJLabel c:priceLabels){
            c.call();
        }
        titleUpdater.act(this);
    }

    public void recalcTC(){
        for(CustomJLabel c:tcLabelsToUpdate){
            c.call();
        }
        titleUpdater.act(this);
    }

    public void update() {
        mainPanel.removeAll();
        labelsToUpdate.clear();
        tcLabelsToUpdate.clear();
        priceLabels.clear();
        initForm();
        titleUpdater.act(this);
        mainPanel.updateUI();
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
            modulesScroll = new JScrollPane(modules);


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


        if(proposal.getPSQuote().getTrainingCourses().size() > 0){
            Border tpB = BorderFactory.createTitledBorder("Training courses");

            JPanel trainingCoursesPanelWrap = new JPanel(new GridBagLayout());

            JPanel trainingCoursesPanel = new JPanel(new GridBagLayout());
            trainingCoursesPanel.setMinimumSize(new Dimension(0, 0));
            trainingCoursesPanel.setBorder(tpB);


            trainingCoursesPanelWrap.setBorder(new EmptyBorder(5, 5, 5, 5));

            {
                GridBagConstraints c = new GridBagConstraints();
                c.gridy = 0;
                c.weightx = 1;
                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.PAGE_START;
                trainingCoursesPanelWrap.add(trainingCoursesPanel, c);
            }


            GridBagConstraints c = new GridBagConstraints();
            c.gridy = 0;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.PAGE_START;
            Border border = BorderFactory.createMatteBorder(1, 1, 0, 0, Color.black);
            Border lborder = BorderFactory.createMatteBorder(1, 1, 0, 1, Color.black);
            {
                c.gridx = 0;
                JLabel label = new JLabel("Key");
                JPanel panel = new JPanel();
                panel.add(label);
                panel.setBorder(border);
                panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                panel.setPreferredSize(new Dimension(120, 25));
                trainingCoursesPanel.add(panel, c);
            }
            {
                c.gridx++;
                JLabel label = new JLabel("Name");
                JPanel panel = new JPanel();
                panel.add(label);
                panel.setBorder(border);
                panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                panel.setPreferredSize(new Dimension(300, 25));
                trainingCoursesPanel.add(panel, c);
            }
            {
                c.gridx++;
                JLabel label = new JLabel("Days");
                JPanel panel = new JPanel();
                panel.add(label);
                panel.setBorder(border);
                panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                panel.setPreferredSize(new Dimension(50, 25));
                trainingCoursesPanel.add(panel, c);
            }
            {
                c.gridx++;
                JLabel label = new JLabel("Attendees");
                JPanel panel = new JPanel();
                panel.add(label);
                panel.setBorder(border);
                panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                panel.setPreferredSize(new Dimension(100, 25));
                trainingCoursesPanel.add(panel, c);
            }
            {
                c.gridx++;
                JLabel label = new JLabel("Price (p/a)");
                JPanel panel = new JPanel();
                panel.add(label);
                panel.setBorder(border);
                panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                panel.setPreferredSize(new Dimension(100, 25));
                trainingCoursesPanel.add(panel, c);
            }
            {
                c.gridx++;
                JLabel label = new JLabel("Include");
                JPanel panel = new JPanel();
                panel.add(label);
                panel.setBorder(border);
                panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                panel.setPreferredSize(new Dimension(50, 25));
                trainingCoursesPanel.add(panel, c);
            }
            {
                c.gridx++;
                JLabel label = new JLabel("Total");
                JPanel panel = new JPanel();
                panel.add(label);
                panel.setBorder(border);
                panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                panel.setPreferredSize(new Dimension(100, 25));
                trainingCoursesPanel.add(panel, c);
            }
            {
                c.gridx++;
                JLabel label = new JLabel("");
                JPanel panel = new JPanel();
                panel.add(label);
                panel.setBorder(lborder);
                panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                panel.setPreferredSize(new Dimension(100, 25));
                trainingCoursesPanel.add(panel, c);
            }
            int k = 0;
            for(TrainingCourse tc : proposal.getPSQuote().getTrainingCourses().values()){
                k++;
                final TrainingCourse _tc = tc;
                final com.compassplus.configurationModel.TrainingCourse tcTemplate = proposal.getConfig().getTrainingCourses().get(tc.getKey());
                c.gridy++;

                /*if(k == proposal.getPSQuote().getTrainingCourses().size()){
                    border = BorderFactory.createMatteBorder(1, 1, 1, 0, Color.black);
                    lborder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black);
                }*/

                {
                    c.gridx = 0;
                    JLabel label = new JLabel(tcTemplate.getTrainingCourseKey());
                    JPanel panel = new JPanel();
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.LEFT_ALIGNMENT);
                    // panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    trainingCoursesPanel.add(panel, c);
                }
                {
                    c.gridx++;
                    JLabel label = new JLabel(tcTemplate.getName());
                    JPanel panel = new JPanel();
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.LEFT_ALIGNMENT);
                    // panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    trainingCoursesPanel.add(panel, c);
                }
                {
                    c.gridx++;
                    JLabel label = new JLabel(tcTemplate.getLength() + "");
                    JPanel panel = new JPanel();
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    // panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    trainingCoursesPanel.add(panel, c);
                }
                /*{
                    c.gridx++;
                    JLabel label = new JLabel(tc.getAttendees() + "");
                    JPanel panel = new JPanel();
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    // panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    trainingCoursesPanel.add(panel, c);
                }*/
                {
                    c.gridx++;
                    final JSpinner input = new JSpinner(new SpinnerNumberModel(tc.getAttendees(), tcTemplate.getMinAttendees(), tcTemplate.getMaxAttendees(), new Integer(1)));
                    input.addChangeListener(new ChangeListener() {
                        public void stateChanged(ChangeEvent e) {
                            final ChangeEvent ev = e;
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    if (ev.getSource() == input) {
                                        _tc.setAttendees((Integer)input.getValue());
                                        recalcTC();
                                    }
                                }
                            });
                        }
                    });
                    input.setMaximumSize(new Dimension(input.getMaximumSize().width, input.getMinimumSize().height));

                    JPanel panelW = new JPanel();
                    panelW.setLayout(new BoxLayout(panelW, BoxLayout.Y_AXIS));
                    panelW.setBorder(new EmptyBorder(4, 4, 4, 4));
                    panelW.add(input);
                    panelW.setBackground(Color.white);

                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setMinimumSize(new Dimension(panel.getMinimumSize().width, 32));

                    input.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(panelW);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    trainingCoursesPanel.add(panel, c);
                }
                {
                    c.gridx++;
                    StringBuilder sb = new StringBuilder();
                    if (proposal.getCurrency().getSymbol() != null) {
                        sb.append(proposal.getCurrency().getSymbol());
                        sb.append(" ");
                    }
                    sb.append(df.format(tc.getPricePerAttendee()));
                    if (proposal.getCurrency().getSymbol() == null) {
                        sb.append(" ");
                        sb.append(proposal.getCurrency().getName());
                    }

                    JLabel label = new JLabel(sb.toString());
                    JPanel panel = new JPanel();
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    // panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    trainingCoursesPanel.add(panel, c);
                }
                {
                    c.gridx++;

                    JCheckBox cb = new JCheckBox("", tc.getInclude());

                    cb.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent e) {
                            JCheckBox src = (JCheckBox) e.getSource();
                            _tc.setInclude(src.isSelected());

                            recalcTC();
                        }
                    });

                    JPanel panel = new JPanel();
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    cb.setBorder(new EmptyBorder(4, 4, 4, 4));
                    // panel.setPreferredSize(new Dimension(0, 32));
                    cb.setAlignmentX(Component.CENTER_ALIGNMENT);
                    panel.add(cb);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    trainingCoursesPanel.add(panel, c);
                }
                {
                    c.gridx++;
                    CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                        public void act(Object src) {
                            StringBuilder sb = new StringBuilder();
                            if (proposal.getCurrency().getSymbol() != null) {
                                sb.append(proposal.getCurrency().getSymbol());
                                sb.append(" ");
                            }
                            sb.append(df.format(_tc.getCleanPrice()));
                            if (proposal.getCurrency().getSymbol() == null) {
                                sb.append(" ");
                                sb.append(proposal.getCurrency().getName());
                            }

                            ((CustomJLabel) src).setText(sb.toString());
                        }

                        public void setData(String key, Object data) {
                            //To change body of implemented methods use File | Settings | File Templates.
                        }

                        public Object getData(String key) {
                            return null;  //To change body of implemented methods use File | Settings | File Templates.
                        }
                    });
                    label.call();
                    tcLabelsToUpdate.add(label);
                    JPanel panel = new JPanel();
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    //panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    trainingCoursesPanel.add(panel, c);
                }
                {

                    GridBagConstraints c2 = new GridBagConstraints();
                    c2.weightx = 0;
                    c2.gridwidth = 1;
                    c2.gridx = 0;
                    c2.gridy = 0;

                    c.gridx++;
                    JPanel panel = new JPanel(new GridBagLayout());
                    panel.setPreferredSize(new Dimension(0, 32));
                    //panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    // panel.setPreferredSize(new Dimension(0, 32));
                    //panel.add(label);


                    if (tc.isRemovable()) {
                        JLabel hl = new JLabel("<html><b>[Remove]</b></html>");
                        hl.setBorder(new EmptyBorder(0, 4, 2, 2));
                        hl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        final String about = tcTemplate.getHint();
                        hl.addMouseListener(new MouseListener() {
                            public void mouseClicked(MouseEvent e) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        proposal.getPSQuote().delTrainingCourse(tcTemplate.getKey());
                                        Point zu = modulesScroll.getViewport().getViewPosition();
                                        update();
                                        modulesScroll.getViewport().setViewPosition(zu);
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

                        panel.add(hl, c2);
                        c2.gridx++;
                    }

                    if (!"".equals(tcTemplate.getHint())) {
                        JLabel hl = new JLabel("<html><b>[?]</b></html>");
                        hl.setBorder(new EmptyBorder(0, 4, 2, 2));
                        hl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        final String about = tcTemplate.getHint();
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
                        panel.add(hl, c2);
                    }

                    panel.setBorder(lborder);
                    panel.setBackground(Color.white);
                    trainingCoursesPanel.add(panel, c);
                }
            }
            border = BorderFactory.createMatteBorder(1, 1, 1, 0, Color.black);
            lborder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black);

            c.gridy++;
            {
                c.gridx = 0;
                JLabel label = new JLabel("Total");
                label.setBorder(new EmptyBorder(4, 4, 4, 4));
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setBackground(Color.white);
                panel.add(label);
                panel.setBorder(border);
                panel.setPreferredSize(new Dimension(120, 25));
                trainingCoursesPanel.add(panel, c);
            }
            {
                c.gridx++;
                JLabel label = new JLabel("");
                JPanel panel = new JPanel();
                panel.setBackground(Color.white);
                panel.add(label);
                panel.setBorder(border);
                panel.setPreferredSize(new Dimension(300, 25));
                trainingCoursesPanel.add(panel, c);
            }
            {
                c.gridx++;
                JLabel label = new JLabel("");
                JPanel panel = new JPanel();
                panel.setBackground(Color.white);
                panel.add(label);
                panel.setBorder(border);
                panel.setPreferredSize(new Dimension(50, 25));
                trainingCoursesPanel.add(panel, c);
            }
            {
                c.gridx++;
                JLabel label = new JLabel("");
                JPanel panel = new JPanel();
                panel.setBackground(Color.white);
                panel.add(label);
                panel.setBorder(border);
                panel.setPreferredSize(new Dimension(100, 25));
                trainingCoursesPanel.add(panel, c);
            }
            {
                c.gridx++;
                JLabel label = new JLabel("");
                JPanel panel = new JPanel();
                panel.setBackground(Color.white);
                panel.add(label);
                panel.setBorder(border);
                panel.setPreferredSize(new Dimension(100, 25));
                trainingCoursesPanel.add(panel, c);
            }
            {
                c.gridx++;
                JLabel label = new JLabel("");
                JPanel panel = new JPanel();
                panel.setBackground(Color.white);
                panel.add(label);
                panel.setBorder(border);
                panel.setPreferredSize(new Dimension(50, 25));
                trainingCoursesPanel.add(panel, c);
            }
            {
                c.gridx++;
                CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                    public void act(Object src) {
                        StringBuilder sb = new StringBuilder();
                        if (proposal.getCurrency().getSymbol() != null) {
                            sb.append(proposal.getCurrency().getSymbol());
                            sb.append(" ");
                        }
                        sb.append(df.format(proposal.getPSQuote().getTrainingCoursesPrice()));
                        if (proposal.getCurrency().getSymbol() == null) {
                            sb.append(" ");
                            sb.append(proposal.getCurrency().getName());
                        }

                        ((CustomJLabel) src).setText(sb.toString());
                    }

                    public void setData(String key, Object data) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public Object getData(String key) {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }
                });
                label.call();
                tcLabelsToUpdate.add(label);
                label.setBorder(new EmptyBorder(4, 4, 4, 4));
                label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setBackground(Color.white);
                panel.add(label);
                panel.setBorder(border);
                panel.setPreferredSize(new Dimension(100, 25));
                trainingCoursesPanel.add(panel, c);
            }
            {
                c.gridx++;
                JLabel label = new JLabel("");
                JPanel panel = new JPanel();
                panel.setBackground(Color.white);
                panel.add(label);
                panel.setBorder(lborder);
                panel.setPreferredSize(new Dimension(100, 25));
                trainingCoursesPanel.add(panel, c);
            }

            parent.add(trainingCoursesPanelWrap, cg);
            cg.gridy += 1;
        }


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
            //JLabel gl = new JLabel("<html><b>" + sg.getName() + "</b></html>");

            final ServicesGroup _ref = sg;
            final Proposal _pref = proposal;
            CustomJLabel gl = new CustomJLabel(new PCTChangedListener() {
                public void act(Object src) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("<html><b>");
                    sb.append(_ref.getName());
                    if(!proposal.getConfig().isSalesSupport()){
                        sb.append(" (");
                        if (proposal.getCurrency().getSymbol() != null) {
                            sb.append(proposal.getCurrency().getSymbol());
                            sb.append(" ");
                        }
                        sb.append(df.format(_ref.getCleanPrice(_pref.getPSQuote())));
                        if (proposal.getCurrency().getSymbol() == null) {
                            sb.append(" ");
                            sb.append(proposal.getCurrency().getName());
                        }
                        sb.append(")");
                    }
                    sb.append("</b></html>");
                    ((CustomJLabel) src).setText(sb.toString());
                }

                public void setData(String key, Object data) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                public Object getData(String key) {
                    return null;  //To change body of implemented methods use File | Settings | File Templates.
                }
            });
            gl.call();
            priceLabels.add(gl);


            gl.setBorder(new EmptyBorder(0, 4, 2, 0));
            labelPanel.add(gl, c);

            JCheckBox cb = new JCheckBox("Export", proposal.getPSQuote().isExportable(sg.getKey()));

            cb.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    JCheckBox src = (JCheckBox) e.getSource();
                    proposal.getPSQuote().setExportable(src.isSelected(), _ref.getKey());
                }
            });

            c.weightx = 0;
            c.gridx++;
            labelPanel.add(cb, c);

            if (!"".equals(sg.getHint())) {
                JLabel hl = new JLabel("<html><b>[?]</b></html>");
                hl.setBorder(new EmptyBorder(0, 14, 2, 2));
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
                c.gridwidth = 4;
            } else {
                c.gridwidth = 3;
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
            JPanel labelPanel = new JPanel();
            if (getFormFromService(service, s/*, parent, labelPanel*/) > 0) {
                ret++;
            } else {
                continue;
            }
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

            final Service _ref = s;
            final Proposal _pref = proposal;
            CustomJLabel gl = new CustomJLabel(new PCTChangedListener() {
                public void act(Object src) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("<html><b>");
                    sb.append(_ref.getName());
                    if(!proposal.getConfig().isSalesSupport()){
                        sb.append(" (");
                        if (proposal.getCurrency().getSymbol() != null) {
                            sb.append(proposal.getCurrency().getSymbol());
                            sb.append(" ");
                        }
                        sb.append(df.format(_ref.getCleanPrice(_pref.getPSQuote())));
                        if (proposal.getCurrency().getSymbol() == null) {
                            sb.append(" ");
                            sb.append(proposal.getCurrency().getName());
                        }
                        sb.append(")");
                    }
                    sb.append("</b></html>");
                    ((CustomJLabel) src).setText(sb.toString());
                }

                public void setData(String key, Object data) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                public Object getData(String key) {
                    return null;  //To change body of implemented methods use File | Settings | File Templates.
                }
            });
            gl.call();
            priceLabels.add(gl);

            gl.setBorder(new EmptyBorder(0, 4, 2, 0));
            labelPanel.add(gl, c);


            JCheckBox cb = new JCheckBox("Export", proposal.getPSQuote().isExportable(s.getKey()));

            cb.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    JCheckBox src = (JCheckBox) e.getSource();
                    proposal.getPSQuote().setExportable(src.isSelected(), _ref.getKey());
                }
            });

            c.weightx = 0;
            c.gridx++;
            labelPanel.add(cb, c);

            if (!"".equals(s.getHint())) {
                JLabel hl = new JLabel("<html><b>[?]</b></html>");
                hl.setBorder(new EmptyBorder(0, 14, 2, 2));
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
                c.gridwidth = 4;
            } else {
                c.gridwidth = 3;
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

    private int getFormFromService(JPanel parent, Service s/*, JPanel jPanel, Component component*/) {
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

            final com.compassplus.proposalModel.Service _ref = r;
            //JLabel gl = new JLabel(sb.toString());
            CustomJLabel gl = new CustomJLabel(new PCTChangedListener() {
                public void act(Object src) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("<html><b>");
                    sb.append(_ref.getName());
                    if(!proposal.getConfig().isSalesSupport()){
                        sb.append(" (");
                        if (proposal.getCurrency().getSymbol() != null) {
                            sb.append(proposal.getCurrency().getSymbol());
                            sb.append(" ");
                        }
                        sb.append(df.format(_ref.getCleanPrice()));
                        if (proposal.getCurrency().getSymbol() == null) {
                            sb.append(" ");
                            sb.append(proposal.getCurrency().getName());
                        }
                        sb.append(")");
                    }
                    sb.append("</b></html>");
                    ((CustomJLabel) src).setText(sb.toString());
                }

                public void setData(String key, Object data) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                public Object getData(String key) {
                    return null;  //To change body of implemented methods use File | Settings | File Templates.
                }
            });
            gl.call();
            priceLabels.add(gl);

            gl.setBorder(new EmptyBorder(0, 4, 2, 0));
            labelPanel.add(gl, c);

            JCheckBox cb = new JCheckBox("Charge M/D", r.getCharge());
            cb.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    JCheckBox src = (JCheckBox) e.getSource();
                    _ref.setCharge(src.isSelected());
                    recalc();
                }
            });
            c.weightx = 0;
            c.gridx++;
            labelPanel.add(cb, c);

            {
                JLabel hl = new JLabel("<html><b>[Move up]</b></html>");
                hl.setBorder(new EmptyBorder(0, 14, 2, 2));
                hl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                final String about = r.getHint();
                hl.addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent e) {
                        proposal.getPSQuote().moveServiceUp(_ref.getKey());
                        Point zu = modulesScroll.getViewport().getViewPosition();
                        update();
                        modulesScroll.getViewport().setViewPosition(zu);
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

            {
                JLabel hl = new JLabel("<html><b>[Move down]</b></html>");
                hl.setBorder(new EmptyBorder(0, 4, 2, 2));
                hl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                final String about = r.getHint();
                hl.addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent e) {
                        proposal.getPSQuote().moveServiceDown(_ref.getKey());
                        Point zu = modulesScroll.getViewport().getViewPosition();
                        update();
                        modulesScroll.getViewport().setViewPosition(zu);
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

            if (!r.isRecommended()) {
                JLabel hl = new JLabel("<html><b>[Remove]</b></html>");
                hl.setBorder(new EmptyBorder(0, 4, 2, 2));
                hl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                final String about = r.getHint();
                /*final JPanel _jPanel = jPanel;
                final Component _component = component;*/
                hl.addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent e) {
                        proposal.getPSQuote().delService(_ref.getKey());
                        Point zu = modulesScroll.getViewport().getViewPosition();
                        update();
                        modulesScroll.getViewport().setViewPosition(zu);
                        //_jPanel.remove(_component);
                        //getRoot().updateUI();
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
                c.gridwidth = 6;
            } else {
                c.gridwidth = 5;
            }
            if (!r.isRecommended()) {
                c.gridwidth++;
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

    private void getFormFromRecommendationNew(JPanel parent, com.compassplus.proposalModel.Service s) {
        final com.compassplus.proposalModel.Service _ref = s;
        final DecimalFormat df = new DecimalFormat("#");
        JPanel settingsWrap = new JPanel();
        parent.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));
        //JScrollPane scroll = new JScrollPane(settingsWrap);
        parent.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        parent.setBorder(new EmptyBorder(4, 0, 4, 0));
        //parent.add(settingsWrap, BorderLayout.CENTER);

        settingsWrap.setLayout(new GridBagLayout());
        JPanel productsTable = new JPanel(new GridBagLayout());
        productsTable.setMinimumSize(new Dimension(0, 0));
        productsTable.setBorder(new EmptyBorder(0, 0, 4, 0));
        {
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = 1;
            c.gridx = 0;
            c.weightx = 1;
            c.weighty = 1;
            c.gridwidth = 1;
            parent.add(productsTable, c);
        }
        {
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = 0;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.PAGE_START;
            Border border = BorderFactory.createMatteBorder(1, 1, 0, 0, Color.black);
            Border lborder = BorderFactory.createMatteBorder(1, 1, 0, 1, Color.black);
            {
                c.gridx = 0;
                JLabel label = new JLabel("Recommended");
                JPanel panel = new JPanel();
                panel.add(label);
                panel.setBorder(border);
                panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                panel.setPreferredSize(new Dimension(100, 25));
                productsTable.add(panel, c);
            }
            {
                c.gridx++;
                JLabel label = new JLabel("Increment");
                JPanel panel = new JPanel();
                panel.add(label);
                panel.setBorder(lborder);
                panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                panel.setPreferredSize(new Dimension(100, 25));
                productsTable.add(panel, c);
            }
            {
                c.gridx++;
                JLabel label = new JLabel("Substitute");
                JPanel panel = new JPanel();
                panel.add(label);
                panel.setBorder(lborder);
                panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                panel.setPreferredSize(new Dimension(100, 25));
                productsTable.add(panel, c);
            }
            {
                c.gridx++;
                JLabel label = new JLabel("Total");
                JPanel panel = new JPanel();
                panel.add(label);
                panel.setBorder(lborder);
                panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                panel.setPreferredSize(new Dimension(100, 25));
                productsTable.add(panel, c);
            }
            /*if(s.isRecommended())*/{
                c.gridy++;
                // second row
                {
                    c.gridx = 0;
                    CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                        public void act(Object src) {
                            ((CustomJLabel) src).setText(df.format(_ref.getMDRecommendationValue()));
                        }

                        public void setData(String key, Object data) {
                            //To change body of implemented methods use File | Settings | File Templates.
                        }

                        public Object getData(String key) {
                            return null;  //To change body of implemented methods use File | Settings | File Templates.
                        }
                    });
                    label.call();
                    labelsToUpdate.add(label);
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    //panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(lborder);
                    panel.setBackground(Color.white);
                    productsTable.add(panel, c);
                }

                //c.gridy++;
                //third row
                {
                    c.gridx++;
                    final JSpinner input = new JSpinner(new SpinnerNumberModel(_ref.getIncrement().doubleValue(), 0d, 10000d, 1d));
                    input.addChangeListener(new ChangeListener() {
                        public void stateChanged(ChangeEvent e) {
                            final ChangeEvent ev = e;
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    if (ev.getSource() == input) {
                                        _ref.setIncrement((Double)input.getValue());
                                        recalc();
                                    }
                                }
                            });
                        }
                    });
                    input.setMaximumSize(new Dimension(input.getMaximumSize().width, input.getMinimumSize().height));

                    JPanel panelW = new JPanel();
                    panelW.setLayout(new BoxLayout(panelW, BoxLayout.Y_AXIS));
                    panelW.setBorder(new EmptyBorder(4, 4, 4, 4));
                    panelW.add(input);
                    panelW.setBackground(Color.white);

                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setMinimumSize(new Dimension(panel.getMinimumSize().width, 32));

                    input.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(panelW);
                    panel.setBorder(lborder);
                    panel.setBackground(Color.white);
                    productsTable.add(panel, c);
                }
            }

            //c.gridy++;
            //fourth row
            /*if(!s.isRecommended()){
                border = BorderFactory.createMatteBorder(1, 1, 1, 0, Color.black);
                lborder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black);
            }*/

            {
                c.gridx++;
                final JSpinner input = new JSpinner(new SpinnerNumberModel(_ref.getSubstitute().doubleValue(), 0d, 10000d, 1d));
                input.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        final ChangeEvent ev = e;
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                if (ev.getSource() == input) {
                                    _ref.setSubstitute((Double)input.getValue());
                                    recalc();
                                }
                            }
                        });
                    }
                });
                input.setMaximumSize(new Dimension(input.getMaximumSize().width, input.getMinimumSize().height));

                JPanel panelW = new JPanel();
                panelW.setLayout(new BoxLayout(panelW, BoxLayout.Y_AXIS));
                panelW.setBorder(new EmptyBorder(4, 4, 4, 4));
                panelW.add(input);
                panelW.setBackground(Color.white);

                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setMinimumSize(new Dimension(panel.getMinimumSize().width, 32));

                input.setAlignmentX(Component.RIGHT_ALIGNMENT);
                panel.setPreferredSize(new Dimension(0, 32));
                panel.add(panelW);
                panel.setBorder(lborder);
                panel.setBackground(Color.white);
                productsTable.add(panel, c);
            }

            /*if(s.isRecommended())*/{
                // total row
                border = BorderFactory.createMatteBorder(1, 1, 1, 0, Color.black);
                lborder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black);

                {
                    c.gridx++;
                    CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                        public void act(Object src) {
                            ((CustomJLabel) src).setText(df.format(_ref.getTotalValue()));
                        }

                        public void setData(String key, Object data) {
                            //To change body of implemented methods use File | Settings | File Templates.
                        }

                        public Object getData(String key) {
                            return null;  //To change body of implemented methods use File | Settings | File Templates.
                        }
                    });
                    label.call();
                    labelsToUpdate.add(label);
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    //panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(lborder);
                    panel.setBackground(Color.white);
                    productsTable.add(panel, c);
                }

            }
        }
    }

    private void getFormFromRecommendation(JPanel parent, com.compassplus.proposalModel.Service s) {
        final com.compassplus.proposalModel.Service _ref = s;
        final DecimalFormat df = new DecimalFormat("#");
        JPanel settingsWrap = new JPanel();
        parent.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));
        //JScrollPane scroll = new JScrollPane(settingsWrap);
        parent.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        parent.setBorder(new EmptyBorder(4, 0, 4, 0));
        //parent.add(settingsWrap, BorderLayout.CENTER);

        settingsWrap.setLayout(new GridBagLayout());
        JPanel productsTable = new JPanel(new GridBagLayout());
        productsTable.setMinimumSize(new Dimension(0, 0));
        productsTable.setBorder(new EmptyBorder(0, 0, 4, 0));
        {
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = 1;
            c.gridx = 0;
            c.weightx = 1;
            c.weighty = 1;
            c.gridwidth = 1;
            parent.add(productsTable, c);
        }
        {
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = 0;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.PAGE_START;
            Border border = BorderFactory.createMatteBorder(1, 1, 0, 0, Color.black);
            Border lborder = BorderFactory.createMatteBorder(1, 1, 0, 1, Color.black);
            {
                c.gridx = 0;
                JLabel label = new JLabel(" ");
                JPanel panel = new JPanel();
                panel.add(label);
                panel.setBorder(border);
                panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                panel.setPreferredSize(new Dimension(100, 25));
                productsTable.add(panel, c);
            }
            {
                c.gridx++;
                JLabel label = new JLabel("M/Ds");
                JPanel panel = new JPanel();
                panel.add(label);
                panel.setBorder(lborder);
                panel.setBackground(Color.getHSBColor(294f, 0.03f, 0.7f));
                panel.setPreferredSize(new Dimension(100, 25));
                productsTable.add(panel, c);
            }
            if(s.isRecommended()){
                c.gridy++;
                // second row
                {
                    c.gridx = 0;
                    JLabel label = new JLabel("Recommended");
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    // panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    productsTable.add(panel, c);
                }
                {
                    c.gridx++;
                    CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                        public void act(Object src) {
                            ((CustomJLabel) src).setText(df.format(_ref.getMDRecommendationValue()));
                        }

                        public void setData(String key, Object data) {
                            //To change body of implemented methods use File | Settings | File Templates.
                        }

                        public Object getData(String key) {
                            return null;  //To change body of implemented methods use File | Settings | File Templates.
                        }
                    });
                    label.call();
                    labelsToUpdate.add(label);
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    //panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(lborder);
                    panel.setBackground(Color.white);
                    productsTable.add(panel, c);
                }

                c.gridy++;
                //third row
                {
                    c.gridx = 0;
                    JLabel label = new JLabel("Increment");
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    //panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    panel.setPreferredSize(new Dimension(0, 32));
                    productsTable.add(panel, c);
                }
                {
                    c.gridx++;
                    final JSpinner input = new JSpinner(new SpinnerNumberModel(_ref.getIncrement().doubleValue(), 0d, 10000d, 1d));
                    input.addChangeListener(new ChangeListener() {
                        public void stateChanged(ChangeEvent e) {
                            final ChangeEvent ev = e;
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    if (ev.getSource() == input) {
                                        _ref.setIncrement((Double)input.getValue());
                                        recalc();
                                    }
                                }
                            });
                        }
                    });
                    input.setMaximumSize(new Dimension(input.getMaximumSize().width, input.getMinimumSize().height));

                    JPanel panelW = new JPanel();
                    panelW.setLayout(new BoxLayout(panelW, BoxLayout.Y_AXIS));
                    panelW.setBorder(new EmptyBorder(4, 4, 4, 4));
                    panelW.add(input);
                    panelW.setBackground(Color.white);

                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setMinimumSize(new Dimension(panel.getMinimumSize().width, 32));

                    input.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(panelW);
                    panel.setBorder(lborder);
                    panel.setBackground(Color.white);
                    productsTable.add(panel, c);
                }
            }

            c.gridy++;
            //fourth row
            if(!s.isRecommended()){
                border = BorderFactory.createMatteBorder(1, 1, 1, 0, Color.black);
                lborder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black);
            }
            {
                c.gridx = 0;
                JLabel label = new JLabel(s.isRecommended()?"Substitute":"Total");
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                label.setBorder(new EmptyBorder(4, 4, 4, 4));
                label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                //panel.setPreferredSize(new Dimension(0, 32));
                panel.add(label);
                panel.setBorder(border);
                panel.setBackground(Color.white);
                panel.setPreferredSize(new Dimension(0, 32));
                productsTable.add(panel, c);
            }
            {
                c.gridx++;
                final JSpinner input = new JSpinner(new SpinnerNumberModel(_ref.getSubstitute().doubleValue(), 0d, 10000d, 1d));
                input.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        final ChangeEvent ev = e;
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                if (ev.getSource() == input) {
                                    _ref.setSubstitute((Double)input.getValue());
                                    recalc();
                                }
                            }
                        });
                    }
                });
                input.setMaximumSize(new Dimension(input.getMaximumSize().width, input.getMinimumSize().height));

                JPanel panelW = new JPanel();
                panelW.setLayout(new BoxLayout(panelW, BoxLayout.Y_AXIS));
                panelW.setBorder(new EmptyBorder(4, 4, 4, 4));
                panelW.add(input);
                panelW.setBackground(Color.white);

                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setMinimumSize(new Dimension(panel.getMinimumSize().width, 32));

                input.setAlignmentX(Component.RIGHT_ALIGNMENT);
                panel.setPreferredSize(new Dimension(0, 32));
                panel.add(panelW);
                panel.setBorder(lborder);
                panel.setBackground(Color.white);
                productsTable.add(panel, c);
            }

            if(s.isRecommended()){
                c.gridy++;
                // total row
                border = BorderFactory.createMatteBorder(1, 1, 1, 0, Color.black);
                lborder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black);
                {
                    c.gridx = 0;
                    JLabel label = new JLabel("Total");
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    //panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(border);
                    panel.setBackground(Color.white);
                    productsTable.add(panel, c);
                }
                {
                    c.gridx++;
                    CustomJLabel label = new CustomJLabel(new PCTChangedListener() {
                        public void act(Object src) {
                            ((CustomJLabel) src).setText(df.format(_ref.getTotalValue()));
                        }

                        public void setData(String key, Object data) {
                            //To change body of implemented methods use File | Settings | File Templates.
                        }

                        public Object getData(String key) {
                            return null;  //To change body of implemented methods use File | Settings | File Templates.
                        }
                    });
                    label.call();
                    labelsToUpdate.add(label);
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    label.setBorder(new EmptyBorder(4, 4, 4, 4));
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    //panel.setPreferredSize(new Dimension(0, 32));
                    panel.add(label);
                    panel.setBorder(lborder);
                    panel.setBackground(Color.white);
                    productsTable.add(panel, c);
                }

            }
        }
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
                null, spane, "Description", JOptionPane.INFORMATION_MESSAGE);
    }
}
