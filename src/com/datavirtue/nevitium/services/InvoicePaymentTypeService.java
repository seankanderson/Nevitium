package com.datavirtue.nevitium.services;
import com.datavirtue.nevitium.database.orm.InvoicePaymentTypeDao;
import com.datavirtue.nevitium.models.invoices.InvoicePaymentType;
import com.j256.ormlite.dao.DaoManager;
import java.sql.SQLException;


public class InvoicePaymentTypeService extends BaseService<InvoicePaymentTypeDao, InvoicePaymentType> {

   
    @Override
    public InvoicePaymentTypeDao getDao() throws SQLException {
        return DaoManager.createDao(connection, InvoicePaymentType.class);
    }

   
}
