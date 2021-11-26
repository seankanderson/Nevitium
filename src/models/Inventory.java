/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
/**
 *
 * @author SeanAnderson
 */
@DatabaseTable(tableName = "inventory")
public class Inventory {

    
    @DatabaseField(generatedId = true)
    private UUID id; 
    @DatabaseField
    private String upc;
    @DatabaseField
    private String code;
    @DatabaseField(canBeNull = false)
    private String description;
    @DatabaseField
    private String size;
    @DatabaseField
    private String weight;
    @DatabaseField
    private Double quantity;
    @DatabaseField
    private Double cost;
    @DatabaseField
    private Double price;    
    @DatabaseField
    private String category;    
    @DatabaseField
    private boolean tax1;
    @DatabaseField
    private boolean tax2;
    @DatabaseField
    private boolean available;
    @DatabaseField
    private Date lastSale = new Date();
    @DatabaseField
    private Date lastReceived = new Date();
    @DatabaseField
    private int reorderCutoff;
    @DatabaseField
    private boolean partialSaleAllowed;
    
    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the id
     */
    public UUID getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * @return the upc
     */
    public String getUpc() {
        return upc;
    }

    /**
     * @param upc the upc to set
     */
    public void setUpc(String upc) {
        this.upc = upc;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the size
     */
    public String getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * @return the weight
     */
    public String getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(String weight) {
        this.weight = weight;
    }

    /**
     * @return the quantity
     */
    public Double getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the cost
     */
    public Double getCost() {
        return cost;
    }

    /**
     * @param cost the cost to set
     */
    public void setCost(Double cost) {
        this.cost = cost;
    }

    /**
     * @return the price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(Double price) {
        this.price = price;
    }
      
    /**
     * @return the tax1
     */
    public boolean isTax1() {
        return tax1;
    }

    /**
     * @param tax1 the tax1 to set
     */
    public void setTax1(boolean tax1) {
        this.tax1 = tax1;
    }

    /**
     * @return the tax2
     */
    public boolean isTax2() {
        return tax2;
    }

    /**
     * @param tax2 the tax2 to set
     */
    public void setTax2(boolean tax2) {
        this.tax2 = tax2;
    }

    /**
     * @return the available
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * @param available the available to set
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * @return the lastSale
     */
    public Date getLastSale() {
        return lastSale;
    }

    /**
     * @param lastSale the lastSale to set
     */
    public void setLastSale(Date lastSale) {
        this.lastSale = lastSale;
    }

    /**
     * @return the lastReceived
     */
    public Date getLastReceived() {
        return lastReceived;
    }

    /**
     * @param lastReceived the lastReceived to set
     */
    public void setLastReceived(Date lastReceived) {
        this.lastReceived = lastReceived;
    }

    /**
     * @return the reorderCutoff
     */
    public int getReorderCutoff() {
        return reorderCutoff;
    }

    /**
     * @param reorderCutoff the reorderCutoff to set
     */
    public void setReorderCutoff(int reorderCutoff) {
        this.reorderCutoff = reorderCutoff;
    }

    /**
     * @return the partialSaleAllowed
     */
    public boolean isPartialSaleAllowed() {
        return partialSaleAllowed;
    }

    /**
     * @param partialSaleAllowed the partialSaleAllowed to set
     */
    public void setPartialSaleAllowed(boolean partialSaleAllowed) {
        this.partialSaleAllowed = partialSaleAllowed;
    }
    
    public Inventory() {
        
    }
    
    
    
    
}
