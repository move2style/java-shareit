package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url:http://server:9090}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> findBookingsUserId(Long userId, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name()
        );
        System.out.println("findBookingsUserId");
        return get("?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> createBooking(Long userId, NewBookingRequest requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> findBookingId(Long bookingId, Long userId) {
        System.out.println("findBookingId called with userId=" + userId + ", bookingId=" + bookingId);
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> findBookingsItemUserId(Long userId, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name()
        );
        System.out.println("findBookingsItemUserId called with userId=" + userId + ", bookingId=" + state);
        return get("/owner?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> approvedBooking(Long bookingId, boolean status, Long userId) {
        Map<String, Object> parameters = Map.of(
                "approved", status
        );
        System.out.println("approvedBooking");
        String path = "/" + bookingId + "?approved={approved}";
        return patch(path, userId, parameters, null);
    }
}
