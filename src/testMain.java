import com.compassplus.configurationModel.Configuration;
import com.compassplus.gui.MainForm;
import com.compassplus.gui.ProposalForm;
import com.compassplus.proposalModel.Proposal;
import com.compassplus.utils.CommonUtils;
import com.compassplus.utils.DesEncrypter;
import com.compassplus.utils.Logger;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 9/21/11
 * Time: 10:35 AM
 */
public class testMain {
    private static final String defaultEnc = "UTF8";

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception unused) {
                }
                proceed();
            }
        });
    }

    private static void createAndShowGUI(String pwd, JFrame oframe) {
        Configuration config = Configuration.getInstance();
        try {
            try {
                DesEncrypter ds = new DesEncrypter(CommonUtils.getInstance().md5(pwd, defaultEnc), defaultEnc);
                config.init(CommonUtils.getInstance().getDocumentFromString(ds.decrypt(FileUtils.readFileToString(new File("config.exml"), defaultEnc))));
                ds = null;
                pwd = null;
            } catch (Exception e) {
                Logger.getInstance().error(e);
                JOptionPane.showMessageDialog(null, "Broken or expired configuration", "Error", JOptionPane.ERROR_MESSAGE);
                oframe.dispose();
                throw e;
            }

            final MainForm main = new MainForm(config);
            final JFrame frame = oframe;
            frame.setTitle("PCT [" + config.getUserName() + "]");
            main.setFrame(frame);
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (main.checkChanges())
                        frame.dispose();
                }
            });
            main.setExitAction(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    if (main.checkChanges())
                        frame.dispose();
                }
            });
            frame.setJMenuBar(main.getMenu());
            frame.setContentPane(main.getRoot());
            frame.pack();
            frame.setSize(700, 500);
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        } catch (Exception e) {

        }
    }

    private static void proceed() {
        final JFrame frame = new JFrame();

        java.net.URL imageURL = testMain.class.getResource("images/home.png");
        if (imageURL != null) {
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage(imageURL));
        }

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        final JPasswordField pwdField = new JPasswordField();
        final JOptionPane optionPane = new JOptionPane(
                new JComponent[]{new JLabel("Password"), pwdField},
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION);
        final JDialog dialog = new JDialog(frame, "PCT", true);
        dialog.setResizable(false);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dialog.dispose();
                frame.dispose();
            }
        });
        dialog.setContentPane(optionPane);

        dialog.setSize(200, 200);
        dialog.setLocationRelativeTo(null);

        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (optionPane.getValue() != null) {
                    String prop = e.getPropertyName();
                    if (dialog.isVisible()
                            && (e.getSource() == optionPane)
                            && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                        if (optionPane.getValue() instanceof Integer) {
                            int value = (Integer) optionPane.getValue();
                            if (value == JOptionPane.OK_OPTION) {
                                String pwd = new String(pwdField.getPassword());
                                dialog.dispose();
                                createAndShowGUI(pwd, frame);
                            } else if (value == JOptionPane.CANCEL_OPTION) {
                                dialog.dispose();
                                frame.dispose();
                            }
                        }
                        optionPane.setValue(null);
                    }
                }
            }
        });
        dialog.pack();
        dialog.setVisible(true);
    }
}