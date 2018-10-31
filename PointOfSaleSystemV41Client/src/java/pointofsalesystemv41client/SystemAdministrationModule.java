package pointofsalesystemv41client;

import ejb.session.stateless.ProductEntityControllerRemote;
import ejb.session.stateless.StaffEntityControllerRemote;
import entity.ProductEntity;
import entity.StaffEntity;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Scanner;
import util.enumeration.AccessRightEnum;
import util.exception.CreateNewProductException;
import util.exception.DeleteProductException;
import util.exception.DeleteStaffException;
import util.exception.InvalidAccessRightException;
import util.exception.ProductNotFoundException;
import util.exception.StaffNotFoundException;
import util.exception.UpdateProductException;



public class SystemAdministrationModule
{
    private StaffEntityControllerRemote staffEntityControllerRemote;
    private ProductEntityControllerRemote productEntityControllerRemote;
    
    private StaffEntity currentStaffEntity;

    
    
    public SystemAdministrationModule()
    {
    }

    
    
    public SystemAdministrationModule(StaffEntityControllerRemote staffEntityControllerRemote, ProductEntityControllerRemote productEntityControllerRemote, StaffEntity currentStaffEntity) 
    {
        this();
        this.staffEntityControllerRemote = staffEntityControllerRemote;
        this.productEntityControllerRemote = productEntityControllerRemote;
        this.currentStaffEntity = currentStaffEntity;
    }
    
    
    
