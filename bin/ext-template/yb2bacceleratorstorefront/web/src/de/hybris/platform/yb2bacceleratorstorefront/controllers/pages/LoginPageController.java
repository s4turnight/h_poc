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

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;
import de.hybris.platform.workflow.services.WorkflowService;
import de.hybris.platform.yb2bacceleratorcore.model.SupplierEmployeeModel;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.ControllerConstants;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.ControllerConstants.Views;
import de.hybris.platform.yb2bacceleratorstorefront.forms.RegisterForm;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Login Controller. Handles login and register for the account flow.
 */
@SuppressWarnings("deprecation")
@Controller
@Scope("tenant")
@RequestMapping(value = "/login")
public class LoginPageController extends AbstractLoginPageController
{
	@Resource(name = "httpSessionRequestCache")
	private HttpSessionRequestCache httpSessionRequestCache;

	public void setHttpSessionRequestCache(final HttpSessionRequestCache accHttpSessionRequestCache)
	{
		this.httpSessionRequestCache = accHttpSessionRequestCache;
	}


	@RequestMapping(method = RequestMethod.GET)
	public String doLogin(@RequestHeader(value = "referer", required = false) final String referer,
			@RequestParam(value = "error", defaultValue = "false") final boolean loginError, final Model model,
			final HttpServletRequest request, final HttpServletResponse response, final HttpSession session)
			throws CMSItemNotFoundException
	{
		if (!loginError)
		{
			storeReferer(referer, request, response);
		}
		return getDefaultLoginPage(loginError, session, model);
	}

	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(@Valid final RegisterForm form, final BindingResult bindingResult) throws CMSItemNotFoundException,
			UnsupportedEncodingException
	{
		if (!bindingResult.hasErrors())
		{

			final FlexibleSearchQuery wftquery = new FlexibleSearchQuery(
					"SELECT {pk} FROM {WorkflowTemplate} WHERE {code}='ApproveUserWFTemplate'");
			final WorkflowTemplateModel wftmodel = flexibleSearchService.searchUnique(wftquery);

			WorkflowModel workflow = null;

			final Boolean is_supplier = form.getType();

			if (is_supplier.booleanValue())
			{
				//create supplier
				final SupplierEmployeeModel semodel = modelService.create(SupplierEmployeeModel.class);
				semodel.setUid(form.getEmail());
				semodel.setName(form.getcName());
				semodel.setEncodedPassword(form.getPwd());
				semodel.setLoginDisabled(true);

				//currency, usergroup
				final FlexibleSearchQuery currencyQuery = new FlexibleSearchQuery("select {pk} from {currency} where {base} = '1'");
				final CurrencyModel base_currency = flexibleSearchService.searchUnique(currencyQuery);
				semodel.setSessionCurrency(base_currency);

				final FlexibleSearchQuery groupQuery = new FlexibleSearchQuery("select {pk} from {" + PrincipalGroupModel._TYPECODE
						+ "} where {uid} = 'supplierEmployeeGroup'");
				final PrincipalGroupModel supplier_group = flexibleSearchService.searchUnique(groupQuery);
				semodel.setGroups(new HashSet<PrincipalGroupModel>(Collections.singleton(supplier_group)));

				modelService.save(semodel);

				workflow = workflowService.createWorkflow(wftmodel, semodel);

			}
			else
			{
				//create customer and its company
				final B2BCustomerModel cmodel = modelService.create(B2BCustomerModel.class);
				cmodel.setUid(form.getEmail());
				cmodel.setEmail(form.getEmail());
				cmodel.setName(form.getName());
				cmodel.setEncodedPassword(form.getPwd());
				cmodel.setActive(Boolean.FALSE);

				//add groups
				final FlexibleSearchQuery query = new FlexibleSearchQuery("select {pk} from {" + PrincipalGroupModel._TYPECODE
						+ "} where {uid} in ('b2badmingroup', 'b2bcustomergroup')");
				final SearchResult<PrincipalGroupModel> result = flexibleSearchService.search(query);

				final List<PrincipalGroupModel> groups = result.getResult();
				final Set<PrincipalGroupModel> groupSet = new HashSet<PrincipalGroupModel>(groups);
				cmodel.setGroups(groupSet);

				// create B2B Unit
				final B2BUnitModel unitModel = modelService.create(B2BUnitModel.class);
				unitModel.setUid(form.getcName());
				unitModel.setLocName(form.getcName(), Locale.CHINESE);
				unitModel.setLocName(form.getcName());
				unitModel.setName(form.getcName());
				System.out.println(form.getcName());

				// switch user
				final UserModel originUser = userService.getCurrentUser();
				userService.setCurrentUser(userService.getAdminUser());
				modelService.save(unitModel);
				unitModel.setActive(Boolean.FALSE);
				userService.setCurrentUser(originUser);

				// assign unit to customer
				cmodel.setDefaultB2BUnit(unitModel);
				modelService.save(cmodel);

				workflow = workflowService.createWorkflow(wftmodel, cmodel);

			}
			workflowProcessingService.startWorkflow(workflow);
		}
		return Views.Pages.Account.RegisterResultPage;
	}

	@Override
	protected String getLoginView()
	{
		return ControllerConstants.Views.Pages.Account.AccountLoginPage;
	}

	@Override
	protected String getSuccessRedirect(final HttpServletRequest request, final HttpServletResponse response)
	{
		if (httpSessionRequestCache.getRequest(request, response) != null)
		{
			return httpSessionRequestCache.getRequest(request, response).getRedirectUrl();
		}

		return "/my-account";
	}

	@Override
	protected AbstractPageModel getLoginCmsPage() throws CMSItemNotFoundException
	{
		return getContentPageForLabelOrId("login");
	}

	protected void storeReferer(final String referer, final HttpServletRequest request, final HttpServletResponse response)
	{
		if (StringUtils.isNotBlank(referer))
		{
			httpSessionRequestCache.saveRequest(request, response);
		}
	}

	@Resource(name = "defaultB2BCustomerService")
	private B2BCustomerService b2BCustomerService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "workflowService")
	private WorkflowService workflowService;

	@Resource(name = "workflowProcessingService")
	private WorkflowProcessingService workflowProcessingService;
}
