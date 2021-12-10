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
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.pspace.ifs.ksan.gw.data.DataCopyObject;
import com.pspace.ifs.ksan.gw.exception.GWErrorCode;
import com.pspace.ifs.ksan.gw.exception.GWException;
import com.pspace.ifs.ksan.gw.format.AccessControlPolicy;
import com.pspace.ifs.ksan.gw.format.AccessControlPolicy.AccessControlList;
import com.pspace.ifs.ksan.gw.format.AccessControlPolicy.Owner;
import com.pspace.ifs.ksan.gw.format.AccessControlPolicy.AccessControlList.Grant;
import com.pspace.ifs.ksan.gw.identity.S3Bucket;
import com.pspace.ifs.ksan.gw.identity.S3Metadata;
import com.pspace.ifs.ksan.gw.identity.S3Parameter;
import com.pspace.ifs.ksan.gw.object.S3Object;
import com.pspace.ifs.ksan.gw.object.S3ObjectOperation;
import com.pspace.ifs.ksan.gw.utils.PrintStack;
import com.pspace.ifs.ksan.gw.utils.GWConstants;
import com.pspace.ifs.ksan.gw.utils.GWUtils;
import com.pspace.ifs.ksan.objmanager.Metadata;

import org.slf4j.LoggerFactory;

public class CopyObject extends S3Request {
    public CopyObject(S3Parameter s3Parameter) {
		super(s3Parameter);
		logger = LoggerFactory.getLogger(CopyObject.class);
	}

