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
using System.Net;
using System.Threading.Tasks;
using PortalData;
using PortalData.Enums;
using PortalProviderInterface;
using PortalSvr.Services;
using Microsoft.AspNetCore.Cors;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using MTLib.AspNetCore;
using Swashbuckle.AspNetCore.Annotations;
using PortalData.Responses.Configs;
using PortalData.Requests.Configs;
using Microsoft.Extensions.Logging;

namespace PortalSvr.Controllers.Config
{
	/// <summary>설정 컨트롤러</summary>
	[EnableCors("CorsPolicy")]
	[Produces("application/json")]
	[Route("api/v1/[controller]")]
	[ApiKeyAuthorize]
	public class ConfigController : BaseController
	{
		/// <summary>설정 데이터 프로바이더</summary>
		private readonly IConfigProvider m_dataProvider;

		/// <summary>생성자</summary>
		/// <param name="configuration">설정 정보</param>
		/// <param name="userManager">사용자 관리자</param>
		/// <param name="logger">로거</param>
		/// <param name="dataProvider">설정 데이터 프로바이더</param>
		public ConfigController(
			IConfiguration configuration,
			UserManager<NNApplicationUser> userManager,
			ILogger<ConfigController> logger,
			IConfigProvider dataProvider
		)
			: base(configuration, userManager, logger, dataProvider)
		{
			m_dataProvider = dataProvider;
		}

		#region All Config
		/// <summary>설정 목록을 가져온다.</summary>
		/// <param name="Request">서비스 설정 정보 객체</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseList<ResponseServiceConfig>))]
		[HttpGet("List")]
		public async Task<ActionResult> GetConfigList([FromBody] RequestServiceConfig Request)
		{
			return Json(await m_dataProvider.GetConfigList(Request.Type));
		}

		/// <summary>설정을 가져온다.</summary>
		/// <param name="Request">서비스 설정 정보 객체</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseServiceConfig>))]
		[HttpGet()]
		public async Task<ActionResult> GetConfig([FromBody] RequestServiceConfig Request)
		{
			return Json(await m_dataProvider.GetConfig(Request.Type));
		}

		/// <summary>특정 버전의 설정을 가져온다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseServiceConfig>))]
		[HttpGet("{Version}")]
		public async Task<ActionResult> GetConfig([FromRoute] int Version)
		{
			return Json(await m_dataProvider.GetConfig(EnumServiceType.ksanOSD, Version));
		}

		/// <summary>설정을 저장한다.</summary>
		/// <param name="Request">서비스 설정 정보 객체</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseUpdateConfig>))]
		[HttpPost()]
		public async Task<ActionResult> SetConfig([FromBody] RequestServiceConfig Request)
		{
			return Json(await m_dataProvider.SetConfig(Request));
		}
		#endregion

		#region RabbitMQ
		/// <summary>RabbitMQ 설정을 가져온다.</summary>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseServiceConfig>))]
		[HttpGet("RabbitMQ")]
		public async Task<ActionResult> GetConfigForRabbitMQ()
		{
			return Json(await m_dataProvider.GetConfig(EnumServiceType.RabbitMQ));
		}
		#endregion
		#region MariaDB
		/// <summary>MariaDB 설정을 가져온다.</summary>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseServiceConfig>))]
		[HttpGet("MariaDB")]
		public async Task<ActionResult> GetConfigForMariaDB()
		{
			return Json(await m_dataProvider.GetConfig(EnumServiceType.MariaDB));
		}
		#endregion
		#region MongoDB
		/// <summary>MongoDB 설정을 가져온다.</summary>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseServiceConfig>))]
		[HttpGet("MongoDB")]
		public async Task<ActionResult> GetConfigForMongoDB()
		{
			return Json(await m_dataProvider.GetConfig(EnumServiceType.MongoDB));
		}
		#endregion
		#region KsanOSD
		/// <summary>KsanOSD 설정 목록을 가져온다.</summary>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseList<ResponseServiceConfig>))]
		[HttpGet("List/KsanOSD")]
		public async Task<ActionResult> GetConfigListForKsanOSD()
		{
			return Json(await m_dataProvider.GetConfigList(EnumServiceType.ksanOSD));
		}

