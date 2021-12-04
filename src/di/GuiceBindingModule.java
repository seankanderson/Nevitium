/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package di;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import java.sql.SQLException;


/**
 *
 * @author SeanAnderson
 */
public class GuiceBindingModule extends AbstractModule{
    @Override
    protected void configure() {
        //bind(Inventory.class).to(Inventory.class);
        //new JdbcConnectionSource("jdbc:h2:mem:nevitium")
       try {
            bind(JdbcConnectionSource.class)
                .annotatedWith(Names.named("DatabaseConnection"))
                .toInstance(new JdbcConnectionSource("jdbc:h2:~/nevitium;AUTO_SERVER=TRUE"));
       }catch(Exception e) {
           e.printStackTrace();
            //swallow exception...should check the database connection for null
       }
       
    }
}
