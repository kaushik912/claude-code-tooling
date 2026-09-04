package com.example.agenttesting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "ControllerFlow")
public class ControllerFlowController {

    @GetMapping("/controller-flow")
    @Operation(summary = "Resolves a shipping label for an order; NPE if the order has no address on file")
    public String resolveShippingLabel(@RequestParam(required = false) String orderId) {
        Order order = OrderStore.lookup(orderId);
        return OrderFlow.buildShippingLabel(order);
    }

    static class Order {
        String id;
        Customer customer;

        Order(String id, Customer customer) {
            this.id = id;
            this.customer = customer;
        }
    }

    static class Customer {
        String name;
        Address address;

        Customer(String name, Address address) {
            this.name = name;
            this.address = address;
        }
    }

    static class Address {
        String city;
        String zip;

        Address(String city, String zip) {
            this.city = city;
            this.zip = zip;
        }
    }

    static class OrderStore {
        static Order lookup(String orderId) {
            if (orderId == null || orderId.equals("guest")) {
                // Guest checkouts are stored without a shipping address on file.
                return new Order(orderId, new Customer("Guest", null));
            }
            return new Order(orderId, new Customer("Jane Doe", new Address("Springfield", "00000")));
        }
    }

    static class OrderFlow {
        static String buildShippingLabel(Order order) {
            if (order == null) {
                return "no order found";
            }
            Customer customer = order.customer;
            if (customer == null) {
                return "no customer on file";
            }
            Address address = customer.address;
            if (address == null) {
                return customer.name + " -> no address on file";
            }
            if (address.city == null) {
                return customer.name + " -> no city on file";
            }
            if (address.zip == null) {
                return customer.name + " -> " + address.city;
            }
            return customer.name + " -> " + address.city + ", " + address.zip;
        }
    }
}