		/// <summary>KsanOSD 설정을 가져온다.</summary>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseServiceConfig>))]
		[HttpGet("KsanOSD")]
		public async Task<ActionResult> GetConfigForKsanOSD()
		{
			return Json(await m_dataProvider.GetConfig(EnumServiceType.ksanOSD));
		}
		/// <summary>특정 버전의 KsanOSD 설정을 가져온다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseServiceConfig>))]
		[HttpGet("KsanOSD/{Version}")]
		public async Task<ActionResult> GetConfigForKsanOSD([FromRoute] int Version)
		{
			return Json(await m_dataProvider.GetConfig(EnumServiceType.ksanOSD, Version));
		}

		/// <summary>KsanOSD 설정을 저장한다.</summary>
		/// <param name="Config">서비스 설정 정보</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseUpdateConfig>))]
		[HttpPost("KsanOSD")]
		public async Task<ActionResult> SetConfigForKsanOSD([FromBody] string Config)
		{
			return Json(await m_dataProvider.SetConfig(new RequestServiceConfig() { Type = EnumServiceType.ksanOSD, Config = Config }));
		}

		/// <summary>KsanOSD 설정의 버전을 변경한다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseUpdateConfig>))]
		[HttpPut("KsanOSD/{Version}")]
		public async Task<ActionResult> SetConfigLastVersionForKsanOSD([FromRoute] int Version)
		{
			return Json(await m_dataProvider.SetConfigLastVersion(EnumServiceType.ksanOSD, Version));
		}

		/// <summary>KsanOSD 설정의 버전을 삭제한다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData))]
		[HttpDelete("KsanOSD/{Version}")]
		public async Task<ActionResult> RemoveConfigForKsanOSD([FromRoute] int Version)
		{
			return Json(await m_dataProvider.RemoveConfig(EnumServiceType.ksanOSD, Version));
		}
		#endregion
		#region KsanGW
		/// <summary>KsanGW 설정 목록을 가져온다.</summary>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseList<ResponseServiceConfig>))]
		[HttpGet("List/KsanGW")]
		public async Task<ActionResult> GetConfigListForKsanGW()
		{
			return Json(await m_dataProvider.GetConfigList(EnumServiceType.ksanGW));
		}

		/// <summary>KsanGW 설정을 가져온다.</summary>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseServiceConfig>))]
		[HttpGet("KsanGW")]
		public async Task<ActionResult> GetConfigForKsanGW()
		{
			return Json(await m_dataProvider.GetConfig(EnumServiceType.ksanGW));
		}

		/// <summary>특정 버전의 KsanGW 설정을 가져온다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseServiceConfig>))]
		[HttpGet("KsanGW/{Version}")]
		public async Task<ActionResult> GetConfigForKsanGW([FromRoute] int Version)
		{
			return Json(await m_dataProvider.GetConfig(EnumServiceType.ksanGW, Version));
		}

		/// <summary>KsanGW 설정을 저장한다.</summary>
		/// <param name="Config">서비스 설정 정보</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseUpdateConfig>))]
		[HttpPost("KsanGW")]
		public async Task<ActionResult> SetConfigForKsanGW([FromBody] string Config)
		{
			return Json(await m_dataProvider.SetConfig(new RequestServiceConfig() { Type = EnumServiceType.ksanGW, Config = Config }));
		}

		/// <summary>KsanGW 설정의 버전을 변경한다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseUpdateConfig>))]
		[HttpPut("KsanGW/{Version}")]
		public async Task<ActionResult> SetConfigLastVersionForKsanGW([FromRoute] int Version)
		{
			return Json(await m_dataProvider.SetConfigLastVersion(EnumServiceType.ksanGW, Version));
		}

