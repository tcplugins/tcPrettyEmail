package prettyemailer.teamcity.config;

import java.util.Properties;

import org.jdom.Element;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class PrettyEmailMainConfig {
	private Integer smtpPort = 25;
	private String smtpHost = null;
	private String smtpUsername = null;
	private String smtpPassword = null;
	private String fromAddress = null;
	private String fromName = null;
	private boolean starttlsEnabled = false;
	
	private String templatePath = null;
	private String attachmentPath = null;
	private Integer maxTestsToShow = 5;
	public final Integer defaultMaxTestsToShow = maxTestsToShow;
	private Integer maxErrorLinesToShow = 50;
	public final Integer defaultMaxErrorLinesToShow = maxErrorLinesToShow;
	private boolean attachImages = true;
	

	public Element getSmtpAsElement(){
		/*
    		  <smtp host="smtp.mycompany.com" port="25" username="foo" password="bar" />
		 */
		if (this.getSmtpHost() == null || this.getSmtpPort() == null){
			return null;
		}
		Element el = new Element("smtp");
		el.setAttribute("host", this.getSmtpHost());
		el.setAttribute("port", String.valueOf(this.getSmtpPort()));
		el.setAttribute("starttls-enabled", String.valueOf(this.getStartTLSEnabled()));
		if (   this.smtpPassword != null && this.smtpPassword.length() > 0 
			&& this.smtpUsername != null && this.smtpUsername.length() > 0 )
		{
			el.setAttribute("username", this.getSmtpUsername());
			el.setAttribute("password", this.getSmtpPassword());
			
		}
		
		if (this.fromAddress != null && this.fromAddress.length() > 0){
			el.setAttribute("from-address", this.getFromAddress());
		}
		
		if (this.fromName != null && this.fromName.length() > 0){
			el.setAttribute("from-name", this.getFromName());
		}

		return el;
	}
	
	public Element getTemplatePathAsElement(){
		if (this.templatePath == null){
			return null;
		}
		
		/*
		  <template-path path="/opt/TeamCity/prettyEmail/templates" />
		 */

		
		Element el = new Element("template-path");
			el.setAttribute("path", this.templatePath);
			
		return el;
	}
		
	public Element getAttachmentPathAsElement(){
		if (this.attachmentPath == null){
			return null;
		}

		/*
		  <attachment-path path="/opt/TeamCity/prettyEmail/attachments/img" />
		 */
		
		Element el = new Element("attachment-path");
			el.setAttribute("path", this.attachmentPath);
			
		return el;
	}

	public Element getAttachImagesAsElement(){
		/*
		  <attach-images attach="/opt/TeamCity/prettyEmail/attachments/img" />
		 */
		
		Element el = new Element("attach-images");
			el.setAttribute("attach", Boolean.toString(this.attachImages));
			
		return el;
	}
	
	public Element getMaxTestToShowAsElement(){
		/*
		  <max-tests-to-show value="5" />
		 */
		
		Element el = new Element("max-tests-to-show");
			el.setAttribute("value", Integer.toString(this.maxTestsToShow));
			
		return el;
	}

	public Element getMaxErrorLinesToShowAsElement(){
		/*
		  <max-error-lines-to-show value="50" />
		 */
		
		Element el = new Element("max-error-lines-to-show");
			el.setAttribute("value", Integer.toString(this.maxErrorLinesToShow));
			
		return el;
	}
	
	
	public Integer getSmtpPort() {
		return this.smtpPort;
	}

	public void setSmtpPort(Integer smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public String getSmtpUsername() {
		return smtpUsername;
	}

	public void setSmtpUsername(String smtpUsername) {
		this.smtpUsername = smtpUsername;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}
	
	public boolean getStartTLSEnabled() {
		return starttlsEnabled;
	}
	
	public void setStartTLSEnabled(boolean starttslenabled) {
		this.starttlsEnabled = starttslenabled;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePrefix) {
		this.templatePath = templatePrefix;
	}

	public String getAttachmentPath() {
		return attachmentPath;
	}

	public void setAttachmentPath(String attachmentPath) {
		this.attachmentPath = attachmentPath;
	}

	public Integer getMaxTestsToShow() {
		return maxTestsToShow;
	}

	public void setMaxTestsToShow(Integer maxTestsToShow) {
		this.maxTestsToShow = maxTestsToShow;
	}

	public Integer getMaxErrorLinesToShow() {
		return maxErrorLinesToShow;
	}

	public void setMaxErrorLinesToShow(Integer maxErrorLinesToShow) {
		this.maxErrorLinesToShow = maxErrorLinesToShow;
	}

	public boolean getAttachImages() {
		return this.attachImages ;
	}

	public void setAttachImages(boolean attachImages) {
		this.attachImages = attachImages;
	}

	public Properties getMailProperties() {
		final Properties props = new Properties();
		props.setProperty("mail.smtp.starttls.enable", String.valueOf(this.starttlsEnabled));
		return props;
	}
}