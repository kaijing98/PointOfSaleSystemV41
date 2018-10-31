package ejb.session.stateless;

import entity.ProductEntity;
import java.util.List;
import util.exception.CreateNewProductException;
import util.exception.DeleteProductException;
import util.exception.ProductNotFoundException;
import util.exception.UpdateProductException;



public interface ProductEntityControllerRemote
{
    ProductEntity createNewProduct(ProductEntity newProductEntity) throws CreateNewProductException;
  
    List<ProductEntity> retrieveAllProducts();

    ProductEntity retrieveProductByProductId(Long productId) throws ProductNotFoundException;

    ProductEntity retrieveProductByProductSkuCode(String skuCode) throws ProductNotFoundException;

    void updateProduct(ProductEntity productEntity) throws ProductNotFoundException, UpdateProductException;
    
    void deleteProduct(Long productId) throws ProductNotFoundException, DeleteProductException;
}
