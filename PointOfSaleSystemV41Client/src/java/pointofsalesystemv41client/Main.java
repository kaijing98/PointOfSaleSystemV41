package pointofsalesystemv41client;

import ejb.session.stateful.CheckoutControllerRemote;
import ejb.session.stateless.EmailControllerRemote;
import ejb.session.stateless.ProductEntityControllerRemote;
import ejb.session.stateless.SaleTransactionEntityControllerRemote;
import ejb.session.stateless.StaffEntityControllerRemote;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;



public class Main
{
    @EJB
    private static StaffEntityControllerRemote staffEntityControllerRemote;
    @EJB
    private static ProductEntityControllerRemote productEntityControllerRemote;
    @EJB
    private static SaleTransactionEntityControllerRemote saleTransactionEntityControllerRemote;
    @EJB
    private static CheckoutControllerRemote checkoutControllerRemote;
    @EJB
    private static EmailControllerRemote emailControllerRemote;
    
    @Resource(mappedName = "jms/queueCheckoutNotification")
    private static Queue queueCheckoutNotification;
    @Resource(mappedName = "jms/queueCheckoutNotificationFactory")
    private static ConnectionFactory queueCheckoutNotificationFactory;
    
    
    
    public static void main(String[] args)
    {
        MainApp mainApp = new MainApp(staffEntityControllerRemote, productEntityControllerRemote, saleTransactionEntityControllerRemote, checkoutControllerRemote, emailControllerRemote, queueCheckoutNotification, queueCheckoutNotificationFactory);
        mainApp.runApp();
    }
}