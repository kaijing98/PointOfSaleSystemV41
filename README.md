# PointOfSaleSystemV41
# Illustrates the idea of multitier architecture enterprise systems
This version uses Java EE and can be run in Netbeans

Version41 of the Point of Sale System makes use of a three tier distributed system architecture, 
includes a relational database and a Java Persistance API.
We use a command line interface in this case and the model-view-controller framework.
Data Access Object is not required as we use the JPA Entity Manager instead.

All use cases below are now implemented with JPA.

For Cashier:
- Checkout
- Void or Refund
- View My Sale Transactions

For Manager: 
- Create New Staff
- View Staff Details
- Update Staff
- Delete Staff
- Create New Product
- View Project Details
- Update Product
- Delete Product
- View All Products