	@Override
	public void process() throws GWException {
		logger.info(GWConstants.LOG_COPY_OBJECT_START);

		String bucket = s3Parameter.getBucketName();
		initBucketInfo(bucket);

		String object = s3Parameter.getObjectName();
		
		S3Bucket s3Bucket = new S3Bucket();
		s3Bucket.setCors(getBucketInfo().getCors());
		s3Bucket.setAccess(getBucketInfo().getAccess());
		s3Parameter.setBucket(s3Bucket);
		GWUtils.checkCors(s3Parameter);

		if (s3Parameter.isPublicAccess() && GWUtils.isIgnorePublicAcls(s3Parameter)) {
			throw new GWException(GWErrorCode.ACCESS_DENIED, s3Parameter);
		}

		checkGrantBucket(s3Parameter.isPublicAccess(), String.valueOf(s3Parameter.getUser().getUserId()), GWConstants.GRANT_WRITE);

		try {
			object = URLDecoder.decode(object, GWConstants.CHARSET_UTF_8);
		} catch (UnsupportedEncodingException e) {
			PrintStack.logging(logger, e);
			throw new GWException(GWErrorCode.SERVER_ERROR, s3Parameter);
		}

		DataCopyObject dataCopyObject = new DataCopyObject(s3Parameter);
		dataCopyObject.extract();

		String cacheControl = dataCopyObject.getCacheControl();
		String contentDisposition = dataCopyObject.getContentDisposition();
		String contentEncoding = dataCopyObject.getContentEncoding();
		String contentLanguage = dataCopyObject.getContentLanguage();
		String contentType = dataCopyObject.getContentType();
		String contentLengthString = dataCopyObject.getContentLength();
		String metadataDirective = dataCopyObject.getMetadataDirective();
		String serversideEncryption = dataCopyObject.getServerSideEncryption();
		String copySource = dataCopyObject.getCopySource();
		String copySourceIfMatch = dataCopyObject.getCopySourceIfMatch();
		String copySourceIfNoneMatch = dataCopyObject.getCopySourceIfNoneMatch();
		String copySourceIfModifiedSince = dataCopyObject.getCopySourceIfModifiedSince();
		String copySourceIfUnmodifiedSince = dataCopyObject.getCopySourceIfUnmodifiedSince();
		String expires = dataCopyObject.getExpires();
		String customerAlgorithm = dataCopyObject.getServerSideEncryptionCustomerAlgorithm();
		String customerKey = dataCopyObject.getServerSideEncryptionCustomerKey();
		String customerKeyMD5 = dataCopyObject.getServerSideEncryptionCustomerKeyMD5();
		String context = dataCopyObject.getServerSideEncryptionContext();
		String bucketKeyEnabled = dataCopyObject.getServerSideEncryptionBucketKeyEnabled();
		String copySourceCustomerAlgorithm = dataCopyObject.getCopySourceServerSideEncryptionCustomerAlgorithm();
		String copySourceCustomerKey = dataCopyObject.getCopySourceServerSideEncryptionCustomerKey();
		String copySourceCustomerKeyMD5 = dataCopyObject.getCopySourceServerSideEncryptionCustomerKeyMD5();
		Map<String, String> userMetadata = dataCopyObject.getUserMetadata();

		// Check copy source
		if (Strings.isNullOrEmpty(copySource)) {
			logger.error(GWConstants.LOG_COPY_SOURCE_IS_NULL);
			throw new GWException(GWErrorCode.BAD_REQUEST, s3Parameter);
		}

		try {
			copySource = URLDecoder.decode(copySource, GWConstants.CHARSET_UTF_8);
		} catch (UnsupportedEncodingException e) {
			PrintStack.logging(logger, e);
			throw new GWException(GWErrorCode.SERVER_ERROR, s3Parameter);
		}

		if (copySource.startsWith(GWConstants.SLASH)) {
			copySource = copySource.substring(1);
		} else if (copySource.contains(GWConstants.S3_ARN)) {
			logger.error(GWConstants.LOG_COPY_SOURCE_IS_NOT_IMPLEMENTED, copySource);
			throw new GWException(GWErrorCode.NOT_IMPLEMENTED, s3Parameter);
		}

		String[] sourcePath = copySource.split(GWConstants.SLASH, 2);
		if (sourcePath.length != 2) {
			throw new GWException(GWErrorCode.INVALID_ARGUMENT, s3Parameter);
		}

		String srcBucket = sourcePath[0];
		String srcObjectName = sourcePath[1];
		String srcVersionId = null;

		setSrcBucket(srcBucket);

		if (srcObjectName.contains(GWConstants.SUB_PARAMETER_VERSIONID) == true) {
			String[] source = sourcePath[1].split(GWConstants.PARAMETER_BACKSLASH_VERSIONID, 2);
			srcObjectName = source[0];
			srcVersionId = source[1].replaceAll(GWConstants.DOUBLE_QUOTE, "");
		}
		
        s3Parameter.setSrcBucketName(srcBucket);
		s3Parameter.setSrcVersionId(srcVersionId);
		s3Parameter.setSrcPath(srcObjectName);
		logger.debug(GWConstants.LOG_SOURCE_INFO, srcBucket, srcObjectName, srcVersionId);

		String versioningStatus = getBucketVersioning(srcBucket);

		Metadata srcMeta = null;
		if (GWConstants.VERSIONING_ENABLED.equalsIgnoreCase(versioningStatus)) {
			if (!Strings.isNullOrEmpty(srcVersionId)) {
				srcMeta = open(srcBucket, srcObjectName, srcVersionId);
			} else {
				srcMeta = open(srcBucket, srcObjectName);
			}
		} else {
			srcMeta = open(srcBucket, srcObjectName);
		}
		srcVersionId = srcMeta.getVersionId();
		srcMeta.setAcl(GWUtils.makeOriginalXml(srcMeta.getAcl(), s3Parameter));
		
		checkGrantObject(s3Parameter.isPublicAccess(), srcMeta, String.valueOf(s3Parameter.getUser().getUserId()), GWConstants.GRANT_READ);

		// get metadata
		S3Metadata s3Metadata = null;
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			s3Metadata = objectMapper.readValue(srcMeta.getMeta(), S3Metadata.class);
		} catch (JsonProcessingException e) {
			PrintStack.logging(logger, e);
			throw new GWException(GWErrorCode.INTERNAL_SERVER_ERROR, s3Parameter);
		}

