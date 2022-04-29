/*
* Copyright (c) 2021 PSPACE, inc. KSAN Development Team ksan@pspace.co.kr
* KSAN is a suite of free software: you can redistribute it and/or modify it under the terms of
* the GNU General Public License as published by the Free Software Foundation, either version
* 3 of the License.  See LICENSE for details
*
* 본 프로그램 및 관련 소스코드, 문서 등 모든 자료는 있는 그대로 제공이 됩니다.
* KSAN 프로젝트의 개발자 및 개발사는 이 프로그램을 사용한 결과에 따른 어떠한 책임도 지지 않습니다.
* KSAN 개발팀은 사전 공지, 허락, 동의 없이 KSAN 개발에 관련된 모든 결과물에 대한 LICENSE 방식을 변경 할 권리가 있습니다.
*/
using System;
using System.Threading.Tasks;
using PortalData;
using PortalData.Requests.Disks;
using PortalData.Requests.Networks;
using PortalData.Requests.Servers;
using PortalData.Responses.Accounts;
using PortalProvider.Providers.RabbitMq;
using PortalProviderInterface;
using PortalResources;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using MTLib.CommonData;
using MTLib.Core;
using MTLib.Reflection;
using Newtonsoft.Json;
// ReSharper disable TemplateIsNotCompileTimeConstantProblem

namespace PortalSvr.RabbitMqReceivers
{
	/// <summary>Rabbit MQ로 부터 서버 정보를 수신하는 클래스</summary>
	public class RabbitMqServerReceiver : RabbitMqReceiver
	{
		/// <summary>생성자</summary>
		/// <param name="rabbitMqOptions">Rabbit MQ 설정 옵션 객체</param>
		/// <param name="logger">로거</param>
		/// <param name="serviceScopeFactory">서비스 팩토리</param>
		public RabbitMqServerReceiver(
			IOptions<RabbitMqConfiguration> rabbitMqOptions,
			ILogger<RabbitMqServerReceiver> logger,
			IServiceScopeFactory serviceScopeFactory
		) : base(
			"portalsvr.servers",
			RabbitMqConfiguration.ExchangeName,
			new[]
			{
				"*.servers.*",
				"*.servers.*.*",
				"*.servers.*.*.*",
				"portalsvr.servers.*",
				"portalsvr.servers.*.*",
				"portalsvr.servers.*.*.*"
			},
			rabbitMqOptions,
			logger,
			serviceScopeFactory)
		{
			try
			{
			}
			catch (Exception ex)
			{
				NNException.Log(ex);
				throw;
			}
		}

