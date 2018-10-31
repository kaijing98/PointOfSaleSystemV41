package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;



@Entity

public class ProductEntity implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    @Column(unique = true)
    private String skuCode;
    private String name;    
    private String description;    
    private Integer quantityOnHand;
    @Column(precision = 11, scale = 2)
    private BigDecimal unitPrice;    
    private String category;

    
    
    public ProductEntity() 
    {
    }

    
    
    public ProductEntity(String skuCode, String name, String description, Integer quantityOnHand, BigDecimal unitPrice, String category) 
    {
        this.skuCode = skuCode;
        this.name = name;
        this.description = description;
        this.quantityOnHand = quantityOnHand;
        this.unitPrice = unitPrice;
        this.category = category;
    }
    
    
    
    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (this.productId != null ? this.productId.hashCode() : 0);
        
        return hash;
    }

    
    
    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof ProductEntity)) 
        {
            return false;
        }
        
        ProductEntity other = (ProductEntity) object;
        
        if ((this.productId == null && other.productId != null) || (this.productId != null && !this.productId.equals(other.productId))) 
        {
            return false;
        }
        
        return true;
    }

    
    
    @Override
    public String toString() 
    {
        return "entity.ProductEntity[ productId=" + this.productId + " ]";
    }

    
    
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}