		/// <summary>KsanGW 설정의 버전을 삭제한다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData))]
		[HttpDelete("KsanGW/{Version}")]
		public async Task<ActionResult> RemoveConfigForKsanGW([FromRoute] int Version)
		{
			return Json(await m_dataProvider.RemoveConfig(EnumServiceType.ksanGW, Version));
		}
		#endregion
		#region KsanRecovery
		/// <summary>KsanRecovery 설정 목록을 가져온다.</summary>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseList<ResponseServiceConfig>))]
		[HttpGet("List/KsanRecovery")]
		public async Task<ActionResult> GetConfigListForKsanRecovery()
		{
			return Json(await m_dataProvider.GetConfigList(EnumServiceType.ksanRecovery));
		}

		/// <summary>KsanRecovery 설정을 가져온다.</summary>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseServiceConfig>))]
		[HttpGet("KsanRecovery")]
		public async Task<ActionResult> GetConfigForKsanRecovery()
		{
			return Json(await m_dataProvider.GetConfig(EnumServiceType.ksanRecovery));
		}

		/// <summary>특정 버전의 KsanRecovery 설정을 가져온다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseServiceConfig>))]
		[HttpGet("KsanRecovery/{Version}")]
		public async Task<ActionResult> GetConfigForKsanRecovery([FromRoute] int Version)
		{
			return Json(await m_dataProvider.GetConfig(EnumServiceType.ksanRecovery, Version));
		}

		/// <summary>KsanRecovery 설정을 저장한다.</summary>
		/// <param name="Config">서비스 설정 정보</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseUpdateConfig>))]
		[HttpPost("KsanRecovery")]
		public async Task<ActionResult> SetConfigForKsanRecovery([FromBody] string Config)
		{
			return Json(await m_dataProvider.SetConfig(new RequestServiceConfig() { Type = EnumServiceType.ksanRecovery, Config = Config }));
		}

		/// <summary>KsanRecovery 설정의 버전을 변경한다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseUpdateConfig>))]
		[HttpPut("KsanRecovery/{Version}")]
		public async Task<ActionResult> SetConfigLastVersionForKsanRecovery([FromRoute] int Version)
		{
			return Json(await m_dataProvider.SetConfigLastVersion(EnumServiceType.ksanRecovery, Version));
		}

		/// <summary>KsanRecovery 설정의 버전을 삭제한다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData))]
		[HttpDelete("KsanRecovery/{Version}")]
		public async Task<ActionResult> RemoveConfigForKsanRecovery([FromRoute] int Version)
		{
			return Json(await m_dataProvider.RemoveConfig(EnumServiceType.ksanRecovery, Version));
		}
		#endregion
		#region KsanLifecycle
		/// <summary>KsanLifecycle 설정 목록을 가져온다.</summary>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseList<ResponseServiceConfig>))]
		[HttpGet("List/KsanLifecycle")]
		public async Task<ActionResult> GetConfigListForKsanLifecycle()
		{
			return Json(await m_dataProvider.GetConfigList(EnumServiceType.ksanLifecycle));
		}

		/// <summary>KsanLifecycle 설정을 가져온다.</summary>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseServiceConfig>))]
		[HttpGet("KsanLifecycle")]
		public async Task<ActionResult> GetConfigForKsanLifecycle()
		{
			return Json(await m_dataProvider.GetConfig(EnumServiceType.ksanLifecycle));
		}

		/// <summary>특정 버전의 KsanLifecycle 설정을 가져온다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseServiceConfig>))]
		[HttpGet("KsanLifecycle/{Version}")]
		public async Task<ActionResult> GetConfigForKsanLifecycle([FromRoute] int Version)
		{
			return Json(await m_dataProvider.GetConfig(EnumServiceType.ksanLifecycle, Version));
		}

		/// <summary>KsanLifecycle 설정을 저장한다.</summary>
		/// <param name="Config">서비스 설정 정보</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseUpdateConfig>))]
		[HttpPost("KsanLifecycle")]
		public async Task<ActionResult> SetConfigForKsanLifecycle([FromBody] string Config)
		{
			return Json(await m_dataProvider.SetConfig(new RequestServiceConfig() { Type = EnumServiceType.ksanLifecycle, Config = Config }));
		}

