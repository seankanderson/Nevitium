package com.datavirtue.nevitium.ui.layoutdesigner;
import java.util.ArrayList;

public class DocumentElementTable {

    public DocumentElementTable(){
        columns.trimToSize();
    }

    public void addColumn(TableColumn tc){
        columns.add(tc);
    }
    public ArrayList getColumns(){
        columns.trimToSize();
        return columns;
    }
    private float row_height = 14.17f;
    private int row_count = 1;

    private ArrayList columns = new ArrayList();

    /**
     * @return the row_height
     */
    public float getRow_height() {
        return row_height;
    }

    /**
     * @param row_height the row_height to set
     */
    public void setRow_height(float row_height) {
        this.row_height = row_height;
    }

    /**
     * @return the row_count
     */
    public int getRow_count() {
        return row_count;
    }

    /**
     * @param row_count the row_count to set
     */
    public void setRow_count(int row_count) {
        this.row_count = row_count;
    }

    public String printElement(String tab){
        String nl = System.getProperty("line.separator");

        StringBuilder sb = new StringBuilder();
        sb.append(tab+"<"+"table"+">"+nl);
        //System.out.println("Doc Table clo count:"+columns.size());
        for (int x = 0; x < columns.size(); x++){
            TableColumn tc = (TableColumn)columns.get(x);
            //System.out.println(tc.printElement(tab));
            sb.append(tc.printElement(tab));
        }

        sb.append(tab+"<"+"row_height"+">"+this.getRow_height()+"<"+"/row_height"+">"+nl);
        sb.append(tab+"<"+"row_count"+">"+this.getRow_count()+"<"+"/row_count"+">"+nl);
        sb.append(tab+"<"+"/table"+">"+nl);
        return sb.toString();
    }


}

