/*
* Copyright (c) 2021 PSPACE, inc. KSAN Development Team ksan@pspace.co.kr
* KSAN is a suite of free software: you can redistribute it and/or modify it under the terms of
* the GNU General Public License as published by the Free Software Foundation, either version 
* 3 of the License. See LICENSE for details
*
* 본 프로그램 및 관련 소스코드, 문서 등 모든 자료는 있는 그대로 제공이 됩니다.
* KSAN 프로젝트의 개발자 및 개발사는 이 프로그램을 사용한 결과에 따른 어떠한 책임도 지지 않습니다.
* KSAN 개발팀은 사전 공지, 허락, 동의 없이 KSAN 개발에 관련된 모든 결과물에 대한 LICENSE 방식을 변경 할 권리가 있습니다.
*/
package com.pspace.backend.libs.Ksan.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseRegion {
	@JsonProperty("Name")
	public String Name;

	@JsonProperty("Address")
	public String Address;

	@JsonProperty("Port")
	public int Port;

	@JsonProperty("SSLPort")
	public int SSLPort;

	@JsonProperty("AccessKey")
	public String AccessKey;

	@JsonProperty("SecretKey")
	public String SecretKey;

	public String getURL() {
		return String.format("http://%s:%s", Address, Port);
	}
}
