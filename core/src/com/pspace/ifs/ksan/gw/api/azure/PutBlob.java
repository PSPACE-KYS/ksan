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

import static com.google.common.io.BaseEncoding.base64;

import com.google.common.base.Strings;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import com.pspace.ifs.ksan.gw.data.azure.DataPutBlob;
import com.pspace.ifs.ksan.gw.exception.AzuErrorCode;
import com.pspace.ifs.ksan.gw.exception.AzuException;
import com.pspace.ifs.ksan.gw.identity.AzuParameter;
import com.pspace.ifs.ksan.gw.utils.AzuConfig;
import com.pspace.ifs.ksan.gw.utils.AzuConstants;
import com.pspace.ifs.ksan.gw.utils.GWConstants;
import com.pspace.ifs.ksan.gw.utils.AzuUtils;
import com.pspace.ifs.ksan.libs.PrintStack;
import com.pspace.ifs.ksan.libs.identity.S3Metadata;
import com.pspace.ifs.ksan.objmanager.Metadata;
import com.pspace.ifs.ksan.gw.object.S3Object;
import com.pspace.ifs.ksan.gw.object.AzuObjectOperation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.security.MessageDigest;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import jakarta.servlet.http.HttpServletResponse;

public class PutBlob extends AzuRequest {

    public PutBlob(AzuParameter parameter) {
        super(parameter);
        logger = LoggerFactory.getLogger(PutBlob.class);
    }

    @Override
    public void process() throws AzuException {
        logger.info(AzuConstants.LOG_CREATE_BLOB_START);
        
        String containerName = azuParameter.getContainerName();
        String blobName = azuParameter.getBlobName();
		String storageClass = GWConstants.AWS_TIER_STANTARD;
        String diskpoolId = "";
        try {
            diskpoolId = azuParameter.getUser().getUserDefaultDiskpoolId();
        } catch (Exception e) {
            PrintStack.logging(logger, e);
        }
        
        DataPutBlob dataCreateBlob = new DataPutBlob(azuParameter);
        dataCreateBlob.extract();

        String contentsLength = dataCreateBlob.getContentLength();
        long blobLength = Long.parseLong(contentsLength);
        String contentMD5 = dataCreateBlob.getContentMD5();

        logger.debug("contentsLength : {}, {}", contentsLength, blobLength);

        String versionId = GWConstants.VERSIONING_DISABLE_TAIL;
        Metadata objMeta = null;
        boolean isExist = false;
        try {
            // check exist object
            objMeta = open(containerName, blobName);
            isExist = true;
        } catch (AzuException e) {
            logger.info(e.getMessage());
            // reset error code
            azuParameter.setErrorCode(GWConstants.EMPTY_STRING);
            objMeta = createLocal(diskpoolId, containerName, blobName, versionId);
        }

        S3Metadata s3Metadata = new S3Metadata();
        ObjectMapper jsonMapper = new ObjectMapper();
        if (isExist) {
            try {
                logger.debug(GWConstants.LOG_META, objMeta.getMeta());
                s3Metadata = jsonMapper.readValue(objMeta.getMeta(), S3Metadata.class);
            } catch (JsonProcessingException e) {
                PrintStack.logging(logger, e);
                throw new AzuException(AzuErrorCode.SERVER_ERROR, azuParameter);
            }
        } else {
            s3Metadata.setCreationDate(new Date());
        }

        s3Metadata.setContentType(dataCreateBlob.getContentType());
        s3Metadata.setOwnerId(azuParameter.getUser().getUserId());
        s3Metadata.setOwnerName(azuParameter.getUser().getUserName());
        s3Metadata.setContentLength(blobLength);

        AzuObjectOperation azuObjectOperation = new AzuObjectOperation(objMeta, s3Metadata, azuParameter, versionId);
        S3Object s3Object = azuObjectOperation.putObject();

        s3Metadata.setETag(s3Object.getEtag());
        s3Metadata.setContentLength(s3Object.getFileSize());
        s3Metadata.setTier(storageClass);
        s3Metadata.setLastModified(s3Object.getLastModified());
        s3Metadata.setDeleteMarker(s3Object.getDeleteMarker());
        s3Metadata.setVersionId(versionId);

        logger.info("MD5 check, receive : {}, result : {}", contentMD5, s3Object.getEtag());

        String jsonmeta = "";
        try {
            jsonmeta = jsonMapper.writeValueAsString(s3Metadata);
        } catch (JsonProcessingException e) {
            PrintStack.logging(logger, e);
            throw new AzuException(AzuErrorCode.SERVER_ERROR, azuParameter);
        }

        logger.debug(AzuConstants.LOG_CREATE_BLOB_PRIMARY_DISK_ID, objMeta.getPrimaryDisk().getId());
        try {
            objMeta.set(s3Object.getEtag(), AzuConstants.EMPTY_STRING, jsonmeta, AzuConstants.EMPTY_STRING, s3Object.getFileSize());
            objMeta.setVersionId(versionId, GWConstants.OBJECT_TYPE_FILE, true);
            int result = insertObject(containerName, blobName, objMeta);
        } catch (AzuException e) {
            PrintStack.logging(logger, e);
            throw new AzuException(AzuErrorCode.SERVER_ERROR, azuParameter);
        }
        
        azuParameter.getResponse().setStatus(HttpServletResponse.SC_CREATED);
    }
}

