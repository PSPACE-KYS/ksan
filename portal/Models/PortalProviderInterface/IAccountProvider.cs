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
using System.Security.Claims;
using System.Threading.Tasks;
using PortalData;
using PortalData.Requests.Accounts;
using PortalData.Responses.Accounts;
using Microsoft.AspNetCore.Http;

namespace PortalProviderInterface
{
	/// <summary>계정 관련 프로바이더 인터페이스</summary>
	public interface IAccountProvider : IBaseProvider
	{
		/// <summary>사용자를 생성한다.</summary>
		/// <param name="request">생성할 사용자 정보</param>
		/// <param name="httpRequest">HttpRequest 객체</param>
		/// <param name="setConfirmEmail">이메일 확인 상태로 설정할지 여부</param>
		/// <returns>생성 결과</returns>
		Task<ResponseData<ResponseLogin>> Create(RequestRegister request, HttpRequest httpRequest, bool setConfirmEmail = false);

		/// <summary>로그인 처리</summary>
		/// <param name="apiKey">API 키 문자열</param>
		/// <param name="httpRequest">HttpRequest 객체</param>
		/// <returns>로그인 결과</returns>
		Task<ResponseData<ResponseLogin>> LoginWithApiKey(string apiKey, HttpRequest httpRequest);

		/// <summary>로그인 처리</summary>
		/// <param name="request">로그인 요청 객체</param>
		/// <param name="httpRequest">HttpRequest 객체</param>
		/// <returns>로그인 결과</returns>
		Task<ResponseData<ResponseLogin>> Login(RequestLogin request, HttpRequest httpRequest);

		/// <summary>로그아웃 처리</summary>
		/// <returns>로그아웃 결과</returns>
		Task<ResponseData> Logout();

		/// <summary>로그인 여부를 가져온다.</summary>
		/// <param name="user">로그인 사용자 정보 객체</param>
		/// <param name="requireRoles">필요한 역할명 목록 (',' 으로 구분)</param>
		/// <returns>로그인 여부 정보</returns>
		ResponseData CheckLogin(ClaimsPrincipal user, string requireRoles = "");

		/// <summary>로그인 정보를 가져온다. </summary>
		/// <param name="loginUser">로그인 사용자 정보 객체</param>
		/// <returns>로그인 정보</returns>
		Task<ResponseData<ResponseLogin>> GetLogin(ClaimsPrincipal loginUser);

		/// <summary>이메일 주소 인증 처리</summary>
		/// <param name="request">이메일 인증 요청 객체</param>
		/// <returns>인증 처리 결과</returns>
		Task<ResponseData> ConfirmEmail(RequestConfirmEmail request);

		/// <summary>현재 로그인한 사용자의 비밀번호를 변경한다.</summary>
		/// <param name="loginUser">로그인 사용자 정보 객체</param>
		/// <param name="request">비밀번호 요청 객체</param>
		/// <returns>비밀번호 변경 결과</returns>
		Task<ResponseData> ChangePassword(ClaimsPrincipal loginUser, RequestChangePassword request);

		/// <summary>비밀번호 찾기 요청</summary>
		/// <param name="request">비밀번호 찾기 요청 객체</param>
		/// <param name="httpRequest">HttpRequest 객체</param>
		/// <returns>비밀번호 찾기 요청 처리 결과</returns>
		Task<ResponseData> ForgotPassword(RequestForgetPassword request, HttpRequest httpRequest);

		/// <summary>비밀번호 재설정</summary>
		/// <param name="request">비밀번호 재설정 요청 객체</param>
		/// <returns>비밀번호 재설정 결과</returns>
		Task<ResponseData> ResetPassword(RequestResetPassword request);

		/// <summary>현재 로그인한 사용자 정보를 수정한다.</summary>
		/// <param name="loginUser">로그인 사용자 정보 객체</param>
		/// <param name="request">회원 정보 수정 요청 객체</param>
		/// <returns>사용자 정보 수정 결과</returns>
		Task<ResponseData> Update(ClaimsPrincipal loginUser, RequestUpdate request);

		/// <summary>특정 사용자에게 역할을 추가한다.</summary>
		/// <param name="id">회원아이디</param>
		/// <param name="roleName">역할명</param>
		/// <returns>역할 추가 결과</returns>
		Task<ResponseData> AddToRole(string id, string roleName);

		/// <summary>특정 사용자에서 역할을 삭제한다.</summary>
		/// <param name="id">회원아이디</param>
		/// <param name="roleName">역할명</param>
		/// <returns>역할 삭제 결과</returns>
		Task<ResponseData> RemoveFromRole(string id, string roleName);

		/// <summary>로그인한 사용자에 대한 권한 목록을 가져온다.</summary>
		/// <param name="loginUser">로그인 사용자 정보 객체</param>
		/// <returns>로그인한 사용자에 대한 사용자 목록</returns>
		Task<ResponseList<ResponseClaim>> GetClaims(ClaimsPrincipal loginUser);

		/// <summary>로그인한 사용자의 권한 중 해당 권한이 존재하는지 확인한다.</summary>
		/// <param name="loginUser">로그인 사용자 정보 객체</param>
		/// <param name="claimValue">검사할 권한 값</param>
		/// <returns>로그인한 사용자의 권한 중 해당 권한이 존재하는지 여부</returns>
		Task<ResponseData> HasClaim(ClaimsPrincipal loginUser, string claimValue);
	}
}
