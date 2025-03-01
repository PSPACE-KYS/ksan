/*
* Copyright (c) 2021 PSPACE, inc. KSAN Development Team ksan@pspace.co.kr
* KSAN is a suite of free software: you can redistribute it and/or modify it under the terms of
* the GNU General Public License as published by the Free Software Foundation, either version
* 3 of the License.See LICENSE for details
*
* 본 프로그램 및 관련 소스코드, 문서 등 모든 자료는 있는 그대로 제공이 됩니다.
* KSAN 프로젝트의 개발자 및 개발사는 이 프로그램을 사용한 결과에 따른 어떠한 책임도 지지 않습니다.
* KSAN 개발팀은 사전 공지, 허락, 동의 없이 KSAN 개발에 관련된 모든 결과물에 대한 LICENSE 방식을 변경 할 권리가 있습니다.
*/
using System;
using PortalData.Enums;
using MTLib.CommonData.Interfaces;

namespace PortalData.Responses.Networks
{
	/// <summary>네트워크 인터페이스 정보 응답 클래스</summary>
	public class ResponseNetworkInterface : IModifier
	{
		/// <summary>네트워크 인터페이스 아이디</summary>
		public string Id { get; set; }

		/// <summary>서버 아이디</summary>
		public string ServerId { get; set; }

		/// <summary>인터페이스명</summary>
		public string Name { get; set; }

		/// <summary>설명</summary>
		public string Description { get; set; }

		/// <summary>DHCP 사용 여부</summary>
		public EnumYesNo? Dhcp { get; set; }

		/// <summary>맥주소</summary>
		public string MacAddress { get; set; }

		/// <summary>네트워크 연결 상태</summary>
		public EnumNetworkLinkState? LinkState { get; set; }

		/// <summary>아이피 주소</summary>
		public string IpAddress { get; set; }

		/// <summary>서브넷 마스크</summary>
		public string SubnetMask { get; set; }

		/// <summary>게이트웨이</summary>
		public string Gateway { get; set; }

		/// <summary>DNS #1</summary>
		public string Dns1 { get; set; }

		/// <summary>DNS #2</summary>
		public string Dns2 { get; set; }

		/// <summary>네트워크 BandWidth</summary>
		public decimal? BandWidth { get; set; }

		/// <summary>관리용 인터페이스인지 여부</summary>
		public bool IsManagement { get; set; }

		/// <summary>수신 속도</summary>
		public decimal? Rx { get; set; }

		/// <summary>송신 속도</summary>
		public decimal? Tx { get; set; }

		/// <summary>수정일시</summary>
		public DateTime? ModDate { get; set; } = null;

		/// <summary>수정인 아이디</summary>
		public string ModId { get; set; } = "";

		/// <summary>수정인 이름</summary>
		public string ModName { get; set; } = "";

	}
}