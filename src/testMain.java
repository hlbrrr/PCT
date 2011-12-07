import com.compassplus.configurationModel.Configuration;
import com.compassplus.gui.MainForm;
import com.compassplus.utils.CommonUtils;
import com.compassplus.utils.DesEncrypter;
import com.compassplus.utils.Logger;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
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
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception unused) {
        }
        Configuration config = Configuration.getInstance();
        try {
            try {
                String pwd = (String) JOptionPane.showInputDialog(null, "Please enter your password", "PCT", JOptionPane.QUESTION_MESSAGE);
                if (pwd != null) {
                    DesEncrypter ds = new DesEncrypter(CommonUtils.getInstance().md5(pwd, defaultEnc), defaultEnc);
                    config.init(CommonUtils.getInstance().getDocumentFromString(ds.decrypt(FileUtils.readFileToString(new File("config.exml"), defaultEnc))));
                    ds = null;
                    pwd = null;
                } else {
                    throw new Exception("NoPassword");
                }
            } catch (Exception e) {
                Logger.getInstance().error(e);
                if (e.getMessage() == null || !"NoPassword".equals(e.getMessage())) {
                    JOptionPane.showMessageDialog(null, "Broken or expired configuration", "Error", JOptionPane.ERROR_MESSAGE);
                }
                throw e;
            }

            MainForm main = new MainForm(config);
            final JFrame frame = new JFrame("PCT [" + config.getUserName() + "]");
            main.setFrame(frame);
            main.setExitAction(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                }
            });
            frame.setJMenuBar(main.getMenu());
            frame.setContentPane(main.getRoot());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setSize(700, 500);
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        } catch (Exception e) {
        }
    }
}