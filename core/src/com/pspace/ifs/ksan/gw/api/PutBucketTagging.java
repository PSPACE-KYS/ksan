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

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.pspace.ifs.ksan.gw.data.DataPutBucketTagging;
import com.pspace.ifs.ksan.gw.exception.GWErrorCode;
import com.pspace.ifs.ksan.gw.exception.GWException;
import com.pspace.ifs.ksan.gw.format.Tagging;
import com.pspace.ifs.ksan.gw.format.Tagging.TagSet.Tag;
import com.pspace.ifs.ksan.gw.identity.S3Bucket;
import com.pspace.ifs.ksan.gw.identity.S3Parameter;
import com.pspace.ifs.ksan.libs.PrintStack;
import com.pspace.ifs.ksan.gw.utils.GWConstants;
import com.pspace.ifs.ksan.gw.utils.GWUtils;

import org.slf4j.LoggerFactory;

public class PutBucketTagging extends S3Request {
    public PutBucketTagging(S3Parameter s3Parameter) {
		super(s3Parameter);
		logger = LoggerFactory.getLogger(PutBucketTagging.class);
	}

	@Override
	public void process() throws GWException {
        logger.info(GWConstants.LOG_PUT_BUCKET_TAGGING_START);
		String bucket = s3Parameter.getBucketName();
		initBucketInfo(bucket);

		GWUtils.checkCors(s3Parameter);

		if (s3Parameter.isPublicAccess() && GWUtils.isIgnorePublicAcls(s3Parameter)) {
			throw new GWException(GWErrorCode.ACCESS_DENIED, s3Parameter);
		}

		DataPutBucketTagging dataPutBucketTagging = new DataPutBucketTagging(s3Parameter);
		dataPutBucketTagging.extract();

		if (!checkPolicyBucket(GWConstants.ACTION_PUT_BUCKET_TAGGING, s3Parameter, dataPutBucketTagging)) {
			checkGrantBucketOwner(s3Parameter.isPublicAccess(), s3Parameter.getUser().getUserId(), GWConstants.GRANT_WRITE_ACP);
		}
		
		String taggingXml = dataPutBucketTagging.getTaggingXml();
        logger.debug(GWConstants.LOG_PUT_BUCKET_TAGGING, taggingXml);
		try {
			Tagging tagging = new XmlMapper().readValue(taggingXml, Tagging.class);

			// 중복 지우기 item이 10개 미만이기 때문에 for loop가 빠름
			if (tagging != null) {

				if (tagging.tagset != null && tagging.tagset.tags != null) {
					for (Tag t : tagging.tagset.tags) {

						// key, value 길이 체크
						if (t.key.length() > 128) {
							throw new GWException(GWErrorCode.INVALID_TAG, s3Parameter);
						}

						if (t.value.length() > 256) {
							throw new GWException(GWErrorCode.INVALID_TAG, s3Parameter);
						}
					}
				}

				if ( tagging.tagset != null && tagging.tagset.tags != null ) {
					if(tagging.tagset.tags.size() > 10) {
						throw new GWException(GWErrorCode.BAD_REQUEST, s3Parameter);	
					}
				}
			}
		} catch (IOException e) {
			PrintStack.logging(logger, e);
			throw new GWException(GWErrorCode.SERVER_ERROR, s3Parameter);
		}

		updateBucketTagging(bucket, taggingXml);
        
		s3Parameter.getResponse().setStatus(HttpServletResponse.SC_OK);
	}
}
