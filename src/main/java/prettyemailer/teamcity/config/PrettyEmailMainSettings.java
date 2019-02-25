package prettyemailer.teamcity.config;

import jetbrains.buildServer.serverSide.MainConfigProcessor;
import jetbrains.buildServer.serverSide.SBuildServer;
import prettyemailer.teamcity.Loggers;

import org.jdom.Element;

public class PrettyEmailMainSettings implements MainConfigProcessor {
	private PrettyEmailMainConfig prettyEmailMainConfig;
	private SBuildServer server;
	private boolean settingsExist = false;

	public PrettyEmailMainSettings(SBuildServer server){
		Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: Constructor called");
		this.server = server;
		prettyEmailMainConfig = new PrettyEmailMainConfig();
	}

    public void register(){
        Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: Registering");
        server.registerExtension(MainConfigProcessor.class, "pretty-email", this);
    }
	
    public void readFrom(Element rootElement)
    /* Is passed an Element by TC, and is expected to persist it to the settings object.
     * Old settings should be overwritten.
     */
    {
    	Loggers.SERVER.info(this.getClass().getSimpleName() + " :: re-reading main settings");
    	Loggers.SERVER.debug(this.getClass().getSimpleName() + ":readFrom :: " + rootElement.toString());
    	PrettyEmailMainConfig tempConfig = new PrettyEmailMainConfig();
    	Element emailElement = rootElement.getChild("pretty-email");
    	if(emailElement != null){
    		this.settingsExist = true;
			Element smtpElement = emailElement.getChild("smtp");
	        if(smtpElement != null){
       	
	        	if (smtpElement.getAttribute("host") != null){
	        		tempConfig.setSmtpHost(smtpElement.getAttributeValue("host"));
	        		Loggers.SERVER.debug(this.getClass().getSimpleName() + ":readFrom :: host " + smtpElement.getAttributeValue("host"));
	        	}
	        	
	        	if (smtpElement.getAttribute("port") != null){
	        		tempConfig.setSmtpPort(Integer.parseInt(smtpElement.getAttributeValue("port")));
	        		Loggers.SERVER.debug(this.getClass().getSimpleName() + ":readFrom :: port " + smtpElement.getAttributeValue("port"));
	        	}
	
	        	if (smtpElement.getAttribute("username") != null){
	        		tempConfig.setSmtpUsername(smtpElement.getAttributeValue("username"));
	        		Loggers.SERVER.debug(this.getClass().getSimpleName() + ":readFrom :: username " + smtpElement.getAttributeValue("username"));
	        	}
	
	        	if (smtpElement.getAttribute("password") != null){
	        		tempConfig.setSmtpPassword(smtpElement.getAttributeValue("password"));
	        		Loggers.SERVER.debug(this.getClass().getSimpleName() + ":readFrom :: password HIDDEN");
	        	}
	        	
	        	if (smtpElement.getAttribute("from-name") != null){
	        		tempConfig.setFromName(smtpElement.getAttributeValue("from-name"));
	        		Loggers.SERVER.debug(this.getClass().getSimpleName() + ":readFrom :: from-name " + smtpElement.getAttributeValue("from-name"));
	        	}
	
	        	if (smtpElement.getAttribute("from-address") != null){
	        		tempConfig.setFromAddress(smtpElement.getAttributeValue("from-address"));
	        		Loggers.SERVER.debug(this.getClass().getSimpleName() + ":readFrom :: from-address " + smtpElement.getAttributeValue("from-address"));
	        	}
	        	
	        	if (smtpElement.getAttribute("starttls-enabled") != null){
	        		tempConfig.setStartTLSEnabled(Boolean.valueOf(smtpElement.getAttributeValue("starttls-enabled")));
	        		Loggers.SERVER.debug(this.getClass().getSimpleName() + ":readFrom :: starttls-enabled " + smtpElement.getAttributeValue("starttls-enabled") + " (" + Boolean.getBoolean(smtpElement.getAttributeValue("starttls-enabled")) + ")");
	        	}
	        }
	        
			Element templatePathElement = emailElement.getChild("template-path");
	        if(templatePathElement != null){
	        	if (templatePathElement.getAttribute("path") != null){
	        		tempConfig.setTemplatePath(templatePathElement.getAttributeValue("path"));
	        		Loggers.SERVER.debug(this.getClass().getSimpleName() + ":readFrom :: template-path " + templatePathElement.getAttributeValue("path"));
	        	}
	    	}

	        Element attachmentPathElement = emailElement.getChild("attachment-path");
	        if(attachmentPathElement != null){
	        	if (attachmentPathElement.getAttribute("path") != null){
	        		tempConfig.setAttachmentPath(attachmentPathElement.getAttributeValue("path"));
	        		Loggers.SERVER.debug(this.getClass().getSimpleName() + ":readFrom :: attachment-path " + attachmentPathElement.getAttributeValue("path"));
	        	}
	    	}
	        
	        Element attachImagesElement = emailElement.getChild("attach-images");
	        if(attachImagesElement != null){
	        	if (attachImagesElement.getAttribute("attach") != null){
	        		tempConfig.setAttachImages(Boolean.valueOf(attachImagesElement.getAttributeValue("attach")));
	        		Loggers.SERVER.debug(this.getClass().getSimpleName() + ":readFrom :: attach-images " + tempConfig.getAttachImages());
	        	}
	    	}

	        Element MaxTestsElement = emailElement.getChild("max-tests-to-show");
	        if(MaxTestsElement != null){
	        	if (MaxTestsElement.getAttribute("value") != null){
	        		tempConfig.setMaxTestsToShow(Integer.valueOf(MaxTestsElement.getAttributeValue("value")));
	        		Loggers.SERVER.debug(this.getClass().getSimpleName() + ":readFrom :: max-tests-to-show " + Integer.valueOf(MaxTestsElement.getAttributeValue("value")));
	        	}
	    	}	        

	        Element MaxErrorsElement = emailElement.getChild("max-error-lines-to-show");
	        if(MaxErrorsElement != null){
	        	if (MaxErrorsElement.getAttribute("value") != null){
	        		tempConfig.setMaxErrorLinesToShow(Integer.valueOf(MaxErrorsElement.getAttributeValue("value")));
	        		Loggers.SERVER.debug(this.getClass().getSimpleName() + ":readFrom :: max-tests-to-show " + Integer.valueOf(MaxErrorsElement.getAttributeValue("value")));
	        	}
	    	}	        
	        
	    } 
    	
        this.prettyEmailMainConfig = tempConfig;
    }

