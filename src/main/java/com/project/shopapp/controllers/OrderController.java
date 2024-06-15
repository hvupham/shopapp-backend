package com.project.shopapp.controllers;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.components.SecurityUtils;
import com.project.shopapp.dtos.*;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderStatus;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.order.OrderResponse;
import com.project.shopapp.services.orders.IOrderService;
import com.project.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    private final LocalizationUtils localizationUtils;
    private final SecurityUtils securityUtils;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> createOrder(
            @Valid @RequestBody OrderDTO orderDTO,
            BindingResult result
    ) throws Exception {
        if(result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(String.join(";", errorMessages))
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }
        User loginUser = securityUtils.getLoggedInUser();
        if(orderDTO.getUserId() == null) {
            orderDTO.setUserId(loginUser.getId());
        }
        Order orderResponse = orderService.createOrder(orderDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                        .message("Insert order successfully")
                        .data(orderResponse)
                        .status(HttpStatus.OK)
                        .build());
    }
    @GetMapping("/user/{user_id}") // Thêm biến đường dẫn "user_id"
    //GET http://localhost:8088/api/v1/orders/user/4
    public ResponseEntity<ResponseObject> getOrders(@Valid @PathVariable("user_id") Long userId) {
        User loginUser = securityUtils.getLoggedInUser();
        boolean isUserIdBlank = userId == null || userId <= 0;
        List<OrderResponse> orderResponses = orderService.findByUserId(isUserIdBlank ? loginUser.getId() : userId);
        return ResponseEntity.ok(ResponseObject
                        .builder()
                        .message("Get list of orders successfully")
                        .data(orderResponses)
                        .status(HttpStatus.OK)
                        .build());
    }
    //GET http://localhost:8088/api/v1/orders/2
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getOrder(@Valid @PathVariable("id") Long orderId) {
        Order existingOrder = orderService.getOrderById(orderId);
        OrderResponse orderResponse = OrderResponse.fromOrder(existingOrder);
        return ResponseEntity.ok(new ResponseObject(
                "Get order successfully",
                    HttpStatus.OK,
                    orderResponse
                ));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    //PUT http://localhost:8088/api/v1/orders/2
    //công việc của admin
    public ResponseEntity<ResponseObject> updateOrder(
            @Valid @PathVariable long id,
            @Valid @RequestBody OrderDTO orderDTO) throws Exception {

        Order order = orderService.updateOrder(id, orderDTO);
        return ResponseEntity.ok(new ResponseObject("Update order successfully", HttpStatus.OK, order));
    }

    @PutMapping("/cancel/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> cancelOrder(
            @Valid @PathVariable long id) throws Exception {
        Order order = orderService.getOrderById(id);
        // Kiểm tra xem người dùng hiện tại có phải là người đã đặt đơn hàng hay không
        User loginUser = securityUtils.getLoggedInUser();
        if (loginUser.getId() != order.getUser().getId()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message("You do not have permission to cancel this order")
                    .build());
        }
        if (order.getStatus().equals(OrderStatus.DELIVERED) ||
                order.getStatus().equals(OrderStatus.SHIPPED) ||
                order.getStatus().equals(OrderStatus.PROCESSING)) {

            String message = "You cannot cancel an order with status: " + order.getStatus();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(message)
                    .build());
        }
        OrderDTO orderDTO = OrderDTO.builder()
                .userId(order.getUser().getId())
                /*
                .email(order.getEmail())
                .note(order.getNote())
                .address(order.getAddress())
                .fullName(order.getFullName())
                .totalMoney(order.getTotalMoney())
                .couponCode(order.getCoupon().getCode())
                */
                .status(OrderStatus.CANCELLED)
                .build();;

        order = orderService.updateOrder(id, orderDTO);
        return ResponseEntity.ok(
                new ResponseObject(
                        "Cancel order successfully",
                        HttpStatus.OK,
                        order)
        );
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteOrder(@Valid @PathVariable Long id) {
        //xóa mềm => cập nhật trường active = false
        orderService.deleteOrder(id);
        String message = localizationUtils.getLocalizedMessage(
                MessageKeys.DELETE_ORDER_SUCCESSFULLY, id);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .message(message)
                        .build()
        );
    }
    @GetMapping("/get-orders-by-keyword")
    public ResponseEntity<ResponseObject> getOrdersByKeyword(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                //Sort.by("createdAt").descending()
                Sort.by("id").ascending()
        );
        Page<OrderResponse> orderPage = orderService
                                        .getOrdersByKeyword(keyword, pageRequest)
                                        .map(OrderResponse::fromOrder);
        // Lấy tổng số trang
        int totalPages = orderPage.getTotalPages();
        List<OrderResponse> orderResponses = orderPage.getContent();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get orders successfully")
                .status(HttpStatus.OK)
                .data(orderResponses)
                .build());
    }
}
