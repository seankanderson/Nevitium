package com.datavirtue.nevitium.ui.layoutdesigner;

class TableColumn {

    public TableColumn(){

    }
    private String columnName = "column";
    private float width = 100;
    private String header_title = "";
    private float arc_width = 0f;
    private float arc_height = 0f;
    private String head_color="0xFFFFFF";
    private String cell_color="0xFFFFFF";
    private String alt_color="0xFFFFFF";
    private float top_border=1f;
    private float bot_border=1f;
    private int rep_count = 0;


    /**
     * @return the width
     */
    public float getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * @return the header_title
     */
    public String getHeader_title() {
        return header_title;
    }

    /**
     * @param header_title the header_title to set
     */
    public void setHeader_title(String header_title) {
        this.header_title = header_title;
    }

    /**
     *
     * @param Title of the column for document builder
     */
    public void setColumnName(String columnName){
        this.columnName = columnName;
    }

    public String getColumnName(){
        return columnName;
    }

    /**
     * @return the head_color
     */
    public String getHead_color() {
        return head_color;
    }

    /**
     * @param head_color the head_color to set
     */
    public void setHead_color(String head_color) {
        this.head_color = head_color;
    }

    /**
     * @return the rep_count
     */
    public int getRep_count() {
        return rep_count;
    }

    /**
     * @param rep_count the rep_count to set
     */
    public void setRep_count(int rep_count) {
        this.rep_count = rep_count;
    }

    /**
     * @return the arc_width
     */
    public float getArc_width() {
        return arc_width;
    }

    /**
     * @param arc_width the arc_width to set
     */
    public void setArc_width(float arc_width) {
        this.arc_width = arc_width;
    }

    /**
     * @return the arc_height
     */
    public float getArc_height() {
        return arc_height;
    }

    /**
     * @param arc_height the arc_height to set
     */
    public void setArc_height(float arc_height) {
        this.arc_height = arc_height;
    }

    /**
     * @return the cell_color
     */
    public String getCell_color() {
        return cell_color;
    }

    /**
     * @param cell_color the cell_color to set
     */
    public void setCell_color(String cell_color) {
        this.cell_color = cell_color;
    }

    /**
     * @return the alt_color
     */
    public String getAlt_color() {
        return alt_color;
    }

    /**
     * @param alt_color the alt_color to set
     */
    public void setAlt_color(String alt_color) {
        this.alt_color = alt_color;
    }

    /**
     * @return the top_border
     */
    public float getTop_border() {
        return top_border;
    }

    /**
     * @param top_border the top_border to set
     */
    public void setTop_border(float top_border) {
        this.top_border = top_border;
    }

    /**
     * @return the bot_border
     */
    public float getBot_border() {
        return bot_border;
    }

    /**
     * @param bot_border the bot_border to set
     */
    public void setBot_border(float bot_border) {
        this.bot_border = bot_border;
    }

    public void setVisible(boolean value){
        visible = value;
    }
    public boolean isVisible(){
        return visible;
    }
    private boolean visible = true;
    String nl = System.getProperty("line.separator");
    public String printElement(String tab){


        StringBuilder sb = new StringBuilder();
        sb.append(tab+tab+"<"+columnName+">"+nl);
        sb.append(tab+tab+tab+"<"+"width"+">"+this.getWidth()+"<"+"/width"+">"+nl);
        sb.append(tab+tab+tab+"<"+"header"+">"+this.getHeader_title()+"<"+"/header"+">"+nl);
        sb.append(tab+tab+tab+"<"+"arc_width"+">"+this.getArc_width()+"<"+"/arc_width"+">"+nl);
        sb.append(tab+tab+tab+"<"+"arc_height"+">"+this.getArc_height()+"<"+"/arc_height"+">"+nl);
        sb.append(tab+tab+tab+"<"+"head_color"+">"+this.getHead_color()+"<"+"/head_color"+">"+nl);
        sb.append(tab+tab+tab+"<"+"cell_color"+">"+this.getCell_color()+"<"+"/cell_color"+">"+nl);
        sb.append(tab+tab+tab+"<"+"alt_color"+">"+this.getAlt_color()+"<"+"/alt_color"+">"+nl);
        sb.append(tab+tab+tab+"<"+"top_border"+">"+this.getTop_border()+"<"+"/top_border"+">"+nl);
        sb.append(tab+tab+tab+"<"+"bot_border"+">"+this.getBot_border()+"<"+"/bot_border"+">"+nl);
        sb.append(tab+tab+tab+"<"+"rep_count"+">"+this.getRep_count()+"<"+"/rep_count"+">"+nl);
        sb.append(tab+tab+tab+"<"+"visible"+">"+Boolean.toString(visible)+"<"+"/visible"+">"+nl);
        sb.append(tab+tab+"<"+"/"+columnName+">"+nl);
        return sb.toString();
    }
}