package lk.jiat.global.service;

import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;

import lk.jiat.global.entity.InventoryItem;
import lk.jiat.global.entity.Warehouse;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
@DisplayName("Inventory Service Integration Tests")
class InventoryServiceArquillianTest {

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
    private InventoryServiceLocal inventoryService;

    @EJB
    private WarehouseServiceLocal warehouseService;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction userTransaction;

    private Warehouse warehouse;

    @BeforeEach
    void setUp() throws Exception {
        userTransaction.begin();
        warehouse = warehouseService.createWarehouse("Inventory WH", "Location", "Sri Lanka", 10000);
        userTransaction.commit();
    }

    @AfterEach
    void tearDown() throws Exception {
        userTransaction.begin();
        em.createQuery("DELETE FROM InventoryItem").executeUpdate();
        em.createQuery("DELETE FROM Warehouse").executeUpdate();
        userTransaction.commit();
    }

    @Test
    @DisplayName("Should create inventory item successfully")
    void createItem_ShouldSucceed() throws Exception {
        // Act
        InventoryItem item = inventoryService.createItem(
                "SKU-001",
                "Test Item",
                warehouse.getWarehouseId(),
                50,
                10,
                BigDecimal.valueOf(99.99)
        );

        // Assert
        assertNotNull(item);
        assertNotNull(item.getItemId());
        assertEquals("SKU-001", item.getSku());
        assertEquals("Test Item", item.getName());
        assertEquals(50, item.getQuantity());
        assertEquals(10, item.getReorderLevel());
        assertEquals(BigDecimal.valueOf(99.99), item.getUnitPrice());
        assertEquals(warehouse.getWarehouseId(), item.getWarehouse().getWarehouseId());
        assertNotNull(item.getLastUpdated());
    }

    @Test
    @DisplayName("Should throw EJBException for invalid data (null SKU)")
    void createItem_ShouldThrowEJBExceptionForNullSku() {
        assertThrows(EJBException.class, () ->
                inventoryService.createItem(null, null, null, null, null, null)
        );
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException for duplicate SKU")
    void createItem_ShouldThrowDuplicateResourceException() throws Exception {
        // Arrange
        inventoryService.createItem(
                "DUPLICATE-SKU",
                "Item 1",
                warehouse.getWarehouseId(),
                10,
                5,
                BigDecimal.valueOf(100)
        );

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () ->
                inventoryService.createItem(
                        "DUPLICATE-SKU",
                        "Item 2",
                        warehouse.getWarehouseId(),
                        20,
                        5,
                        BigDecimal.valueOf(200)
                )
        );
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid warehouse")
    void createItem_ShouldThrowResourceNotFoundException() {
        assertThrows(ResourceNotFoundException.class, () ->
                inventoryService.createItem(
                        "SKU-999",
                        "Invalid WH Item",
                        999,
                        10,
                        5,
                        BigDecimal.valueOf(100)
                )
        );
    }

    @Test
    @DisplayName("Should find item by ID")
    void findItemById_ShouldReturnItem() throws Exception {
        // Arrange
        InventoryItem created = inventoryService.createItem(
                "FIND-SKU",
                "Find Me",
                warehouse.getWarehouseId(),
                100,
                10,
                BigDecimal.valueOf(50)
        );

        // Act
        InventoryItem found = inventoryService.findItemById(created.getItemId()).orElse(null);

        // Assert
        assertNotNull(found);
        assertEquals(created.getItemId(), found.getItemId());
        assertEquals("FIND-SKU", found.getSku());
    }

    @Test
    @DisplayName("Should find item by SKU")
    void findItemBySku_ShouldReturnItem() throws Exception {
        // Arrange
        String sku = "SKU-FIND-BY-SKU";
        inventoryService.createItem(
                sku,
                "Find by SKU",
                warehouse.getWarehouseId(),
                100,
                10,
                BigDecimal.valueOf(75)
        );

        // Act
        InventoryItem found = inventoryService.findItemBySku(sku).orElse(null);

        // Assert
        assertNotNull(found);
        assertEquals(sku, found.getSku());
    }

    @Test
    @DisplayName("Should find all items")
    void findAllItems_ShouldReturnList() throws Exception {
        // Arrange
        inventoryService.createItem("SKU-1", "Item 1", warehouse.getWarehouseId(), 10, 5, BigDecimal.valueOf(10));
        inventoryService.createItem("SKU-2", "Item 2", warehouse.getWarehouseId(), 20, 5, BigDecimal.valueOf(20));

        // Act
        List<InventoryItem> items = inventoryService.findAllItems();

        // Assert
        assertTrue(items.size() >= 2);
    }

    @Test
    @DisplayName("Should find items by warehouse")
    void findItemsByWarehouse_ShouldReturnWarehouseItems() throws Exception {
        // Arrange
        inventoryService.createItem("WH-SKU-1", "WH Item 1", warehouse.getWarehouseId(), 10, 5, BigDecimal.valueOf(10));
        inventoryService.createItem("WH-SKU-2", "WH Item 2", warehouse.getWarehouseId(), 20, 5, BigDecimal.valueOf(20));

        // Act
        List<InventoryItem> items = inventoryService.findItemsByWarehouse(warehouse.getWarehouseId());

        // Assert
        assertEquals(2, items.size());
    }

    @Test
    @DisplayName("Should find low stock items")
    void findLowStockItems_ShouldReturnLowStockItems() throws Exception {
        // Arrange
        inventoryService.createItem("LOW-1", "Low Item 1", warehouse.getWarehouseId(), 3, 10, BigDecimal.valueOf(10));
        inventoryService.createItem("LOW-2", "Low Item 2", warehouse.getWarehouseId(), 5, 10, BigDecimal.valueOf(20));
        inventoryService.createItem("OK-1", "OK Item", warehouse.getWarehouseId(), 50, 10, BigDecimal.valueOf(30));

        // Act
        List<InventoryItem> lowStockItems = inventoryService.findLowStockItems();

        // Assert
        assertTrue(lowStockItems.size() >= 2);
        assertTrue(lowStockItems.stream().anyMatch(i -> "Low Item 1".equals(i.getName())));
        assertTrue(lowStockItems.stream().anyMatch(i -> "Low Item 2".equals(i.getName())));
    }

    @Test
    @DisplayName("Should update item quantity")
    void updateItem_ShouldUpdateQuantity() throws Exception {
        // Arrange
        InventoryItem item = inventoryService.createItem(
                "UPDATE-SKU",
                "Update Test",
                warehouse.getWarehouseId(),
                50,
                10,
                BigDecimal.valueOf(100)
        );

        // Act
        InventoryItem updated = inventoryService.updateItem(
                item.getItemId(),
                item.getSku(),
                item.getName(),
                item.getWarehouse().getWarehouseId(),
                75,
                item.getReorderLevel(),
                item.getUnitPrice()
        );

        // Assert
        assertEquals(75, updated.getQuantity());
        assertNotNull(updated.getLastUpdated());
    }

    @Test
    @DisplayName("Should delete item")
    void deleteItem_ShouldRemoveItem() throws Exception {
        // Arrange
        InventoryItem item = inventoryService.createItem(
                "DELETE-SKU",
                "Delete Me",
                warehouse.getWarehouseId(),
                10,
                5,
                BigDecimal.valueOf(50)
        );

        // Act
        inventoryService.deleteItem(item.getItemId());

        // Assert
        InventoryItem found = inventoryService.findItemById(item.getItemId()).orElse(null);
        assertNull(found);
    }
}