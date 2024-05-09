# Warehouse-Management

#INSTRUCTIONS
There are 3 initialized users
username: client, password: Password!23
username: manager, password: Password!23
username: admin, password: Password!23

Only register and login are accessable without a bearer token
Login with any of the users to get the token in order to access the other endpoints or create a new Client with the register endpoint
(only admin can create users with other Roles via /user url)

Before placing an order you have to make sure that the items exist in the inventory else you will get a message "Products do not exists!" (Create items via /item endpoint)

