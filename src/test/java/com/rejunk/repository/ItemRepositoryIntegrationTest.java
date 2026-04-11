package com.rejunk.repository;

import com.rejunk.domain.model.CollectionRequest;
import com.rejunk.domain.model.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CollectionRequestRepository collectionRequestRepository;


    // TEST SAVE + FIND BY ID
    @Test
    @DisplayName("Should save item and retrieve it by ID")
    void shouldSaveAndFindItem() {
        // Arrange
        Item item = new Item();
        item.setTitle("Old Laptop");
        item.setDescription("Used but working");

        // Act
        Item savedItem = itemRepository.save(item);

        Optional<Item> foundItem = itemRepository.findById(savedItem.getId());

        // Assert
        assertThat(foundItem).isPresent();
        assertThat(foundItem.get().getTitle()).isEqualTo("Old Laptop");
        assertThat(foundItem.get().getDescription()).isEqualTo("Used but working");
    }

    // TEST DELETE
    @Test
    @DisplayName("Should delete item")
    void shouldDeleteItem() {
        // Arrange
        Item item = new Item();
        item.setTitle("Chair");

        Item savedItem = itemRepository.save(item);

        // Act
        itemRepository.deleteById(savedItem.getId());

        Optional<Item> result = itemRepository.findById(savedItem.getId());

        // Assert
        assertThat(result).isEmpty();
    }
}