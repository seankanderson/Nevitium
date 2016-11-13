package businessmanager.InvoiceQuote.LayoutManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Sean Anderson
 */
public class DocumentElement {

    public DocumentElement(String name){
       element_name = name;
    }

    public void setPaperSize(float [] paperSize){
        this.paper_size = paperSize;
    }
    //returns the point size fixation
    public float[] getPaperSize(){
        return new float[] {(this.paper_size[0] * this.pointsPerMillimeter), (this.paper_size[1] * this.pointsPerMillimeter)};
    }
    public void setPortraitOrientation(boolean p){
        this.portrait = p;
    }
    public boolean isPortrait(){
        return this.portrait;
    }
    public void setPosition(float x, float y){
        pos_x = x;
        pos_y = y;
    }

    public float[] getPosition(){
        float a[] = {pos_x,pos_y};
        return a;
    }
    public void setSize(float x, float y){
        size_x = x;
        size_y = y;
    }
    public float[] getSize(){
        float a[] = {size_x,size_y};
        return a;
    }
    private float data_x=0;
    private float data_y=0;
    public void setDataPosition(float x, float y){
        data_x = x;
        data_y = y;
    }
    public float[] getDataPosition(){
        float a[] = {data_x,data_y};
        return a;
    }

    public void setFont(String family, int style, int points){
        font_family = family;
        font_style = style;
        font_size = points;
        //System.out.println("DocumentElement setFont "+font_family+font_style+font_size);
    }
    public Font getFont(){
        return new Font(font_family, font_style, font_size);

    }

    public void setImage(String file){
        imageFile = file;
    }

    public BufferedImage getImage(){
        if (imageFile.equalsIgnoreCase("null") || imageFile.trim().length() < 1){
            return null;
        }
        BufferedImage image;
            try {
             image = javax.imageio.ImageIO.read(new File(imageFile));
             return image;

        } catch (IOException ex) {
            //System.out.println("problem creating BufferedImage");
            return null;
        }
    }
    
    public void setText(String content){
        text = content;
    }
    public String getText(){
        return text;
    }

    private float text_x=0.0f;
    private float text_y=0.0f;
    /* These are added to element position coodinates */
    /* negative values are welcome */
    public void setTextXY(float x, float y){
        text_x = x;
        text_y = y;
    }

    public float[] getTextPosition(){
        return new float [] {text_x, text_y};
    }
    public String getName(){
        return element_name;
    }
    public void setBorderThickness(float thickness){
        border_thickness = thickness;
    }
    public float getBorderThickness(){
        return border_thickness;
    }
    public void setColors(String hex_fg, String hex_bg){
        fg_color = hex_fg;
        bg_color = hex_bg;
    }
    public Color getFgColor(){
        int hex_code;
        try{
            hex_code = Integer.decode(fg_color);
            if (hex_code < 0) hex_code = Integer.decode("0x000000");

        }catch(NumberFormatException e){
            return new Color(0x000000);
        }
        return new Color(hex_code);
    }
   
        public Color getBgColor(){
        int hex_code;
        try{
            hex_code = Integer.decode(bg_color);
            if (hex_code < 0){
                hex_code = Integer.decode("0x55FFFFFF");
                return new Color(hex_code, true);
            }
            if (bg_color.length() == 10){
                return new Color(hex_code, true);
            }
        }catch(NumberFormatException e){
            return new Color(0x55FFFFFF, true);
        }
        return new Color(hex_code);
    }


    public void setOpacity(int percent){
        opacity = percent;
    }
    public float getOpacity(){
        return opacity * .01f;
    }
    public void setTab(String tab){
        this.tab = tab;
    }

    public DocumentElementTable getTable(){
        return table;
    }