		/// <summary>KsanLifecycle 설정의 버전을 변경한다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseUpdateConfig>))]
		[HttpPut("KsanLifecycle/{Version}")]
		public async Task<ActionResult> SetConfigLastVersionForKsanLifecycle([FromRoute] int Version)
		{
			return Json(await m_dataProvider.SetConfigLastVersion(EnumServiceType.ksanLifecycle, Version));
		}

		/// <summary>KsanLifecycle 설정의 버전을 삭제한다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData))]
		[HttpDelete("KsanLifecycle/{Version}")]
		public async Task<ActionResult> RemoveConfigForKsanLifecycle([FromRoute] int Version)
		{
			return Json(await m_dataProvider.RemoveConfig(EnumServiceType.ksanLifecycle, Version));
		}
		#endregion
		#region KsanReplication
		/// <summary>KsanReplication 설정 목록을 가져온다.</summary>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseList<ResponseServiceConfig>))]
		[HttpGet("List/KsanReplication")]
		public async Task<ActionResult> GetConfigListForKsanReplication()
		{
			return Json(await m_dataProvider.GetConfigList(EnumServiceType.ksanReplication));
		}

		/// <summary>KsanReplication 설정을 가져온다.</summary>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseServiceConfig>))]
		[HttpGet("KsanReplication")]
		public async Task<ActionResult> GetConfigForKsanReplication()
		{
			return Json(await m_dataProvider.GetConfig(EnumServiceType.ksanReplication));
		}

		/// <summary>특정 버전의 KsanReplication 설정을 가져온다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseServiceConfig>))]
		[HttpGet("KsanReplication/{Version}")]
		public async Task<ActionResult> GetConfigForKsanReplication([FromRoute] int Version)
		{
			return Json(await m_dataProvider.GetConfig(EnumServiceType.ksanReplication, Version));
		}

		/// <summary>KsanReplication 설정을 저장한다.</summary>
		/// <param name="Config">서비스 설정 정보</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseUpdateConfig>))]
		[HttpPost("KsanReplication")]
		public async Task<ActionResult> SetConfigForKsanReplication([FromBody] string Config)
		{
			return Json(await m_dataProvider.SetConfig(new RequestServiceConfig() { Type = EnumServiceType.ksanReplication, Config = Config }));
		}

		/// <summary>KsanReplication 설정의 버전을 변경한다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseUpdateConfig>))]
		[HttpPut("KsanReplication/{Version}")]
		public async Task<ActionResult> SetConfigLastVersionForKsanReplication([FromRoute] int Version)
		{
			return Json(await m_dataProvider.SetConfigLastVersion(EnumServiceType.ksanReplication, Version));
		}

		/// <summary>KsanReplication 설정의 버전을 삭제한다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData))]
		[HttpDelete("KsanReplication/{Version}")]
		public async Task<ActionResult> RemoveConfigForKsanReplication([FromRoute] int Version)
		{
			return Json(await m_dataProvider.RemoveConfig(EnumServiceType.ksanReplication, Version));
		}
		#endregion
		#region KsanLogManager
		/// <summary>KsanLogManager 설정 목록을 가져온다.</summary>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseList<ResponseServiceConfig>))]
		[HttpGet("List/KsanLogManager")]
		public async Task<ActionResult> GetConfigListForKsanLogManager()
		{
			return Json(await m_dataProvider.GetConfigList(EnumServiceType.ksanLogManager));
		}

		/// <summary>KsanLogManager 설정을 가져온다.</summary>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseServiceConfig>))]
		[HttpGet("KsanLogManager")]
		public async Task<ActionResult> GetConfigForKsanLogManager()
		{
			return Json(await m_dataProvider.GetConfig(EnumServiceType.ksanLogManager));
		}

