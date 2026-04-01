/*package com.rejunk.domain.model;

import com.rejunk.domain.enums.ItemCondition;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void testItemProperties() {
        Item item = new Item();
        item.setId(UUID.randomUUID());
        item.setTitle("Board Game");
        item.setCondition(ItemCondition.NEW);
        item.setEvaluatedPrice(new BigDecimal("40.00"));

        assertNotNull(item.getId());
        assertEquals("Board Game", item.getTitle());
        assertEquals(ItemCondition.NEW, item.getCondition());
        assertEquals(new BigDecimal("40.00"), item.getEvaluatedPrice());
    }
}*/

package com.rejunk.domain.model;

import com.rejunk.domain.enums.ItemCondition;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void testItemBuilderAndGetters() {
        // Create a dummy CollectionRequest
        CollectionRequest request = new CollectionRequest();
        request.setId(UUID.randomUUID());

        // Build an Item using the builder
        Item item = Item.builder()
                .collectionRequest(request)
                .title("Test Item")
                .description("This is a test item")
                .condition(ItemCondition.GOOD)
                .evaluatedPrice(new BigDecimal("150.00"))
                .build();

        // Assertions to verify fields
        assertNotNull(item);
        assertEquals("Test Item", item.getTitle());
        assertEquals("This is a test item", item.getDescription());
        assertEquals(ItemCondition.GOOD, item.getCondition());
        assertEquals(new BigDecimal("150.00"), item.getEvaluatedPrice());
        assertEquals(request, item.getCollectionRequest());

        // The id and listing should be null initially
        assertNull(item.getId());
        assertNull(item.getListing());
    }

    @Test
    void testSetters() {
        Item item = new Item();

        CollectionRequest request = new CollectionRequest();
        request.setId(UUID.randomUUID());

        item.setCollectionRequest(request);
        item.setTitle("Another Item");
        item.setDescription("Another description");
        item.setCondition(ItemCondition.FAIR);
        item.setEvaluatedPrice(new BigDecimal("75.50"));

        assertEquals(request, item.getCollectionRequest());
        assertEquals("Another Item", item.getTitle());
        assertEquals("Another description", item.getDescription());
        assertEquals(ItemCondition.FAIR, item.getCondition());
        assertEquals(new BigDecimal("75.50"), item.getEvaluatedPrice());
    }
}