package com.datavirtue.nevitium.services;

import com.datavirtue.nevitium.database.orm.InvoiceCustomerInfoDao;
import com.datavirtue.nevitium.models.invoices.InvoiceCustomerInfo;
import com.j256.ormlite.dao.DaoManager;
import java.sql.SQLException;

/**
 *
 * @author SeanAnderson
 */
public class InvoiceCustomerInfoService extends BaseService<InvoiceCustomerInfoDao, InvoiceCustomerInfo> {
    
    @Override
    public InvoiceCustomerInfoDao getDao() throws SQLException {
        return DaoManager.createDao(connection, InvoiceCustomerInfo.class);
    }
    
}
