/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package businessmanager.database;
import businessmanager.database.InventoryItemImage;
import java.util.*;

/**
 *
 * @author Administrator
 */
public class InventoryItem {
    
    private int id;
    private String upc;
    private String code;
    private String description;
    private String size;
    private String weight;
    private double onhand;
    private double cost;
    private double price;
    private String category;
    private boolean tax1;
    private boolean tax2;
    private boolean available;
    private long lastSale;
    private long lastReceived;
    private double cutoff;
    private boolean partialQuantity;    
    private ArrayList<InventoryItemImage> images;
    private int nextImageIndex=0;
    
    
    public InventoryItemImage next(){
        
        if (getImages() == null) return null;
        if (getNextImageIndex() >= getImages().size()) {
            setNextImageIndex(getNextImageIndex() + 1);
            return getImages().get(getNextImageIndex());
            
        }
        
        return null;
    }
    
    
    /*
     * CREATE TABLE INVENTORY 
(
INVENTORY_ID INT NOT NULL PRIMARY KEY, UPC VARCHAR(14), CODE VARCHAR(16), 
DESCRIPTION VARCHAR(300), SIZE VARCHAR(15), WEIGHT VARCHAR(15), 
ONHAND REAL, COST DECIMAL(19,4), PRICE DECIMAL(19,4),
CATEGORY VARCHAR(30), TAX1 BOOLEAN, TAX2 BOOLEAN,
AVAILABLE BOOLEAN, LAST_SALE BIGINT, LAST_RECEIVED BIGINT,
CUTOFF REAL, PARTIAL_QTY_ALLOWED BOOLEAN
);

CREATE TABLE INVENTORY_IMAGES
(
INVENTORY_IMAGES_ID INT NOT NULL PRIMARY KEY, 
INVENTORY_ID INT CONSTRAINT INVENTORY_IMAGES_REF REFERENCES INVENTORY(INVENTORY_ID) ON DELETE CASCADE ON UPDATE RESTRICT,
TITLE VARCHAR(128), DATE BIGINT, TYPE VARCHAR(3), BITMAP BLOB
);
     * 
     */

    /**
     * @return the id
     */
    public int getId() {
        return id;
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
     * @return the onhand
     */
    public double getOnhand() {
        return onhand;
    }

    /**
     * @param onhand the onhand to set
     */
    public void setOnhand(double onhand) {
        this.onhand = onhand;
    }

    /**
     * @return the cost
     */
    public double getCost() {
        return cost;
    }

    /**
     * @param cost the cost to set
     */
    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }

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
    public long getLastSale() {
        return lastSale;
    }

    /**
     * @param lastSale the lastSale to set
     */
    public void setLastSale(long lastSale) {
        this.lastSale = lastSale;
    }

    /**
     * @return the lastReceived
     */
    public long getLastReceived() {
        return lastReceived;
    }

    /**
     * @param lastReceived the lastReceived to set
     */
    public void setLastReceived(long lastReceived) {
        this.lastReceived = lastReceived;
    }

    /**
     * @return the cutoff
     */
    public double getCutoff() {
        return cutoff;
    }

    /**
     * @param cutoff the cutoff to set
     */
    public void setCutoff(double cutoff) {
        this.cutoff = cutoff;
    }

    /**
     * @return the partialQuantity
     */
    public boolean isPartialQuantity() {
        return partialQuantity;
    }

    /**
     * @param partialQuantity the partialQuantity to set
     */
    public void setPartialQuantity(boolean partialQuantity) {
        this.partialQuantity = partialQuantity;
    }

    /**
     * @return the images
     */
    public ArrayList<InventoryItemImage> getImages() {
        return images;
    }

    /**
     * @param images the images to set
     */
    public void setImages(ArrayList<InventoryItemImage> images) {
        this.images = images;
    }

    /**
     * @return the nextImageIndex
     */
    public int getNextImageIndex() {
        return nextImageIndex;
    }

    /**
     * @param nextImageIndex the nextImageIndex to set
     */
    public void setNextImageIndex(int nextImageIndex) {
        this.nextImageIndex = nextImageIndex;
    }
}

