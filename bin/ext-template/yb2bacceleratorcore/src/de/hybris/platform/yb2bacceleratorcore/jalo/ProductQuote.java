/*
 *  
 * [y] hybris Platform
 *  
 * Copyright (c) 2000-2011 hybris AG
 * All rights reserved.
 *  
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *  
 */
package de.hybris.platform.yb2bacceleratorcore.jalo;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowItemAttachmentModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;
import de.hybris.platform.workflow.services.WorkflowService;
import de.hybris.platform.yb2bacceleratorcore.model.ProductQuoteModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


@SuppressWarnings("deprecation")
public class ProductQuote extends GeneratedProductQuote
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(ProductQuote.class.getName());

	@Override
	protected Item createItem(final SessionContext ctx, final ComposedType type, final ItemAttributeMap allAttributes)
			throws JaloBusinessException
	{
		// business code placed here will be executed before the item is created
		// then create the item
		final Item item = super.createItem(ctx, type, allAttributes);
		// business code placed here will be executed after the item was created
		// and return the item
		return item;
	}

	private ModelService modelService;
	private B2BCustomerService b2bCustomerService;
	private ProductService productService;
	@SuppressWarnings("deprecation")
	private WorkflowService workflowService;
	private FlexibleSearchService flexibleSearchService;
	private UserService userService;
	private WorkflowProcessingService workflowProcessingService;

	@SuppressWarnings("deprecation")
	public void createAndSave(final String description, final String productID)
	{
		final ProductQuoteModel pqmodel = new ProductQuoteModel();
		final ProductModel pmodel = getProductService().getProductForCode(productID);
		final B2BCustomerModel cmodel = (B2BCustomerModel) getB2bCustomerService().getCurrentB2BCustomer();
		pqmodel.setCustomer(cmodel);
		pqmodel.setProduct(pmodel);
		pqmodel.setDescription(description);
		getModelService().save(pqmodel);

		//create workflows
		FlexibleSearchQuery query = new FlexibleSearchQuery(
				"SELECT {pk} FROM {WorkflowTemplate} WHERE {code}='ProductQuoteWFTemplate'");
		final WorkflowTemplateModel wftmodel = flexibleSearchService.searchUnique(query);
		//final List<WorkflowTemplateModel> list = result.getResult();
		//final WorkflowTemplateModel wftmodel = list.iterator().next();

		final Collection<VariantProductModel> variants = pqmodel.getProduct().getVariants();
		WorkflowModel workflow;
		final List<WorkflowItemAttachmentModel> attachList = new ArrayList<WorkflowItemAttachmentModel>();
		WorkflowItemAttachmentModel attachment;

		for (final VariantProductModel variant : variants)
		{
			query = new FlexibleSearchQuery("SELECT {supplier} FROM {PlatformSupplierVariantProduct} WHERE {code}='"
					+ variant.getCode() + "'");
			final EmployeeModel supplier = flexibleSearchService.searchUnique(query);

			workflow = workflowService.createWorkflow(wftmodel, pqmodel);
			workflow.setDescription(description);

			attachList.clear();
			//add quote attachment
			attachment = modelService.create(WorkflowItemAttachmentModel.class);
			attachment.setItem(pqmodel);
			attachment.setCode("Quotation");
			attachment.setWorkflow(workflow);
			attachList.add(attachment);

			//add product attachment
			attachment = modelService.create(WorkflowItemAttachmentModel.class);
			attachment.setItem(variant);
			attachment.setCode("QuotedProduct");
			attachment.setWorkflow(workflow);
			attachList.add(attachment);

			//add customer attachment
			attachment = modelService.create(WorkflowItemAttachmentModel.class);
			attachment.setItem(cmodel);
			attachment.setCode("QuotingCustomer");
			attachment.setWorkflow(workflow);
			attachList.add(attachment);

			final WorkflowActionModel action = workflow.getActions().iterator().next();
			action.setPrincipalAssigned(supplier);
			action.setAttachments(attachList);
			action.setDescription(description, Locale.CHINESE);

			/*
			 * final WorkflowItemAttachmentModel attachment = modelService.create(WorkflowItemAttachmentModel.class);
			 * attachment.setItem(pqmodel); attachment.setWorkflow(workflow); attachment.setCode("SampleProductAtt");
			 * workflow.setAttachments(Collections.singletonList(attachment));
			 */

			workflowProcessingService.startWorkflow(workflow);

		}
	}

	@Required
	protected ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Required
	protected B2BCustomerService getB2bCustomerService()
	{
		return b2bCustomerService;
	}

	public void setB2bCustomerService(final B2BCustomerService b2bCustomerService)
	{
		this.b2bCustomerService = b2bCustomerService;
	}

	@Required
	protected ProductService getProductService()
	{
		return productService;
	}

	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	@Required
	public WorkflowService getWorkflowService()
	{
		return workflowService;
	}

	public void setWorkflowService(final WorkflowService workflowService)
	{
		this.workflowService = workflowService;
	}

	@Required
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	@Required
	public UserService userService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	@Required
	public WorkflowProcessingService getWorkflowProcessingService()
	{
		return workflowProcessingService;
	}

	public void setWorkflowProcessingService(final WorkflowProcessingService workflowProcessingService)
	{
		this.workflowProcessingService = workflowProcessingService;
	}
}
