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
package com.pspace.ifs.ksan.gw.api;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;
import com.pspace.ifs.ksan.gw.data.DataGetBucketVersioning;
import com.pspace.ifs.ksan.gw.exception.GWErrorCode;
import com.pspace.ifs.ksan.gw.exception.GWException;
import com.pspace.ifs.ksan.gw.identity.S3Bucket;
import com.pspace.ifs.ksan.gw.identity.S3Parameter;
import com.pspace.ifs.ksan.libs.PrintStack;
import com.pspace.ifs.ksan.gw.utils.GWConstants;
import com.pspace.ifs.ksan.gw.utils.GWUtils;

import org.slf4j.LoggerFactory;

public class GetBucketVersioning extends S3Request {

	public GetBucketVersioning(S3Parameter s3Parameter) {
		super(s3Parameter);
		logger = LoggerFactory.getLogger(GetBucketVersioning.class);
	}

	@Override
	public void process() throws GWException {		
		logger.info(GWConstants.LOG_GET_BUCKET_VERSIONING_START);
		String bucket = s3Parameter.getBucketName();
		initBucketInfo(bucket);

		GWUtils.checkCors(s3Parameter);
        
        if (s3Parameter.isPublicAccess() && GWUtils.isIgnorePublicAcls(s3Parameter)) {
			throw new GWException(GWErrorCode.ACCESS_DENIED, s3Parameter);
		}

		DataGetBucketVersioning dataGetBucketVersioning = new DataGetBucketVersioning(s3Parameter);
		dataGetBucketVersioning.extract();

		if (!checkPolicyBucket(GWConstants.ACTION_GET_BUCKET_VERSIONING, s3Parameter, dataGetBucketVersioning)) {
			checkGrantBucketOwner(s3Parameter.isPublicAccess(), s3Parameter.getUser().getUserId(), GWConstants.GRANT_READ_ACP);
		}
		
		String versioningStatus = getBucketInfo().getVersioning();
		logger.debug(GWConstants.LOG_GET_BUCKET_VERSIONING, bucket, versioningStatus);

		String xml = null;

        if (Strings.isNullOrEmpty(versioningStatus)) {
            xml = GWConstants.VERSION_CONFIGURATION_XMLNS_DISABLE;
        } else {
			if (GWConstants.VERSIONING_ENABLED.equals(versioningStatus)) {
				xml = GWConstants.VERSION_CONFIGURATION_XMLNS_ENABLED;
			} else if (GWConstants.VERSIONING_SUSPENDED.equals(versioningStatus)) {
				xml = GWConstants.VERSION_CONFIGURATION_XMLNS_SUSPENDED;
			} else {
				logger.error(GWConstants.LOG_GET_BUCKET_VERSIONING_WRONG, versioningStatus);
				throw new GWException(GWErrorCode.SERVER_ERROR, s3Parameter);
			}
        }

		logger.debug(GWConstants.LOG_GET_BUCKET_VERSIONING_XML, xml);
		try {
			s3Parameter.getResponse().setContentType(GWConstants.XML_CONTENT_TYPE);
			s3Parameter.getResponse().getOutputStream().write(xml.getBytes());
		} catch (IOException e) {
			PrintStack.logging(logger, e);
			throw new GWException(GWErrorCode.SERVER_ERROR, s3Parameter);
		}

		s3Parameter.getResponse().setStatus(HttpServletResponse.SC_OK);
	}

}
