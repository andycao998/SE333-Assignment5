package org.example;

import org.example.Amazon.Amazon;
import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ExtraCostForElectronics;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.RegularCost;
import org.example.Amazon.Item;
import org.example.Amazon.ShoppingCart;
import org.example.Amazon.ShoppingCartAdaptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AmazonUnitTest {
    /*
    Partitions
    Methods:
    calculate()
        No direct inputs, but different PriceRules:
            RegularCost - List<Item>
                - null
                - empty list
                - list with null Item
                - list of size = 1
                - list of size > 1
            ExtraCostForElectronics - List<Item>
                - null
                - empty list
                - list with null Item
                - list of size = 1
                - list of size > 1
            DeliveryPrice - List<Item>
                - null
                - empty list
                - list with null Item
                - list of size = 1
                - list of size > 1
            Combinations of PriceRules:
                - RegularCost + ExtraCostForElectronics
                - RegularCost + DeliveryPrice
                - RegularCost + ExtraCostForElectronics + DeliveryPrice
            Output - double


    addToCart(Item item)
        item - Item
        - null Item
        - Item(ItemType type, String name, int quantity, double pricePerUnit)
            - null ItemType
            - ItemType.OTHER
            - ItemType.ELECTRONIC
            - null String
            - empty String
            - String length = 1
            - String length > 1
            - negative (quantity)
            - zero (quantity)
            - positive (quantity)
            - negative (pricePerUnit)
            - zero (pricePerUnit)
            - positive (pricePerUnit)
        Combinations:
            - null ItemType, null String
            - null ItemType, String
            - null ItemType, empty String
            - ItemType, null String
            - ItemType, String
            - ItemType, empty String
            - quantity and pricePerUnit negative, zero, positive combinations
        Output: none (void)
     */

    // Specification-based Tests (black box)

    @DisplayName("specification-based")
    @Test
    void testAllPriceAggregationsWithNull() {
        ShoppingCart mockedCart = mock(ShoppingCartAdaptor.class);
        when(mockedCart.getItems()).thenReturn(null);

        Amazon amazon = new Amazon(mockedCart, List.of(new RegularCost(), new ExtraCostForElectronics(), new DeliveryPrice()));

        assertThrows(NullPointerException.class, () -> {
            amazon.calculate();
        });
        verify(mockedCart).getItems();
    }

    @DisplayName("specification-based")
    @Test
    void testAllPriceAggregationsWithEmptyList() {
        ShoppingCart mockedCart = mock(ShoppingCartAdaptor.class);
        when(mockedCart.getItems()).thenReturn(new ArrayList<Item>());

        Amazon amazon = new Amazon(mockedCart, List.of(new RegularCost(), new ExtraCostForElectronics(), new DeliveryPrice()));

        assertEquals(0, amazon.calculate());
        verify(mockedCart, times(3)).getItems();
    }

    @DisplayName("specification-based")
    @Test
    void testAllPriceAggregationsWithNullItem() {
        ShoppingCart mockedCart = mock(ShoppingCartAdaptor.class);
        ArrayList<Item> items = new ArrayList<>();
        items.add(null);
        when(mockedCart.getItems()).thenReturn(items);

        Amazon amazon = new Amazon(mockedCart, List.of(new RegularCost(), new ExtraCostForElectronics(), new DeliveryPrice()));

        assertThrows(NullPointerException.class, () -> {
            amazon.calculate();
        });
        verify(mockedCart).getItems();
    }

    @DisplayName("specification-based")
    @Test
    void testRegularCostPriceAggregationWithOneItem() {
        ShoppingCart mockedCart = mock(ShoppingCartAdaptor.class);
        when(mockedCart.getItems()).thenReturn(
                List.of(new Item(ItemType.OTHER, "Chair", 2, 35.5))
        );

        Amazon amazon = new Amazon(mockedCart, List.of(new RegularCost()));

        assertEquals(71, amazon.calculate());
        verify(mockedCart).getItems();
    }

    @DisplayName("specification-based")
    @Test
    void testRegularCostPriceAggregationWithMoreThanOneItem() {
        ShoppingCart mockedCart = mock(ShoppingCartAdaptor.class);
        when(mockedCart.getItems()).thenReturn(
                List.of(
                        new Item(ItemType.OTHER, "Chair", 2, 35.5),
                        new Item(ItemType.OTHER, "Sofa", 1, 300)
                )
        );

        Amazon amazon = new Amazon(mockedCart, List.of(new RegularCost()));

        assertEquals(371, amazon.calculate());
        verify(mockedCart).getItems();
    }

    @DisplayName("specification-based")
    @Test
    void testExtraCostForElectronicsAndRegularCostPriceAggregationWithOneItem() {
        ShoppingCart mockedCart = mock(ShoppingCartAdaptor.class);
        when(mockedCart.getItems()).thenReturn(
                List.of(new Item(ItemType.ELECTRONIC, "Keyboard", 4, 26.0))
        );

        Amazon amazon = new Amazon(mockedCart,
                List.of(new RegularCost(), new ExtraCostForElectronics())
        );

        assertEquals(111.5, amazon.calculate());
        verify(mockedCart, times(2)).getItems();
    }

    @DisplayName("specification-based")
    @Test
    void testExtraCostForElectronicsAndRegularCostPriceAggregationWithMoreThanOneItem() {
        ShoppingCart mockedCart = mock(ShoppingCartAdaptor.class);
        when(mockedCart.getItems()).thenReturn(
                List.of(
                        new Item(ItemType.ELECTRONIC, "Keyboard", 4, 26.0),
                        new Item(ItemType.ELECTRONIC, "TV", 1, 500)
                )
        );

        Amazon amazon = new Amazon(mockedCart,
                List.of(new RegularCost(), new ExtraCostForElectronics())
        );

        assertEquals(611.5, amazon.calculate());
        verify(mockedCart, times(2)).getItems();
    }

    @DisplayName("specification-based")
    @Test
    void testDeliveryPriceAndRegularCostPriceAggregationWithOneItem() {
        ShoppingCart mockedCart = mock(ShoppingCartAdaptor.class);
        when(mockedCart.getItems()).thenReturn(
                List.of(new Item(ItemType.OTHER, "Water Bottle", 3, 15.0))
        );

        Amazon amazon1 = new Amazon(mockedCart,
                List.of(new RegularCost(), new DeliveryPrice())
        );

        assertEquals(50, amazon1.calculate());
        verify(mockedCart, times(2)).getItems();
    }

    @DisplayName("specification-based")
    @Test
    void testDeliveryPriceAndRegularCostPriceAggregationWithMoreThanOneItem() {
        ShoppingCart mockedCart = mock(ShoppingCartAdaptor.class);
        when(mockedCart.getItems()).thenReturn(
                List.of(
                        new Item(ItemType.OTHER, "Water Bottle", 3, 15.0),
                        new Item(ItemType.OTHER, "Toothpaste", 1, 10.0)
                )
        );

        Amazon amazon1 = new Amazon(mockedCart,
                List.of(new RegularCost(), new DeliveryPrice())
        );

        assertEquals(60.0, amazon1.calculate());
        verify(mockedCart, times(2)).getItems();
    }

    @DisplayName("specification-based")
    @Test
    void testDeliveryPriceAndExtraCostForElectronicsAndRegularCostPriceAggregationWithOneItem() {
        ShoppingCart mockedCart = mock(ShoppingCartAdaptor.class);
        when(mockedCart.getItems()).thenReturn(
                List.of(new Item(ItemType.ELECTRONIC, "Computer Monitor", 1, 150.0))
        );

        Amazon amazon1 = new Amazon(mockedCart,
                List.of(new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics())
        );

        assertEquals(162.5, amazon1.calculate());
        verify(mockedCart, times(3)).getItems();
    }

    @DisplayName("specification-based")
    @Test
    void testDeliveryPriceAndExtraCostForElectronicsAndRegularCostPriceAggregationWithMoreThanOneItem() {
        ShoppingCart mockedCart = mock(ShoppingCartAdaptor.class);
        when(mockedCart.getItems()).thenReturn(
                List.of(
                        new Item(ItemType.ELECTRONIC, "Computer Monitor", 1, 150.0),
                        new Item(ItemType.ELECTRONIC, "Phone", 1, 1000.0)
                )
        );

        Amazon amazon1 = new Amazon(mockedCart,
                List.of(new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics())
        );

        assertEquals(1162.5, amazon1.calculate());
        verify(mockedCart, times(3)).getItems();
    }

    @DisplayName("specification-based")
    @Test
    void testAddToCartWithNull() {
        ShoppingCart mockedCart = mock(ShoppingCartAdaptor.class);
        doNothing().when(mockedCart).add(any(Item.class));

        Amazon amazon = new Amazon(mockedCart, List.of(new RegularCost()));
        Item item = null;
        amazon.addToCart(item);

        verify(mockedCart, never()).add(any(Item.class));
    }

    @DisplayName("specification-based")
    @Test
    void testAddToCartWithOneItem() {
        ShoppingCart mockedCart = mock(ShoppingCartAdaptor.class);
        doNothing().when(mockedCart).add(any(Item.class));

        Amazon amazon = new Amazon(mockedCart, List.of(new RegularCost()));

        Item item = new Item(ItemType.ELECTRONIC, "Computer Monitor", 1, 150.0);
        amazon.addToCart(item);

        verify(mockedCart).add(any(Item.class));
    }

    @DisplayName("specification-based")
    @Test
    void testAddToCartWithNullOrEmptyItemDetails() {
        ShoppingCart mockedCart = mock(ShoppingCartAdaptor.class);
        doNothing().when(mockedCart).add(any(Item.class));

        Amazon amazon = new Amazon(mockedCart, List.of(new RegularCost()));

        Item item1 = new Item(null, "Computer Monitor", 1, 150.0);
        Item item2 = new Item(ItemType.ELECTRONIC, null, 1, 150.0);
        Item item3 = new Item(ItemType.ELECTRONIC, "", 1, 150.0);
        amazon.addToCart(item1); // hidden by mock, but this fails in integration test
        amazon.addToCart(item2);
        amazon.addToCart(item3);

        verify(mockedCart, times(3)).add(any(Item.class));
    }

    @DisplayName("specification-based")
    @Test
    void testAddToCartWithVariousQuantitiesAndPrices() {
        ShoppingCart mockedCart = mock(ShoppingCartAdaptor.class);
        doNothing().when(mockedCart).add(any(Item.class));

        Amazon amazon = new Amazon(mockedCart, List.of(new RegularCost()));

        Item item1 = new Item(ItemType.ELECTRONIC, "Computer Monitor", -1, 150.0);
        Item item2 = new Item(ItemType.ELECTRONIC, "Computer Monitor", 0, -150.0);
        Item item3 = new Item(ItemType.ELECTRONIC, "Computer Monitor", 1, 0.0);
        amazon.addToCart(item1);
        amazon.addToCart(item2);
        amazon.addToCart(item3);

        verify(mockedCart, times(3)).add(any(Item.class));
    }

    @DisplayName("structural-based")
    @Test
    void testDeliveryPriceWithGreaterThanThreeItems() {
        // totalItems between 4 and 10
        ShoppingCart mockedCart2 = mock(ShoppingCartAdaptor.class);
        when(mockedCart2.getItems()).thenReturn(
                List.of(
                        new Item(ItemType.OTHER, "Water Bottle", 3, 15.0),
                        new Item(ItemType.OTHER, "Pillow", 1, 7),
                        new Item(ItemType.OTHER, "Socks", 2, 4),
                        new Item(ItemType.OTHER, "Dress Shoes", 1, 100)
                )
        );

        Amazon amazon2 = new Amazon(mockedCart2,
                List.of(new RegularCost(), new DeliveryPrice())
        );

        assertEquals(172.5, amazon2.calculate());
        verify(mockedCart2, times(2)).getItems();


        // totalItems greater than 10
        ShoppingCart mockedCart3 = mock(ShoppingCartAdaptor.class);
        when(mockedCart3.getItems()).thenReturn(
                List.of(
                        new Item(ItemType.OTHER, "Water Bottle", 3, 15.0),
                        new Item(ItemType.OTHER, "Pillow", 1, 7),
                        new Item(ItemType.OTHER, "Socks", 2, 4),
                        new Item(ItemType.OTHER, "Dress Shoes", 1, 100),
                        new Item(ItemType.OTHER, "Notebook", 1, 10),
                        new Item(ItemType.OTHER, "Pens", 1, 10),
                        new Item(ItemType.OTHER, "Graph Paper", 1, 10),
                        new Item(ItemType.OTHER, "Jacket", 1, 100),
                        new Item(ItemType.OTHER, "Moisturizer", 1, 10),
                        new Item(ItemType.OTHER, "Book", 1, 10),
                        new Item(ItemType.OTHER, "Backpack", 1, 10)
                )
        );

        Amazon amazon3 = new Amazon(mockedCart3,
                List.of(new RegularCost(), new DeliveryPrice())
        );

        assertEquals(340.0, amazon3.calculate());
        verify(mockedCart3, times(2)).getItems();
    }

    @DisplayName("structural-based")
    @Test
    void testExtraCostForElectronicsWithNonElectronicItem() {
        ShoppingCart mockedCart = mock(ShoppingCartAdaptor.class);
        when(mockedCart.getItems()).thenReturn(
                List.of(
                        new Item(ItemType.OTHER, "Toy", 1, 15.0)
                )
        );

        Amazon amazon1 = new Amazon(mockedCart,
                List.of(new RegularCost(), new ExtraCostForElectronics())
        );

        assertEquals(15.0, amazon1.calculate());
        verify(mockedCart, times(2)).getItems();
    }
}
