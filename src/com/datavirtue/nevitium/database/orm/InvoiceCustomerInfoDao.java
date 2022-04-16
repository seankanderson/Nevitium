package com.datavirtue.nevitium.database.orm;

import com.datavirtue.nevitium.models.invoices.InvoiceCustomerInfo;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;

/**
 *
 * @author SeanAnderson
 */
public class InvoiceCustomerInfoDao extends BaseDaoImpl<InvoiceCustomerInfo, Object> implements InvoiceCustomerInfoDaoInterface {
    public InvoiceCustomerInfoDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, InvoiceCustomerInfo.class);
    }
}
