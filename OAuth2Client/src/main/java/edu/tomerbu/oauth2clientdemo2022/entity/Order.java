/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tomerbu.oauth2clientdemo2022.entity;

import lombok.NoArgsConstructor;

public record Order(String orderId,
                    String productId,
                    String userId,
                    int quantity,
                    OrderStatus orderStatus) {
}