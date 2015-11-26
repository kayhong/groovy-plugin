package com.sap.smartbi.devops.plugin.hcp.html5;

public interface IHcpMojo {
	void setAccount(final String account);

	void setDispatcher(final String dispatcher);

	void setHost(final String host);
	
	void setPassword(final String password);

	void setProxyHost(final String proxyHost);

	void setProxyPort(int proxyPort);

	void setUser(final String user);
}
