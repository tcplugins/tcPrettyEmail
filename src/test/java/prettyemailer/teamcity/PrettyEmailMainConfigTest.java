package prettyemailer.teamcity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

public class PrettyEmailMainConfigTest {

	@Test
	public void testPrettyEmailMainConfig() {
		PrettyEmailMainConfig mc = new PrettyEmailMainConfig();
		assertEquals(mc.defaultMaxTestsToShow, mc.getMaxTestsToShow());
	}

	@Test
	public void testGetSmtpAsElement() {
		PrettyEmailMainSettings ms = new PrettyEmailMainSettings(null);
		Element defaultConfig = this.getDefaultConfigElement(); 
		ms.readFrom(defaultConfig);
		Element e1 = defaultConfig.getChild("pretty-email").getChild("smtp");
		Element e2 = ms.getConfig().getSmtpAsElement();
		assertEquals(
				e1.getAttributeValue("host"), 
				e2.getAttributeValue("host")
			);
		assertEquals(
				e1.getAttributeValue("port"), 
				e2.getAttributeValue("port")
			);
		assertEquals(
				e1.getAttributeValue("from-address"), 
				e2.getAttributeValue("from-address")
			);
		assertEquals(
				e1.getAttributeValue("from-name"), 
				e2.getAttributeValue("from-name")
			);
		assertEquals(
				e1.getAttributeValue("username"), 
				e2.getAttributeValue("username")
			);
		assertEquals(
				e1.getAttributeValue("password"), 
				e2.getAttributeValue("password")
			);
	}

	@Test
	public void testGetTemplatePathAsElement() {
		PrettyEmailMainSettings ms = new PrettyEmailMainSettings(null);
		Element defaultConfig = this.getDefaultConfigElement(); 
		ms.readFrom(defaultConfig);
		assertEquals(
				defaultConfig.getChild("pretty-email").getChild("template-path").getAttributeValue("path"), 
				ms.getConfig().getTemplatePathAsElement().getAttributeValue("path")
			);
	}

	@Test
	public void testGetAttachmentPathAsElement() {
		PrettyEmailMainSettings ms = new PrettyEmailMainSettings(null);
		Element defaultConfig = this.getDefaultConfigElement(); 
		ms.readFrom(defaultConfig);
		assertEquals(
				defaultConfig.getChild("pretty-email").getChild("attachment-path").getAttributeValue("path"), 
				ms.getConfig().getAttachmentPathAsElement().getAttributeValue("path")
			);
	}

	@Test
	public void testGetAttachImagesAsElement() {
		PrettyEmailMainSettings ms = new PrettyEmailMainSettings(null);
		Element defaultConfig = this.getDefaultConfigElement(); 
		ms.readFrom(defaultConfig);
		assertEquals(
				defaultConfig.getChild("pretty-email").getChild("attach-images").getAttributeValue("attach"), 
				ms.getConfig().getAttachImagesAsElement().getAttributeValue("attach")
			);
	}

	@Test
	public void testGetMaxTestToShowAsElement() {
		PrettyEmailMainSettings ms = new PrettyEmailMainSettings(null);
		Element defaultConfig = this.getDefaultConfigElement(); 
		ms.readFrom(defaultConfig);
		assertEquals(
				defaultConfig.getChild("pretty-email").getChild("max-tests-to-show").getAttributeValue("value"), 
				ms.getConfig().getMaxTestToShowAsElement().getAttributeValue("value")
			);
	}

	@Test
	public void testGetMaxErrorLinesToShowAsElement() {
		PrettyEmailMainSettings ms = new PrettyEmailMainSettings(null);
		Element defaultConfig = this.getDefaultConfigElement(); 
		ms.readFrom(defaultConfig);
		assertEquals(
				defaultConfig.getChild("pretty-email").getChild("max-error-lines-to-show").getAttributeValue("value"), 
				ms.getConfig().getMaxErrorLinesToShowAsElement().getAttributeValue("value")
			);
	}

	@Test
	public void testGetSmtpPort() {
		PrettyEmailMainConfig mc = new PrettyEmailMainConfig();
		mc.setSmtpPort(100);
		assertEquals(Integer.valueOf(100), mc.getSmtpPort());
	}

	@Test
	public void testSetSmtpPort() {
		testGetSmtpPort();
	}

