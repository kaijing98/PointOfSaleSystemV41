package ejb.session.stateless;

import entity.StaffEntity;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.DeleteStaffException;
import util.exception.InvalidLoginCredentialException;
import util.exception.StaffNotFoundException;



@Stateless
@Local(StaffEntityControllerLocal.class)
@Remote(StaffEntityControllerRemote.class)

public class StaffEntityController implements StaffEntityControllerLocal, StaffEntityControllerRemote
{

    @PersistenceContext(unitName = "PointOfSaleSystemV41-ejbPU")
    private EntityManager entityManager;
    
    
    
    
    public StaffEntityController()
    {
    }
    
    
    
    @Override
    public StaffEntity createNewStaff(StaffEntity newStaffEntity)
    {
        entityManager.persist(newStaffEntity);
        entityManager.flush();
        
        return newStaffEntity;
    }
    
    
    
    @Override
    public List<StaffEntity> retrieveAllStaffs()
    {
        Query query = entityManager.createQuery("SELECT s FROM StaffEntity s");
        
        return query.getResultList();
    }
    
    
    
    @Override
    public StaffEntity retrieveStaffByStaffId(Long staffId) throws StaffNotFoundException
    {
        StaffEntity staffEntity = entityManager.find(StaffEntity.class, staffId);
        
        if(staffEntity != null)
        {
            return staffEntity;
        }
        else
        {
            throw new StaffNotFoundException("Staff ID " + staffId + " does not exist!");
        }
    }
    
    
    
    @Override
    public StaffEntity retrieveStaffByUsername(String username) throws StaffNotFoundException
    {
        Query query = entityManager.createQuery("SELECT s FROM StaffEntity s WHERE s.username = :inUsername");
        query.setParameter("inUsername", username);
        
        try
        {
            return (StaffEntity)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new StaffNotFoundException("Staff Username " + username + " does not exist!");
        }
    }
    
    
    
    @Override
    public StaffEntity staffLogin(String username, String password) throws InvalidLoginCredentialException
    {
        try
        {
            StaffEntity staffEntity = retrieveStaffByUsername(username);
            
            if(staffEntity.getPassword().equals(password))
            {
                staffEntity.getSaleTransactionEntities().size();                
                return staffEntity;
            }
            else
            {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        }
        catch(StaffNotFoundException ex)
        {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }
    
    
    
    @Override
    public void updateStaff(StaffEntity staffEntity) throws StaffNotFoundException
    {
        // Updated in v4.1 to update selective attributes instead of merging the entire state passed in from the client
        // Also check for existing staff before proceeding with the update
        
        if(staffEntity.getStaffId() != null)
        {
            StaffEntity staffEntityToUpdate = retrieveStaffByStaffId(staffEntity.getStaffId());
            if(staffEntityToUpdate.getUsername().equals(staffEntity.getUsername()))
            {
                staffEntityToUpdate.setFirstName(staffEntity.getFirstName());
                staffEntityToUpdate.setLastName(staffEntity.getLastName());
                staffEntityToUpdate.setAccessRightEnum(staffEntity.getAccessRightEnum());
                staffEntityToUpdate.setUsername(staffEntity.getUsername());
                // Password is deliberately NOT updated to demonstrate that client is not allowed to update password through this business method
            }
        }
        else
        {
            throw new StaffNotFoundException("Staff ID not provided for staff to be updated");
        }
    }
    
    
    
    @Override
    public void deleteStaff(Long staffId) throws StaffNotFoundException, DeleteStaffException
    {
        StaffEntity staffEntityToRemove = retrieveStaffByStaffId(staffId);
        
        if(staffEntityToRemove.getSaleTransactionEntities().isEmpty())
        {
            entityManager.remove(staffEntityToRemove);
        }
        else
        {
            // New in v4.1 to prevent deleting staff with existing sale transaction(s)
            throw new DeleteStaffException("Staff ID " + staffId + " is associated with existing sale transaction(s) and cannot be deleted!");
        }
    }
}