    public void menuSystemAdministration() throws InvalidAccessRightException
    {
        if(currentStaffEntity.getAccessRightEnum() != AccessRightEnum.MANAGER)
        {
            throw new InvalidAccessRightException("You don't have MANAGER rights to access the system administration module.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** POS System :: System Administration ***\n");
            System.out.println("1: Create New Staff");
            System.out.println("2: View Staff Details");
            System.out.println("3: View All Staffs");
            System.out.println("-----------------------");
            System.out.println("4: Create New Product");
            System.out.println("5: View Product Details");
            System.out.println("6: View All Products");
            System.out.println("-----------------------");
            System.out.println("7: Back\n");
            response = 0;
            
            while(response < 1 || response > 7)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doCreateNewStaff();
                }
                else if(response == 2)
                {
                    doViewStaffDetails();
                }
                else if(response == 3)
                {
                    doViewAllStaffs();
                }
                else if(response == 4)
                {
                    doCreateNewProduct();
                }
                else if(response == 5)
                {
                    doViewProductDetails();
                }
                else if(response == 6)
                {
                    doViewAllProducts();
                }
                else if (response == 7)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 7)
            {
                break;
            }
        }
    }
    
    
    
    private void doCreateNewStaff()
    {
        Scanner scanner = new Scanner(System.in);
        StaffEntity newStaffEntity = new StaffEntity();
        
        System.out.println("*** POS System :: System Administration :: Create New Staff ***\n");
        System.out.print("Enter First Name> ");
        newStaffEntity.setFirstName(scanner.nextLine().trim());
        System.out.print("Enter Last Name> ");
        newStaffEntity.setLastName(scanner.nextLine().trim());
        
        while(true)
        {
            System.out.print("Select Access Right (1: Cashier, 2: Manager)> ");
            Integer accessRightInt = scanner.nextInt();
            
            if(accessRightInt >= 1 && accessRightInt <= 2)
            {
                newStaffEntity.setAccessRightEnum(AccessRightEnum.values()[accessRightInt-1]);
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        scanner.nextLine();
        System.out.print("Enter Username> ");
        newStaffEntity.setUsername(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        newStaffEntity.setPassword(scanner.nextLine().trim());
        
        newStaffEntity = staffEntityControllerRemote.createNewStaff(newStaffEntity);
        System.out.println("New staff created successfully!: " + newStaffEntity.getStaffId() + "\n");
    }
    
    
    
    private void doViewStaffDetails()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        System.out.println("*** POS System :: System Administration :: View Staff Details ***\n");
        System.out.print("Enter Staff ID> ");
        Long staffId = scanner.nextLong();
        
        try
        {
            StaffEntity staffEntity = staffEntityControllerRemote.retrieveStaffByStaffId(staffId);
            System.out.printf("%8s%20s%20s%15s%20s%20s\n", "Staff ID", "First Name", "Last Name", "Access Right", "Username", "Password");
            System.out.printf("%8s%20s%20s%15s%20s%20s\n", staffEntity.getStaffId().toString(), staffEntity.getFirstName(), staffEntity.getLastName(), staffEntity.getAccessRightEnum().toString(), staffEntity.getUsername(), staffEntity.getPassword());         
            System.out.println("------------------------");
            System.out.println("1: Update Staff");
            System.out.println("2: Delete Staff");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if(response == 1)
            {
                doUpdateStaff(staffEntity);
            }
            else if(response == 2)
            {
                doDeleteStaff(staffEntity);
            }
        }
        catch(StaffNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving staff: " + ex.getMessage() + "\n");
        }
    }
    
    
    private void doUpdateStaff(StaffEntity staffEntity)
    {
        Scanner scanner = new Scanner(System.in);        
        String input;
        
        System.out.println("*** POS System :: System Administration :: View Staff Details :: Update Staff ***\n");
        System.out.print("Enter First Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            staffEntity.setFirstName(input);
        }
                
        System.out.print("Enter Last Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            staffEntity.setLastName(input);
        }
        
        while(true)
        {
            System.out.print("Select Access Right (0: No Change, 1: Cashier, 2: Manager)> ");
            Integer accessRightInt = scanner.nextInt();
            
            if(accessRightInt >= 1 && accessRightInt <= 2)
            {
                staffEntity.setAccessRightEnum(AccessRightEnum.values()[accessRightInt-1]);
                break;
            }
            else if (accessRightInt == 0)
            {
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        scanner.nextLine();
        System.out.print("Enter Username (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            staffEntity.setUsername(input);
        }
        
        System.out.print("Enter Password (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            staffEntity.setPassword(input);
        }
                
        try
        {
            staffEntityControllerRemote.updateStaff(staffEntity);
            System.out.println("Staff updated successfully!\n");
        } 
        catch (StaffNotFoundException ex) 
        {
            System.out.println("An error has occurred while updating staff: " + ex.getMessage() + "\n");
        }
    }
    
    
    
    private void doDeleteStaff(StaffEntity staffEntity)
    {
        Scanner scanner = new Scanner(System.in);        
        String input;
        
        System.out.println("*** POS System :: System Administration :: View Staff Details :: Delete Staff ***\n");
        System.out.printf("Confirm Delete Staff %s %s (Staff ID: %d) (Enter 'Y' to Delete)> ", staffEntity.getFirstName(), staffEntity.getLastName(), staffEntity.getStaffId());
        input = scanner.nextLine().trim();
        
        if(input.equals("Y"))
        {
            try 
            {
                staffEntityControllerRemote.deleteStaff(staffEntity.getStaffId());
                System.out.println("Staff deleted successfully!\n");
            }
            catch (StaffNotFoundException | DeleteStaffException ex) 
            {
                System.out.println("An error has occurred while deleting staff: " + ex.getMessage() + "\n");
            }
        }
        else
        {
            System.out.println("Staff NOT deleted!\n");
        }
    }
    
    
    
    private void doViewAllStaffs()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** POS System :: System Administration :: View All Staffs ***\n");
        
        List<StaffEntity> staffEntities = staffEntityControllerRemote.retrieveAllStaffs();
        System.out.printf("%8s%20s%20s%15s%20s%20s\n", "Staff ID", "First Name", "Last Name", "Access Right", "Username", "Password");

        for(StaffEntity staffEntity:staffEntities)
        {
            System.out.printf("%8s%20s%20s%15s%20s%20s\n", staffEntity.getStaffId().toString(), staffEntity.getFirstName(), staffEntity.getLastName(), staffEntity.getAccessRightEnum().toString(), staffEntity.getUsername(), staffEntity.getPassword());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    
    
    private void doCreateNewProduct()
    {
        Scanner scanner = new Scanner(System.in);
        ProductEntity newProductEntity = new ProductEntity();
        
        System.out.println("*** POS System :: System Administration :: Create New Product ***\n");
        System.out.print("Enter SKU Code> ");
        newProductEntity.setSkuCode(scanner.nextLine().trim());
        System.out.print("Enter Name> ");
        newProductEntity.setName(scanner.nextLine().trim());
        System.out.print("Enter Description> ");
        newProductEntity.setDescription(scanner.nextLine().trim());
        System.out.print("Enter Quantity On Hand> ");
        newProductEntity.setQuantityOnHand(scanner.nextInt());        
        System.out.print("Enter Unit Price> $");
        newProductEntity.setUnitPrice(scanner.nextBigDecimal());
        scanner.nextLine();
        System.out.print("Enter Category> ");
        newProductEntity.setCategory(scanner.nextLine().trim());
        
        try
        {
            newProductEntity = productEntityControllerRemote.createNewProduct(newProductEntity);
            
            System.out.println("New product created successfully!: " + newProductEntity.getProductId() + "\n");
        }
        catch(CreateNewProductException ex)
        {
            System.out.println("An error has occurred while creating the new product: " + ex.getMessage() + "\n");
        }
    }
    
    
    
    private void doViewProductDetails()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        System.out.println("*** POS System :: System Administration :: View Product Details ***\n");
        System.out.print("Enter SKU Code> ");
        String skuCode = scanner.nextLine().trim();
        
        try
        {
            ProductEntity productEntity = productEntityControllerRemote.retrieveProductByProductSkuCode(skuCode);
            System.out.printf("%10s%20s%20s%20s%13s%20s\n", "SKU Code", "Name", "Description", "Quantity On Hand", "Unit Price", "Category");
            System.out.printf("%10s%20s%20s%20d%13s%20s\n", productEntity.getSkuCode(), productEntity.getName(), productEntity.getDescription(), productEntity.getQuantityOnHand(), NumberFormat.getCurrencyInstance().format(productEntity.getUnitPrice()), productEntity.getCategory());
            System.out.println("------------------------");
            System.out.println("1: Update Product");
            System.out.println("2: Delete Product");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if(response == 1)
            {
                doUpdateProduct(productEntity);
            }
            else if(response == 2)
            {
                doDeleteProduct(productEntity);
            }
        }
        catch(ProductNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving product: " + ex.getMessage() + "\n");
        }
    }
    
    
    
    private void doUpdateProduct(ProductEntity productEntity)
    {
        Scanner scanner = new Scanner(System.in);        
        String input;
        Integer integerInput;
        BigDecimal bigDecimalInput;
        
        System.out.println("*** POS System :: System Administration :: View Product Details :: Update Product ***\n");
        System.out.print("Enter SKU Code (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            productEntity.setSkuCode(input);
        }
        
        System.out.print("Enter Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            productEntity.setName(input);
        }
        
        System.out.print("Enter Description (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            productEntity.setDescription(input);
        }
        
        System.out.print("Enter Quantity On Hand (negative number if no change)> ");
        integerInput = scanner.nextInt();
        if(integerInput >= 0)
        {
            productEntity.setQuantityOnHand(integerInput);
        }                
        
        System.out.print("Enter Unit Price (zero if no change)> $");
        bigDecimalInput = scanner.nextBigDecimal();
        if(bigDecimalInput.compareTo(BigDecimal.ZERO) > 0)
        {
            productEntity.setUnitPrice(bigDecimalInput);
        }
        
        scanner.nextLine();
        
        System.out.print("Enter Category (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            productEntity.setCategory(input);
        }
        
        try
        {
            productEntityControllerRemote.updateProduct(productEntity);
            System.out.println("Product updated successfully!\n");
        }
        catch (ProductNotFoundException | UpdateProductException ex) 
        {
            System.out.println("An error has occurred while updating product: " + ex.getMessage() + "\n");
        }
    }
    
    
    
    private void doDeleteProduct(ProductEntity productEntity)
    {
        Scanner scanner = new Scanner(System.in);     
        String input;
        
        System.out.println("*** POS System :: System Administration :: View Product Details :: Delete Product ***\n");
        System.out.printf("Confirm Delete Product %s (SKU Code: %s) (Enter 'Y' to Delete)> ", productEntity.getName(), productEntity.getSkuCode());
        input = scanner.nextLine().trim();
        
        if(input.equals("Y"))
        {
            try 
            {
                productEntityControllerRemote.deleteProduct(productEntity.getProductId());
                System.out.println("Product deleted successfully!\n");
            } 
            catch (ProductNotFoundException | DeleteProductException ex) 
            {
                System.out.println("An error has occurred while deleting product: " + ex.getMessage() + "\n");
            }
        }
        else
        {
            System.out.println("Product NOT deleted!\n");
        }
    }
    
    
    
    private void doViewAllProducts()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** POS System :: System Administration :: View All Products ***\n");
        
        List<ProductEntity> productEntities = productEntityControllerRemote.retrieveAllProducts();
        System.out.printf("%10s%20s%20s%20s%13s%20s\n", "SKU Code", "Name", "Description", "Quantity On Hand", "Unit Price", "Category");

        for(ProductEntity productEntity:productEntities)
        {
            System.out.printf("%10s%20s%20s%20d%13s%20s\n", productEntity.getSkuCode(), productEntity.getName(), productEntity.getDescription(), productEntity.getQuantityOnHand(), NumberFormat.getCurrencyInstance().format(productEntity.getUnitPrice()), productEntity.getCategory());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
}