        // Check match
		if (!Strings.isNullOrEmpty(copySourceIfMatch)) {
			logger.debug(GWConstants.LOG_SOURCE_ETAG_MATCH, s3Metadata.getETag(), copySourceIfMatch.replace(GWConstants.DOUBLE_QUOTE, ""));
			if (!GWUtils.maybeQuoteETag(s3Metadata.getETag()).equals(copySourceIfMatch.replace(GWConstants.DOUBLE_QUOTE, ""))) {
				throw new GWException(GWErrorCode.PRECONDITION_FAILED, s3Parameter);
			}
		}

		if (!Strings.isNullOrEmpty(copySourceIfNoneMatch)) {
			logger.debug(GWConstants.LOG_SOURCE_ETAG_MATCH, s3Metadata.getETag(), copySourceIfNoneMatch.replace(GWConstants.DOUBLE_QUOTE, ""));
			if (GWUtils.maybeQuoteETag(s3Metadata.getETag()).equals(copySourceIfNoneMatch.replace(GWConstants.DOUBLE_QUOTE, ""))) {
				throw new GWException(GWErrorCode.DOES_NOT_MATCH, String.format(GWConstants.LOG_ETAG_IS_MISMATCH), s3Parameter);
			}
		}

		if (!Strings.isNullOrEmpty(copySourceIfModifiedSince)) {
			long copySourceIfModifiedSinceLong = Long.parseLong(copySourceIfModifiedSince);
			if (copySourceIfModifiedSinceLong != -1) {
				Date modifiedSince = new Date(copySourceIfModifiedSinceLong);
				if (s3Metadata.getLastModified().before(modifiedSince)) {
					throw new GWException(GWErrorCode.DOES_NOT_MATCH, String.format(GWConstants.LOG_MATCH_BEFORE, s3Metadata.getLastModified(), modifiedSince), s3Parameter);
				}
			}
		}
		
		if (!Strings.isNullOrEmpty(copySourceIfUnmodifiedSince)) {
			long copySourceIfUnmodifiedSinceLong = Long.parseLong(copySourceIfUnmodifiedSince);
			if (copySourceIfUnmodifiedSinceLong != -1) {
				Date unmodifiedSince = new Date(copySourceIfUnmodifiedSinceLong);
				if (s3Metadata.getLastModified().after(unmodifiedSince)) {
					throw new GWException(GWErrorCode.PRECONDITION_FAILED, String.format(GWConstants.LOG_MATCH_AFTER, s3Metadata.getLastModified(), unmodifiedSince), s3Parameter);
				}
			}
		}

		accessControlPolicy = new AccessControlPolicy();
		accessControlPolicy.aclList = new AccessControlList();
		accessControlPolicy.aclList.grants = new ArrayList<Grant>();
		accessControlPolicy.owner = new Owner();
		accessControlPolicy.owner.id = String.valueOf(s3Parameter.getUser().getUserId());
		accessControlPolicy.owner.displayName = s3Parameter.getUser().getUserName();

		String xml = GWUtils.makeAclXml(accessControlPolicy, 
										null, 
										dataCopyObject.hasAclKeyword(), 
										null, 
										dataCopyObject.getAcl(),
										getBucketInfo(),
										String.valueOf(s3Parameter.getUser().getUserId()),
										s3Parameter.getUser().getUserName(),
										dataCopyObject.getGrantRead(),
										dataCopyObject.getGrantWrite(), 
										dataCopyObject.getGrantFullControl(), 
										dataCopyObject.getGrantReadAcp(), 
										dataCopyObject.getGrantWriteAcp(),
										s3Parameter);
        
        boolean bReplaceMetadata = false;
		logger.debug(GWConstants.LOG_COPY_OBJECT_METADATA_DIRECTIVE, metadataDirective);
		if (!Strings.isNullOrEmpty(metadataDirective) && metadataDirective.equalsIgnoreCase(GWConstants.REPLACE)) {
            bReplaceMetadata = true;
		}
		
        s3Metadata.setOwnerId(String.valueOf(s3Parameter.getUser().getUserId()));
        s3Metadata.setOwnerName(s3Parameter.getUser().getUserName());
        if (userMetadata.size() > 0) {
            for (String key : userMetadata.keySet()) {
                logger.info(GWConstants.LOG_COPY_OBJECT_USER_METADATA, key, userMetadata.get(key));
            }
            if (bReplaceMetadata) {
				logger.info(GWConstants.LOG_COPY_OBJECT_REPLACE_USER_METADATA, userMetadata.toString());
                s3Metadata.setUserMetadataMap(userMetadata);
            }
        }

