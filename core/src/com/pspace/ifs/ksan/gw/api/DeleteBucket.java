
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

import jakarta.servlet.http.HttpServletResponse;

import com.pspace.ifs.ksan.gw.data.DataDeleteBucket;
import com.pspace.ifs.ksan.gw.exception.GWErrorCode;
import com.pspace.ifs.ksan.gw.exception.GWException;
import com.pspace.ifs.ksan.gw.identity.S3Bucket;
import com.pspace.ifs.ksan.gw.identity.S3Parameter;
import com.pspace.ifs.ksan.gw.utils.GWConstants;
import com.pspace.ifs.ksan.gw.utils.GWUtils;

import org.slf4j.LoggerFactory;
import com.google.common.base.Strings;

public class DeleteBucket extends S3Request{

    public DeleteBucket(S3Parameter s3Parameter) {
        super(s3Parameter);
        logger = LoggerFactory.getLogger(DeleteBucket.class);
    }

    @Override
    public void process() throws GWException {
        logger.info(GWConstants.LOG_DELETE_BUCKET_START);
        String bucket = s3Parameter.getBucketName();
        initBucketInfo(bucket);

		GWUtils.checkCors(s3Parameter);

        DataDeleteBucket dataDeleteBucket = new DataDeleteBucket(s3Parameter);
        dataDeleteBucket.extract();

        String expectedBucketOwner = dataDeleteBucket.getExpectedBucketOwner();
        if (!Strings.isNullOrEmpty(expectedBucketOwner)) {
            if (!isBucketOwner(expectedBucketOwner)) {
                throw new GWException(GWErrorCode.ACCESS_DENIED, s3Parameter);
            }
        }

		if (s3Parameter.isPublicAccess() && GWUtils.isIgnorePublicAcls(s3Parameter)) {
			throw new GWException(GWErrorCode.ACCESS_DENIED, s3Parameter);
		}

        deleteBucket(bucket);

        s3Parameter.getResponse().setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
