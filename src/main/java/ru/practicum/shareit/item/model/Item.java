package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items", schema = "public")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "owner_id")
    Long owner;
    @Column
    String name;
    @Column
    String description;
    @Column
    Boolean available;
    @Column(name = "request_id")
    Long request;
}