    private String nl = System.getProperty("line.separator");
    public String printElement(boolean println){

        
        StringBuilder sb = new StringBuilder();
        
        if (this.element_name.equals("pageSettings")){
            
            sb.append("<"+element_name+">"+nl);
            sb.append(tab+"<"+"paperSize"+">"+nl);
            sb.append(tab+tab+"<"+"X"+">"+this.paper_size[0]+"</X>"+nl);
            sb.append(tab+tab+"<"+"Y"+">"+this.paper_size[1]+"</Y>"+nl);
            sb.append(tab+"<"+"/paperSize"+">"+nl);
            sb.append(tab+"<portrait>"+Boolean.toString(this.portrait)+"</portrait>"+nl);
            sb.append("</"+element_name+">"+nl);
            return sb.toString();
        }
        
        
        sb.append("<"+element_name+">"+nl);

        sb.append(tab+"<"+"position"+">"+nl);
        sb.append(tab+tab+"<"+"X"+">"+pos_x+"</X>"+nl);
        sb.append(tab+tab+"<"+"Y"+">"+pos_y+"</Y>"+nl);
        sb.append(tab+"<"+"/position"+">"+nl);

        sb.append(tab+"<"+"size"+">"+nl);
        sb.append(tab+tab+"<"+"X"+">"+size_x+"</X>"+nl);
        sb.append(tab+tab+"<"+"Y"+">"+size_y+"</Y>"+nl);
        sb.append(tab+"<"+"/size"+">"+nl);

        sb.append(tab+"<"+"data"+">"+nl);
        sb.append(tab+tab+"<"+"X"+">"+data_x+"</X>"+nl);
        sb.append(tab+tab+"<"+"Y"+">"+data_y+"</Y>"+nl);
        sb.append(tab+"<"+"/data"+">"+nl);

        sb.append(tab+"<"+"text"+">"+nl);
        sb.append(tab+tab+"<string>"+text+"</string>");
        sb.append(tab+tab+"<X>"+text_x+"</X>");
        sb.append(tab+tab+"<Y>"+text_y+"</Y>");
        sb.append(tab+"</text>"+nl);

        sb.append(tab+"<"+"font"+">"+nl);
        sb.append(tab+tab+"<"+"family"+">"+font_family+"</family>"+nl);
        sb.append(tab+tab+"<"+"style"+">"+font_style+"</style>"+nl);
        sb.append(tab+tab+"<"+"points"+">"+font_size+"</points>"+nl);
        sb.append(tab+"<"+"/font"+">"+nl);

        sb.append(tab+"<image>"+imageFile+"</image>"+nl);
        

        sb.append(this.getTable().printElement(tab));

        sb.append(tab+"<"+"graphics"+">"+nl);
        sb.append(tab+tab+"<"+"border"+">"+border_thickness+"</border>"+nl);
        sb.append(tab+tab+"<"+"fg_color"+">"+fg_color+"</fg_color>"+nl);
        sb.append(tab+tab+"<"+"bg_color"+">"+bg_color+"</bg_color>"+nl);
        sb.append(tab+tab+"<"+"opacity"+">"+opacity+"</opacity>"+nl);
        sb.append(tab+"<"+"/graphics"+">"+nl);

        sb.append(tab+"<include>"+Boolean.toString(include)+"</include>"+nl);

        sb.append("</"+element_name+">"+nl);
        if (println) System.out.println(sb.toString());
        return sb.toString();

    }
    private float pointsPerMillimeter = 2.8346457f;
    private boolean portrait = true;
    private float [] paper_size = new float[] {215.9f, 279.4f}; //8.5" X 11" American
    private String element_name="generic";
    private float pos_x=0;
    private float pos_y=0;
    private float size_x=0;
    private float size_y=0;
    private String font_family="tahoma";
    private int font_style=Font.PLAIN;
    private int font_size=16;
    private String imageFile = "null";
    private String text="";
    private float border_thickness = 2; //zero = no draw
    private String fg_color = "0x000000";
    private String bg_color ="0xFFFFFF";
    private int opacity = 100;
    private String tab = "  ";
    private boolean include = true;

    public boolean isInclude() {
        return include;
    }

    public void setInclude(boolean include) {
        this.include = include;
    }
    private DocumentElementTable table = new DocumentElementTable();
}

