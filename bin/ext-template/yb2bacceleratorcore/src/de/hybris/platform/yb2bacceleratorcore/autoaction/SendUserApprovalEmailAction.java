/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.yb2bacceleratorcore.autoaction;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.workflow.jalo.AutomatedWorkflowTemplateJob;
import de.hybris.platform.workflow.jalo.WorkflowAction;
import de.hybris.platform.workflow.jalo.WorkflowDecision;
import de.hybris.platform.workflow.jobs.WorkflowAutomatedAction;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.yb2bacceleratorcore.model.SupplierEmployeeModel;
import de.hybris.platform.yb2bacceleratorcore.services.MailService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


public class SendUserApprovalEmailAction implements AutomatedWorkflowTemplateJob
{
	private static final Logger LOG = Logger.getLogger(WorkflowAutomatedAction.class.getName());

	@Autowired
	private MailService mailService;

	@Autowired
	private ModelService modelService;

	public WorkflowDecisionModel perform(final WorkflowActionModel action)
	{
		LOG.info("This will send the approval email");

		System.out.println(action.getAttachments().size());
		if (!action.getAttachments().isEmpty())
		{
			final UserModel umodel = (UserModel) (action.getAttachments().get(0).getItem());

			System.out.println(umodel.getUid());

			if (umodel instanceof SupplierEmployeeModel)
			{
				mailService.sendSupplierApprovalmail((SupplierEmployeeModel) umodel);
				System.out.println("Supplier Mail Sent");
			}
			else if (umodel instanceof B2BCustomerModel)
			{
				mailService.sendCustomerApprovalMail((B2BCustomerModel) umodel);
				System.out.println("Customer Mail Sent");
			}
		}

		for (final WorkflowDecisionModel decision : action.getDecisions())
		{
			return decision;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.workflow.jalo.AutomatedWorkflowTemplateJob#perform(de.hybris.platform.workflow.jalo.WorkflowAction
	 * )
	 */
	@Override
	public WorkflowDecision perform(final WorkflowAction wfAction)
	{
		perform((WorkflowActionModel) modelService.get(wfAction.getPK()));
		return wfAction.getDecisions().iterator().next();

	}
}