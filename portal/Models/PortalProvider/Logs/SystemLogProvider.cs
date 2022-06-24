﻿/*
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
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using PortalData;
using PortalData.Enums;
using PortalData.Responses.Logs;
using PortalModels;
using PortalProviderInterface;
using PortalResources;
using Microsoft.EntityFrameworkCore;
using MTLib.CommonData;
using MTLib.Core;
using MTLib.EntityFramework;

namespace PortalProvider.Logs
{
	/// <summary>시스템 로그 프로바이더 클래스</summary>
	public class SystemLogProvider : ISystemLogProvider
	{
		/// <summary>디비 컨텍스트</summary>
		protected readonly PortalModel m_dbContext;

		/// <summary>생성자</summary>
		/// <param name="dbContext">DB 컨텍스트</param>
		public SystemLogProvider(PortalModel dbContext)
		{
			m_dbContext = dbContext;
		}

		/// <summary>로그를 등록한다.</summary>
		/// <param name="Level">로그레벨</param>
		/// <param name="MessageFormat">로그 내용 형식</param>
		/// <param name="MessageValues">로그 내용 값</param>
		/// <returns>등록결과</returns>
		public async Task<ResponseData> Add(EnumLogLevel Level, string MessageFormat, params object[] MessageValues)
		{
			var Result = new ResponseData();
			try
			{
				// 메세지가 유효한 경우
				if (!MessageFormat.IsEmpty())
				{
					// 로그 저장
					await m_dbContext.SystemLogs.AddAsync(new SystemLog() { LogLevel = (EnumDbLogLevel)Level, Message = string.Format(MessageFormat, MessageValues), RegDate = DateTime.Now });
					await m_dbContext.SaveChangesWithConcurrencyResolutionAsync();
				}
			}
			catch (Exception ex)
			{
				NNException.Log(ex);

				Result.Code = Resource.EC_COMMON__EXCEPTION;
				Result.Message = Resource.EM_COMMON__EXCEPTION;
			}
			return Result;
		}

		/// <summary>로그 목록을 반환한다.</summary>
		/// <param name="SearchStartDate">검색 시작 일시</param>
		/// <param name="SearchEndDate">검색 종료 일시</param>
		/// <param name="Levels">로그 레벨 목록</param>
		/// <param name="Skip">건너뛸 레코드 수 (옵션, 기본 0)</param>
		/// <param name="CountPerPage">페이지 당 레코드 수</param>
		/// <param name="SearchFields">검색필드목록 (Email, LoginId, Name, Code, Message)</param>
		/// <param name="SearchKeyword">검색어</param>
		/// <returns>로그 목록</returns>
		public async Task<ResponseList<ResponseSystemLog>> GetLogs(
			DateTime SearchStartDate, DateTime SearchEndDate,
			List<EnumLogLevel> Levels = null,
			int Skip = 0, int CountPerPage = 100,
			List<string> SearchFields = null, string SearchKeyword = ""
		)
		{
			var Result = new ResponseList<ResponseSystemLog>();
			try
			{
				if (SearchStartDate > DateTime.MinValue && SearchEndDate > DateTime.MinValue)
				{
					// 검색 필드 목록을 모두 소문자로 변환
					if (SearchFields != null)
						SearchFields = SearchFields.ToLower();

					// 로그 목록을 가져온다.
					Result.Data = await m_dbContext.SystemLogs.AsNoTracking()
						.Where(i => SearchStartDate <= i.RegDate && i.RegDate <= SearchEndDate
							 && (
								 SearchFields == null || SearchFields.Count == 0 || SearchKeyword.IsEmpty()
								 || (SearchFields.Contains("Message") && i.Message.Contains(SearchKeyword))
							 )
							 && (Levels == null || Levels.Count == 0 || Levels.Select(j => (int)j).Contains((int)i.LogLevel))
						)
						.OrderByDescending(i => i.RegDate)
						.CreateListAsync<SystemLog, ResponseSystemLog>(Skip, CountPerPage);
				}
				Result.Result = EnumResponseResult.Success;
			}
			catch (Exception ex)
			{
				NNException.Log(ex);

				Result.Code = Resource.EC_COMMON__EXCEPTION;
				Result.Message = Resource.EM_COMMON__EXCEPTION;
			}
			return Result;
		}
	}
}
