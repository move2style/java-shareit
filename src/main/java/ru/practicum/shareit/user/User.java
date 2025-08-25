package ru.practicum.shareit.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {

    Long id;
    @NotNull
    String name;
    @NotNull
    String email;
}
