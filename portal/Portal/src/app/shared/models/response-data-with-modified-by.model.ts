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
import {ResponseDataWithCreatedBy} from './response-data-with-created-by.model';

/**
 * 작성인/수정인 정보가 포함된 응답 데이터 클래스
 */
export class ResponseDataWithModifiedBy extends ResponseDataWithCreatedBy {

	/**
	 * 생성자
	 * @param ModDate 수정일시
	 * @param ModId 수정 아이디
	 * @param ModName 수정 이름
	 * @param RegDate 등록일시
	 * @param RegId 등록 아이디
	 * @param RegName 등록 이름
	 */
	constructor(ModDate?: Date, ModId?: string, ModName?: string, RegDate?: Date, RegId?: string, RegName?: string) {
		super(RegDate, RegId, RegName);
		this.ModDate = ModDate;
		this.ModId = ModId;
		this.ModName = ModName;
	}

	/**
	 * 수정일시
	 */
	public ModDate: Date;
	/**
	 * 수정 아이디
	 */
	public ModId: string;
	/**
	 * 수정 이름
	 */
	public ModName: string;
}


