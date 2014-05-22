/**
 * 
 */
package de.hybris.platform.yb2bacceleratorcore.services.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.Config;
import de.hybris.platform.yb2bacceleratorcore.model.SupplierEmployeeModel;
import de.hybris.platform.yb2bacceleratorcore.services.MailService;

import java.util.Locale;

import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;


/**
 * @author I074791
 * 
 */
public class PlatformMailService implements MailService, InitializingBean
{

	private final static Logger LOG = Logger.getLogger(PlatformMailService.class);
	private String domain;
	private String fromAddress;
	private String replyToAddress;


	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private SessionService sessionService;
	@Autowired
	private L10NService l10NService;
	@Autowired
	private I18NService i18nService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.yb2bacceleratorcore.services.MailService#sendSupplierApprovalmail(de.hybris.platform.
	 * yb2bacceleratorcore.model.SupplierEmployeeModel)
	 */
	@Override
	public void sendSupplierApprovalmail(final SupplierEmployeeModel supplier)
	{
		final MailPreparator preparator = new MailPreparator()
		{

			@Override
			public void prepare(final MimeMessageHelper message) throws Exception
			{
				message.setSubject(l10NService.getLocalizedString("mail.supplierapproval.subject"));
				message.setText(l10NService.getLocalizedString("mail.supplierapproval.body", new Object[]
				{ supplier.getName() }));

			}
		};

		send(preparator, supplier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.yb2bacceleratorcore.services.MailService#sendCustomerApprovalMail(de.hybris.platform.b2b.model
	 * .B2BCustomerModel)
	 */
	@Override
	public void sendCustomerApprovalMail(final B2BCustomerModel customer)
	{
		final MailPreparator preparator = new MailPreparator()
		{

			@Override
			public void prepare(final MimeMessageHelper message) throws Exception
			{
				message.setSubject(l10NService.getLocalizedString("mail.customerapproval.subject"));
				message.setText(l10NService.getLocalizedString("mail.customerapproval.body", new Object[]
				{ customer.getName(), domain }));

			}
		};

		send(preparator, customer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception
	{
		domain = "powertools.local:9001/yb2bacceleratorstorefront";
		fromAddress = Config.getParameter("mail.from");
		replyToAddress = Config.getParameter("mail.replyto");

		if (domain == null || domain.isEmpty() || fromAddress == null || fromAddress.isEmpty() || replyToAddress == null
				|| replyToAddress.isEmpty())
		{
			throw new IllegalStateException(
					"Can not start mail service, please configure properties 'cuppy.domain','mail.from' and 'mail.replyto'");
		}

	}

	protected void send(final MailPreparator preparer, final UserModel user)
	{
		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				i18nService.setLocalizationFallbackEnabled(true);
				if (user.getSessionLanguage() != null)
				{
					i18nService.setCurrentLocale(new Locale(user.getSessionLanguage().getIsocode()));
				}

				final MimeMessagePreparator preparator = new MimeMessagePreparator()
				{
					@Override
					public void prepare(final MimeMessage mimeMessage) throws Exception //NOPMD
					{
						final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
						message.setTo(user.getUid());
						message.setFrom(fromAddress);
						message.setReplyTo(replyToAddress);
						preparer.prepare(message);
					}
				};
				try
				{
					mailSender.send(preparator);
				}
				catch (final MailException e)
				{
					//log it and go on
					LOG.error("Can not send mail to " + user.getUid() + ": " + getMailExceptionMessage(e));
				}
			}
		});
	}


	protected interface MailPreparator
	{
		void prepare(MimeMessageHelper message) throws Exception;
	}

	public void setMailSender(final JavaMailSender mailSender)
	{
		this.mailSender = mailSender;
	}

	private String getMailExceptionMessage(final MailException exception)
	{
		final StringBuilder result = new StringBuilder(exception.getMessage());
		if (exception.getCause() != null)
		{
			final Throwable cause = exception.getCause();
			result.append(": ").append(exception.getMessage());
			if (cause.getCause() != null)
			{
				result.append(": ").append(cause.getMessage());
			}
		}
		return result.toString();
	}

}