        if (!Strings.isNullOrEmpty(cacheControl) && bReplaceMetadata) {
				s3Metadata.setCacheControl(cacheControl);
		}
		if (!Strings.isNullOrEmpty(contentDisposition) && bReplaceMetadata) {
				s3Metadata.setContentDisposition(contentDisposition);
		}
		if (!Strings.isNullOrEmpty(contentEncoding) && bReplaceMetadata) {
				s3Metadata.setContentEncoding(contentEncoding);
		}
		if (!Strings.isNullOrEmpty(contentLanguage) && bReplaceMetadata) {
				s3Metadata.setContentLanguage(contentLanguage);
		}
		if (!Strings.isNullOrEmpty(contentType) && bReplaceMetadata) {
				s3Metadata.setContentType(contentType);
		}

        if (Strings.isNullOrEmpty(contentLengthString)) {
            logger.error(GWErrorCode.MISSING_CONTENT_LENGTH.getMessage());
			throw new GWException(GWErrorCode.MISSING_CONTENT_LENGTH, s3Parameter);
        } /*else {
            try {
                long contentLength = Long.parseLong(contentLengthString);
                s3Metadata.setContentLength(contentLength);
            } catch (NumberFormatException e) {
                logger.error(e.getMessage());
				throw new S3Exception(S3ErrorCode.INVALID_ARGUMENT, e);
            }
        }*/

		String jsonmeta = "";
        // update
        if (s3Parameter.getSrcBucketName().equals(bucket) && s3Parameter.getSrcPath().equals(object)) {
            if (!Strings.isNullOrEmpty(metadataDirective) && bReplaceMetadata) {
                // update metadata
				try {
					S3Metadata metaClass = objectMapper.readValue(srcMeta.getMeta(), S3Metadata.class);
					s3Metadata.setTier(GWConstants.AWS_TIER_STANTARD);
					s3Metadata.setContentLength(metaClass.getContentLength());
					s3Metadata.setETag(metaClass.getETag());
				} catch (JsonProcessingException e) {
					PrintStack.logging(logger, e);
					throw new GWException(GWErrorCode.SERVER_ERROR, s3Parameter);
				}
				s3Metadata.setLastModified(new Date());
				try {
					jsonmeta = objectMapper.writeValueAsString(s3Metadata);
				} catch (JsonProcessingException e) {
					PrintStack.logging(logger, e);
                    throw new GWException(GWErrorCode.SERVER_ERROR, s3Parameter);
				}
				logger.debug(GWConstants.LOG_COPY_OBJECT_META, jsonmeta);
				srcMeta.setMeta(jsonmeta);
				updateObjectMeta(srcMeta);

                s3Parameter.getResponse().setCharacterEncoding(GWConstants.CHARSET_UTF_8);
                XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
                try (Writer writer = s3Parameter.getResponse().getWriter()) {
                    s3Parameter.getResponse().setContentType(GWConstants.XML_CONTENT_TYPE);
                    XMLStreamWriter xmlout = xmlOutputFactory.createXMLStreamWriter(writer);
                    xmlout.writeStartDocument();
                    xmlout.writeStartElement(GWConstants.COPY_OBJECT_RESULT);
                    xmlout.writeDefaultNamespace(GWConstants.AWS_XMLNS);

                    writeSimpleElement(xmlout, GWConstants.LAST_MODIFIED, formatDate(s3Metadata.getLastModified()));
                    writeSimpleElement(xmlout, GWConstants.ETAG, GWUtils.maybeQuoteETag(s3Metadata.getETag()));

                    xmlout.writeEndElement();
                    xmlout.flush();
                } catch (XMLStreamException | IOException e) {
                    PrintStack.logging(logger, e);
                    throw new GWException(GWErrorCode.SERVER_ERROR, s3Parameter);
                }

                s3Parameter.getResponse().setStatus(HttpServletResponse.SC_OK);
                return;
            } else {
                logger.error(GWErrorCode.INVALID_REQUEST.getMessage());
				throw new GWException(GWErrorCode.INVALID_REQUEST, s3Parameter);
            }
        }

