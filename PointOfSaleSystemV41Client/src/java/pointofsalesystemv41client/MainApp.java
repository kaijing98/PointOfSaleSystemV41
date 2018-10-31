package pointofsalesystemv41client;

import ejb.session.stateful.CheckoutControllerRemote;
import ejb.session.stateless.EmailControllerRemote;
import ejb.session.stateless.ProductEntityControllerRemote;
import ejb.session.stateless.SaleTransactionEntityControllerRemote;
import ejb.session.stateless.StaffEntityControllerRemote;
import entity.StaffEntity;
import java.util.Scanner;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import util.exception.InvalidAccessRightException;
import util.exception.InvalidLoginCredentialException;



public class MainApp
{
    private StaffEntityControllerRemote staffEntityControllerRemote;
    private ProductEntityControllerRemote productEntityControllerRemote;
    private SaleTransactionEntityControllerRemote saleTransactionEntityControllerRemote;
    private CheckoutControllerRemote checkoutControllerRemote;
    private EmailControllerRemote emailControllerRemote;
    
    private Queue queueCheckoutNotification;
    private ConnectionFactory queueCheckoutNotificationFactory;
    
    private CashierOperationModule cashierOperationModule;
    private SystemAdministrationModule systemAdministrationModule;
    
    private StaffEntity currentStaffEntity;
    
    
    
    public MainApp() 
    {        
    }

    
    
    public MainApp(StaffEntityControllerRemote staffEntityControllerRemote, ProductEntityControllerRemote productEntityControllerRemote, SaleTransactionEntityControllerRemote saleTransactionEntityControllerRemote, CheckoutControllerRemote checkoutControllerRemote, EmailControllerRemote emailControllerRemote, Queue queueCheckoutNotification, ConnectionFactory queueCheckoutNotificationFactory) 
    {
        this.staffEntityControllerRemote = staffEntityControllerRemote;
        this.productEntityControllerRemote = productEntityControllerRemote;
        this.saleTransactionEntityControllerRemote = saleTransactionEntityControllerRemote;
        this.checkoutControllerRemote = checkoutControllerRemote;
        this.emailControllerRemote = emailControllerRemote;
        
        this.queueCheckoutNotification = queueCheckoutNotification;
        this.queueCheckoutNotificationFactory = queueCheckoutNotificationFactory;
    }
    
    
    
    public void runApp()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to Point-of-Sale (POS) System (v4.1) ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;
            
            while(response < 1 || response > 2)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    try
                    {
                        doLogin();
                        System.out.println("Login successful!\n");
                        
                        cashierOperationModule = new CashierOperationModule(productEntityControllerRemote, saleTransactionEntityControllerRemote, checkoutControllerRemote, emailControllerRemote, queueCheckoutNotification, queueCheckoutNotificationFactory, currentStaffEntity);
                        systemAdministrationModule = new SystemAdministrationModule(staffEntityControllerRemote, productEntityControllerRemote, currentStaffEntity);
                        menuMain();
                    }
                    catch(InvalidLoginCredentialException ex) 
                    {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                }
                else if (response == 2)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 2)
            {
                break;
            }
        }
    }
    
    
    
    private void doLogin() throws InvalidLoginCredentialException
    {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";
        
        System.out.println("*** POS System :: Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if(username.length() > 0 && password.length() > 0)
        {
            currentStaffEntity = staffEntityControllerRemote.staffLogin(username, password);      
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }
    
    
    
    private void menuMain()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Point-of-Sale (POS) System (v4.1) ***\n");
            System.out.println("You are login as " + currentStaffEntity.getFirstName() + " " + currentStaffEntity.getLastName() + " with " + currentStaffEntity.getAccessRightEnum().toString() + " rights\n");
            System.out.println("1: Cashier Operation");
            System.out.println("2: System Administration");
            System.out.println("3: Logout\n");
            response = 0;
            
            while(response < 1 || response > 3)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    cashierOperationModule.menuCashierOperation();
                }
                else if(response == 2)
                {
                    try
                    {
                        systemAdministrationModule.menuSystemAdministration();
                    }
                    catch (InvalidAccessRightException ex)
                    {
                        System.out.println("Invalid option, please try again!: " + ex.getMessage() + "\n");
                    }
                }
                else if (response == 3)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 3)
            {
                break;
            }
        }
    }
}