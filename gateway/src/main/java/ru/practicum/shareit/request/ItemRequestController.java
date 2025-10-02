package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    //POST /requests
    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestBody ItemRequestDto request,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.addRequest(request, userId);
    }

    //GET /requests
    @GetMapping
    public ResponseEntity<Object> getOwnRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getOwnRequests(userId);
    }

    //GET /requests/all
    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getOtherUsersRequests(userId);
    }

    //GET /requests/{requestId}
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable("requestId") Long requestId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getRequestById(requestId, userId);
    }
}
