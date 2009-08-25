package prettyemailer.teamcity;

import org.jdom.Element;

public class PrettyEmailMainConfig {
	private Integer smtpPort = 25;
	private String smtpHost = null;
	private String smtpUsername = null;
	private String smtpPassword = null;
	private String fromAddress = null;
	private String fromName = null;
	
	private String templatePath = null;
	private String attachmentPath = null;
	private Integer maxTestsToShow = 5;
	private boolean attachImages = true;
	

	public PrettyEmailMainConfig() {

	}

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
		  <!-- NOTE: This path is only read on TC startup, due to single 
		  		instance of the Velocity Engine which is initialised 
		  		in the Notifier register function. --> 
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
		  <!-- NOTE: This path is only read on TC startup, due to single 
		  		instance of the Velocity Engine which is initialised 
		  		in the Notifier register function. --> 
		  <attachment-path path="/opt/TeamCity/prettyEmail/attachments/img" />
		 */
		
		Element el = new Element("attachment-path");
			el.setAttribute("path", this.attachmentPath);
			
		return el;
	}

	public Element getAttachImagesAsElement(){
		/*
		  <!-- NOTE: This path is only read on TC startup, due to single 
		  		instance of the Velocity Engine which is initialised 
		  		in the Notifier register function. --> 
		  <attachment-path path="/opt/TeamCity/prettyEmail/attachments/img" />
		 */
		
		Element el = new Element("attach-images");
			el.setAttribute("attach", Boolean.toString(this.attachImages));
			
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

	public void setSmtpUsername(String proxyUsername) {
		this.smtpUsername = proxyUsername;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(String proxyPassword) {
		this.smtpPassword = proxyPassword;
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

	public boolean getAttachImages() {
		return this.attachImages ;
	}

	public void setAttachImages(boolean attachImages) {
		this.attachImages = attachImages;
	}
}