		/// <summary>메시지 처리</summary>
		/// <param name="routingKey">라우팅 키</param>
		/// <param name="body">내용</param>
		protected override async Task<ResponseMqData> HandleMessage(string routingKey, byte[] body)
		{
			ResponseMqData result = new ResponseMqData();

			try
			{
				// 수신된 데이터를 문자열로 변환
				string json = body.GetString();

				using (var scope = m_serviceScopeFactory.CreateScope())
				{
					// API 키 프로바이더를 가져온다.
					IApiKeyProvider apiKeyProvider = scope.ServiceProvider.GetService<IApiKeyProvider>();
					if (apiKeyProvider == null)
						return new ResponseMqData(EnumResponseResult.Error, Resource.EC_COMMON__CANNOT_CREATE_INSTANCE, Resource.EM_COMMON__CANNOT_CREATE_INSTANCE);

					// 내부 시스템 API 키 정보를 가져온다.
					ResponseData<ResponseApiKey> responseApiKey = await apiKeyProvider.GetApiKey(PredefinedApiKey.InternalSystemApiKey);

					// API 키를 가져오는데 실패한 경우
					if (responseApiKey.Result == EnumResponseResult.Error)
						return new ResponseMqData(EnumResponseResult.Error, responseApiKey.Code, responseApiKey.Message);

					// 서버 상태 관련인 경우
					if (routingKey.EndsWith("servers.state"))
					{
						// 처리할 프로바이더를 가져온다.
						IServerProvider dataProvider = scope.ServiceProvider.GetService<IServerProvider>();
						if (dataProvider == null)
							return new ResponseMqData(EnumResponseResult.Error, Resource.EC_COMMON__CANNOT_CREATE_INSTANCE, Resource.EM_COMMON__CANNOT_CREATE_INSTANCE);

						// json을 객체로 변환
						RequestServerState request = JsonConvert.DeserializeObject<RequestServerState>(json);

						// 서버 상태 수정
						ResponseData response = await dataProvider.UpdateState(request, responseApiKey.Data.UserId, responseApiKey.Data.UserName);

						result.CopyValueFrom(response);
						result.IsProcessed = true;
					}
					// 서버 사용 정보 관련인 경우
					else if (routingKey.EndsWith("servers.usage"))
					{
						// 처리할 프로바이더를 가져온다.
						IServerProvider dataProvider = scope.ServiceProvider.GetService<IServerProvider>();
						if (dataProvider == null)
							return new ResponseMqData(EnumResponseResult.Error, Resource.EC_COMMON__CANNOT_CREATE_INSTANCE, Resource.EM_COMMON__CANNOT_CREATE_INSTANCE);

						// json을 객체로 변환
						RequestServerUsage request = JsonConvert.DeserializeObject<RequestServerUsage>(json);

						// 서버 사용 정보 수정
						ResponseData response = await dataProvider.UpdateUsage(request);

						result.CopyValueFrom(response);
						result.IsProcessed = true;
					}
					// 네트워크 인터페이스 연결 상태 관련인 경우
					else if (routingKey.EndsWith("servers.interfaces.linkstate"))
					{
						// 처리할 프로바이더를 가져온다.
						INetworkInterfaceProvider dataProvider = scope.ServiceProvider.GetService<INetworkInterfaceProvider>();
						if (dataProvider == null)
							return new ResponseMqData(EnumResponseResult.Error, Resource.EC_COMMON__CANNOT_CREATE_INSTANCE, Resource.EM_COMMON__CANNOT_CREATE_INSTANCE);

						// json을 객체로 변환
						RequestNetworkInterfaceLinkState request = JsonConvert.DeserializeObject<RequestNetworkInterfaceLinkState>(json);

						// 네트워크 인터페이스 연결 상태 수정
						ResponseData response = await dataProvider.UpdateLinkState(request);

						result.CopyValueFrom(response);
						result.IsProcessed = true;
					}
					// 네트워크 인터페이스 사용 정보 관련인 경우
					else if (routingKey.EndsWith("servers.interfaces.usage"))
					{
						// 처리할 프로바이더를 가져온다.
						INetworkInterfaceProvider dataProvider = scope.ServiceProvider.GetService<INetworkInterfaceProvider>();
						if (dataProvider == null)
							return new ResponseMqData(EnumResponseResult.Error, Resource.EC_COMMON__CANNOT_CREATE_INSTANCE, Resource.EM_COMMON__CANNOT_CREATE_INSTANCE);

						// json을 객체로 변환
						RequestNetworkInterfaceUsage request = JsonConvert.DeserializeObject<RequestNetworkInterfaceUsage>(json);

						// 네트워크 인터페이스 사용 정보 수정
						ResponseData response = await dataProvider.UpdateUsage(request);

						result.CopyValueFrom(response);
						result.IsProcessed = true;
					}
					// 디스크 크기 관련인 경우
					else if (routingKey.EndsWith("servers.disks.size"))
					{
						// 처리할 프로바이더를 가져온다.
						IDiskProvider dataProvider = scope.ServiceProvider.GetService<IDiskProvider>();
						if (dataProvider == null)
							return new ResponseMqData(EnumResponseResult.Error, Resource.EC_COMMON__CANNOT_CREATE_INSTANCE, Resource.EM_COMMON__CANNOT_CREATE_INSTANCE);

						// json을 객체로 변환
						RequestDiskSize request = JsonConvert.DeserializeObject<RequestDiskSize>(json);

						// 디스크 크기 수정
						ResponseData response = await dataProvider.UpdateSize(request);

						result.CopyValueFrom(response);
						result.IsProcessed = true;
					}
				}
			}
			catch (Exception ex)
			{
				NNException.Log(ex);

				result.Code = Resource.EC_COMMON__EXCEPTION;
				result.Message = Resource.EM_COMMON__EXCEPTION;
			}

			return result;
		}
	}
}