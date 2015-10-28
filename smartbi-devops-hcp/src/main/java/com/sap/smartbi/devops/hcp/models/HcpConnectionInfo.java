package com.sap.smartbi.devops.hcp.models;

import java.net.URI;

public final class HcpConnectionInfo {
	private String host;
	private String account;
	private String userName;
	private String password;
	private URI proxyUri;
	
	public HcpConnectionInfo(final String host, final String account, final String userName, final String password) {
		this(host, account, userName, password, null);
	}
	
	public HcpConnectionInfo(final String host, final String account, final String userName, final String password, final URI proxyUri) {
		this.host = host;
		this.account = account;
		this.userName = userName;
		this.password = password;
		this.proxyUri = proxyUri;
	}
	
	public String getAccount() {
		return this.account;
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
