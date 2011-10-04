import com.compassplus.configurationModel.Configuration;
import com.compassplus.proposalModel.Proposal;
import com.compassplus.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
            //http://poi.apache.org/spreadsheet/how-to.html
            ArrayList<String> asd = new ArrayList<String>();
            //main.init(CommonUtils.getInstance().getDocumentFromFile("d:\\exampleModel"));
            config.init(CommonUtils.getInstance().getDocumentFromFile("examples/exampleModel"));
            System.out.println("\n\n\n");
            Proposal proposal = new Proposal(config);
            proposal.init(CommonUtils.getInstance().getDocumentFromFile("examples/exampleProposal"));
            System.out.println(proposal.toString());
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
