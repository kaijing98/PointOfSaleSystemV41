package ejb.session.stateless;

import entity.ProductEntity;
import entity.SaleTransactionLineItemEntity;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.CreateNewProductException;
import util.exception.DeleteProductException;
import util.exception.ProductInsufficientQuantityOnHandException;
import util.exception.ProductNotFoundException;
import util.exception.UpdateProductException;



@Stateless
@Local(ProductEntityControllerLocal.class)
@Remote(ProductEntityControllerRemote.class)

public class ProductEntityController implements ProductEntityControllerLocal, ProductEntityControllerRemote
{
    @PersistenceContext(unitName = "PointOfSaleSystemV41-ejbPU")
    private javax.persistence.EntityManager entityManager;
    
    @EJB
    private SaleTransactionEntityControllerLocal saleTransactionEntityControllerLocal;
    
    
    
    public ProductEntityController()
    {
    }
    
    
    
    @Override
    public ProductEntity createNewProduct(ProductEntity newProductEntity) throws CreateNewProductException
    {
        // Updated in v4.1 to check for duplicated SKU code
        
        try
        {
            entityManager.persist(newProductEntity);
            entityManager.flush();

            return newProductEntity;
        }
        catch(PersistenceException ex)
        {
            System.err.println("********** 1: " + ex.getClass().getSimpleName());
            if(ex.getCause() != null && 
                    ex.getCause().getCause() != null &&
                    ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException"))
            {
                throw new CreateNewProductException("Product with same SKU code already exist");
            }
            else
            {
                throw new CreateNewProductException("An unexpected error has occurred: " + ex.getMessage());
            }
        }
        catch(Exception ex)
        {
            System.err.println("********** 3: " + ex.getClass().getSimpleName());
            throw new CreateNewProductException("An unexpected error has occurred: " + ex.getMessage());
        }
    }
    
    
    
    @Override
    public List<ProductEntity> retrieveAllProducts()
    {
        Query query = entityManager.createQuery("SELECT p FROM ProductEntity p ORDER BY p.skuCode ASC");
        
        return query.getResultList();
    }
    
    
    
    @Override
    public ProductEntity retrieveProductByProductId(Long productId) throws ProductNotFoundException
    {
        ProductEntity productEntity = entityManager.find(ProductEntity.class, productId);
        
        if(productEntity != null)
        {
            return productEntity;
        }
        else
        {
            throw new ProductNotFoundException("Product ID " + productId + " does not exist!");
        }               
    }
    
    
    
    // Added in v4.1
    
    @Override
    public ProductEntity retrieveProductByProductSkuCode(String skuCode) throws ProductNotFoundException
    {
        Query query = entityManager.createQuery("SELECT p FROM ProductEntity p WHERE p.skuCode = :inSkuCode");
        query.setParameter("inSkuCode", skuCode);
        
        try
        {
            return (ProductEntity)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new ProductNotFoundException("Sku Code " + skuCode + " does not exist!");
        }
    }
    
    
    
    // Updated in v4.1
    
    @Override
    public void updateProduct(ProductEntity productEntity) throws ProductNotFoundException, UpdateProductException
    {
        if(productEntity.getProductId()!= null)
        {
            ProductEntity productEntityToUpdate = retrieveProductByProductId(productEntity.getProductId());
            
            if(productEntityToUpdate.getSkuCode().equals(productEntity.getSkuCode()))
            {
                productEntityToUpdate.setName(productEntity.getName());
                productEntityToUpdate.setDescription(productEntity.getDescription());
                productEntityToUpdate.setQuantityOnHand(productEntity.getQuantityOnHand());
                productEntityToUpdate.setUnitPrice(productEntity.getUnitPrice());
                productEntityToUpdate.setCategory(productEntity.getCategory());
            }
            else
            {
                throw new UpdateProductException("SKU Code of product record to be updated does not match the existing record");
            }
        }
        else
        {
            throw new ProductNotFoundException("Product ID not provided for product to be updated");
        }
    }
    
    
    
    // Updated in v4.1
    
    @Override
    public void deleteProduct(Long productId) throws ProductNotFoundException, DeleteProductException
    {
        ProductEntity productEntityToRemove = retrieveProductByProductId(productId);
        
        List<SaleTransactionLineItemEntity> saleTransactionLineItemEntities = saleTransactionEntityControllerLocal.retrieveSaleTransactionLineItemsByProductId(productId);
        
        if(saleTransactionLineItemEntities.isEmpty())
        {
            entityManager.remove(productEntityToRemove);
        }
        else
        {
            throw new DeleteProductException("Product ID " + productId + " is associated with existing sale transaction line item(s) and cannot be deleted!");
        }
    }
    
    
    
    // Added in v4.1
    
    @Override
    public void debitQuantityOnHand(Long productId, Integer quantityToDebit) throws ProductNotFoundException, ProductInsufficientQuantityOnHandException
    {
        ProductEntity productEntity = retrieveProductByProductId(productId);
        
        if(productEntity.getQuantityOnHand() >= quantityToDebit)
        {
            productEntity.setQuantityOnHand(productEntity.getQuantityOnHand() - quantityToDebit);
        }
        else
        {
            throw new ProductInsufficientQuantityOnHandException("Product " + productEntity.getSkuCode() + " quantity on hand is " + productEntity.getQuantityOnHand() + " versus quantity to debit of " + quantityToDebit);
        }
    }
    
    
    
    // Added in v4.1
    
    @Override
    public void creditQuantityOnHand(Long productId, Integer quantityToCredit) throws ProductNotFoundException
    {
        ProductEntity productEntity = retrieveProductByProductId(productId);
        productEntity.setQuantityOnHand(productEntity.getQuantityOnHand() + quantityToCredit);
    }
}