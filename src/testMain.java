import com.compassplus.configurationModel.Configuration;
import com.compassplus.gui.MainForm;
import com.compassplus.proposalModel.Product;
import com.compassplus.proposalModel.Proposal;
import com.compassplus.utils.CommonUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 9/21/11
 * Time: 10:35 AM
 */
public class testMain {

    public static void main(String[] args) {
        Configuration config = Configuration.getInstance();
        try {
            config.init(CommonUtils.getInstance().getDocumentFromFile("examples/exampleModel.xml"));
//            Proposal proposal = new Proposal(config);
//            proposal.init(CommonUtils.getInstance().getDocumentFromFile("examples/exampleProposal.xml"));

            //FileOutputStream out = new FileOutputStream("c:\\Users\\hlbrrr\\Desktop\\workbook.xls");
            //FileOutputStream out = new FileOutputStream("/home/arudin/Desktop/workbook.xls");
            //proposal.getWorkbook().write(out);
            //out.close();

//            for (Product p : proposal.getProducts().values()) {
//                System.out.println(p.getDescription());
//            }

            MainForm main = new MainForm(config);
            final JFrame frame = new JFrame("PCT");
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
