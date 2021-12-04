/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import models.Invoice;

/**
 *
 * @author SeanAnderson
 */
public class InvoiceDao extends BaseDaoImpl<Invoice, Object> implements InvoiceDaoInterface {
    public InvoiceDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Invoice.class);
    }
}
