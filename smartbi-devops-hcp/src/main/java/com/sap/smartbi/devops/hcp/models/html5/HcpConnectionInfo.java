package com.sap.smartbi.devops.hcp.models.html5;

import java.net.URI;

public final class HcpConnectionInfo {
	private String account;
	private String dispatcher;
	private String host;
	private String password;
	private URI proxyUri;
	private String userName;
	
	public HcpConnectionInfo(final String dispatcher, final String host, final String account, final String userName, final String password) {
		this(dispatcher, host, account, userName, password, null);
	}
	
	public HcpConnectionInfo(final String dispatcher, final String host, final String account, final String userName, final String password, final URI proxyUri) {
		this.account = account;
		this.dispatcher = dispatcher;
		this.host = host;
		this.password = password;
		this.proxyUri = proxyUri;
		this.userName = userName;
	}
	
	public String getAccount() {
		return this.account;
	}

	public String getDispatcher() {
		return this.dispatcher;
	}

	public String getHost() {
		return this.host;
	}
	
	public String getUserName() {
		return this.userName;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public URI getProxyUri() {
		return this.proxyUri;
	}
}
