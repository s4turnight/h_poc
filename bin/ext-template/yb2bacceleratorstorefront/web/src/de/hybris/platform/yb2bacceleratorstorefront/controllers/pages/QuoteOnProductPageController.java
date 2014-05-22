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
package de.hybris.platform.yb2bacceleratorstorefront.controllers.pages;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.yb2bacceleratorcore.jalo.ProductQuote;
import de.hybris.platform.yb2bacceleratorstorefront.annotations.RequireHardLogIn;
import de.hybris.platform.yb2bacceleratorstorefront.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.ControllerConstants;
import de.hybris.platform.yb2bacceleratorstorefront.forms.QuoteProductForm;

import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Controller for the forgotten password pages. Supports requesting a password reset email as well as changing the
 * password once you have got the token that was sent via email.
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/quote/product")
public class QuoteOnProductPageController extends AbstractPageController
{
	private static final Logger LOG = Logger.getLogger(QuoteOnProductPageController.class);

	@Resource(name = "simpleBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;

	@Resource(name = "b2bCustomerFacade")
	protected CustomerFacade customerFacade;

	@Resource(name = "productQuote")
	protected ProductQuote productQuote;


	@RequestMapping(value = "/request/{productCode:.*}", method = RequestMethod.GET)
	@RequireHardLogIn
	public String productDetail(@PathVariable("productCode") final String productCode, final Model model)
			throws CMSItemNotFoundException, UnsupportedEncodingException
	{
		model.addAttribute(new QuoteProductForm());

		return ControllerConstants.Views.Fragments.ProductQuoteRequestPopup;
	}

	@RequestMapping(value = "/request/{productCode:.*}", method = RequestMethod.POST)
	@RequireHardLogIn
	public String productDetail(@PathVariable("productCode") final String productCode, @Valid final QuoteProductForm form,
			final BindingResult bindingResult) throws CMSItemNotFoundException, UnsupportedEncodingException
	{

		if (bindingResult.hasErrors())
		{
			return ControllerConstants.Views.Fragments.ProductQuoteRequestPopup;
		}
		else
		{
			productQuote.createAndSave(form.getDescription(), productCode);
			return ControllerConstants.Views.Fragments.ProductQuoteRequestResult;
		}


	}
	/*
	 * @RequestMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.POST) public String
	 * productDetail(@PathVariable("productCode") final String productCode, final QuoteProductForm form, final
	 * BindingResult bindingResult ) throws CMSItemNotFoundException, UnsupportedEncodingException
	 * 
	 * @RequireHardLogIn final CustomerData customerData = customerFacade.getCurrentCustomer();
	 */
}