		/// <summary>특정 버전의 KsanLogManager 설정을 가져온다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseServiceConfig>))]
		[HttpGet("KsanLogManager/{Version}")]
		public async Task<ActionResult> GetConfigForKsanLogManager([FromRoute] int Version)
		{
			return Json(await m_dataProvider.GetConfig(EnumServiceType.ksanLogManager, Version));
		}

		/// <summary>KsanLogManager 설정을 저장한다.</summary>
		/// <param name="Config">서비스 설정 정보</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseUpdateConfig>))]
		[HttpPost("KsanLogManager")]
		public async Task<ActionResult> SetConfigForKsanLogManager([FromBody] string Config)
		{
			return Json(await m_dataProvider.SetConfig(new RequestServiceConfig() { Type = EnumServiceType.ksanLogManager, Config = Config }));
		}

		/// <summary>KsanLogManager 설정의 버전을 변경한다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseUpdateConfig>))]
		[HttpPut("KsanLogManager/{Version}")]
		public async Task<ActionResult> SetConfigLastVersionForKsanLogManager([FromRoute] int Version)
		{
			return Json(await m_dataProvider.SetConfigLastVersion(EnumServiceType.ksanLogManager, Version));
		}

		/// <summary>KsanLogManager 설정의 버전을 삭제한다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData))]
		[HttpDelete("KsanLogManager/{Version}")]
		public async Task<ActionResult> RemoveConfigForKsanLogManager([FromRoute] int Version)
		{
			return Json(await m_dataProvider.RemoveConfig(EnumServiceType.ksanLogManager, Version));
		}
		#endregion
		#region KsanMetering
		/// <summary>KsanMetering 설정 목록을 가져온다.</summary>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseList<ResponseServiceConfig>))]
		[HttpGet("List/KsanMetering")]
		public async Task<ActionResult> GetConfigListForKsanMetering()
		{
			return Json(await m_dataProvider.GetConfigList(EnumServiceType.ksanMetering));
		}

		/// <summary>KsanMetering 설정을 가져온다.</summary>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseServiceConfig>))]
		[HttpGet("KsanMetering")]
		public async Task<ActionResult> GetConfigForKsanMetering()
		{
			return Json(await m_dataProvider.GetConfig(EnumServiceType.ksanMetering));
		}

		/// <summary>특정 버전의 KsanMetering 설정을 가져온다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseServiceConfig>))]
		[HttpGet("KsanMetering/{Version}")]
		public async Task<ActionResult> GetConfigForKsanMetering([FromRoute] int Version)
		{
			return Json(await m_dataProvider.GetConfig(EnumServiceType.ksanMetering, Version));
		}

		/// <summary>KsanMetering 설정을 저장한다.</summary>
		/// <param name="Config">서비스 설정 정보</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseUpdateConfig>))]
		[HttpPost("KsanMetering")]
		public async Task<ActionResult> SetConfigForKsanMetering([FromBody] string Config)
		{
			return Json(await m_dataProvider.SetConfig(new RequestServiceConfig() { Type = EnumServiceType.ksanMetering, Config = Config }));
		}

		/// <summary>KsanMetering 설정의 버전을 변경한다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData<ResponseUpdateConfig>))]
		[HttpPut("KsanMetering/{Version}")]
		public async Task<ActionResult> SetConfigLastVersionForKsanMetering([FromRoute] int Version)
		{
			return Json(await m_dataProvider.SetConfigLastVersion(EnumServiceType.ksanMetering, Version));
		}

		/// <summary>KsanMetering 설정의 버전을 삭제한다.</summary>
		/// <param name="Version">서비스 버전</param>
		/// <returns>결과 JSON 문자열</returns>
		[SwaggerResponse((int)HttpStatusCode.OK, null, typeof(ResponseData))]
		[HttpDelete("KsanMetering/{Version}")]
		public async Task<ActionResult> RemoveConfigForKsanMetering([FromRoute] int Version)
		{
			return Json(await m_dataProvider.RemoveConfig(EnumServiceType.ksanMetering, Version));
		}
		#endregion
	}
}