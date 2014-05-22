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
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.yb2bacceleratorstorefront.breadcrumb.Breadcrumb;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.util.GlobalMessages;
import de.hybris.platform.yb2bacceleratorstorefront.forms.LoginForm;
import de.hybris.platform.yb2bacceleratorstorefront.forms.RegisterForm;

import java.util.Collection;
//import java.util.HashSet;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;


/**
 * Abstract base class for login page controllers
 */
public abstract class AbstractLoginPageController extends AbstractPageController
{
	protected static final String SPRING_SECURITY_LAST_USERNAME = "SPRING_SECURITY_LAST_USERNAME";

	@ModelAttribute("titles")
	public Collection<TitleData> getTitles()
	{
		return getUserFacade().getTitles();
	}
	
	/*@ModelAttribute("types")
	public Collection<TitleData> getTypes()
	{
		TitleData t1 = new TitleData();
		TitleData t2 = new TitleData();
		t1.setName("Customer"); t1.setCode("Customer");
		t2.setName("Supplier"); t2.setCode("Supplier");
		HashSet<TitleData> set = new HashSet<TitleData>();
		set.add(t1); set.add(t2);
		return set;
	}*/

	protected abstract String getLoginView();

	protected abstract AbstractPageModel getLoginCmsPage() throws CMSItemNotFoundException;

	protected abstract String getSuccessRedirect(final HttpServletRequest request, final HttpServletResponse response);


	protected String getDefaultLoginPage(final boolean loginError, final HttpSession session, final Model model)
			throws CMSItemNotFoundException
	{
		final LoginForm loginForm = new LoginForm();
		model.addAttribute(loginForm);
		model.addAttribute(new RegisterForm());
		final String username = (String) session.getAttribute(SPRING_SECURITY_LAST_USERNAME);
		if (username != null)
		{
			session.removeAttribute(SPRING_SECURITY_LAST_USERNAME);
		}

		loginForm.setJ_username(username);
		storeCmsPageInModel(model, getLoginCmsPage());
		setUpMetaDataForContentPage(model, (ContentPageModel) getLoginCmsPage());
		model.addAttribute("metaRobots", "index,no-follow");

		final Breadcrumb loginBreadcrumbEntry = new Breadcrumb("#", getMessageSource().getMessage("header.link.login", null,
				getI18nService().getCurrentLocale()), null);
		model.addAttribute("breadcrumbs", Collections.singletonList(loginBreadcrumbEntry));

		if (loginError)
		{
			GlobalMessages.addErrorMessage(model, "login.error.account.not.found.title");
		}

		return getLoginView();
	}
}
