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
using PortalResources;
using MTLib.CommonData;

namespace PortalData.Requests.Services
{
	/// <summary>서비스 사용 정보 수정 요청 클래스</summary>
	public class RequestServiceUsage : CommonRequestData
	{
		/// <summary>서비스 아이디</summary>
		[Required(ErrorMessageResourceName = "EM_SERVICES_REQUIRE_ID", ErrorMessageResourceType = typeof(Resource))]
		public string Id { get; set; }

		/// <summary>CPU 사용률</summary>
		[Range(0, 100, ErrorMessageResourceName = "EM_SERVICES_CPU_USAGE_SHOULD_BE_BETWEEN_0_TO_100", ErrorMessageResourceType = typeof(Resource))]
		public float CpuUsage { get; set; }

		/// <summary>사용 메모리 크기</summary>
		[Range(0, 999999999999999, ErrorMessageResourceName = "EM_SERVICES_MEMORY_SIZE_MUST_BE_AT_LEAST_0", ErrorMessageResourceType = typeof(Resource))]
		public decimal MemoryUsed { get; set; }

		/// <summary>스레드 수</summary>
		[Range(0, 999999999999999, ErrorMessageResourceName = "EM_SERVICES_THREAD_COUNT_MUST_BE_AT_LEAST_0", ErrorMessageResourceType = typeof(Resource))]
		public int ThreadCount { get; set; }
	}
}