package com.example.bot.repository;

import com.example.bot.entity.Penalty;
import com.example.bot.entity.Roles;
import com.example.bot.entity.Shop;
import com.example.bot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    Shop getByBuyerAndRole(User buyer, Roles role);
    List<Shop> getShopsByBuyer(User buyer);
}
