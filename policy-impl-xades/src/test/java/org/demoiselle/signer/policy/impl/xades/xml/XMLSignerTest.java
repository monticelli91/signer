package org.demoiselle.signer.policy.impl.xades.xml;

import static org.junit.Assert.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStore.Builder;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Enumeration;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.demoiselle.signer.core.keystore.loader.KeyStoreLoader;
import org.demoiselle.signer.core.keystore.loader.factory.KeyStoreLoaderFactory;
import org.demoiselle.signer.policy.impl.xades.XMLPoliciesOID;
import org.demoiselle.signer.policy.impl.xades.util.DocumentUtils;
import org.demoiselle.signer.policy.impl.xades.xml.impl.Constants;
import org.demoiselle.signer.policy.impl.xades.xml.impl.XMLSigner;
import org.junit.Test;
import org.w3c.dom.Document;

public class XMLSignerTest {

	@Test
	public void testEnvelopedFromFile() {

		try {
			KeyStore ks = null;

			// window ou NeoID
			ks = getKeyStoreTokenBySigner();

			// arquivo
			// ks = getKeyStoreFileBySigner();

			// token
			// ks = getKeyStoreToken();

			String fileName = "teste_assinatura.xml";

			ClassLoader classLoader = getClass().getClassLoader();
			URL fileUri = classLoader.getResource(fileName);
			File newFile = new File(fileUri.toURI());

			String alias = getAlias(ks);
			XMLSigner xmlSigner = new XMLSigner();

			// para token
			xmlSigner.setPrivateKey((PrivateKey) ks.getKey(alias, null));

			// para arquivo
			// quando certificado em arquivo, precisa informar a senha
			// char[] senha = "teste".toCharArray();
			// xmlSigner.setPrivateKey((PrivateKey) ks.getKey(alias, senha));

			xmlSigner.setCertificateChain(ks.getCertificateChain(alias));
			// para mudar a politica de Assinatura
			xmlSigner.setPolicyId(XMLPoliciesOID.AD_RT_XADES_2_4.getOID());
			
			xmlSigner.setSignatureAlgorithm(Constants.SHA512withRSA);
			if (org.demoiselle.signer.core.keystore.loader.configuration.Configuration.getInstance().getSO().toLowerCase().indexOf("indows") > 0) {
				xmlSigner.setSignatureAlgorithm(Constants.SHA256withRSA);
				
			}	
			
			// indicando o local do arquivo XML
			Document doc = xmlSigner.signEnveloped(true, newFile.getPath());

			String signedFile = fileName.replaceFirst(".xml$", "_rt_signed.xml");
			OutputStream os = new FileOutputStream("src/test/resources/" + signedFile);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			trans.transform(new DOMSource(doc), new StreamResult(os));

		} catch (TransformerException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (Throwable e) {
			e.printStackTrace();
			assertFalse(true);
		}

	}

	// @Test
	public void testEnvelopedFromString() {

		try {

			String xmlAsString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<raiz>\n"
					+ " <documento>um documento</documento>\n" + " <conteudo>texto para assinar</conteudo>\n"
					+ "</raiz>";
			KeyStore ks = null;

			// window ou NeoID
			ks = getKeyStoreTokenBySigner();

			// arquivo
			// ks = getKeyStoreFileBySigner();

			// token
			// ks = getKeyStoreToken();

			String alias = getAlias(ks);
			XMLSigner xmlSigner = new XMLSigner();

			// para token
			xmlSigner.setPrivateKey((PrivateKey) ks.getKey(alias, null));

			// para arquivo
			// quando certificado em arquivo, precisa informar a senha
			// char[] senha = "teste".toCharArray();
			// xmlSigner.setPrivateKey((PrivateKey) ks.getKey(alias, senha));

			xmlSigner.setCertificateChain(ks.getCertificateChain(alias));
			// para mudar a politica
			xmlSigner.setPolicyId(XMLPoliciesOID.AD_RT_XADES_2_4.getOID());
			// indicando o local do arquivo XML
			Document doc = xmlSigner.signEnveloped(xmlAsString);

			String signedFile = "from_string_rt_signed.xml";
			OutputStream os = new FileOutputStream("src/test/resources/" + signedFile);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			trans.transform(new DOMSource(doc), new StreamResult(os));

		} catch (TransformerException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (Throwable e) {
			e.printStackTrace();
			assertFalse(true);
		}

	}

	// @Test
	public void testEnvelopedFromDocument() {

		try {
			KeyStore ks = null;

			// window ou NeoID
			ks = getKeyStoreTokenBySigner();

			// arquivo
			// ks = getKeyStoreFileBySigner();

			// token
			// ks = getKeyStoreToken();

			String fileName = "teste_assinatura.xml";

			ClassLoader classLoader = getClass().getClassLoader();
			Document doc = DocumentUtils.loadXMLDocument(classLoader.getResourceAsStream(fileName));

			String alias = getAlias(ks);
			XMLSigner xmlSigner = new XMLSigner();

			// para token
			xmlSigner.setPrivateKey((PrivateKey) ks.getKey(alias, null));

			// para arquivo
			// quando certificado em arquivo, precisa informar a senha
			// char[] senha = "teste".toCharArray();
			// xmlSigner.setPrivateKey((PrivateKey) ks.getKey(alias, senha));

			xmlSigner.setCertificateChain(ks.getCertificateChain(alias));
			// para mudar a politica
			xmlSigner.setPolicyId(XMLPoliciesOID.AD_RT_XADES_2_4.getOID());
			// indicando o local do arquivo XML
			Document docSigned = xmlSigner.signEnveloped(doc);

			String signedFile = fileName.replaceFirst(".xml$", "from_doc_rt_signed.xml");
			OutputStream os = new FileOutputStream("src/test/resources/" + signedFile);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			trans.transform(new DOMSource(docSigned), new StreamResult(os));

		} catch (TransformerException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (Throwable e) {
			e.printStackTrace();
			assertFalse(true);
		}

	}

	// @Test
	public void testEnvelopedFromInputStream() {

		try {
			KeyStore ks = null;

			// window ou NeoID
			ks = getKeyStoreTokenBySigner();

			// arquivo
			// ks = getKeyStoreFileBySigner();

			// token
			// ks = getKeyStoreToken();

			String fileName = "teste_assinatura.xml";

			ClassLoader classLoader = getClass().getClassLoader();

			String alias = getAlias(ks);
			XMLSigner xmlSigner = new XMLSigner();

			// para token
			xmlSigner.setPrivateKey((PrivateKey) ks.getKey(alias, null));

			// para arquivo
			// quando certificado em arquivo, precisa informar a senha
			// char[] senha = "teste".toCharArray();
			// xmlSigner.setPrivateKey((PrivateKey) ks.getKey(alias, senha));

			xmlSigner.setCertificateChain(ks.getCertificateChain(alias));
			// para mudar a politica
			xmlSigner.setPolicyId(XMLPoliciesOID.AD_RT_XADES_2_4.getOID());
			// indicando o local do arquivo XML
			Document docSigned = xmlSigner.signEnveloped(classLoader.getResourceAsStream(fileName));

			String signedFile = fileName.replaceFirst(".xml$", "from_inputstream_rt_signed.xml");
			OutputStream os = new FileOutputStream("src/test/resources/" + signedFile);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			trans.transform(new DOMSource(docSigned), new StreamResult(os));

		} catch (TransformerException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (Throwable e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}

	//@Test
	public void testEnvelopedFromByteArray() {

		try {
			KeyStore ks = null;

			// window ou NeoID
			ks = getKeyStoreTokenBySigner();

			// arquivo
			// ks = getKeyStoreFileBySigner();

			// token
			// ks = getKeyStoreToken();

			String fileName = "teste_assinatura.xml";

			ClassLoader classLoader = getClass().getClassLoader();

			String alias = getAlias(ks);
			XMLSigner xmlSigner = new XMLSigner();

			// para token
			xmlSigner.setPrivateKey((PrivateKey) ks.getKey(alias, null));

			// para arquivo
			// quando certificado em arquivo, precisa informar a senha
			// char[] senha = "teste".toCharArray();
			// xmlSigner.setPrivateKey((PrivateKey) ks.getKey(alias, senha));

			xmlSigner.setCertificateChain(ks.getCertificateChain(alias));
			// para mudar a politica
			xmlSigner.setPolicyId(XMLPoliciesOID.AD_RT_XADES_2_4.getOID());
			// indicando o local do arquivo XML

			Document docSigned = xmlSigner
					.signEnveloped(IOUtils.toByteArray(classLoader.getResourceAsStream(fileName)));

			String signedFile = fileName.replaceFirst(".xml$", "from_bytearray_rt_signed.xml");
			OutputStream os = new FileOutputStream("src/test/resources/" + signedFile);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			trans.transform(new DOMSource(docSigned), new StreamResult(os));

		} catch (TransformerException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (Throwable e) {
			e.printStackTrace();
			assertFalse(true);
		}

	}

	// @Test
	public void testDetachedEnvelopedFromFile() {

		try {
			KeyStore ks = null;

			// window ou NeoID
			ks = getKeyStoreTokenBySigner();

			// arquivo
			// ks = getKeyStoreFileBySigner();

			// token
			// ks = getKeyStoreToken();

			String fileName = "teste_assinatura.xml";

			ClassLoader classLoader = getClass().getClassLoader();
			URL fileUri = classLoader.getResource(fileName);
			File newFile = new File(fileUri.toURI());

			String alias = getAlias(ks);
			XMLSigner xmlSigner = new XMLSigner();

			// para token
			xmlSigner.setPrivateKey((PrivateKey) ks.getKey(alias, null));

			// para arquivo
			// quando certificado em arquivo, precisa informar a senha
			// char[] senha = "teste".toCharArray();
			// xmlSigner.setPrivateKey((PrivateKey) ks.getKey(alias, senha));

			xmlSigner.setCertificateChain(ks.getCertificateChain(alias));
			// para mudar a politica
			xmlSigner.setPolicyId(XMLPoliciesOID.AD_RT_XADES_2_4.getOID());
			// indicando o local do arquivo XML
			Document doc = xmlSigner.signDetachedEnveloped(newFile.getPath());

			String signedFile = fileName.replaceFirst(".xml$", "_rt_detached_signed.xml");
			OutputStream os = new FileOutputStream("src/test/resources/" + signedFile);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			trans.transform(new DOMSource(doc), new StreamResult(os));

		} catch (TransformerException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (Throwable e) {
			e.printStackTrace();
			assertFalse(true);
		}

	}

// Usa o Signer para leitura, funciona para windows e NeoID
	private KeyStore getKeyStoreTokenBySigner() {

		try {

			KeyStoreLoader keyStoreLoader = KeyStoreLoaderFactory.factoryKeyStoreLoader();
			KeyStore keyStore = keyStoreLoader.getKeyStore();

			return keyStore;

		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		} finally {
		}

	}

	/**
	 * 
	 * Faz a leitura do token em LINUX, precisa setar a lib (.SO) e a senha do
	 * token.
	 */
	@SuppressWarnings({ "restriction", "unused" })
	private KeyStore getKeyStoreToken() {

		try {
			// ATENÇÃO ALTERAR CONFIGURAÇÃO ABAIXO CONFORME O TOKEN USADO

			// Para TOKEN Branco a linha abaixo
			// String pkcs11LibraryPath =
			// "/usr/lib/watchdata/ICP/lib/libwdpkcs_icp.so";

			// Para TOKEN Azul a linha abaixo
			String pkcs11LibraryPath = "/usr/lib/libeToken.so";

			StringBuilder buf = new StringBuilder();
			buf.append("library = ").append(pkcs11LibraryPath).append("\nname = Provedor\n");
			Provider p = new sun.security.pkcs11.SunPKCS11(new ByteArrayInputStream(buf.toString().getBytes()));
			Security.addProvider(p);
			// ATENÇÃO ALTERAR "SENHA" ABAIXO
			Builder builder = KeyStore.Builder.newInstance("PKCS11", p,
					new KeyStore.PasswordProtection("senha".toCharArray()));
			KeyStore ks;
			ks = builder.getKeyStore();

			return ks;

		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		} finally {
		}

	}

	@SuppressWarnings("unused")
	private KeyStore getKeyStoreFileBySigner() {

		try {
			// informar o caminho e nome do arquivo
			File filep12 = new File("/");

			KeyStoreLoader loader = KeyStoreLoaderFactory.factoryKeyStoreLoader(filep12);
			// Informar a senha
			KeyStore keystore = loader.getKeyStore("senha");
			return keystore;

		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		} finally {
		}

	}

	private String getAlias(KeyStore ks) {
		@SuppressWarnings("unused")
		Certificate[] certificates = null;
		String alias = "";
		Enumeration<String> e;
		try {
			e = ks.aliases();
			while (e.hasMoreElements()) {
				alias = e.nextElement();
				System.out.println("alias..............: " + alias);
				System.out.println("iskeyEntry" + ks.isKeyEntry(alias));
				System.out.println("containsAlias" + ks.containsAlias(alias));
				// System.out.println(""+ks.getKey(alias, null));
				certificates = ks.getCertificateChain(alias);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return alias;
	}

}
