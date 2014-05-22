/**
 * 
 */
package de.hybris.platform.yb2bacceleratorcore.services;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.yb2bacceleratorcore.model.SupplierEmployeeModel;


/**
 * @author I074791
 * 
 */
public interface MailService
{

	void sendSupplierApprovalmail(SupplierEmployeeModel supplier);

	void sendCustomerApprovalMail(B2BCustomerModel customer);

}