	@Test
	public void testGetSmtpHost() {
		PrettyEmailMainConfig mc = new PrettyEmailMainConfig();
		mc.setSmtpHost("foobar.foo.bar");
		assertEquals("foobar.foo.bar", mc.getSmtpHost());
	}

	@Test
	public void testSetSmtpHost() {
		testGetSmtpHost();
	}

	@Test
	public void testGetSmtpUsername() {
		PrettyEmailMainConfig mc = new PrettyEmailMainConfig();
		mc.setSmtpUsername("foobar");
		assertEquals("foobar", mc.getSmtpUsername());
	}

	@Test
	public void testSetSmtpUsername() {
		testGetSmtpUsername();
	}

	@Test
	public void testGetSmtpPassword() {
		PrettyEmailMainConfig mc = new PrettyEmailMainConfig();
		mc.setSmtpPassword("barfoo");
		assertEquals("barfoo", mc.getSmtpPassword());
	}

	@Test
	public void testSetSmtpPassword() {
		testGetSmtpPassword();
	}

	@Test
	public void testGetFromAddress() {
		PrettyEmailMainConfig mc = new PrettyEmailMainConfig();
		mc.setFromAddress("barfoo@example.com");
		assertEquals("barfoo@example.com", mc.getFromAddress());
	}

	@Test
	public void testSetFromAddress() {
		testGetFromAddress();
	}

	@Test
	public void testGetFromName() {
		PrettyEmailMainConfig mc = new PrettyEmailMainConfig();
		mc.setFromName("Foo Bar");
		assertTrue(mc.getFromName().equals("Foo Bar"));
	}


	@Test
	public void testSetFromName() {
		testGetFromName();
	}

	@Test
	public void testGetTemplatePath() {
		PrettyEmailMainConfig mc = new PrettyEmailMainConfig();
		mc.setTemplatePath("/opt/Foo/Bar");
		assertEquals("/opt/Foo/Bar", mc.getTemplatePath());
	}

	@Test
	public void testSetTemplatePath() {
		testGetTemplatePath();
	}

	@Test
	public void testGetAttachmentPath() {
		PrettyEmailMainConfig mc = new PrettyEmailMainConfig();
		mc.setAttachmentPath("/opt/Bar/Foo/Bar");
		assertEquals("/opt/Bar/Foo/Bar", mc.getAttachmentPath());
	}

	@Test
	public void testSetAttachmentPath() {
		testGetAttachmentPath();
	}

	@Test
	public void testGetMaxTestsToShow() {
		PrettyEmailMainConfig mc = new PrettyEmailMainConfig();
		assertEquals(mc.defaultMaxTestsToShow, mc.getMaxTestsToShow());
	}

	@Test
	public void testSetMaxTestsToShow() {
		PrettyEmailMainConfig mc = new PrettyEmailMainConfig();
		mc.setMaxTestsToShow(1000);
		assertEquals(Integer.valueOf(1000), mc.getMaxTestsToShow());
	}

	@Test
	public void testGetMaxErrorLinesToShow() {
		PrettyEmailMainConfig mc = new PrettyEmailMainConfig();
		assertEquals(mc.defaultMaxErrorLinesToShow, mc.getMaxErrorLinesToShow());
	}

	@Test
	public void testSetMaxErrorLinesToShow() {
		PrettyEmailMainConfig mc = new PrettyEmailMainConfig();
		mc.setMaxErrorLinesToShow(1001);
		assertEquals(Integer.valueOf(1001), mc.getMaxErrorLinesToShow());
	}

	@Test
	public void testGetAttachImages() {
		PrettyEmailMainConfig mc = new PrettyEmailMainConfig();
		assertTrue(mc.getAttachImages());
		mc.setAttachImages(false);
		assertFalse(mc.getAttachImages());
	}

	@Test
	public void testSetAttachImages() {
		testGetAttachImages();
	}

	private Element getFullConfigElement(){
		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);
		try {
			Document doc = builder.build("src/test/resources/main-config-full.xml");
			return doc.getRootElement();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private Element getMinimalConfigElement(){
		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);
		try {
			Document doc = builder.build("src/test/resources/main-config-minimal.xml");
			return doc.getRootElement();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private Element getDefaultConfigElement() {
		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);
		try {
			Document doc = builder.build("src/test/resources/main-config-default.xml");
			return doc.getRootElement();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
}
