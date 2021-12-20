/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.datavirtue.nevitium.database.orm;

import com.j256.ormlite.dao.Dao;
import java.util.List;
import com.datavirtue.nevitium.models.inventory.Inventory;
import java.sql.SQLException;

/**
 *
 * @author SeanAnderson
 */
public interface InventoryDaoInterface extends Dao<Inventory, Object> {
}

