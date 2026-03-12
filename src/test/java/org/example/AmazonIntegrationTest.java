package org.example;

import org.example.Amazon.*;
import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ExtraCostForElectronics;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.RegularCost;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AmazonIntegrationTest {
    private Database db;
    private ShoppingCartAdaptor cart;

    @BeforeEach
    void setup() {
        db = new Database();
        db.resetDatabase();
        cart = new ShoppingCartAdaptor(db);
    }

    @AfterEach
    void cleanup() {
        db.close();
    }

    @DisplayName("specification-based")
    @Test
    void testAllPriceAggregationsWithNull() {
        assertThrows(NullPointerException.class, () -> {
            cart.add(null);
        });
    }

    @DisplayName("specification-based")
    @Test
    void testAllPriceAggregationsWithEmptyList() {
        Amazon amazon = new Amazon(cart, List.of(new RegularCost(), new ExtraCostForElectronics(), new DeliveryPrice()));

        assertEquals(0, amazon.calculate());
    }

    @DisplayName("specification-based")
    @Test
    void testRegularCostPriceAggregationWithOneItem() {
        cart.add(new Item(ItemType.OTHER, "Chair", 2, 35.5));

        Amazon amazon = new Amazon(cart, List.of(new RegularCost()));

        assertEquals(71, amazon.calculate());
    }

    @DisplayName("specification-based")
    @Test
    void testExtraCostForElectronicsAndRegularCostPriceAggregationWithOneItem() {
        cart.add(new Item(ItemType.ELECTRONIC, "Keyboard", 4, 26.0));

        Amazon amazon = new Amazon(cart,
                List.of(new RegularCost(), new ExtraCostForElectronics())
        );

        assertEquals(111.5, amazon.calculate());
    }

    @DisplayName("specification-based")
    @Test
    void testDeliveryPriceAndRegularCostPriceAggregationWithOneItem() {
        cart.add(new Item(ItemType.OTHER, "Water Bottle", 3, 15.0));

        Amazon amazon1 = new Amazon(cart,
                List.of(new RegularCost(), new DeliveryPrice())
        );

        assertEquals(50, amazon1.calculate());
    }

    @DisplayName("specification-based")
    @Test
    void testDeliveryPriceAndExtraCostForElectronicsAndRegularCostPriceAggregationWithOneItem() {
        cart.add(new Item(ItemType.ELECTRONIC, "Computer Monitor", 1, 150.0));

        Amazon amazon1 = new Amazon(cart,
                List.of(new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics())
        );

        assertEquals(162.5, amazon1.calculate());
    }

    @DisplayName("specification-based")
    @Test
    void testAddToCartWithNull() {
        Amazon amazon = new Amazon(cart, List.of(new RegularCost()));
        Item item = null;

        assertThrows(NullPointerException.class, () -> {
            amazon.addToCart(item);
        });
    }

    @DisplayName("specification-based")
    @Test
    void testAddToCartWithNullOrEmptyItemDetails() {
        Amazon amazon = new Amazon(cart, List.of(new RegularCost()));

        Item item1 = new Item(null, "Computer Monitor", 1, 150.0);
        Item item2 = new Item(ItemType.ELECTRONIC, null, 1, 150.0);
        Item item3 = new Item(ItemType.ELECTRONIC, "", 1, 150.0);

        assertThrows(NullPointerException.class, () -> {
            amazon.addToCart(item1);
        });

        amazon.addToCart(item2);
        amazon.addToCart(item3);

        assertEquals(2, cart.getItems().size()); // cart.numberOfItems() always returns 0 for me
        assertNull(cart.getItems().get(0).getName()); //item2
        assertEquals("", cart.getItems().get(1).getName()); //item3
    }

    @DisplayName("specification-based")
    @Test
    void testAddToCartWithVariousQuantitiesAndPrices() {
        Amazon amazon = new Amazon(cart, List.of(new RegularCost()));

        Item item1 = new Item(ItemType.ELECTRONIC, "Computer Monitor", -1, 150.0);
        Item item2 = new Item(ItemType.ELECTRONIC, "Computer Monitor", 0, -150.0);
        Item item3 = new Item(ItemType.ELECTRONIC, "Computer Monitor", 1, 0.0);
        amazon.addToCart(item1);
        amazon.addToCart(item2);
        amazon.addToCart(item3);

        assertEquals(3, cart.getItems().size());
    }

    @DisplayName("structural-based")
    @Test
    void testDeliveryPriceWithGreaterThanThreeItems() {
        // totalItems between 4 and 10
        cart.add(new Item(ItemType.OTHER, "Water Bottle", 3, 15.0));
        cart.add(new Item(ItemType.OTHER, "Pillow", 1, 7));
        cart.add(new Item(ItemType.OTHER, "Socks", 2, 4));
        cart.add(new Item(ItemType.OTHER, "Dress Shoes", 1, 100));

        Amazon amazon = new Amazon(cart,
                List.of(new RegularCost(), new DeliveryPrice())
        );

        assertEquals(172.5, amazon.calculate());
        assertEquals(4, cart.getItems().size());

        // totalItems greater than 10
        cart.add(new Item(ItemType.OTHER, "Water Bottle", 3, 15.0));
        cart.add(new Item(ItemType.OTHER, "Pillow", 1, 7));
        cart.add(new Item(ItemType.OTHER, "Socks", 2, 4));
        cart.add(new Item(ItemType.OTHER, "Dress Shoes", 1, 100));
        cart.add(new Item(ItemType.OTHER, "Notebook", 1, 10));
        cart.add(new Item(ItemType.OTHER, "Pens", 1, 10));
        cart.add(new Item(ItemType.OTHER, "Graph Paper", 1, 10));

        Amazon amazon2 = new Amazon(cart,
                List.of(new RegularCost(), new DeliveryPrice())
        );

        assertEquals(370.0, amazon2.calculate());
        assertEquals(11, cart.getItems().size());
    }

    @DisplayName("structural-based")
    @Test
    void testExtraCostForElectronicsWithNonElectronicItem() {
        cart.add(new Item(ItemType.OTHER, "Toy", 1, 15.0));

        Amazon amazon = new Amazon(cart,
                List.of(new RegularCost(), new ExtraCostForElectronics())
        );

        assertEquals(15.0, amazon.calculate());
    }
}
