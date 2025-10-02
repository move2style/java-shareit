package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    private final RequestService requestService;

    //POST /requests
    @PostMapping
    public ItemRequestDto addItemRequest(@RequestBody ItemRequestDto request,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.addRequest(request, userId);
    }

    //GET /requests
    @GetMapping
    public List<ItemRequestDto> getOwnRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getOwnRequests(userId);
    }

    //GET /requests/all
    @GetMapping("/all")
    public List<ItemRequestDto> getOtherUsersRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getOtherUsersRequests(userId);
    }

    //GET /requests/{requestId}
    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable("requestId") Long requestId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getRequestById(requestId, userId);
    }

}
