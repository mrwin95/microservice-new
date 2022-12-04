package com.kenzoo.order.service;

import com.kenzoo.order.dto.InventoryResponse;
import com.kenzoo.order.dto.OrderLineItemsDTO;
import com.kenzoo.order.dto.OrderRequest;
import com.kenzoo.order.model.Order;
import com.kenzoo.order.model.OrderLineItems;
import com.kenzoo.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    public String placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> lineItemsList = orderRequest.getOrderLineItemsList()
                .stream().map(this::mapToDto).toList();
        order.setOrderLineItemList(lineItemsList);

        List<String> skuCodes = order.getOrderLineItemList().stream()
                .map(OrderLineItems::getSkuCode).toList();

        // call inventory service to place order if product is in stock
        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                .uri("lb://inventory-service/api/v1/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                        .retrieve()
                                .bodyToMono(InventoryResponse[].class)
                                        .block();
        boolean allProductsIsInStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);
        log.info(format("The Order with number %s has placed", order.getOrderNumber()));
        if(allProductsIsInStock){
            orderRepository.save(order);
            return "Order place successfully";
        }else {
            throw  new IllegalArgumentException(("product is not in the stock"));
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDTO OrderLineItemsDTO) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setId(OrderLineItemsDTO.getId());
        orderLineItems.setSkuCode(OrderLineItemsDTO.getSkuCode());
        orderLineItems.setPrice(OrderLineItemsDTO.getPrice());
        orderLineItems.setQuantity(OrderLineItemsDTO.getQuantity());
        return orderLineItems;
    }
}