        Metadata objMeta = createCopy(srcBucket, srcObjectName, srcVersionId, bucket, object);
        versioningStatus = getBucketVersioning(bucket);

		String versionId = null;
		if (GWConstants.VERSIONING_ENABLED.equalsIgnoreCase(versioningStatus)) {
			versionId = String.valueOf(System.nanoTime());
		} else {
			versionId = GWConstants.VERSIONING_DISABLE_TAIL;
		}

		S3ObjectOperation objectOperation = new S3ObjectOperation(objMeta, s3Metadata, s3Parameter, versionId, null);
		S3Object s3Object = objectOperation.copyObject(srcMeta);

        s3Metadata.setETag(s3Object.getEtag());
		s3Metadata.setSize(s3Object.getFileSize());
		s3Metadata.setTier(GWConstants.AWS_TIER_STANTARD);
		s3Metadata.setLastModified(s3Object.getLastModified());
		s3Metadata.setDeleteMarker(s3Object.getDeleteMarker());
		s3Metadata.setVersionId(s3Object.getVersionId());

        try {
			jsonmeta = objectMapper.writeValueAsString(s3Metadata);
		} catch (JsonProcessingException e) {
			PrintStack.logging(logger, e);
			throw new GWException(GWErrorCode.SERVER_ERROR, s3Parameter);
		}

		logger.debug(GWConstants.LOG_COPY_OBJECT_META, jsonmeta);
        try {
			int result;
			if (objMeta.getReplicaDisk() != null) {
				result = insertObject(bucket, object, s3Object.getEtag(), jsonmeta, srcMeta.getTag(), s3Object.getFileSize(), xml, objMeta.getPrimaryDisk().getPath(), objMeta.getReplicaDisk().getPath(), versionId, GWConstants.OBJECT_TYPE_FILE);
			} else {
				result = insertObject(bucket, object, s3Object.getEtag(), jsonmeta, srcMeta.getTag(), s3Object.getFileSize(), xml, objMeta.getPrimaryDisk().getPath(), "", versionId, GWConstants.OBJECT_TYPE_FILE);
			}

			if (result != 0) {
				logger.error(GWConstants.LOG_COPY_OBJECT_FAILED, bucket, object);
			}
			logger.debug(GWConstants.LOG_COPY_OBJECT_INFO, bucket, object, s3Object.getFileSize(), s3Object.getEtag(), srcMeta.getAcl(), srcMeta.getAcl(), versionId);
		} catch (Exception e) {
			PrintStack.logging(logger, e);
			throw new GWException(GWErrorCode.SERVER_ERROR, s3Parameter);
		}

        s3Parameter.getResponse().setCharacterEncoding(GWConstants.CHARSET_UTF_8);
		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		try (Writer writer = s3Parameter.getResponse().getWriter()) {
			s3Parameter.getResponse().setContentType(GWConstants.XML_CONTENT_TYPE);
			XMLStreamWriter xmlout = xmlOutputFactory.createXMLStreamWriter(writer);
			xmlout.writeStartDocument();
			xmlout.writeStartElement(GWConstants.COPY_OBJECT_RESULT);
			xmlout.writeDefaultNamespace(GWConstants.AWS_XMLNS);

			writeSimpleElement(xmlout, GWConstants.LAST_MODIFIED, formatDate(s3Metadata.getLastModified()));
			writeSimpleElement(xmlout, GWConstants.ETAG, GWUtils.maybeQuoteETag(s3Object.getEtag()));

			xmlout.writeEndElement();
			xmlout.flush();
		} catch (XMLStreamException | IOException e) {
			PrintStack.logging(logger, e);
			throw new GWException(GWErrorCode.SERVER_ERROR, s3Parameter);
		}

		s3Parameter.getResponse().setStatus(HttpServletResponse.SC_OK);
	}
}