    public void writeTo(Element parentElement)
    /* Is passed an (probably empty) Element by TC, which is expected to be populated from the settings
     * in memory. 
     */
    {
    	Loggers.SERVER.info(this.getClass().getSimpleName() + " :: re-writing main settings");
    	Loggers.SERVER.debug(this.getClass().getSimpleName() + ":writeTo :: " + parentElement.toString());
    	Element el = new Element("pretty-email");
        if(	  prettyEmailMainConfig != null 
           && prettyEmailMainConfig.getSmtpHost() != null 
           && prettyEmailMainConfig.getSmtpPort() != null && prettyEmailMainConfig.getSmtpPort() > 0 )
        {
        	el.addContent(prettyEmailMainConfig.getSmtpAsElement());
			Loggers.SERVER.debug(this.getClass().getSimpleName() + "writeTo :: smtpHost " + prettyEmailMainConfig.getSmtpHost().toString());
			Loggers.SERVER.debug(this.getClass().getSimpleName() + "writeTo :: smtpPort " + prettyEmailMainConfig.getSmtpPort().toString());
        }
        
        if (   prettyEmailMainConfig != null
        	&& prettyEmailMainConfig.getTemplatePath() != null)
        {
        	el.addContent(prettyEmailMainConfig.getTemplatePathAsElement());
        }
        
        if (   prettyEmailMainConfig != null
            	&& prettyEmailMainConfig.getAttachmentPath() != null)
        {
        	el.addContent(prettyEmailMainConfig.getAttachmentPathAsElement());
        }

        if (   prettyEmailMainConfig != null )
        {
        	el.addContent(prettyEmailMainConfig.getAttachImagesAsElement());
        }

        if (   prettyEmailMainConfig != null 
        		&& prettyEmailMainConfig.defaultMaxTestsToShow != prettyEmailMainConfig.getMaxTestsToShow())
        {
        	el.addContent(prettyEmailMainConfig.getMaxTestToShowAsElement());
        }
        
        if (   prettyEmailMainConfig != null 
        		&& prettyEmailMainConfig.defaultMaxErrorLinesToShow != prettyEmailMainConfig.getMaxErrorLinesToShow())
        {
        	el.addContent(prettyEmailMainConfig.getMaxErrorLinesToShowAsElement());
        }
        
        parentElement.addContent(el);
    }
    
    public boolean doSettingsExist(){
    	return this.settingsExist;
    }
    
	public void dispose() {
		Loggers.SERVER.debug(this.getClass().getSimpleName() + ":dispose() called");
	}

	public PrettyEmailMainConfig getConfig() {
		return this.prettyEmailMainConfig;	
	}
}
