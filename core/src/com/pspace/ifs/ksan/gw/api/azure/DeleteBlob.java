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

package com.pspace.ifs.ksan.gw.api.azure;

import com.pspace.ifs.ksan.gw.exception.AzuException;
import com.pspace.ifs.ksan.gw.identity.AzuParameter;
import com.pspace.ifs.ksan.gw.utils.AzuConstants;
import com.pspace.ifs.ksan.gw.exception.AzuErrorCode;
import com.pspace.ifs.ksan.gw.utils.GWConstants;
import com.pspace.ifs.ksan.gw.utils.AzuUtils;
import com.pspace.ifs.ksan.libs.PrintStack;
import com.pspace.ifs.ksan.libs.identity.S3Metadata;
import com.pspace.ifs.ksan.objmanager.Metadata;
import com.pspace.ifs.ksan.gw.object.S3Object;
import com.pspace.ifs.ksan.gw.object.AzuObjectOperation;

import java.io.File;

import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletResponse;

public class DeleteBlob extends AzuRequest {

    public DeleteBlob(AzuParameter parameter) {
        super(parameter);
        logger = LoggerFactory.getLogger(DeleteBlob.class);
    }

    @Override
    public void process() throws AzuException {
        logger.info(AzuConstants.LOG_DELETE_BLOB_START);
        
        String containerName = azuParameter.getContainerName();
        String blobName = azuParameter.getBlobName();
        String versionId = GWConstants.VERSIONING_DISABLE_TAIL;
        Metadata objMeta = null;
		try {
			objMeta = open(containerName, blobName);
            remove(azuParameter.getContainerName(), azuParameter.getBlobName());
            AzuObjectOperation azuObjectOperation = new AzuObjectOperation(objMeta, null, azuParameter, versionId);
			azuObjectOperation.deleteObject();
		} catch (AzuException e) {
            PrintStack.logging(logger, e);
			throw new AzuException(AzuErrorCode.NO_SUCH_KEY, azuParameter);
		}

        azuParameter.getResponse().setStatus(HttpServletResponse.SC_ACCEPTED);
    }
}

