﻿/*
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
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Data.Common;
using System.Linq;
using System.Linq.Expressions;

namespace PortalModels
{
	/// <summary> 접속이 허용된 아이피 </summary>
	public partial class AllowedConnectionIp
	{

		public AllowedConnectionIp()
		{
			OnCreated();
		}

		/// <summary> 접속 허용 아이피 아이디 </summary>
		public virtual Guid Id { get; set; }

		/// <summary> 역할(권한그룹) 아이디 </summary>
		public virtual Guid? RoleId { get; set; }

		/// <summary> 아이피 주소 문자열 </summary>
		public virtual string IpAddress { get; set; }

		/// <summary> 아이피 범위 시작 숫자값 </summary>
		public virtual long StartAddress { get; set; }

		/// <summary> 아이피 범위 종료 숫자값 </summary>
		public virtual long EndAddress { get; set; }

		/// <summary> 등록자 아이디 </summary>
		public virtual Guid? RegId { get; set; }

		/// <summary> 등록자명 </summary>
		public virtual string RegName { get; set; }

		/// <summary> 등록일시 </summary>
		public virtual DateTime RegDate { get; set; }

		/// <summary> 역할 정보 </summary>
		public virtual Role Role { get; set; }

		/// <summary> 등록 사용자 </summary>
		public virtual User RegUser { get; set; }

		#region Extensibility Method Definitions

		partial void OnCreated();

		#endregion
	}

}
