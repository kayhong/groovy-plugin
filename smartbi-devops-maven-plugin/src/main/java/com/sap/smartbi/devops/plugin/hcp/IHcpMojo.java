package com.sap.smartbi.devops.plugin.hcp;

public interface IHcpMojo {
	void setAccount(final String account);

	void setHost(final String host);
	
	void setPassword(final String password);

	void setProxyHost(final String proxyHost);

	void setProxyPort(int proxyPort);

	void setUser(final String user);
}
