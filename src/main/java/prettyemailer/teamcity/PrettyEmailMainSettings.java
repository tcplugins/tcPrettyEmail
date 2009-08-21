package prettyemailer.teamcity;

import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.MainConfigProcessor;
import jetbrains.buildServer.serverSide.SBuildServer;

import org.jdom.Element;

public class PrettyEmailMainSettings implements MainConfigProcessor {
	private PrettyEmailMainConfig prettyEmailMainConfig;
	private SBuildServer server;
	private boolean SettingsExist;

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
			Element smtpElement = emailElement.getChild("smtp");
	        if(smtpElement != null){
       	
	        	if (smtpElement.getAttribute("host") != null){
	        		tempConfig.setSmtpHost(smtpElement.getAttributeValue("host"));
	        	}
	        	
	        	if (smtpElement.getAttribute("port") != null){
	        		tempConfig.setSmtpPort(Integer.parseInt(smtpElement.getAttributeValue("port")));
	        	}
	
	        	if (smtpElement.getAttribute("username") != null){
	        		tempConfig.setSmtpUsername(smtpElement.getAttributeValue("username"));
	        	}
	
	        	if (smtpElement.getAttribute("password") != null){
	        		tempConfig.setSmtpPassword(smtpElement.getAttributeValue("password"));
	        	}
	        	
	        	if (smtpElement.getAttribute("from-name") != null){
	        		tempConfig.setFromName(smtpElement.getAttributeValue("from-name"));
	        	}
	
	        	if (smtpElement.getAttribute("from-address") != null){
	        		tempConfig.setFromAddress(smtpElement.getAttributeValue("from-address"));
	        	}
	        }
	        
			Element templatePathElement = emailElement.getChild("template-path");
	        if(templatePathElement != null){
	        	if (templatePathElement.getAttribute("path") != null){
	        		tempConfig.setTemplatePath(smtpElement.getAttributeValue("path"));
	        	}
	    	}

	        Element attachmentPathElement = emailElement.getChild("attachment-path");
	        if(attachmentPathElement != null){
	        	if (attachmentPathElement.getAttribute("path") != null){
	        		tempConfig.setAttachmentPath(smtpElement.getAttributeValue("path"));
	        	}
	    	}
	        this.SettingsExist = true;
    	} else {
    		this.SettingsExist = false;
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
        
        parentElement.addContent(el);
    }
    
    public boolean doSettingsExist(){
    	return this.SettingsExist;
    }
    
	public void dispose() {
		Loggers.SERVER.debug(this.getClass().getSimpleName() + ":dispose() called");
	}

	public PrettyEmailMainConfig getConfig() {
		return this.prettyEmailMainConfig;	
	}
}
