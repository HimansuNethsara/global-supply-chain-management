package lk.jiat.global.service;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;

import lk.jiat.global.entity.*;
import lk.jiat.global.exception.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
@DisplayName("Customer Order Service Integration Tests")
class CustomerOrderServiceArquillianTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "lk.jiat.global")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsLibraries(
                        Maven.resolver().loadPomFromFile("pom.xml")
                                .resolve("org.hibernate.orm:hibernate-core")
                                .withTransitivity().asFile()
                )
                .addAsLibraries(
                        Maven.resolver().loadPomFromFile("pom.xml")
                                .resolve("com.mysql:mysql-connector-j")
                                .withTransitivity().asFile()
                );
    }

    @EJB
    private CustomerOrderServiceLocal customerOrderService;

    @EJB
    private CustomerServiceLocal customerService;

    @EJB
    private VendorServiceLocal vendorService;

    @EJB
    private WarehouseServiceLocal warehouseService;

    @EJB
    private InventoryServiceLocal inventoryService;

    @EJB
    private UserServiceLocal userService;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction userTransaction;

    private Customer customer;
    private InventoryItem item;
    private Warehouse warehouse;
    private User user;

    @BeforeEach
    void setUp() throws Exception {
        userTransaction.begin();

        // Create test user
        Role customerRole = em.createQuery("SELECT r FROM Role r WHERE r.roleName = 'CUSTOMER'", Role.class)
                .getResultStream().findFirst().orElse(null);
        if (customerRole == null) {
            customerRole = new Role("CUSTOMER", "Customer Role");
            em.persist(customerRole);
        }

        user = new User("testcustomer", "hash", "customer@test.com", "Test Customer", customerRole);
        em.persist(user);

        customer = new Customer(user, "Sri Lanka");
        em.persist(customer);

        warehouse = warehouseService.createWarehouse("Test WH", "Location", "Sri Lanka", 10000);

        item = inventoryService.createItem(
                "ORDER-SKU-001",
                "Test Item",
                warehouse.getWarehouseId(),
                100,
                10,
                BigDecimal.valueOf(100.0)
        );

        userTransaction.commit();
    }

    @AfterEach
    void tearDown() throws Exception {
        userTransaction.begin();
        em.createQuery("DELETE FROM OrderItem").executeUpdate();
        em.createQuery("DELETE FROM CustomerOrder").executeUpdate();
        em.createQuery("DELETE FROM InventoryItem").executeUpdate();
        em.createQuery("DELETE FROM Customer").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
        em.createQuery("DELETE FROM Warehouse").executeUpdate();
        userTransaction.commit();
    }

    // ========== 1. CREATE ORDER TESTS ==========

    @Test
    @DisplayName("Should create order successfully")
    void createOrder_ShouldSucceed() throws Exception {
        Map<Integer, Integer> items = new HashMap<>();
        items.put(item.getItemId(), 5);

        CustomerOrder order = customerOrderService.createOrder(
                customer.getCustomerId(),
                "Colombo, Sri Lanka",
                "Test order notes",
                items
        );

        assertNotNull(order);
        assertNotNull(order.getOrderId());
        assertEquals("PENDING", order.getStatus());
        assertEquals("Colombo, Sri Lanka", order.getDestination());

        InventoryItem updatedItem = inventoryService.findItemById(item.getItemId()).orElse(null);
        assertNotNull(updatedItem);
        assertEquals(95, updatedItem.getQuantity());

        List<OrderItem> orderItems = customerOrderService.getOrderItems(order.getOrderId());
        assertEquals(1, orderItems.size());
        assertEquals(5, orderItems.get(0).getQuantity());
    }

    @Test
    @DisplayName("Should throw ValidationException when creating order with no items")
    void createOrder_ShouldThrowValidationException_NoItems() {
        Map<Integer, Integer> emptyItems = new HashMap<>();
        assertThrows(ValidationException.class, () ->
                customerOrderService.createOrder(
                        customer.getCustomerId(),
                        "Colombo",
                        "No items",
                        emptyItems
                )
        );
    }

    @Test
    @DisplayName("Should throw InsufficientInventoryException when not enough stock")
    void createOrder_ShouldThrowInsufficientInventoryException() throws Exception {
        Map<Integer, Integer> items = new HashMap<>();
        items.put(item.getItemId(), 200);

        assertThrows(InsufficientInventoryException.class, () ->
                customerOrderService.createOrder(
                        customer.getCustomerId(),
                        "Colombo",
                        "Too many items",
                        items
                )
        );
    }

    // ========== 2. GET ORDERS TESTS ==========

    @Test
    @DisplayName("Should get all orders for a customer")
    void getOrdersForCustomer_ShouldReturnOrders() throws Exception {
        Map<Integer, Integer> items = new HashMap<>();
        items.put(item.getItemId(), 2);

        customerOrderService.createOrder(customer.getCustomerId(), "Destination 1", null, items);
        customerOrderService.createOrder(customer.getCustomerId(), "Destination 2", null, items);

        List<CustomerOrder> orders = customerOrderService.getOrdersForCustomer(customer.getCustomerId());

        assertEquals(2, orders.size());
        assertTrue(orders.stream().anyMatch(o -> "Destination 1".equals(o.getDestination())));
        assertTrue(orders.stream().anyMatch(o -> "Destination 2".equals(o.getDestination())));
    }

    @Test
    @DisplayName("Should find order by ID")
    void findOrderById_ShouldReturnOrder() throws Exception {
        Map<Integer, Integer> items = new HashMap<>();
        items.put(item.getItemId(), 3);
        CustomerOrder created = customerOrderService.createOrder(
                customer.getCustomerId(),
                "Find by ID",
                null,
                items
        );

        Optional<CustomerOrder> found = customerOrderService.findOrderById(created.getOrderId());

        assertTrue(found.isPresent());
        assertEquals(created.getOrderId(), found.get().getOrderId());
        assertEquals("Find by ID", found.get().getDestination());
    }

    // ========== 3. CANCEL ORDERS TESTS ==========

    @Test
    @DisplayName("Should cancel pending order and restore inventory")
    void cancelOrder_ShouldSucceedAndRestoreInventory() throws Exception {
        Map<Integer, Integer> items = new HashMap<>();
        items.put(item.getItemId(), 10);
        CustomerOrder order = customerOrderService.createOrder(
                customer.getCustomerId(),
                "Cancel Test",
                null,
                items
        );

        InventoryItem initialItem = inventoryService.findItemById(item.getItemId()).orElse(null);
        assertNotNull(initialItem);
        assertEquals(90, initialItem.getQuantity());

        CustomerOrder cancelled = customerOrderService.cancelOrder(
                order.getOrderId(),
                customer.getCustomerId(),
                "Cancelled by customer"
        );

        assertEquals("CANCELLED", cancelled.getStatus());

        InventoryItem restoredItem = inventoryService.findItemById(item.getItemId()).orElse(null);
        assertNotNull(restoredItem);
        assertEquals(100, restoredItem.getQuantity());
    }

    @Test
    @DisplayName("Should throw ValidationException when cancelling non-pending order")
    void cancelOrder_ShouldThrowValidationException_NonPending() throws Exception {
        Map<Integer, Integer> items = new HashMap<>();
        items.put(item.getItemId(), 5);
        CustomerOrder order = customerOrderService.createOrder(
                customer.getCustomerId(),
                "Non-Pending Cancel",
                null,
                items
        );

        // Update order status within a transaction
        userTransaction.begin();
        try {
            order.setStatus("ASSIGNED");
            em.merge(order);
            em.flush();
            userTransaction.commit();
        } catch (Exception e) {
            userTransaction.rollback();
            throw e;
        }

        assertThrows(ValidationException.class, () ->
                customerOrderService.cancelOrder(
                        order.getOrderId(),
                        customer.getCustomerId(),
                        "Should fail"
                )
        );
    }

    // ========== 4. ORDER ITEMS TESTS ==========

    @Test
    @DisplayName("Should get order items")
    void getOrderItems_ShouldReturnItems() throws Exception {
        Map<Integer, Integer> items = new HashMap<>();
        items.put(item.getItemId(), 7);
        CustomerOrder order = customerOrderService.createOrder(
                customer.getCustomerId(),
                "Items Test",
                null,
                items
        );

        List<OrderItem> orderItems = customerOrderService.getOrderItems(order.getOrderId());

        assertEquals(1, orderItems.size());
        assertEquals(item.getItemId(), orderItems.get(0).getItem().getItemId());
        assertEquals(7, orderItems.get(0).getQuantity());
    }

    @Test
    @DisplayName("Should get order item count")
    void getOrderItemCount_ShouldReturnCount() throws Exception {
        Map<Integer, Integer> items = new HashMap<>();
        items.put(item.getItemId(), 3);
        CustomerOrder order = customerOrderService.createOrder(
                customer.getCustomerId(),
                "Count Test",
                null,
                items
        );

        int count = customerOrderService.getOrderItemCount(order.getOrderId());

        assertEquals(1, count);
    }

    // ========== 5. ORDER OWNERSHIP TESTS ==========

    @Test
    @DisplayName("Should verify order ownership")
    void isOrderOwnedByCustomer_ShouldReturnCorrectValue() throws Exception {
        Map<Integer, Integer> items = new HashMap<>();
        items.put(item.getItemId(), 1);
        CustomerOrder order = customerOrderService.createOrder(
                customer.getCustomerId(),
                "Ownership Test",
                null,
                items
        );

        assertTrue(customerOrderService.isOrderOwnedByCustomer(
                order.getOrderId(),
                customer.getCustomerId()
        ));

        assertFalse(customerOrderService.isOrderOwnedByCustomer(
                order.getOrderId(),
                999
        ));
    }

    // ========== 6. STATUS TRANSITION VALIDATION TESTS ==========

    @Test
    @DisplayName("Should validate valid status transitions")
    void isValidStatusTransition_ShouldReturnTrueForValidTransitions() {
        assertTrue(customerOrderService.isValidStatusTransition("PENDING", "ASSIGNED"));
        assertTrue(customerOrderService.isValidStatusTransition("PENDING", "CANCELLED"));
        assertTrue(customerOrderService.isValidStatusTransition("ASSIGNED", "FULFILLED"));
    }

    @Test
    @DisplayName("Should reject invalid status transitions")
    void isValidStatusTransition_ShouldReturnFalseForInvalidTransitions() {
        assertFalse(customerOrderService.isValidStatusTransition("PENDING", "FULFILLED"));
        assertFalse(customerOrderService.isValidStatusTransition("ASSIGNED", "CANCELLED"));
        assertFalse(customerOrderService.isValidStatusTransition("FULFILLED", "ASSIGNED"));
        assertFalse(customerOrderService.isValidStatusTransition(null, "PENDING"));
        assertFalse(customerOrderService.isValidStatusTransition("PENDING", null));
    }

    // ==========  7. ORDER ROLLBACK TEST ==========

    @Test
    @DisplayName("Should rollback order creation on failure")
    void createOrder_ShouldRollbackOnFailure() throws Exception {
        Map<Integer, Integer> items = new HashMap<>();
        items.put(999, 5);

        assertThrows(ResourceNotFoundException.class, () ->
                customerOrderService.createOrder(
                        customer.getCustomerId(),
                        "Rollback Test",
                        null,
                        items
                )
        );

        List<CustomerOrder> orders = customerOrderService.getOrdersForCustomer(customer.getCustomerId());
        assertTrue(orders.isEmpty());
    }
}