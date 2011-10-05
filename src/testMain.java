import com.compassplus.configurationModel.Configuration;
import com.compassplus.proposalModel.Proposal;
import com.compassplus.utils.CommonUtils;

import java.io.FileOutputStream;

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
            config.init(CommonUtils.getInstance().getDocumentFromFile("examples/exampleModel"));
            Proposal proposal = new Proposal(config);
            proposal.init(CommonUtils.getInstance().getDocumentFromFile("examples/exampleProposal"));

            FileOutputStream out = new FileOutputStream("/home/arudin/Desktop/workbook.xls");
            proposal.getWorkbook().write(out);
            out.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
/*        JFrame frame = new JFrame("testForm");
        frame.setContentPane(new testForm().getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);*/
    }

}
