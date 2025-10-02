package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i " +
            "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND i.available = true")
    List<Item> searchByNameOrDescription(String text);

    @Query("SELECT i from Item i WHERE i.owner = :userId")
    List<Item> findItemByOwnerId(@Param("userId") Long userId);

    @Query("SELECT i from Item i WHERE i.owner = :userId")
    List<Item> findByOwnerId(@Param("userId") Long userId);

    List<Item> findByRequest(Long requestId);
}
