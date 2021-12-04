/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.InvoiceDao;
import java.sql.SQLException;
import models.Invoice;

/**
 *
 * @author SeanAnderson
 */
public class InvoiceService extends BaseService<InvoiceDao, Invoice> {

    @Override
    public InvoiceDao getDao() throws SQLException {
        return dao == null ? new InvoiceDao(connection) : dao;
    }
    
}
