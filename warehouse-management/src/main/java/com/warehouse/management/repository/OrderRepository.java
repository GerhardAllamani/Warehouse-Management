package com.warehouse.management.repository;

import com.warehouse.management.model.Order;
import com.warehouse.management.model.Status;
import com.warehouse.management.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("""
select o from Order o where o.user.username = :username
""")
    List<Object[]> findOrders(String username);

    @Query("""
select o from Order o where o.user.username = :username and o.status = :status
""")
    List<Object[]> findOrdersByStatus(String username, Status status);

    @Query("""
select o from Order o where o.user.username = :username and o.order_number = :orderNumber
""")
    Order findOrdersByOrderNumber(String username, Integer orderNumber);

    @Query("""
select o from Order o where o.order_number = :orderNumber
""")
    Order findByOrderNumber(Integer orderNumber);

    @Query("SELECT o.status, o.submitted_date FROM Order o order by o.submitted_date desc")
    List<Object[]> getAllOrders();

}