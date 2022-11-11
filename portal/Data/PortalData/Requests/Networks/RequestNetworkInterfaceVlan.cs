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
using System.ComponentModel.DataAnnotations;
using PortalData.ValidationAttributes;
using PortalResources;
using MTLib.CommonData;

namespace PortalData.Requests.Networks
{
	/// <summary>네트워크 인터페이스 VLAN 설정 요청 클래스</summary>
	public class RequestNetworkInterfaceVlan : CommonRequestData
	{
		/// <summary>VLAN 태그</summary>
		[Range(2, 4094, ErrorMessageResourceName = "EM_NETWORKS_NETWORK_INTERFACE_VLAN_TAG_MUST_BETWEEN_2_4094", ErrorMessageResourceType = typeof(Resource))]
		public ushort Tag { get; set; }

		/// <summary>아이피 주소</summary>
		[IpAddress(ErrorMessageResourceName = "EM_COMMON__INVALID_IP_ADDRESS", ErrorMessageResourceType = typeof(Resource))]
		public string IpAddress { get; set; }

		/// <summary>서브넷 마스크</summary>
		[IpAddress(ErrorMessageResourceName = "EM_COMMON__INVALID_SUBNET_MASK", ErrorMessageResourceType = typeof(Resource))]
		public string SubnetMask { get; set; }

		/// <summary>게이트웨이</summary>
		[IpAddress(ErrorMessageResourceName = "EM_COMMON__INVALID_GATEWAY", ErrorMessageResourceType = typeof(Resource))]
		public string Gateway { get; set; }
	}
}