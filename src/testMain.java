import com.compassplus.configurationModel.Configuration;
import com.compassplus.utils.CommonUtils;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 9/21/11
 * Time: 10:35 AM
 */
public class testMain {

    public static void main(String[] args) {
        Configuration main = Configuration.getInstance();
        try {
            //main.init(CommonUtils.getInstance().getDocumentFromFile("d:\\exampleModel"));
            main.init(CommonUtils.getInstance().getDocumentFromFile("/home/arudin/exampleModel"));
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
